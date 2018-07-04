package ai;

/**
 * Created by monsio on 7/12/17.
 */
public interface Goal {

    boolean isGoal(State state);
    Object getGoal();
}
