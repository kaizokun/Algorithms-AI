package dataStructure.tas;


import java.util.*;

/**
 * Created by monsio on 9/08/2015.
 */
public class TasMax {

    protected ArrayList<ItPileData> tas = new ArrayList<ItPileData>();

    public TasMax( ArrayList<ItPileData> tab) {

        this.tas = tab;
        this.buildPile();
    }

    public TasMax(){
    }

    public boolean isEmpty(){
        return tas.isEmpty();
    }

    protected int cmp( Double o1, Double o2){
        return o1.compareTo(o2);
    }
/**
 * construction du tas en entassant les noeuds qui ne sont pas considérés
 * comme des feuilles et donc des tas max triviaux
 * */
    protected void buildPile(){

        int size = tas.size();

        for( int i = 0 ; i < size ; i ++  ){
           tas.get(i).setIndex(i);
        }

        for( int i = (size/2 )-1 ; i >= 0 ; i -- ){
            pileUp(i, tas.size());
        }
    }


    /**
     * procedure pour placer un element i au bon endroit, en l'echangeant avec le plus grand(petit) de ses enfants,
     * puis en rappellant la fonction entasser sur la nouvelle position, au cas ou la propriété du tax max n'est pas respecté.
     * */
    protected void pileUp( int i, int size ){

        int left = left(i),
                right = right(i),
                max;

        if( left < size && cmp(tas.get(left).getPriority(), tas.get(i).getPriority()) > 0 ){
            max = left;
        }else{
            max = i;
        }

        if( right < size && cmp(tas.get(right).getPriority(), tas.get(max).getPriority()) > 0 ){
            max = right;
        }

        if( max != i ){

            ItPileData tmp = tas.get(i);

            tas.set(i, tas.get(max));
            tas.set(max, tmp);

            /*---Modification de la trace de l index----*/

            tas.get(i).setIndex(i);
            tas.get(max).setIndex(max);

            pileUp(max, size);
        }

    }

