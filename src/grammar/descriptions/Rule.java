package grammar.descriptions;

public interface Rule {
    int getCount();

    RuleChecker getChecker();
}
