package ai.agent.logic;

import ai.agent.logic.expressions.*;
import util.Util;

import java.util.*;

public class PlSolveFirstOrderV3LiteralPattern extends PL_Solve {

    static int totalRs = 0 ;

    private static Hashtable<Statement,HashSet<Statement>> computed;
    private static Hashtable<String,List<Statement>> statementsByLiterals;

    static int dp = 0;
    public static void showProcess(List<Statement> childs){

        if(childs.isEmpty())
            return;

        List<Statement> parents = new LinkedList<>();

        for( Statement child : childs ){

            if(child.getP1() != null) {
                parents.add(child.getP1());
            }
            if(child.getP2() != null) {
                parents.add(child.getP2());
            }
        }

        showProcess(parents);

        String ident = Util.getIdent(dp);

        for( Statement child : childs ){

            if(child.getP1() == null) {
                System.out.println(ident + child + " PREDICAT ORIGINE");

            }else {
                System.out.println(ident + child + " ENGENDRE PAR " + child.getP1() + " " + child.getP2());
                System.out.println(ident + "AVANT NORMALISATION  " + child.getNotNormalized());
                System.out.println(ident + "ANNULATION DE " + child.getL1() + " PAR " + child.getL2());
                System.out.println(ident + "SUBSTITUTION " + child.getSubs());
                System.out.println(ident + "FACTORISATION " + child.getSubs2());
                System.out.println(ident + "FACTORISATION " + child.getFacto());

            }
        }

        System.out.println();

        dp++;

    }


    private static void addClauseToIndex(Statement clause){

        // System.out.println();
        // System.out.println("AJOUT A L INDEX DE LA CLAUSE "+clause);

        for( Statement literal : clause.getLiteralList()){

            //   System.out.println("AJOUT DU LITERAL "+literal);

            Set<String> patterns = literal.getSymbol().getPatterns(literal instanceof Not);

            for(String pattern : patterns) {

                // System.out.println("------> "+pattern);

                if (!statementsByLiterals.containsKey(pattern)) {
                    statementsByLiterals.put(pattern, new LinkedList<Statement>());
                }

                statementsByLiterals.get(pattern).add(clause);

            }
        }

    }

