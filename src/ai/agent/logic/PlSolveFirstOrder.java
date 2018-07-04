package ai.agent.logic;

import ai.agent.logic.expressions.*;
import util.Util;

import java.util.*;

public class PlSolveFirstOrder extends PL_Solve {

    static int totalRs = 0 ;

    private static Hashtable<Statement,HashSet<Statement>> computed;


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

            System.out.println(ident+child+" ENGENDRE PAR "+child.getP1()+" "+child.getP2());
        }

        System.out.println();

        dp++;

    }



    public static boolean valid(List<Statement> p_clauses){

        System.out.println();
        System.out.println();
        computed = new Hashtable<>();
/*
        Collections.sort(p_clauses, new Comparator<Statement>() {
            @Override
            public int compare(Statement o1, Statement o2) {
                return Integer.compare(o1.getLiterals().size(), o2.getLiterals().size());
            }
        });
*/
        HashSet<Statement> clauseSet = new LinkedHashSet<>(p_clauses);

/*
        for(Statement statement : clauseSet)
            System.out.println(statement);

        System.exit(0);
*/
        Hashtable<String, Statement> newClausesSet = new Hashtable<>();

        boolean showlog = false;

        int s = 0;

        /*
        *
        * les clauses resultantes doivent être cloné pour empecher que les modifications ou
        * substitutions n'empiettent sur les clauses d'origines
        *
        * les clauses resultantes doivent être normalisés ?
        *
        * les clauses CNF doivent toutes constitués d' instances differentes
        *
        * les constantes de skolem contiennent des variables qui les rendent uniques mais qui peuvent gener l'unification
        *
        * une fonction de skolem contenant des variables peut faire echouer le test
        faut il considerer la fonction de skolem comme une constante et ne pas comparer les variables qu'elle contient
        ou faut il les comparer ???
        * */

        System.out.println(clauseSet.size());

        do{
            ArrayList<Statement> clauses = new ArrayList<>(clauseSet);

            Collections.sort(clauses, new Comparator<Statement>() {
                @Override
                public int compare(Statement o1, Statement o2) {
                    return Integer.compare(o1.getLiterals().size(), o2.getLiterals().size());
                }
            });

            System.out.println("--------------TOUR "+s+"------------");


            int totalSimilar = 0;
            totalRs = 0;
            for(int i = 0 ; i < clauses.size() ; i ++ ){

                for(int j = i + 1 ; j < clauses.size() ; j ++ ){
                    //création de nouvelles clause en resolvant la paire

                    //System.out.println("TEST \n CL1 : " + clauses.get(i) + "\n CL2 : " + clauses.get(j));

                    HashSet<Statement> rsClauses = solve(clauses.get(i), clauses.get(j));
                    totalRs += rsClauses.size();

                    if(showlog) {

                        //?? retirer les clauses resolues ou les garder
                        //ajouter directement les clauses resolues à la fin ?


                        if(!rsClauses.isEmpty()) {

                            //System.out.println("TEST B \n CL1 : " + cl1 + "\n CL2 : " +cl2);

                            System.out.println("TOTAL NEW " + newClausesSet.size());

                            System.out.println();
                            System.out.println("RESOLUTION \n CL1 : " + clauses.get(i) + "\n CL2 : " + clauses.get(j));

                            for (Statement clause : rsClauses) {
                                System.out.println(clause);
                            }

                            System.out.println();
                        }
                    }

                    //System.exit(0);
/*
                    if( newClausesSet.size() > 0 && newClausesSet.size() % 30000 == 0)
                        System.out.println(newClausesSet.size());
*/
                    //verifie si les nouvelles clause créés contiennent une clause vide
                    for(Statement rsClause : rsClauses){
                        if(rsClause instanceof EmptyStatement){
                            if(showlog || true) {

                                System.out.println("RESOLUTION \n CL1 : " + clauses.get(i) + "\n CL2 : " + clauses.get(j));

                                List<Statement> l1 = new LinkedList<>();
                                List<Statement> l2 = new LinkedList<>();

                                l1.add(clauses.get(i));
                                l2.add(clauses.get(j));

                                showProcess(l1);
                                showProcess(l2);

                                System.out.println("EMPTY");
                            }

                            return true;
                        }
                    }

                    //ajout des nouvelles clauses

                    for(Statement rsClause : rsClauses){
                       // System.out.println("RS CLAUSE  SIGN "+rsClause.toString());
                      //  System.out.println("RS CLAUSE NO VAR SIGN "+rsClause.noVarsSignature());
/*
                        if(!newClausesSet.containsKey(rsClause.noVarsSignature())){
                            newClausesSet.put(rsClause.noVarsSignature(),rsClause);
                        }
                        */

                        if(!newClausesSet.containsKey(rsClause.toString())){
                            newClausesSet.put(rsClause.toString(),rsClause);
                        }

                    }

                    //newClausesSet.addAll(rsClauses);
                    //pour chaque clause resultat

                    //System.out.println("TOTAL "+totalRs);
                    //System.out.println("TOTAL NEW "+newClausesSet.size());

                }

            }


            if(showlog || true) {
                System.out.println("TOTAL SIMILAR " + totalSimilar);
                System.out.println("TOTAL RESULT " + totalRs);
                System.out.println("TOTAL NEW " + newClausesSet.size());
            }

            if(showlog) {
                System.out.println();
                System.out.println("CLAUSE SET");
                for (Statement clause : clauseSet)
                    System.out.println(clause);

                System.out.println();
                System.out.println("NEW CLAUSE SET");
                for (Statement clause : newClausesSet.values())
                    System.out.println(clause);

            }



            if(clauseSet.containsAll(newClausesSet.values())){

                System.out.println("Pas de nouvelles clauses "+s);

                return false;
            }

            clauseSet.addAll(newClausesSet.values());




            s++;

           // System.exit(0);

        }while (true);

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

    protected static HashSet<Statement> solve(Statement cl1, Statement cl2) {
/*
        if(alreadyCalulated(cl1,cl2) || alreadyCalulated(cl2,cl1)){
            return new LinkedHashSet<>();
        }
        */
        HashSet<Statement> clauses = new LinkedHashSet<>();

        //on boucle sur les enonces (prédicats positifs ou négatifs) de la premiere clause
        for(Statement statement : cl1.getLiterals()){

            for( Statement statement2 : cl2.getLiterals() ){
                //si les deux literaux sont complementaires
                if( (statement instanceof Symbol && statement2 instanceof Not)
                        || (statement2 instanceof Symbol && statement instanceof Not) ){

                    try {

                        Hashtable<Variable,FonctionalSymbol> substitutions  = Unify.unify(statement.getSymbol(),
                                statement2.getSymbol(), new Hashtable<Variable,FonctionalSymbol>());

                        // !!! normaliser la nouvelle clause
                        HashSet<Statement> clause = new LinkedHashSet<>();

                        for(Statement cl : cl1.getLiterals()){
                            clause.add(cl.clone(true));
                        }

                        clause.remove(statement);

                        for(Statement cl : cl2.getLiterals()){
                            clause.add(cl.clone(true));
                        }

                        clause.remove(statement2);

                        //System.out.println(totalRs+" NEW CLAUSE = "+clause);

                        if(clause.isEmpty()){

                            clauses.add(new EmptyStatement());

                        }else{

                            //remplacer chaque variable par son substitut
                            //en substituant les parametres la valeur de hashage du literal n'est plus corect
                            //pour le set clause !!!

                            List<Statement> literals = new LinkedList<>(clause);

                            for(Statement predicat : literals){
                                predicat.getSymbol().replaceVars(substitutions);
                            }

                            clause.clear();
                            clause.addAll(literals);

                            //retirer chaque literal unifiable avec un autre dans la clause pour n'en conserver qu'un
                            List<Statement> l1 = new LinkedList<>(clause);
                            List<Statement> rm = new LinkedList<>();
                            List<Statement> add = new LinkedList<>();
                            boolean trueClause = false;

                            breakPoint:
                            for( int i = 0 ; i < l1.size() ; i ++ ){

                                for(int j = i + 1 ; j < l1.size() ; j ++ ){

                                    Hashtable<Variable, FonctionalSymbol> subs = null;

                                    try {
/*
                                        System.out.println("UNIFICATION");
                                        System.out.println(l1.get(i));
                                        System.out.println(l1.get(j));
                                        */
                                        //recupere les symboles de substitutions pour les deux literaux
                                        subs = Unify.unify(l1.get(i).getSymbol(),
                                                l1.get(j).getSymbol(), new Hashtable<Variable, FonctionalSymbol>());
                                        //si les deux clauses sont unifiables
                                        // System.out.println("REUSSITE");
                                        //si les deux symboles sont complementaires la clause est vrai
                                        if( (l1.get(i) instanceof Symbol && l1.get(j) instanceof Not)
                                                || (l1.get(j) instanceof Symbol && l1.get(i) instanceof Not) ){
                                            trueClause = true;
                                            break breakPoint;
                                        }//literaux de meme signe sont redondants
                                        else{
/*
                                            System.out.println("FACTORISATION ");
                                            System.out.println(l1.get(i));
                                            System.out.println(l1.get(j));
*/
                                            //on sauvegarde la clause à retirer l'une des deux ou les deux et la plus général

                                            rm.add(l1.get(i));
                                            rm.add(l1.get(j));

                                            //on clone une des deux
                                            Statement unify = l1.get(i).clone(true);
                                            Statement unify2 = l1.get(j).clone(true);
                                            //on l'unifie ce qui doit donner le meme resultat pour les deux
                                            unify.getSymbol().replaceVars(subs);
                                            unify2.getSymbol().replaceVars(subs);
                                            add.add(unify);
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
                                        System.out.println(se.getMessage());
                                    }catch (Unify.UnifyException e){
                                        //System.out.println("ECHEC "+e.getMessage()+" "+l1.get(i)+" "+l1.get(j));
                                    }catch (Exception e){

                                        System.out.println("CL1 : "+cl1+"\nCL2 : "+cl2);
                                        System.out.println("LIT 1 : "+statement+" \nLIT 2 : "+statement2);
                                        System.out.println("CLAUSE : "+clause);
                                        System.out.println(l1.get(i)+" "+l1.get(j));
                                        System.out.println("TRUE CLAUSE "+trueClause);
                                        System.out.println("SUBSTITUTION : "+subs);

                                        throw e;
                                    }
                                }

                            }

                            //retrait de tout les doublons
                            if(!trueClause) {

                                if(!rm.isEmpty()) {
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
                                    clause.removeAll(rm);
                                    clause.addAll(add);
                                    //System.out.println("FACTORISED CLAUSE "+clause);
                                }

                                //clause.removeAll(rm);
                                //System.out.println("FACTORISED CLAUSE "+clause);

                                //System.out.println(totalRs+" "+cl1+" "+cl2);

                                //System.out.println(totalRs+" CLAUSE RESULTAT "+clause);
                                Statement newStatement = new Or().getStatement(new LinkedList(clause));
                                newStatement.setP1(cl1);
                                newStatement.setP2(cl2);
                                clauses.add(newStatement);
                            }

                        }


                    }catch (Statement.SubstitutionException se){
                        System.out.println(se.getMessage());
                    } catch (Unify.UnifyException e) {
                        //System.out.println("ECHEC "+e.getMessage());
                    }

                }

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