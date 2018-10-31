package mylib.tas;


import java.util.*;

/**
 * Created by monsio on 9/08/2015.
 */
public class TasMax<T> {

    protected ArrayList<Handle> tas = new ArrayList();

    public TasMax( ArrayList<Handle> tab) {
        this.tas = tab;
    }

    public void init(){
        this.buildPile();
    }


    protected int cmp( int o1, int o2){
       // System.out.println(o1+" - "+o2);
       // System.out.println("cmp : "+(o1-o2));


        return o1 - o2;
    }
/**
 * construction du tas en entassant les noeuds qui ne sont pas considérés
 * comme des feuilles et donc des tas max triviaux
 * */
    protected void buildPile(){

        int size = tas.size();

        for( int i = 0 ; i < size ; i ++ ){
           tas.get(i).setIndexTas(i);
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

            Handle tmp = tas.get(i);

            tas.set(i, tas.get(max));
            tas.set(max, tmp);

            /*---Modification de la trace de l index----*/

            tas.get(i).setIndexTas(i);
            tas.get(max).setIndexTas(max);

            pileUp(max, size);
        }


    }

    public void insert( Handle key ){

        //on sauvegarde la priorité de la valeur que l'on souhaite ajouter
        int prio = key.getPriority();

        //on donne à la clé une valeur infini tres petite(grande) pour un tas max(min)
        key.setPriority(getInfiniteLesserPriority());

        //on ajoute la clé à la fin du tableau
        tas.add(key);
       // key.setTas(this);
        key.setIndexTas(tas.size() - 1);

        try {
            //on augmente la priorité de cette clé en lui redonnant la priorité initiale
            upKey(tas.size() - 1, prio);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int getInfiniteLesserPriority() {
        return Integer.MIN_VALUE;
    }

    protected int getInfiniteGreatestPriority() {
        return Integer.MAX_VALUE;
    }

    public void upKey(Handle data, Integer newPriority) throws Exception{
        upKey(data.getIndexTas(), newPriority);
    }

    public void upKey(int i, int newPriority) throws Exception{

        /*
        * pour un tas max on compare newPriority à l'ancienne, si la nouvelle priorité est supérieure càd cmp > 0
        * pour un tas min la fonction cmp, redefinie, inverse l'ordre des deux valeurs à comparer
        * c'est donc l'ancienne valeur qui doit être supérieure pour être remplacé
        * */


        if( cmp(newPriority, tas.get(i).getPriority()) <= 0 ){
            //System.out.println("Nouvelle priorité plus faible ou egale");
            return;
            //throw  new Exception("Nouvelle priorité plus faible ou egale");
        }

        tas.get(i).setPriority(newPriority);

        for( ; i > 0 && cmp(newPriority, tas.get(parent(i)).getPriority() ) >= 1 ; i = parent(i) ){
            int iParent = parent(i);
            Handle parent = tas.get(iParent);
            tas.set(iParent,tas.get(i));
            tas.get(iParent).setIndexTas(iParent);//mise a jour de l index
            tas.set(i,parent);
            tas.get(i).setIndexTas(i);//mise a jour de l index
        }

    }

    public void downKey(Handle data, Integer newPriority) throws Exception{
        downKey(data.getIndexTas(),newPriority);
    }

    public void downKey(int i, Integer newPriority) throws Exception{

        if( cmp(newPriority,tas.get(i).getPriority()) >= 0 ){
            throw  new Exception("Nouvelle priorit? plus forte ou égale.");
        }

        tas.get(i).setPriority(newPriority);

        pileUp(i,tas.size());

    }

    public Object maxPile(){
        return this.tas.get(0);
    }

    public T extractMax() throws Exception{

        if(tas.size() < 1){
            throw new Exception("Tas vide, aucun ?l?ment a extraire");
        }

        if(tas.size() == 1){
            return (T)tas.remove(0);
        }

        //r?cupere l'indice du dernier element
        int end = tas.size() - 1;
        //sauvegarde le premier element du tas le max (min) pour un tas max (min)
        Handle tmp = tas.get(0);
        //ajoute en position zero l'element de trouvant ? la fin du tableau, soit un element ? priorit? faible pour un tas max
        tas.set(0,tas.get(end));
        //retire du tableau l'element de fin recopi? au d?but
        tas.remove(end);
        //met a jour l index correspondant ? l'object dans l'object
        tas.get(0).setIndexTas(0);
        //recreer un tas max pour positionner correctement l'element
        pileUp(0, tas.size());


        return (T)tmp;
    }

    public boolean isEmpty(){
        return this.tas.isEmpty();
    }

    public void removeFromTas(int i){

        try {
            upKey(i,getInfiniteGreatestPriority());
            //show(tas);
            extractMax();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeFromTas(Handle data){
        removeFromTas(data.getIndexTas());
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

            Handle tmp = tas.get(0);
            tas.set(0,tas.get(i));
            tas.set(i,tmp);

            tas.get(0).setIndexTas(0);
            tas.get(i).setIndexTas(i);

            size --;

            pileUp(0,size);

        }

        return tas;
    }



    /*--------------------GETTER SETTER----------------*/


    public ArrayList<Handle> getTas() {
        return tas;
    }

    public static void main(String[] args) {

        Handle[] datas = new DataTest[]{
                new DataTest(1,"A"),new DataTest(2,"F"),
                new DataTest(11,"B"),new DataTest(12,"G"),
                new DataTest(4,"C"),new DataTest(8,"H"),
                new DataTest(5,"D"),new DataTest(9,"I"),
                new DataTest(6,"E"),new DataTest(10,"J")};

        ArrayList<Handle> dataTab = new ArrayList(Arrays.asList(datas));

        TasMax tas = new TasMax(dataTab);

        tas.init();

        Handle a = new DataTest(3,"K"), b = new DataTest(7,"L");

        show(tas.getTas());

        tas.insert(a);

        tas.insert(b);

        show(tas.getTas());

        try {

            while (!tas.isEmpty()){
                System.out.println(tas.extractMax());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        // Util.show(tas.getTas());
/*
        tas.sort();

        Util.show(tas.getTas());
*/

    }

    public static void show( ArrayList array){

        System.out.println("================TAS===============");
        for( Object e : array ){
            System.out.print(e);
        }
        System.out.println();

    }

    /**
     * Created by monsio on 3/23/16.
     */
    public static class DataTest implements Handle {

        int indexTas;
        String info;
        protected int priority;

        public DataTest(int priority, String info) {
            this.setPriority(priority);
            this.setInfo(info);
        }

        public int getIndexTas() {
            return indexTas;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        @Override
        public void setIndexTas(int tasId) {
                indexTas = tasId;
        }

        @Override
        public int getPriority() {
            return this.priority;
        }

        @Override
        public void setPriority(int priority) {
                this.priority = priority;
        }

        @Override
        public String toString() {
            return info+" ";
        }
    }
}
