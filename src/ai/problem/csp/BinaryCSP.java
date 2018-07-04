package ai.problem.csp;

import ai.explorationStrategy.csp.*;
import ai.games.eightqueen.CanNotAttackConstraint;
import ai.problem.csp.constraint.*;
import geometry.Point;
import util.Util;

import java.util.*;

public class BinaryCSP<T> {

    protected ArrayList<CSPvariable> variables = new ArrayList<>();
    protected ArrayList<ArrayList<T>> domains = new ArrayList<>();
    protected Hashtable<CSPvariable,Hashtable<CSPvariable,BinaryConstraint>> constraints = new Hashtable<>();
    protected List<HashSet<CSPvariable>> uselessAssignations = new LinkedList<>();

    public BinaryCSP() { }

    public BinaryCSP(ArrayList<CSPvariable> variables, ArrayList<ArrayList<T>> domains, Hashtable<CSPvariable, Hashtable<CSPvariable, BinaryConstraint>> constraints) {
        this.variables = variables;
        this.domains = domains;
        this.constraints = constraints;
    }

    public CSPvariable createVariable(String label, ArrayList<T> domain){

        CSPvariable newVar = new CSPvariable(label,variables.size());
        variables.add(newVar);
        domains.add(domain);
        return newVar;

    }

    public void addConstraint(BinaryConstraint binaryConstraint){

        Hashtable<CSPvariable, BinaryConstraint> v1Constraints = constraints.get(binaryConstraint.getXi());

        if(v1Constraints == null){
            v1Constraints = new Hashtable();
            constraints.put(binaryConstraint.getXi(), v1Constraints);
        }

        v1Constraints.put(binaryConstraint.getXj(), binaryConstraint);

    }


    public List<BinaryConstraint> getConstraints(){

        LinkedList<BinaryConstraint> constraints = new LinkedList<>();

        for(Hashtable<CSPvariable,BinaryConstraint> v1Constraints : this.constraints.values()){
            for(BinaryConstraint constraint : v1Constraints.values()){
                constraints.add(constraint);
            }
        }

        return constraints;
    }

    public Collection<BinaryConstraint> getConstraints(CSPvariable csPvariable){
        if(this.constraints.get(csPvariable) != null && !this.constraints.get(csPvariable).isEmpty())
            return this.constraints.get(csPvariable).values();
        return new LinkedList();
    }

    public ArrayList<T> getDomain(CSPvariable csPvariable){
        return domains.get(csPvariable.getIndex());
    }

    public ArrayList<T> getDomain(int iVar){
        return domains.get(iVar);
    }

    public ArrayList<CSPvariable> getVariables() {
        return variables;
    }

    public BinaryConstraint getConstraint(CSPvariable Xi, CSPvariable Xj){
        return this.constraints.get(Xi).get(Xj);
    }

    public boolean isEmptyDomain(CSPvariable csPvariable) {
        return this.domains.get(csPvariable.getIndex()).isEmpty();
    }



    public List<List<BinaryConstraint>> findWayConstraints(BinaryConstraint binaryConstraint){
        //System.out.println("findWayConstraints");
        List<List<BinaryConstraint>> binaryConstraintPairList = new LinkedList<>();

        for( CSPvariable csPvariable : this.constraints.keySet()){
            //System.out.println(csPvariable);
            Hashtable<CSPvariable, BinaryConstraint> wayConstraints = constraints.get(csPvariable);
            BinaryConstraint c1 = wayConstraints.get(binaryConstraint.getXi());
            BinaryConstraint c2 = wayConstraints.get(binaryConstraint.getXj());
            if(c1 != null && c2 != null){
                //System.out.println("ADD "+c1+" "+c2);
                LinkedList<BinaryConstraint> binaryConstraintPair = new LinkedList<>();
                binaryConstraintPair.add(c1);
                binaryConstraintPair.add(c2);
                binaryConstraintPairList.add(binaryConstraintPair);
            }

        }

        //System.out.println(binaryConstraintPairList.get(0).size());

        return binaryConstraintPairList;
    }

