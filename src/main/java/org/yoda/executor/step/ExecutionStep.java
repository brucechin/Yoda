package org.yoda.executor.step;

import org.yoda.codegen.CodeGenerator;
import org.yoda.executor.config.RunConfig;
import org.yoda.executor.smc.QueryExecution;
import org.yoda.type.SecureRelRecordType;

public interface ExecutionStep {


	// generates a main method for smc
	public String generate() throws Exception;

	public String getPackageName();

	public SecureRelRecordType getInSchema();

	public SecureRelRecordType getSchema();

	public SecureRelRecordType getSchema(boolean forSecureLeaf);

	public RunConfig getRunConfig();

	public CodeGenerator getCodeGenerator();

	public boolean visited(); // check if visited

	public void visit(); // mark step as executed

	public void setHostname(String host); // where it will be run, Alice/generator if SecureStep

	public QueryExecution getExec();
}
