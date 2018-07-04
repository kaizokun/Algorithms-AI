package ai;

public class Loop extends Plan {

    @Override
    public Plan getNextPlan(State state) {
        return null;
    }

    @Override
    public Action getNextAction(State state) {
        return new NoAction();
    }
}
