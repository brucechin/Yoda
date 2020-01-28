package org.yoda.cloud;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

public class EpochManager {
    int curEpochId_;
    public ORAMThreadPool threadPool_; // use this thread pool to execute query tasks.
    public Queue<List<String>> epochQueue; //epoches of operations waiting for org.yoda.execution
    private boolean isRunning_;
    private LogManager logManager_;

    EpochManager() {
        threadPool_ = new ORAMThreadPool();
    }

    //TODO we can have a read write logging for each epoch here for validation?
    public int getCurEpochId() {
        return curEpochId_;
    }

    public void incrementEpochId() throws IOException {
        //TODO use atomic incremental op
        if (isRunning_) {
            curEpochId_++;
        } else {
            try {
                epochEnd();
            } catch (IOException e) {
                e.printStackTrace();
            }
            curEpochId_++;
        }

    }

    public void epochStart() throws IOException {
        isRunning_ = true;
        //TODO then start execute batched operations within this epoch
    }

    public void epochEnd() throws IOException {
        //TODO check if all operations within this epoch have finished
        isRunning_ = false;
    }

}
