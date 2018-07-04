package tri;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by monsio on 4/3/16.
 */
public class PackageSort {

    public static ArrayList<Integer> packageSort(ArrayList<Integer> A){

        ArrayList<ArrayList<Comparable>> B = new ArrayList<ArrayList<Comparable>>(A.size());

        ArrayList<Integer> C = new ArrayList<Integer>();

        int n = A.size();

        for( int i = 0 ; i < n ; i ++ )
            B.add(new ArrayList());

        for( Integer a : A)
            B.get(a/n).add(a);

        for(ArrayList b : B)
            InsertionSort.insertSort(b);

        for(ArrayList b : B)
            C.addAll(b);

        return C;

    }

    public static void main(String[] args) {

        ArrayList<Integer> A = new ArrayList<Integer>(Arrays.asList(new Integer[]{78,17,39,26,72,94,21,12,23,68}));
        ArrayList<Integer> B = packageSort(A);

        util.Util.showTab(B);

    }

}
