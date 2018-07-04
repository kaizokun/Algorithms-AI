package ai.problem;

import ai.Action;
import ai.State;
import ai.games.vacuum.VacuumAction;
import ai.games.vacuum.VacuumActionValue;
import ai.games.vacuum.VacuumEnvironmentState;

import ai.games.vacuum.VacuumEnvironmentStateOfBelief;

import java.util.*;

public class NotDeterministicPartiallyObservableVacuumProblem extends VacuumProblem {

    protected VacuumEnvironmentState vacuumEnvironmentState;

    protected List<VacuumEnvironmentStateOfBelief> visitedStatesOfBelief = new LinkedList<>();

    public VacuumEnvironmentState getVacuumEnvironmentState() {
        return vacuumEnvironmentState;
    }

    public NotDeterministicPartiallyObservableVacuumProblem(State initialState, int xLimit, int yLimit) {
        super(initialState, xLimit, yLimit);
    }

    public NotDeterministicPartiallyObservableVacuumProblem(int xLimit, int yLimit) {
        super(xLimit, yLimit);
    }


    @Override
    public List<Action> getActions(State state) {
        return super.getActions(state);
    }


    public void getRealResult(VacuumEnvironmentState realVacuumEnvironmentState,
                              VacuumEnvironmentStateOfBelief stateOfBelief,
                              VacuumEnvironmentStateOfBelief stateOfBeliefRs, Action action) {

        VacuumAction vacuumAction = (VacuumAction) action;

        //VacuumEnvironmentState vacuumState = (VacuumEnvironmentState) state;
        //VacuumEnvironmentState vacuumStateClone = vacuumState.clone();

        Random random = new Random();

        LinkedHashSet<VacuumEnvironmentState> rsStates = new LinkedHashSet<>();

        //un deplacement ici ne produit qu'un seul resultat
        if(vacuumAction.getActionValue() != VacuumActionValue.ASPIRE ) {

            double ratioGliss = 0.3;

            if(random.nextDouble() < ratioGliss) {
                //System.out.println("GLISSE");
                for(VacuumEnvironmentState vacuumEnvironmentState : stateOfBelief.getStates()) {
                    rsStates.add(vacuumEnvironmentState.clone());
                }

            }else{
                try {
                    //System.out.println("DEPLACEMENT");
                    for(VacuumEnvironmentState vacuumEnvironmentState : stateOfBelief.getStates()) {

                        VacuumEnvironmentState vacuumStateClone = vacuumEnvironmentState.clone();
                        vacuumStateClone.moveVacuum(vacuumAction, xLimit, yLimit);
                        rsStates.add(vacuumStateClone);
                    }

                    realVacuumEnvironmentState.moveVacuum(vacuumAction,xLimit,yLimit);

                    //les etats de l'etat de croyance fournissant le même percept sont donc tous au mêê endroit
                    //par consequent si un deplecement est interdit pour l'un il l'est pour tous
                } catch (VacuumActionValue.CannotMoveException e) { }
            }


        }else {

            //La position de l'aspirateur est sale
            //Tout les etats de l'etat de croyance fournissent le même percept
            if (stateOfBelief.getStates().get(0).isVacummPositionDirty(xLimit)) {

                //probabilité de tout nettoyer
                double probaCleanAll = 0.33;

                //si les chances de nettoyer toutes les cases alentours sont inferieur au ratio
                if (random.nextDouble() < probaCleanAll) {
                    //System.out.println("NETTOIE TOUT");
                    for (VacuumEnvironmentState vacuumEnvironmentState : stateOfBelief.getStates()) {
                        VacuumEnvironmentState vacuumStateClone = vacuumEnvironmentState.clone();
                        vacuumStateClone.aspireCurrentAndAroundVacummPos(xLimit, yLimit);
                        rsStates.add(vacuumStateClone);
                    }

                    realVacuumEnvironmentState.aspireCurrentAndAroundVacummPos(xLimit, yLimit);

                } else {
                    //System.out.println("NETTOIE SIMPLE");
                    for (VacuumEnvironmentState vacuumEnvironmentState : stateOfBelief.getStates()) {
                        VacuumEnvironmentState vacuumStateClone = vacuumEnvironmentState.clone();
                        vacuumStateClone.aspireCurrentVacummPos(xLimit);
                        rsStates.add(vacuumStateClone);
                    }
                    realVacuumEnvironmentState.aspireCurrentVacummPos(xLimit);
                }

            }else {
/*
                double probaAddDirt = 0.25;

                if (random.nextDouble() < probaAddDirt) {
                    // System.out.println("AJOUT POUSSIERE");
                    for (VacuumEnvironmentState vacuumEnvironmentState : stateOfBelief.getStates()) {
                        VacuumEnvironmentState vacuumStateClone = vacuumEnvironmentState.clone();
                        vacuumStateClone.addDirtOnVacuumPosition(xLimit);
                        rsStates.add(vacuumStateClone);
                    }
                    realVacuumEnvironmentState.addDirtOnVacuumPosition(xLimit);
                }
*/
            }

        }


        for(VacuumEnvironmentState state : rsStates)
            stateOfBeliefRs.addState(state);


    }


