package com.oblivm.compiler.log;

import com.oblivm.compiler.ast.Position;

import java.io.PrintStream;

public abstract class Log {
    public PrintStream ps;
    public boolean isOn = true;

    public Log(PrintStream ps) {
        this.ps = ps;
    }

    public abstract String tag();

    public void turnOn() {
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
    }

    public void log(Position pos, String msg) {

        log(pos.toString() + ": " + msg);
    }

    public void log(String msg) {
        if (isOn) {
            ps.println("[" + tag() + "]\t" + msg);
        }
    }
}
