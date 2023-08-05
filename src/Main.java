import builder.Generator;
import parser.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        test(100000);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        Parser parser = new Parser();
        Expression expr = parser.parse(line);
        System.out.println(expr);
    }

    public static void test(int testsCount) {
        Generator generator = new Generator(0);
        Parser parser = new Parser();
        for (int it = 0; it < testsCount; it++) {
            Expression before = generator.generate(2);
            String expr = before.suffixString(null);
            Expression after = parser.parse(expr);
            if (before.toString().equals(after.toString())) {
                System.out.println("OK");
                System.out.println("Before: " + before);
                System.out.println("After: " + after);
                System.out.println("Suffix: " + before.suffixString(null));
            } else {
                System.out.println("ERROR!");
                System.out.println("Before: " + before);
                System.out.println("After: " + after);
                System.out.println("Suffix before: " + before.suffixString(null));
                System.out.println("Suffix after:  " + after.suffixString(null));
                break;
            }
        }
    }
}