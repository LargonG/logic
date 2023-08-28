package tasks;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {
    public static String getLine(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        return scanner.nextLine();
    }

    public static List<String> getLines(InputStream stream) {
        Scanner scanner = new Scanner(stream);

        List<String> lines = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.isEmpty() || line.equals("0")) {
                break;
            }
            lines.add(line);
        }

        return lines;
    }
}
