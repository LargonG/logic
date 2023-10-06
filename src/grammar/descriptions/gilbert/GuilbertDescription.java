package grammar.descriptions.gilbert;

import grammar.descriptions.Description;
import grammar.proof.GProof;

import java.util.List;

public class GuilbertDescription extends Description {
    public GuilbertDescription(GuilbertRule rule, List<GProof> links) {
        super(rule, links);
    }

    public GuilbertDescription(GuilbertRule rule, GProof... links) {
        super(rule, links);
    }

    @Override
    public List<GProof> getLinks() {
        return super.getLinks();
    }
}
