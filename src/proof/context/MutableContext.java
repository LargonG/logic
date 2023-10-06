package proof.context;

import grammar.Expression;

import java.util.*;

public class MutableContext extends AbstractContext {
    public MutableContext(final Map<Expression, Integer> map,
                          final boolean doCopy) {
        super(map, doCopy);
    }

    public MutableContext(final Map<Expression, Integer> map) {
        this(map, true);
    }

    public MutableContext(final List<Expression> list) {
        this(Context.toMap(list), false);
    }

    public MutableContext(final Expression... expressions) {
        this(Arrays.asList(expressions));
    }

    public static MutableContext empty() {
        return new MutableContext();
    }

    public static MutableContext of(Expression... expressions) {
        return new MutableContext(expressions);
    }

    @Override
    public void add(Expression... expressions) {
        for (Expression expression : expressions) {
            map.merge(expression, 1, Integer::sum);
        }
    }

    @Override
    public void add(List<Expression> list) {
        for (Expression expression : list) {
            map.merge(expression, 1, Integer::sum);
        }
    }

    @Override
    public void add(Context context) {
        for (Map.Entry<Expression, Integer> entry : context.getMap().entrySet()) {
            map.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    @Override
    public void remove(Expression... expressions) {
        for (Expression expression : expressions) {
            map.merge(expression, -1, Integer::sum);
            removeIfZero(expression);
        }
    }

    @Override
    public void remove(List<Expression> list) {
        for (Expression expression : list) {
            map.merge(expression, -1, Integer::sum);
            removeIfZero(expression);
        }
    }

    @Override
    public void remove(Context context) {
        for (Map.Entry<Expression, Integer> entry : context.getMap().entrySet()) {
            map.merge(entry.getKey(), -entry.getValue(), Integer::sum);
            removeIfZero(entry.getKey());
        }
    }

    private void removeIfZero(Expression expression) {
        if (map.get(expression) <= 0) {
            map.remove(expression);
        }
    }

    @Override
    public MutableContext merge(Context context) {
        return new MutableContext(Context.mergeMaps(getMap(), context.getMap()), false);
    }

    @Override
    public MutableContext merge(List<Expression> list) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : list) {
            mp.merge(expression, 1, Integer::sum);
        }
        return new MutableContext(mp, false);
    }

    @Override
    public MutableContext merge(Expression... expressions) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : expressions) {
            mp.merge(expression, 1, Integer::sum);
        }
        return new MutableContext(mp, false);
    }

    @Override
    public MutableContext diff(Context context) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Map.Entry<Expression, Integer> entry : context.getMap().entrySet()) {
            mp.merge(entry.getKey(), -entry.getValue(), Integer::sum);
            if (mp.get(entry.getKey()) <= 0) {
                mp.remove(entry.getKey());
            }
        }
        return new MutableContext(mp, false);
    }

    @Override
    public MutableContext diff(List<Expression> list) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : list) {
            mp.merge(expression, -1, Integer::sum);
            if (mp.get(expression) <= 0) {
                mp.remove(expression);
            }
        }
        return new MutableContext(mp, false);
    }

    @Override
    public MutableContext diff(Expression... expressions) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression : expressions) {
            mp.merge(expression, -1, Integer::sum);
            if (mp.get(expression) <= 0) {
                mp.remove(expression);
            }
        }
        return new MutableContext(mp, false);
    }

    @Override
    public Map<Expression, Integer> getMap() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public List<Expression> getList() {
        return Context.toList(map);
    }
}
