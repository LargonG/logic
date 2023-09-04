package generator;

import grammar.Expression;

public interface IGenerator {
    Expression generate(int len);
}
