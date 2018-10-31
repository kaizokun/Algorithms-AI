package mylib.graph;

import mylib.ensemble.EnsembleDisjointTree;
import mylib.tas.TasMin;

import java.util.*;

/**
 * Created by monsio on 05/07/16.
 */
public class Graph implements ItGraph{


    private List<Sommet> listeSommet = new ArrayList<>();
    private LinkedList<Sommet> parcourTopologique;
    private boolean circuit = false;
    private int sizeArc = 0;

    public Graph() {}

    public void init(){

        this.listeSommet = new ArrayList<>();

        for(Sommet s : this.listeSommet){
            s.init();
            this.circuit = false;
        }

        this.parcourTopologique = new LinkedList<>();
    }

    private void initSommet(Sommet sommet){

        if( sommet.getIndexGraph() == -1 ){
            sommet.setIndexGraph(this.listeSommet.size());
            this.listeSommet.add(sommet);
        }
    }

    @Override
    public void ajoutArc( Sommet source, Sommet destination, int poids, boolean oriente) {

        this.initSommet(source);
        this.initSommet(destination);

        source.ajoutArc(new Arc(source,destination,poids));
        if(!oriente)
            destination.ajoutArc(new Arc(destination,source,poids));

        this.sizeArc++;
    }

    @Override
    public void ajoutArc( Sommet source, Sommet destination, boolean oriente ) {
        ajoutArc(source,destination,0,oriente);
    }

    @Override
    public ItGraph getTranspose(){

        ItGraph t = new Graph();

        for(Sommet source : this.listeSommet)
            for( Arc arc :  source.getArcs())
                t.ajoutArc(arc.getDestination(),source,true);

        return t;
    }

    public void voirArcsSommets(List<Sommet> liste){
        for(Sommet source : liste){
            System.out.println("\n"+source);
            for(Arc arc : source.getArcs()){
                System.out.print(" |_____ "+arc.getDestination());
            }
            System.out.println();
        }
    }

    public void voirArcsSommets(){
        this.voirArcsSommets(this.listeSommet);
    }

    /*================================================================================ PARCOUR EN LARGEUR =============*/

    @Override
    public void parcourLargeur(){

        this.init();

        for( Sommet sommet : listeSommet)
        {

            if(!sommet.isVisite()){

                LinkedList<Sommet> liste = new LinkedList<>();
                sommet.setProfondeur(0);
                liste.add(sommet);

                while(!liste.isEmpty()){

                    Sommet sommet1 = liste.removeFirst();

                    List<Arc> arcs = sommet1.getArcs();
                    sommet1.setVisite(true);

                    for(Arc arc : arcs){
                        if(!arc.getDestination().isVisite()) {
                            arc.getDestination().setProfondeur(sommet1.getProfondeur() + 1);
                            liste.add(arc.getDestination());
                        }
                    }

                }

            }

        }


    }


    /*================================================================================ PONT =============*/


    public void afficherPonts(){

        for( Sommet s : listeSommet ){

            for(Arc a : s.getArcs()){
                if(a.getEstPont()){
                    System.out.println(a.getSource()+" -> "+a.getDestination());
                }
            }

        }

    }


    public void trouverPontsv1(){


                LinkedList<Sommet> pile = new LinkedList<>();
                Sommet sommet = listeSommet.get(0);
                pile.add(sommet);

                while(!pile.isEmpty()) {

                    Sommet top = pile.getLast();

                    if( !top.isVisite() ) {
                        System.out.println("DEBUT "+top);
                        top.setVisite(true);

                        for (int s = 0; s < top.getArcs().size(); s++) {

                            Sommet destination = top.getArcs().get(s).getDestination();

                            if( !top.aArcPrecedent() || !destination.equals(top.getArcPrecedent().getSource())) {

                                if (!destination.isVisite()) {
                                    destination.setArcPrecedent(top.getArcs().get(s));
                                    pile.add(destination);
                                } else {
                                    top.getArcs().get(s).estPont(false);

                                    Sommet courant = top;

                                    while( !courant.equals(destination))
                                    {
                                        courant.getArcPrecedent().estPont(false);
                                        courant = courant.getArcPrecedent().getSource();
                                    }


                                }
                            }
                        }

                    }else{
                        System.out.println("FIN "+top+" "+pile.size());
                        Sommet s = pile.removeLast();

                        if( s.aArcPrecedent() )
                            s.removeArcPrecedent();

                        s.setVisite(false);
                    }

                }



    }


