package tri;

/**
 * Created by monsio on 4/3/16.
 */
public class SelectionSort {


    public static void selectSort(Comparable tab[]){

        int n = tab.length;

        for( int j = 0 ; j < n-1 ; j ++ ){

            int iMin = j;

            for(int i = j+1 ; i < n ; i ++ )
                if( tab[i].compareTo(tab[iMin]) < 0 )
                    iMin = i;

            Comparable min = tab[iMin];
            tab[iMin] = tab[j];
            tab[j] = min;

        }

    }



}
