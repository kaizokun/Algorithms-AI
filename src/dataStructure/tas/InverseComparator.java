package dataStructure.tas;

import java.util.Comparator;

/**
 * Created by monsio on 10/08/2015.
 */

public class InverseComparator<T> implements Comparator<T> {

    private Comparator<T> comparator;

    public InverseComparator( Comparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(T o1, T o2) {
        return this.comparator.compare(o2,o1);
    }

}