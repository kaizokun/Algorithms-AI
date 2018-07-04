package ai.agent;

import ai.Goal;
import ai.State;
import ai.datastructure.Map;
import ai.explorationStrategy.standard.StandardSearch;
import ai.explorationStrategy.standard.Astar;
import ai.problem.FindWayProblemGraph;
import ai.problem.Problem;

/**
 * Created by monsio on 7/12/17.
 */
public class FindWayAgent extends SimpleSolveProblemAgent {

    public FindWayAgent(State state, Problem problem, StandardSearch explorer, Goal goal) {
        super(state, problem, explorer, goal);
    }


    public static void main(String[] args) {

        Map map = loadRomanianMap();

        Goal goal = new Goal() {
            @Override
            public boolean isGoal(State state) {
                return state.getValue().equals(getGoal());
            }

            @Override
            public Object getGoal() {
                return "Bucarest";
            }
        };

        Problem findWayProblemGraph = new FindWayProblemGraph(map);

        StandardSearch searchStrategy = new Astar();

        FindWayAgent findWayAgent = new FindWayAgent(map.getNode("Arad"), findWayProblemGraph, searchStrategy, goal);

        try {
            findWayAgent.start(true);
        } catch (StandardSearch.ExplorationFailedException e) {
            e.printStackTrace();
        }
        //System.out.println("END");

    }


    public static Map loadRomanianMap(){

        Map romaniaMap = new Map();

        romaniaMap.addNode("Arad")
                .addArc("Arad","Sibiu",140)
                .addArc("Arad","Zerind",75)
                .addArc("Arad","Timisiora",118)
                .addArc("Zerind","Oradea",71)
                .addArc("Oradea","Sibiu",151)
                .addArc("Sibiu","Fagaras",99)
                .addArc("Sibiu","Rimnicu Vilcea",80)
                .addArc("Rimnicu Vilcea","Pitesti",97)
                .addArc("Pitesti","Bucarest",101)
                .addArc("Fagaras","Bucarest",211)
                .addArc("Bucarest","Giurgiu",90)
                .addArc("Bucarest","Urziceni",85)
                .addArc("Lugoj","Timisiora",111)
                .addArc("Lugoj","Mehadia",70)
                .addArc("Drobeta","Mehadia",75)
                .addArc("Drobeta","Craiova",120)
                .addArc("Craiova","Rimnicu Vilcea",146)
                .addArc("Craiova","Pitesti",138)
                .addArc("Hirsova","Urziceni",98)
                .addArc("Hirsova","Eforie",86)
                .addArc("Urziceni","Vaslui",142)
                .addArc("Vaslui","Lasi",92)
                .addArc("Neamt","Lasi",87);

        romaniaMap.addCoordinate("Arad",46.186561,21.312268);
        romaniaMap.addCoordinate("Sibiu",45.833201,24.131561);
        romaniaMap.addCoordinate("Oradea",47.093688,21.961853);
        romaniaMap.addCoordinate("Timisiora",45.772504,21.247742);
        romaniaMap.addCoordinate("Bucarest",44.426767,26.102538);
        romaniaMap.addCoordinate("Zerind", 46.62251 ,  21.517419);
        romaniaMap.addCoordinate("Fagaras",45.84164, 24.973095);
        romaniaMap.addCoordinate("Rimnicu Vilcea", 45.099675,24.369318 );
        romaniaMap.addCoordinate("Pitesti",44.85648, 24.869182);
        romaniaMap.addCoordinate("Giurgiu",43.903708, 25.969926);
        romaniaMap.addCoordinate("Urziceni",44.716532, 26.641121);
        romaniaMap.addCoordinate("Lugoj",45.69099, 21.903461);
        romaniaMap.addCoordinate("Mehadia",44.904114, 22.364516);
        romaniaMap.addCoordinate("Drobeta",44.636923, 22.659734);
        romaniaMap.addCoordinate("Craiova",44.330179, 23.794881);
        romaniaMap.addCoordinate("Hirsova",44.689348, 27.945655);
        romaniaMap.addCoordinate("Vaslui",46.640692, 27.727647);
        romaniaMap.addCoordinate("Lasi",47.158455, 27.601442);
        romaniaMap.addCoordinate("Neamt",46.975869,  26.381876);
        romaniaMap.addCoordinate("Eforie",44.058422,  28.633607);


        return romaniaMap;

    }

}
