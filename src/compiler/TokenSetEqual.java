/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

/**
 *
 * @author leijurv
 */
public class TokenSetEqual extends Token {
    boolean inferType;
    public TokenSetEqual(boolean inferType) {
        this.inferType = inferType;
    }
    @Override
    public String toString() {
        return inferType ? ":=" : "=";
        //return "=";
    }
}
