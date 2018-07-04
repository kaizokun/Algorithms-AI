package tri;

/**
 * Created by monsio on 4/3/16.
 */
public class MergeSort {

    public static void mergeSort(Comparable tab[]){
        mergeSort(tab,0,tab.length);
    }

    public static void mergeSort(Comparable tab[], int p, int r){

        //permet de travailler avec des indices [i à j[
        r--;

        if (p < r) {
            int q = (p + r) / 2;
            mergeSort(tab, p, q+1);
            mergeSort(tab, q + 1, r+1);
            merge(tab, p, q, r);
        }
    }


    private static void merge(Comparable tab[], int p, int q, int r) {

        int n1 = q - p + 1;
        int n2 = r - q;

        Comparable R[] = new Comparable[n1];
        Comparable L[] = new Comparable[n2];

        for( int i = 0 ; i < n1 ; i ++)
            R[i] = tab[p+i];

        for( int i = 0 ; i < n2 ; i ++)
            L[i] = tab[q+1+i];

        int i = 0, j = 0, k = p;

        while( k <= r ){
            if(R[i].compareTo(L[j]) < 0 ){
                tab[k++] = R[i++];
                if(i == n1) break;

            }else{
                tab[k++] = L[j++];
                if(j == n2) break;
            }
        }

        while( j < n2 )
            tab[k++] = L[j++];

        while( i < n1)
            tab[k++] = R[i++];

    }


}
