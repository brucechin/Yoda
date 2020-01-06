package execution;

import utils.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class TranxQueue {
    int size;
    Queue<Transaction> queue;
    Lock lock;

    void tranxReorder(int len){
        //reorder the order of top len transactions in the queue to minimize the potential conflicts
    }

    void receiveTranx(){
        //should work in the background consistently
    }

    boolean insertTranx(Transaction t){
        //concurrently insert transactions to queue.
        //TODO use thread-safe queue to implement it lock-free
        lock.lock();
        queue.offer(t);
        lock.unlock();
        return true;
    }

    List<Transaction> submitToBatchManager(int num){
        List<Transaction> list = new ArrayList<>();
        int count = 0;
        lock.lock();
        while(!queue.isEmpty() && count < num){
            Transaction t = queue.poll();
            list.add(t);
        }
        lock.unlock();
        return list;
    }
}
