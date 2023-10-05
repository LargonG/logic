package grammar.descriptions.natural.checker;

import grammar.descriptions.RuleChecker;
import grammar.descriptions.natural.NaturalRule;
import grammar.proof.MetaProof;

import java.util.List;

public class CommentChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        return true;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.COMMENT;
    }
}
