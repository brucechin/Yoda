package org.yoda.codegen.smc.query;

import org.yoda.codegen.smc.query.tpcc.neworder.TpccGetWarehouseTax;
import org.yoda.db.query.Query;

public class SecureQueryFactory {
    public static SecureQuery get(Query q) throws Exception {
        switch (q.getQueryName()) {
            //TODO return corresponding SecureQuery instance.
            //TODO do we really need to make fine-grained abstraction for each query type? or we can simply do CodeGenUtils.generateFromTemplate(corresponding query template path).
            case "TpccGetWarehouseTax":
                return new TpccGetWarehouseTax(q);
            default:
                return null;
        }
    }
}
