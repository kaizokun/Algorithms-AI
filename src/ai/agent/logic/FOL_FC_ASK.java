package ai.agent.logic;

import ai.agent.logic.expressions.*;
import util.Util;

import java.util.*;

public class FOL_FC_ASK {

    private static boolean showLog = true;

    public static List<Hashtable<Variable, FonctionalSymbol>> valid(List<Statement> axioms, List<Statement> facts,
                                                                    Hashtable<String,List<Statement>> factsIndex,
                                                                    Statement request, int max){

        List<Hashtable<Variable, FonctionalSymbol>> allSubstitutions = new LinkedList<>();



        System.out.println();
        System.out.println("----------------FOL_FC_ASK---------------");
        System.out.println();

        List<Statement> newFacts;
        ArrayList<Hashtable<String,List<Statement>>> previousFactsIndex = new ArrayList<>();

        previousFactsIndex.add(factsIndex);

        int i = 1;

        do {
            if(showLog)
            System.out.println("ITERATION N° "+i);

            newFacts = new LinkedList<>();

            for(Statement axiom : axioms){

                if(showLog)
                    System.out.println("\nAXIOM "+axiom);

                //teste si l'axiom s'unifie avec des faits inférés à l'itération précédente
                boolean check = false;

                for(Statement literal : ((Imply)axiom).getSt1().getLiterals()){

                    if(showLog)
                        System.out.println("LITERAL "+literal+" "+literal.noVarsSignature()+" ");

                   if( previousFactsIndex.get(i-1).containsKey(literal.noVarsSignature())){
                       check = true;
                       break;
                   }
                }

                //si aucun fait inféré à l'itération précédente ne s'unifie avec l'axiom
                //il est inutile de chercher à inferer de nouveaux fait à partir de ceux d'origine
                //ou obtenus aux iterations antérieures.
                //ces faits ayant pu être inférés aux itérations precedentes

                if(!check){
                    continue;
                }


                Statement axiomb = rename(axiom);

                //récupération des combinaisons d'assignations differentes
                Set<Statement> assignations = getAxiomInstances(axiomb, factsIndex);

                if(showLog)
                    System.out.println("\nASSIGNATION TOTAL "+assignations.size());

                for( Statement assignation : assignations ){

                    if(showLog)
                        System.out.println("ASSIGNATION "+assignation);

                    //recuperer la conclusion
                    Statement conclusion = ((Imply)assignation).getSt2();
                    //pour chaque fait de la KB

                    int failCptr = 0;

                    for(Statement fact : facts){

                        try {
                            //on tente d'unifier la conclusion à un fait connu
                            Unify.unify(fact,conclusion, new Hashtable<Variable, FonctionalSymbol>());
                        } catch (Unify.UnifyException e) {
                            //on compte les echecs
                            failCptr ++;
                        }

                    }

                    //si la conclusion ne s'unifie avec aucun fait connu, il y a autant d'echec que de faits connus
                    //la conclusion est donc inconnue dans la KB
                    if(failCptr == facts.size()){

                        if(showLog)
                            System.out.println("CONCLUSION INCONNU "+conclusion);

                        //on l'ajoute entant que nouveau fait
                        newFacts.add(conclusion);


                        try {
                            //on tente de l'unifier avec la requete si on y parvient c'est qu'on à prouvé la requete
                            Hashtable<Variable, FonctionalSymbol> subs = new Hashtable();
                            Unify.unify(request, conclusion, subs );

                            allSubstitutions.add(subs);

                            if(allSubstitutions.size() == max){

                                facts.addAll(newFacts);
                                Statement.addFactsToIndex(newFacts,factsIndex);

                                return allSubstitutions;
                            }

                        } catch (Unify.UnifyException e) { }

                    }

                }

            }

            facts.addAll(newFacts);
            previousFactsIndex.add(Statement.getFactsIndex(newFacts));

            if(showLog) {
                System.out.println("NOUVEL INDEX DE FAIT");

                Statement.showFactsIndex(previousFactsIndex.get(i));
            }

            //Statement.addFactsToIndex(previousFactsIndex.get(i),factsIndex);

            Statement.addFactsToIndex(newFacts,factsIndex);

            i ++;

        }while (!newFacts.isEmpty());

        return allSubstitutions;

    }

