package org.yoda.executor.smc;

import com.oblivm.backend.flexsc.Party;
import org.yoda.db.data.Tuple;
import org.yoda.executor.config.RunConfig;

import java.io.Serializable;
import java.util.List;

// sequence of generated operators for execution (no sync points with other ops / hosts)
public class ExecutionSegment implements Serializable {
    //TODO there is no query plan tree anymore. no recursive execution.
    public QueryExecution rootNode;

    // replacing SMCConfig
    public RunConfig runConf;
    public String workerId;
    public Party party;

    //public ExecutionMode executionMode;
    //public boolean isPlanRoot = false;

    //public List<Tuple> sliceValues;
    public List<Tuple> complementValues;


    public void checkInit() throws Exception {
        if (party == null || workerId == null) {
            throw new Exception("Parent segment uninitialized!");
        }
        checkInitHelper(rootNode);
    }

    public void checkInitHelper(QueryExecution op) throws Exception {
        if (op == null || op.parentSegment != this) {
            return;
        }

        if (op.parentSegment == null) {
            throw new Exception("Segment uninitialized for " + op);
        }


        if (op.getWorkerId() == null || op.getParty() == null) {
			throw new Exception("Bad configuration for " + op);
        }


    }

    // for use after a single slice execution to prepare for next one
    public void resetOutput() {
        resetOutputHelper(rootNode);
    }

    private void resetOutputHelper(QueryExecution op) {
        if (op == null || op.parentSegment != this)
            return;

        op.output = null;


    }

}
