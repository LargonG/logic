package grammar.proof;

import grammar.Expression;
import grammar.descriptions.Description;
import grammar.proof.context.ImmutableContext;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class MetaProof {
    protected Proof proof;
    protected Description description;

    public MetaProof(final Proof proof,
                     final Description description) {
        this.proof = proof;
        this.description = description;
    }

    public MetaProof(final Expression expression,
                     final ImmutableContext immutableContext,
                     final Description description) {
        this.proof = new Proof(expression, immutableContext);
        this.description = description;
    }

    public static <T extends Description> String metaExpression(String proof, T description) {
        return proof + " [" + description + "]";
    }

    /**
     * Возвращает всё дерево доказательства,
     * что связанны с данным доказательством через {@link MetaProof#getDescription() description}
     *
     * @return result контейнер, где в итоге будут лежать доказательства, которые связаны с нашим
     */
    public List<MetaProof> getProofTree() {
        List<MetaProof> result = new ArrayList<>();
        getProofTree(result);
        return result;
    }

    protected abstract void getProofTree(List<MetaProof> proofs);

    public abstract void printProofsTree(PrintWriter out);

    public Proof getProof() {
        return proof;
    }

    /**
     * Пояснение к доказательству, из чего оно было получено,
     * внутри себя хранит ссылки на предыдущие доказательства <br>
     * Если хотим получить общую картину доказательства (рекурсивно),
     * то вызываем метод {@link MetaProof#printProofsTree(PrintWriter)}
     *
     * @return description
     */
    public Description getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return metaExpression(getProof().toString(), getDescription());
    }
}