    private void trouverPontsv2(){

                LinkedList<Sommet> pile = new LinkedList<>();
                Sommet sommet = listeSommet.get(0);
                pile.add(sommet);
                sommet.addProfondeur(0);

                while(!pile.isEmpty()) {

                    Sommet top = pile.getLast();

                    String id =  exercicesFranceIoi.util.Util.getIdent(top.getLastProfondeur());

                    if( !top.isVisite() ) {

                        System.out.println(id+"DEBUT "+top);

                        top.setVisite(true);

                        top.setMin(top.getLastProfondeur());

                        for (int s = 0; s < top.getArcs().size(); s++) {

                            Sommet destination = top.getArcs().get(s).getDestination();

                            if( !top.aArcPrecedent() ||  !destination.equals(top.getArcPrecedent().getSource())) {
                                //System.out.println(id+top.getArcs().get(s));
                                if (!destination.isVisite()) {
                                    //System.out.println(id+"NON VISITE "+destination+" d = "+(top.getLastProfondeur()+1));
                                    pile.add(destination);
                                    destination.setArcPrecedent(top.getArcs().get(s));
                                    destination.addProfondeur(top.getLastProfondeur() + 1);
                                    //destination.setProfondeur(top.getProfondeur()+1);
                                }else{
                                    //System.out.println(id+"VISITE");
                                    top.getArcs().get(s).estPont(false);
                                    top.setMin(Math.min(top.getMin(), destination.getLastProfondeur()));
                                }
                            }
                        }

                    }else{

                        System.out.println(id+"FIN "+top+" "+pile.size());

                        Sommet s = pile.removeLast();

                        if( s.aArcPrecedent() && top.getMin() <= s.getArcPrecedent().getSource().getLastProfondeur()){
                            s.getArcPrecedent().estPont(false);
                            s.getArcPrecedent().getSource().setMin(
                                    Math.min(top.getMin(),s.getArcPrecedent().getSource().getMin())
                            );
                            s.removeArcPrecedent();
                            s.removeLastProfondeur();
                        }

                        s.setVisite(false);

                        System.out.println(id+pile.size());

                    }

                }

    }

    /*================================================================================ PARCOUR EN PROFONDEUR =============*/


    @Override
    public void parcourProfondeur(){
        parcourProfondeur(this.listeSommet);
    }

/**
 * Parcour en profondeur itératif
 * */
    private void parcourProfondeur(List<Sommet> listeSommet){

        this.parcourTopologique = new LinkedList<>();

        int date = 0;

        for( Sommet sommet : listeSommet) {

            if(!sommet.isVisite()){

                LinkedList<Sommet> pile = new LinkedList<>();
                pile.add(sommet);

                while(!pile.isEmpty()) {

                    Sommet top = pile.getLast();

                    if( !top.isVisite() ) {
                        top.setVisite(true);
                        top.setDebut(++date);

                        for (int s = 0; s < top.getArcs().size(); s++) {

                            Sommet destination = top.getArcs().get(s).getDestination();

                            if (!destination.isVisite()) {
                                pile.add(destination);
                            }else{
                                circuit = true;
                            }
                        }

                    }else{

                        top.setFin(++date);
                        Sommet s = pile.removeLast();
                        this.parcourTopologique.addFirst(s);
                    }

                }
            }
        }

    }


    /**
     * Parcour en profondeur récursif
     * */

    public void parcourProfondeurRec(){

        this.init();

        int date = 0;
        for( int i = 1 ; i < listeSommet.size() ; i ++ )
        {
            Sommet sommet = listeSommet.get(i);
            if(!sommet.isVisite()){
                date = parcourProfondeurRec(sommet, date + 1);
            }
        }

    }

    public int parcourProfondeurRec(Sommet sommet, int date){

        sommet.setVisite(true);
        sommet.setDebut(date);

        for(Arc arc :  sommet.getArcs()){
            if(!arc.getDestination().isVisite()){
                date = parcourProfondeurRec(arc.getDestination(), date + 1);
            }
        }

        sommet.setFin(date + 1);
        return date + 1;

    }

