package client;

public class SqlCompiler {
    String query_;
    String queryId_;


    public SqlCompiler(String sql) throws Exception {

    }

    public String getQueryId() {
        return queryId_;
    }

    public boolean compileEmpCode() throws Exception {
        return true;
    }

    public String getEmpCode() throws Exception {
        //get compiled emp code for SQL query
        return null;
    }
}