    public static ArrayList<Double> getDoubleList(int max){

        ArrayList<Double> doubles = new ArrayList<>();

        for( int i = 0 ; i <= max ; i ++){
            doubles.add(new Double(i));
        }

        return doubles;
    }

    public int varCount(){
        return this.variables.size();
    }

    public CSPvariable getVariable(int i){
        return this.variables.get(i);
    }

    public BinaryCSP<?> clone(){

        ArrayList<ArrayList<T>> domainsCopy = new ArrayList<>();

        for(ArrayList<T> domain : this.domains){
            domainsCopy.add(new ArrayList<T>(domain));
        }

        return new BinaryCSP<>(this.variables, domainsCopy,this.constraints);
    }

    public BinaryCSP<T> fullClone(){

        ArrayList<ArrayList<T>> domainsCopy = new ArrayList<>();
        //copie des domaines
        for(ArrayList<T> domain : this.domains){
            domainsCopy.add(new ArrayList<T>(domain));
        }

        //copie des variables
        ArrayList<CSPvariable> varsCopy = new ArrayList<>();

        for(CSPvariable var : this.variables){
            varsCopy.add(new CSPvariable(var));
        }

        Hashtable<CSPvariable,Hashtable<CSPvariable,BinaryConstraint>> varsConstraintCopy = new Hashtable<>();
        //pour chaque copy de variable
        for(CSPvariable var : varsCopy){
            //créer uen table de hashage pour stocker les copy de contraintes de la variable
            Hashtable<CSPvariable, BinaryConstraint> constraints = new Hashtable<>();
            //récuperer les contraintes de la variable ( hashcode identique )
            Collection<BinaryConstraint> varConstraint = this.constraints.get(var).values();
            //si des containtes existe pour cette variable
            if(varConstraint != null) {
                //pour chaque contrainte
                for (BinaryConstraint constraint : varConstraint) {
                    //recuperer dans les copies de variables la variable destination
                    CSPvariable xjClone = varsCopy.get(constraint.getXj().getIndex());
                    //créer un nouvelle contrainte binaire et l'ajouter dans la table
                    constraints.put(xjClone, constraint.getNewBinaryConstraint(var, xjClone));
                }

                varsConstraintCopy.put(var,constraints);

            }

        }

        return new BinaryCSP<>(varsCopy, domainsCopy, varsConstraintCopy);
    }


    public boolean assignationValid() {

        //pour chaque variable assigné
        for( CSPvariable var : variables ){

            if( var.getValue() != null ){

                //pour chaque contrainte dont la variable est la statement
                //et donc la variable de destination est assigné
                Hashtable<CSPvariable,BinaryConstraint> varConstraints = this.constraints.get(var);
                if(varConstraints != null ) {
                    for (BinaryConstraint constraint : varConstraints.values()) {

                        if (constraint.getXj().getValue() != null) {
                            //si une conrtainte n'est pas satisfaite l'assignation
                            //est mauvaise on retourne faux
                            if (!constraint.satisfied()) {
                                return false;
                            }

                        }

                    }
                }

            }

        }

        return true;
    }

    public ArrayList<ArrayList<T>> getDomains() {
        return domains;
    }

    public void addUselessAssignation(HashSet<CSPvariable> uselessAssignation){

        HashSet<CSPvariable> uselessAssignationCp = new LinkedHashSet<>();

        for(CSPvariable var : uselessAssignation){
            uselessAssignationCp.add(new CSPvariable(var));
        }

        this.uselessAssignations.add(uselessAssignationCp);
    }