    /*==========================COMPOSANTES FORTEMENT CONNEXES=========================*/

    public void composantesFortementConnexes(){

        this.init();
        //Création de la transposée
        Graph transpose = (Graph) this.getTranspose();
        //parcour en profondeur du graphe courant
        this.parcourProfondeur();
        //récuperation de la liste des sommets de la transposé
        List<Sommet> listeSommetTranspose = transpose.getListeSommet();
        //initialise les sommet de la transposé avec les dates de debut et de fin du graphe courant
        for(Sommet s : this.listeSommet){
            listeSommetTranspose.get(s.getIndexGraph()).setDebut(s.getDebut());
            listeSommetTranspose.get(s.getIndexGraph()).setFin(s.getFin());
        }

        //creer une copie de la liste d'adjacence de la transposee
        List<Sommet> listeSommetTransposeCpy = new ArrayList<>(listeSommetTranspose);
        //trie des sommets des la transposé en fonction de la date de fin
        Collections.sort(listeSommetTransposeCpy, new Comparator<Sommet>() {
            @Override
            public int compare(Sommet s1, Sommet s2) {
               return s2.getFin() - s1.getFin();
            }
        });
        //parcour de la liste des sommet de la transposé par ordre de date de fin decroissant
        transpose.parcourProfondeur(listeSommetTransposeCpy);

        this.voirArcsSommets();

        System.out.println("\n=====================Transposée====================\n");

        transpose.voirArcsSommets();

    }

    /*=====================================ARBRE COUVRANT MINIMUM================================*/

    /*==============KRUSKAL*/
    public void acmKruskal(){

        List<Arc> arcsACM = new LinkedList<>();
        ArrayList<Arc> arcs = new ArrayList(this.sizeArc);
        //EnsembleDisjointList ensembleDisjoint = new EnsembleDisjointList();
        EnsembleDisjointTree ensembleDisjoint = new EnsembleDisjointTree();

        for(Sommet s : this.listeSommet){
            ensembleDisjoint.creerEnsemble(s);
            arcs.addAll(s.getArcs());
        }

        Collections.sort(arcs);

        for( Arc arc : arcs ){
            if( !ensembleDisjoint.memeEnsemble(arc.getSource(),arc.getDestination())){
                arcsACM.add(arc);
                ensembleDisjoint.union(arc.getSource(), arc.getDestination());
            }
        }

        for(Arc arc : arcsACM)
            System.out.println(arc);

    }

