/***
 * Copyright (C) 2015 by Chang Liu <liuchang@cs.umd.edu>
 */
package com.oblivm.compiler.frontend.bloop;

import com.oblivm.compiler.ast.ASTFunctionDef;
import com.oblivm.compiler.ast.DefaultStatementExpressionVisitor;
import com.oblivm.compiler.ast.expr.*;
import com.oblivm.compiler.ast.stmt.*;
import com.oblivm.compiler.ast.type.ASTIntType;
import com.oblivm.compiler.ast.type.ASTLabel;
import com.oblivm.compiler.ast.type.ASTType;
import com.oblivm.compiler.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class BoundedLoopRewritter extends DefaultStatementExpressionVisitor<List<ASTStatement>, ASTExpression> {

    StateIdentifier si = new StateIdentifier();
    ASTFunctionDef function;

    String stateVar = "__state";
    String count = "__count";
    String newStateVar;

    private int maxCount = 0;

    public String add(String prefix) {
        String ori = prefix;
        boolean f = false;
        for (Pair<ASTType, String> pair : function.localVariables) {
            if (pair.right.equals(prefix)) {
                f = true;
                break;
            }
        }
        int id = 0;
        while (f) {
            prefix = ori + id;
            f = false;
            for (Pair<ASTType, String> pair : function.localVariables) {
                if (pair.right.equals(prefix)) {
                    f = true;
                    break;
                }
            }
            id++;
        }
        return prefix;
    }

    public void rewrite(ASTFunctionDef func) {
        this.function = func;
        stateVar = "_t_state";
        newStateVar = "_t_state_new";
        count = "_t_count";
        stateVar = add(stateVar);
        count = add(count);
        function.localVariables.add(new Pair<ASTType, String>(ASTIntType.get(32, ASTLabel.Secure), stateVar));
        function.localVariables.add(new Pair<ASTType, String>(ASTIntType.get(32, ASTLabel.Secure), newStateVar));
        function.localVariables.add(new Pair<ASTType, String>(ASTIntType.get(32, ASTLabel.Pub), count));
        List<ASTStatement> stmts = new ArrayList<ASTStatement>();
        maxCount = 0;
        for (int i = 0; i < func.body.size(); ++i) {
            stmts.addAll(visit(func.body.get(i)));
        }
        int now = 1;
        while ((1 << now) < maxCount) ++now;
        function.localVariables.get(function.localVariables.size() - 2).left = ASTIntType.get(now, ASTLabel.Secure);
        func.body = stmts;
    }

    public List<ASTStatement> buildOne(ASTStatement stmt) {
        List<ASTStatement> ret = new ArrayList<ASTStatement>();
        ret.add(stmt);
        return ret;
    }

    @Override
    public List<ASTStatement> visit(ASTAssignStatement assignStatement) {
        return buildOne(assignStatement);
    }

    @Override
    public List<ASTStatement> visit(ASTFuncStatement funcStatement) {
        return buildOne(funcStatement);
    }

    List<ASTStatement> visit(List<ASTStatement> stmts) {
        List<ASTStatement> ret = new ArrayList<ASTStatement>();
        for (ASTStatement s : stmts)
            ret.addAll(visit(s));
        return ret;
    }

    @Override
    public List<ASTStatement> visit(ASTIfStatement ifStatement) {
        for (int i = 0; i < ifStatement.trueBranch.size(); ++i) {
            ifStatement.trueBranch = visit(ifStatement.trueBranch);
        }
        for (int i = 0; i < ifStatement.falseBranch.size(); ++i) {
            ifStatement.falseBranch = visit(ifStatement.falseBranch);
        }
        return buildOne(ifStatement);
    }

    @Override
    public List<ASTStatement> visit(ASTBoundedWhileStatement stmt) {
        Label.total = 0;
        StateMachine sm = si.visit(stmt);
        if (Label.total > maxCount)
            maxCount = Label.total;
        return sm.toWhileLoop(stateVar, count);
    }

    @Override
    public List<ASTStatement> visit(ASTOnDummyStatement stmt) {
        stmt.body = visit(stmt.body);
        return buildOne(stmt);
    }

    @Override
    public List<ASTStatement> visit(ASTReturnStatement returnStatement) {
        return buildOne(returnStatement);
    }

    @Override
    public List<ASTStatement> visit(ASTWhileStatement stmt) {
        stmt.body = visit(stmt.body);
        return buildOne(stmt);
    }

    public ASTExpression visit(ASTExpression expression) {
        return expression;
    }


    @Override
    public ASTExpression visit(ASTAndPredicate andPredicate) {
        return andPredicate;
    }

    @Override
    public ASTExpression visit(ASTArrayExpression arrayExpression) {
        return arrayExpression;
    }

    @Override
    public ASTExpression visit(ASTBinaryExpression binaryExpression) {
        return binaryExpression;
    }

    @Override
    public ASTExpression visit(ASTBinaryPredicate binaryPredicate) {
        return binaryPredicate;
    }

    @Override
    public ASTExpression visit(ASTConstantExpression constantExpression) {
        return constantExpression;
    }

    @Override
    public ASTExpression visit(ASTFloatConstantExpression constantExpression) {
        return constantExpression;
    }

    @Override
    public ASTExpression visit(ASTStringConstantExpression constantExpression) {
        return constantExpression;
    }

    @Override
    public ASTExpression visit(ASTFuncExpression funcExpression) {
        return funcExpression;
    }

    @Override
    public ASTExpression visit(ASTNewObjectExpression exp) {
        return exp;
    }

    @Override
    public ASTExpression visit(ASTOrPredicate orPredicate) {
        return orPredicate;
    }

    @Override
    public ASTExpression visit(ASTRecExpression rec) {
        return rec;
    }

    @Override
    public ASTExpression visit(ASTRecTupleExpression tuple) {
        return tuple;
    }

    @Override
    public ASTExpression visit(ASTTupleExpression tuple) {
        return tuple;
    }

    @Override
    public ASTExpression visit(ASTLogExpression exp) {
        return exp;
    }

    @Override
    public ASTExpression visit(ASTRangeExpression exp) {
        return exp;
    }

    @Override
    public ASTExpression visit(ASTVariableExpression variableExpression) {
        return variableExpression;
    }

    @Override
    public ASTExpression visit(ASTNullExpression exp) {
        return exp;
    }

    @Override
    public List<ASTStatement> visit(ASTUsingStatement stmt) {
        stmt.body = visit(stmt.body);
        return buildOne(stmt);
    }

    @Override
    public ASTExpression visit(ASTSizeExpression exp) {
        return exp;
    }

    @Override
    public List<ASTStatement> visit(ASTDebugStatement stmt) {
        return buildOne(stmt);
    }

    @Override
    public List<ASTStatement> visit(ASTSetNonNullArrayEntriesStatement stmt) {
        return buildOne(stmt);
    }

    @Override
    public List<ASTStatement> visit(ASTWriteArrayStatement stmt) {
        return buildOne(stmt);

    }

    @Override
    public List<ASTStatement> visit(ASTReadArrayStatement stmt) {
        return buildOne(stmt);
    }

    @Override
    public List<ASTStatement> visit(ASTGetNonNullArrayEntriesStatement stmt) {
        return buildOne(stmt);
    }

}