    public boolean assignationUsefull() {

        //pour chaque assignation partielle inutile
        for(HashSet<CSPvariable> uselessAssign : this.uselessAssignations){

            //on considere au prealable que l'assignation est inutile
            boolean useless = true;
            //pour chaque variable et valeur de l'assignation
            for(CSPvariable var : uselessAssign){
                //on recupere la valeur de la variable pour l'assignation courante
                Object varVal = this.variables.get(var.getIndex()).getValue();
                //si cette valeur est nulle ou qu'elle est differente de la valeur inutile
                if( varVal == null || !varVal.equals(var.getValue())){
                    //on considere qu'elle est potentiellement utile
                    useless = false;
                    break;
                }
            }

            //si apres avoir conparé l'assignation actuelle à une assignation useless sauvegardé
            //la correspondance est complete on retourne faux, l'assignation sera ignorée
            if(useless) {
                return false;
            }

        }

        //si aucune assignation useless sauvegardé ne correspond à l'assignation courante on peut la tester
        return true;

    }

    public void initRandomAssignation() {
        for(CSPvariable var : this.variables){
            int iVal = Util.rdnInt(0, this.domains.get(var.getIndex()).size() - 1);
            var.setValue(this.domains.get(var.getIndex()).get(iVal));
        }
    }

    public CSPvariable getRandomConflictualVariable() {

        List<CSPvariable> vars = new ArrayList<>();
        //pour chaque variable
        for(CSPvariable var : this.variables){
            //on recupere toutes les contraintes
            for(BinaryConstraint constraint : this.getConstraints(var)){
                //si une contrainte n'est pas satisfaite on l'ajoute dans la liste
                if(!constraint.satisfied()) {
                    vars.add(var);
                    break;
                }

            }

        }

        return vars.get(Util.rdnInt(0,vars.size() - 1));
    }

    public CSPvariable getMoreConflictualVariable() {

        int bestTotalConficts = 0;
        Hashtable<Integer,List<CSPvariable>> worseVars = new Hashtable<>();

        //pour chaque variable
        for(CSPvariable var : this.variables){
            //on recupere toutes les contraintes

            int totalConflicts = this.totalConflicts(var);

            if(totalConflicts >= bestTotalConficts){
                bestTotalConficts = totalConflicts;

                List<CSPvariable> values = worseVars.get(totalConflicts);
                if(values == null){
                    values = new ArrayList<>();
                    worseVars.put(totalConflicts,values);
                }

                values.add(var);
            }

        }

        //récuperation d'une des variables entrant le plus en conflit
        List<CSPvariable> vars = worseVars.get(bestTotalConficts);

        //trie des variables les plus conflictuelles par taille du domaine
        Collections.sort(vars, new Comparator<CSPvariable>() {
            @Override
            public int compare(CSPvariable o1, CSPvariable o2) {
                return Integer.compare(domains.get(o1.getIndex()).size(), domains.get(o2.getIndex()).size()) ;
            }
        });

        //retourne celle ayant le moins de valeur possible
        return vars.get(0);
        //retourne une varibael au hazard
        //return vars.get(array.Util.rdnInt(0,vars.size() - 1));
    }



    private int totalConflicts(CSPvariable var){

        int totalConflicts = 0;
        for(BinaryConstraint constraint : this.getConstraints(var)){
            //si une contrainte n'est pas satisfaite on compte un conflit de plus
            if(!constraint.satisfied()) {
                totalConflicts ++;
            }

        }

        return totalConflicts;
    }

