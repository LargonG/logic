package util;

import java.util.Set;

public class Renamer {
    private String base;
    private int iter;
    private final Set<String> letters;

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
