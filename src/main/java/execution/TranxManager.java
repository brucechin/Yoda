package execution;

import execution.generator.TpccGen;
import utils.Transaction;

public class TranxManager {
    //TODO use th data from logManager or epochManger for validation phase?
    LogManager logManager_;
    EpochManager epochManager_;
    BatchManager batchManager_;
    TpccGen generator_;
    public void recordTranxStats(Transaction t){
        //TODO collect stats of tranx's read write operations
    }


    public boolean validateTranx(Transaction t){
        //TODO use write read set for validation
        return true;
    }


    public void recovery() {
        //use logManager for failure recovery
    }

    public void tranxArrival() {
        //an individual thread to generating transactions and push_back to tranxQueue
    }
}
