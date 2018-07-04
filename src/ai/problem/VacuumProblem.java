package ai.problem;

import ai.Action;
import ai.State;
import ai.games.vacuum.Vacuum;
import ai.games.vacuum.VacuumAction;
import ai.games.vacuum.VacuumActionValue;
import ai.games.vacuum.VacuumEnvironmentState;
import util.Util;

import java.util.*;

public class VacuumProblem extends SimpleProblem {

    protected static int xLimit, yLimit;
    protected List<VacuumEnvironmentState> statesSpace = new LinkedList<>();

    public VacuumProblem(State initialState, int xLimit, int yLimit) {
        this(xLimit,yLimit);
        this.initialState = initialState;
    }

    public VacuumProblem(int xLimit, int yLimit) {
        this.xLimit = xLimit;
        this.yLimit = yLimit;
    }

    public void initStatesSpace(){

        List<ArrayList<Boolean>> grounds = new LinkedList<>();

        this.initGrounds(grounds, new ArrayList(Arrays.asList(new Boolean[xLimit*yLimit])),0);

        for( int x = 0 ;  x < xLimit ; x ++ ) {
            for (int y = 0; y < yLimit; y++) {
                for(ArrayList<Boolean> ground : grounds) {
                    statesSpace.add(new VacuumEnvironmentState(new ArrayList<Boolean>(ground),new Vacuum(x,y)));
                }
            }
        }
/*
        System.out.println("INIT SPACE-------------------------------------------------");

        for(VacuumEnvironmentState state : statesSpace)
            System.out.println(state+"\n\n");
        System.out.println("INIT SPACE-------------------------------------------------");
*/
    }


    private void initGrounds(List<ArrayList<Boolean>> grounds, ArrayList<Boolean> ground, int p) {

        if(p == xLimit * yLimit){
            grounds.add(new ArrayList<Boolean>(ground));
        }else {

            ground.set(p, true);
            initGrounds(grounds, ground, p + 1);

            ground.set(p, false);
            initGrounds(grounds, ground, p + 1);
        }

    }

    public static void main(String[] args) {

        VacuumProblem vacuumProblem = new VacuumProblem(3,3);

        vacuumProblem.initStatesSpace();

        for(State state : vacuumProblem.getStatesSpace()) {
            System.out.println(state);
            System.out.println();
        }

    }

    public List<VacuumEnvironmentState> getStatesSpace() {
        return statesSpace;
    }


    @Override
    public List<Action> getActions(State state) {

        List<Action> actions = new LinkedList();

        for(VacuumActionValue actionValue : VacuumActionValue.values()){
            actions.add(new VacuumAction(actionValue));
        }

        //Collections.shuffle(actions);

        return actions;
    }


    /* RECODER LE SYSTEM EN ORIENTE OBJET
    *  TABLEAU D OBJETS CASES POUVANT ETRE PROPRE OU SALE
    *
    *  VACUMMSTATE comprenant un objet de type VACUUM
    *  contenant la position de l'aspirateur avec la capacité de se deplacer
    *  en fonction d'un deplacement envoyé en parametre
    *
    * */

    @Override
    public State getResult(State state, Action action) {

        VacuumAction vacuumAction = (VacuumAction) action;
        VacuumEnvironmentState vacuumState = (VacuumEnvironmentState) state;
        VacuumEnvironmentState vacuumStateClone = vacuumState.clone();

        //un deplacement ici ne produit qu'un seul resultat
        if(vacuumAction.getActionValue() != VacuumActionValue.ASPIRE ) {

            try {
                vacuumStateClone.moveVacuum(vacuumAction, xLimit, yLimit);
                return vacuumStateClone;
            } catch (VacuumActionValue.CannotMoveException e) {
                return vacuumState;
            }

        }

        //la position de l'aspirateur est sale
        if(vacuumState.isVacummPositionDirty(xLimit)) {
            vacuumStateClone.aspireCurrentVacummPos(xLimit);
        }

        return vacuumStateClone;

    }


    @Override
    public boolean isGoal(State state) {

        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;

        for( boolean c : vacuumEnvironmentState.getGround() )
            if(c == false)
                return false;

        return true;
    }

    public static int getxLimit() {
        return xLimit;
    }

    public static int getyLimit() {
        return yLimit;
    }

    @Override
    public State rdmState() {

        Random random = new Random();

        double ratioClean = 0.25;

        int x =  Util.rdnInt(0, xLimit-1);
        int y =  Util.rdnInt(0, yLimit-1);

        Vacuum vacuum =  new Vacuum(x, y);
        ArrayList<Boolean> ground = new ArrayList<Boolean>(xLimit*yLimit);

        for( int i = 0 ; i < xLimit*yLimit ; i ++){
            ground.add(random.nextDouble() < ratioClean);
        }

        return new VacuumEnvironmentState(ground, vacuum);

    }

    @Override
    public void setStateVisited(State state) {
        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        Long key = vacuumEnvironmentState.hashKey();
        this.visited.add(key);
    }

    @Override
    public boolean isVisited(State state) {

        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        Long key = vacuumEnvironmentState.hashKey();
        if(this.visited.contains(key)){
            this.visitedState = state;
            return true;
        }

        return false;
    }

    @Override
    public void setStateUnVisited(State state) {
        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        long key = vacuumEnvironmentState.hashKey();
        this.visited.remove(key);
    }

    @Override
    public boolean isLeaf(State state) {
        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        return leafs.containsKey(vacuumEnvironmentState.hashKey());
    }

    @Override
    public State getLeaf(State state) {
        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        return leafs.get(vacuumEnvironmentState.hashKey());
    }

    @Override
    public void setIsLeaf(State state) {
        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        this.leafs.put(vacuumEnvironmentState.hashKey(), state);
    }

    @Override
    public void setIsNotLeaf(State state) {
        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        this.leafs.remove(vacuumEnvironmentState.hashKey());
    }

    @Override
    public Long getStateKey(State state) {
        VacuumEnvironmentState vacuumEnvironmentState = (VacuumEnvironmentState) state;
        return vacuumEnvironmentState.hashKey();
    }
}


