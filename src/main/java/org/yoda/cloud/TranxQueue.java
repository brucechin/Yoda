package org.yoda.cloud;

import lombok.NonNull;
import org.yoda.db.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TranxQueue {
    LinkedBlockingQueue<Transaction> queue_;

    public TranxQueue() {
        queue_ = new LinkedBlockingQueue<>();
    }

    public void tranxReorder(int len) {
        //reorder the order of top len transactions in the queue to minimize the potential conflicts
    }


    /**
     * concurrently insert transactions to queue.
     * @param t The transaction to be added
     * @return the status of insert
     */
    public boolean insertTranx(@NonNull Transaction t) {
        return queue_.offer(t);
    }

    /**
     * Is the queue empty
     * @return the status of queue
     */
    public boolean isEmpty() {
        return queue_.isEmpty();
    }

    /**
     * Pop the given number of transactions
     * @param num the number of transactions to be popped
     * @return a {@link List} of transactions
     */
    public List<Transaction> popTransactions(int num) {
        assert num > 0;
        List<Transaction> list = new ArrayList<>();
        queue_.drainTo(list, num);
        return list;
    }
}
