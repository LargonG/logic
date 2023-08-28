package tasks;

import grammar.Expression;
import grammar.proof.MetaProof;
import grammar.proof.NProof;
import parser.ExpressionParser;
import parser.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskD implements Task {
    public static void main(String[] args) {
        new TaskD().solution(args);
    }

    @Override
    public void solution(String... args) {
        String line = Util.getLine(System.in);
        Parser<Expression> parser = new ExpressionParser();
        Expression expression = parser.parse(line);
        List<String> variables = expression.getVariablesNames();
        Map<String, Boolean> variablesValues = new HashMap<>();
        for (int i = 0; i < (1 << variables.size()); i++) {
            for (int j = 0; j < variables.size(); j++) {
                variablesValues.put(variables.get(j), (i & (1 << j)) == 0);
            }

            if (!expression.calculate(variablesValues)) {
                System.out.println("Formula is refutable [" +
                        variablesValues
                                .entrySet()
                                .stream()
                                .map(entry ->entry.getKey() + ":=" + (entry.getValue() ? "T" : "F"))
                                .reduce(
                                        (String left, String right) -> left + "," + right
                                ).orElseThrow(() -> new RuntimeException("WHAT?"))
                        + "]"
                );
                return;
            }
        }

        List<NProof> proofs = expression.createNProof();
        NProof zipped = NProof.zipContext(proofs, variables.size());

        List<MetaProof> result = zipped.getProofsTree();
        for (MetaProof proof: result) {
            System.out.println(proof);
        }
    }
}
