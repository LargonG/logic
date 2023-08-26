//package builder.proof;
//
//import builder.descriptions.natural.NaturalDescription;
//import grammar.Expression;
//
//import java.util.List;
//
//public class NProof extends MetaProof {
//    public NProof(final Expression expression,
//                  final Context context,
//                  final NaturalDescription description) {
//        super(expression, context, description);
//    }
//
//    public NProof(final Expression expression,
//                  final Context context) {
//        super(expression, context);
//    }
//
//    @Override
//    protected void getProofsTree(List<MetaProof> result) {
//        getProofTree(result, 0);
//    }
//
//    private <T extends Proof> void getProofTree(final List<IProof> result,
//                              final int depth) {
//        List<Proof> links = description.getLinks();
//        for (Proof link: links) {
//            ((NProof) link).getProofTree(result, depth + 1);
//        }
//
//        result.add(new IProof(this, depth));
//    }
//}
