package ai;

import dataStructure.tas.ItPileData;

/**
 * Created by monsio on 7/12/17.
 */
public abstract class State<T> implements ItPileData {

    protected State source;
    protected T value;
    protected boolean visited = false, isLeaf = false;
    protected Action action;
    protected int depth;
    protected double wayCost;
    protected int index;
    protected double priority;
    protected double score;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isLeaf() {
        return this.isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public State getSource() {
        return this.source;
    }

    public void setSource(State source) {
        this.source = source;
    }

    public Action getAction() {
        return action;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public double getWayCost() {
        return wayCost;
    }

    public void setWayCost(double wayCost) {
        this.wayCost = wayCost;
    }

    public Double getPriority() {
        return this.priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getIndex() {
        return index;
    }

    public void init() {
        this.depth = 0;
        this.wayCost = 0;
        this.priority = 0;
        this.visited = false;
        this.isLeaf = false;
    }

    public long hashKey(){
        return new Long(this.hashCode());
    }

    public String toStringb(int i){
        return toString();
    }
}
