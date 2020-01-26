package org.yoda.executor.smc.merge;

import com.oblivm.backend.flexsc.CompEnv;
import com.oblivm.backend.gc.GCSignal;
import com.oblivm.backend.oram.SecureArray;
import org.yoda.executor.smc.SecureQueryTable;
import org.yoda.executor.smc.runnable.SMCRunnable;

// operator specific for combining plaintext and smc outputs
public interface SecureMerge {

    public SecureArray<GCSignal> merge(SecureQueryTable src, CompEnv<GCSignal> env, SMCRunnable parent) throws Exception;

}
