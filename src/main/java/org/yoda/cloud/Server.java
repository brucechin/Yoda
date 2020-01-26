package org.yoda.cloud;

import org.yoda.client.Client;

import java.util.List;

public class Server {
    //TODO : for eaiser implementation, we can generate random transactions and control the generating speed all at the server side. Therefore, we don't need to implement clients and take care of the org.yoda.client-server connection.
    List<Client> clients_;
    TranxManager tranxManager_;

    public Server() {

    }

    public void start() {

    }

    public void stop() {

    }

    public void statistics() {
        //TODO collect some stats of executions
    }
}
