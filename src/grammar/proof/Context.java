package grammar.proof;

import grammar.Expression;
import grammar.operators.Operator;

import java.util.*;
import java.util.stream.Collectors;

public class Context {
    protected List<Expression> list;
    protected Map<Expression, Integer> map;
    protected boolean createdList;
    protected boolean createdMap;

    protected Context() {
        this.list = null;
        this.map = null;
        this.createdList = false;
        this.createdMap = false;
    }

    public Context(List<Expression> list) {
        this.list = list;
        this.createdList = true;
        this.map = null;
        this.createdMap = false;
    }

    public Context(final Map<Expression, Integer> map) {
        this.map = map;
        this.createdMap = true;
        this.list = null;
        this.createdList = false;
    }

    private Context(final List<Expression> list,
                             final boolean doCopy) {
        this(toMap(list));
    }

    private Context(final Map<Expression, Integer> map,
                             final boolean doCopy) {
        this(doCopy ? new HashMap<>(map): map);
    }

    public Context(final Context context,
                            final List<Expression> list) {
        super();
        this.map = mergeMaps(context.map, toMap(list));
        this.createdMap = true;
    }

    public Context(final Context context,
                   final Map<Expression, Integer> map) {
        super();
        this.map = mergeMaps(context.map, map);
        this.createdMap = true;
    }

    public Context(final Context context) {
        super();
        this.map = new HashMap<>(context.map);
        this.createdMap = true;
    }

    protected List<Expression> createList() {
        return toList(map);
    }

    protected Map<Expression, Integer> createMap() {
        return toMap(list);
    }

    protected final void dropList() {
        this.list = null;
        this.createdList = false;
    }
    protected final void dropMap() {
        this.map = null;
        this.createdMap = false;
    }

    /**
     * Возвращает списочную интерпретацию контекста, при этом если изначально контекст был задан списком,
     * то вернёт его без изменения порядка, иначе ничего не гарантированно
     * @return список выражений, содержащихся в контексте
     */
    public final List<Expression> getList() {
        if (!createdList) {
            this.list = createList();
            createdList = true;
        }
        return list;
    }

    /**
     * Возвращает map-интерпретацию контекста,
     * @return key -> value, где key - выражение, value - сколько раз встречается в контексте
     */
    public final Map<Expression, Integer> getMap() {
        if (!createdMap) {
            this.map = createMap();
            createdMap = true;
        }
        return map;
    }
    /**
     * При помощи дедукции перекидывает всё что можно в контекст, возвращая новый, не меняя старый
     * @return контекст с максимальным количеством выражений
     */
    public Context getFullContext(Expression expression) {
        List<Expression> sep = Expression.separate(expression, Operator.IMPL);
        return new Context(this, sep.subList(0, sep.size() - 1));
    }

    /**
     * Добавляет выражения к этому контексту, меняет его
     * @return this context or throws UnsupportedOperationException if this context is immutable
     */
    public Context add(Expression... elements) {
        for (Expression expression: elements) {
            map.merge(expression, 1, Integer::sum);
        }
        return this;
    }

