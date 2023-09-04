package grammar.descriptions.natural.checker;

import grammar.descriptions.natural.Rule;
import grammar.proof.MetaProof;

import java.util.List;

public class CommentChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        return true;
    }

    @Override
    public Rule getRule() {
        return Rule.COMMENT;
    }
}
