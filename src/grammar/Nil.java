package grammar;

import proof.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import grammar.predicates.arithmetic.Letter;
import proof.NProof;
import proof.PreProof;
import proof.Proof;
import proof.context.ImmutableContext;
import util.Renamer;

import java.util.Map;
import java.util.Set;

public class Nil implements Expression {
    private final static String NAME = "_|_";
    private final static Nil INSTANCE = new Nil();

    private Nil() {

    }

    public static Nil getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        return false;
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return this;
    }

    @Override
    public Expression toNormalForm() {
        return this;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        ImmutableContext newContext = context.merge(this);
        return NProof.zip(
                new PreProof(new Proof(this, newContext), NaturalRule.AXIOM),
                new PreProof(Expression.create(Operator.IMPL, this, this), context,
                        NaturalRule.DEDUCTION, 0)
        );
    }

    @Override
    public String toString() {
        return NAME;
    }

    @Override
    public void suffixString(StringBuilder builder, Operator before, boolean brackets) {
        builder.append(NAME);
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        // do nothing
    }

    @Override
    public void getLettersNames(Set<String> result) {
        // do nothing
    }

    @Override
    public void getLetters(Set<Letter> letters) {
        // do nothing
    }

    @Override
    public boolean canRenameLetter(String oldName, String newName) {
        return true;
    }

    @Override
    public Expression renameLetter(String oldName, String newName) {
        return this;
    }

    @Override
    public PreliminaryFormStep preliminaryFormStep(Renamer renamer, boolean restruct, boolean operations) {
        throw new UnsupportedOperationException("???");
    }
}
