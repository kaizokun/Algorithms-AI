package ai.explorationStrategy.online;

import ai.Action;
import ai.NoAction;
import ai.State;
import ai.explorationStrategy.Search;
import ai.problem.EightQueenProblem;
import ai.problem.Problem;
import ai.problem.TaquinProblem;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OnlineDFS implements Search<LinkedList<Action>> {


    @Override
    public int getNodesDeployed() {
        return 0;
    }

    @Override
    public int getNodesAddToFrontier() {
        return 0;
    }

    @Override
    public LinkedList<Action> search(Problem problem) throws ExplorationFailedException {

        State currentState = problem.getInitialState();

        State previousState = null;

        Action previousAction = null;

        Hashtable<State,Hashtable<Action,State>> results = new Hashtable<>();

        Hashtable<State, List<Action>> notTriedActions = new Hashtable<>();

        Hashtable<State,LinkedList<State>> notRevisited = new Hashtable<>();

        LinkedList<Action> solution = new LinkedList<>();

        while(!problem.isGoal(currentState)){

            if(!notTriedActions.containsKey(currentState))
                notTriedActions.put(currentState, problem.getActions(currentState));

            if(previousState != null) {

                if (!results.containsKey(previousState)) {
                    Hashtable<Action, State> rs = new Hashtable<>();
                    rs.put(previousAction, currentState);
                    results.put(previousState, rs);
                } else {
                    results.get(previousState).put(previousAction, currentState);
                }

                if (!notRevisited.containsKey(currentState)) {
                    LinkedList<State> nr = new LinkedList<>();
                    nr.add(previousState);
                    notRevisited.put(currentState, nr);
                } else {
                    notRevisited.get(currentState).add(previousState);
                }
            }

            if(notTriedActions.get(currentState).isEmpty()){

                if(notRevisited.get(currentState).isEmpty()){
                    solution.add(new NoAction());
                    break;
                }

                State notRevisitedState = notRevisited.get(currentState).removeLast();

                for(Map.Entry<Action,State> actionState : results.get(currentState).entrySet()){
                    if( actionState.getValue().equals(notRevisitedState) ) {
                        solution.add(actionState.getKey());
                        break;
                    }
                }

            }else{
                solution.add(notTriedActions.get(currentState).remove(0));
            }

            previousAction = solution.getLast();
            previousState = currentState;
            currentState = problem.getResult(currentState, previousAction);

            //System.out.println(previousState+" "+previousAction.getActionName()+" "+currentState);

        }

        return solution;
    }

    public static void main(String[] args) {

        Problem problem = new TaquinProblem();
        State currentState = problem.rdmState();
        problem.setInitialState(currentState);

        OnlineDFS onlineDFS = new OnlineDFS();

        try {

            List<Action> actions = onlineDFS.search(problem);

            System.out.println(currentState);
            for(Action action : actions){
                currentState = problem.getResult(currentState,action);
                System.out.println(action.getActionName()+" "+currentState);
            }

            System.out.println(actions.size()+" "+problem.isGoal(currentState));

        } catch (ExplorationFailedException e) {
            e.printStackTrace();
        }

    }


}
