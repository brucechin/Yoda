package org.yoda.executor;

import com.oblivm.backend.flexsc.Party;
import com.oblivm.backend.gc.GCSignal;
import org.apache.commons.lang3.StringUtils;
import org.gridkit.nanocloud.Cloud;
import org.gridkit.nanocloud.CloudFactory;
import org.gridkit.nanocloud.RemoteNode;
import org.gridkit.nanocloud.VX;
import org.gridkit.nanocloud.telecontrol.ssh.SshSpiConf;
import org.gridkit.vicluster.ViNode;
import org.gridkit.vicluster.telecontrol.Classpath;
import org.gridkit.vicluster.telecontrol.ssh.RemoteNodeProps;
import org.yoda.config.SystemConfiguration;
import org.yoda.db.data.QueryTable;
import org.yoda.executor.config.ConnectionManager;
import org.yoda.executor.config.WorkerConfiguration;
import org.yoda.executor.smc.ExecutionSegment;
import org.yoda.executor.smc.SecureBufferPool;
import org.yoda.executor.smc.SecureQueryTable;
import org.yoda.executor.smc.runnable.RunnableSegment;
import org.yoda.type.SecureRelRecordType;
import org.yoda.util.Utilities;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

//act like a thread pool in architecture design doc
public class YodaQueryExecutor {
    private static YodaQueryExecutor instance = null; //singleton design
    private SecureRelRecordType lastSchema;
    private List<SecureQueryTable> lastOutput;
    Cloud cloud = null;
    WorkerConfiguration aliceWorker, bobWorker;
    String remotePath;
    Logger logger;
    //TODO add multiple ORAM workers for parallel execution
    //TODO add query queue

    public static YodaQueryExecutor getInstance() throws Exception {
        if (instance == null) {
            ConnectionManager cm = null;
            try {
                cm = ConnectionManager.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert cm != null;
            List<WorkerConfiguration> workers = cm.getWorkerConfigurations();

            if (workers.size() >= 2) {
                String aWorkerId = workers.get(0).workerId;
                String bWorkerId = workers.get(1).workerId;
                try {
                    instance = new YodaQueryExecutor(aWorkerId, bWorkerId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    public YodaQueryExecutor(String aWorker, String bWorker) throws Exception {
        //TODO initialize worker configurations, logger, remote path, cloud, bufferpool

        aliceWorker = ConnectionManager.getInstance().getWorker(aWorker);
        bobWorker = ConnectionManager.getInstance().getWorker(bWorker);
        logger = SystemConfiguration.getInstance().getLogger();

        remotePath = SystemConfiguration.getInstance().getProperty("remote-path");
        if (remotePath == null)
            remotePath = "/tmp/smcql";


        String msg = "Initializing segment executor for " + aWorker + ", " + bWorker + " on " + aliceWorker.hostname + "," + bobWorker.hostname;
        logger.info(msg);

        cloud = CloudFactory.createCloud();
        RemoteNode.at(cloud.node("**")).useSimpleRemoting();

        initializeHost(bobWorker);
        initializeHost(aliceWorker);

        String bufferPoolPointers = SecureBufferPool.getInstance().getPointers();


        cloud.node("**").setProp("plan.pointers", bufferPoolPointers);
        cloud.node("**").setProp("smcql.setup.str", getSetupParameters());
        cloud.node("**").setProp("smcql.connections.str", getConnectionParameters());

        // configure Alice and Bob
        cloud.node(aWorker).setProp("party", "gen");
        cloud.node(aWorker).setProp("workerId", aliceWorker.workerId);

        cloud.node(bWorker).setProp("party", "eva");
        cloud.node(bWorker).setProp("workerId", bobWorker.workerId);

        cloud.node("**").touch();

        cloud.node("**").massExec(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                String pointers = System.getProperty("plan.pointers");
                SecureBufferPool.getInstance().addPointers(pointers);
                return null;

            }
        });
    }

    public Cloud getCloud() {
        return cloud;
    }

    public List<SecureQueryTable> runSecureSegment(ExecutionSegment segment) throws Exception {
        return runSecure(segment);
    }

    private void initializeHost(WorkerConfiguration worker) throws Exception {
        String host = worker.hostname;
        String workerId = worker.workerId;

        ViNode cloudHost = cloud.node(workerId);

        RemoteNodeProps.at(cloudHost).setRemoteHost(host);

        cloudHost.setProp(SshSpiConf.SPI_JAR_CACHE, remotePath);

        if (host.equalsIgnoreCase("localhost")) {
            cloudHost.x(VX.TYPE).setLocal();
            //ViProps.at(cloudHost).setIsolateType(); // enable debugger
        }

		/*if(host.startsWith("codd")) {
			 cloudHost.x(VX.PROCESS).addJvmArg("-Xms1024m").addJvmArg("-Xmx60g");
		}*/
    }

    private String getSetupParameters() throws Exception {

        String srcFile = SystemConfiguration.getInstance().getConfigFile();
        List<String> params = Utilities.readFile(srcFile);
        return StringUtils.join(params.toArray(), '\n');

    }

    private String getConnectionParameters() throws Exception {
        String srcFile = SystemConfiguration.getInstance().getProperty("data-providers");
        List<String> params = Utilities.readFile(srcFile);
        return StringUtils.join(params.toArray(), '\n');

    }

    public static String getRemotePathToJar(final String partOfJarName) throws Exception {
        for (Classpath.ClasspathEntry cpe : Classpath.getClasspath(ClassLoader.getSystemClassLoader())) {

            if (cpe.getFileName().contains(partOfJarName)) {
                return File.separatorChar + cpe.getContentHash() + File.separatorChar + cpe.getFileName();
            }
        }
        throw new Exception("Jar not found!");
    }

    public List<SecureQueryTable> runSecure(ExecutionSegment segment) {

        List<SecureQueryTable> result = cloud.node("**").massExec(new Callable<SecureQueryTable>() {
            @Override
            public SecureQueryTable call() throws Exception {
                Party party = (System.getProperty("party").equals("gen")) ? Party.Alice : Party.Bob;
                String workerId = System.getProperty("workerId");


                segment.party = party;
                segment.workerId = workerId;

                RunnableSegment<GCSignal> runner = new RunnableSegment<GCSignal>(segment);
                if (party == Party.Alice)
                    Thread.sleep(200); // eva must start first


                Thread execThread = runner.runIt();
                execThread.join();

                return runner.getOutput();
            }
        });

        return result;
    }

    public QueryTable getOutput() throws Exception {
        if (lastOutput == null)
            return null;


        SecureQueryTable lhs = lastOutput.get(0);
        SecureQueryTable rhs = lastOutput.get(1);

        return lhs.declassify(rhs, lastSchema);

    }

}
