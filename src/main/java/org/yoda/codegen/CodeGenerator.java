package org.yoda.codegen;

public interface CodeGenerator {

	String generate() throws Exception;


	//String generate(boolean asSecureLeaf) throws Exception;

	public String getPackageName();


	//public SecureRelRecordType getInSchema();


	public String destFilename();

	public void compileIt() throws Exception;

	//public SecureRelRecordType getSchema();


	//public SecureRelRecordType getSchema(boolean asSecureLeaf);


}
