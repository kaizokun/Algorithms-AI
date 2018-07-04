package ai.datastructure;

import java.math.BigDecimal;
import java.util.Hashtable;

/**
 * Created by monsio on 7/16/17.
 */
public class Map {

    private GraphNode root;

    private Hashtable<Object,GraphNode> nodes = new Hashtable<Object, GraphNode>();
    private Hashtable<Object, CoordonateGps> coordinates = new Hashtable<Object, CoordonateGps>();
/*
    public GraphNode getRoot() {
        return root;
    }

    public void setRoot(GraphNode root) {
        this.root = root;

        if(!nodes.contains(root.getObjectRef()))
            nodes.put(root.getObjectRef(),root);
    }
*/
    public Hashtable<Object, GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(Hashtable<Object, GraphNode> nodes) {
        this.nodes = nodes;
    }

    public Map addNode(String value){
        this.nodes.put(value, new GraphNode(value));
        return this;
    }

    public Map addArc(String source, String destination, int weigh) {

        GraphNode sourceNode = this.nodes.get(source);
        GraphNode destinationNode = this.nodes.get(destination);

        if(sourceNode == null) {
            sourceNode =  new GraphNode(source);
            this.nodes.put(source, sourceNode);
        }

        if(destinationNode == null) {
            destinationNode = new GraphNode(destination);
            this.nodes.put(destination, destinationNode);
        }

        sourceNode.addArc(destinationNode,weigh);


        return this;

    }

    public void addCoordinate(String city, double latitude, double longitude){
        this.coordinates.put(city, new CoordonateGps(latitude,longitude));
    }

    public double getSLD(Object city1, Object city2){
        //System.out.println(city1+" "+city2);

        CoordonateGps city1Coo = this.coordinates.get(city1);
        CoordonateGps city2Coo = this.coordinates.get(city2);

        BigDecimal latDelta = city1Coo.latitude.subtract(city2Coo.latitude);
        BigDecimal longDelta = city1Coo.longitude.subtract(city2Coo.longitude);

       return Math.sqrt(latDelta.pow(2).add(longDelta.pow(2)).doubleValue());

    }

    public double getSLDKM(Object c1, Object c2){
        //System.out.println(c1+" "+c2+" "+(getSLD(c1, c2)));
        double ratio = 423.0 / getSLD("Arad","Bucarest");
        return getSLD(c1, c2) * ratio;
    }

    private class CoordonateGps{
        private BigDecimal longitude, latitude;

        public CoordonateGps(double latitude, double longitude){
            this.longitude = new BigDecimal(longitude);
            this.latitude = new BigDecimal(latitude);
        }

        public BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }

        public BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }
    }

    public GraphNode getNode(String key){
        return this.nodes.get(key);
    }

}
