package grammar.predicates.quantifiers;

import grammar.Expression;
import grammar.predicates.arithmetic.Letter;

import java.util.Map;

public class ForAll extends Quantifier {
    public ForAll(Letter letter, Expression expression) {
        super(letter, expression, "@");
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        Expression newExpr = expression.paste(values);
        if (newExpr != expression) {
            return new ForAll(letter, newExpr);
        }
        return this;
    }

    @Override
    public Expression toNormalForm() {
        Expression newExpr = expression.toNormalForm();
        if (newExpr != expression) {
            return new ForAll(letter, newExpr);
        }
        return this;
    }

    @Override
    public Expression renameLetter(String oldName, String newName) {
        if (!letter.getName().equals(oldName)) {
            Expression newExpr = expression.renameLetter(oldName, newName);
            if (newExpr != expression) {
                return new ForAll(letter, newExpr);
            }
        }
        return this;
    }
}
