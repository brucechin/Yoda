package org.yoda.cloud;

import org.yoda.cloud.storage.ORAM;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ORAMThreadPool<Job extends Runnable> {
    //use this pool to execute QueryTasks in the queue
    private int numORAM_;
    private ThreadPoolExecutor threadPool_;
    private boolean running_;
    private List<ORAM> oramExecutors_;

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
