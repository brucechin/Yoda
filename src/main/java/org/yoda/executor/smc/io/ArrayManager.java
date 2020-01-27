package org.yoda.executor.smc.io;

import com.oblivm.backend.flexsc.CompEnv;
import com.oblivm.backend.flexsc.Party;
import com.oblivm.backend.gc.GCSignal;
import com.oblivm.backend.lang.inter.Util;
import com.oblivm.backend.oram.SecureArray;
import org.yoda.config.SystemConfiguration;
import org.yoda.db.data.QueryTable;
import org.yoda.db.data.Tuple;
import org.yoda.executor.config.ConnectionManager;
import org.yoda.executor.plaintext.SqlQueryExecutor;
import org.yoda.executor.smc.ExecutionSegment;
import org.yoda.executor.smc.QueryExecution;
import org.yoda.executor.smc.SecureBufferPool;
import org.yoda.executor.smc.SecureQueryTable;
import org.yoda.executor.smc.runnable.SMCRunnable;
import org.yoda.util.SMCUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

// for use in SMCQLRunnable$Generator, SMCQLRunnable$Evaluator
public class ArrayManager<T> implements Serializable {
    Map<QueryExecution, SecureArray<T>> inputArrays;
    SecureBufferPool bufferPool;


    Tuple executingKey;
    Logger logger;

    public ArrayManager() throws Exception {
        inputArrays = new LinkedHashMap<QueryExecution, SecureArray<T>>();
        bufferPool = SecureBufferPool.getInstance();
        logger = SystemConfiguration.getInstance().getLogger();
    }


    @SuppressWarnings("unchecked")
    public SecureArray<T> getInput(QueryExecution op, boolean isLhs, CompEnv<T> env, SMCRunnable parent) throws Exception {

        if (op == null)
            return null;

        QueryExecution src = op;
        Party party = op.getParty();
        String workerId = op.getWorkerId();

        if (op.parentSegment != parent.getSegment()) {
            //throw new Exception("Mismatched operator " + op);
        }


        // local intermediate result
        if (inputArrays.containsKey(src)) {
            return inputArrays.get(src);
        }


        if (src != null) {

            // intermediate result from another execution step
            SecureQueryTable table = bufferPool.readRecord(src.packageName, workerId, party);
            if (table != null) {
                return (SecureArray<T>) table.getSecureArray((CompEnv<GCSignal>) env, parent);
            }
        }

        if (op.getSourceSQL() != null) {
            if ((isLhs && party == Party.Alice) || (!isLhs && party == Party.Bob)) {
                return prepareLocalPlainData(op, env, parent);
            } else {
                // retrieve local half of shared secret for Bob's input

                return SMCUtils.prepareRemotePlainArray(env, parent);

            }

        }


        return null;

    }


    public void registerArray(QueryExecution srcOp, SecureArray<T> arr, CompEnv<T> env, SMCRunnable parent) throws Exception {
        inputArrays.put(srcOp, arr);

        T[] dstArray = (arr == null) ? null : Util.secToIntArray(env, arr);
        T[] length = (arr == null) ? null : arr.getNonNullEntries();

        bufferPool.addArray(srcOp, (GCSignal[]) dstArray, (GCSignal[]) length, (CompEnv<GCSignal>) env, parent);
    }

    public void registerSlicedArray(QueryExecution srcOp, SecureArray<T> arr, CompEnv<T> env, SMCRunnable parent, Tuple t) throws Exception {
        inputArrays.put(srcOp, arr);

        T[] dstArray = (arr == null) ? null : Util.secToIntArray(env, arr);
        T[] length = (arr == null) ? null : arr.getNonNullEntries();

        bufferPool.addArray(srcOp, (GCSignal[]) dstArray, (GCSignal[]) length, (CompEnv<GCSignal>) env, parent, t);
    }

    public boolean hasArray(QueryExecution op, SMCRunnable parent) {
        ExecutionSegment segment = parent.getSegment(); // reference parent segment because sometimes we draw from other segments
        String workerId = segment.workerId;
        Party p = segment.party;

        if (inputArrays.containsKey(op)) {
            return true;
        }
        if (bufferPool.readRecord(op.packageName, workerId, p) != null) {
            return true;
        }

        return false;
    }


    public SecureArray<T> prepareLocalPlainData(QueryExecution o, CompEnv<T> env, SMCRunnable parent) throws Exception {
        QueryTable table = queryIt(o);
        return SMCUtils.prepareLocalPlainArray(table, env, parent);
    }

    private QueryTable queryIt(QueryExecution op) throws Exception {
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection c = cm.getConnection(op.getWorkerId());
        double start = System.nanoTime();
        logger.info("For operator " + op.packageName + ", running plaintext query: " + op.getSourceSQL());

        QueryTable tupleData = SqlQueryExecutor.query(op.outSchema, op.getSourceSQL(), c);

        String limitStr = SystemConfiguration.getInstance().getProperty("truncate-input");
        if (limitStr != null) {
            int limit = Integer.parseInt(limitStr);
            logger.info("Truncating to " + limit + " tuples.");
            //  for testing
            tupleData.truncateData(limit);
        }
        double end = System.nanoTime();
        double elapsed = (end - start) / 1e9;
        logger.info("Finished running plaintext for operator " + op.packageName + " in " + elapsed + " seconds.");
        return tupleData;

    }


    public SecureArray<T> sendAlice(boolean[] srcData, int tupleSize, CompEnv<T> env) throws Exception {

        int len = srcData.length;

        T[] inputA = env.inputOfAlice(srcData);
        env.flush();

        SecureArray<T> input = Util.intToSecArray(env, inputA, tupleSize, len / tupleSize);

        env.flush();
        return input;

    }

}
