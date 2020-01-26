package org.yoda.executor.smc;

import java.io.Serializable;

import org.yoda.db.data.QueryTable;
import org.yoda.executor.smc.runnable.SMCRunnable;
import org.yoda.type.SecureRelRecordType;

import com.oblivm.backend.flexsc.CompEnv;
import com.oblivm.backend.flexsc.Party;
import com.oblivm.backend.gc.GCSignal;
import com.oblivm.backend.oram.SecureArray;

public interface SecureQueryTable extends Serializable {


    public String getBufferPoolKey();

    public Party getParty();

    public QueryTable declassify(SecureQueryTable bob) throws Exception;

    public QueryTable declassify(SecureQueryTable bob, SecureRelRecordType schema) throws Exception;

    public SecureArray<GCSignal> getSecureArray(CompEnv<GCSignal> env, SMCRunnable runnable) throws Exception;

    // for mixed computation
    public void setPlaintextOutput(QueryTable pc) throws Exception;


    public GCSignal[] getSecurePayload(CompEnv<GCSignal> localEnv) throws Exception;

    public GCSignal[] getSecureNonNullLength(CompEnv<GCSignal> localEnv) throws Exception;

    public QueryTable getPlaintextOutput();

}
