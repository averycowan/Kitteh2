/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expression;
import compiler.Context;
import compiler.tac.IREmitter;
import compiler.tac.TempVarUsage;
import compiler.type.Type;

/**
 *
 * @author leijurv
 */
public abstract class Expression {
    public final Type getType() {
        if (cachedType == null) {
            cachedType = calcType();
            if (cachedType == null) {
                throw new IllegalStateException();
            }
        }
        return cachedType;
    }
    private Type cachedType = null;
    protected abstract Type calcType();//the return type
    public abstract void calcNaiveTAC(Context context, IREmitter emit, TempVarUsage tempVars, String resultLocation);
    public abstract int calcTACLength();//todo cache this like the command tac length
}