    public static boolean valid(List<Statement> p_clauses){

        System.out.println();
        System.out.println();
        computed = new Hashtable<>();
        statementsByLiterals = new Hashtable<>(3000);

        for(Statement clause : p_clauses){
            addClauseToIndex(clause);
        }

        HashSet<Statement> clauseSet = new LinkedHashSet<>(p_clauses);

        Hashtable<String, Statement> newClausesSet = new Hashtable<>();

        for(Statement clause : p_clauses){
            newClausesSet.put(clause.noVarsSignature(),clause);
        }

        boolean showlog = false;

        int s = 0;

        /*
        *
        * les clauses resultantes doivent être cloné pour empecher que les modifications ou
        * substitutions n'empiettent sur les clauses d'origines
        *
        * les clauses CNF doivent toutes constitués d' instances differentes
        *
        * */

        System.out.println(clauseSet.size());

        long test = 0;
        long noRsTest = 0;

        try {

            do {
                ArrayList<Statement> clauses = new ArrayList<>(clauseSet);

                Collections.sort(clauses, new Comparator<Statement>() {
                    @Override
                    public int compare(Statement o1, Statement o2) {
                        return Integer.compare(o1.getLiteralList().size(), o2.getLiteralList().size());
                    }
                });

                if (showlog)
                    System.out.println("--------------TOUR " + s + "------------");

                int totalSimilar = 0;
                totalRs = 0;

                HashSet<Statement> newclauses = new LinkedHashSet<>();

                for (int i = 0; i < clauses.size(); i++) {
                    if (showlog) {
                        System.out.println();
                        System.out.println("------------------------- TRAITEMENT CLAUSE " + clauses.get(i) + " --------------------------");
                    }

                    //pour chaque literal de la clause
                    Statement clause = clauses.get(i);
                    //System.out.println();
                    //System.out.println("CLAUSE 1 "+clause);

                    Set<Statement> oppositeclauses = new LinkedHashSet<>();

                    for (Statement literal : clause.getLiteralList()) {

                        //System.out.println("RECHERCHE CLAUSE OPPOSE POUR LITERAL "+literal);

                        if (showlog)
                            System.out.println("LITERAL : " + literal);
                        //recupere les clauses qui ont un literal opposé


                        Set<String> matchesPatterns = literal.getSymbol().getMatchesPatterns(!(literal instanceof Not));
/*
                        Set<Statement> matchClauses = new LinkedHashSet<>();

                        for(String matchPattern : matchesPatterns) {

                            if (statementsByLiterals.containsKey(matchPattern)){
                                matchClauses.addAll(statementsByLiterals.get(matchPattern));
                            }
                        }

                        System.out.println("TOTAL CORRESPONDANCE POUR LITERAL : "+matchClauses.size());
*/

                        for(String matchPattern : matchesPatterns) {

                            // System.out.println("----->PATTERN CORRESPONDANT : "+matchPattern);

                            statementsByLiterals.containsKey(matchPattern);

                            if (statementsByLiterals.containsKey(matchPattern)){

                                //System.out.println("------>TOTAL CLAUSE TROUVE "+statementsByLiterals.get(matchPattern).size());

                                for (Statement oppositeClause : statementsByLiterals.get(matchPattern)) {

                                    //System.out.println("------>CLAUSE OPPOSE "+oppositeClause);

                                    if (showlog)
                                        System.out.println("CLAUSE INTERESSANTE " + oppositeClause);

                                    if (clause.equals(oppositeClause)) {
                                        if (showlog)
                                            System.out.println("MEME CLAUSE");
                                        continue;
                                    }


                                    HashSet<Statement> opp = computed.get(clause);

                                    if (opp == null || !opp.contains(oppositeClause)) {

                                        if (showlog) {
                                            System.out.println("TRAITEMENT ");
                                            System.out.println("RESOLUTION \n CL1 : " + clause + "\n CL2 : " + oppositeClause);
                                        }
                                        //marque les deux clauses comme déja testé

                                        oppositeclauses.add(oppositeClause);

                                        HashSet<Statement> rsClauses = solve(clause, oppositeClause, literal, matchPattern);
                                        totalRs += rsClauses.size();

                                        test++;
                                        if (rsClauses.isEmpty()) {
                                            noRsTest++;
                                        }

                                        for (Statement rsClause : rsClauses) {

                                            if (rsClause instanceof EmptyStatement) {

                                                System.out.println("RESOLUTION \n CL1 : " + clause + "\n CL2 : " + oppositeClause);

                                                List<Statement> l1 = new LinkedList<>();
                                                List<Statement> l2 = new LinkedList<>();

                                                l1.add(clause);
                                                l2.add(oppositeClause);

                                                showProcess(l1);
                                                showProcess(l2);
/*
                                                for(Map.Entry<String,List<Statement>> entry : statementsByLiterals.entrySet()){
                                                    System.out.println("Pattern "+entry.getKey());
                                                    for(Statement st : entry.getValue()) {
                                                        System.out.println("         " +st);
                                                    }
                                                }
*/
                                                System.out.println("Total keys : "+statementsByLiterals.keySet().size());

                                                return true;
                                            }

/*
                                    if(!newClausesSet.containsKey(rsClause.noVarsSignature())){
                                        newClausesSet.put(rsClause.noVarsSignature(),rsClause);
                                        newclauses.add(rsClause);

                                    }
*/
                                            if (showlog) {
                                                System.out.println("RESULTAT :" + rsClause);
                                                System.out.println("ANNULATION DE " + rsClause.getL1() + " PAR " + rsClause.getL2());
                                                System.out.println("SUBSTITUTION " + rsClause.getSubs());
                                                System.out.println();
                                            }


                                            if (!newClausesSet.containsKey(rsClause.toString())) {
                                                newclauses.add(rsClause);
                                                newClausesSet.put(rsClause.toString(), rsClause);
                                            }

                                        }

                                    } else {
                                        if (showlog)
                                            System.out.println("DEJA TESTE");
                                    }

                                }
                            }
                        }

                    }


                    for(Statement oppositeClause : oppositeclauses) {
                        setCalculated(clause, oppositeClause);
                        setCalculated(oppositeClause, clause);
                    }
/*
                if(i > 10)
                System.exit(0);*/

                }


                for (Statement newClause : newclauses) {
                    addClauseToIndex(newClause);
                }

                if (showlog) {
                    System.out.println("TOTAL SIMILAR " + totalSimilar);
                    System.out.println("TOTAL RESULT " + totalRs);
                    System.out.println("TOTAL NEW " + newClausesSet.size());
                }

                if (showlog) {
                    System.out.println();
                    System.out.println("CLAUSE SET " + clauseSet.size());
                    for (Statement clause : clauseSet)
                        System.out.println(clause);

                    System.out.println();
                    System.out.println("NEW CLAUSE SET " + newClausesSet.size());
                    for (Statement clause : newClausesSet.values())
                        System.out.println(clause);

                }


                if (clauseSet.containsAll(newClausesSet.values())) {

                    System.out.println("Pas de nouvelles clauses " + s);

                    return false;
                }


                clauseSet.addAll(newClausesSet.values());

                if (showlog || true) {
                    System.out.println();
                    System.out.println("CLAUSE SET APRES FUSION " + clauseSet.size());
                    for (Statement clause : clauseSet)
                        System.out.println(clause);
                }

                s++;

                //System.exit(0);
                System.out.println();
                System.out.println("TOTAL TESTS "+totalRs);
                System.out.println("TOTAL NO RESULTS TESTS "+noRsTest);
                System.out.println();





            } while (true);

        }catch (Exception e){

            System.out.println(totalRs);
            System.out.println(noRsTest);
            throw e;
        }



    }

