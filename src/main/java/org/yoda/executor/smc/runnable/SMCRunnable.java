package org.yoda.executor.smc.runnable;

import org.yoda.db.data.Tuple;
import org.yoda.executor.smc.ExecutionSegment;
import org.yoda.executor.smc.OperatorExecution;
import org.yoda.executor.smc.SecureQueryTable;

public interface SMCRunnable {

    public void sendInt(int toSend);

    public void sendTuple(Tuple toSend);

    public int getInt();

    public Tuple getTuple();


    public ExecutionSegment getSegment();

    public SecureQueryTable getOutput();

    public OperatorExecution getRootOperator();

}
