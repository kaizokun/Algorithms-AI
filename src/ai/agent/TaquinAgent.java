package ai.agent;

import ai.Goal;
import ai.State;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.local.*;
import ai.explorationStrategy.standard.StandardSearch;
import ai.explorationStrategy.standard.Astar;
import ai.games.taquin.TaquinConfig;
import ai.problem.Problem;
import ai.problem.TaquinProblem;

import java.util.*;

/**
 * Created by monsio on 7/18/17.
 */
public class TaquinAgent extends SimpleSolveProblemAgent {

    public TaquinAgent(State state, Problem problem, Search searchStrategy) {
        super(state, problem, searchStrategy);
    }


    public static void main(String[] args) {

        localSearch();
        //standardSearch();
    }


    private static void localSearch(){

        LocalSearch localSearch = new TabooSearch();
        Problem problem = new TaquinProblem();

        TaquinConfig initialState = (TaquinConfig) problem.rdmState();
        problem.setInitialState(initialState);

        try {

            State rsState = localSearch.search(problem);
            System.out.println("etat initial : "+initialState+" \n" +
                    "etat trouve : "+rsState+"\n" +
                    "noeud testés : "+localSearch.getNodesDeployed()+"\n" +
                    "estimation initiale : "+problem.getGoalCostEstimation(initialState)+"\n" +
                    "estimation finale : "+problem.getGoalCostEstimation(rsState));

        } catch (Search.ExplorationFailedException e) {
            e.printStackTrace();
        }



    }

    private static void standardSearch(){


        Problem problem = new TaquinProblem();

        StandardSearch searchStrategy = new Astar();

        int totalNodes = 0, noSolution = 0, totalTest = 100;

        for(int t = 0 ; t < totalTest ; t ++) {

            TaquinConfig taquinConfig = (TaquinConfig) problem.rdmState();

            TaquinAgent agent = new TaquinAgent(taquinConfig, problem, searchStrategy);

            //System.out.println(taquinConfig);

            try {

                agent.getNextAction(null);
                //agent.start();
                totalNodes += agent.searchStrategy.getNodesDeployed();
                System.out.println(agent.searchStrategy.getNodesDeployed());
            } catch (StandardSearch.ExplorationFailedException e) {
                // System.out.println("NO SOLUTION ");
                noSolution ++;
            }

            //System.out.println(searchStrategy.getNodesDeployed());

        }

        System.out.println("Nombre de problèmes : "+totalTest);
        System.out.println("Probleme non résolus "+noSolution);
        System.out.println("Probleme résolus : "+(totalTest-noSolution));

        if(totalNodes - noSolution > 0 )
            System.out.println("Moyenne : "+(totalNodes / (totalTest - noSolution)));

    }


}
