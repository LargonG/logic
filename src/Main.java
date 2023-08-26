import builder.descriptions.gilbert.Deduction;
import builder.descriptions.gilbert.Incorrect;
import builder.proof.GProof;
import builder.proof.MetaProof;
import builder.proof.NProof;
import builder.proof.Proof;
import generator.Generator;
import generator.MetaGenerator;
import grammar.Expression;
import parser.ExpressionParser;
import parser.Parser;
import parser.ProofParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    /**
     * Сейчас данная программа решает задачу C:
     * по введённому корректному доказательству
     * оно строит полное доказательство
     * @param args
     */
    public static void main(String[] args) {
//        testMeta(10000);
//        Scanner scanner = new Scanner(System.in);
//        List<String> input = new ArrayList<>();
//        while (scanner.hasNext()) {
//            String line = scanner.nextLine();
//            if (line.equals("0")) {
//                break;
//            }
//            input.add(line);
//        }
//
//        Parser<Proof> parser = new ProofParser();
//        List<Proof> pr = input.stream().map(parser::parse).collect(Collectors.toList());
//        for (Proof proof: pr) {
//            System.out.println(proof);
//        }
//        System.out.println();
//
//        List<GProof> proofs = GProof.addMeta(pr);
//
//        for (GProof proof : proofs) {
//            System.out.println(proof);
//        }
//        Proof proof = proofs.get(proofs.size() - 1).getProof();
//        String contextString = joinContext(proof.getContext().getList());
//        System.out.println(contextString + "|-" + proofs.get(proofs.size() - 1).getProof().getExpression().suffixString());
//        GProof realProof = proofs.get(proofs.size() - 1).unpackDeduction();
//        List<MetaProof> realProofs = realProof.getProofsTree();
//        for (MetaProof r: realProofs) {
//            System.out.println(r);
//        }
//        List<Expression> expressions = realProofs.stream().map(box -> box.getProof().getExpression()).collect(Collectors.toList());
//        for (Expression expression: expressions) {
//            System.out.println(expression.suffixString());
//        }
//        //checkRealExpression(expressions, contextString);

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        Parser<Expression> parser = new ExpressionParser();
        Expression expression = parser.parse(line);
        List<NProof> proofs = expression.createNProof();
        for (NProof proof: proofs) {
            List<MetaProof> fullProof = proof.getProofsTree();
            for (MetaProof x : fullProof) {
                System.out.println(x);
            }
            System.out.println();
        }
        NProof zip = NProof.zipContext(proofs, expression.getVariablesNames().size());
        List<MetaProof> zips = zip.getProofsTree();
        System.out.println("\nresult: ");
        for (MetaProof proof: zips) {
            System.out.println(proof);
        }
    }

    public static void test(int testsCount) {
        Generator generator = new Generator(0);
        ExpressionParser expressionParser = new ExpressionParser();
        for (int it = 0; it < testsCount; it++) {
            Expression before = generator.generate(2);
            String expr = before.suffixString();
            Expression after = expressionParser.parse(expr);
            if (before.toString().equals(after.toString())) {
                System.out.println("OK");
                System.out.println("Before: " + before);
                System.out.println("After: " + after);
                System.out.println("Suffix: " + before.suffixString());
            } else {
                System.out.println("ERROR!");
                System.out.println("Before: " + before);
                System.out.println("After: " + after);
                System.out.println("Suffix before: " + before.suffixString());
                System.out.println("Suffix after:  " + after.suffixString());
                break;
            }
        }
    }

    public static void testMeta(int tests) {
        Parser<Proof> proofParser = new ProofParser();
        MetaGenerator generator = new MetaGenerator();
        for (int i = 0; i < tests; i++) {
            MetaGenerator.Test test = generator.generateAxioms(1);
            List<GProof> proofs = GProof.addMeta(
                    Arrays.stream(test.lines)
                            .map(proofParser::parse)
                            .collect(Collectors.toList()));
            for (GProof proof : proofs) {
                System.out.println(proof);
            }
            String contextString = proofs.get(proofs.size() - 1).getProof()
                    .getContext().getList()
                    .stream()
                    .map(Expression::suffixString)
                    .reduce((left, right) -> left + "," + right).orElse("");
            List<Expression> expressions = proofs.get(proofs.size() - 1).unpackDeduction()
                    .getProofsTree().stream()
                    .map(box -> box.getProof().getExpression())
                    .collect(Collectors.toList());
            checkRealExpression(expressions, contextString);
        }
    }

    public static void checkRealExpression(List<Expression> expressions, String contextString) {
        assert !expressions.isEmpty();
        Parser<Proof> proofParser = new ProofParser();
        List<String> lines = expressions.stream()
                .map(expression -> contextString + "|-" + expression.suffixString()).collect(Collectors.toList());
        List<GProof> check = GProof.addMeta(lines.stream().map(proofParser::parse).collect(Collectors.toList()));
        for (GProof p: check) {
            if (p.getDescription() instanceof Deduction || p.getDescription() instanceof Incorrect) {
                throw new RuntimeException();
            }
        }
    }

    public static String joinContext(List<Expression> context) {
        return context.stream()
                .map(Expression::suffixString)
                .reduce((left, right) -> left + "," + right).orElse("");
    }

}