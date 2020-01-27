package org.yoda.db.query;

import org.yoda.type.SecureRelRecordType;
import org.yoda.util.Utilities;

public class Query {
    //we will use this class to initialize SecureQuery class.
    String queryName_; //for example : TPCCInsertOrder
    String queryStmt_; //for example : insert into order (order_id, customer_id, item_id, quantity) values (....)
    int queryId_;
    SecureRelRecordType schema_; //output schema for this query

    //TODO what else attributes are needed to abstract a SQL query
    public Query(String stmt, String name, int id) {
        queryStmt_ = stmt;
        queryName_ = name;
        queryId_ = id;
    }

    public String getQueryName() {
        return queryName_;
    }

    public String getPackageName() {
        //return the compiled query class name. unique. for DynamicCompiler.loadclass bytecode.
        return "org.yoda.generated." + queryName_ + Integer.toString(queryId_);
    }

    public String getQueryStmt() {
        return queryStmt_;
    }

    public int getQueryId() {
        return queryId_;
    }

    public SecureRelRecordType getSchema() {
        //initialize each query type with their corresponding schema
        return schema_;
    }

    public String destFilename() {
        return Utilities.getCodeGenTarget() + "/" + getQueryId() + "/smc/" + getQueryName() + ".lcc";
    }

}
