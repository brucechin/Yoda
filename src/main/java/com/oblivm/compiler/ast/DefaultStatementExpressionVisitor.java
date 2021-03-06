/***
 * Copyright (C) 2015 by Chang Liu <liuchang@cs.umd.edu>
 */
package com.oblivm.compiler.ast;

import com.oblivm.compiler.ast.expr.*;
import com.oblivm.compiler.ast.stmt.*;

/**
 * Visitor pattern class used for traversing the abstract syntax tree created
 * after parsing. This class defines our "visit" functions which are triggered
 * during the creation of the target abstract syntax tree.
 *
 * @param <T1>
 * @param <T2>
 */
public abstract class DefaultStatementExpressionVisitor<T1, T2> implements StatementVisitor<T1>, ExpressionVisitor<T2> {

    public T2 visitNull() {
        throw new RuntimeException("Unsupported null expression!");
    }

    public T2 visit(ASTPredicate predicate) {
        if (predicate instanceof ASTBinaryPredicate) {
            return visit((ASTBinaryPredicate) predicate);
        } else if (predicate instanceof ASTAndPredicate) {
            return visit((ASTAndPredicate) predicate);
        } else if (predicate instanceof ASTOrPredicate) {
            return visit((ASTOrPredicate) predicate);
        } else
            throw new RuntimeException("Unknown Predicate!");
    }

    public T2 visit(ASTExpression expression) {
        if (expression == null) {
            return visitNull();
        } else if (expression instanceof ASTBinaryExpression) {
            return visit((ASTBinaryExpression) expression);
        } else if (expression instanceof ASTConstantExpression) {
            return visit((ASTConstantExpression) expression);
        } else if (expression instanceof ASTFloatConstantExpression) {
            return visit((ASTFloatConstantExpression) expression);
        } else if (expression instanceof ASTStringConstantExpression) {
            return visit((ASTStringConstantExpression) expression);
        } else if (expression instanceof ASTArrayExpression) {
            return visit((ASTArrayExpression) expression);
        } else if (expression instanceof ASTRecExpression) {
            return visit((ASTRecExpression) expression);
        } else if (expression instanceof ASTVariableExpression) {
            return visit((ASTVariableExpression) expression);
        } else if (expression instanceof ASTPredicate) {
            return visit((ASTPredicate) expression);
        } else if (expression instanceof ASTFuncExpression) {
            return visit((ASTFuncExpression) expression);
        } else if (expression instanceof ASTNewObjectExpression) {
            return visit((ASTNewObjectExpression) expression);
        } else if (expression instanceof ASTRecTupleExpression) {
            return visit((ASTRecTupleExpression) expression);
        } else if (expression instanceof ASTTupleExpression) {
            return visit((ASTTupleExpression) expression);
        } else if (expression instanceof ASTLogExpression) {
            return visit((ASTLogExpression) expression);
        } else if (expression instanceof ASTRangeExpression) {
            return visit((ASTRangeExpression) expression);
        } else if (expression instanceof ASTNullExpression) {
            return visit((ASTNullExpression) expression);
        } else if (expression instanceof ASTSizeExpression) {
            return visit((ASTSizeExpression) expression);
        } else
            throw new RuntimeException("Unknown Expression!");
    }

    @Override
    public T1 visit(ASTStatement statement) {
        if (statement instanceof ASTIfStatement) {
            return visit((ASTIfStatement) statement);
        } else if (statement instanceof ASTAssignStatement) {
            return visit((ASTAssignStatement) statement);
        } else if (statement instanceof ASTWhileStatement) {
            return visit((ASTWhileStatement) statement);
        } else if (statement instanceof ASTReturnStatement) {
            return visit((ASTReturnStatement) statement);
        } else if (statement instanceof ASTFuncStatement) {
            return visit((ASTFuncStatement) statement);
        } else if (statement instanceof ASTOnDummyStatement) {
            return visit((ASTOnDummyStatement) statement);
        } else if (statement instanceof ASTBoundedWhileStatement) {
            return visit((ASTBoundedWhileStatement) statement);
        } else if (statement instanceof ASTUsingStatement) {
            return visit((ASTUsingStatement) statement);
        } else if (statement instanceof ASTDebugStatement) {
            return visit((ASTDebugStatement) statement);
        } else if (statement instanceof ASTSetNonNullArrayEntriesStatement) {
            return visit((ASTSetNonNullArrayEntriesStatement) statement);
        } else if (statement instanceof ASTGetNonNullArrayEntriesStatement) {
            return visit((ASTGetNonNullArrayEntriesStatement) statement);
        } else if (statement instanceof ASTWriteArrayStatement) {
            return visit((ASTWriteArrayStatement) statement);
        } else if (statement instanceof ASTReadArrayStatement) {
            return visit((ASTReadArrayStatement) statement);
        }

        throw new RuntimeException("Unknown Statement!");
    }

}
