package ai.datastructure;

import ai.Action;
import ai.State;

/**
 * Created by monsio on 7/12/17.
 */
public class Arc extends Action {


    public Arc(GraphNode destination, double cost) {
        super(destination, cost);
    }

    @Override
    public String getActionName() {
        return " Aller de "+result.getSource().getValue()+" à "+result.getValue()+", distance : "+cost+" Km";
    }

    @Override
    public void setResult(State state) {

    }

    @Override
    public String toString() {
        return "Arc{" +
                "destination=" + result +
                ", cost=" + cost +
                '}';
    }
}