    /*
    *
    * Ameliorer la selections des axioms à instancier
    *
    * Pre traitement :
    * pour chaque axiom parcourir l'ensemble des literaux et verifier si une regle connu correspond,
    * pour chaque correspondance implementer le nombre literaux connu de l'axiom.
    * une fois que le compteur atteind le nombre de literaux de l'axiom, activer l'axiom pour resolution :
    * l'ajouter dans la liste des faits a traiter et decrementer son compteur de 1 pour le desactiver.
    *
    * ensuite pendant le deroulement de l'algorithme de chainage avant, à chaque nouvelle conclusion
    * incrementer à nouveau les axioms ou l'on peut trouver ce literal pour activer de nouvelles regles
    * ou reactiver d'ancienne pour de nouvelles combinaisons.

    *
    *
    * */


    /*
     *
     *
     * recuperer la liste des symboles de la premise
     *
     * recuperer le symbole de la conclusion
     *
     * Creer une liste pour stocker les instances de l'énoncé
     *
     *
     * Rec(symbolesPremisse, symboleConclusion, ListInstancesEnonce)
     *
     * Si symbolesPremisse est vide
     *      on cree un enonce à partir des symboles de premise et de la conclusion
     *      on ajoute l'enonce dans la liste des instances.
     * Fin Si
     *
     *
     * Trier les symboles par nombre de fait correspondant à leur masque du plus petit au plus grand
     *
     * Retirer le premier symbole
     *
     * Recuperer tout les faits correspondants au masque du symbole
     *
     * Pour chaque fait
     *
     *      unifier le symbole avec le masque et obtenir la substitution
     *
     *      appliquer la substitution à l'ensemble des symboles de la premise et à la conclusion
     *
     *      Rec(symbolesPremisse, symboleConclusion, ListInstancesEnonce)
     *
     * Fin pour
     *
     * */


    private static Set<Statement> getAxiomInstances(Statement axiom, Hashtable<String, List<Statement>> factsIndex){

        Imply axiomImply = (Imply) axiom;

        ArrayList<Statement> st1 = new ArrayList<Statement>(axiomImply.getSt1().getSymbols());

        //sauvegarde les literaux d'origine avant assignations
        //afin de savoir à un moment donné de l'assignation quelles variables ont été assignées
        ArrayList<Statement> st1Origin = new ArrayList<Statement>(axiomImply.getSt1().getSymbols());
        for(int i = 0 ; i < st1.size() ; i ++){
            st1.get(i).setPreviousState(st1Origin.get(i));
        }

        Statement st2 =  axiomImply.getSt2();

        Set<Statement> axiomInstances = new LinkedHashSet<>();

        try {
            loadAxiomInstances(st1,st2,axiomInstances,factsIndex, 0);
        } catch (AssignationFailedException e) {
           // e.printStackTrace();
        }

        return axiomInstances;

    }

