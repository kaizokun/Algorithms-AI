package ai;

import java.util.List;

public abstract class Plan {

    public abstract Plan getNextPlan(State state);
    public abstract Action getNextAction(State state);

    //public abstract Action getAction();
    /*
    public abstract String showPlan(int i);
    public abstract List<Plan> nextPlans();
    */
}
