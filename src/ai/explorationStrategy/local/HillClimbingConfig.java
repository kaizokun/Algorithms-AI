package ai.explorationStrategy.local;

public class HillClimbingConfig extends LocalSearchConfig {

    private int lateralMoveLimit = 10;

    public HillClimbingConfig() { }

    public HillClimbingConfig(int noImprovementCountLimit, int lateralMoveLimit) {
        this.neighbourTestLimit = noImprovementCountLimit;
        this.lateralMoveLimit = lateralMoveLimit;
    }

    public boolean stopLoop(int lateralMovesCount, int neighbourTestCount){
        return neighbourTestCount > neighbourTestLimit || lateralMovesCount > this.lateralMoveLimit;
    }

}
