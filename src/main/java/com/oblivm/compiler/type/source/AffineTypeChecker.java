/***
 * Copyright (C) 2015 by Chang Liu <liuchang@cs.umd.edu>
 */
package com.oblivm.compiler.type.source;

import com.oblivm.compiler.ast.type.*;

public class AffineTypeChecker extends ASTDefaultTypeVisitor<Boolean> {
    private static AffineTypeChecker inst = null;

    private AffineTypeChecker() {
    }

    public static AffineTypeChecker get() {
        if (inst == null)
            inst = new AffineTypeChecker();
        return inst;
    }

    public boolean isAffine(ASTType type) {
        return visit(type);
    }

    @Override
    public Boolean visit(ASTArrayType type) {
        return visit(type.type);
    }

    @Override
    public Boolean visit(ASTIntType type) {
        return false;
    }

    @Override
    public Boolean visit(ASTFloatType type) {
        return false;
    }

    @Override
    public Boolean visit(ASTRndType type) {
        return true;
    }

    @Override
    public Boolean visit(ASTNativeType type) {
        // TODO NativeType is by default not an affine type. Change this later
        return false;
    }

    @Override
    public Boolean visit(ASTRecType type) {
        // TODO add support for type annotation indicating affine type or not.
        for (ASTType ty : type.fieldsType.values()) {
            if (visit(ty))
                return true;
        }
        return false;
    }

    @Override
    public Boolean visit(ASTVariableType type) {
        // TODO currently, all generic types are treated as star (not affine) type. Will change this later by type annotation.
        return type.isAffine;
    }

    @Override
    public Boolean visit(ASTVoidType type) {
        return false;
    }

    @Override
    public Boolean visit(ASTFunctionType type) {
        return false;
    }

    @Override
    public Boolean visit(ASTNullType type) {
        return false;
    }

    @Override
    public Boolean visit(ASTDummyType type) {
        // TODO dummy type should by default be a boxed affine type.
        return visit(type.type);
    }

    @Override
    public Boolean visit(ASTTupleType type) {
        for (ASTType ty : type.types)
            if (visit(ty))
                return true;

        return false;
    }

}
