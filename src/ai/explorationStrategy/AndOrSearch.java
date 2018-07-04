package ai.explorationStrategy;

import ai.*;
import ai.games.vacuum.VacuumEnvironmentState;
import ai.games.vacuum.VacuumEnvironmentStateOfBelief;
import ai.problem.Problem;
import util.Util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class AndOrSearch implements Search {

    private boolean showLog = false;

    @Override
    public int getNodesDeployed() {
        return 0;
    }

    @Override
    public int getNodesAddToFrontier() {
        return 0;
    }

  //  private Hashtable<Long, List<ActionStatePlan>> stateLoops = new Hashtable<>();

    private ArrayList<State> loopStates = new ArrayList<>();
    private ArrayList<List<ActionStatePlan>> loopStatesPlans = new ArrayList();

    private int timeLimit = Integer.MAX_VALUE, startLimit = Integer.MAX_VALUE - 1, endLimit = Integer.MAX_VALUE;

    private void init() {
        loopStates = new ArrayList<>();
        loopStatesPlans = new ArrayList();
    }

    public Plan search(Problem problem) throws ExplorationFailedException {
         System.out.println(problem.getInitialState());

        for(int i = startLimit; i < endLimit ; i ++) {


            try {
                System.out.println("\n============================= LIMIT "+i+" =======================================");
                System.out.println(problem.getInitialState());
                problem.init();
                this.init();
                Plan actionState = this.explorationOr(problem.getInitialState(), problem, 0,0, i, System.currentTimeMillis());
                System.out.println("\n============================= LIMIT SOLUTION "+i+"=============================");

                //for(this.loopStatesPlans)

                return actionState;

            }catch (ExplorationFailedException e){
                System.out.println("\nNO RESULT");
            }catch (LoopException e){
                System.out.println("\nNO RESULT LOOP");
            }catch (TimeExpiredException tee){
                System.out.println("\nTemps Expiré");
            }
        }

        throw new ExplorationFailedException();

    }



    private Plan explorationOr(State state, Problem problem, int i,int k, int depthLimit, long timeLimit) throws ExplorationFailedException, TimeExpiredException, LoopException {

        if(this.showLog) {
            System.out.println(util.Util.getIdent(k) + " OR ");
            System.out.println(util.Util.getIdent(k) + " STATE "+state.toStringb(k)+" "+state.hashCode());
        }

        if(System.currentTimeMillis() - timeLimit > this.timeLimit){
            throw new TimeExpiredException();
        }

        if(i == depthLimit ) {
            if(this.showLog)
                System.out.println(util.Util.getIdent(k)+" LIMIT ");

            throw new ExplorationFailedException();
        }

        if( problem.isGoal(state) ) {
            if(this.showLog)
                System.out.println(util.Util.getIdent(k)+" GOAL "+ state.toStringb(k));
            //return new ActionState(new NoAction(), state);
            return new ActionPlan(new NoAction(), null);
        }

        if(problem.isVisited(state)) {
            if(this.showLog)
                System.out.println(util.Util.getIdent(k)+" LOOP "+ state.toStringb(k)+" DEJA VISITE :  "+problem.getVisitedState().toStringb(k));

            throw new LoopException();
        }

        problem.setStateVisited(state);

        for( Action action : problem.getActions(state)){
            if(this.showLog)
                System.out.println(util.Util.getIdent(k)+" ACTION :  "+action.getActionName());

            List<State> rsStates = problem.getResults(state, action);
            if(rsStates.isEmpty()) {
                if(this.showLog)
                    System.out.println(util.Util.getIdent(k)+" NO RESULT ");

                continue;
            }

            try {

                Plan plan = this.explorationAnd(rsStates, problem, i, k+1, depthLimit, timeLimit);
                ActionPlan actionPlan = new ActionPlan(action, plan);
                //plan.setActionStateList(actionStates);

                //on verifie si des loop ne sont pas associé à cet etat plus en profondeur

                problem.setStateUnVisited(state);
                int id = loopStates.indexOf(state);
                /*
                System.out.println(util.Util.getIdent(k)+"loppstates indexOf : "+id);
                int l = 0 ;
                for(State st : loopStates){
                    if(st.equals(state)){
                        System.out.println(util.Util.getIdent(k)+"loopstates ID "+l);

                    }
                    l++;
                }
*/
                if(id != -1){
                    if(showLog)
                    System.out.println(Util.getIdent(k)+"REPLACE LOOP FOR "+state.toStringb(k));

                    for(ActionStatePlan actionStatePlan : loopStatesPlans.get(id)){
                        actionStatePlan.replaceLoop(state, actionPlan);
                    }

                    loopStates.remove(id);
                    loopStatesPlans.remove(id);

                }



                //avant de retourner le plan pour un etat
/*
                Long key = state.hashKey();

                if(stateLoops.containsKey(key)){
                    //si c'est le cas pour chaque plan contenant une loop pour cet etat on remplace le plan de type loop
                    //par le plan crée pour l'état

                    for(ActionStatePlan actionStatePlan : stateLoops.get(key)){
                        actionStatePlan.replaceLoop(state, actionPlan);
                    }

                    stateLoops.remove(key);
                }
*/

                return actionPlan;

            }catch (ExplorationFailedException efe){
                if(showLog)
                System.out.println(Util.getIdent(k)+"!!!!!!!!!!!!!! ECHEC "+action+" !!!!!!!!!!!!!!");

            }catch (TimeExpiredException tee){
                throw tee;
            }

        }

        int id = loopStates.indexOf(state);
        if(id != -1) {
            loopStates.remove(id);
            loopStatesPlans.remove(id);
        }

        problem.setStateUnVisited(state);

        throw new ExplorationFailedException();

    }

    private Plan explorationAnd(List<State> states, Problem problem, int i,int k, int limit, long timeLimit) throws ExplorationFailedException, TimeExpiredException {

        if(this.showLog)
            System.out.println("\n"+util.Util.getIdent(k)+" AND ");

        //List<ActionState> actionStates = new LinkedList<>();

        List<Plan> plans = new LinkedList<>();
        LinkedList<State> visitedStates = new LinkedList<>();
        boolean loop = true;
        for( State state : states ){
            if(this.showLog)
                System.out.println(util.Util.getIdent(k)+" RS STATE "+ state.toStringb(k));
            //actionStates.add(explorationOr(state, problem, i + 1, limit, timeLimit));

            try {
                Plan noLoopPlan = explorationOr(state, problem, i + 1, k+1, limit, timeLimit);
                plans.add(noLoopPlan);
                loop = false;
            } catch (LoopException e) {
                //Si on retombe sur un etat visité on crée un plan de type Loop qui sera remplacé
                //plus tard quand on associera une action suivit d'un plan a un état
                plans.add(new Loop());
                visitedStates.add(problem.getVisitedState());
                if(showLog)
                    System.out.println(Util.getIdent(k)+"ADD LOOP FOR "+state.toStringb(k));
                //e.printStackTrace();
            }catch (ExplorationFailedException efe){
                throw efe;
            }
            //contingentActions.addActionState(state, plan);
        }

        if(loop){
            throw new ExplorationFailedException();
        }

        if(plans.size() == 1) {
            return plans.get(0);
        }

        ActionStatePlan actionStates = new ActionStatePlan();
        for(int j = 0 ; j < plans.size() ; j ++){
            actionStates.addActionState(states.get(j), plans.get(j));
        }


        for(int j = 0 ; j  < plans.size() ; j ++){
            //si un plan et de type loop
            if(plans.get(j) instanceof Loop){

                State visitedState = visitedStates.removeFirst();
                if(showLog) {
                    System.out.println(Util.getIdent(k) + plans.get(j));
                    System.out.println(visitedState.toStringb(k) + " " + visitedState.hashCode());
                    System.out.println(states.get(j).toStringb(k) + " " + states.get(j).hashCode());
                }
                //on recupere l'index de l'état
                int id = this.loopStates.indexOf(visitedState);

                if(id == -1){
                    //on ajoute l'état
                    this.loopStates.add(visitedState);
                    //on crée la liste de plans loop liés à l'état et ajoute le plan
                    LinkedList<ActionStatePlan> actionStatePlans = new LinkedList<>();
                    actionStatePlans.add(actionStates);
                    //on ajoute la liste au même index que l'état
                    this.loopStatesPlans.add(actionStatePlans);
                }else{
                    //l'état est deja présent on ajoute le plan loop
                    this.loopStatesPlans.get(id).add(actionStates);
                }



                //ancienne methode de gestion des loops avec table de hachage pour identifié un etat
                //ne focntionne pas avec un etat de croyance qui contient plusieurs etats différentes

                //Long key = states.get(j).hashKey();
                //if(!stateLoops.containsKey(key)){
                   // stateLoops.put(key, new LinkedList<ActionStatePlan>());
               // }
               // stateLoops.get(key).add(actionStates);


            }
        }

        return actionStates;

    }

}

class TimeExpiredException extends  Exception{ }
class LoopException extends  Exception{ }

