/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.tac.optimize;
import compiler.tac.TACConst;
import compiler.tac.TACFunctionCall;
import compiler.tac.TACStandard;
import compiler.tac.TACStatement;
import compiler.tac.TempVarUsage;
import java.util.ArrayList;

/**
 * Remove temp vars that have no point Like t1=5,a=t1+t2 should become a=5+t2
 *
 * @author leijurv
 */
public class UselessTempVars extends TACOptimization {
    public UselessTempVars(ArrayList<TACStatement> statements) {
        super(statements);
    }
    public static boolean isTempVariable(String s) {
        if (!s.startsWith(TempVarUsage.TEMP_VARIABLE_PREFIX)) {//all temp vars start with t. variables starting with a t are not supported in kitteh
            return false;
        }
        try {
            Integer.parseInt(s.substring(TempVarUsage.TEMP_VARIABLE_PREFIX.length()));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @Override
    public void run() {
        for (int ind = 0; ind < size() - 1; ind++) {
            if (!(get(ind) instanceof TACConst)) {
                continue;
            }
            TACConst curr = (TACConst) get(ind);
            String valSet = curr.destName;
            if (!isTempVariable(valSet)) {
                continue;
            }
            TACStatement next = get(ind + 1);
            if (next instanceof TACStandard) {
                TACStandard n = (TACStandard) next;
                if (n.secondName.equals(valSet)) {
                    System.out.println("Optimizing " + valSet + " " + curr + "    " + next);
                    n.secondName = curr.sourceName;
                    n.second = curr.source;
                    remove(ind);
                    ind = Math.max(-1, ind - 2);
                    continue;
                }
                if (n.firstName.equals(valSet)) {
                    System.out.println("Optimizing " + valSet + " " + curr + "    " + next);
                    n.firstName = curr.sourceName;
                    n.first = curr.source;
                    remove(ind);
                    ind = Math.max(-1, ind - 2);
                    continue;
                }
            }
            if (next instanceof TACConst) {
                TACConst c = (TACConst) next;
                if (c.sourceName.equals(valSet)) {
                    System.out.println("Optimizing " + valSet + " " + curr + "    " + next);
                    c.sourceName = curr.sourceName;
                    c.source = curr.source;
                    remove(ind);
                    ind = Math.max(-1, ind - 2);
                    continue;
                }
            }
            if (next instanceof TACFunctionCall) {
                TACFunctionCall c = (TACFunctionCall) next;
                boolean shouldContinue = false;
                for (int i = 0; i < c.paramNames.size(); i++) {
                    if (c.paramNames.get(i).equals(valSet)) {
                        System.out.println("Optimizing " + valSet + " " + curr + "    " + next);
                        c.paramNames.set(i, curr.sourceName);
                        c.params.set(i, curr.source);
                        remove(ind);
                        ind = Math.max(-1, ind - 2);
                        shouldContinue = true;
                        break;
                    }
                }
                if (shouldContinue) {
                    continue;
                }
            }
        }
    }
}
