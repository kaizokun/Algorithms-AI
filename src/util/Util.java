package util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by monsio on 07.04.16.
 */
public class Util {

    public static String getIdent(int d){
        String ident = "";
        for( int i = 0 ; i < d ; i ++ )
            ident+="     ";

        return ident+">>"+d+">> ";

    }

    public static void showTab( List tab){

        if(tab.isEmpty())
            return;

        System.out.println();
        for(Object t : tab)
            System.out.print(t+" ");

        System.out.println();

    }

    /**
     * [a,b] a inclusif, b inclusif
     * */
    public static int rdnInt(int a, int b){
        return ThreadLocalRandom.current().nextInt(a, b + 1);
    }


    private static long t1;

    public static void initTime(){
        t1 = System.nanoTime();
    }

    public static double timeDelta;

    public static void printTimeDelta(){
        computeTimeDelta();
        System.out.println(timeDelta+" ms");
    }

    public static String getTimeDeltaStr(){
        computeTimeDelta();
        return timeDelta+" ms";
    }

    public static double computeTimeDelta(){
        timeDelta = ((System.nanoTime() - t1)/1000000.0);
       return timeDelta;
    }

}
