package ai.explorationStrategy.csp.value.sort;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

public class LessStressedValue extends SortDomainMode {

    @Override
    public void sortDomain(BinaryCSP<?> csp, CSPvariable var) {

        final Hashtable<Object,Integer> valueCount = new Hashtable<>();

        //pour chaque valeur du domaine
        for( Object value : csp.getDomain(var) ){

            valueCount.put(value, 0);
            //pour chaque variable du CSP
            for( CSPvariable var2 : csp.getVariables() ){
                //si la variable est differente et non assignée
                if(!var2.equals(var) && var2.getValue() == null){
                    //si le domaine contient la valeur
                    if(csp.getDomain(var2).contains(value)){
                        //on incremente le compteur
                        valueCount.put(value, valueCount.get(value) + 1);
                    }

                }

            }

        }

        //on trie le domaine par ordre croissant en fonction du niveau de contrainte sur les valeurs
        Collections.sort(csp.getDomain(var), new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return valueCount.get(o1).compareTo(valueCount.get(o2));
            }
        });

    }

}
