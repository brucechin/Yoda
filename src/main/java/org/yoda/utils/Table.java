package org.yoda.utils;

import java.util.List;

public class Table implements Runnable {
    String tableName_;
    private int size_;//size of tuple
    private int count_;//number of tuples
    private int tableId;
    private List<Tuple> tuples_;
    //TODO add tuple schema


    public int getTableSize() {
        return size_;
    }

    public void addTuples(Table src) {
        if (src == null) return;

        for (Tuple t : src.tuples_) {
            tuples_.add(t);
        }
    }

    public void run() {

    }

    public List<Tuple> tuples() {
        return tuples_;
    }

    public int tupleSize() {
        return size_;
    }

    public int tupleCount() {
        return count_;
    }

    public Tuple getTuple(int offset) {
        return tuples_.get(offset);
    }
}
