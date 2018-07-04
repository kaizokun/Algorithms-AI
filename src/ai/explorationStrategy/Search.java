package ai.explorationStrategy;


import ai.problem.Problem;

public interface Search<T> {

    int getNodesDeployed();

    class ExplorationFailedException extends Exception{}

    int getNodesAddToFrontier();

    T search(Problem problem) throws ExplorationFailedException;

}
