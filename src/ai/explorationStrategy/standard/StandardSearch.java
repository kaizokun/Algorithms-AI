package ai.explorationStrategy.standard;

import ai.Action;
import ai.explorationStrategy.Search;
import ai.problem.Problem;

import java.util.List;

/**
 * Created by monsio on 7/12/17.
 */
public interface StandardSearch extends Search<List<Action>> {

    List<Action> search(Problem problem) throws ExplorationFailedException;

}