    /*============PRIM*/
    public void acmPrim(){

        for(Sommet s : listeSommet){
            s.setPriority(Integer.MAX_VALUE);
            s.setPrevious(null);
            s.setHandled(true);
        }

        listeSommet.get(0).setPriority(0);

        TasMin<Sommet> tasMin = new TasMin(new ArrayList(listeSommet));
        tasMin.init();

        try {

            while(!tasMin.isEmpty()){

                Sommet s = tasMin.extractMin();
                s.setHandled(false);

                System.out.println(s.getPrevious()+" -> "+s);

                for(Arc arc : s.getArcs()){
                    Sommet dest = arc.getDestination();
                    if( dest.isHandled() && arc.getPoids() < dest.getPriority() ){
                        dest.setPrevious(s);
                        tasMin.upKey(dest.getIndexTas(),arc.getPoids());
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*===================================== PLUS COUR CHEMIN ORIGINE UNIQUE ================================*/

    private void sourceUniqueInitialisation( Sommet s ){

        for(Sommet so : this.listeSommet){
            so.setProfondeur(Integer.MAX_VALUE);
            so.setPrevious(null);
        }

        s.setProfondeur(0);
    }


    private int calculProfondeur(int prof, int poids){
        return prof == Integer.MAX_VALUE ? Integer.MAX_VALUE : prof + poids ;
    }

    private void relacher( Arc arc ){

        int profPlusPoids = calculProfondeur(arc.getSource().getProfondeur(),arc.getPoids());

        if(arc.getDestination().getProfondeur() > profPlusPoids ){
            arc.getDestination().setProfondeur(profPlusPoids);
            arc.getDestination().setPrevious(arc.getSource());
        }
    }

        /*--------------BELLMAN FORD----------------*/

    /**
     * - fonctionne avec arcs négatifs
     * - determine si le graphe comporte des circuits négatifs
     * - O(SA)
     * */

    public boolean bellmanFord(Sommet s){

        this.sourceUniqueInitialisation(s);

        for( int i = 0 ; i <  this.listeSommet.size() - 1 ; i ++ )
            for( Sommet so :  this.listeSommet)
                for( Arc a : so.getArcs())
                    this.relacher(a);

        for( Sommet so :  this.listeSommet)
            for( Arc a : so.getArcs())
               if(a.getDestination().getProfondeur() > calculProfondeur(a.getSource().getProfondeur(),a.getPoids()))
                   return false;

        return true;
    }


    /*-------------- PLUS COUR CHEMIN GRAPHE ACYCLIQUE ORIENTE----------------*/

    /**
     * O(S+A)
     * */
    public void plucCourtsCheminsGSS(Sommet s){

        this.parcourProfondeur();

        this.sourceUniqueInitialisation(s);

        for( Sommet so : this.parcourTopologique)
            for (Arc a : so.getArcs())
                relacher(a);

    }


    /*------------------------DIJKSTRA-------------------------------*/


    /**
     * Graphe avec cycle autorisé mais arc de poids stictement positifs ou nul
     * tas Min : O( (S+A) log S )
     * */

    public void dijkstra(Sommet s){

        for(Sommet so : this.listeSommet){
            so.setPriority(Integer.MAX_VALUE);
            so.setPrevious(null);
            so.setHandled(true);
        }

        s.setPriority(0);

        TasMin<Sommet> tasMin = new TasMin(new ArrayList(this.listeSommet));
        tasMin.init();


            while( !tasMin.isEmpty() ){

                Sommet u = null;

                try {
                    u = tasMin.extractMin();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                u.setHandled(false);

                for( Arc arc : u.getArcs() ){
                    //si la destination est toujours dans la file de priorité
                    if( arc.getDestination().isHandled() ){
                        //le relachement modifie la profondeur pour la diminuer si necessaire
                        int profPlusPoids = calculProfondeur(u.getPriority(), arc.getPoids());

                        //ici la procedure de relachement differe un peu dans le sens ou il faut modifier la priorité (ou profondeur) à partir du TasMin
                        if(arc.getDestination().getPriority() > profPlusPoids ){
                           // System.out.println("Relachement");
                            try {
                                tasMin.upKey(arc.getDestination().getIndexTas(), profPlusPoids);
                                arc.getDestination().setPrevious(u);
                            }catch (Exception e ){}

                        }

                    }
                }

            }



    }

    /*---------------------------------------------------------------*/


    public void afficherChemin( Sommet s ){

        if( s.getPrevious() != null )
            afficherChemin(s.getPrevious());

        System.out.print(" <- "+s);
    }


    /*===================================== PLUS COUR CHEMIN TOUTE PAIRE DE SOMMET ================================*/

    private int D[][],P[][], W[][];

    public void plusCourtCheminTousCoupleAccelere() {

        int s = this.listeSommet.size();

         D = new int[s][s];
         P = new int[s][s];

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++) {
                D[i][j] = i==j ? 0 : Integer.MAX_VALUE;
                P[i][j] = -1;
            }

        for (Sommet so : this.listeSommet)
            for (Arc a : so.getArcs())
                D[a.getSource().getIndexGraph()][a.getDestination().getIndexGraph()] = a.getPoids();

        int m = 1;

        while (m < s - 1) {
            D = extensionPlusCourtsChemin(D,D,P, s);
            m *= 2;
        }

        voirmatrice(D,m-1,"Matrice ");

        voirmatriceP(P);

    }

    /*
    * on a obtenu les plus court chemin pour les longeurs puissance de deux.
    * si un chemin entre deux sommets est de longueur 5 on obtient un sommet intermediaire
     * qui decoupe en deux chemins un de 2 et un de 3
     *      le chemin de 3 lui même decoupe en un chemins de longueurs 2 et 1
     *
     *      A VERIFIER DAVANTAGE !!!!
    * */
    public void voirCheminAccelere(Sommet a, Sommet b){

        int id = P[a.getIndexGraph()][b.getIndexGraph()];

        if( id == -1 ) {
            System.out.println(a + " -> " + b);
            return;
        }

        Sommet i = listeSommet.get(id);

        voirCheminAccelere(a, i);
        voirCheminAccelere(i,b);

    }

    public void plusCourtCheminTousCoupleRalenti() {


        int s = this.listeSommet.size();

         D = new int[s][s];
         P = new int[s][s];
         W = new int[s][s];

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++) {
                D[i][j] = i==j ? 0 : Integer.MAX_VALUE;
            }

        for (Sommet so : this.listeSommet)
            for (Arc a : so.getArcs())
                D[a.getSource().getIndexGraph()][a.getDestination().getIndexGraph()] = a.getPoids();

        for (int i = 0; i < s; i++)
            for (int j = 0; j < s; j++) {
                W[i][j] = D[i][j];
                P[i][j] = -1;
            }

        int m = 2;
        for(  ; m  < s ; m ++){
            D = extensionPlusCourtsChemin(D,W,P,s);
        }

        voirmatrice(D,m-1,"Matrice ");

        voirmatriceP(P);

    }

    private int[][] extensionPlusCourtsChemin(int[][] D, int[][] W, int[][] P,int s) {

        int[][] D2 = new int[s][s];

        for( int i = 0 ; i < s ; i ++) {
            for (int j = 0; j < s; j++) {

                D2[i][j] = D[i][j];

                for (int k = 0; k < s; k++) {

                    int SUM = (D[i][k] == Integer.MAX_VALUE || W[k][j] == Integer.MAX_VALUE ) ? Integer.MAX_VALUE : D[i][k] + W[k][j];

                    if( D2[i][j] > SUM ){
                        D2[i][j] = SUM;

                        if(P != null)//modifie le predecesseur si on obtiens un chemin plus court
                            P[i][j] = k;
                    }
                }
            }
        }

        return D2;

    }
    
    private void voirmatrice(int[][]M, int matrice,String text){

        System.out.println("\n=============== "+text+" "+matrice+" ============\n");

        int s = this.listeSommet.size();

        for(Sommet som : listeSommet)
            System.out.printf("[ %2c ]", som.getValue());

        System.out.println("\n");

        for(int i = 0 ; i < s ; i ++){
            for(int j = 0 ; j < s ; j ++){

                String val = M[i][j] == Integer.MAX_VALUE ? "+" : Integer.toString(M[i][j]);

                System.out.printf("[ %2s ]",val);
            }

            System.out.println();
        }

    }


    private void voirmatriceP(int[][]M){

        System.out.println("\n=============== MATRICE PREDECESSEUR ============\n");

        int s = this.listeSommet.size();

        for(Sommet som : listeSommet)
            System.out.printf("[ %4c ]", som.getValue());

        System.out.println("\n");

        for(int i = 0 ; i < s ; i ++){
            for(int j = 0 ; j < s ; j ++) {
                String val = (M[i][j] == -1) ? "___" : listeSommet.get(M[i][j]).toString();
                System.out.printf("[ %4s ]", val);
            }
            System.out.println();
        }

    }

    /*=================GETTER SETTER===============================*/


    public List<Sommet> getListeSommet(){
        return listeSommet;
    }

    public boolean isCircuit() {
        return circuit;
    }

    public LinkedList<Sommet> getParcourTopologique() {

        if( !this.isCircuit() )
            return parcourTopologique;

        return new LinkedList<>();
    }

   /*================= MAIN ===============================*/

    public static void main(String[] args) {

        //testplusCourCheminOrigineunique();
        //testplusCourCheminOrigineunique2();
        //testDijkstra();
        testPCCTC();

    }

    public static void testPCCTC(){

        Graph graph = new Graph();

        Sommet a = new Sommet('A'),
                b = new Sommet('B'),
                c = new Sommet('C'),
                d = new Sommet('D'),
                e = new Sommet('E');

        graph.ajoutArc(a,b,3,true);
        graph.ajoutArc(a,c,8,true);

        graph.ajoutArc(b,d,1,true);
        graph.ajoutArc(b,e,7,true);
        graph.ajoutArc(a,e,-4,true);

        graph.ajoutArc(c,b,4,true);

        graph.ajoutArc(d,a,2,true);
        graph.ajoutArc(d,c,-5,true);

        graph.ajoutArc(e,d,6,true);
        graph.ajoutArc(e,c,-4,true);

        //graph.plusCourtCheminTousCoupleRalenti();
        graph.plusCourtCheminTousCoupleAccelere();

        System.out.println();
        System.out.println();

        graph.voirCheminAccelere(e,a);

    }

    public static void testDijkstra(){

        Graph graph =  new Graph();

        Sommet s = new Sommet("s"),
                t = new Sommet("t"),
                x = new Sommet("x"),
                y = new Sommet("y"),
                z = new Sommet("z");

        graph.ajoutArc(s,t,10,true);
        graph.ajoutArc(s,y,5,true);

        graph.ajoutArc(t,x,1,true);
        graph.ajoutArc(t,y,2,true);

        graph.ajoutArc(y,t,3,true);
        graph.ajoutArc(y,x,9,true);
        graph.ajoutArc(y,z,2,true);

        graph.ajoutArc(x,z,4,true);

        graph.ajoutArc(z,x,6,true);
        graph.ajoutArc(z,s,7,true);

        graph.dijkstra(s);

        graph.afficherChemin(z);

    }


    public static void testplusCourCheminOrigineunique(){

        Graph graph =  new Graph();

        Sommet s = new Sommet("s"),
                t = new Sommet("t"),
                y = new Sommet("y"),
                x = new Sommet("x"),
                z = new Sommet("z");

        graph.ajoutArc(s,t,6,true);
        graph.ajoutArc(s,y,7,true);
        graph.ajoutArc(t,x,5,true);
        graph.ajoutArc(t,z,-4,true);
        graph.ajoutArc(t,y,8,true);
        graph.ajoutArc(y,x,-3,true);
        graph.ajoutArc(y,z,9,true);
        graph.ajoutArc(x,t,-2,true);
        graph.ajoutArc(z,s,2,true);
        graph.ajoutArc(z,x,7,true);

        graph.bellmanFord(s);

       //
        // System.out.println(s.getPrevious());
        graph.afficherChemin(z);

    }

    public static void testplusCourCheminOrigineunique2(){

        Graph graph =  new Graph();

        Sommet s = new Sommet("s"),
                r = new Sommet("r"),
                t = new Sommet("t"),
                y = new Sommet("y"),
                x = new Sommet("x"),
                z = new Sommet("z");

        graph.ajoutArc(s,t,2,true);
        graph.ajoutArc(s,x,6,true);

        graph.ajoutArc(r,s,5,true);
        graph.ajoutArc(r,t,3,true);

        graph.ajoutArc(t,x,7,true);
        graph.ajoutArc(t,y,4,true);
        graph.ajoutArc(t,z,2,true);

        graph.ajoutArc(x,y,-1,true);
        graph.ajoutArc(x,z,1,true);

        graph.ajoutArc(y,z,-2,true);

        graph.plucCourtsCheminsGSS(s);

        graph.afficherChemin(z);

    }


    public static void testArbreCouvrantMinimum(){

        Graph graph =  new Graph();
        Graph graph2 =  new Graph();

        /*tableau de sommet initialisé valeur [0 - (size - 1) ]*/

        int sizeS = 9;
        Sommet[] s = new Sommet[sizeS];
        for( int i = 0 ; i < sizeS ; i ++)
            s[i] = new Sommet(i);

        /*tableau d'arcs avec les index des sommets de source et destination correspondant au tableau précedent*/
        Sommet arcs[][] = new Sommet[][]{{s[0],s[1]},{s[0],s[3]},{s[0],s[4]},
                {s[0],s[6]},{s[1],s[4]},{s[1],s[2]},{s[2],s[5]},{s[2],s[7]},
                {s[2],s[8]},{s[3],s[6]},{s[4],s[7]},{s[5],s[8]},{s[6],s[7]},
                {s[7],s[8]}};

        int poids[] = new int[]{1,8,7,11,6,2,10,4,14,4,2,9,9,7};


        for(int i = 0 ; i < arcs.length ; i ++){
            Sommet arc[] = arcs[i];
            graph2.ajoutArc(arc[0], arc[1], poids[i], false);
            graph.ajoutArc(arc[0], arc[1], poids[i], false);
        }

        graph.acmKruskal();
        System.out.println("\n=========PRIM=============\n");
        graph2.acmPrim();

    }



}

