package dataStructure.tas;

/**
 * Created by monsio on 13/08/2015.
 */
public class PileData implements ItPileData{

    protected Double priority;
    protected int index;
    protected String data;

    public PileData(double priority, String data) {
        this.priority = priority;
        this.data = data;
    }

    public Double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "P(" + priority+") ["+index+"] V ["+TasMax.parent(index)+"] - ";
    }

    public void setIndex(int i) {
        this.index = i;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setPriority(Double priority) {
        this.priority = priority;
    }
}
