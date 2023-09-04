//package grammar.proof.context;
//
//import grammar.Expression;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public abstract class Context {
//    protected List<Expression> list;
//    protected Map<Expression, Integer> map;
//    protected boolean createdList;
//    protected boolean createdMap;
//
//    protected Context() {
//        this.list = null;
//        this.map = null;
//        this.createdList = false;
//        this.createdMap = false;
//    }
//
//    protected Context(List<Expression> list) {
//        this.list = list;
//        this.createdList = true;
//        this.map = null;
//        this.createdMap = false;
//    }
//
//    protected Context(final Map<Expression, Integer> map) {
//        this.map = map;
//        this.createdMap = true;
//        this.list = null;
//        this.createdList = false;
//    }
//
//    protected abstract List<Expression> createList();
//
//    protected abstract Map<Expression, Integer> createMap();
//
//    protected final void dropList() {
//        this.list = null;
//        this.createdList = false;
//    }
//    protected final void dropMap() {
//        this.map = null;
//        this.createdMap = false;
//    }
//
//    /**
//     * Возвращает списочную интерпретацию контекста, при этом если изначально контекст был задан списком,
//     * то вернёт его без изменения порядка, иначе ничего не гарантированно
//     * @return список выражений, содержащихся в контексте
//     */
//    public final List<Expression> getList() {
//        if (!createdList) {
//            this.list = createList();
//            createdList = true;
//        }
//        return list;
//    }
//
//    /**
//     * Возвращает map-интерпретацию контекста,
//     * @return key -> value, где key - выражение, value - сколько раз встречается в контексте
//     */
//    public final Map<Expression, Integer> getMap() {
//        if (!createdMap) {
//            this.map = createMap();
//            createdMap = true;
//        }
//        return map;
//    }
//    /**
//     * При помощи дедукции перекидывает всё что можно в контекст, возвращая новый, не меняя старый
//     * @return контекст с максимальным количеством выражений
//     */
//    public abstract Context getFullContext(Expression expression);
//
//    /**
//     * Добавляет выражения к этому контексту, меняет его
//     * @return this context or throws UnsupportedOperationException if this context is immutable
//     */
//    public abstract Context add(Expression... elements);
//
//    /**
//     * Добавляет контекст к контексту, меняет тот, от которого мы вызвались
//     * @param context some other context
//     * @return this context or throws UnsupportedOperationException if this context is immutable
//     */
//    public abstract Context add(Context context);
//
//    /**
//     * Убирает элементы из контекста, меняет его
//     * @param elements элементы, которые мы хотим убрать, они не обязательно должны лежать в контексте
//     * @return this context or <code>throw new UnsupportedOperationException</code>
//     */
//    public abstract Context remove(Expression... elements);
//
//    /**
//     * Убирает элементы другого контекста из данного, меняет его
//     * @param context любой контекст, элементы которого мы хотим удалить из этого
//     * @return this context or throws UnsupportedOperationException
//     */
//    public abstract Context remove(Context context);
//
//    /**
//     * Создаёт на базе этого контекста новый, с добавленными выражениями
//     * @param elements добавленные выражения
//     * @return новый контекст, старый не меняется
//     */
//    public abstract Context merge(Expression... elements);
//
//    /**
//     * Сливает два контекста, возвращая новый, не изменяя старые контексты
//     * @param context другой контекст
//     * @return новый контекст, старые не изменился
//     */
//    public abstract Context merge(Context context);
//
//    /**
//     * Возвращает новый контекст без некоторых выражений, не изменяя старые <br>
//     * Было бы эквивалентно функции {@link Context#remove}, если бы изменяла данный элемент
//     * @param elements элементы, которые мы хотим убрать
//     * @return новый контекст, старый не изменился
//     */
//    public abstract Context diff(Expression... elements);
//
//    /**
//     * Вычитает первый контекст из второго, возвращает новый контекст, не меняя старый
//     * @param context некоторый контекст
//     * @return новый контекст, старый не меняется
//     */
//    public abstract Context diff(Context context);
//
//    public boolean contains(Expression expression) {
//        return getMap().containsKey(expression);
//    }
//
//    public int indexOf(Expression expression) {
//        return getList().indexOf(expression);
//    }
//
//    static <T> List<T> toList(Map<T, Integer> map) {
//        return map.entrySet()
//                .stream()
//                .map(entry -> Collections.nCopies(entry.getValue(), entry.getKey()))
//                .reduce(new ArrayList<>(), (left, right) -> { left.addAll(right); return left; }
//                );
//    }
//
//    static <T> Map<T, Integer> toMap(List<T> list) {
//        return list.stream().collect(Collectors.toMap(
//                expr -> expr,
//                expr -> 1,
//                Integer::sum
//        ));
//    }
//
//    static <T> List<T> mergeLists(final List<T> first, final List<T> second) {
//        return new ArrayList<T>(first) {{addAll(second);}};
//    }
//
//    static <T> Map<T, Integer> mergeMaps(final Map<T, Integer> first, final Map<T, Integer> second) {
//        return new HashMap<T, Integer>(first) {{
//                second.forEach((key, value) -> this.merge(key, value, Integer::sum));
//        }};
//    }
//
//    public static Context empty() {
//        return ImmutableContext.empty();
//    }
//
//
//    public static Context of(Expression... expressions) {
//        return ImmutableContext.of(expressions);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Context context = (Context) o;
//        return Objects.equals(getMap(), context.getMap());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getMap());
//    }
//
//    @Override
//    public String toString() {
//        return getList().stream()
//                .map(Expression::suffixString)
//                .reduce((left, right) -> left + "," + right).orElse("");
//    }
//}
