import builder.MetaBuilder;
import builder.Proof;
import builder.RealBuilder;
import generator.Generator;
import generator.MetaGenerator;
import parser.*;
import resolver.Axioms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // checkAxioms(100, 2);
        // test(100000);
        // System.out.println("End testing");
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
        List<Expression> realProofs = new RealBuilder().build(proofs);
        System.out.println(proofs.get(proofs.size() - 1).getExpressionString());
        for (Expression expr: realProofs) {
            System.out.println(expr.suffixString());
        }
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
}