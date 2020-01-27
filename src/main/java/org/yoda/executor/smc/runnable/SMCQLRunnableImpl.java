package org.yoda.executor.smc.runnable;

import com.oblivm.backend.flexsc.CompEnv;
import com.oblivm.backend.gc.GCSignal;
import com.oblivm.backend.lang.inter.ISecureRunnable;
import com.oblivm.backend.lang.inter.Util;
import com.oblivm.backend.oram.SecureArray;
import org.yoda.codegen.smc.DynamicCompiler;
import org.yoda.config.SystemConfiguration;
import org.yoda.db.data.QueryTable;
import org.yoda.executor.smc.*;
import org.yoda.executor.smc.io.ArrayManager;
import org.yoda.util.Utilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// place for methods entirely duplicated by gen and eva
public class SMCQLRunnableImpl<T> implements Serializable {
    //all sliced secure execution related API and variables are removed
    ExecutionSegment runSpec;
    ArrayManager<T> dataManager;
    Map<String, Double> perfReport;

    // single slice key / segment
    boolean semijoinExecution = true;
    SMCRunnable parent;
    SecureQueryTable lastOutput = null;
    Logger logger;


    public SMCQLRunnableImpl(ExecutionSegment segment, SMCRunnable runnable) throws Exception {
        runSpec = segment;
        dataManager = new ArrayManager<T>();
        perfReport = new HashMap<String, Double>();

        logger = SystemConfiguration.getInstance().getLogger();

        try {
            semijoinExecution = SystemConfiguration.getInstance().getProperty("semijoin-execution").equals("true");
        } catch (Exception e) {
            e.printStackTrace();
        }



        parent = runnable;
    }

    public static String getKey(QueryExecution opEx, boolean isLhs) {
        String ret = opEx.packageName;
        ret += isLhs ? "-lhs" : "-rhs";
        return ret;
    }


    @SuppressWarnings("unchecked")
    public void secureCompute(CompEnv<T> env) throws Exception {
        //sliced secure execution has been removed because we want to execute all on private fields. There is no public or protected fields now.
        SecureArray<T> secResult = runOne(runSpec.rootNode, env);
        GCSignal[] payload = (GCSignal[]) Util.secToIntArray(env, secResult);
        GCSignal[] nonNulls = (GCSignal[]) secResult.getNonNullEntries();

        BasicSecureQueryTable output = new BasicSecureQueryTable(payload, nonNulls, runSpec.rootNode.outSchema, (CompEnv<GCSignal>) env, parent);
        lastOutput = output;

    }


    // returns output of run
    @SuppressWarnings("unchecked")
    SecureArray<T> runOne(QueryExecution op, CompEnv<T> env) throws Exception {

        if (op == null || (op.parentSegment != runSpec)) // != runspec implies that child was computed in another segment
            return null;

        // already exec'd cte
        if (op.output != null) {
            return (SecureArray<T>) op.output;
        }

        //TODO we should delete below lines which execute op's children
        SecureArray<T> lhs = runOne(op.lhsChild, env);
        if (lhs == null) { // get input from outside execution segment
            long start = System.nanoTime();
            lhs = dataManager.getInput(op, true, env, parent);
            long end = System.nanoTime();
            logger.info("Loaded lhs data in " + (end - start) / 1e9 + " seconds.");
        }
        SecureArray<T> rhs = runOne(op.rhsChild, env);

        if (rhs == null) {
            long start = System.nanoTime();
            rhs = dataManager.getInput(op, false, env, parent);
            long end = System.nanoTime();
            logger.info("Loaded rhs data in " + (end - start) / 1e9 + " seconds.");

        }

        double start = System.nanoTime();
        ISecureRunnable<T> runnable = DynamicCompiler.loadClass(op.packageName, op.byteCode, env);
        int rhsLength = (rhs != null) ? rhs.length : 0;
        int lhsLength = (lhs != null) ? lhs.length : 0;
        String msg = "Operator " + op.packageName + " started at " + Utilities.getTime() + " on " + lhsLength + "," + rhsLength + " tuples.";
        logger.info(msg);

        SecureArray<T> secResult = null;

        secResult = runnable.run(lhs, rhs);
        if (secResult == null)
            throw new Exception("Null result for " + op.packageName);

        if (secResult.getNonNullEntries() == null) {
            T[] prevEntries = lhs.getNonNullEntries();
            secResult.setNonNullEntries(prevEntries);
        }

        double end = System.nanoTime();
        double elapsed = (end - start) / 1e9;

        msg = "Operator ended at " + Utilities.getTime() + " it ran in " + op.packageName + " ran in " + elapsed + " seconds, output=" + secResult;
        logger.info(msg);

        // sum for slices
        if (perfReport.containsKey(op.packageName)) {
            double oldSum = perfReport.get(op.packageName);

            perfReport.put(op.packageName, oldSum + elapsed);
        } else
            perfReport.put(op.packageName, elapsed);

        dataManager.registerArray(op, secResult, env, parent);
        op.output = (SecureArray<GCSignal>) secResult;
        return secResult;

    }


    public void prepareOutput(CompEnv<T> env) throws Exception {
        if (runSpec.sliceComplementSQL != null && !runSpec.sliceComplementSQL.isEmpty() && semijoinExecution) {
            QueryTable plainOut = SqlQueryExecutor.query(runSpec.sliceComplementSQL, runSpec.outSchema, runSpec.workerId);
            lastOutput.setPlaintextOutput(plainOut);
            SecureBufferPool.getInstance().addArray(runSpec.rootNode, lastOutput);
        }


        logger.info("Finished prepare output for  " + runSpec.rootNode.packageName + " at " + Utilities.getTime());
        logger.info("Perf times " + perfReport);

    }

    public SecureQueryTable getOutput() {
        return lastOutput;
    }
}
