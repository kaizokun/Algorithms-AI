package ai.games.taquin;

import ai.Action;
import ai.State;

/**
 * Created by monsio on 7/18/17.
 */
public class TaquinAction extends Action {

    public static final int [][] deltaMov = new int[][]{{0,-1},{0,1},{1,0},{-1,0}};

    public static final int UP = 0, DOWN = 1, RIGHT = 2, LEFT = 3;

    public static final String[] moveLabels = new String[]{"UP","DOWN","RIGHT","LEFT"};

    protected int actionId;

    public TaquinAction(int actionId) {
        this.actionId = actionId;
    }

    @Override
    public String getActionName() {
        return moveLabels[actionId];
    }
/*
    @Override
    public double getCost() {
        return 1;
    }

    @Override
    public void setCost(double cost) {}
*/
    public int getActionId() {
        return actionId;
    }
}
