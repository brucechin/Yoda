package org.yoda.cloud;

import org.yoda.db.transaction.Transaction;

import java.util.List;

public class BatchManager {
    TranxQueue queue_;
    private List<Transaction> runningTranx;
    EpochManager epochManager_;
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

    public boolean validateTranx(Transaction t) {
        //TODO use write read set for validation
        return true;
    }

    public void abortTranx(Transaction t) {
        //TODO undo the operations
    }

    public void submitBatchOp(List<String> batch) {
        epochManager_.epochQueue.add(batch);
    }

    public void pullFromTranxQueue(int num) {
        //TODO pull num transactions from tranxqueue
    }

}
