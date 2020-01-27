package org.yoda.executor.step;

import org.yoda.codegen.CodeGenerator;
import org.yoda.executor.config.RunConfig;
import org.yoda.executor.smc.QueryExecution;
import org.yoda.type.SecureRelRecordType;

import java.io.Serializable;

public class SecureStep implements ExecutionStep, Serializable {

    transient CodeGenerator codeGenerator;
    RunConfig runConf;
    boolean visited = false;

    // hold on to state for serialization
    boolean isMerge = false;
    QueryExecution exec;

    public SecureStep(CodeGenerator cg, RunConfig r, ExecutionStep lhsChild, ExecutionStep rhsChild) throws Exception {
        codeGenerator = cg;
        runConf = r;

        exec = new QueryExecution(this);

    }


    @Override
    public String generate() throws Exception {
        return codeGenerator.generate();
    }

    @Override
    public QueryExecution getExec() {
        return exec;
    }

    @Override
    public String getPackageName() {
        return codeGenerator.getPackageName();
    }


    @Override
    public SecureRelRecordType getInSchema() {
        return codeGenerator.getInSchema();
    }

    @Override
    public SecureRelRecordType getSchema() {
        return codeGenerator.getSchema();
    }

    @Override
    public RunConfig getRunConfig() {
        return runConf;
    }

    @Override
    public CodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    @Override
    public boolean visited() {
        return visited;
    }

    @Override
    public void visit() {
        visited = true;
    }

    @Override
    public void setHostname(String host) {
        runConf.host = host;

    }

    @Override
    public SecureRelRecordType getSchema(boolean forSecureLeaf) {
        return getSchema();
    }


}
