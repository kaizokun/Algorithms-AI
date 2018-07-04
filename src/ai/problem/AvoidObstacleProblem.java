package ai.problem;

import ai.Action;
import ai.State;
import ai.games.avoidobstacleboat.Move;
import ai.games.avoidobstacleboat.Vertice;
import geometry.Point;
import geometry.PolygonPoint;
import geometry.Polygon;
import geometry.Vector;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class AvoidObstacleProblem extends SimpleProblem {

    protected Hashtable<Double, Hashtable<Double,State>> leafs;
    protected Hashtable<Double, HashSet<Double>> visited;
    protected List<Polygon> obstacles;

    public AvoidObstacleProblem(List<Polygon> obstacles) {
        this.obstacles = obstacles;
        this.leafs = new Hashtable<Double, Hashtable<Double,State>>();
        this.visited = new Hashtable<Double, HashSet<Double>>();
    }

    @Override
    public List<Action> getActions(State state) {

       // System.out.println("getActions");

        Vertice vertice = (Vertice) state;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

       // System.out.println(point);

        List<Action> actions = new LinkedList<Action>();

        //Pour chaque polygone
        for(Polygon polygon : obstacles){
          // System.out.println(polygon);
            //pour chaque sommet du polygone
            for(Point pt : polygon.getVertices()){

                if(pt.equals(point))
                    continue;

                this.addAction(actions, point, pt);
                //créer un vecteur partant de l'etat courant au sommet

            }
        }

        this.addAction(actions, point, (PolygonPoint) goal.getGoal());

       // System.exit(0);

        return actions;
    }

    private void addAction(List<Action> actions, Point point, Point pt) {

        Vector way = new Vector(point, pt);

        // System.out.println("--------"+way);

        if(isWay(way)){
            //System.out.println("WAY");
            actions.add(new Move(
                    // new Vertice(way.getP2().getX(), way.getP2().getY()),
                    new Vertice(way.getP2()),
                    PolygonPoint.distance(point,way.getP2())
                    //PolygonPoint.squareDistance(point,way.getP2())
            ));
        }
    }


    private boolean isWay(Vector way){

        //System.out.println("isWay "+obstacles.size());
        //System.out.println(way.getP1()+" "+way.getP2());

        boolean samePolygonForWay = ((PolygonPoint)way.getP1()).getPolygon() != null &&  ((PolygonPoint)way.getP1()).getPolygon().equals(((PolygonPoint)way.getP2()).getPolygon());

       // System.out.println(way.getP1().getPolygon()+" "+way.getP2().getPolygon());

       // System.out.println(samePolygonForWay);

      //  System.out.println("SAME POLY : "+samePolygonForWay);
        if(!samePolygonForWay) {
            for (Polygon polygon1 : obstacles) {
                // System.out.println("POLYGON "+polygon1);
                //Pour chaque vecteur
                for (Vector vector : polygon1.getVectors()) {
                    //si le chemin croise un autre segment
                    // System.out.println(vector);
                    int rs = Vector.crossOrTouch(way, vector);

                    // System.out.println("RS "+rs);

                    if (rs == Vector.CROSS) {
                        //le chemin n'est pas pratiquable
                        return false;
                    }

                }

            }

        }else{

            if (samePolygonForWay ) {
                //System.out.println(way+" "+samePolygonForWay);
                //System.out.println("SAME "+way);
                for (Vector intVector : ((PolygonPoint)way.getP1()).getPolygon().getInternalVectors()) {
                    //  System.out.println(way+" <> "+intVector+" "+(Vector.crossOrTouch(way, intVector)));
                    int rs = Vector.crossOrTouch(way, intVector);
                    if (rs == Vector.CROSS || rs == Vector.SAMEDIRECTION) {
                        return false;
                    }
                }

            }

        }


        return true;
    }

    @Override
    public State getResult(State state, Action action) {
        return action.getResult();
    }

    @Override
    public void setStateVisited(State state) {

        Vertice vertice = (Vertice) state;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

        if(this.visited.containsKey(point.getX())){
            this.visited.get(point.getX()).add(point.getY());
        }else{
            HashSet<Double> Ys = new HashSet<Double>();
            Ys.add(point.getY());
            this.visited.put(point.getX(),Ys);
        }

    }

    @Override
    public boolean isVisited(State rsState) {

        Vertice vertice = (Vertice) rsState;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

        return this.visited.containsKey(point.getX()) && this.visited.get(point.getX()).contains(point.getY());

    }

    @Override
    public boolean isLeaf(State rsState) {

        Vertice vertice = (Vertice) rsState;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

        return this.leafs.containsKey(point.getX()) && this.leafs.get(point.getX()).containsKey(point.getY());

    }

    @Override
    public State getLeaf(State state) {

        Vertice vertice = (Vertice) state;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

        return this.leafs.get(point.getX()).get(point.getY());

    }

    @Override
    public void setIsLeaf(State state) {

        Vertice vertice = (Vertice) state;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

        if(this.leafs.containsKey(point.getX())){
            this.leafs.get(point.getX()).put(point.getY(),state);
        }else{
            Hashtable<Double,State> hashtable = new Hashtable<Double, State>();
            hashtable.put(point.getY(),state);
            this.leafs.put(point.getX(),hashtable);
        }

    }

    @Override
    public void init() {
        this.leafs.clear();
        this.visited.clear();
    }

    @Override
    public double getGoalCostEstimation(State state) {
        return PolygonPoint.distance((PolygonPoint)state.getValue(), (PolygonPoint)goal.getGoal());

        // return PolygonPoint.squareDistance((PolygonPoint)state.getObjectRef(), (PolygonPoint)((Vertice)goal.getGoal()).getObjectRef());
    }

    @Override
    public void setStateUnVisited(State state) {

        Vertice vertice = (Vertice) state;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

        this.visited.get(point.getX()).remove(point.getY());
/*
        if(this.visited.get(point.getX()).isEmpty())
            this.visited.remove(point.getX());
*/
    }

    @Override
    public void setIsNotLeaf(State state) {

        Vertice vertice = (Vertice) state;

        PolygonPoint point = (PolygonPoint) vertice.getValue();

        this.leafs.get(point.getX()).remove(point.getY());
/*
        if(this.leafs.get(point.getX()).isEmpty())
            this.leafs.remove(point.getX());
*/
    }



}
