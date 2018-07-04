package ai.agent.logic.expressions;

import java.util.Iterator;

public class Equality extends PredicateSymbol {

    private static final String eqLabel = "Equal";

    public Equality() {
        super(eqLabel, 2);
    }

    public Equality(int arity) {
        super(eqLabel, arity < 2 ? 2 : arity);
    }

    public Equality( FonctionalSymbol... terms) {
        super(eqLabel, terms);
    }

    @Override
    public boolean isTrue() {

        Iterator<FonctionalSymbol> iterator = this.fonctionalSymbols.iterator();

        FonctionalSymbol t1 = iterator.next();

        boolean equals = true;

        while(equals && iterator.hasNext()){
            equals = t1.equals(iterator.next());
        }

        return equals;
    }

    @Override
    public String toString() {

        String eq = " ( ";

        for(FonctionalSymbol symbol : this.fonctionalSymbols){
            eq+=symbol+" = ";
        }

        return eq.substring(0,eq.length()-2)+" ) ";

    }
}
