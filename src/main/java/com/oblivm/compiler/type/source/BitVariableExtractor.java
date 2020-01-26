/***
 * Copyright (C) 2015 by Chang Liu <liuchang@cs.umd.edu>
 */
package com.oblivm.compiler.type.source;

import com.oblivm.compiler.ast.expr.ASTConstantExpression;
import com.oblivm.compiler.ast.expr.ASTExpression;
import com.oblivm.compiler.ast.expr.ASTVariableExpression;
import com.oblivm.compiler.ast.type.*;

import java.util.HashSet;
import java.util.Set;

public class BitVariableExtractor extends ASTDefaultTypeVisitor<Set<String>> {

    @Override
    public Set<String> visit(ASTArrayType type) {
        return null;
    }

    @Override
    public Set<String> visit(ASTIntType type) {
        Set<String> ret = new HashSet<String>();
        if (type.getBits() instanceof ASTConstantExpression)
            return ret;
        else if (type.getBits() instanceof ASTVariableExpression)
            ret.add(((ASTVariableExpression) type.getBits()).var);
        else
            return null;
        return ret;
    }

    @Override
    public Set<String> visit(ASTFloatType type) {
        Set<String> ret = new HashSet<String>();
        if (type.getBits() instanceof ASTConstantExpression)
            return ret;
        else if (type.getBits() instanceof ASTVariableExpression)
            ret.add(((ASTVariableExpression) type.getBits()).var);
        else
            return null;
        return ret;
    }

    @Override
    public Set<String> visit(ASTRndType type) {
        Set<String> ret = new HashSet<String>();
        if (type.getBits() instanceof ASTConstantExpression)
            return ret;
        else if (type.getBits() instanceof ASTVariableExpression)
            ret.add(((ASTVariableExpression) type.getBits()).var);
        else
            return null;
        return ret;
    }

    @Override
    public Set<String> visit(ASTNativeType type) {
        return null;
    }

    @Override
    public Set<String> visit(ASTRecType type) {
        Set<String> ret = new HashSet<String>();
        for (ASTExpression s : type.bitVariables)
            ret.add(((ASTVariableExpression) s).var);
        return ret;
    }

    @Override
    public Set<String> visit(ASTVariableType type) {
        Set<String> ret = new HashSet<String>();
        for (ASTExpression s : type.bitVars)
            ret.add(((ASTVariableExpression) s).var);
        return ret;
    }

    @Override
    public Set<String> visit(ASTVoidType type) {
        return null;
    }

    @Override
    public Set<String> visit(ASTFunctionType type) {
        return null;
    }

    @Override
    public Set<String> visit(ASTNullType type) {
        return null;
    }

    @Override
    public Set<String> visit(ASTDummyType type) {
        return visit(type.type);
    }

    @Override
    public Set<String> visit(ASTTupleType type) {
        Set<String> ret = visit(type.types.get(0));
        for (int i = 1; i < type.types.size(); ++i)
            ret.addAll(visit(type.types.get(i)));
        return ret;
    }
}

