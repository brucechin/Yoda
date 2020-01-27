package org.yoda.runner;

import org.apache.calcite.sql.SqlDialect;
import org.yoda.config.SystemConfiguration;
import org.yoda.db.data.QueryTable;
import org.yoda.executor.YodaQueryExecutor;
import org.yoda.executor.config.WorkerConfiguration;
import org.yoda.executor.smc.ExecutionSegment;
import org.yoda.util.Utilities;

public class YodaRunner {
    protected static WorkerConfiguration honestBroker;
    protected SqlDialect dialect = SqlDialect.DatabaseProduct.POSTGRESQL.getDialect();
    protected String codePath = Utilities.getSMCQLRoot() + "/conf/workload/sql";

    private static void setUp() throws Exception {
        System.setProperty("smcql.setup", Utilities.getSMCQLRoot() + "/conf/setup.localhost");
        honestBroker = SystemConfiguration.getInstance().getHonestBrokerConfig();
        SystemConfiguration.getInstance().resetCounters();
    }

    public static void main(String[] args) throws Exception {
        setUp();

        String sql = args[0];
        System.out.println("\nQuery:\n" + sql);

        String aWorkerId = args[1];
        String bWorkerId = args[2];

        String testName = "userQuery";

        YodaQueryExecutor exec = new YodaQueryExecutor(aWorkerId, bWorkerId);
        //TODO how to build Execution Segment for a query without querycompiler(which accepts a query plan tree and iterate throught the tree and build ExecutionSegments for each node)
        ExecutionSegment segment = new ExecutionSegment();
        exec.runSecure(segment);

        QueryTable results = exec.getOutput();
        System.out.println("\nOutput:\n" + results);
        System.exit(0);
    }

}
