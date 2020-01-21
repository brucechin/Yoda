package execution;

import utils.Transaction;

import java.util.List;
import java.util.Queue;

public class BatchManager {
    private List<Transaction> runningTranx;
    private Queue<String> operationQueue; //I/O operations waiting for submitting to ORAM servers for execution
    private int batchSize;
    ORAMThreadPool threadPool_; // use this thread pool to execute query tasks.

    BatchManager(){

    }

    public void removeTranx(Transaction t) {
        //if t is complete, remove from runningTranx
    }

    public void addTranx(Transaction t) {
        //add t to runningTranx, insert its I/O operations to operationQueue too.
    }

    public void executeOneEpoch() {
        //use up all available ORAMs for execution

        //if any running transaction finishes its reading phase, do its compute phase here

        //for transaction committing phase
    }


}
