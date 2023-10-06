package proof.context;

import grammar.Expression;

import java.util.Arrays;
import java.util.List;

public class LinkedContext extends ImmutableContext {
    public LinkedContext(final List<Expression> list,
                         boolean doCopy) {
        super(list, doCopy);
    }

    public LinkedContext(final List<Expression> list) {
        this(list, true);
    }

    public LinkedContext(final Expression... expressions) {
        this(Arrays.asList(expressions), false);
    }

    public int indexOf(Expression expression) {
        return list.indexOf(expression);
    }
}
