package execution;

import java.io.IOException;
import java.util.Queue;

public class EpochManager {
    int curEpochId_;
    private boolean isRunning_;
    ORAMThreadPool threadPool_; // use this thread pool to execute query tasks.
    Queue<String> epochQueue; //epoches of operations waiting for execution

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
