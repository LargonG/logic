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
    protected int id;

    public MetaProof(final Proof proof,
                     final Description description,
                     final int id) {
        this.proof = proof;
        this.description = description;
        this.id = id;
    }

    public MetaProof(final Expression expression,
                     final ImmutableContext immutableContext,
                     final Description description,
                     final int id) {
        this.proof = new Proof(expression, immutableContext);
        this.description = description;
        this.id = id;
    }

    /**
     * Возвращает всё дерево доказательства,
     * что связанны с данным доказательством через {@link MetaProof#getDescription() description}
     * @return result контейнер, где в итоге будут лежать доказательства, которые связаны с нашим
     */
    public List<MetaProof> getProofTree() {
        List<MetaProof> result = new ArrayList<>();
        getProofTree(result);
        return result;
    }

    protected abstract void getProofTree(List<MetaProof> proofs);


    public abstract void printProofsTree(PrintWriter out);


    /**
     * Опциональный индекс, нужен для вывода,
     * при этом стоит учитывать, что объектом могут обладать сразу несколько коллекций,
     * или в одной коллекции может лежать сразу несколько ссылок на один и тот же объект,
     * Поэтому лучше таких случаев избегать
     * @return id in some collection, that was called last
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает новый {@link MetaProof#getId id}
     * @param id новый id
     */
    public void setId(int id) {
        this.id = id;
    }

    public Proof getProof() {
        return proof;
    }

    /**
     * Пояснение к доказательству, из чего оно было получено,
     * внутри себя хранит ссылки на предыдущие доказательства <br>
     * Если хотим получить общую картину доказательства (рекурсивно),
     * то вызываем метод {@link MetaProof#printProofsTree(PrintWriter)}
     * @return description
     */
    public Description getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return metaExpression(getId(), getProof().toString(), getDescription());
    }

    public static String metaExpression(int id, String proof, Description description) {
        return "[" + (id + 1) + "] " + proof + " [" + description + "]";
    }
}
