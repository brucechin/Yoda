package com.oblivm.backend.util;

import com.oblivm.backend.flexsc.Flag;
import org.apache.commons.cli.ParseException;

public class Sender<T> extends com.oblivm.backend.network.Client implements Runnable {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ParseException, ClassNotFoundException {
        Sender r = new Sender();
        r.run();
        Flag.sw.print();

    }

    public void run() {
        try {
            connect("192.168.1.102", 54321);
            System.out.println("connected");

            while (true) {
                double t = System.nanoTime();
                readBytes(65536);
                double t2 = System.nanoTime();
                System.out.println((t2 - t) / 1000000000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}