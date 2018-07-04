package ai;

import ai.games.vacuum.VacuumEnvironmentState;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ActionStatePlan extends Plan {


    private List<Plan> plans = new ArrayList<>();
    private List<State> states = new ArrayList<>();

    @Override
    public Plan getNextPlan(State state) {

        int i = states.indexOf(state);
        if(i != -1 )
            return plans.get(i).getNextPlan(null);
        return null;

    }

    @Override
    public Action getNextAction(State state) {

        System.out.println("GET NEXT ACTION "+toString());

        Plan plan = null;
        int i = states.indexOf(state);
        if(i != -1)
            plan = plans.get(i);

        if(plan == null)
            return new NoAction();

        return plan.getNextAction(null);

    }

    public void addActionState(State state, Plan plan){

        this.states.add(state);
        this.plans.add(plan);

    }

    @Override
    public  String toString() {

        String rs = "";

        int i = 0;
        for(State state : states){
            rs+=" SI";
            rs+=" ETAT = "+ state;
            rs+=" ALORS : ";
            rs+=" "+ plans.get(i);
            i++;
        }

        return rs;
    }


    public void replaceLoop(State state, ActionPlan actionPlan) {

        for(int i = 0 ; i < states.size() ; i ++) {

            if(states.get(i).equals(state)){
                plans.set(i, actionPlan);
            }

        }

    }



/*

//Ancienne version avec table de hashage

    private Hashtable<Long, Plan> stateActions = new Hashtable<>();
    private Hashtable<Long, State> states = new Hashtable<>();


    @Override
    public Plan getNextPlan(State state) {

        long key = state.hashKey();
        if(stateActions.containsKey(key))
            return stateActions.get(key).getNextPlan(null);
        return null;


    }

    @Override
    public Action getNextAction(State state) {

        Plan plan = stateActions.get(state.hashKey());

        if(plan == null)
            return new NoAction();

        return plan.getNextAction(null);

    }

    public void addActionState(State state, Plan plan){
        this.stateActions.put(state.hashKey(), plan);
        this.states.put(state.hashKey(), state);
    }

    @Override
    public  String toString() {

        String rs = "";

        for(Long key : stateActions.keySet()){
            rs+=" SI";
            rs+=" ETAT = "+ states.get(key);
            rs+=" ALORS : ";
            rs+=" "+ stateActions.get(key) ;
        }


        return rs;
    }


    public void replaceLoop(State state, ActionPlan actionPlan) {

        long key = state.hashKey();
        stateActions.put(key,actionPlan);

    }


*/



}
