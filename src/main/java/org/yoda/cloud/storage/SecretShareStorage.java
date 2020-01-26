package org.yoda.cloud.storage;

import java.util.HashMap;
import java.util.List;

public class SecretShareStorage {
    private EmpExecutor executor_;//execute EMP code on secret shares
    private List<String> dataOwners_;
    private Integer party_;
    private HashMap<String, Table> data_;//tablename -> table. table contains EMP secret shares for MPC org.yoda.execution
    private String ipAdrr_;

    //TODO add JDBC connection variables for extracting secret shares from data owners.

    SecretShareStorage() {

    }

    public void addDataOwner(String dataOwner) {

    }

    //TODO how to execute EMP code in parallel?
}
