package ai.explorationStrategy.local;

public class LocalSearchConfig {

    protected int neighbourTestLimit = 1;
    protected int lateralMoveLimit = 10;
    protected int iterationLimit = 100;


    public LocalSearchConfig() { }

    public int getNeighbourTestLimit() {
        return neighbourTestLimit;
    }

    public void setNeighbourTestLimit(int neighbourTestLimit) {
        this.neighbourTestLimit = neighbourTestLimit;
    }

    public int getLateralMoveLimit() {
        return lateralMoveLimit;
    }

    public void setLateralMoveLimit(int lateralMoveLimit) {
        this.lateralMoveLimit = lateralMoveLimit;
    }

    public int getIterationLimit() {
        return iterationLimit;
    }

    public void setIterationLimit(int iterationLimit) {
        this.iterationLimit = iterationLimit;
    }
}
