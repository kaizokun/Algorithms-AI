package ai.explorationStrategy.standard;

import ai.Action;
import ai.problem.Problem;

import java.util.List;

/**
 * Created by monsio on 7/14/17.
 */
public class LDFS extends DFS {


    @Override
    public List<Action> search(Problem problem) throws StandardSearch.ExplorationFailedException {

        int limit = 999;
        int maxDepth = 1000;//Integer.MAX_VALUE;

        while (limit < maxDepth) {

            System.out.println("LIMIT : "+limit);

            long t1 = System.currentTimeMillis();

            try {

                 List<Action> sol = super.search(problem, limit);

                 System.out.println(" FINAL TIME : "+(System.currentTimeMillis() - t1)+" ms");

                 return sol;


            } catch (StandardSearch.ExplorationFailedException efe) {
                System.out.println(" TIME : "+(System.currentTimeMillis() - t1)+" ms");
                limit++;
            }

        }

        throw new StandardSearch.ExplorationFailedException();

    }

}
