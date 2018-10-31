package mylib.ensemble;

/**
 * Created by monsio on 15/07/16.
 */
public interface ItEnsembleDisjoint<E> {
    void creerEnsemble(E e);

    boolean memeEnsemble(E e1, E e2);

    void union(E e1, E e2);
}
