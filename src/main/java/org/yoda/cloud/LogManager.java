package org.yoda.cloud;

public class LogManager {
    public static LogManager instance_;
    boolean is_running_;

    private LogManager() {
        is_running_ = false;
    }

    public static LogManager getInstance() {
        if (instance_ == null) {
            synchronized(LogManager.class) {
                if (instance_ == null) {
                    instance_ = new LogManager();
                }
            }
        }
        return instance_;
    }

    public boolean getStatus() {
        return is_running_;
    }

    public void insertLog() {
        //TODO append-only logging?
        //TODO parallelize inserting using a log buffer?
    }


    public void startCheckpointing() {

    }


    public void recovery() {

    }

}
