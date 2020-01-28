package org.yoda.cloud;

import org.yoda.cloud.storage.OPRAM;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class ORAMThreadPool<Job extends Runnable> {
    //use this pool to execute QueryTasks in the queue
    private ThreadPoolExecutor threadPool_;
    private boolean running_;
    private int degreeOfParallelism_;
    private HashMap<String, OPRAM> OPRAMExecutor_;

    public ORAMThreadPool() {
        //threadPool_ = new ThreadPoolExecutor();
    }

    public void read() {

    }

    public void update() {

    }

    public void insert() {

    }

    public void delete() {


    }

    public void execute(Job j) {

    }

    public void shutdown() {

    }

    public void start() {

    }
}
