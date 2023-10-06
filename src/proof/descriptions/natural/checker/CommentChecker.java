package proof.descriptions.natural.checker;

import proof.descriptions.RuleChecker;
import proof.descriptions.natural.NaturalRule;
import proof.MetaProof;

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
