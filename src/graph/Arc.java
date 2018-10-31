package mylib.graph;

import java.util.Comparator;

/**
 * Created by monsio on 05/07/16.
 */
public class Arc implements Comparable<Arc>{

    private Sommet source, destination;
    private int poids = 0;
    private Boolean estPont = true;


    public Arc(Sommet source, Sommet destination) {
        this.source = source;
        this.destination = destination;
    }

    public Arc(Sommet source, Sommet destination,int poids) {
        this(source, destination);
        this.poids = poids;
    }

    public Sommet getDestination() {
        return destination;
    }

    public int getPoids() {
        return poids;
    }

    public void setPoids(int poids) {
        this.poids = poids;
    }

    public void setDestination(Sommet destination) {
        this.destination = destination;
    }

    public Sommet getSource() {
        return source;
    }

    public void setSource(Sommet source) {
        this.source = source;
    }

    public Boolean getEstPont() {
        return estPont;
    }

    public void estPont(Boolean estPont) {
        this.estPont = estPont;
    }

    @Override
    public String toString() {
        return "(" + source.getValue() +" ---("+this.poids+")---> " + destination.getValue()+")";
    }


    @Override
    public int compareTo(Arc arc) {
/*
        int cmp = this.getPoids() - arc.getPoids();

        if (cmp < 0)
            return -1;

        if (cmp == 0)
            return 0;
*/
        return this.getPoids() - arc.getPoids();
    }
}
