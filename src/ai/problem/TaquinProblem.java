package ai.problem;

import ai.Action;
import ai.State;
import ai.games.taquin.TaquinAction;
import ai.games.taquin.TaquinConfig;

import java.util.*;

import static ai.games.taquin.TaquinAction.deltaMov;

/**
 * Created by monsio on 7/18/17.
 */
public class TaquinProblem extends SimpleProblem {


    protected LinkedList<Action> actions = new LinkedList<Action>();

    public TaquinProblem() {
        super();
        this.actions.add(new TaquinAction(TaquinAction.UP));
        this.actions.add(new TaquinAction(TaquinAction.DOWN));
        this.actions.add(new TaquinAction(TaquinAction.RIGHT));
        this.actions.add(new TaquinAction(TaquinAction.LEFT));

    }

    @Override
    public List<Action> getActions(State state) {
        List<Action> actions = new LinkedList<Action>();
        List<Integer> mvs = new ArrayList<>(
                Arrays.asList(
                        new Integer[]{
                                TaquinAction.UP,
                                TaquinAction.DOWN,
                                TaquinAction.RIGHT,
                                TaquinAction.LEFT
                        }));

        for(int mv : mvs) {
            Action action = new TaquinAction(mv);
            State rsState = this.getActionResult(state, action);

            if (rsState != null) {
                action.setResult(rsState);
                actions.add(action);
            }
        }

        return actions;
    }

    protected State getActionResult(State state, Action action) {

        TaquinConfig config = (TaquinConfig) state;
        TaquinAction tAction = (TaquinAction) action;

        int emptyPos = config.getEmptyPos();

        int x = (emptyPos % 3) + deltaMov[tAction.getActionId()][0];

        int y = (emptyPos / 3) + deltaMov[tAction.getActionId()][1];

        int newEmptyPos = (y * 3) + x;

        if( newEmptyPos >= 0 &&  newEmptyPos < 9 ){

            ArrayList<Integer> tabCp = new ArrayList<Integer>((ArrayList<Integer>)config.getValue());

            tabCp.set(emptyPos, tabCp.get(newEmptyPos));
            tabCp.set(newEmptyPos, 0);

            return new TaquinConfig(tabCp);

        }

        return null;

    }

    @Override
    public State getResult(State state, Action action) {

        return action.getResult();
    }

/*


    @Override
    public double getGoalCostEstimation(State state) {

        double est = 0;

        ArrayList<Integer> tab = (ArrayList<Integer>) (state).getObjectRef();

        int i = 0;
        for( Integer v : tab  ) {
            est += Math.abs(v - i);
            i++;
        }

        return est;
    }
*/


    private static int goalPos[][] = new int[][]{{2,2},{0,0},{1,0},{2,0},{0,1},{1,1},{2,1},{0,2},{1,2}};

    @Override
    public double getGoalCostEstimation(State state) {


        //123456780

        double est = 0;

        ArrayList<Integer> tab = (ArrayList<Integer>) (state).getValue();

        int i = 0;
        for( Integer v : tab  ) {
/*
            int x = v % 3;
            int y = v / 3;
*/
            int x = goalPos[v][0];
            int y = goalPos[v][1];

            int x2 = i % 3;
            int y2 = i / 3;

            est += Math.abs(x - x2) + Math.abs(y - y2);

            i++;
        }

        return est;
    }

    @Override
    public State rdmState() {

        ArrayList<Integer> taquin = new ArrayList<Integer>(Arrays.asList(new Integer[]{0,1,2,3,4,5,6,7,8}));

        Collections.shuffle(taquin);

        return new TaquinConfig(taquin);
    }

    @Override
    public boolean isGoal(State state) {

        System.out.println(state);

        TaquinConfig config = (TaquinConfig) state;
        return  config.hashCode() == 123456780 ;

    }
}
