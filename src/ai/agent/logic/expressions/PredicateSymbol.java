package ai.agent.logic.expressions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class PredicateSymbol extends Symbol {

    public PredicateSymbol(String label, List<FonctionalSymbol> terms) {
        super(label, terms);
    }

    public PredicateSymbol(String label, boolean value, int arity) {
        super(label, value, arity);
    }

    public PredicateSymbol(String label) {
        super(label);
    }

    public PredicateSymbol(String label, int arity) {
        super(label, arity);
    }

    public PredicateSymbol(String label, FonctionalSymbol... terms) {
        super(label, terms);
    }

    protected Symbol getNew(String label, List<FonctionalSymbol> params){
        return new PredicateSymbol(label,params);
    }

    @Override
    protected void addConsts(HashSet<FonctionalSymbol> consts) {

        for(FonctionalSymbol fonctionalSymbol : fonctionalSymbols){
            fonctionalSymbol.addConsts(consts);
        }

    }


}
