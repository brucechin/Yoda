package org.yoda.executor.smc;

import com.oblivm.backend.flexsc.Party;
import com.oblivm.backend.gc.GCSignal;
import com.oblivm.backend.oram.SecureArray;
import org.yoda.codegen.CodeGenerator;
import org.yoda.codegen.smc.query.SecureQuery;
import org.yoda.executor.config.RunConfig;
import org.yoda.executor.step.SecureStep;
import org.yoda.type.SecureRelRecordType;
import org.yoda.util.Utilities;

//modified from SMCQL org.yoda.executor.smc.OperationExecution. because we don't have multiple segments in a query plan for recursive execution, we abstract the query execution content to this class
//there is no plaintext or sliced secure execution
public class QueryExecution {
    private static final long serialVersionUID = 6565213527478140219L;
    public String packageName;
    //TODO need to read how QueryCompiler get the schema
    public SecureRelRecordType inSchema;
    public SecureRelRecordType outSchema;
    public byte[] byteCode; // compiled .class for this step
    public ExecutionSegment parentSegment = null; // pointer to segment for SMCConfig
    public transient SecureArray<GCSignal> output; // optional - for passing around data w/in segment
    private SecureQuery query; // point to specific query class like TpccGetWarehouseTax and call its generate() API to generate lcc code
    RunConfig runConf;
    // for merge case


    public QueryExecution() {

    }

    public QueryExecution(SecureQuery q) {
        packageName = q.getPackageName();
        outSchema = q.getSchema(); //change this

        try {
            byteCode = Utilities.readGeneratedClassFile(packageName);//TODO how to read query's byte code??
        } catch (Exception e) {
            // do nothing
        }

    }

    public String generate() throws Exception {
        return query.generate();
    }

    public String getQueryName() {
        return query.getQueryName();
    }

    public RunConfig getRunConfig() {
        return runConf;
    }

//    public SecureRelRecordType getInSchema() {
//        return codeGenerator.getInSchema();
//    }
//
//    public SecureRelRecordType getSchema() {
//        return codeGenerator.getSchema();
//    }

    public Party getParty() {
        if (parentSegment != null)
            return parentSegment.party;
        return null;
    }

    public String getWorkerId() {
        if (parentSegment != null)
            return parentSegment.workerId;
        return null;
    }

    @Override
    public String toString() {

        String ret = packageName + getParty() + ", " + getWorkerId();
        ret += "  source: " + query.getQueryStmt();

        return ret;
    }

}
