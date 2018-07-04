package ai.agent.logic.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Universal extends UnaryStatement {

    private List<FonctionalSymbol> variables;


    public Universal() {

    }

    public Universal(Statement st1) {
        super(st1);
    }

    public Universal(Statement st1, List<FonctionalSymbol> variables) {
        super(st1);
        this.variables = new ArrayList(variables);
    }

    public Universal(Statement st1, FonctionalSymbol... variables) {
        super(st1);
        this.variables = new ArrayList(Arrays.asList(variables));
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    @Override
    public Statement clone(boolean cloneSymbol) {
        return new Universal(this.st1.clone(cloneSymbol),this.variables);
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
        return "(∀ "+variables+" ( "+this.st1+" ))";
    }

    @Override
    public String noVarsSignature() {
        return "(∀ "+variables+" ( "+this.st1.noVarsSignature()+" ))";
    }

    public int varCount(){
        return this.variables.size();
    }


    public void skolemisation() {

        List<Existential> existentials = this.getExistentials();
        int i = 0;
        for(Existential existential : existentials){

            //remplacer chaque variable de l'énonce existenciel par une fonction ayant comme argument l'ensemble des variables
            //de la clause universelle parent
            Hashtable<String, FonctionalSymbol> vars = new Hashtable<>();
            for(FonctionalSymbol var : existential.getVariables()){
                FonctionalSymbol skolemConst = new FonctionalSymbol(""+((char)('A'+i)), this.variables);
                vars.put( var.label,skolemConst);
                i++;
            }

            existential.replaceVarBySkolemConst(vars);
        }


    }

}
