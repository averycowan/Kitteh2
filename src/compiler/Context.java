/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import compiler.expression.ExpressionConst;
import compiler.tac.TempVarUsage;
import compiler.type.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Sorta a symbol table that also deals with scoping and temp variables that
 * overlap on the stack
 *
 * @author leijurv
 */
public class Context {
    public static class VarInfo {//this class is here because when it's actually a compiler this will store some more sketchy data like stack offset, stack size, etc
        String name;
        Type type;
        ExpressionConst knownValue;
        int stackLocation;
        //TODO maybe a pointer to the Context / TempVarInfo that instantiated this
        public VarInfo(String name, Type type, int stackLocation) {
            this.name = name;
            this.type = type;
            this.stackLocation = stackLocation;
        }
        @Override
        public String toString() {
            return ("{type: " + type + ", location: " + stackLocation + "}");
        }
        public Type getType() {
            return type;
        }
        public int getStackLocation() {
            return stackLocation;
        }
    }
    private final HashMap<String, VarInfo>[] values;
    private int stackSize;
    private Integer additionalSizeTemp = null;
    private TempVarUsage currentTempVarUsage = null;
    public Context() {
        this.values = new HashMap[]{new HashMap<>()};
        this.stackSize = 0;
    }
    public TempVarUsage getTempVarUsage() {
        if (currentTempVarUsage == null) {
            throw new IllegalStateException("Unable to add int and boolean on line 7");//lol
        }
        return currentTempVarUsage;
    }
    public void setTempVarUsage(TempVarUsage curr) {
        if (curr == null) {
            Stream s = Stream.of(new String[]{});
            s.count();
            s.count();//this causes an exception
        }
        this.currentTempVarUsage = curr;
    }
    public int getTotalStackSize() {
        return stackSize + (additionalSizeTemp == null ? 0 : additionalSizeTemp);
    }
    public int getNonTempStackSize() {
        return stackSize;
    }
    public void updateMaxAdditionalSizeTemp(int tempSize) {
        if (additionalSizeTemp == null) {
            additionalSizeTemp = tempSize;
        } else {
            additionalSizeTemp = Math.max(additionalSizeTemp, tempSize);
        }
    }
    private Context(HashMap<String, VarInfo>[] values, int stackSize) {
        this.values = values;
        this.stackSize = stackSize;
    }
    public Context subContext() {
        HashMap<String, VarInfo>[] temp = new HashMap[values.length + 1];
        System.arraycopy(values, 0, temp, 0, values.length);
        temp[values.length] = new HashMap<>();
        return new Context(temp, stackSize);
    }
    /*public Context superContext() {
     if (values.length <= 1) {
     throw new IllegalStateException("Already top context");
     }
     HashMap<String, VarInfo>[] temp = new HashMap[values.length - 1];
     System.arraycopy(values, 0, temp, 0, temp.length);
     return new Context(temp);
     }
     public Context topContext() {
     return new Context(new HashMap[]{values[0]});
     }*/
    private void defineLocal(String name, VarInfo value) {
        values[values.length - 1].put(name, value);
    }
    public ExpressionConst knownValue(String name) {
        VarInfo info = get(name);
        //System.out.println("Known for " + name + ": " + info);
        if (info == null) {
            return null;
        }
        return info.knownValue;
    }
    public boolean varDefined(String name) {
        for (HashMap<String, VarInfo> value : values) {
            if (value.containsKey(name)) {
                return true;
            }
        }
        return false;
    }
    public void setType(String name, Type type) {
        if (varDefined(name)) {
            throw new IllegalStateException(name + " is already defined -_-");
        }
        defineLocal(name, new VarInfo(name, type, stackSize));//Otherwise define it as local
        stackSize += type.getSizeBytes();
    }
    public Type getType(String name) {
        VarInfo info = get(name);
        return info == null ? null : info.type;//deviously pass off the inevitable nullpointerexception
    }
    public void setKnownValue(String name, ExpressionConst val) {
        get(name).knownValue = val;
    }
    public void clearKnownValue(String name) {
        setKnownValue(name, null);
    }
    public int getStackLocation(String name) {
        return get(name).stackLocation;
    }
    public VarInfo get(String name) {
        for (int i = values.length - 1; i >= 0; i--) {
            VarInfo possibleValue = values[i].get(name);
            if (possibleValue != null) {
                return possibleValue;
            }
        }
        if (currentTempVarUsage != null) {
            VarInfo pos = currentTempVarUsage.getInfo(name);
            if (pos != null) {
                return pos;
            }
        }
        System.out.println("WARNING: Unable to find requested variable named '" + name + "'. Returning null. Context is " + toString());
        return null;
    }
    @Override
    public String toString() {
        return Arrays.asList(values).toString();
    }
}
