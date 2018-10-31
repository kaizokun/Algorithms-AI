package mylib.tas;

import java.util.ArrayList;
/**
 * Created by monsio on 9/08/2015.
 */
public class TasMin<T> extends TasMax<T>{

    public TasMin( ArrayList tab ) {
        super( tab );
    }

    @Override
    protected int cmp(int o1, int o2) {
        return super.cmp(o2, o1);
    }

    @Override
    protected int getInfiniteLesserPriority() {
        return Integer.MAX_VALUE;
    }

    protected int getInfiniteGreatestPriority() {
        return Integer.MIN_VALUE;
    }

    public T extractMin() throws Exception {
        return extractMax();
    }
}
