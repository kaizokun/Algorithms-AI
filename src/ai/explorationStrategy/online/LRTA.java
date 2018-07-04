package ai.explorationStrategy.online;

import ai.Action;
import ai.State;
import ai.explorationStrategy.Search;
import ai.problem.EightQueenProblem;
import ai.problem.Problem;
import ai.problem.TaquinProblem;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class LRTA implements Search<List<Action>> {

    @Override
    public int getNodesDeployed() {
        return 0;
    }

    @Override
    public int getNodesAddToFrontier() {
        return 0;
    }

    @Override
    public List<Action> search(Problem problem) throws ExplorationFailedException {

        State currentState = problem.getInitialState(), previousState = null;

        Action previousAction = null;

        Hashtable<State, Hashtable<Action, State>> results = new Hashtable<>();

        Hashtable<State, Double> H = new Hashtable<>();

        LinkedList<Action> solution = new LinkedList<>();

        while (!problem.isGoal(currentState)){

            if(!H.containsKey(currentState)) {

                H.put(currentState, problem.getGoalCostEstimation(currentState));
            }

            if(previousState != null){

                Hashtable<Action,State> rs = results.get(previousState);

                if( rs == null ){

                    rs = new Hashtable<>();

                    results.put(previousState, rs);
                }

                rs.put(previousAction, currentState);

                double minEst = Double.MAX_VALUE, est;

                for(Action action : problem.getActions(previousState)){

                    est = coutLRTA(previousState, action, results.get(previousState).get(action), H, problem);

                    if( est < minEst ){

                        minEst = est;
                    }

                }

                H.put(previousState, minEst);

            }

            Action bestAction = null;

            double minEst = Double.MAX_VALUE, est;

            for(Action action : problem.getActions(currentState)){

                State rState = null;

                Hashtable<Action, State> rs = results.get(currentState);

                if(rs != null) {
                    rState = rs.get(action);
                }

                est = coutLRTA(currentState, action, rState, H, problem);

                if( est < minEst ){

                    minEst = est;

                    bestAction = action;
                }

            }


            solution.add(bestAction);

            previousAction = bestAction;

            previousState = currentState;

            currentState = problem.getResult(currentState, bestAction);
           // System.out.println("ACTION : "+previousAction.getActionName()+"\n STATE : "+previousState);

           // System.out.println("\nRESULT "+currentState);

        }

        return solution;
    }

    private double coutLRTA(State state, Action action, State rsState, Hashtable<State, Double> H, Problem problem) {

        if( rsState == null ){

            return problem.getGoalCostEstimation(state);
        }

        return action.getCost() + H.get(rsState);
    }


    public static void main(String[] args) {

        Problem problem = new EightQueenProblem();
        State currentState = problem.rdmState();
        problem.setInitialState(currentState);

        LRTA lrta = new LRTA();

        try {

            System.out.println(problem.getInitialState());

            List<Action> actions = lrta.search(problem);

            System.out.println(currentState);
            for(Action action : actions){
                currentState = problem.getResult(currentState,action);
                System.out.println(action.getActionName()+" "+currentState);
            }

            System.out.println(actions.size()+" "+problem.isGoal(currentState));

        } catch (ExplorationFailedException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            //e.printStackTrace();
        }

    }


}
