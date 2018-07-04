package ai.games.vacuum;

import ai.Action;

public class VacuumAction extends Action {

    private VacuumActionValue actionValue;

    public VacuumAction(VacuumActionValue actionValue) {
        this.actionValue = actionValue;
    }

    @Override
    public String getActionName() {
        return actionValue.toString();
    }

    public VacuumActionValue getActionValue() {
        return actionValue;
    }

    public void setActionValue(VacuumActionValue actionValue) {
        this.actionValue = actionValue;
    }

    @Override
    public String toString() {
        return ""+actionValue;
    }
}
