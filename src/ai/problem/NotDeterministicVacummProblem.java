package ai.problem;

import ai.Action;
import ai.ActionStatePlan;
import ai.State;
import ai.games.vacuum.VacuumAction;
import ai.games.vacuum.VacuumActionValue;
import ai.games.vacuum.VacuumEnvironmentState;
import ai.games.vacuum.VacuumEnvironmentStateOfBelief;

import java.util.*;

public class NotDeterministicVacummProblem extends VacuumProblem {

    public NotDeterministicVacummProblem(State initialState, int xLimit, int yLimit) {
        super(xLimit,yLimit);
        this.initialState = initialState;
    }

    public NotDeterministicVacummProblem(int xLimit, int yLimit) {
        super(xLimit,yLimit);
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

        Random random = new Random();

        //un deplacement ici ne produit qu'un seul resultat
        if(vacuumAction.getActionValue() != VacuumActionValue.ASPIRE ) {

            double rationGliss = 0.3;

            if(random.nextDouble() < rationGliss) {
                return vacuumStateClone;
            }else{
                try {
                    vacuumStateClone.moveVacuum(vacuumAction, xLimit, yLimit);
                    return vacuumStateClone;
                } catch (VacuumActionValue.CannotMoveException e) {
                    // e.printStackTrace();
                }
            }

        }

        //la position de l'aspirateur est sale
        if(vacuumState.isVacummPositionDirty(xLimit)) {

            //probabilité de tout nettoyer
            double probaCleanAll = 0.33;

            //si les chances de nettoyer toutes les cases alentours sont inferieur au ratio
            if (random.nextDouble() < probaCleanAll) {
                vacuumStateClone.aspireCurrentAndAroundVacummPos(xLimit,yLimit);
            } else {
                vacuumStateClone.aspireCurrentVacummPos(xLimit);
            }

        }else{

            double probaAddDirt = 0.25;

            if(random.nextDouble() < probaAddDirt){
                vacuumStateClone.addDirtOnVacuumPosition(xLimit);
            }

        }

        return vacuumStateClone;

    }





    @Override
    public List<State> getResults(State state, Action action) {

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

                VacuumEnvironmentState vacuumStateClone = vacuumState.clone();

                rsList.add(vacuumStateClone);

                vacuumStateClone = vacuumState.clone();
                vacuumStateClone.addDirtOnVacuumPosition(xLimit);

                rsList.add(vacuumStateClone);

            }

        }

        return rsList;

    }

}


