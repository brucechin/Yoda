package org.yoda.codegen;

import org.yoda.type.SecureRelRecordType;

public interface CodeGenerator {

    String generate() throws Exception;


    //String generate(boolean asSecureLeaf) throws Exception;

    public String getPackageName();


    //public SecureRelRecordType getInSchema();//input schema


    public String destFilename();

    public void compileIt() throws Exception;

    //public SecureRelRecordType getSchema();//output schema


    //public SecureRelRecordType getSchema(boolean asSecureLeaf);


}
