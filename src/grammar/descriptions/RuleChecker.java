package grammar.descriptions;

import grammar.descriptions.natural.NaturalRule;
import grammar.proof.MetaProof;

import java.util.List;

public interface RuleChecker {
    boolean check(final MetaProof root, final List<MetaProof> children);
    Rule getRule();
}
