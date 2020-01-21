package execution.generator;

import utils.Transaction;

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
