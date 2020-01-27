package org.yoda.executor.smc;

import com.oblivm.backend.flexsc.Party;
import com.oblivm.backend.gc.GCSignal;
import com.oblivm.backend.oram.SecureArray;
import org.yoda.executor.step.SecureStep;
import org.yoda.type.SecureRelRecordType;
import org.yoda.util.Utilities;

//modified from SMCQL org.yoda.executor.smc.OperationExecution. because we don't have multiple segments in a query plan for recursive execution, we abstract the query execution content to this class
//there is no plaintext or sliced secure execution
public class QueryExecution {
    private static final long serialVersionUID = 6565213527478140219L;
    public String packageName; //TODO replace this with query type
    public SecureRelRecordType outSchema;
    public byte[] byteCode; // compiled .class for this step
    public ExecutionSegment parentSegment = null; // pointer to segment for SMCConfig
    // for merge case
    String sourceSQL = null;
    public transient SecureArray<GCSignal> output; // optional - for passing around data w/in segment


    public QueryExecution() {

    }


    public QueryExecution(SecureStep s) {
        packageName = s.getPackageName();
        outSchema = s.getSchema(); //change this

        try {
            byteCode = Utilities.readGeneratedClassFile(packageName);//TODO how to read query's byte code??
        } catch (Exception e) {
            // do nothing
        }

    }


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
        if (getSourceSQL() != null)
            ret += "source: " + getSourceSQL();

        return ret;
    }


    public String getSourceSQL() {
        return sourceSQL;
    }


    public void setSourceSQL(String sourceSql) throws Exception {
        this.sourceSQL = sourceSql;
    }
}
