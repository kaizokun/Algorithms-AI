package ai.agent;

import ai.Goal;
import ai.State;
import ai.explorationStrategy.standard.Astar;
import ai.explorationStrategy.standard.DFS;
import ai.explorationStrategy.standard.StandardSearch;
import ai.games.avoidobstacleboat.Vertice;
import ai.problem.AvoidObstacleProblem;
import ai.problem.Problem;
import geometry.PolygonPoint;
import geometry.Polygon;
import geometry.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AvoidObstaclesAgent extends SimpleSolveProblemAgent {


    public AvoidObstaclesAgent(State state, Problem problem, StandardSearch searchStrategy, Goal goal) {
        super(state, problem, searchStrategy, goal);

    }

    public static void main(String[] args) {

        Goal goal = new Goal() {
            @Override
            public boolean isGoal(State state) {
                Vertice vertice = (Vertice)state;
                return (vertice.getValue()).equals(getGoal());
            }

            @Override
            public Object getGoal() {
                return new PolygonPoint(24,12);
            }
        };

        List<Polygon> obstacles = initObstacles();

        State initialState = new Vertice(0,0);

        Problem problem = new AvoidObstacleProblem(obstacles);

        StandardSearch searchStrategy = new DFS();

        AvoidObstaclesAgent avoidObstaclesAgent =  new AvoidObstaclesAgent(initialState, problem, searchStrategy, goal);

        try {
            avoidObstaclesAgent.start(true);
            System.out.println(avoidObstaclesAgent.searchStrategy.getNodesDeployed());
            System.out.println();
            avoidObstaclesAgent.searchStrategy = new Astar();
            avoidObstaclesAgent.start(true);
            System.out.println(avoidObstaclesAgent.searchStrategy.getNodesDeployed());

        } catch (StandardSearch.ExplorationFailedException e) {
            e.printStackTrace();
        }

    }

    private static List<Polygon> initObstacles() {


        List<Polygon> obstacles = new ArrayList<Polygon>();

        List<Vector> polygon1 = new ArrayList<Vector>(), polygonInt1 = new ArrayList<Vector>();
        List<Vector> polygon2 = new ArrayList<Vector>(), polygonInt2 = new ArrayList<Vector>();
        List<Vector> polygon3 = new ArrayList<Vector>();
        List<Vector> polygon4 = new ArrayList<Vector>(), polygonInt4 = new ArrayList<Vector>();
        List<Vector> polygon5 = new ArrayList<Vector>();
        List<Vector> polygon6 = new ArrayList<Vector>(), polygonInt6 = new ArrayList<Vector>();
        List<Vector> polygon7 = new ArrayList<Vector>(), polygonInt7 = new ArrayList<Vector>();
        List<Vector> polygon8 = new ArrayList<Vector>(), polygonInt8 = new ArrayList<Vector>();


        //A
        polygon1.add(new Vector(new PolygonPoint(2,5),new PolygonPoint(1,7)));
        polygon1.add(new Vector(new PolygonPoint(1,7),new PolygonPoint(4,10)));
        polygon1.add(new Vector(new PolygonPoint(4,10),new PolygonPoint(6,8)));

        //TRiangle
       // polygon1.add(new Vector(new PolygonPoint(6,8),new PolygonPoint(2,5)));
        polygon1.add(new Vector(new PolygonPoint(6,8),new PolygonPoint(5,4)));
        polygon1.add(new Vector(new PolygonPoint(5,4),new PolygonPoint(2,5)));
        //AI
        polygonInt1.add(new Vector(new PolygonPoint(4,10),new PolygonPoint(2,5)));
        polygonInt1.add(new Vector(new PolygonPoint(4,10),new PolygonPoint(5,4)));

        //B
        polygon2.add(new Vector(new PolygonPoint(3,3),new PolygonPoint(3,1)));
        polygon2.add(new Vector(new PolygonPoint(3,3),new PolygonPoint(12,3)));
        polygon2.add(new Vector(new PolygonPoint(12,1),new PolygonPoint(12,3)));
        polygon2.add(new Vector(new PolygonPoint(12,1),new PolygonPoint(3,1)));
        //BI
        polygonInt2.add(new Vector(new PolygonPoint(3,3),new PolygonPoint(12,1)));

        //C
        polygon3.add(new Vector(new PolygonPoint(8,9),new PolygonPoint(9,4)));
        polygon3.add(new Vector(new PolygonPoint(8,9),new PolygonPoint(7,4)));
        polygon3.add(new Vector(new PolygonPoint(7,4),new PolygonPoint(9,4)));

        //D
        polygon4.add(new Vector(new PolygonPoint(10,10),new PolygonPoint(12,10)));
        polygon4.add(new Vector(new PolygonPoint(10,10),new PolygonPoint(10,7)));
        polygon4.add(new Vector(new PolygonPoint(14,9),new PolygonPoint(10,7)));
        polygon4.add(new Vector(new PolygonPoint(14,9),new PolygonPoint(12,10)));
        //DI
        polygonInt4.add(new Vector(new PolygonPoint(10,10),new PolygonPoint(14,9)));

        //E
        polygon5.add(new Vector(new PolygonPoint(12,6),new PolygonPoint(13,2)));
        polygon5.add(new Vector(new PolygonPoint(12,6),new PolygonPoint(16,4)));
        polygon5.add(new Vector(new PolygonPoint(16,4),new PolygonPoint(13,2)));

        //F
        polygon6.add(new Vector(new PolygonPoint(15,12),new PolygonPoint(19,12)));
        polygon6.add(new Vector(new PolygonPoint(15,12),new PolygonPoint(15,6)));
        polygon6.add(new Vector(new PolygonPoint(19,6),new PolygonPoint(19,12)));
        polygon6.add(new Vector(new PolygonPoint(19,6),new PolygonPoint(15,6)));
        //FI
        polygonInt6.add(new Vector(new PolygonPoint(15,12),new PolygonPoint(19,6)));

        //G
        polygon7.add(new Vector(new PolygonPoint(20,5),new PolygonPoint(18,4)));
        polygon7.add(new Vector(new PolygonPoint(20,5),new PolygonPoint(22,4)));
        polygon7.add(new Vector(new PolygonPoint(18,4),new PolygonPoint(18,2)));
        polygon7.add(new Vector(new PolygonPoint(22,4),new PolygonPoint(22,2)));
        polygon7.add(new Vector(new PolygonPoint(20,1),new PolygonPoint(22,2)));
        polygon7.add(new Vector(new PolygonPoint(20,1),new PolygonPoint(18,2)));
        //GI
        polygonInt7.add(new Vector(new PolygonPoint(20,5),new PolygonPoint(18,2)));
        polygonInt7.add(new Vector(new PolygonPoint(20,5),new PolygonPoint(20,1)));
        polygonInt7.add(new Vector(new PolygonPoint(20,5),new PolygonPoint(22,2)));

        //H
        polygon8.add(new Vector(new PolygonPoint(22,12),new PolygonPoint(20,11)));
        polygon8.add(new Vector(new PolygonPoint(22,12),new PolygonPoint(23,11)));
        polygon8.add(new Vector(new PolygonPoint(22,6),new PolygonPoint(20,11)));
        polygon8.add(new Vector(new PolygonPoint(22,6),new PolygonPoint(23,11)));
        //HI
        polygonInt8.add(new Vector(new PolygonPoint(22,12),new PolygonPoint(22,6)));

        obstacles.add(new Polygon(polygon1, polygonInt1));
        obstacles.add(new Polygon(polygon2, polygonInt2));
        obstacles.add(new Polygon(polygon3, new LinkedList<Vector>()));
        obstacles.add(new Polygon(polygon4, polygonInt4));
        obstacles.add(new Polygon(polygon5, new LinkedList<Vector>()));
        obstacles.add(new Polygon(polygon6, polygonInt6));
        obstacles.add(new Polygon(polygon7, polygonInt7));
        obstacles.add(new Polygon(polygon8, polygonInt8));


        return obstacles;

    }


}
