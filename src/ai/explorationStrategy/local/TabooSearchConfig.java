package ai.explorationStrategy.local;

public class TabooSearchConfig extends LocalSearchConfig {

    private int visitedMaxSize = Integer.MAX_VALUE;

    public TabooSearchConfig(){ }

    public TabooSearchConfig(int neighbourTestLimit, int iterationLimit, int visitedMaxSize) {
        this.neighbourTestLimit = neighbourTestLimit;
        this.iterationLimit = iterationLimit;
        this.visitedMaxSize = visitedMaxSize;
    }

    public boolean stopLoop(int neighbourTestCount, int iterationCount ){
        return neighbourTestCount > neighbourTestLimit || iterationCount > iterationLimit;
    }

    public int getVisitedMaxSize() {
        return visitedMaxSize;
    }
}
