package org.yoda.cloud;

import org.yoda.db.transaction.Transaction;

import java.util.List;
import java.util.Queue;

public class BatchManager {
    TranxQueue queue_;
    private List<Transaction> runningTranx;
    EpochManager epochManager_;
    private Queue<String> operationQueue; //I/O operations waiting for submitting to ORAM servers for org.yoda.execution
    private int batchSize;


    BatchManager() {

    }

    public void removeTranx(Transaction t) {
        //if t is complete, remove from runningTranx
    }

    public void addTranx(Transaction t) {
        //add t to runningTranx, insert its I/O operations to operationQueue too.
    }

    public void commitTranx(Transaction t) {

    }

    public void abortTranx(Transaction t) {

    }

    public void submitBatchOp(List<String> batch) {
        epochManager_.epochQueue.add(batch);
    }


}
