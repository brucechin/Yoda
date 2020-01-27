package org.yoda.executor.smc;

import com.oblivm.backend.flexsc.CompEnv;
import com.oblivm.backend.flexsc.Party;
import com.oblivm.backend.gc.GCGenComp;
import com.oblivm.backend.gc.GCSignal;
import com.oblivm.backend.lang.inter.Util;
import com.oblivm.backend.oram.SecureArray;
import org.yoda.db.data.QueryTable;
import org.yoda.executor.smc.io.SecureOutputReader;
import org.yoda.executor.smc.merge.SecureMerge;
import org.yoda.executor.smc.merge.SecureMergeFactory;
import org.yoda.executor.smc.runnable.SMCRunnable;
import org.yoda.type.SecureRelRecordType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BasicSecureQueryTable implements SecureQueryTable, Serializable {


    public GCSignal[] payload; // raw data
    public GCSignal[] nonNullLength; // 32-bit int for real array length, as opposed to SecureArrray.length, which may include nulls
    transient public SecureRelRecordType schema; // output schema
    public GCSignal R = null; // initialize this for alice
    public String bufferPoolKey;
    Party party;
    transient CompEnv<GCSignal> env = null;
    transient SMCRunnable parent = null;
    QueryTable plaintextOutput;
    int tupleSize;
    SecureMerge merger; //use SecureMerge to restore the plaintext table from secret shares in two parties

    // PI is null if this is part of a SlicedSecureQueryTable
    BasicSecureQueryTable plaintextInput;

    public BasicSecureQueryTable(GCSignal[] data, GCSignal[] length, SecureRelRecordType table, CompEnv<GCSignal> arrayEnv, SMCRunnable r) {
        payload = data;
        nonNullLength = length;
        schema = table;
        env = arrayEnv;
        party = env.party;
        parent = r;
        tupleSize = (table == null) ? 0 : table.size();
        //TODO how to restore in query execution abstraction?(we do not have operator execution class now)
        OperatorExecution op = parent.getRootOperator();
        merger = SecureMergeFactory.get(op);
        R = GCGenComp.R;
    }

    public BasicSecureQueryTable(GCSignal[] data, int tSize, CompEnv<GCSignal> arrayEnv, SMCRunnable r) {
        payload = data; // for use in SlicedSecureQueryTable
        env = arrayEnv;
        party = env.party;
        parent = r;
        tupleSize = tSize;
        OperatorExecution op = parent.getRootOperator();
        merger = SecureMergeFactory.get(op);
        R = GCGenComp.R;


    }


    @Override
    public String getBufferPoolKey() {
        return bufferPoolKey;
    }

    @Override
    public Party getParty() {
        return party;
    }

    @Override
    public QueryTable declassify(SecureQueryTable other, SecureRelRecordType schema) throws Exception {
        if (!(other instanceof BasicSecureQueryTable)) {
            throw new Exception("Cannot decode unmatched tables!");
        }
        QueryTable output = null;

        if (party == Party.Bob) {
            BasicSecureQueryTable aTable = (BasicSecureQueryTable) other;
            output = SecureOutputReader.assembleOutput(aTable, this, schema);
        } else {
            BasicSecureQueryTable bTable = (BasicSecureQueryTable) other;
            output = SecureOutputReader.assembleOutput(this, bTable, schema);
        }

        output.addTuples(plaintextOutput);
        output.addTuples(((BasicSecureQueryTable) other).plaintextOutput);

        return output;
    }

    @Override
    public SecureArray<GCSignal> getSecureArray(CompEnv<GCSignal> localEnv, SMCRunnable runnable) throws Exception {
        if (plaintextOutput == null) { // for use within a sliced operator
            int tupleSize = schema.size();
            int tupleCount = payload.length / tupleSize;
            SecureArray<GCSignal> input = Util.intToSecArray(localEnv, payload, tupleSize, tupleCount);
            input.setNonNullEntries(nonNullLength);

            return input;
        }

        // for use *after* a sliced operator
        return merger.merge(this, localEnv, runnable);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(payload);
        out.writeObject(nonNullLength);
        out.writeObject(R);
        out.writeObject(bufferPoolKey);
        out.writeObject(party);
        out.writeObject(plaintextOutput);
        out.writeInt(tupleSize);
        out.writeObject(merger);
        out.writeObject(plaintextInput);
        //out.writeObject(schema);
        //out.writeObject(parent);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        payload = (GCSignal[]) ois.readObject();
        nonNullLength = (GCSignal[]) ois.readObject();

        R = (GCSignal) ois.readObject();
        bufferPoolKey = (String) ois.readObject();
        party = (Party) ois.readObject();
        plaintextOutput = (QueryTable) ois.readObject();
        tupleSize = ois.readInt();
        merger = (SecureMerge) ois.readObject();
        plaintextInput = (BasicSecureQueryTable) ois.readObject();
        //schema = (SecureRelRecordType)ois.readObject();
        //parent = (SMCRunnable)ois.readObject();
    }

    @Override
    public GCSignal[] getSecurePayload(CompEnv<GCSignal> localEnv) {
        return payload;
    }

    @Override
    public GCSignal[] getSecureNonNullLength(CompEnv<GCSignal> localEnv) {
        return nonNullLength;
    }

    @Override
    public QueryTable getPlaintextOutput() {
        return plaintextOutput;
    }

    @Override
    public void setPlaintextOutput(QueryTable pc) throws Exception {
        plaintextOutput = pc;

    }

    @Override
    public QueryTable declassify(SecureQueryTable bob) throws Exception {
        return declassify(bob, null);
    }


}
