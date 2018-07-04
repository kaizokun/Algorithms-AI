package ai.games.avoidobstacleboat;

import ai.Action;
import ai.State;

public class Move extends Action {

    public Move(State result, double cost) {
        super(result, cost);
    }

    @Override
    public String getActionName() {
        return "Move to "+result;
    }
/*
    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public State getResult() {
        return this.result;
    }

    @Override
    public void setResult(State state) {
        this.result = state;
    }
*/
}
