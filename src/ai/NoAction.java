package ai;

/**
 * Created by monsio on 7/12/17.
 */
public class NoAction extends Action {


    @Override
    public String getActionName() {
        return "NO ACTION";
    }

    @Override
    public double getCost() {
        return 0;
    }

    @Override
    public void setCost(double cost) {

    }

    @Override
    public State getResult() {
        return null;
    }

    @Override
    public void setResult(State state) {

    }

    @Override
    public String toString() {
        return "NoAction";
    }
}
