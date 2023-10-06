package proof.descriptions.gilbert;

import proof.descriptions.Description;
import proof.GProof;

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
