package ai.environment;

import ai.datastructure.GraphNode;

/**
 * Created by monsio on 7/12/17.
 */
public class Map implements Environment {

    protected GraphNode root;

    protected String name;

    public Map(GraphNode root, String name) {
        this.root = root;
        this.name = name;
    }

}
