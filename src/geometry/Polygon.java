package geometry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Polygon {

    protected List<Vector> vectors;
    protected List<Vector> internalVectors;

    protected Set<Point> vertices = new HashSet<Point>();

    public Polygon(List<Vector> vectors, List<Vector> internalVectors) {

        this.vectors = vectors;
        this.internalVectors = internalVectors;

        for(Vector vector : vectors){
            vertices.add(vector.p1);
            vertices.add(vector.p2);
            ((PolygonPoint)vector.p1).setPolygon(this);
            ((PolygonPoint)vector.p2).setPolygon(this);
        }

    }

    public List<Vector> getVectors() {
        return vectors;
    }

    public void setVectors(List<Vector> vectors) {
        this.vectors = vectors;
    }

    public Set<Point> getVertices() {
        return vertices;
    }

    public List<Vector> getInternalVectors() {
        return internalVectors;
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "vertices=" + vertices +
                '}';
    }
}
