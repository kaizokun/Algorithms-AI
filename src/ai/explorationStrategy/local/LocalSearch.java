package ai.explorationStrategy.local;

import ai.State;
import ai.explorationStrategy.Search;

public abstract class LocalSearch implements Search<State> {


    protected int nodeDeployed = 0;

    public int getNodesDeployed() {
        return nodeDeployed;
    }

    @Override
    public int getNodesAddToFrontier() {
        return 0;
    }
}
