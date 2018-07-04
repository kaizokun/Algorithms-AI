package ai.games.vacuum;

import ai.State;

import java.util.LinkedList;
import java.util.List;

public class VacuumEnvironmentStateOfBelief extends State {

    private List<VacuumEnvironmentState> states = new LinkedList<>();

    public VacuumEnvironmentStateOfBelief() {
    }

    public VacuumEnvironmentStateOfBelief(List<VacuumEnvironmentState> states) {
        this.states = states;
    }

    public void addState(VacuumEnvironmentState state){
        this.states.add(state);
    }

    public List<VacuumEnvironmentState> getStates() {
        return states;
    }

    /**
     * La methode equals de cette classe ne compare pas l'egalité totale entre deux ensembles
     * mais verifie si une ensemble est compris dans l'autre
     * utilisé dans la classe ActionStatePlan pour retrouver un plan en fonction d'un etat de croyance.
     * Les etats de croyance étant stockés dans un tableau, la méthode indexOf utilise equals.
     * Le mieux serait de definir une fonction indexOf indexOfSimilar qui utiliserait une méthode
     * similarTo plutot que equal et qui pour un etat normal passerait le relais à la méthode equals.
     * **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VacuumEnvironmentStateOfBelief states1 = (VacuumEnvironmentStateOfBelief) o;

        return states1.states.containsAll(this.states) || this.states.containsAll(states1.states);

    }

    @Override
    public String toString() {

       String rs = "\n=====ETAT DE CROYANCE====\n";
       for(VacuumEnvironmentState state : states){
           rs+=state+"\nOU\n";
       }

       return rs.substring(0,rs.length()-3);

    }

    @Override
    public String toStringb(int i) {
        String ident = util.Util.getIdent(i);
        String rs = "\n"+ident+"=====ETAT DE CROYANCE====\n";
        for(VacuumEnvironmentState state : states){
            rs+=ident+state+"\n"+ident+"OU\n";
        }

        return rs.substring(0,rs.length()-3);
    }
}
