
package org.yoda.codegen;

import com.oblivm.backend.flexsc.Mode;
import org.apache.commons.io.FileUtils;
import org.yoda.config.SystemConfiguration;
import org.yoda.executor.config.ConnectionManager;
import org.yoda.executor.config.RunConfig;
import org.yoda.executor.config.RunConfig.ExecutionMode;
import org.yoda.executor.smc.ExecutionSegment;
import org.yoda.executor.step.ExecutionStep;
import org.yoda.executor.step.SecureStep;
import org.yoda.type.SecureRelRecordType;
import org.yoda.util.ClassPathUpdater;
import org.yoda.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;


public class QueryCompiler {

	Map<ExecutionStep, String> sqlCode;
	Map<ExecutionStep, String> smcCode;

	//in the SMCQL design, it use query plan tree to iterate throughout the tree and store all the execution segments and store their corresponding sql or smc code for execution.
	//Map<Operator,ExecutionStep> allSteps;

	String queryId;

	List<String> smcFiles;
	List<String> sqlFiles;
	List<ExecutionSegment> executionSegments;

	String userQuery = null;

	ExecutionStep compiledRoot;

	Mode mode = Mode.REAL;
	String generatedClasspath = null;

	public QueryCompiler(SecureRelRoot q) throws Exception {

		smcFiles = new ArrayList<String>();
		sqlFiles = new ArrayList<String>();
		sqlCode = new HashMap<ExecutionStep, String>();
		smcCode = new HashMap<ExecutionStep, String>();
		executionSegments = new ArrayList<ExecutionSegment>();

		queryId = q.getName();
		Operator root = q.getPlanRoot();

		// set up space for .class files
		generatedClasspath = Utilities.getSMCQLRoot() + "/bin/org/smcql/generated/" + queryId;
		Utilities.mkdir(generatedClasspath);
		Utilities.cleanDir(generatedClasspath);

	}

	public QueryCompiler(SecureRelRoot q, String sql) throws Exception {


		smcFiles = new ArrayList<String>();
		sqlFiles = new ArrayList<String>();
		sqlCode = new HashMap<ExecutionStep, String>();
		smcCode = new HashMap<ExecutionStep, String>();
		executionSegments = new ArrayList<ExecutionSegment>();
		userQuery = sql;

		queryId = q.getName();
		Operator root = q.getPlanRoot();

		// set up space for .class files
		generatedClasspath = Utilities.getSMCQLRoot() + "/bin/org/smcql/generated/" + queryId;
		Utilities.mkdir(generatedClasspath);
		Utilities.cleanDir(generatedClasspath);

	}

	public QueryCompiler(SecureRelRoot q, Mode m) throws Exception {


		mode = m;
		smcFiles = new ArrayList<String>();
		sqlFiles = new ArrayList<String>();
		sqlCode = new HashMap<ExecutionStep, String>();
		smcCode = new HashMap<ExecutionStep, String>();
		executionSegments = new ArrayList<ExecutionSegment>();

		queryId = q.getName();

	}


	public List<ExecutionSegment> getSegments() {
		return executionSegments;
	}

	public void writeToDisk() throws Exception {

		String targetPath = Utilities.getCodeGenTarget() + "/" + queryId;
		Utilities.cleanDir(targetPath);


		Utilities.mkdir(targetPath + "/sql");

		Utilities.mkdir(targetPath + "/smc");


		for (Entry<ExecutionStep, String> e : sqlCode.entrySet()) {
			CodeGenerator cg = e.getKey().getCodeGenerator();
			String targetFile = cg.destFilename(ExecutionMode.Plain);
			sqlFiles.add(targetFile);
			Utilities.writeFile(targetFile, e.getValue());
		}

		for (Entry<ExecutionStep, String> e : smcCode.entrySet()) {
			CodeGenerator cg = e.getKey().getCodeGenerator();
			String targetFile = cg.destFilename(ExecutionMode.Secure);
			smcFiles.add(targetFile);
			if (e.getValue() != null) // no ctes
				Utilities.writeFile(targetFile, e.getValue());
		}

	}

	public List<String> getClasses() throws IOException, InterruptedException {

		File path = new File(Utilities.getCodeGenTarget() + "/org/smcql/generated/" + queryId);
		String[] extensions = new String[1];
		extensions[0] = "class";
		Collection<File> files = FileUtils.listFiles(path, extensions, true);
		List<String> filenames = new ArrayList<String>();

		for (File f : files) {
			filenames.add(f.toString());
		}
		return filenames;
	}

	public void loadClasses() throws IOException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		List<String> classFiles = getClasses();
		for (String classFile : classFiles) {
			ClassPathUpdater.add(classFile);
		}
	}


	private ExecutionStep generateSecureStep(Operator op, List<ExecutionStep> children, List<Operator> opsToCombine, List<ExecutionStep> merges) throws Exception {
		SecureOperator secOp = SecureOperatorFactory.get(op);
		if (!merges.isEmpty())
			secOp.setMerges(merges);

		for (Operator cur : opsToCombine) {
			if (cur instanceof Filter) {
				secOp.addFilter((Filter) cur);
			} else if (cur instanceof Project) {
				secOp.addProject((Project) cur);
			}
		}
		secOp.compileIt();

		RunConfig sRunConf = new RunConfig();

		sRunConf.port = (SystemConfiguration.getInstance()).readAndIncrementPortCounter();
		sRunConf.smcMode = mode;
		sRunConf.host = getAliceHostname();

		SecureStep smcStep = null;

		allSteps.put(op, smcStep);
		String code = secOp.generate();
		smcCode.put(smcStep, code);
		return smcStep;
	}

	public Map<ExecutionStep, String> getSMCCode() {
		return smcCode;
	}

	public Map<ExecutionStep, String> getSQLCode() {
		return sqlCode;
	}

	private String getAliceHostname() throws Exception {
		ConnectionManager cm = ConnectionManager.getInstance();
		String alice = cm.getAlice();
		return cm.getWorker(alice).hostname;
	}


	private ExecutionSegment createSegment(ExecutionStep secStep) throws Exception {
		ExecutionSegment current = new ExecutionSegment();
		current.rootNode = secStep.getExec();

		current.runConf = secStep.getRunConfig();
		current.outSchema = new SecureRelRecordType(secStep.getSchema());
		current.executionMode = secStep.getSourceOperator().getExecutionMode();

		if (secStep.getSourceOperator().getExecutionMode() == ExecutionMode.Slice && userQuery != null) {
			current.sliceSpec = secStep.getSourceOperator().getSliceKey();

			PlainOperator sqlGenRoot = secStep.getSourceOperator().getPlainOperator();
			sqlGenRoot.inferSlicePredicates(current.sliceSpec);
			current.sliceValues = sqlGenRoot.getSliceValues();
			current.complementValues = sqlGenRoot.getComplementValues();
			current.sliceComplementSQL = sqlGenRoot.generatePlaintextForSliceComplement(userQuery); //plaintext query for single site values
		}

		return current;

	}

	Byte[] toByteObject(byte[] primitive) {
		Byte[] bytes = new Byte[primitive.length];
		int i = 0;
		for (byte b : primitive)
			bytes[i++] = Byte.valueOf(b);
		return bytes;
	}

}
