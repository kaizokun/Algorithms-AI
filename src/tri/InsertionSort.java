package tri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static util.Util.showTab;

/**
 * Created by monsio on 4/3/16.
 */
public class InsertionSort {


    /*
    public static void insertSort(Comparable tab[]){

        for( int j = 1 ; j < tab.length ; j++){

            Comparable key = tab[j];

            int i = j - 1;

            for( ; i >= 0 && key.compareTo(tab[i]) < 0 ; i-- ){
                tab[i+1] = tab[i];
            }

            tab[i+1] = key;


        }

    }
*/


    /**
     * version avec recherche dichotomique dans la boucle interieure
     * pas beaucoup plus efficace sauf moins de comparaisons
     * */
    public static void insertSort(List<Comparable> tab){

        for( int j = 1 ; j < tab.size() ; j++){

            Comparable key = tab.get(j);
            int iInf = BinarySearch.binarySearchClosedSup(tab, key, 0, j);

            if(iInf != -1){
                int i = j - 1;

                while(i >= iInf) {
                    tab.set(i + 1, tab.get(i));
                    i--;
                }

                tab.set(iInf,key);
            }

        }

    }


    public static void main(String[] args) {

        List<Comparable> A = new ArrayList<Comparable>(Arrays.asList(new Integer[]{9, 1, 7, 5, 3, 16, 3, 2, 4, 6, 3, 11, 2, 12, 5, 13, 2, 18}));

        showTab(A);

        InsertionSort.insertSort(A);
        showTab(A);

    }

}
