package ai.games.avoidobstacleboat;

import ai.State;
import geometry.Point;
import geometry.PolygonPoint;

public class Vertice extends State<Point>  {

    public Vertice(double x, double y) {
        this.value = new PolygonPoint(x, y);
    }

    public Vertice(Point point){
        this.value = point;
    }

    @Override
    public boolean equals(Object obj) {
        return this.value.equals(((Vertice)obj).getValue());
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
