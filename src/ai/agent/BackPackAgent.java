package ai.agent;

import ai.Goal;
import ai.State;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.local.*;
import ai.explorationStrategy.standard.*;
import ai.games.backPack.BackPackState;
import ai.games.backPack.Item;
import ai.games.backPack.ParticleSwarmConfigBackPack;
import ai.problem.BackPackProblem;
import ai.problem.EightQueenProblem;
import ai.problem.Problem;
import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;

import java.math.BigDecimal;
import java.util.ArrayList;

public class BackPackAgent extends SimpleSolveProblemAgent {


    public BackPackAgent(State state, Problem problem, StandardSearch searchStrategy) {
        super(state, problem, searchStrategy);
    }


    public BackPackAgent(State state, Problem problem, StandardSearch searchStrategy, Goal goal) {
        super(state, problem, searchStrategy, goal);
    }

    public static void main(String[] args) {


        double maxWeigh = 150;

        ArrayList<Item> allItems = initAllItemsList();

        final Problem problem = new BackPackProblem(allItems, maxWeigh);

        System.out.println("HILL CLIMBING");
        localSearch( problem, new HillClimbing());
        System.out.println();
        System.out.println("TABOO");
        localSearch(problem, new TabooSearch());
        System.out.println();
        System.out.println("RECUIT");
        localSearch(problem,  new SimulatedAnnealing());
        System.out.println();

        long t1 = System.currentTimeMillis();

        System.out.println("SWARM");
        localSearch(problem,  new ParticleSwarm(new ParticleSwarmConfigBackPack()));

        long t2 = System.currentTimeMillis();

        System.out.println(new BigDecimal(t2-t1).divide(new BigDecimal(1000)));


        Goal goal = new Goal() {
            @Override
            public boolean isGoal(State state) {
                //System.out.println( ((BackPackState)state).totalValue() +" "+ getGoal());
                return ((BackPackState)state).totalValue() == (double)getGoal();
            }

            @Override
            public Object getGoal() {
                return 287.0;
            }
        };

        standardSearch(problem, new Astar(), problem.rdmState(), goal);

    }

    private static State localSearch(Problem problem, LocalSearch localSearch){

        State rsState = null;

        int totalTest = 100;
        int totalRsValue = 0;

        for(int i = 0 ; i < totalTest ; i ++ ) {

            BackPackState initialState = (BackPackState) problem.rdmState();

            problem.setInitialState(initialState);

            try {

                rsState = localSearch.search(problem);
/*
                System.out.println("etat initial : " + initialState + " \n" +
                        "etat trouve : " + rsState + "\n" +
                        "noeud testés : " + localSearch.getNodesDeployed() + "\n" +
                        "estimation initiale : " + problem.getGoalCostEstimation(initialState) + "\n" +
                        "estimation finale : " + problem.getGoalCostEstimation(rsState));

                System.out.println();
*/

                totalRsValue += rsState.getScore();

            } catch (Search.ExplorationFailedException e) {
                e.printStackTrace();
            }

        }

        System.out.println("SCORE MOYEN "+(totalRsValue/totalTest));

        return rsState;

    }


    private static void standardSearch(Problem backPackProblem, StandardSearch standardSearch, State initialState, Goal goal){


        BigDecimal totalDeployed = new BigDecimal(0),
                totalAddToFrontier = new BigDecimal(0);

        int cpt = 100;

        try {

            for(int i = 0 ; i < cpt ; i ++) {

                BackPackAgent backPackAgent = new BackPackAgent(initialState, backPackProblem, standardSearch, goal);
                backPackAgent.start(false);
                totalDeployed = totalDeployed.add(new BigDecimal(standardSearch.getNodesDeployed()));
                totalAddToFrontier = totalAddToFrontier.add(new BigDecimal(standardSearch.getNodesAddToFrontier()));
                initialState = backPackProblem.rdmState();
            }

            System.out.println("\nDeployé Moyenne : "+(totalDeployed.divide(new BigDecimal(cpt))));

            System.out.println("Ajouté à la frontière Moyenne : "+(totalAddToFrontier.divide(new BigDecimal(cpt))));


        } catch (StandardSearch.ExplorationFailedException e) {
            e.printStackTrace();
        }

    }

    private static ArrayList<Item> initAllItemsList() {

        ArrayList<Item> items = new ArrayList<>();

        items.add(new Item('A',4,15));//0
        items.add(new Item('B',7,15));//1
        items.add(new Item('C',10,20));//2
        items.add(new Item('D',3,10));//3
        items.add(new Item('E',6,11));//4
        items.add(new Item('F',12,16));//5
        items.add(new Item('G',11,12));//6
        items.add(new Item('H',16,22));//7
        items.add(new Item('I',5,12));//8
        items.add(new Item('J',14,21));//9
        items.add(new Item('K',4,10));//10
        items.add(new Item('L',3,7));//11

        items.add(new Item('M',9,18));//11
        items.add(new Item('N',10,14));//11
        items.add(new Item('O',6,13));//11
        items.add(new Item('P',4,15));//0
        items.add(new Item('Q',7,15));//1
        items.add(new Item('R',10,20));//2
        items.add(new Item('S',3,10));//3
        items.add(new Item('T',6,11));//4
        items.add(new Item('U',12,16));//5
/*
        items.add(new Item('V',11,12));//6
        items.add(new Item('W',16,22));//7
        items.add(new Item('X',5,12));//8
        items.add(new Item('Y',14,21));//9
        items.add(new Item('Z',4,10));//10

        items.add(new Item('0',6,11));//4
        items.add(new Item('1',3,19));//11
        items.add(new Item('2',9,18));//11
        items.add(new Item('3',10,14));//11
        items.add(new Item('4',6,13));//11
        items.add(new Item('5',16,22));//7
        items.add(new Item('6',5,12));//8
        items.add(new Item('7',14,21));//9
        items.add(new Item('8',4,10));//10
        items.add(new Item('9',3,19));//11
*/
        return items;
    }

}
