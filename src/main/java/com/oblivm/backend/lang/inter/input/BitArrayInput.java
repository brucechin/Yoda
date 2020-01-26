/***
 * Copyright (C) 2015 by Chang Liu <liuchang@cs.umd.edu>
 */
package com.oblivm.backend.lang.inter.input;

import com.oblivm.backend.lang.inter.Input;

import java.io.IOException;
import java.util.stream.IntStream;

/**
 * @author Chang Liu
 * <p>
 * JMD: extension for in-memory bit arrays rather than reading from file
 */
public class BitArrayInput implements Input {

    private boolean[] array;
    private int current = 0;

    private BitArrayInput() {

    }

    public static BitArrayInput read(String s) throws IOException {

        IntStream ins = s.chars();
        int[] a = ins.toArray();

        BitArrayInput input = new BitArrayInput();

        input.array = new boolean[a.length];
        for (int i = 0; i < a.length; ++i) {
            input.array[i] = a[i] == '1';
        }
        input.current = 0;
        return input;
    }

    @Override
    public boolean nextBoolean() {
        if (current >= array.length)
            return false;
        return array[current++];
    }

    @Override
    public boolean[] readAll() {
        return array;
    }

    @Override
    public boolean isEnd() {
        return current >= array.length;
    }

    @Override
    public boolean closed() {
        return current >= array.length;
    }


}
