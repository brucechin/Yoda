package org.yoda.executor.smc.runnable;

import com.oblivm.backend.flexsc.Party;
import org.yoda.executor.smc.ExecutionSegment;
import org.yoda.executor.smc.SecureBufferPool;
import org.yoda.executor.smc.SecureQueryTable;

import java.io.Serializable;
import java.util.concurrent.Callable;

// creates runner thread for single SecureOperator
// this one implements single input, single output (no joins)
public class RunnableSegment<T> implements Callable<SecureQueryTable>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5996234969658841425L;
    protected SecureBufferPool bufferPool = null;
    // only one of these two may be non-null
    protected SMCQLRunnable.Generator<T> genRunner = null;
    protected SMCQLRunnable.Evaluator<T> evaRunner = null;
    Thread runnerThread;
    ExecutionSegment segment;


    public RunnableSegment(ExecutionSegment exec) throws Exception {
        segment = exec;

    }


    @SuppressWarnings("unchecked")
    public Thread runIt() throws Exception {
        if (segment.party == Party.Alice) {
            genRunner = new SMCQLRunnable.Generator<T>(segment);
            runnerThread = new Thread(genRunner);
        } else {
            evaRunner = new SMCQLRunnable.Evaluator<T>(segment);
            runnerThread = new Thread(evaRunner);
        }

        runnerThread.start();
        return runnerThread;
    }

    public SMCQLRunnable.Generator<T> getGenRunner() {
        return genRunner;
    }

    public SMCQLRunnable.Evaluator<T> getEvaRunner() {
        return evaRunner;
    }


    @Override
    public SecureQueryTable call() throws Exception {
        try {

            Thread run = runIt();
            run.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return SecureBufferPool.getInstance().readRecord(segment.rootNode);
    }


    public SecureQueryTable getOutput() {
        if (genRunner != null)
            return genRunner.getOutput();
        if (evaRunner != null)
            return evaRunner.getOutput();

        return null;
    }


}
