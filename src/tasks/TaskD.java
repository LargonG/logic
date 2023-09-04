package tasks;

import generator.Generator;
import generator.MetaGenerator;
import generator.NormalGenerator;
import grammar.Expression;
import grammar.proof.MetaProof;
import grammar.proof.NProof;
import grammar.proof.Proof;
import parser.ExpressionParser;
import parser.Parser;
import parser.ProofParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
                                ).orElse("")
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

    private void checkCorrection(final List<NProof> result) {
        for (NProof proof: result) {
            if (!proof.check()) {
                System.out.println("Illegal proof:");
                System.out.println(proof);
                System.out.println("children:");
                for (MetaProof child: proof.getDescription().getLinks()) {
                    System.out.println(child);
                }
                throw new RuntimeException("Illegal proof description");
            }
        }
    }

    private void test(int count, int len) {
        MetaGenerator generator = new MetaGenerator(new NormalGenerator());
        Parser<Proof> parser = new ProofParser();
        for (int it = 0; it < count; it++) {
            System.out.println(it);
            MetaGenerator.Test tst = generator.generateAxioms(len);
            for (int k = 0; k < tst.lines.length; k++) {
                Proof axiom = parser.parse(tst.lines[k]);
                Expression expr = axiom.getExpression();
                System.out.println("Testing " + expr.suffixString() + "...");
                List<String> variables = expr.getVariablesNames();
                Map<String, Boolean> variablesValues = new HashMap<>();
                boolean finish = false;
                for (int i = 0; i < (1 << variables.size()); i++) {
                    for (int j = 0; j < variables.size(); j++) {
                        variablesValues.put(variables.get(j), (i & (1 << j)) == 0);
                    }

                    if (!expr.calculate(variablesValues)) {
                        System.out.println("Formula is refutable [" +
                                variablesValues
                                        .entrySet()
                                        .stream()
                                        .map(entry -> entry.getKey() + ":=" + (entry.getValue() ? "T" : "F"))
                                        .reduce(
                                                (String left, String right) -> left + "," + right
                                        ).orElseThrow(() -> new RuntimeException("WHAT?"))
                                + "]"
                        );
                        finish = true;
                        break;
                    }
                }

                if (finish)
                    continue;

                List<NProof> proofs = expr.createNProof();
                for (NProof proof : proofs) {
                    checkCorrection(proof.getProofsTree().stream().map(pr -> (NProof) pr).collect(Collectors.toList()));
                }
                NProof zipped = NProof.zipContext(proofs, variables.size());

                List<MetaProof> result = zipped.getProofsTree();
                for (MetaProof proof : result) {
                    System.out.println(proof);
                }
                System.out.println();

                checkCorrection(result.stream().map(proof -> (NProof) proof).collect(Collectors.toList()));
            }
        }
    }
}
