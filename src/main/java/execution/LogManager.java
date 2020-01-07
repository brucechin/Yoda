package execution;

public class LogManager {

    boolean is_running_;

    public LogManager(){

    }

    public static LogManager getInstance(){
        LogManager logManager  = new LogManager();
        return logManager;
    }

    public boolean getStatus(){
        return is_running_;
    }

    public void insertLog(){
        //TODO append-only logging?
        //TODO parallelize inserting using a log buffer?
    }

    public void deleteLog(){

    }

    public void startCheckpointing(){

    }

    public void stopCheckpointing(){

    }

    public void recovery(){

    }

}
