package utils;

import types.TransactionId;

import java.util.List;

public class Epoch {
    int epochId_;
    int tranxCount_;//number of transactions in this epoch
    List<TransactionId> tranxIds_;

    public Epoch() {

    }

    public int comparedTo(Epoch e) {
        return Integer.compare(this.epochId_, e.epochId_);
    }
}
