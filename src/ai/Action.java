package ai;

/**
 * Created by monsio on 7/12/17.
 */
public abstract class Action {

    protected State result;
    protected double cost = 1;

    public Action() { }

    public Action(State result, double cost) {
        this.result = result;
        this.cost = cost;
    }

    public abstract String getActionName();

    public double getCost(){
        return cost;
    }

    public void setCost(double cost){
        this.cost = cost;
    }

    public State getResult(){
        return result;
    }

    public void setResult(State state){
        this.result = state;
    }
}
