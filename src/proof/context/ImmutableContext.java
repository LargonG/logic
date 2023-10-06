package proof.context;

import grammar.Expression;

import java.util.*;

public class ImmutableContext extends AbstractContext {
    public final static ImmutableContext EMPTY = new ImmutableContext();

    private final static String errorMessage = "Immutable context cannot be changed";

    protected List<Expression> list = null;

    protected ImmutableContext(final Map<Expression, Integer> map,
                               boolean doCopy) {
        super(Collections.unmodifiableMap(doCopy ? new HashMap<>(map) : map), false);
    }

    public ImmutableContext(final Map<Expression, Integer> map) {
        this(map, true);
    }

    protected ImmutableContext(final List<Expression> list,
                               boolean doCopy) {
        this(Context.toMap(list), false);
        this.list = Collections.unmodifiableList(doCopy ? new ArrayList<>(list) : list);
    }

    public ImmutableContext(final List<Expression> list) {
        this(list, true);
    }

    public ImmutableContext(final Expression... expressions) {
        this(Arrays.asList(expressions), false);
    }

    public static ImmutableContext empty() {
        return EMPTY;
    }

    public static ImmutableContext of(Expression... expressions) {
        return new ImmutableContext(expressions);
    }

    @Override
    public final void remove(Expression... expressions) {
        throw new UnsupportedOperationException(errorMessage);
    }

    @Override
    public void remove(List<Expression> list) {
        throw new UnsupportedOperationException(errorMessage);
    }

    @Override
    public final void remove(Context context) {
        throw new UnsupportedOperationException(errorMessage);
    }

    @Override
    public final void add(Expression... expressions) {
        throw new UnsupportedOperationException(errorMessage);
    }

    @Override
    public void add(List<Expression> list) {
        throw new UnsupportedOperationException(errorMessage);
    }

    @Override
    public final void add(Context context) {
        throw new UnsupportedOperationException(errorMessage);
    }

    @Override
    public ImmutableContext merge(Context context) {
        return new ImmutableContext(Context.mergeMaps(getMap(), context.getMap()), false);
    }

    @Override
    public ImmutableContext merge(List<Expression> list) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : list) {
            mp.merge(expression, 1, Integer::sum);
        }
        return new ImmutableContext(mp, false);
    }

    @Override
    public ImmutableContext merge(Expression... expressions) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : expressions) {
            mp.merge(expression, 1, Integer::sum);
        }
        return new ImmutableContext(mp, false);
    }

    @Override
    public ImmutableContext diff(Context context) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Map.Entry<Expression, Integer> entry : context.getMap().entrySet()) {
            mp.merge(entry.getKey(), -entry.getValue(), Integer::sum);
            if (mp.get(entry.getKey()) <= 0) {
                mp.remove(entry.getKey());
            }
        }
        return new ImmutableContext(mp, false);
    }

    @Override
    public ImmutableContext diff(List<Expression> list) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : list) {
            mp.merge(expression, -1, Integer::sum);
            if (mp.get(expression) <= 0) {
                mp.remove(expression);
            }
        }
        return new ImmutableContext(mp, false);
    }

    @Override
    public ImmutableContext diff(Expression... expressions) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : expressions) {
            mp.merge(expression, -1, Integer::sum);
            if (mp.get(expression) <= 0) {
                mp.remove(expression);
            }
        }
        return new ImmutableContext(mp, false);
    }

    @Override
    public Map<Expression, Integer> getMap() {
        return map;
    }

    @Override
    public List<Expression> getList() {
        if (list == null) {
            list = Collections.unmodifiableList(Context.toList(map));
        }
        return list;
    }
}
