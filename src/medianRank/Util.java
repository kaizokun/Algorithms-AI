package medianRank;

import tri.InsertionSort;
import tri.QuickSort;

import java.util.*;
import static util.Util.*;
/**
 * Created by monsio on 4/3/16.
 */
public class Util {

    private static boolean rankIfound = false;

    /** Algorithmique 3eme edition page 198-199 9.1 (Minimum et Maximum)**/
    public static void minMax( List<Comparable> list){

        Comparable min,max;

        int i;
        
        if( list.size() % 2 != 0){
            i = 1;
            min = list.get(0);
            max = list.get(0);
        }else{

            i = 2;

            if( list.get(0).compareTo(list.get(1)) >= 0 ){
                max = list.get(0);
                min = list.get(1);
            }else{
                max = list.get(1);
                min = list.get(0);
            }
        }

        for( ; i < list.size() - 1 ; i+=2 ){

            Comparable one = list.get(i), two = list.get(i+1);

            //si le premier est plus grand que le deuxieme
            if( one.compareTo(two) >= 0 ){
                //si le premier est plus grand que le max il le remplace
                if(one.compareTo(max) >= 0 ){
                    max = one;
                }

                //si le deuxieme est plus petit que le min il le remplace
                if(two.compareTo(min) <= 0 ){
                    min = two;
                }

            }else{
                //si le premier est plus grand que le max il le remplace
                if(two.compareTo(max) >= 0 ){
                    max = two;
                }

                //si le deuxieme est plus petit que le min il le remplace
                if(one.compareTo(min) <= 0 ){
                    min = one;
                }
            }

        }

        System.out.println("Min : "+min+" Max : "+max);

    }

    /*---------------------TROUVER OBJET RANG i-------------------*/

    public static Object selectionRandom(List tab, int rank){
        return selectionRandom(tab, rank, 0, tab.size() - 1, new QuickSort.Pivot() {
            @Override
            public void initPivot(List<Comparable> tab, int s, int e) {
                int rdm = QuickSort.randInt(s, e);

                Comparable x = tab.get(rdm);

                tab.set(rdm, tab.get(e));

                tab.set(e, x);
            }
        });
    }


    private static Object selectionRandom(List<Comparable> list, int i, int s, int e, QuickSort.Pivot fct){

        if( s == e )
            return list.get(s);
        //determine le pivot
        int p = QuickSort.split(list, s, e, fct);
        //determine le rang du pivot dans le sous tableau [s-e]
        int k = p - s + 1;

        if( k == i )//si le rang recherche correspond à celui du pivot
            return list.get(p);
        else if( i < k )//reessayer pour un sous tableau[s à p-1]
            return selectionRandom(list, i, s, p - 1, fct);
        else//l'element recherche est desormais le (i - k) plus petit element du sous tableau [(p+1) à e]
            return selectionRandom(list, i - k, p + 1, e, fct);

    }

    /*-------------------------------SELECTION TEMPS LINEAIRE-------------------------------*/

    public static Comparable selection(List<Comparable> list, List<Comparable> lMedians, int i, int s, int e, int d)throws RuntimeException{

        //System.out.println(""+getIdent(d)+"================Appel Selection ["+d+"]\n");

        //System.out.println(""+getIdent(d)+"Tableau courant : "+list.subList(s,e+1));

       // System.out.println(list.getClass()+" "+lMedians.getClass());

        if( i < 1 || i > list.size())
           throw new RuntimeException("en dehors des limites");

        if( lMedians.size() == 1) {
         //   System.out.println(""+getIdent(d)+"Median trouvé :"+lMedians.get(0)+"\n");
            return lMedians.get(0);
        }

        List<List<Comparable>> groupes = new LinkedList<List<Comparable>>();

        int n = lMedians.size();
        int g = 0;

      //  System.out.println(getIdent(d)+"-----------Selection du median-------------\n");

       // System.out.println(""+getIdent(d)+"Liste medians courant : "+lMedians+"\n");

      //  System.out.println(getIdent(d)+"Nombre de groupes : "+(n/5+1)+" - reste un groupe de "+(n%5)+" éléments\n");

        /*Création des 5 groupes*/
        for( ; g < n / 5 ; g ++)
            groupes.add(lMedians.subList(g*5, g*5+5));
        // plus eventuellement 1 si n n'est pas un multiple de5
        if( n%5 != 0 ) {
            groupes.add(lMedians.subList(g * 5, g * 5 + n % 5));
        }

        System.out.println("\n"+getIdent(d)+"Groupes non triés : \n");
        for(List l : groupes)
            System.out.println(getIdent(d)+l);

        //création de la liste des medians
        List<Comparable> medians = new ArrayList<Comparable>();
        //tri des groupes
        for(List l : groupes)
            InsertionSort.insertSort(l);

        System.out.println("\n"+getIdent(d)+"Groupes triés : \n");
        for(List l : groupes)
            System.out.println(getIdent(d)+l);

        //ajout des medians des groupes dans une liste
        for(List l : groupes)
            medians.add((Comparable) l.get( (l.size()+1)/2 - 1));
        //selection du median des medians

        System.out.println("\n"+getIdent(d)+"Médians : \n");
        System.out.print(getIdent(d));
        for(Object m : medians)
            System.out.print("[" + m + "]");
        System.out.println("\n");
        
        Comparable median = selection(list,medians,i,s,e,d+1);
/*
        System.out.println(getIdent(d)+"-----------AVANT SPLIT Tableau courant : "+list.subList(s,e+1)+"\n");
*/
        if(rankIfound)
            return median;

        int p = split(list,s,e,median);
/*
        System.out.println(getIdent(d)+"-----------APRES SPLIT Tableau courant : "+list.subList(s,e+1)+"\n");
        System.out.println(getIdent(d)+"----------- Pivot : ["+p+"] = "+list.get(p)+"\n");
*/
        //determine le rang du pivot dans le sous tableau [s-e]
        int k = p - s + 1;
        if( k == i )//si le rang recherche correspond à celui du pivot
        {
            //System.out.println(getIdent(d)+"Rang "+i+" trouvé : "+list.get(p));
            rankIfound = true;
            return list.get(p);}
        else if( i < k )//reessayer pour un sous tableau[s à p-1]
            return selection(list, list.subList(s,p), i, s, p - 1,d+1);
        else//l'element recherche est desormais le (i - k) plus petit element du sous tableau [(p+1) à e]
            return selection(list, list.subList(p+1,e+1), i - k, p + 1, e,d+1);

    }


    private static int split( List<Comparable> tab, int s, int e, Comparable q){

        int i = s - 1;

        int iPivot=-1;

        for( ; s <= e ; s ++ ){
            if( tab.get(s).compareTo(q) <= 0 ){
                i ++ ;
                Comparable tmp = tab.get(i);
                tab.set(i,tab.get(s));
                tab.set(s,tmp);

                if(tab.get(i).compareTo(q)==0)
                    iPivot = i;
            }
        }

        Comparable tmp = tab.get(i);
        tab.set(i,q);
        tab.set(iPivot,tmp);

        return i;

    }



    public static void main(String[] args) {

        ArrayList<Comparable> list = new ArrayList<Comparable>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}));
        Collections.shuffle(list);
        System.out.println(selection(list,list, 20, 0, list.size() - 1,1));
        //minMax(list);
        //System.out.println(selectionRandom(list, 3));

    }


}
