package mylib.graph;

import mylib.ensemble.ElementListe;
import mylib.ensemble.ElementTree;
import mylib.tas.Handle;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by monsio on 05/07/16.
 */
public class Sommet implements ElementTree, ElementListe, Handle {

    private Object value;
    private int debut = 0,fin = 0;
    private int profondeur = 0;
    private boolean visite = false;
    private int indexGraph = -1;

    /**Trouver pont itératif*/
    private LinkedList<Arc> arcsPrecedents = new LinkedList<>();
    /**Trouver pont itératif*/
    private LinkedList<Integer> profondeurs = new LinkedList<>();
    /**Trouver pont itératif*/
    private int min;

    /**ACM-PRIM-PLUS_COUR_CHEMINS*/
    private Sommet previous;
    private int indexTas;
    private int priority;
    private boolean handled;


    public Sommet getPrevious() {
        return previous;
    }

    public void setPrevious(Sommet previous) {
        this.previous = previous;
    }

    @Override
    public void setIndexTas(int tasId) {
        this.indexTas = tasId;
    }

    @Override
    public int getIndexTas() {
        return indexTas;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    /**Ensemble disjoint tree*/

    private ElementTree parent;
    private int rank;

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public ElementTree getParent() {
        return this.parent;
    }

    @Override
    public void setRank(int i) {
        this.rank = i;
    }

    @Override
    public void setParent(ElementTree e) {
        this.parent = e;
    }


    /*Ensemble disjoint liste*/

    private Object ensemble;

    @Override
    public Object getEnsemble() {
        return ensemble;
    }

    @Override
    public void setEnsemble(Object ensemble) {
        this.ensemble = ensemble;
    }

    /*---------------------------*/

    private List<Arc> arcs = new LinkedList<>();

    public Sommet(Object value) {
        this.value = value;
    }

    public void ajoutArc( Arc arc ){
        arcs.add(arc);
    }

    public int getDebut() {
        return debut;
    }

    public void setDebut(int debut) {
        this.debut = debut;
    }

    public int getFin() {
        return fin;
    }

    public void setFin(int fin) {
        this.fin = fin;
    }

    public boolean isVisite() {
        return visite;
    }

    public void setVisite(boolean visite) {
        this.visite = visite;
    }

    public int getProfondeur() {
        return profondeur;
    }

    public void setProfondeur(int profondeur) {
        this.profondeur = profondeur;
    }

    public List<Arc> getArcs() {
        return arcs;
    }

    public void setArcs(List<Arc> arcs) {
        this.arcs = arcs;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Arc getArcPrecedent() {
        return arcsPrecedents.getLast();
    }

    public Arc removeArcPrecedent(){
       return this.arcsPrecedents.removeLast();
    }

    public void setArcPrecedent(Arc arcPrecedent) {
        this.arcsPrecedents.addLast(arcPrecedent);
    }

    public boolean aArcPrecedent() {
        return !this.arcsPrecedents.isEmpty();
    }


    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getIndexGraph() {
        return indexGraph;
    }

    public void setIndexGraph(int indexGraph) {
        this.indexGraph = indexGraph;
    }

    @Override
    public String toString() {
        return "("+value+")";
    }

    public void init() {
        this.visite = false;
        this.debut = 0;
        this.fin = 0;
        this.profondeur = 0;
       // this.indexGraph = 0;
    }
/*
    @Override
    public int hashCode() {
        return  getValue().hashCode();
    }
*/
    @Override
    public boolean equals(Object o) {

        if(o == null)
            return false;

        return getValue().hashCode() == ((Sommet)o).getValue().hashCode();

    }

    public void addProfondeur(int i) {
        this.profondeurs.addLast(i);
    }

    public int getLastProfondeur() {
        return this.profondeurs.getLast();
    }

    public void removeLastProfondeur() {
        this.profondeurs.removeLast();
    }

}
