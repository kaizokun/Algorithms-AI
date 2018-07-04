package ai.datastructure;

import ai.Action;
import ai.State;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by monsio on 7/12/17.
 */
public class GraphNode extends State<String>{


    protected List<Action> arcs = new LinkedList<Action>();


    public GraphNode(String value, GraphNode parent, double weigh){
        this.value = value;
        parent.addArc(this, weigh);
    }

    public GraphNode(GraphNode parent, String value) {
        this(value,parent,1);
    }

    public GraphNode(String value){
        this.value = value;
    }

    public void addArc(GraphNode destination, double weigh){
        Arc arc = new Arc(destination, weigh);
        Arc arc2 = new Arc(this, weigh);
        this.arcs.add(arc);
        destination.arcs.add(arc2);
    }

    public List<Action> getArcs() {
        return arcs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphNode)) return false;

        GraphNode node = (GraphNode) o;

        return getValue().equals(node.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        return "GraphNode{" +
                "isTrue=" + value +
                '}';
    }

}