    private static void loadAxiomInstances(ArrayList<Statement> st1Literals, Statement st2Literal,
                                           Set<Statement> axiomInstances, final Hashtable<String, List<Statement>> factsIndex, int iLiteral ) throws AssignationFailedException {

        String ident = null;

        if(showLog)
            ident = Util.getIdent(iLiteral);

        /*
        * Recuperer toutes les variables deja assignées qui se trouvent dans le literal courant
        * ces variables sont deja assigné dans le literal courant donc on ne peut les reconnaitre
        * qu'en sauvegardant l'état initial du literal
        */

        if(iLiteral == st1Literals.size()){

            Statement st1 = new Or().getStatement(new LinkedList(st1Literals)).clone(true);
            Statement st2 = st2Literal.clone(true);

            Imply axiomInstance = new Imply(st1,st2);

            if(showLog)
                System.out.println(ident+"ASSIGNATION COMPLETE "+axiomInstance);

            axiomInstances.add(axiomInstance);

            return;
        }


        //on trie les symboles restants par nombre de fait disponibles
        Collections.sort(st1Literals.subList(iLiteral, st1Literals.size()), new Comparator<Statement>() {
            @Override
            public int compare(Statement l1, Statement l2) {

                List<Statement> l1Facts = factsIndex.get(l1.noVarsSignature()),
                                l2Facts = factsIndex.get(l2.noVarsSignature());

                int totalL1 = l1Facts != null ? l1Facts.size() : 0 ;
                int totalL2 = l2Facts != null ? l2Facts.size() : 0 ;

                return Integer.compare(totalL1,totalL2);
            }
        });

        //ajout des variables non assignées à l'origine
        Set<Variable> conflicts = new LinkedHashSet<>(st1Literals.get(iLiteral).getPreviousState().getAllVariables());
        //retrait des variables encore non assigné en cour
        conflicts.removeAll(st1Literals.get(iLiteral).getAllVariables());


        if(showLog)
            System.out.println(ident+"ORDRE DES LITERAUX DE LA PREMISSE "+st1Literals);

        if(showLog)
            System.out.println(ident+"LITERAL COURANT "+st1Literals.get(iLiteral)+" ETAT ORIGINE "+st1Literals.get(iLiteral).getPreviousState());
        System.out.println(ident+"ENSEMBLE DES CONFLICTS "+conflicts);

        //pour chaque fait correspondant au literal de la premises qui en possede le moins
        //pour l'assignation courante
        if(factsIndex.containsKey(st1Literals.get(iLiteral).noVarsSignature())) {

            if(showLog)
                System.out.println(ident+"NOMBRE DE FAITS CORRESPONDANT A "+st1Literals.get(iLiteral)+" "+ factsIndex.get(st1Literals.get(iLiteral).noVarsSignature()).size());

            for (Statement fact : factsIndex.get(st1Literals.get(iLiteral).noVarsSignature())) {

                if(showLog)
                    System.out.println(ident+"FAIT "+fact);

                Hashtable<Variable, FonctionalSymbol> assignations = new Hashtable<>();

                try {
                    //on unifie le fait au literal
                    Unify.unify(st1Literals.get(iLiteral), fact, assignations);

                    //on applique l'assignation à l'ensemble des literaux restants de la premisse et à la conclusion

                    //ArrayList<Statement> st1LiteralsCopy = new ArrayList<>(Arrays.asList(new Statement[st1Literals.size()]));
                    //copy du tableau de literaux complets
                    ArrayList<Statement> st1LiteralsCopy = new ArrayList<>(st1Literals.size());
                    //on pourrait assigner uniquement à partir de l indice du literal courant
                    //mais rien ne dit qu'un literal choisi precedemment ai été instancié partiellement
                    //et qu'un literal choisi par après ne puisse pas completer son assignation
                    for (int i = 0 /*iLiteral*/; i < st1Literals.size(); i++) {
                        Statement st1LiteralCopy =  st1Literals.get(i).clone(true);
                        st1LiteralCopy.setPreviousState(st1Literals.get(i).getPreviousState());
                        st1LiteralCopy.replaceVars(assignations);
                        st1LiteralsCopy.add(i,st1LiteralCopy);
                    }

                    Statement st2LiteralCopy = st2Literal.clone(true);
                    st2LiteralCopy.replaceVars(assignations);

                    try {
                        loadAxiomInstances(st1LiteralsCopy, st2LiteralCopy, axiomInstances, factsIndex, iLiteral + 1);
                    }catch (AssignationFailedException e){

                        if(showLog)
                        System.out.println(ident+" ASSIGNATION FAILED");

                        boolean canFix = false;

                        //l'ensemble des variables non assignées pour le literal courant
                        Set<Variable> variables = st1Literals.get(iLiteral).getAllVariables();

                        //si une des variables non assignées du literal courant peut regler le probleme
                        //c'est à dire que dans l'ensemble des variables retourné
                        //on retrouve une correpondance avec les variables courantes
                        for(Variable var : e.getConflicts()){
                            if(variables.contains(var)){
                                canFix = true;
                                break;
                            }
                        }

                        if(canFix){

                            if(showLog)
                                System.out.println(ident+"UNE AUTRE ASSIGNATION A LA CLAUSE PEUT REGLER LE PROBLEME");

                            //absorbtion des conflict rencontre plus en profondeur
                            conflicts.addAll(e.getConflicts());

                            //retrait des variables de la clause courante non assignées
                            conflicts.removeAll(st1Literals.get(iLiteral).getAllVariables());

                        }else{
                            if(showLog)
                                System.out.println(ident+" LA CLAUSE COURANTE NE PERMET PAS DE REGLER LE PROBLEME");
                            throw e;
                        }

                    }

                }catch (Statement.SubstitutionException se){
                    System.out.println(se.getMessage());
                } catch (Unify.UnifyException e) {

                }


            }
        }else{
            //à ce stade on detecte qu'aucune assignation n'est valide
            //on peut implementer un mecanisme de backtracking intelligent
            //qui remontrait à un literal susceptible de regler le probleme
            //il s'agit du literal qui contient une ou plusieurs variables en commun
            //avec celle du literal courant dont l'assignation a échoué
            if(showLog)
                System.out.println(ident+"AUCUNE ASSIGNATION VALIDE");

            throw new AssignationFailedException(conflicts);

        }


    }

