package org.yoda.client;

import org.yoda.utils.Transaction;

import java.util.Queue;

public class Client {

    Queue<Transaction> tranxQueue_;//compiled transaction queue waiting for submission
    private SqlCompiler compiler_;
    private int clientId_;
    private String IP_;
    private int port_;

    public Client() {
        //constructor
    }

    boolean connect() {
        //connect to cloud server
        return true;
    }

    boolean submitTranx(Transaction t) {
        //TODO synchronous or asynchronous?
        return true;
    }

    Transaction compileTranx(Transaction t) {
        //compile all queries in tranx to EMP code
        return t;
    }

    void generateTranx() {
        Transaction t = new Transaction();
        Transaction compiled = compileTranx(t);
        tranxQueue_.add(compiled);
    }

}
