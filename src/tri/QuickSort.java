package tri;

import java.util.List;
import java.util.Random;

/**
 * Created by monsio on 4/3/16.
 */
public class QuickSort {

    /*========================== QUICK SORT=====================*/


    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top isTrue,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public interface Pivot {
        void initPivot(List<Comparable> tab, int s, int e);
    }

    public static void quickSort( List tab ){
        quickSort(tab, 0, tab.size() - 1, new Pivot() {
            @Override
            public void initPivot(List<Comparable> tab, int s, int e) {}
        });
    }

    public static void quickSortRdm( List tab ){
        quickSort(tab, 0, tab.size() - 1, new Pivot() {
            @Override
            public void initPivot(List<Comparable> tab, int s, int e) {
                int rdm = randInt(s, e);

                Comparable x = tab.get(rdm);

                tab.set(rdm, tab.get(e));

                tab.set(e, x);
            }
        });
    }

    public static void quickSort( List tab, Pivot fct ){
        quickSort(tab,0,tab.size()-1,fct);
    }


    private static void quickSort( List<Comparable> tab, int s, int e, Pivot fct ){

        if( s < e ){
            int p = split(tab, s, e, fct);
            quickSort(tab, s, p-1,fct);
            quickSort(tab, p+1, e,fct);
        }

    }

    /**
     * La procedure split cree quatres sous ensembles à partir de l'ensemble E[s, e-1] avec comme pivot E[e]
     * un sous ensemble est compris entre E1[1, i] il contient les elements de E plus petits que le pivot
     * un autre sous ensemble est compris entre E2[i+1, j-1] il contient les elements plus grand que le pivot
     * les deux autre sous sensembles sous l'ensemble à element unique contenant le pivot et celui contenant les elements n'appartennant pa encore a E1 et E2.
     * Au fur et à mesure que j avance, i avance également lorsque j contient un element plus petit que le pivot
     * au cas ou un element plus petit est rencontré apres E2 il est interverti avec le premier element de E2.
     * A la fin le pivot est interverti avec i+1 qui n'est autre que lui même si tout les elements de E sont plus petit que le pivot.
     * Sinon c'est le plus petit element de E2 qui est echangé.
     * */

    public static int split( List<Comparable> tab, int s, int e, Pivot fct){

        fct.initPivot(tab, s, e);

        Comparable x = tab.get(e);

        int i = s - 1;

        for( ; s <= e - 1 ; s ++ ){
            if( tab.get(s).compareTo(x) <= 0 ){
                i ++ ;
                Comparable tmp = tab.get(i);
                tab.set(i,tab.get(s));
                tab.set(s,tmp);
            }
        }

        Comparable tmp = tab.get(i+1);
        tab.set(i+1,x);
        tab.set(e,tmp);

        return i+1;

    }





}
