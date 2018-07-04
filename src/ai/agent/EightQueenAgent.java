package ai.agent;

import ai.State;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.local.*;
import ai.explorationStrategy.standard.Astar;
import ai.explorationStrategy.standard.StandardSearch;
import ai.games.eightqueen.EightQueensConfig;
import ai.problem.EightQueenProblem;
import ai.problem.Problem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class EightQueenAgent extends SimpleSolveProblemAgent{

    public EightQueenAgent(State state, Problem problem, StandardSearch searchStrategy) {
        super(state, problem, searchStrategy);
    }

    public static void main(String[] args) {
/*
        System.out.println("LOCAL");
        System.out.println("HILL CLIMBING");
        localSearch(new HillClimbing());
        System.out.println();
        System.out.println("TABOO");
        localSearch(new TabooSearch());
        System.out.println();
        System.out.println("RECUIT");
        localSearch(new SimulatedAnnealing());
        */

        System.out.println();
        System.out.println("STANDARD");

        //EightQueensConfig config = new EightQueensConfig((ArrayList<Integer>) localRsState.getValue());

        standardSearch();
        //standardSearch(new EightQueensConfig(rdmEightQueenConfig()));

    }

    private static State localSearch( LocalSearch localSearch){

        Problem problem = new EightQueenProblem();
        problem.setInitialState(problem.rdmState());

        State rsState = null;

        int totalTest = 1000;
        int totalScore = 0;

        try {

            for(int i = 0 ; i  < totalTest ; i++) {

                rsState = localSearch.search(problem);
                if(totalTest == 1) {
                    System.out.println("etat initial : " + problem.getInitialState() + " \n" +
                            "etat trouve : " + rsState + "\n" +
                            "noeud testés : " + localSearch.getNodesDeployed() + "\n" +
                            "estimation initiale : " + problem.getGoalCostEstimation(problem.getInitialState()) + "\n" +
                            "estimation finale : " + problem.getGoalCostEstimation(rsState));
                }
                totalScore += -rsState.getScore();
                problem.setInitialState(problem.rdmState());

            }

            System.out.println("Moyenne : "+(totalScore / totalTest));

        } catch (Search.ExplorationFailedException e) {
            e.printStackTrace();
        }



        return rsState;

    }


    private static void standardSearch(){


        StandardSearch searchStrategy = new Astar();

        Problem eightQueenProblem = new EightQueenProblem();

        State initialState = eightQueenProblem.rdmState();

        BigDecimal totalDeployed = new BigDecimal(0),
                totalAddToFrontier = new BigDecimal(0);
        int cpt = 1;

        try {

            for(int i = 0 ; i < cpt ; i ++) {

                EightQueenAgent eightQueenAgent = new EightQueenAgent(initialState, eightQueenProblem, searchStrategy);
                eightQueenAgent.start(true);
                totalDeployed = totalDeployed.add(new BigDecimal(eightQueenAgent.searchStrategy.getNodesDeployed()));
                totalAddToFrontier = totalAddToFrontier.add(new BigDecimal(eightQueenAgent.searchStrategy.getNodesAddToFrontier()));

                //System.out.print((i+1)+", ");
                initialState = eightQueenProblem.rdmState();
            }



            System.out.println("\nTotal Deployé: "+totalDeployed+" - Moyenne : "+(totalDeployed.divide(new BigDecimal(cpt))));

            System.out.println("Total ajouté à la frontière : "+totalAddToFrontier+" - Moyenne : "+(totalAddToFrontier.divide(new BigDecimal(cpt))));


        } catch (StandardSearch.ExplorationFailedException e) {
            e.printStackTrace();
        }

    }


}