    /**
     * Добавляет контекст к контексту, меняет тот, от которого мы вызвались
     * @param context some other context
     * @return this context or throws UnsupportedOperationException if this context is immutable
     */
    public Context add(Context context) {
        if (context.createdMap) {
            for (Map.Entry<Expression, Integer> entry: context.map.entrySet()) {
                map.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        } else {
            for (Expression expression: context.list) {
                map.merge(expression, 1, Integer::sum);
            }
        }

        return this;
    }

    /**
     * Убирает элементы из контекста, меняет его
     * @param elements элементы, которые мы хотим убрать, они не обязательно должны лежать в контексте
     * @return this context or <code>throw new UnsupportedOperationException</code>
     */
    public Context remove(Expression... elements) {
        for (Expression expression: elements) {
            map.merge(expression, -1, Integer::sum);
            removeIfZero(expression);
        }

        return this;
    }

    /**
     * Убирает элементы другого контекста из данного, меняет его
     * @param context любой контекст, элементы которого мы хотим удалить из этого
     * @return this context or throws UnsupportedOperationException
     */
    public Context remove(Context context) {
        if (context.createdMap) {
            for (Map.Entry<Expression, Integer> entry: context.map.entrySet()) {
                map.merge(entry.getKey(), -entry.getValue(), Integer::sum);
                removeIfZero(entry.getKey());
            }
        } else {
            for (Expression expression: context.list) {
                map.merge(expression, -1, Integer::sum);
                removeIfZero(expression);
            }
        }
        return this;
    }

    private void removeIfZero(Expression expression) {
        if (map.get(expression) <= 0) {
            map.remove(expression);
        }
    }

    /**
     * Создаёт на базе этого контекста новый, с добавленными выражениями
     * @param elements добавленные выражения
     * @return новый контекст, старый не меняется
     */
    public Context merge(Expression... elements) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression: elements) {
            mp.merge(expression, 1, Integer::sum);
        }
        return new Context(mp, false);
    }

    /**
     * Сливает два контекста, возвращая новый, не изменяя старые контексты
     * @param context другой контекст
     * @return новый контекст, старые не изменился
     */
    public Context merge(Context context) {
        if (context.createdList) {
            return new Context(this, context.list);
        }
        return new Context(this, context.map);
    }

    /**
     * Возвращает новый контекст без некоторых выражений, не изменяя старые <br>
     * Было бы эквивалентно функции {@link Context#remove}, если бы изменяла данный элемент
     * @param elements элементы, которые мы хотим убрать
     * @return новый контекст, старый не изменился
     */
    public Context diff(Expression... elements) {
        Map<Expression, Integer> mp = new HashMap<>(getMap());
        for (Expression expression: elements) {
            mp.merge(expression, -1, Integer::sum);
            if (mp.get(expression) <= 0) {
                mp.remove(expression);
            }
        }
        return new Context(mp, false);
    }

    /**
     * Вычитает первый контекст из второго, возвращает новый контекст, не меняя старый
     * @param context некоторый контекст
     * @return новый контекст, старый не меняется
     */
    public Context diff(Context context) {
        Map<Expression, Integer> values = new HashMap<>(getMap());
        for (Map.Entry<Expression, Integer> entry: context.getMap().entrySet()) {
            values.merge(entry.getKey(), -entry.getValue(), Integer::sum);
            if (values.get(entry.getKey()) <= 0) {
                values.remove(entry.getKey());
            }
        }
        return new Context(values, false);
    }

    public boolean contains(Expression expression) {
        return getMap().containsKey(expression);
    }

    public int indexOf(Expression expression) {
        return toList(map).indexOf(expression);
    }

    static <T> List<T> toList(Map<T, Integer> map) {
        return map.entrySet()
                .stream()
                .map(entry -> Collections.nCopies(entry.getValue(), entry.getKey()))
                .reduce(new ArrayList<>(), (left, right) -> { left.addAll(right); return left; }
                );
    }

    static <T> Map<T, Integer> toMap(List<T> list) {
        return list.stream().collect(Collectors.toMap(
                expr -> expr,
                expr -> 1,
                Integer::sum
        ));
    }

    static <T> List<T> mergeLists(final List<T> first, final List<T> second) {
        return new ArrayList<T>(first) {{addAll(second);}};
    }

    static <T> Map<T, Integer> mergeMaps(final Map<T, Integer> first, final Map<T, Integer> second) {
        return new HashMap<T, Integer>(first) {{
            second.forEach((key, value) -> this.merge(key, value, Integer::sum));
        }};
    }

    public static Context empty() {
        return new Context(new ArrayList<>(), false);
    }


    public static Context of(Expression... expressions) {
        return new Context(Arrays.asList(expressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return Objects.equals(getMap(), context.getMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMap());
    }

    @Override
    public String toString() {
        return toList(map).stream()
                .map(Expression::suffixString)
                .reduce((left, right) -> left + "," + right).orElse("");
    }
}
