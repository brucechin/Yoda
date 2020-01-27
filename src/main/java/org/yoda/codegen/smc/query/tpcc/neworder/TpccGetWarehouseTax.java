package org.yoda.codegen.smc.query.tpcc.neworder;

import org.yoda.codegen.smc.query.SecureQuery;
import org.yoda.db.query.Query;
import org.yoda.util.CodeGenUtils;

import java.util.Map;

public class TpccGetWarehouseTax extends SecureQuery {
    public TpccGetWarehouseTax(Query q) throws Exception {
        super(q);
    }

    @Override
    public String generate() throws Exception {
        Map<String, String> variables = baseVariables();
        String generatedCode = null;
        generatedCode = CodeGenUtils.generateFromTemplate("/conf/smc/tpcc/neworder/tpccGetWarehouseTax.txt", variables);
        return generatedCode;
    }
}