    public void setLessConflictualValue(CSPvariable var) {


        //Object bestValue;
        int bestTotalConflicts = Integer.MAX_VALUE;
        //contient les valeurs par nombre de conflicts rencontrées
        Hashtable<Integer,List<Object>> totalConflictsValues = new Hashtable<>();
        //pour chaque valeur du domaine de la variable
        for( Object value : this.domains.get(var.getIndex())){
            //attribuer la valeur à la variable
            var.setValue(value);
            //compter le nombre de conflicts avec les autres variables pour cette valeur
            int totalConflicts = this.totalConflicts(var);
            //si on obtient moins de conflicts ou le même nombre
            if(totalConflicts <= bestTotalConflicts){
                //sauvegarder le minimum de conflicts
                bestTotalConflicts = totalConflicts;
                //ajouter la valeur dans la liste associé aux nombre de conflicts
                List<Object> values = totalConflictsValues.get(totalConflicts);
                if(values == null){
                    values = new ArrayList<>();
                    totalConflictsValues.put(totalConflicts,values);
                }

                values.add(value);
            }

        }
        //choisir une valeur au hazard parmis les meilleurs
        List<Object> bestValues = totalConflictsValues.get(bestTotalConflicts);
        int rdmId = Util.rdnInt(0, bestValues.size() - 1);
        //l'assigner à la variable
        var.setValue(bestValues.get(rdmId));

    }






    public static void main(String[] args) {

        int eightQueenSize = 32;

        BinaryCSP<Point> csp = testEightQueen(eightQueenSize);
        BinaryCSP<Point> cspClone = csp.fullClone();
/*
        BinaryCSP<Point> csp = testMapAustralia();
        BinaryCSP<Point> cspClone = csp.fullClone();
        */
        //PC2 pc2 = new PC2();

        //System.out.println(pc2.isCoherent(csp));

        BackTrackingExploration exploration = new BackTrackingExploration();
        long t1;

        try {

            t1 = System.currentTimeMillis();
            exploration.backTrack(csp,0);

            System.out.println("TEMPS : "+(System.currentTimeMillis() - t1)+" millisecondes ");
/*
            for(CSPvariable var : csp.variables)
                System.out.println(var);
*/
            showEightQueen(csp,eightQueenSize);

        } catch (BackTrackingExploration.ExplorationCSPFailedException e) {
            //e.printStackTrace();
            System.out.println("PAS DE SOLUTION !!!!!!!!!!");
        }

        System.out.println(exploration.getCptNodesDeploied());

        System.out.println();
        System.out.println();
        System.out.println("MIN CONFLICTS");

        MinConflicts minConflicts = new MinConflicts();

        try {

            t1 = System.currentTimeMillis();
            minConflicts.explore(cspClone, 1000);
            System.out.println("TEMPS : "+(System.currentTimeMillis() - t1)+" millisecondes ");

            showEightQueen(cspClone,eightQueenSize);
        } catch (BackTrackingExploration.ExplorationCSPFailedException e) {
            e.printStackTrace();
        }


    }

    private static void showEightQueen(BinaryCSP<Point> csp, int totalQueen) {

        String plateau[][] = new String[totalQueen][totalQueen];

        for( int x = 0 ; x < totalQueen ; x ++)
            for( int y = 0 ; y < totalQueen ; y ++)
                plateau[x][y] = "[ ]";

        for(CSPvariable var : csp.variables){

            Point p = (Point) var.getValue();
            plateau[(int)p.getY()][(int)p.getX()] = "[Q]";
        }

        for( int y = 0 ; y < totalQueen ; y ++) {
            for (int x = 0; x < totalQueen; x++) {
                System.out.print(plateau[y][x]);
            }
            System.out.println();
        }

    }

