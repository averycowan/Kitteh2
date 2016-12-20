/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.tac;
import compiler.type.TypeFloat;
import compiler.type.TypeInt32;
import compiler.type.TypeNumerical;
import compiler.x86.X86Emitter;
import compiler.x86.X86Param;
import compiler.x86.X86Register;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author leijurv
 */
public class TACCast extends TACStatement {
    public TACCast(String inputName, String dest) {
        super(inputName, dest);
    }
    @Override
    public List<String> requiredVariables() {
        return Arrays.asList(paramNames[0]);
    }
    @Override
    public List<String> modifiedVariables() {
        return Arrays.asList(paramNames[1]);
    }
    @Override
    protected void onContextKnown() {
        X86Param input = params[0];
        X86Param dest = params[1];
        TypeNumerical inp = (TypeNumerical) input.getType();
        TypeNumerical out = (TypeNumerical) dest.getType();
        if (inp.equals(out)) {
            throw new IllegalStateException(input + " " + dest + " " + inp.getSizeBytes() + " " + out.getSizeBytes() + " " + inp);
        }
    }
    @Override
    public String toString0() {
        return params[1] + " = (" + params[1].getType() + ") " + params[0];
    }
    @Override
    public void printx86(X86Emitter emit) {
        cast(params[0], params[1], emit);
    }
    public static void cast(X86Param input, X86Param dest, X86Emitter emit) {
        TypeNumerical inp = (TypeNumerical) input.getType();
        TypeNumerical out = (TypeNumerical) dest.getType();
        if (out instanceof TypeFloat) {
            if (!(inp instanceof TypeInt32)) {
                throw new RuntimeException("noplease");
            }
            emit.addStatement("cvtsi2ssl " + input.x86() + ", %xmm2");//kill me
            emit.addStatement("movss %xmm2, " + dest.x86());
            return;
        }
        if (inp.getSizeBytes() >= out.getSizeBytes()) {
            //down cast
            if (inp.equals(out)) {
                throw new IllegalStateException(input + " " + dest + " " + inp.getSizeBytes() + " " + out.getSizeBytes() + " " + inp);
            }
            emit.addStatement("mov" + inp.x86typesuffix() + " " + input.x86() + ", " + X86Register.C.getRegister(inp));
        } else {
            //up cast
            emit.addStatement("movs" + inp.x86typesuffix() + "" + out.x86typesuffix() + " " + input.x86() + ", " + X86Register.C.getRegister(out));
        }
        emit.addStatement("mov" + out.x86typesuffix() + " " + X86Register.C.getRegister(out) + ", " + dest.x86());
    }
}
