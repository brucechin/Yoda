package org.yoda.cloud;

import org.yoda.cloud.generator.TpccGen;
import org.yoda.db.transaction.Transaction;

public class TranxManager {
    //TODO use th data from logManager or epochManger for validation phase?


    BatchManager batchManager_;
    TpccGen generator_;

    public void recordTranxStats(Transaction t) {
        //TODO collect stats of tranx's read write operations
    }


    public void recovery() {
        //use logManager for failure recovery
    }

//    public void tranxArrival() {
//        //an individual thread to generating transactions and push_back to tranxQueue
//    }
}
