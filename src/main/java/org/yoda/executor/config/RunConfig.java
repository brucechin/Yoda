package org.yoda.executor.config;

import com.oblivm.backend.flexsc.Mode;

import java.io.Serializable;

// parts of config that are agnostic to Alice/Bob
public class RunConfig implements Serializable {

    public int port = 54321;

    ;
    public Mode smcMode = Mode.REAL;
    public String host = "localhost"; // location of generator

    public RunConfig() {

    }


    public RunConfig(int aPort, Mode aMode, String aHost) {
        port = aPort;
        smcMode = aMode;
        host = aHost;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RunConfig) {
            RunConfig r = (RunConfig) o;
            if (this.port == r.port
                    && this.smcMode == r.smcMode
                    && this.host.equals(r.host)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return host + ":" + port + " using " + smcMode;
    }

    public enum ExecutionMode {Plain, Slice, Secure}

}
