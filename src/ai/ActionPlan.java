package ai;


public class ActionPlan extends Plan {

    private Plan nextPlan;
    private Action action;

    public ActionPlan(Action action, Plan nextPlan) {
        this.action = action;
        this.nextPlan = nextPlan;
    }

    @Override
    public Plan getNextPlan(State state) {
        return this.nextPlan;
    }

    @Override
    public Action getNextAction(State state) {
        //System.out.println("GET NEXT ACTION "+toString());
        return this.action;
    }

    @Override
    public String toString() {
        return " "+action;
    }

    /*
    @Override
    public Action getAction() {
        return this.action;
    }

    @Override
    public String showPlan(int i) {

        return Util.getIdent(i)+" "+action.getActionName();
    }

    @Override
    public List<Plan> nextPlans() {
        LinkedList<Plan> plans = new LinkedList<>();
        plans.add(nextPlan);
        return plans;
    }
*/

}
