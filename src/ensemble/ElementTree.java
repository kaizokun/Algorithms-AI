package mylib.ensemble;

/**
 * Created by monsio on 15/07/16.
 */
public interface ElementTree extends Element {
    int getRank();
    void setRank(int i);
    ElementTree getParent();
    void setParent(ElementTree e);

}
