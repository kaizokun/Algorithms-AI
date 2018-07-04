package dataStructure.tas;

import java.util.ArrayList;
/**
 * Created by monsio on 9/08/2015.
 */
public class TasMin extends TasMax{

    public TasMin( ArrayList tab ) {
        super( tab );
    }

    public TasMin() {
    }

    @Override
    protected int cmp(Double o1, Double o2) {
        return super.cmp(o2, o1);
    }

    @Override
    protected double getInfiniteLesserPriority() {
        return Double.MAX_VALUE;
    }

    protected double getInfiniteGreatestPriority() {
        return Double.MIN_VALUE;
    }

    public ItPileData extractMin() {
        return extractMax();
    }
}
