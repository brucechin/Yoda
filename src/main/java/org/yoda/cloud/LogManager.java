package org.yoda.cloud;

public class LogManager {
    public static LogManager instance;
    boolean is_running_;

    public LogManager() {

    }

    public static LogManager getInstance() {
        instance = new LogManager();
        return instance;
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
