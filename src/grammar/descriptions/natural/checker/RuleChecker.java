package grammar.descriptions.natural.checker;

import grammar.descriptions.natural.Rule;
import grammar.proof.MetaProof;

import java.util.List;

public interface RuleChecker {
    boolean check(final MetaProof root, final List<MetaProof> children);
    Rule getRule();
}
