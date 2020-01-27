package org.yoda.codegen.smc;

import org.yoda.util.Utilities;

import java.io.IOException;

public class ByteArrayClassLoader extends ClassLoader {

    byte[] byteArray = null;
    String className = null;

    public ByteArrayClassLoader(String packageName, byte[] data, ClassLoader parent) {
        super(parent);
        className = packageName + ".NoClass";
        byteArray = data;
    }


    public ByteArrayClassLoader() {
        //  Auto-generated constructor stub
    }


    public Class findClass(String packageName) {

        // load local file
        if (className == null && byteArray == null) {
            try {
                byteArray = Utilities.readGeneratedClassFile(packageName);
                className = packageName + ".NoClass";
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return defineClass(className, byteArray, 0, byteArray.length);
    }

}
