package ai.agent.logic;

import ai.Action;
import ai.agent.logic.expressions.*;
import ai.agent.logic.*;
import ai.agent.logic.expressions.*;
import ai.explorationStrategy.standard.*;

import java.util.LinkedList;
import java.util.List;

public class StatementTest {

    public static void main(String[] args) {

        long t1;

        /*------------------ SYMBOLES ------------------*/

        Symbol P11 = new Symbol("P(1,1)");
        Symbol P12 = new Symbol("P(1,2)");
        Symbol P13 = new Symbol("P(1,3)");
        Symbol P21 = new Symbol("P(2,1)");
        Symbol P22 = new Symbol("P(2,2)");
        Symbol P31 = new Symbol("P(3,1)");
        Symbol B11 = new Symbol("B(1,1)");
        Symbol B21 = new Symbol("B(2,1)");
        Symbol B12 = new Symbol("B(1,2)");

        List<Symbol> symbols = new LinkedList<>();

        symbols.add(P11);
        symbols.add(P12);
        symbols.add(P21);
        symbols.add(P22);
        symbols.add(P13);
        symbols.add(P31);
        symbols.add(B11);
        symbols.add(B21);
        symbols.add(B12);

        /*------------------ REGLES ------------------*/

        Statement R1 = new Not(P11);
        Statement R2 = new DoubleImply(B11, new Or(P12, P21));
        Statement R3 = new DoubleImply(B21, new Or(P11, new Or( P31, P22)));
        Statement R4 = new Not(B11);
        Statement R5 = B21;
        Statement R6 = new DoubleImply(B12, new Or(P11, new Or(P22, P13)));
        Statement R7 = new Not(B12);

        Statement RP = new Not(P31);

        //Statement RP = P22;
        /*------------------ KBpercepts ------------------*/
        System.out.println("KBpercepts");

        List<Statement> KBstatements = new LinkedList<>();

        KBstatements.add(R1);
        KBstatements.add(R2);
        KBstatements.add(R3);
        KBstatements.add(R4);
        KBstatements.add(R5);
        KBstatements.add(R6);
        KBstatements.add(R7);

        KnowledgeBase kb = new KnowledgeBase(KBstatements);

        /*------------------ EPURATION KBpercepts ------------------*/

        System.out.println("EPURATION");

        PropositionalLogicProblem problem = new PropositionalLogicProblem();
        kb = problem.removeUselessStatements(kb, RP);
/*
        System.out.println("KBpercepts");
        for(Statement s : kb.getStatements().values())
            System.out.println(s);
        System.out.println();*/
        Statement KB = kb.getStatement();

        /*-------------------CNF_convert---------------------*/

        CNF_solve(KB, RP);

        /*-------------COMPARAISON DE MODELE-------------*/

        //modelComp(symbols, KBpercepts, RP);

        /*-------------EXPLORATION-------------*/

        //exploration(problem, kb, RP);

    }

    private static void modelComp(List<Symbol> symbols, Statement KB,Statement RP){

        TT_Entails tt_entails = new TT_Entails();
        long t1 = System.currentTimeMillis();

        System.out.println();
        System.out.println(RP+" "+tt_entails.check(KB, RP, symbols));
        System.out.println("TEMPS : "+(System.currentTimeMillis() - t1)+" ms");

    }

    private static void CNF_solve(Statement KB, Statement RP){

        long t1 = System.currentTimeMillis();

        CNF_convert cnf = new CNF_convert();

        Statement KB_A = new And(KB, new Not(RP));

        And CNF = (And) cnf.convert(KB_A);

        System.out.println("TEMPS CONVERSION : " + (System.currentTimeMillis() - t1) + " ms");

        System.out.println(CNF);

        List<Statement> orClauses = CNF.getOrClauses();

        System.out.println();
        System.out.println("CLAUSES");
        System.out.println();

        for(Statement clause : orClauses){
            System.out.println(clause);
        }

        System.out.println();

        System.out.println();

        // System.exit(0);

        PL_Solve pl_solve = new PL_Solve();

        t1 = System.currentTimeMillis();

        System.out.println("SOLVE : "+pl_solve.valid(orClauses));

        System.out.println("TEMPS : " + (System.currentTimeMillis() - t1) + " ms");

        System.out.println();

    }

    private static void exploration(PropositionalLogicProblem problem, KnowledgeBase kb, Statement RP){


        problem.setInitialState(kb);
        problem.setStatementToFind(RP);

        Explore search = new BFS();

        try {

            long t1 = System.currentTimeMillis();
            List<Action> solution = search.search(problem);
            System.out.println("TEMPS : "+(System.currentTimeMillis() - t1)+" ms");

            System.out.println();
            System.out.println("SOLUTION");
            System.out.println();

            System.out.println(kb);
            System.out.println();

            for(Action action : solution) {
                System.out.println(" >>>>>>>>>>>>>>>>>> "+action.getActionName());
                kb = (KnowledgeBase) problem.getResult(kb, action);
                System.out.println(" ================== "+kb);
                System.out.println();
            }

        } catch (StandardSearch.ExplorationFailedException e) {
            System.out.println("PAS DE SOLUTION");
            e.printStackTrace();
        }

    }

}