    public void insert( ItPileData key ){

        //on sauvegarde la priorité de la valeur que l'on souhaite ajouter
        Double prio = key.getPriority();

        //on donne à la clé une valeur infini tres petite(grande) pour un tas max(min)
        key.setPriority(getInfiniteLesserPriority());

        //on ajoute la clé à la fin du tableau
        tas.add(key);
       // key.setTas(this);
        key.setIndex(tas.size()-1);

        try {
            //on augmente la priorité de cette clé en lui redonnant la priorité initiale
            this.upKey(tas.size() - 1, prio);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected double getInfiniteLesserPriority() {
        return Double.MIN_VALUE;
    }

    protected double getInfiniteGreatestPriority() {
        return Double.MAX_VALUE;
    }

    public void upKey(ItPileData data, Double newPriority) throws Exception{
        upKey(data.getIndex(), newPriority);
    }

    public void upKey(int i, Double newPriority) throws Exception{


        /*
        * pour un tas max on compare newPriority à l'ancienne, si la nouvelle priorité est supérieure càd cmp > 0
        * pour un tas min la fonction cmp, redefinie, inverse l'ordre des deux valeurs à comparer
        * c'est donc l'ancienne valeur qui doit être supérieure pour être remplacé
        * */

        if( cmp(newPriority, tas.get(i).getPriority()) <= 0 ){
            throw  new Exception("Nouvelle priorit? plus faible ou egale - NEW : "+newPriority+" <> OLD : "+tas.get(i).getPriority());
        }

        tas.get(i).setPriority(newPriority);

        for( ; i > 0 && cmp(newPriority, tas.get(parent(i)).getPriority() ) >= 1 ; i = parent(i) ){
            int iParent = parent(i);
            ItPileData parent = tas.get(iParent);
            tas.set(iParent,tas.get(i));
            tas.get(iParent).setIndex(iParent);//mise a jour de l index dans le handle
            tas.set(i,parent);
            tas.get(i).setIndex(i);//mise a jour de l index dans le handle
        }

    }

    public void downKey(ItPileData data, Double newPriority) throws Exception{
        downKey(data.getIndex(), newPriority);
    }

    public void downKey(int i, Double newPriority) throws Exception{

        if( cmp(newPriority,tas.get(i).getPriority()) >= 0 ){
            throw  new Exception("Nouvelle priorit? plus forte ou égale.");
        }

        tas.get(i).setPriority(newPriority);

        pileUp(i,tas.size());

    }

    public Object maxPile(){
        return this.tas.get(0);
    }

    public ItPileData extractMax(){

        if(tas.size() < 1){
            return null;
        }

        if(tas.size() == 1)
            return tas.remove(0);

        //récupere l'indice du dernier element
        int end = tas.size() - 1;
        //sauvegarde le premier element du tas le max (min) pour un tas max (min)
        ItPileData tmp = tas.get(0);
        //ajoute en position zero l'element de trouvant ? la fin du tableau, soit un element ? priorit? faible pour un tas max
        tas.set(0,tas.get(end));
        //retire du tableau l'element de fin recopi? au d?but
        tas.remove(end);
        //met a jour l index correspondant ? l'object dans l'object
        tas.get(0).setIndex(0);
        //recreer un tas max pour positionner correctement l'element
        pileUp(0,end);

        return tmp;
    }

    public void removeFromTas(int i){

        try {
            upKey(i,getInfiniteGreatestPriority());
            show(tas);
            extractMax();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeFromTas(PileData data){
        removeFromTas(data.getIndex());
    }

    /*-----------------Calcul d'indices------------------*/

    protected static int parent( int i){
        return ((i + 1) >> 1 ) - 1 ;
    }

    protected static int left( int i){
        return ((i + 1 ) << 1) - 1;
    }

    protected static int right( int i){
        return left(i) + 1;
    }

    /*-------------------TRI PAR TAS----------------*/


    /**
     * trie le tableau a partir du tas,
     * consiste à echanger l' element maximum situé au debut avec celui de fin
     * puis de rappeler la fonction sur l'elements de debut pour le repositionner au bon endroit
     * et ainsi de suite.
     * T(n) = O(n Log n) ou log n est la hauteur du tas.
     * */
    public ArrayList sort(){

        int size = tas.size();

        for( int i = size - 1 ; i >= 1 ; i -- ){

            ItPileData tmp = tas.get(0);
            tas.set(0,tas.get(i));
            tas.set(i,tmp);

            tas.get(0).setIndex(0);
            tas.get(i).setIndex(i);

            size --;

            pileUp(0,size);

        }

        return tas;
    }


    /*--------------------GETTER SETTER----------------*/


    public ArrayList<ItPileData> getTas() {
        return tas;
    }

    public static void main(String[] args) {

        ArrayList<ItPileData> dataTab = new ArrayList<ItPileData>();

        dataTab.add(new PileData(1,"data 1"));
        dataTab.add(new PileData(2,"data 2"));
        dataTab.add(new PileData(3,"data 3"));
        dataTab.add(new PileData(4,"data 4"));

        //TasMin tas = new TasMin(dataTab);

        TasMax tas = new TasMax(dataTab);

       // Util.show(tas.getTas());

        tas.insert(new PileData(5,"data 5"));

      //  Util.show(tas.getTas());

        tas.insert(new PileData(6,"data 6"));

       show(tas.getTas());

        try {

/*
            System.out.println(tas.extractMax());
            System.out.println(tas.extractMax());
            System.out.println(tas.extractMax());

            Util.show(tas.getTas());
*/
           //tas.upKey(5,0);

            tas.removeFromTas(3);

            show(tas.getTas());

        } catch (Exception e) {
            e.printStackTrace();
        }



        // Util.show(tas.getTas());
/*
        tas.sortDomain();

        Util.show(tas.getTas());
*/

    }

    public static void show( ArrayList array){
        System.out.println();
        for( Object e : array ){
            System.out.print(e);
        }
        System.out.println();
    }

}
