package org.yoda.codegen.smc.query;

import org.yoda.codegen.CodeGenerator;
import org.yoda.codegen.smc.DynamicCompiler;
import org.yoda.db.query.Query;
import org.yoda.type.SecureRelRecordType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SecureQuery implements CodeGenerator, Serializable {
    //in SMCQL there are many secure operator classes which can generate specific .lcc code using procedure templates
    protected Query planNode;
    Map<String, String> variables; //used for lcc code generation. insert variables into procedure templates.
    public SecureQuery() {
    }

    public SecureQuery(Query q) throws Exception {
        //todo use input to initialize variables. variables should be randomly generated.
        planNode = q;
    }

    @Override
    public String generate() throws Exception {
        return null;
    }

    @Override
    public String getPackageName() {
        return planNode.getPackageName();
    }

    public String getQueryName() {
        return planNode.getQueryName();
    }

    public String getQueryStmt() {
        return planNode.getQueryStmt();
    }

    @Override
    public String destFilename() {
        return planNode.destFilename();
    }

    public SecureRelRecordType getSchema() {//output schema
        //TODO
        return null;
    }

    public SecureRelRecordType getInSchema() {//input schema
        //TODO
        return null;
    }


    @Override
    public void compileIt() throws Exception {
        String code = generate();

        if (code != null)
            DynamicCompiler.compileOblivLang(code, this.getPackageName());
    }

    public Map<String, String> baseVariables() throws Exception {
        //TODO how to extract variables needed in .lcc code? will take care of this function after write an ObliVM code template for a query type.

        // variables: size, sortKeySize signal, fid, bitmask
        Map<String, String> variables = new HashMap<String, String>();

        // tuple size in bits
        String tupleSize = Integer.toString(planNode.getSchema().size());
        variables.put("size", tupleSize);

        // for ops with different schemas between input and output
        // overridden by Join

        String dstSize = (projects.isEmpty()) ? variables.get("size") : Integer.toString(projects.get(0).getSchema().size());

        //input and output schema size. maybe used in some lcc code.
        variables.put("sSize", srcSize);
        variables.put("dSize", dstSize);

        //TODO add filter and project variables
        handleFilters(variables);
        handleProjects(variables);

        variables.put("packageName", planNode.getPackageName());


        return variables;

    }


}
