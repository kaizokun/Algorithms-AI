package ai.agent.logic;

import ai.agent.logic.expressions.FonctionalSymbol;
import ai.agent.logic.expressions.Variable;

public class Substitution {

    private Variable var;
    private FonctionalSymbol fonctionalSymbol;

    public Substitution(Variable var, FonctionalSymbol fonctionalSymbol) {
        this.var = var;
        this.fonctionalSymbol = fonctionalSymbol;
    }

    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    public FonctionalSymbol getFonctionalSymbol() {
        return fonctionalSymbol;
    }

    public void setFonctionalSymbol(FonctionalSymbol fonctionalSymbol) {
        this.fonctionalSymbol = fonctionalSymbol;
    }

    @Override
    public String toString() {
        return var+"/"+fonctionalSymbol;
    }
}
