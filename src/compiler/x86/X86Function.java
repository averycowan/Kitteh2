/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.x86;
import compiler.tac.TACStatement;
import compiler.util.Pair;
import java.util.List;

/**
 *
 * @author leijurv
 */
public class X86Function {
    private static final String FUNC_HEADER = "	.cfi_startproc\n"
            + "	pushq	%rbp\n"
            + "	.cfi_def_cfa_offset 16\n"
            + "	.cfi_offset %rbp, -16\n"
            + "	movq	%rsp, %rbp\n"
            + "	.cfi_def_cfa_register %rbp\n";
    private static final String FUNC_FOOTER = "\n	.cfi_endproc\n";
    public static String generateX86(Pair<String, List<TACStatement>> pair) {
        return generateX86(pair.getKey(), pair.getValue());
    }
    public static String generateX86(String name, List<TACStatement> result) {
        long start = System.currentTimeMillis();
        System.out.println("> BEGIN X86 GENERATION FOR " + name);
        X86Emitter emitter = new X86Emitter(name);
        for (int i = 0; i < result.size(); i++) {
            emitter.addStatement(emitter.lineToLabel(i) + ":");
            emitter.addStatement("#   " + result.get(i));
            result.get(i).printx86(emitter);
            emitter.addStatement(""); //nice blank line makes it more readable =)
        }
        StringBuilder resp = new StringBuilder();
        resp.append("\t.globl\t_").append(name).append("\n\t.align\t4, 0x90\n");
        resp.append("_").append(name).append(":\n");
        resp.append(FUNC_HEADER);
        resp.append(emitter.toX86());
        resp.append(FUNC_FOOTER);
        System.out.println("> END X86 GENERATION FOR " + name + " - " + (System.currentTimeMillis() - start) + "ms");
        return resp.toString();
    }
}