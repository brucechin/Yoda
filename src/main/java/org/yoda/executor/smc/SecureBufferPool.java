package org.yoda.executor.smc;

import com.oblivm.backend.flexsc.CompEnv;
import com.oblivm.backend.flexsc.Party;
import com.oblivm.backend.gc.GCGenComp;
import com.oblivm.backend.gc.GCSignal;
import org.yoda.config.SystemConfiguration;
import org.yoda.db.data.QueryTable;
import org.yoda.db.data.Tuple;
import org.yoda.executor.config.ConnectionManager;
import org.yoda.executor.smc.io.SecureOutputReader;
import org.yoda.executor.smc.runnable.SMCRunnable;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

// singleton for maintaining intermediate results between secure operators
public class SecureBufferPool implements Serializable {

    //TODO each ORAM executor server should have an instance of secure buffer pool
    public static int lengthBits = 32;
    // array produced by a given smc config
    private static Map<String, SecureQueryTable> records;
    private static Map<String, String> pointers; // pointers for common table expressions to their actual data sources
    private static Logger logger;
    private static SecureBufferPool instance = null;

    static {
        records = Collections.synchronizedMap(new HashMap<String, SecureQueryTable>());
        pointers = Collections.synchronizedMap(new HashMap<String, String>());
        try {
            logger = SystemConfiguration.getInstance().getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected SecureBufferPool() throws Exception {


    }


    public static SecureBufferPool getInstance() throws Exception {
        if (instance == null) {
            instance = new SecureBufferPool();
        }

        return instance;
    }

    public static String getKey(QueryExecution op) {
        Party p = op.getParty();
        String suffix = (p == Party.Alice) ? "-gen" : "-eva";
        return op.packageName + "." + op.getWorkerId() + suffix;

    }

    public synchronized void addArray(QueryExecution op, GCSignal[] value, GCSignal[] length, CompEnv<GCSignal> env, SMCRunnable parent) {
        String key = getKey(op);

        BasicSecureQueryTable table = new BasicSecureQueryTable(value, length, op.outSchema, env, parent);
        if (op.getParty() == Party.Alice) {
            table.R = GCGenComp.R;
        }

        table.bufferPoolKey = key;
        logger.fine("Secure buffer pool adding new key: " + key + " --> " + table);

        records.put(key, table);

    }

    public synchronized void addArray(QueryExecution op, GCSignal[] value, GCSignal[] length, CompEnv<GCSignal> env, SMCRunnable parent, Tuple t) throws Exception {
        String key = getKey(op);

        BasicSecureQueryTable val = (value == null) ? null : new BasicSecureQueryTable(value, length, op.outSchema, env, parent);
        SlicedSecureQueryTable table = new SlicedSecureQueryTable(op, env, parent, val, t);

        if (op.getParty() == Party.Alice) {
            table.R = GCGenComp.R;
        }

        table.bufferPoolKey = key;
        logger.fine("Secure buffer pool adding new key: " + key + " --> " + table);

        records.put(key, table);

    }

    public synchronized void addArray(QueryExecution op, SecureQueryTable table) {
        String key = getKey(op);
        records.put(key, table);

    }

    public synchronized SecureQueryTable readRecord(QueryExecution op) {
        String key = getKey(op);
        return readRecord(key);
    }

    public synchronized SecureQueryTable readRecord(String key) {
        String packageName = getPackageName(key);
        if (pointers.containsKey(packageName)) {
            String worker = key.substring(key.lastIndexOf('.') + 1);
            key = pointers.get(packageName) + "." + worker;
        }

        SecureQueryTable record = records.get(key);
        boolean found = true;
        if (record == null)
            found = false;


        logger.info("Secure buffer pool looking up key " + key + " found? " + found + " table " + record);
        return record;
    }

    public synchronized SecureQueryTable readRecord(String packageName, String workerId, Party party) {

        String partyStr = (party == Party.Alice) ? "-gen" : "-eva";
        String key = packageName + "." + workerId + partyStr;

        return readRecord(key);
    }

    // pointers are for package names
    public synchronized void addPointer(String src, String dst) {
        pointers.put(src, dst);
    }

    public String getPointers() {
        return pointers.toString();
    }

    public void addPointers(String ptrs) {

        String csv = ptrs.substring(1, ptrs.length() - 1); // strip out { }
        if (csv.isEmpty())
            return;

        String[] entries = csv.split(", ");
        for (String e : entries) {
            String[] tokens = e.split("=");

            pointers.put(tokens[0], tokens[1]);
        }
    }

    public String getPointer(String key) {
        return pointers.get(key);
    }

    public void cleanup(String queryId) throws InterruptedException {
        String prefix = "org.yoda.generated." + queryId;
        List<String> killList = new ArrayList<String>();
        for (String k : records.keySet()) {
            if (k.startsWith(prefix)) {
                killList.add(k);
            }
        }

        for (String k : killList) {
            records.remove(k);
        }


    }

    public GCSignal[] getLength(String packageName, String workerId, Party party) {
        String key = packageName + "." + workerId;
        String partyStr = (party == Party.Alice) ? "-gen" : "-eva";
        key += partyStr;


        return this.getLength(key);
    }

    public GCSignal[] getLength(String key) {

        SecureQueryTable table = records.get(key);
        if (table instanceof BasicSecureQueryTable) {
            return ((BasicSecureQueryTable) table).nonNullLength;
        }

        return null;
    }

    // only works in local mode where both halves of the shared secret are here
    // Alice always ends with -gen, Bob, -eva
    public void printArray(QueryExecution op) throws Exception {
        String aliceKey = getKey(op);
        String baseKey = getPackageName(aliceKey);

        String bobKey = baseKey + "." + ConnectionManager.getInstance().getBob() + "-eva";

        reachSyncPoint(aliceKey, bobKey);

        SecureQueryTable alice = records.get(aliceKey);
        SecureQueryTable bob = records.get(bobKey);
        QueryTable output = SecureOutputReader.assembleOutput((BasicSecureQueryTable) alice, (BasicSecureQueryTable) bob, null);
        System.out.println("Output for package " + baseKey + " is: \n" + output);
    }

    // wait for other thread to catch up
    void reachSyncPoint(String aKey, String bKey) throws InterruptedException {
        while (!(records.containsKey(aKey) && records.containsKey(bKey))) {
            Thread.sleep(500);
        }
    }

    String getPackageName(String key) {
        if (key != null)
            return key.substring(0, key.lastIndexOf('.')); // chop off workerid and party
        return null;
    }
}
