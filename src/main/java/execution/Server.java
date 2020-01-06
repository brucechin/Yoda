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
    BatchManager batchManager_;
    HashMap<String, OPRAM> opramMap_;
    TranxQueue tranxQueue_;


}