    private static BinaryCSP testEightQueen(int totalQueen){

        BinaryCSP<Point> csp = new BinaryCSP();

        for(int x = 0 ;  x < totalQueen ; x ++){

            ArrayList<Point> domain = new ArrayList<>();

            for( int y = 0 ; y <  totalQueen ; y  ++){

                domain.add(new Point(x,y));
            }

            csp.createVariable("Q"+(x+1),domain);
        }

        for(CSPvariable Xi : csp.variables){

            for(CSPvariable Xj : csp.variables){

                if(!Xi.equals(Xj)){
                    csp.addConstraint(new CanNotAttackConstraint(Xi, Xj));
                }

            }

        }

        /*

        //crée une liste d'indice de 0 au nombre de variables
        LinkedList<Integer> ids = new LinkedList<>();

        for( int i = 0 ; i < totalQueen ; i ++) {
            ids.add(i);
        }
        //melange la liste
        Collections.shuffle(ids);

        //reduire le domaine de certaines variables à une seule valeur
        for(int v = 0 ; v < 2 ; v ++){
            //recupere et retire un indice
            int rdmId = ids.removeFirst();
            //recupere le domaine
            ArrayList<Point> dom = csp.getDomain(rdmId);

            //recupere une valeur du domaine au hazard
            int rdmValId = array.Util.rdnInt(0,dom.size() - 1);

            Point rdmVal = dom.get(rdmValId);

            //reduit le domaine à cette valeur
            dom.clear();

            dom.add(rdmVal);
        }
*/

        return csp;

    }

    private static BinaryCSP testMapAustralia(){

        BinaryCSP<String> csp = new BinaryCSP();
        ArrayList<String> couleurs = new ArrayList<>(Arrays.asList(new String[]{"Vert","Rouge","Bleu"}));

        CSPvariable AO = csp.createVariable("AO",new ArrayList<>(couleurs));
        CSPvariable NGS = csp.createVariable("NGS",new ArrayList<>(couleurs));
        CSPvariable T = csp.createVariable("T",new ArrayList<>(couleurs));
        CSPvariable TN = csp.createVariable("TN",new ArrayList<>(couleurs));
        CSPvariable AM = csp.createVariable("AM",new ArrayList<>(couleurs));
        CSPvariable Q = csp.createVariable("Q",new ArrayList<>(couleurs));
        CSPvariable V = csp.createVariable("V",new ArrayList<>(couleurs));


        csp.addConstraint(new BinaryConstraintDif(AO,TN));
        csp.addConstraint(new BinaryConstraintDif(AO,AM));
        csp.addConstraint(new BinaryConstraintDif(TN,AO));
        csp.addConstraint(new BinaryConstraintDif(TN,AM));
        csp.addConstraint(new BinaryConstraintDif(TN,Q));
        csp.addConstraint(new BinaryConstraintDif(Q,TN));
        csp.addConstraint(new BinaryConstraintDif(Q,NGS));
        csp.addConstraint(new BinaryConstraintDif(Q,AM));
        csp.addConstraint(new BinaryConstraintDif(NGS,Q));
        csp.addConstraint(new BinaryConstraintDif(NGS,AM));
        csp.addConstraint(new BinaryConstraintDif(NGS,V));
        csp.addConstraint(new BinaryConstraintDif(V,NGS));
        csp.addConstraint(new BinaryConstraintDif(V,AM));
        csp.addConstraint(new BinaryConstraintDif(AM,AO));
        csp.addConstraint(new BinaryConstraintDif(AM,TN));
        csp.addConstraint(new BinaryConstraintDif(AM,Q));
        csp.addConstraint(new BinaryConstraintDif(AM,NGS));
        csp.addConstraint(new BinaryConstraintDif(AM,V));
        csp.addConstraint(new BinaryConstraintDif(T,V));

        return csp;

    }

    private static BinaryCSP testSquare(){

        BinaryCSP<Double> binaryCSP = new BinaryCSP();

        CSPvariable X1 = binaryCSP.createVariable("X",getDoubleList(100));
        CSPvariable X2 = binaryCSP.createVariable("Y",getDoubleList(1000));

        BinaryConstraintSquare binaryConstraintSquare = new BinaryConstraintSquare(X1,X2);
        BinaryConstraintSquareRoot binaryConstraintSquareRoot = new BinaryConstraintSquareRoot(X2,X1);

        binaryCSP.addConstraint(binaryConstraintSquare);
        binaryCSP.addConstraint(binaryConstraintSquareRoot);

        //AC3 ac3 = new AC3();

        return binaryCSP;

    }




}
