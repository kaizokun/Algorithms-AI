package mylib.ensemble;

import mylib.list.MyLinkedList;

import java.util.Iterator;

/**
 * Created by monsio on 12/07/16.
 */
public class EnsembleDisjointList implements ItEnsembleDisjoint<ElementListe> {

    @Override
    public void creerEnsemble(ElementListe elementListe){
        MyLinkedList<ElementListe> ensemble = new MyLinkedList();
        ensemble.add(elementListe);
        elementListe.setEnsemble(ensemble);

    }

    @Override
    public boolean memeEnsemble(ElementListe e1, ElementListe e2){

        return e1.getEnsemble().equals(e2.getEnsemble());
    }

    @Override
    public void union(ElementListe e1, ElementListe e2){

        //récuperation du premier element de l'ensemble de e1
        MyLinkedList<ElementListe> ens1 = ((MyLinkedList<ElementListe>) e1.getEnsemble()),
                              ens2 = ((MyLinkedList<ElementListe>) e2.getEnsemble());

        //Le plus petit ensemble est ajouté au plus grand
        if(ens1.size() > ens2.size()){
            MyLinkedList<ElementListe> tmp = ens1;
            ens1 = ens2;
            ens2 = tmp;
        }

        ElementListe e1Last = ens1.getLast();

        //ajout de e2 après e1 : e1.nil <-> e2.first ... e2.last <-> e1.first... e1.last <-> e1.nil
        ens1.append(ens2);

        Iterator<ElementListe> it = ens1.iterator();
        //modification du pointeur vers l'ensemble de e1 dans les elements anciennement dans l'ensemble e2
        while( it.hasNext() && !it.next().equals(e1Last) ){
            it.next().setEnsemble(ens1);

        }

    }

}
