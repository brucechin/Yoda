package execution;

import client.Client;
import storage.SecretShareStorage;

import java.util.HashMap;
import java.util.List;

public class Server {
    List<Client> clients_;
//    SecretShareStorage storageA_;
//    SecretShareStorage storageB_;
    TranxManager tranxManager_;
    HashMap<String, OPRAM> opramMap_;

    public Server(){

    }

    public void start(){

    }

    public void stop(){

    }

    public void statistics(){
        //TODO collect some stats of executions
    }
}
