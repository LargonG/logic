//package builder.proof;
//
//import builder.descriptions.Description;
//
//public class IProof {
//    private final Proof proof;
//    private final int id;
//
//    public IProof(final Proof proof,
//                  final int id) {
//        this.proof = proof;
//        this.id = id;
//    }
//
//    public Proof getProof() {
//        return proof;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    @Override
//    public String toString() {
//        return "[" + id + "] " + proof;
//    }
//
//    public static String metaExpression(String expression, int id, Description description) {
//        return "[" + id + "] " + expression + " [" + description + "]";
//    }
//}
