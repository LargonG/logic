package util;

import java.util.Set;

public class Renamer {
    private final Set<String> letters;
    private final String base;
    private int iter;

    public Renamer(Set<String> letters) {
        this.letters = letters;
        this.base = "a";
        iter = 0;
    }

    public String getNext() {
        String next = base + iter++;
        while (letters.contains(next)) {
            next = base + iter++;
        }
        return next;
    }
}
