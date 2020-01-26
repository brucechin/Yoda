package org.yoda.cloud.generator;

import org.yoda.util.Transaction;

public class TpccGen {
    //TODO use this class for random transaction generation and insert into transaction queue.
    private int contentionLevel_;
    private int transactionArrivalRate_;

    TpccGen() {

    }

    public Transaction generateOneTranx() {
        Transaction t = new Transaction();
        return t;
    }
}
