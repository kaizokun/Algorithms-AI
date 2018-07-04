package arrayUtil;

import java.util.Random;

/**
 * Created by monsio on 7/08/2015.
 */


public class RandomArray {


    public static int[] arrayShuffle( int [] array ){

        int length = array.length;
        int [] randArray = new int[length];

        Random rand = new Random();

        for( int l = length - 1; l >=0 ; l -- ){
            int randomNum = rand.nextInt(l + 1);
            randArray[l] = array[randomNum];
            array[randomNum] = array[l];
        }

        return randArray;
    }

    public static int[] directArrayShuffle( int [] tab ){

        Random rand = new Random();
        int l = tab.length;

        for( int i = 0 ; i < l ; i ++ ){
            int rdm = rand.nextInt( (l - i) + 1);
            int tmp = tab[i];
            tab[i] = tab[rdm];
            tab[rdm] = tmp;
        }

        return tab;
    }

    /*============================Tri par tas=======================*/


    public static void main(String[] args) {

        int[] tab = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12};

        int[] t2 = directArrayShuffle(tab);

        for( int i : t2 ){
            System.out.print(i+" ");
        }
    }

}
