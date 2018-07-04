package tri;

import java.util.List;

/**
 * Created by monsio on 4/3/16.
 */
public class BinarySearch {


    public static int binarySearch(Comparable valeurs[], Comparable cible){
        return binarySearch(valeurs,cible,0,valeurs.length);
    }

    public static int binarySearch(Comparable valeurs[], Comparable cible, int posGauche, int posDroite)
    {
        //permet de travailler avec des indices [i à j[
        posDroite--;

        if (posGauche == posDroite)
            return cible.compareTo(valeurs[posGauche]) == 0 ? posGauche : -1 ;
        int posMilieu = (posGauche + posDroite) / 2;
        if (cible.compareTo(valeurs[posMilieu]) <= 0 )
            return binarySearch(valeurs, cible, posGauche, posMilieu+1);
        else
            return binarySearch(valeurs, cible, posMilieu + 1, posDroite+1);
    }



    public static int binarySearchClosedInf(Integer valeurs[], Integer cible)
    {
        return binarySearchClosedInf(valeurs, cible, 0, valeurs.length);
    }

    public static int binarySearchClosedInf(Integer valeurs[], Integer cible, int posGauche, int posDroite)
    {
        //permet de travailler avec des indices [i à j[
        posDroite--;

        Integer valGauche = valeurs[posGauche];
        Integer valDroite = valeurs[posDroite];
        if (posGauche == posDroite) {
            return valGauche.compareTo(cible) <= 0 ? posGauche : -1;
        }
        if (posGauche + 1 == posDroite){

            //Si la valeur de droite est inferieure a la cible celle de gauche l'est forcement encore plus
            if(valDroite <= cible)
                return posDroite;
                //Sinon si seul la valeur de gauche est inferieure a la cible
            else if(valGauche <= cible)
                return posGauche;
            else
                return -1;
        }

        int posMilieu = (posGauche + posDroite) / 2;
        if (cible.compareTo(valeurs[posMilieu]) <= 0)
            return binarySearchClosedInf(valeurs, cible, posGauche, posMilieu + 1);
        else
            return binarySearchClosedInf(valeurs, cible, posMilieu, posDroite + 1);
    }

    public static int binarySearchClosedSup(List<Comparable> valeurs, Integer cible)
    {
        return binarySearchClosedSup(valeurs, cible, 0, valeurs.size());
    }


    public static int binarySearchClosedSup(List<Comparable> valeurs, Comparable cible, int posGauche, int posDroite)
    {
        //permet de travailler avec des indices [i à j[
        posDroite--;

        Comparable valGauche = valeurs.get(posGauche);
        Comparable valDroite = valeurs.get(posDroite);
        if (posGauche == posDroite) {
            return valDroite.compareTo(cible) >= 0 ? posDroite : -1;
        }
        if (posGauche + 1 == posDroite){

            //Si la valeur de gauche est superieure a la cible celle de droite l'est forcement encore plus
            if(valGauche.compareTo(cible) >= 0)
                return posGauche;
                //Sinon si seul la valeur de droite est superieure a la cible
            else if(valDroite.compareTo(cible) >= 0)
                return posDroite;
            else
                return -1;
        }

        int posMilieu = (posGauche + posDroite) / 2;
        if (cible.compareTo(valeurs.get(posMilieu)) <= 0)
            return binarySearchClosedSup(valeurs, cible, posGauche, posMilieu + 1);
        else
            return binarySearchClosedSup(valeurs, cible, posMilieu, posDroite + 1);
    }


}
