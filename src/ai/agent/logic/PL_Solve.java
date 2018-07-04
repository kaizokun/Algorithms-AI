package ai.agent.logic;

import ai.agent.logic.expressions.*;
import ai.agent.logic.expressions.*;

import java.util.*;

public class PL_Solve {

    private static Hashtable<Statement,HashSet<Statement>> computed;

    public static boolean valid(List<Statement> p_clauses){

        computed = new Hashtable<>();

        HashSet<Statement> clauseSet = new LinkedHashSet<>(p_clauses);

        HashSet<Statement> newClausesSet = new LinkedHashSet<>();

        boolean showlog = false;

        int s = 0;

        do{

            ArrayList<Statement> clauses = new ArrayList<>(clauseSet);

            int totalRs = 0 ;

            for(int i = 0 ; i < clauses.size() ; i ++ ){

                for(int j = i + 1 ; j < clauses.size() ; j ++ ){
                    //création de nouvelles clause en resolvant la paire
                    HashSet<Statement> rsClauses = solve(clauses.get(i), clauses.get(j));
                    totalRs += rsClauses.size();

                    if(showlog) {

                        //?? retirer les clauses resolues ou les garder
                        //ajouter directement les clauses resolues à la fin ?


                        if(!rsClauses.isEmpty()) {
                            System.out.println();
                            System.out.println("RESOLUTION \n CL1 : " + clauses.get(i) + "\n CL2 : " + clauses.get(j));

                            for (Statement clause : rsClauses) {
                                System.out.println(clause);
                            }
                        }
                    }

                    //System.exit(0);

                    //verifie si les nouvelles clause créés contiennent une clause vide
                    for(Statement rsClause : rsClauses){
                        if(rsClause instanceof EmptyStatement){
                            if(showlog)
                            System.out.println("EMPTY");
                            return true;
                        }
                    }

                    //ajout des nouvelles clauses
                    newClausesSet.addAll(rsClauses);
                    //System.out.println("TOTAL "+totalRs);
                    //System.out.println("TOTAL NEW "+newClausesSet.size());

                }

            }

            System.out.println("TOTAL RESULT "+totalRs);
            System.out.println("TOTAL NEW "+newClausesSet.size());

            if(showlog) {
                System.out.println();
                System.out.println("CLAUSE SET");
                for (Statement clause : clauseSet)
                    System.out.println(clause);

                System.out.println();
                System.out.println("NEW CLAUSE SET");
                for (Statement clause : newClausesSet)
                    System.out.println(clause);

            }

            if(clauseSet.containsAll(newClausesSet)){
                //if(showlog) {
                    System.out.println("CONTAIN "+s);
                //}
                return false;
            }

            clauseSet.addAll(newClausesSet);

            System.out.println("TOTAL CLAUSE "+clauseSet.size());

            s++;
/*
            if(s == 2)
                System.exit(0);
*/
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

        if(alreadyCalulated(cl1,cl2) || alreadyCalulated(cl2,cl1)){
            return new LinkedHashSet<>();
        }

        HashSet<Statement> clauses = new LinkedHashSet<>();

        //on boucle sur les enonces (symbole ou négation de symbole) de la premiere table
        for(Statement statement : cl1.getLiterals()){

            Statement opposite = null;

            if(statement instanceof Symbol){
                opposite = new Not(statement);
            }else if (statement instanceof Not){
                opposite = ((Not)statement).getSt1();
            }


            //System.out.println(cl2+" "+opposite);

            //si la clause 2 contient la contraposée

            if(opposite != null && cl2.containOpposite(opposite)){
                //System.out.println("CONTAIN");

                List<Statement> clause = new LinkedList<>();

                for(Statement cl : cl1.getLiterals()){
                    if(!cl.equals(statement))
                        clause.add(cl.clone(true));
                }

                for(Statement cl : cl2.getLiterals()){
                    if(!cl.equals(opposite))
                        clause.add(cl.clone(true));
                }

                if(clause.isEmpty()){
                    clauses.add(new EmptyStatement());
                }else{
                    Statement newOrClause = new Or().getStatement(new LinkedList<Statement>(clause));
                    if(!newOrClause.containOpposite()) {
                        clauses.add(newOrClause);

                    }
                }

            }

        }

        return clauses;

    }

}
