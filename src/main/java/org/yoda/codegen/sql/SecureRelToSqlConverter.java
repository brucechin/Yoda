package org.yoda.codegen.sql;

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.yoda.plan.operator.Filter;
import org.yoda.plan.operator.Operator;
import org.yoda.plan.operator.Project;
import org.yoda.type.SecureRelDataTypeField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecureRelToSqlConverter extends ExtendedRelToSqlConverter {

    private Operator scanOperator;
    private boolean needsRewrite;

    public SecureRelToSqlConverter(SqlDialect dialect, Operator operator) {
        super(dialect);
        scanOperator = operator;
        while (scanOperator instanceof Project || scanOperator instanceof Filter) {
            scanOperator = scanOperator.getChild(0);
        }
        needsRewrite = !scanOperator.secureComputeOrder().isEmpty();
    }

    private List<SqlIdentifier> getOrderAttrs(SqlParserPos sqlParserPos) {
        List<SqlIdentifier> result = new ArrayList<SqlIdentifier>();
        for (SecureRelDataTypeField field : scanOperator.secureComputeOrder()) {
            result.add(new SqlIdentifier(Arrays.asList(field.getName()), sqlParserPos));
        }

        return result;
    }

    private Result addOrderBy(RelNode e) {
        Result x = dispatch(e);

        SqlSelect select = x.asSelect();
        SqlNodeList list = new SqlNodeList(select.getParserPosition());
        for (SqlIdentifier iden : getOrderAttrs(select.getParserPosition()))
            list.add(iden);
        select.setOrderBy(list);

        final Builder builder = new Builder(e, new ArrayList<Clause>(), select, x.qualifiedContext());
        Result res = builder.result();

        return res;
    }

    @Override
    public Result visitChild(int i, RelNode e) {
        if (needsRewrite) {
            needsRewrite = false;
            return addOrderBy(e);
        }

        return dispatch(e);
    }
}
