package grammar.predicates.arithmetic;

import java.util.Objects;
import java.util.Set;

public class Letter implements Arithmetic {
    private final String name;

    public Letter(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Letter letter = (Letter) o;
        return Objects.equals(name, letter.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public Arithmetic rename(String oldName, String newName) {
        if (name.equals(oldName)) {
            return new Letter(newName);
        }
        return this;
    }

    @Override
    public void getLettersNames(Set<String> result) {
        result.add(name);
    }

    @Override
    public void getLetters(Set<Letter> letters) {
        letters.add(this);
    }

    @Override
    public String suffixString() {
        return name;
    }

    @Override
    public void suffixString(StringBuilder builder, ArithmeticOperator before, boolean brackets) {
        builder.append(name);
    }

    public String getName() {
        return name;
    }
}
