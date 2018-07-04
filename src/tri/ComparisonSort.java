package tri;

/**
 * Created by monsio on 4/3/16.
 */
public class ComparisonSort {

    /*Utilisé par le tri par base afin de recuperer la valeur du tableau ou une partie comme la partie correspondant au unités, dizaine, centaine*/
    interface Avalue{
        int getA(Integer A[],int i, int div);
    }

    /*Trie par comparaison directement sur les valeurs du tableau */
    public static Integer[] coutingSort(Integer A[], int k){

        Integer B[] = new Integer[A.length];
        return coutingSort(A, B, k, 1, new ComparisonSort.Avalue() {
            @Override
            public int getA(Integer[] A, int i, int div) {
                return A[i];
            }
        });
    }

    public static Integer[] coutingSort(Integer A[], Integer B[], int k, int div, Avalue v){

        //crée un tableau de la taille du plus grand élément de A + 1
        int C[] = new int[k+1];

        //Initialise les compteurs d'apparitions des elements de A à 0
        for(int i = 0; i <= k; i ++)
            C[i] = 0;

        //Compte les apparitions des éléments de A
        for(int j = 0; j < A.length; j ++){
            int a  = v.getA(A,j,div);
            C[a] ++;
        }

        //Compte pour chaque valeur differente de l'ensemble A les nombre de valeurs plus petites ou égales
        for(int i = 1; i <= k; i ++)
            C[i] += C[i-1];

        for(int j = A.length - 1; j >= 0; j --) {
            /*positionne A[j] dans B en fonction du nombre d'elements dans A plus petite ou égal à A[j], à l'aide de C
            * Si A[j] vaut trois et que A contient 3 autres 2 autres elements plus petit ou egal à trois 3 se place en position 3...
            * */
            int a  = v.getA(A,j,div);
            B[C[a]-1] = A[j];
            //decremente le compteur d'element <= à A[j] au cas ou A[j] n'est pas un element unique les autres elements egaux à A[j] seront placé avant lui.
            C[a]--;

        }

        return B;

    }

}
