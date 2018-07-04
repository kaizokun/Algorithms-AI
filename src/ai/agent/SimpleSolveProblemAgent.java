package ai.agent;

import ai.*;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.standard.StandardSearch;
import ai.problem.Problem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by monsio on 7/12/17.
 */
public abstract class SimpleSolveProblemAgent {


    protected State state;
    protected Problem problem;
    protected Object solution;
    protected Goal goal;
    protected Search searchStrategy;

    public SimpleSolveProblemAgent() {
    }

    public SimpleSolveProblemAgent(State state, Problem problem, Search searchStrategy, Goal goal) {
        this.state = state;
        this.problem = problem;
        this.searchStrategy = searchStrategy;
        this.goal = goal;
    }

    public SimpleSolveProblemAgent(State state, Problem problem, Search searchStrategy) {
        this.state = state;
        this.problem = problem;
        this.searchStrategy = searchStrategy;
    }

    public Action getNextAction( Percept percept ) throws Search.ExplorationFailedException{

        this.updateCurrentState(this.state, percept);

        if(this.solution == null || this.solutionIsEmpty()){

            this.formulateGoal(this.state);
            this.formulateProblem(this.state, this.goal);
            this.solution = this.searchStrategy.search(this.problem);

        }

        Action action = this.getNextAction();

        this.updateSolution();

        return action;
    }

    protected boolean solutionIsEmpty() {
        return  ((List) this.solution).isEmpty();
    }


    protected void formulateProblem(State state, Goal goal) {
        this.problem.setGoal(goal);
        this.problem.setInitialState(state);
    }

    protected  void updateCurrentState(State state, Percept percept){
        // throw new UnsupportedOperationException();
    }

    protected  void formulateGoal(State state){}

    protected Action getNextAction() {
        List<Action> solution = (List) this.solution;
        if(!solution.isEmpty())
            return solution.get(0);

        return new NoAction();
    }


    protected void updateSolution() {
        List<Action> solution = (List) this.solution;
        if(!solution.isEmpty())
            solution.remove(0);
    }

    public Problem getProblem() {
        return problem;
    }

    public void start(boolean log) throws StandardSearch.ExplorationFailedException {

        Action nextAction;

       // System.out.println("INITIAL STATE \n"+this.state);

        while ( !((nextAction = this.getNextAction(null)) instanceof NoAction)) {

            if(log) {

                System.out.println("CURRENT STATE \n"+this.state);
                System.out.println("ACTION NAME : " + nextAction.getActionName());

            }

            this.state = this.problem.getResult(this.state, nextAction);

            if( problem.isGoal(this.state) ) {
                System.out.println("GOAL "+problem.getGoalCostEstimation(this.state));
                break;
            }

        }

        System.out.println("FINAL STATE \n"+this.state);

    }

    public int totalNodes(){
        return this.searchStrategy.getNodesDeployed();
    }



}
