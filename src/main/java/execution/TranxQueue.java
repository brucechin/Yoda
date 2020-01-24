package execution;

import utils.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class TranxQueue {
    int size_;
    Queue<Transaction> queue_;
    Lock lock_;

    public void tranxReorder(int len) {
        //reorder the order of top len transactions in the queue to minimize the potential conflicts
    }


    public boolean insertTranx(Transaction t) {
        //concurrently insert transactions to queue.
        //TODO use thread-safe queue to implement it lock-free
        lock_.lock();
        queue_.offer(t);
        lock_.unlock();
        return true;
    }

    public boolean isEmpty() {
        return queue_.isEmpty();
    }

    public List<Transaction> popTransactions(int num) {
        List<Transaction> list = new ArrayList<>();
        int count = 0;
        lock_.lock();
        while (!queue_.isEmpty() && count < num) {
            Transaction t = queue_.poll();
            list.add(t);
        }
        lock_.unlock();
        return list;
    }
}
