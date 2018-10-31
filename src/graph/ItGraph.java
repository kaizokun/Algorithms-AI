package mylib.graph;

import java.util.List;

/**
 * Created by monsio on 05/07/16.
 */
public interface ItGraph {

    void ajoutArc(Sommet source, Sommet destination, int poids, boolean oriente);
    void ajoutArc(Sommet source, Sommet destination, boolean oriente);
    void parcourLargeur();
    void parcourProfondeur();
    ItGraph getTranspose();
    List<Sommet> getListeSommet();

}
