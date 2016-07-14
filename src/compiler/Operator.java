/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author leijurv
 */
public enum Operator {//extends Token maybe? might make things easier... idk
    PLUS("+", 50), MINUS("-", 50), MULTIPLY("*", 100), DIVIDE("/", 100), MOD("%", 1000), EQUALS("==", 10), GREATER(">", 10), LESS("<", 10), GREATER_OR_EQUAL(">=", 10), LESS_OR_EQUAL("<=", 10), OR("||", 5), AND("&&", 5), NOT_EQUALS("!=", 10);
    public static final ArrayList<Operator> ORDER = orderOfOperations();//sorry this can't be the first line
    String str;
    int precedence;
    private Operator(String str, int precedence) {
        this.str = str;
        this.precedence = precedence;
    }
    @Override
    public String toString() {
        return str;
    }
    public static ArrayList<Operator> orderOfOperations() {
        ArrayList<Operator> ops = new ArrayList<>(Arrays.asList(values()));
        ops.sort(Comparator.comparingInt(op -> -op.precedence));//reverse order, so that the most important comes first (%) and least important comes last (&&, ||)
        return ops;
    }
}
