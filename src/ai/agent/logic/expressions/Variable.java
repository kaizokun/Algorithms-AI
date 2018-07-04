package ai.agent.logic.expressions;

import ai.agent.logic.Unify;

import java.util.Hashtable;
import java.util.Set;

public class Variable extends FonctionalSymbol {

    protected FonctionalSymbol constante;


    public Variable(String label,FonctionalSymbol constante) {
        super(label);
        this.constante = constante;
    }

    public Variable(String label) {
        super(label);
    }

    public void clear(){
        this.constante = null;
    }

    @Override
    public boolean containVar(Variable v) {
        return v.getLabel().equals(this.label);
    }

    @Override
    public Statement clone(boolean cloneSymbol) {
        if(cloneSymbol) {
            return new Variable(new String(this.label),constante);
        }

        return this;
    }
/*
    @Override
    protected void addVariables(Set<Variable> variables) {
      if(this.constante == null){variables.add(this);}
    }
*/

    @Override
    protected void addVariables(Set<Variable> variables) {
      variables.add(this);
    }

    public FonctionalSymbol getConstante() {
        return constante;
    }

    public void setConstante(FonctionalSymbol constante) {
        this.constante = constante;
    }

    @Override
    public String toString() {
        return constante != null ? constante.toString()+" " : label+" ";
    }

    @Override
    public String noVarsSignature() {
        return constante != null ? constante.toString() : "";
    }

    @Override
    public FonctionalSymbol replaceValue(Hashtable<Variable, FonctionalSymbol> subs) throws Unify.UnifyException {
        /*si une variable contenu dans une fonction possede une substitution
        * qui est elle meme une variable on recommence le processus
        * */
        if( subs.containsKey(this) ){
            return subs.get(this).replaceValue(subs);
        }

        return (FonctionalSymbol) this.clone(true);
    }
}