    @Override
    public State getResult(State state, Action action) {

        //System.out.println("GET RESULT STATE OF BELIEF : "+state+" - ACTION : "+action);

        //this.vacuumEnvironmentState = (VacuumEnvironmentState) super.getResult(this.vacuumEnvironmentState, action);


        VacuumEnvironmentStateOfBelief stateOfBelief = (VacuumEnvironmentStateOfBelief) state;
        VacuumEnvironmentStateOfBelief stateOfBeliefRs = new VacuumEnvironmentStateOfBelief();

       // System.out.println("ENVIRONMENT STATE "+this.vacuumEnvironmentState);
        //application de l'action à tout les etats de l'etat de croyance
        this.getRealResult(this.vacuumEnvironmentState, stateOfBelief, stateOfBeliefRs, action);
       // System.out.println("ENVIRONMENT STATE "+this.vacuumEnvironmentState);
        //récupération de l' identifiant du percept apres avoir appliqué l'action à l'environnement reel
        int perceptId = this.vacuumEnvironmentState.getPerceptId(xLimit);
       // System.out.println("ENVIRONMENT STATE PERCEPT : "+perceptId);

        List<VacuumEnvironmentState> rmStates = new LinkedList<>();
        //pour chaque etat de l'etat de croyance resultant de l'action
        //si le percept ne correspondant pas à celui renvoyé par l'environnement reel
        //on enregistre l'etat correspondant pour suppression
        //System.out.println("APPLY ACTION ON STATE OF BELIEF : "+stateOfBeliefRs.getStates().size());
        for(VacuumEnvironmentState vacuumEnvironmentState : stateOfBeliefRs.getStates()){
            //System.out.println("STATE : "+vacuumEnvironmentState);
            //System.out.println("PERCEPT KEY : "+vacuumEnvironmentState.getPerceptId(xLimit));
            if(vacuumEnvironmentState.getPerceptId(xLimit) != perceptId){
                rmStates.add(vacuumEnvironmentState);
            }
        }

        stateOfBeliefRs.getStates().removeAll(rmStates);

        return stateOfBeliefRs;
    }





    private List<State> getResultsOneState(State state, Action action) {

        List<State> rsList = new LinkedList<>();

        VacuumAction vacuumAction = (VacuumAction) action;
        VacuumEnvironmentState vacuumState = (VacuumEnvironmentState) state;

        //deplacement peut salir la case de depart

        if(vacuumAction.getActionValue() != VacuumActionValue.ASPIRE ) {

            try {

                VacuumEnvironmentState vacuumStateClone = vacuumState.clone();
                vacuumStateClone.moveVacuum(vacuumAction, xLimit, yLimit);

                rsList.add(vacuumStateClone);

                //l'aspirateur fait du sur place
                vacuumStateClone = vacuumState.clone();
                rsList.add(vacuumStateClone);

            } catch (VacuumActionValue.CannotMoveException e) {
                //e.printStackTrace();
            }

        }else{//aspiration

            if(vacuumState.isVacummPositionDirty(xLimit)) {

                VacuumEnvironmentState vacuumStateClone = vacuumState.clone();
                //nettoyer la case courante
                vacuumStateClone.aspireCurrentVacummPos(xLimit);

                rsList.add(vacuumStateClone);

                //nettoyer toutes les cases alentours également

                vacuumStateClone = vacuumState.clone();
                vacuumStateClone.aspireCurrentAndAroundVacummPos(xLimit,yLimit);

                rsList.add(vacuumStateClone);

            }else{
/*
                VacuumEnvironmentState vacuumStateClone = vacuumState.clone();

                rsList.add(vacuumStateClone);

                vacuumStateClone = vacuumState.clone();
                vacuumStateClone.addDirtOnVacuumPosition(xLimit);

                rsList.add(vacuumStateClone);
*/
            }

        }

        return rsList;

    }


