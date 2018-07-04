package tri;

/**
 * Created by monsio on 4/3/16.
 */
public class BaseSort {

    /*=================================== TRI PAR BASE ================================*/

    public static Integer[] baseSort(Integer A[], int d){

        int div = 1;

        for(int i = 0 ; i < d ; i ++ ){
            A = ComparisonSort.coutingSort(A, new Integer[A.length], 10, div, new ComparisonSort.Avalue() {
                @Override
                public int getA(Integer A[], int j, int div) {
                    return (A[j]/div)%10;
                }
            });
            div*=10;
        }

        return A;

    }

}
