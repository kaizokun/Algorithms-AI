package ai.agent.logic.expressions;

import ai.agent.logic.Unify;

import java.util.*;

public class FonctionalSymbol extends Symbol {

    public FonctionalSymbol(String label, int arity) {
        super(label, arity);
    }

    public FonctionalSymbol(String label) {
        super(label);
    }

    public FonctionalSymbol(String label, boolean value, List<FonctionalSymbol> terms){
        super(label,terms);
        this.value = value;

    }

    public FonctionalSymbol(String label, List<FonctionalSymbol> terms){
        super(label,terms);
    }

    public FonctionalSymbol(String label, boolean value) {
        super(label, value);
    }
    /*
        public FonctionalSymbol(String label, Object objectRef, FonctionalSymbol... terms) {
            super(label, terms);
            System.out.println("FONCTIONAL SYMBOLS ... NEW "+label+" "+terms.length+" "+objectRef);
            this.objectRef = objectRef;
        }
    */
    public FonctionalSymbol(String label, FonctionalSymbol... terms) {
        super(label, terms);
    }

    protected Symbol getNew(String label, List<FonctionalSymbol> params){
        return new FonctionalSymbol(label,params);
    }

    public static void main(String[] args) {

        FonctionalSymbol mere = new FonctionalSymbol("Mère",1);
        FonctionalSymbol pere = new FonctionalSymbol("Père",1);
        FonctionalSymbol jean = new FonctionalSymbol("Jean");

        pere.addFonctionalSymbol(jean);
        mere.addFonctionalSymbol(pere);


        System.out.println(mere);
    }

    @Override
    protected void addConsts(HashSet<FonctionalSymbol> consts) {

        if(fonctionalSymbols == null || fonctionalSymbols.isEmpty()) {
            consts.add(this);
        }else {
            for(FonctionalSymbol fonctionalSymbol : fonctionalSymbols){
                fonctionalSymbol.addConsts(consts);
            }
        }

    }


    public FonctionalSymbol replaceValue(Hashtable<Variable, FonctionalSymbol> subs) throws Unify.UnifyException {

        if(fonctionalSymbols != null) {

            for (int i = 0; i < fonctionalSymbols.size(); i++) {

                FonctionalSymbol replacevalue = fonctionalSymbols.get(i).replaceValue(subs);
                /*
                //si l'on remplace une variable contenu dans une fct de skolem par une autre fonction de skolem
                if(skolem && replacevalue.skolem){
                    throw new Unify.UnifyException();
                }
*/
                fonctionalSymbols.set(i, replacevalue );

            }
        }

        this.signature = null;

        return this;
    }


}