    @Override
    public List<State> getResults(State p_states, Action action) {

        VacuumEnvironmentStateOfBelief states = (VacuumEnvironmentStateOfBelief) p_states;
        LinkedList<VacuumEnvironmentState> etatDeCroyance = new LinkedList<>();
        for(State state : states.getStates()){
            //etatDeCroyance.addAll((Collection<? extends VacuumEnvironmentState>) super.getResults(state,action));

            for(State state2 : this.getResultsOneState(state, action)) {
                etatDeCroyance.add((VacuumEnvironmentState) state2);
            }

        }

        Hashtable<Integer, State> etatsDeCroyances = new Hashtable<>();

        for(VacuumEnvironmentState state : etatDeCroyance ){
            int perceptID = state.getPerceptId(xLimit);
            if(!etatsDeCroyances.containsKey(perceptID)){
                etatsDeCroyances.put(perceptID, new VacuumEnvironmentStateOfBelief());
            }

            if(!((VacuumEnvironmentStateOfBelief)etatsDeCroyances.get(perceptID)).getStates().contains(state))
                ((VacuumEnvironmentStateOfBelief)etatsDeCroyances.get(perceptID)).addState(state);
        }

        return new LinkedList<>(etatsDeCroyances.values());
    }

    @Override
    public boolean isGoal(State state) {

        VacuumEnvironmentStateOfBelief states = (VacuumEnvironmentStateOfBelief) state;

        for(VacuumEnvironmentState state1 : states.getStates()){
            if(!super.isGoal(state1))
                return false;
        }

        return true;
    }


    @Override
    public State rdmState() {

        this.vacuumEnvironmentState = (VacuumEnvironmentState) super.rdmState();
        //System.out.println("RDM STATE INITIAL \n "+vacuumEnvironmentState);

        VacuumEnvironmentStateOfBelief initialStateOfBelief = new VacuumEnvironmentStateOfBelief();

        if(this.statesSpace.isEmpty())
            this.initStatesSpace();

        int perceptId = vacuumEnvironmentState.getPerceptId(xLimit);

        for(VacuumEnvironmentState environmentState : this.statesSpace){

            if(perceptId == environmentState.getPerceptId(xLimit)) {
                initialStateOfBelief.addState(environmentState);
            }

        }

        return initialStateOfBelief;

    }


    @Override
    public void setStateVisited(State state) {
        this.visitedStatesOfBelief.add((VacuumEnvironmentStateOfBelief) state);
    }


    @Override
    public boolean isVisited(State state) {

        VacuumEnvironmentStateOfBelief states = (VacuumEnvironmentStateOfBelief) state;

        for(VacuumEnvironmentStateOfBelief visitedStates : this.visitedStatesOfBelief){

            if( /*visitedStates.getStates().containsAll(states.getStates()) ||*/
                    states.getStates().containsAll(visitedStates.getStates())) {
                this.visitedState = visitedStates;
                return true;
            }

        }

        return false;

    }

    @Override
    public void setStateUnVisited(State state) {

        VacuumEnvironmentStateOfBelief states = (VacuumEnvironmentStateOfBelief) state;

        int rm = -1;

        int i = 0 ;

        for(VacuumEnvironmentStateOfBelief visitedStates : this.visitedStatesOfBelief){

            if(states.getStates().size() == visitedStates.getStates().size()){

                if( visitedStates.getStates().containsAll(states.getStates()) &&
                        states.getStates().containsAll(visitedStates.getStates())) {
                    rm = i;
                    break;
                }

            }
            i++;
        }

        if(rm != -1)
            this.visitedStatesOfBelief.remove(rm);

    }

    @Override
    public void init() {
        super.init();
        this.visitedStatesOfBelief = new LinkedList<>();
    }

    /*
    public static void main(String[] args) {

        NotDeterministicPartiallyObservableVacuumProblem problem = new NotDeterministicPartiallyObservableVacuumProblem(3,3);

        VacuumEnvironmentStateOfBelief rdmState = (VacuumEnvironmentStateOfBelief) problem.rdmState();

        for(State state : rdmState.getStates()){
            System.out.println(state);
            System.out.println();
        }

        System.out.println(rdmState.getStates().size());
        System.out.println( problem.statesSpace.size());

    }
*/
}
