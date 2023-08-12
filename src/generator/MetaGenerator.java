package generator;

import builder.Proof;
import builder.descriptions.AxiomScheme;
import parser.Expression;
import parser.Parser;
import resolver.Axioms;

import java.util.HashMap;

public class MetaGenerator {
    private final Parser parser;
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
        this.parser = new Parser();
        this.expressionGenerator = new Generator();
    }

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
            result[ai] = Proof.metaExpression(lines[ai], ai + 1, new AxiomScheme(ai));
            ai++;
        }
        return new Test(lines, result);
    }
}
