package generator;

import grammar.Expression;
import grammar.descriptions.gilbert.AxiomScheme;
import grammar.proof.MetaProof;
import parser.ExpressionParser;
import resolver.Axioms;

import java.util.HashMap;

public class MetaGenerator {
    private final ExpressionParser expressionParser;
    private final IGenerator generator;

    public static class Test {
        public final String[] lines;
        public final String[] result;

        public Test(String[] lines, String[] result) {
            this.lines = lines;
            this.result = result;
        }
    }

    public MetaGenerator(IGenerator generator) {
        this.expressionParser = new ExpressionParser();
        this.generator = generator;
    }

    /**
     * Creates one expression for each axiom
     * @param len -- length of the binary operator expression
     * @return generated axioms strings & it's proof representation
     */
    public Test generateAxioms(int len) {
        String[] lines = new String[Axioms.values.size()];
        String[] result = new String[Axioms.values.size()];
        int ai = 0;
        for (Expression axiom: Axioms.values) {
            lines[ai] = "|-" + axiom.paste(new HashMap<String, Expression>() {{
                put("a", generator.generate(len));
                put("b", generator.generate(len));
                put("c", generator.generate(len));
                put("y", generator.generate(len));
            }}).toNormalForm().suffixString();
            result[ai] = "[" + (ai + 1) + "] " + MetaProof.metaExpression(lines[ai], new AxiomScheme(ai));
            ai++;
        }
        return new Test(lines, result);
    }
}
