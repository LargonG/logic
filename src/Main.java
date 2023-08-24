import builder.MetaBuilder;
import builder.Proof;
import builder.RealBuilder;
import builder.descriptions.Deduction;
import builder.descriptions.Incorrect;
import builder.descriptions.ModusPonens;
import generator.Generator;
import generator.MetaGenerator;
import grammar.Expression;
import parser.*;

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
        Scanner scanner = new Scanner(System.in);
        List<String> input = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.equals("0")) {
                break;
            }
            input.add(line);
        }
        List<Proof> proofs = new MetaBuilder().build(input);
        for (Proof proof : proofs) {
            System.out.println(proof);
        }
        Proof proof = proofs.get(proofs.size() - 1);
        String contextString = joinContext(proof.getContextList());
        if (proof.description instanceof ModusPonens) {
            contextString = joinContext(((ModusPonens) proof.description).alpha.getContextList());
        }
        System.out.println(contextString + "|-" + proofs.get(proofs.size() - 1).expression.suffixString());
        List<Expression> expressions = new RealBuilder().build(proofs);
        for (Expression expression: expressions) {
            System.out.println(expression.suffixString());
        }
        checkRealExpression(expressions, contextString);
    }

    public static void test(int testsCount) {
        Generator generator = new Generator(0);
        Parser parser = new Parser();
        for (int it = 0; it < testsCount; it++) {
            Expression before = generator.generate(2);
            String expr = before.suffixString();
            Expression after = parser.parse(expr);
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
        MetaGenerator generator = new MetaGenerator();
        for (int i = 0; i < tests; i++) {
            MetaGenerator.Test test = generator.generateAxioms(1);
            List<Proof> proofs = new MetaBuilder().build(Arrays.asList(test.lines));
            for (Proof proof : proofs) {
                System.out.println(proof);
            }
            String contextString = proofs.get(proofs.size() - 1)
                    .getContextList()
                    .stream()
                    .map(Expression::suffixString)
                    .reduce((left, right) -> left + "," + right).orElse("");
            List<Expression> expressions = new RealBuilder().build(proofs);
            checkRealExpression(expressions, contextString);
        }
    }

    public static void checkRealExpression(List<Expression> expressions, String contextString) {
        assert !expressions.isEmpty();
        List<String> lines = expressions.stream()
                .map(expression -> contextString + "|-" + expression.suffixString()).collect(Collectors.toList());
        List<Proof> check = new MetaBuilder().build(lines);
        for (Proof p: check) {
            if (p.description instanceof Deduction || p.description instanceof Incorrect) {
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