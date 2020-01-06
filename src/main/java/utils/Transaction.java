package utils;

import types.TransactionId;

import java.io.IOException;
import java.util.List;

enum ResultType {
    INVALID,  // invalid result type
    SUCCESS,
    FAILURE,
    ABORTED,  // aborted
    NOOP,     // no op
    UNKNOWN,
    QUEUING,
    TO_ABORT
};

public class Transaction {
    private final TransactionId txnId_;
    boolean is_written_; // if tranx has write op
    boolean read_only_ = false;
    int threadId_; // which thread is executing this tranx
    long epochId_;
    long timestamp_; // when the transaction began
    ReadWriteSet rwSet_;
    ResultType result_ = ResultType.SUCCESS;
    List<String> queryStrings_; //list of queries. Need to compiled to EMP code before submitting to server for execution


    public Transaction(){
        txnId_ = new TransactionId();
    }

    public TransactionId getTxnId_(){
        return txnId_;
    }

    public boolean commit() throws IOException{
        //TODO persist updates here
        return true;
    }

    public boolean abort() throws IOException{
        return true;
    }



}
