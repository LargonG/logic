package generator;

import builder.descriptions.gilbert.AxiomScheme;
import builder.proof.MetaProof;
import grammar.Expression;
import parser.ExpressionParser;
import resolver.Axioms;

import java.util.HashMap;

public class MetaGenerator {
    private final ExpressionParser expressionParser;
    private final Generator expressionGenerator;

    public static class Test {
        public final String[] lines;
        public final String[] result;

        public Test(String[] lines, String[] result) {
            this.lines = lines;
            this.result = result;
        }
    }

    public MetaGenerator() {
        this.expressionParser = new ExpressionParser();
        this.expressionGenerator = new Generator();
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
                put("a", expressionGenerator.generate(len));
                put("b", expressionGenerator.generate(len));
                put("c", expressionGenerator.generate(len));
                put("y", expressionGenerator.generate(len));
            }}).suffixString();
            result[ai] = MetaProof.metaExpression(ai + 1, lines[ai], new AxiomScheme(ai));
            ai++;
        }
        return new Test(lines, result);
    }
}
