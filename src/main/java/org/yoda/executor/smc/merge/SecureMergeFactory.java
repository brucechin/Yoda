package org.yoda.executor.smc.merge;

import org.yoda.executor.smc.OperatorExecution;
import org.yoda.type.SecureRelDataTypeField;
import org.yoda.util.Utilities;

public class SecureMergeFactory {

    public static SecureMerge get(OperatorExecution op) {

        if (op == null)
            return null;

        if (op.parent == null)
            return new MergeProject();

        OperatorExecution parent = op.parent;
        String opID = Utilities.getOperatorId(parent.packageName);
        String opType = opID.replaceAll("\\d*$", "");

        if (opType.equals("Aggregate")) {

            for (SecureRelDataTypeField r : parent.outSchema.getAttributes()) {
                String name = r.getName().toLowerCase();

                if (name.contains("count")) {
                    return new MergeCount();
                }
            }
        }


        return new MergeProject();
    }
}
