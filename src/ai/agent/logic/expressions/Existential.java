package ai.agent.logic.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Existential extends UnaryStatement {

    private List<FonctionalSymbol> variables;

    public Existential() {

    }

    public Existential(Statement st1, List<FonctionalSymbol> variables) {
        super(st1);
        this.variables = new ArrayList(variables);
    }

    public Existential(Statement st1, FonctionalSymbol... variables) {
        super(st1);
        this.variables = new ArrayList(Arrays.asList(variables));
    }

    public Existential(Statement st1) {
        super(st1);
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    @Override
    public Statement clone(boolean cloneSymbol) {
        return new Existential(this.st1.clone(cloneSymbol),this.variables);
    }

    @Override
    public String toShortString() {
        return null;
    }

    public List<FonctionalSymbol> getVariables() {
        return variables;
    }

    public void setVariables(List<FonctionalSymbol> variables) {
        this.variables = variables;
    }


    @Override
    public String toString() {
        return "(∃ "+variables+" ("+this.st1+"))";
    }

    @Override
    public String noVarsSignature() {
        return "(∃ "+variables+" ("+this.st1.noVarsSignature()+"))";
    }

    @Override
    protected void addExistentials(List<Existential> existentials) {
        existentials.add(this);
    }


    public void skolemisation(int cptrExist, List<FonctionalSymbol> variables) {

            //remplacer chaque variable de l'énonce existenciel par une fonction ayant comme argument l'ensemble des variables
            //de la clause universelle parent
            Hashtable<String, FonctionalSymbol> vars = new Hashtable<>();
            for(FonctionalSymbol var : this.getVariables()){
                FonctionalSymbol skolemConst = new FonctionalSymbol(""+((char)('A'+cptrExist)), variables);
                skolemConst.skolem = true;
                vars.put( var.label,skolemConst);
            }

            this.replaceVarBySkolemConst(vars);

    }


}
