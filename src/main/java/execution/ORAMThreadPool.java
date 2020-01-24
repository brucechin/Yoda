package execution;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ORAMThreadPool<Job extends Runnable> {
    //use this pool to execute QueryTasks in the queue
    private int numORAM_;
    private ThreadPoolExecutor threadPool_;
    private boolean running_;
    private List<ORAM> oramExecutors_;

    public ORAMThreadPool() {

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