    public static class AssignationFailedException extends Exception{

        private Set<Variable> conflicts;

        public AssignationFailedException() { }

        public AssignationFailedException(Set<Variable> conflicts) {
            this.conflicts = conflicts;
        }

        public Set<Variable> getConflicts() {
            return conflicts;
        }

        public void setConflicts(Set<Variable> conflicts) {
            this.conflicts = conflicts;
        }
    }

    private static Statement rename(Statement axiom) {
        return axiom;
    }


    public static void main(String[] args) {



        ennemiesOfAmerica();

    }

    private static void ennemiesOfAmerica() {

        List<Statement> axioms = new LinkedList<>();
        List<Statement> facts = new LinkedList<>();


        axioms.add(Statement.getStatementFromString(" ((((Americain(x) & Arme(y)) & Vend(x,y,z)) & Hostile(z)) > Criminel(x)) "));
        axioms.add(Statement.getStatementFromString(" ( Missile(x) > Arme(x) ) "));
        axioms.add(Statement.getStatementFromString(" ( Drone(x) > Arme(x) ) "));
        axioms.add(Statement.getStatementFromString(" ( Ennemi(x,Amerique) > Hostile(x) ) "));

        axioms.add(Statement.getStatementFromString(" (( Missile(x) & Possede(Nono,x) ) > Vend(West,x,Nono)) "));
        axioms.add(Statement.getStatementFromString(" (( Drone(x) & Possede(Rara,x) ) > Vend(East,x,Rara)) "));

        facts.add(Statement.getStatementFromString(" Possede(Nono,M1) "));
        facts.add(Statement.getStatementFromString(" Possede(Rara,M2) "));
        facts.add(Statement.getStatementFromString(" Missile(M1) "));
        facts.add(Statement.getStatementFromString(" Drone(M2)"));
        facts.add(Statement.getStatementFromString(" Americain(West) "));
        facts.add(Statement.getStatementFromString(" Americain(East) "));
        facts.add(Statement.getStatementFromString(" Ennemi(Rara,Amerique) "));
        facts.add(Statement.getStatementFromString(" Ennemi(Nono,Amerique) "));

        Statement request = Statement.getStatementFromString("Criminel(x)");
/*

        axioms.add(Statement.getStatementFromString(" ((((((((Diff(ao,tn) & Diff(ao,am)) & Diff(tn,q)) & Diff(tn,am)) & Diff(q,ngs)) & Diff(q,am)) & Diff(ngs,v)) & Diff(ngs,am)) & Diff(v,am)) > Colorable(ao,tn,q,ngs,v,am) "));

        facts.add(Statement.getStatementFromString(" Diff(Rouge,Bleu) "));
        facts.add(Statement.getStatementFromString(" Diff(Rouge,Vert) "));
        facts.add(Statement.getStatementFromString(" Diff(Vert,Rouge) "));
        facts.add(Statement.getStatementFromString(" Diff(Vert,Bleu) "));
        facts.add(Statement.getStatementFromString(" Diff(Bleu,Rouge) "));
        facts.add(Statement.getStatementFromString(" Diff(Bleu,Vert) "));

        Statement request = Statement.getStatementFromString("Colorable(ao,tn,q,ngs,v,am)");
*/

        Hashtable<String,List<Statement>> indexFacts = Statement.getFactsIndex(facts);

        //Statement.showFactsIndex(indexFacts);
        System.out.println("\nFACTS--------------");
        for(Statement statement : facts)
            System.out.println(statement);

        System.out.println("\nAXIOMS--------------");
        for(Statement statement : axioms)
            System.out.println(statement);

        List<Hashtable<Variable,FonctionalSymbol>> rs = valid(axioms,facts,indexFacts,request, 1);

        if(rs == null){
            System.out.println("ECHEC");
        }else{
            System.out.println();
            for(Hashtable asign : rs) {
                System.out.println("ASSIGNATION : " + asign);
            }
        }

        System.out.println("\nFACTS--------------");
        for(Statement statement : facts)
            System.out.println(statement);

    }


}
