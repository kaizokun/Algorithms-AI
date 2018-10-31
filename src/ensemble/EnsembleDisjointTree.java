package mylib.ensemble;

import java.util.LinkedList;

/**
 * Created by monsio on 15/07/16.
 */
public class EnsembleDisjointTree implements ItEnsembleDisjoint<ElementTree> {

    public EnsembleDisjointTree() {

    }

    @Override
    public void creerEnsemble(ElementTree element) {
        element.setRank(0);
        element.setParent(element);

    }

    @Override
    public boolean memeEnsemble(ElementTree e1, ElementTree e2) {

        return this.trouverEnsemble(e1).equals(this.trouverEnsemble(e2));
    }

    @Override
    public void union(ElementTree e1, ElementTree e2) {
        this.lier(trouverEnsemble(e1),trouverEnsemble(e2));
    }

    private void lier(ElementTree e1, ElementTree e2){
        if(e1.getRank() > e2.getRank() ){
            e2.setParent(e1);
        }else{
            e1.setParent(e2);
            if(e1.getRank() == e2.getRank()){
                e2.setRank(e2.getRank()+1);
            }
        }
    }

    private ElementTree trouverEnsemble(ElementTree e){

        LinkedList<ElementTree> pile = new LinkedList<>();

        while ( !e.getParent().equals(e) ){
            pile.addLast(e);
            e = e.getParent();

        }

        while(!pile.isEmpty()) {
            pile.removeLast().setParent(e);
        }

        return e;

    }



}