    protected static void setCalculated(Statement cl1, Statement cl2){

        HashSet<Statement> cl2s = computed.get(cl1);

        if(cl2s == null){
            cl2s = new HashSet<Statement>();
            computed.put(cl1, cl2s);
        }

        cl2s.add(cl2);

    }


    protected static boolean alreadyCalulated(Statement cl1, Statement cl2){

        HashSet<Statement> cl2s = computed.get(cl1);

        if(cl2s == null){
            computed.put(cl1, new HashSet<Statement>());
        }else{

            if(!cl2s.contains(cl2)){
                cl2s.add(cl2);
            }else{
                return true;
            }

        }

        return false;

    }

    /** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *
     * Pour l instant on compare chaques literaux des deux clause
     *
     * Mais avec l'index des clauses par literaux ont sait quelles literaux
     * peuvent être potentiellement unifié sans devoir tous les testé
     * on a le literal 1 et on conait le masque de correspondance dans la clause opposé
     * on pourrait indexer les literaux opposé par masques correspondant et ainsi obtenir le resultat plus rapidement
     *
     * */


    protected static HashSet<Statement> solve( Statement cl1, Statement cl2,Statement lit1, String matchLit2) {



/*
        if(alreadyCalulated(cl1,cl2) || alreadyCalulated(cl2,cl1)){
            return new LinkedHashSet<>();
        }
        */

/*
        if(cl1.toString() == "(  ¬ GrandParent(x1,y1)  ∨ Parent(x1,A(x1,y1)) )" ||
                cl2.toString() == "(  ¬ Parent(x9,ElizabethII)  ∨ GrandParent(x9,Charles) )"){
            System.out.println("SOLVE : "+cl1+" - "+cl2);
        }
*/
        HashSet<Statement> clauses = new LinkedHashSet<>();

        //on boucle sur les enonces (prédicats positifs ou négatifs) de la premiere clause

        for(Statement lit2 : cl2.getMatchLiterals(matchLit2)) {

            try {

                Hashtable<Variable, FonctionalSymbol> substitutions = Unify.unify(lit1.getSymbol(),
                        lit2.getSymbol(), new Hashtable<Variable, FonctionalSymbol>());

                //l'unification à fonctionné mais tout les substituts sont des variables
/*
                        if(Unify.allVars(substitutions)) {
                            //System.out.println("\nALL VARS "+substitutions);
                           //System.out.println("ST1 : "+cl1+" \nST2 : "+cl2);
                           // System.out.println(statement+" "+statement2);
                            continue;
                        }
*/

                // !!! normaliser la nouvelle clause
                List<Statement> clause = new ArrayList<>();

                for (Statement cl : cl1.getLiteralList()) {
                    if (!cl.equals(lit1))
                        clause.add(cl.clone(true));
                }

                for (Statement cl : cl2.getLiteralList()) {
                    if (!cl.equals(lit2))
                        clause.add(cl.clone(true));
                }

                //System.out.println(totalRs+" NEW CLAUSE = "+clause);

                if (clause.isEmpty()) {

                    clauses.add(new EmptyStatement());

                } else {

                    //remplacer chaque variable par son substitut
                    //en substituant les parametres la valeur de hashage du literal n'est plus corect
                    //pour le set clause !!!

                    Statement beforeAnyChange = new Or().getStatement(clause).clone(true);
                    // Statement cl1B = cl1.clone(true);
                    // Statement cl2B = cl2.clone(true);


                    for (Statement predicat : clause) {
                        predicat.getSymbol().replaceVars(substitutions);
                    }

                    //retirer chaque literal unifiable avec un autre dans la clause pour n'en conserver qu'un
                    // List<Statement> rm = new LinkedList<>();
                    // List<Statement> add = new LinkedList<>();
                    boolean trueClause = false;
                    Hashtable<Variable, FonctionalSymbol> subs = null;
                    String facto = "";
                    breakPoint:
                    for (int i = 0; i < clause.size(); i++) {

                        for (int j = i + 1; j < clause.size(); j++) {

                            try {
/*
                                        System.out.println("UNIFICATION");
                                        System.out.println(l1.get(i));
                                        System.out.println(l1.get(j));
                                        */
                                //recupere les symboles de substitutions pour les deux literaux
                                subs = Unify.unify(clause.get(i).getSymbol(),
                                        clause.get(j).getSymbol(), new Hashtable<Variable, FonctionalSymbol>());
                                //si les deux clauses sont unifiables
                                // System.out.println("REUSSITE");
                                //si les deux symboles sont complementaires la clause est vrai
                                if ((clause.get(i) instanceof Symbol && clause.get(j) instanceof Not)
                                        || (clause.get(j) instanceof Symbol && clause.get(i) instanceof Not)) {
                                    trueClause = true;
                                    break breakPoint;
                                }//literaux de meme signe sont redondants
                                else /*if(!Unify.allVars(subs))*/ {

/*
                                            System.out.println("FACTORISATION ");
                                            System.out.println(l1.get(i));
                                            System.out.println(l1.get(j));
*/
                                    //on sauvegarde la clause à retirer l'une des deux ou les deux et la plus général

                                    //rm.add(clause.get(i));
                                    // rm.add(clause.get(j));


                                    //on clone une des deux
                                    //Statement unify = clause.get(i).clone(true);
                                    //Statement unify2 = clause.get(j).clone(true);

                                    //on unifie l'ensemble de la clause !!!
                                    //unify.getSymbol().replaceVars(subs);
                                    // unify2.getSymbol().replaceVars(subs);
                                    // add.add(unify);

                                    for (Statement lit : clause) {
                                        lit.replaceVars(subs);
                                    }

                                    facto += " " + clause.get(i) + " - " + clause.get(j) + " = " + subs;
                                    // System.out.println("UNIFY");
                                    // System.out.println(unify+" "+unify2);

                                    //on substitue les variables pour rendre les clauses identiques
/*
                                            //if(!subs.isEmpty()) {
                                            l1.get(i).getSymbol().replaceVars(subs);
                                            //System.out.println(l1.get(i).getSymbol().getFonctionalSymbols());
                                            l1.get(j).getSymbol().replaceVars(subs);
                                            //System.out.println(l1.get(j).getSymbol().getFonctionalSymbols());
*/
                                    //}
                                            /*
                                            System.out.println("SUBSTITUTION ");
                                            System.out.println(l1.get(i));
                                            System.out.println(l1.get(j));

*/
                                }


                            }catch (Statement.SubstitutionException se){
                               // System.out.println("FACTO SUB : "+se.getMessage());
                            } catch (Unify.UnifyException e) {
                                //System.out.println("ECHEC "+e.getMessage()+" "+l1.get(i)+" "+l1.get(j));
                            } catch (Exception e) {

                                System.out.println("CL1 : " + cl1 + "\nCL2 : " + cl2);
                                System.out.println("LIT 1 : " + lit1 + " \nLIT 2 : " + lit2);
                                System.out.println("CLAUSE : " + clause);
                                System.out.println(clause.get(i) + " " + clause.get(j));
                                System.out.println("TRUE CLAUSE " + trueClause);
                                System.out.println("SUBSTITUTION : " + subs);

                                //throw e;
                            } catch (StackOverflowError so) {
                                System.out.println("STACK OVERFLOW B");
                                System.out.println(cl1 + " " + cl2);
                                System.out.println(lit1 + " " + lit2);
                                System.out.println(clause.get(i) + " " + clause.get(j));
                            }
                        }

                    }

                    //retrait de tout les doublons
                    if (!trueClause) {

                        // if(!rm.isEmpty()) {
/*
                                    System.out.println("CLAUSE" + clause);

                                    for (Statement statement1 : clause) {
                                        System.out.println();
                                        for (Statement statement3 : rm) {
                                            System.out.println(" " + statement1 + " <> " + statement3 + " " + statement1.equals(statement3) + "" +
                                                    " " + statement1.hashCode() + " " + statement3.hashCode());
                                        }
                                    }

                                    System.out.println("REMOVE " + rm + " from " + clause);
*/
                        // clause.removeAll(rm);
                        // clause.addAll(add);
                        //System.out.println("FACTORISED CLAUSE "+clause);
                        //  }

                        //clause.removeAll(rm);
                        //System.out.println("FACTORISED CLAUSE "+clause);

                        //System.out.println(totalRs+" "+cl1+" "+cl2);

                        //System.out.println(totalRs+" CLAUSE RESULTAT "+clause);

                        Statement newStatement = new Or().getStatement(new LinkedHashSet(clause));

                        newStatement.setP1(cl1);
                        newStatement.setP2(cl2);
                        newStatement.setL1(lit1);
                        newStatement.setL2(lit2);
                        newStatement.setSubs(substitutions);
                        newStatement.setSubs2(subs);
                        newStatement.setFacto(facto);
                        newStatement.setNotNormalized(beforeAnyChange);
                        //Statement.normalize(newStatement);

                        boolean ok = true;
                        for(Statement literal : newStatement.getLiteralList()){
                            if(literal.getSymbol().skolemIntoSkolem()){
                                ok = false;
                                break;
                            }
                        }
                        if(ok) {
                            clauses.add(newStatement);
                        }else{
                            //System.out.println("SKOLEM INTO SKOLEM : "+newStatement);
                        }
                    }

                }


            }catch (Statement.SubstitutionException se){
               // System.out.println("MAIN SUB : "+se.getMessage());
            } catch (Unify.UnifyException e) {
                //System.out.println("ECHEC "+e.getMessage());
            } catch (StackOverflowError so) {
                System.out.println("STACK OVERFLOW A");
                System.out.println(cl1 + " " + cl2);
                System.out.println(lit1 + " " + lit2);
            }

        }

        return clauses;

    }

}
/*
*
* TEST A
 CL1 :  (  ¬ GrandParent( g1, c1 )  ∨ Parent( A( g1, c1 ), c1 ) )
 CL2 : GrandParent( George, Andrew )
TEST B
 CL1 :  (  ¬ GrandParent( g1, c1 )  ∨ Parent( A( g1, c1 ), c1 ) )
 CL2 : GrandParent( George, Andrew )

RESOLUTION
 CL1 :  (  ¬ GrandParent( g1, c1 )  ∨ Parent( A( George, Andrew ), Andrew ) )
 CL2 : GrandParent( George, Andrew )
Parent( A( George, Andrew ), Andrew )
*
* */