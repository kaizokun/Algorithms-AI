package ai.agent.logic;

import ai.agent.logic.expressions.*;

import javax.sql.rowset.Predicate;
import java.util.*;

public class Unify {

    public static boolean log = false;

    public static UnifyResult unify(Object x, Object y) throws UnifyException {

        Hashtable<Variable,FonctionalSymbol> subsTab = unify( x,  y, new Hashtable<Variable, FonctionalSymbol>());

        Set<Unify.Substitution> subsSet = new LinkedHashSet<>();
        //pour chaque substitution possible creation de l'object
        for(Map.Entry<Variable,FonctionalSymbol> sub : subsTab.entrySet()){
            subsSet.add(new Unify.Substitution(sub.getKey(),sub.getValue()));
        }

        return new UnifyResult(subsSet,subsTab);
    }


    public static Hashtable<Variable,FonctionalSymbol> unify(Object x, Object y, Hashtable<Variable,FonctionalSymbol> substitutions ) throws UnifyException {

        //Statement sta = ((Statement) x).clone(true), stb = ((Statement) y).clone(true);

        Hashtable<Variable, FonctionalSymbol> subs = null;

        try {
            if(log) {
                System.out.println();
                System.out.println(x + " UNIFICATION " + y);
            }

            subs = unifyA(x, y, substitutions);

            if(log) {
                System.out.println("UNIFICATION REUSSI  " + subs);
            }
            /*
            for (Map.Entry<Variable, FonctionalSymbol> sub : subs.entrySet()) {

                sta.replaceVars(sub.getKey(), sub.getValue());
                stb.replaceVars(sub.getKey(), sub.getValue());
                unify(sta, stb, new Hashtable<Variable, FonctionalSymbol>());

            }
            */

        }catch (UnifyException ue){
            if(log) {
                System.out.println("UNIFICATION RATE ");
            }
            throw ue;

        }catch (StackOverflowError so){
            System.out.println("StackOverflowError");
            System.out.println(x+" "+y);
            System.exit(0);
            throw so;
        }



        for(Map.Entry<Variable,FonctionalSymbol> entry : subs.entrySet()){
            subs.put(entry.getKey(), ((FonctionalSymbol)entry.getValue().clone(true)).replaceValue(subs));
        }

        return subs;
    }

    private static Hashtable<Variable,FonctionalSymbol> unifyA(Object x, Object y, Hashtable<Variable,FonctionalSymbol> substitutions ) throws UnifyException {
        //System.out.println(x+" "+y);

        // System.out.println(x+" "+x.getClass()+" - "+y+" "+y.getClass());

        try {

            if (x.equals(y)) {
                //System.out.println("EQUALS");
                return substitutions;
            } else if (x instanceof Variable) {
                return unifyVar(x, y, substitutions);
            } else if (y instanceof Variable) {
                return unifyVar(y, x, substitutions);
            } else if (x instanceof PredicateSymbol && y instanceof PredicateSymbol
                    || (x instanceof FonctionalSymbol && y instanceof FonctionalSymbol)) {

                // System.out.println("Two predicates "+x+" "+y);

                List<FonctionalSymbol> xArgs = ((Symbol) x).getFonctionalSymbols();
                List<FonctionalSymbol> yArgs = ((Symbol) y).getFonctionalSymbols();

                if (xArgs.isEmpty() && yArgs.isEmpty()) {
                    return unifyA(((Symbol) x).getLabel(), ((Symbol) y).getLabel(), substitutions);
                } else {
                    return unifyA(xArgs, yArgs, unifyA(((Symbol) x).getLabel(), ((Symbol) y).getLabel(), substitutions));
                }

            } else if (x instanceof List && y instanceof List) {
                //System.out.println("l1 "+x+" l2 "+y);
                LinkedList<FonctionalSymbol> x1 = new LinkedList<>((Collection<? extends FonctionalSymbol>) x);
                LinkedList<FonctionalSymbol> y1 = new LinkedList<>((Collection<? extends FonctionalSymbol>) y);

                FonctionalSymbol x1f = x1.removeFirst();
                FonctionalSymbol y1f = y1.removeFirst();

                return unifyA(x1, y1, unifyA(x1f, y1f, substitutions));
            }

        }catch (StackOverflowError error){
            System.out.println("UNIFY A");
            System.out.println(x+" - "+y);
            throw error;
        }

        throw new UnifyException("Aucune correspondance");
    }

    private static Hashtable<Variable,FonctionalSymbol> unifyVar(Object var, Object x, Hashtable<Variable,FonctionalSymbol> substitutions) throws UnifyException {

        //System.out.println(var+" "+x);
        //System.out.println("UNIFY VAR : "+var+" "+x+" "+var.getClass()+" "+x.getClass());


        try {

            if (substitutions.containsKey(var)) {
                //   System.out.println("T1");
                return unifyA(substitutions.get(var), x, substitutions);
            }else if (substitutions.containsKey(x)) {
                //   System.out.println("T2");
                return unifyA(var, substitutions.get(x), substitutions);
            }else if (!occurenceCtrl((Variable) var,(FonctionalSymbol) x,substitutions)) {
                throw new UnifyException("Test occurence failed");
            }else {
                substitutions.put((Variable) var, (FonctionalSymbol) x);
            }

        }catch (StackOverflowError sto){
            System.out.println("UNIFY VAR");
            System.out.println("var : "+var+" - x : "+x);
            System.out.println("SUBSTITUTIONS "+substitutions);
            throw sto;
        }

        return substitutions;
    }


    public static boolean occurenceCtrl(Variable var, FonctionalSymbol value, Hashtable<Variable,FonctionalSymbol> subs){

        if(var.equals(value)){
            return false;
        }else if(value instanceof Variable && subs.containsKey(value)){
            return occurenceCtrl(var,subs.get(value),subs);
        }else if(value instanceof FonctionalSymbol){

            for(FonctionalSymbol args : value.getFonctionalSymbols()){
                if(!occurenceCtrl(var,args,subs)){
                    return false;
                }
            }
        }

        return true;

    }

    public static class UnifyException extends Exception{
        public UnifyException() {
        }

        public UnifyException(String message) {
            super(message);
        }
    }

    public static boolean allVars(Hashtable<Variable,FonctionalSymbol> substitutions){

        if(substitutions.isEmpty()){
            return false;
        }

        //des que l'on trouve un subsitut qui est autre chose q'une variable on retourne faux
        for(FonctionalSymbol sub : substitutions.values()){
            if( !( sub instanceof Variable )) {
                return false;
            }
        }

        //si on ne trouve pas autre chose que des variables ont retourne vrai
        //
        return true;
    }

    public static class UnifyResult {
        private  Set<Substitution> subsSet;
        private  Hashtable<Variable, FonctionalSymbol> subsTab;

        public UnifyResult(Set<Substitution> subsSet, Hashtable<Variable, FonctionalSymbol> subsTab) {
            this.subsSet = subsSet;
            this.subsTab = subsTab;
        }

        public Set<Substitution> getSubsSet() {
            return subsSet;
        }

        public void setSubsSet(Set<Substitution> subsSet) {
            this.subsSet = subsSet;
        }

        public Hashtable<Variable, FonctionalSymbol> getSubsTab() {
            return subsTab;
        }

        public void setSubsTab(Hashtable<Variable, FonctionalSymbol> subsTab) {
            this.subsTab = subsTab;
        }

        @Override
        public String toString() {
            return subsSet.toString();
        }
    }

    public static class Substitution{

        private Variable var;
        private FonctionalSymbol value;

        public Substitution(Variable var, FonctionalSymbol value) {
            this.var = var;
            this.value = value;
        }

        @Override
        public String toString() {
            return var.toString()+"="+value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Substitution that = (Substitution) o;

            if (var != null ? !var.equals(that.var) : that.var != null) return false;
            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            int result = var != null ? var.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    public static void main(String[] args) {

/*
        Statement p = Statement.getStatementFromString("Connait(Jean,x)");
        Statement q = Statement.getStatementFromString("Connait(y,Mere(y))");
*/


/*
        Statement p =  Statement.getStatementFromString("P(x,F(x))");
        Statement q =  Statement.getStatementFromString("P(q,q)");
*/
        //Statement p =  Statement.getStatementFromString("P(x,q)");
        //Statement q =  Statement.getStatementFromString("P(F(x),q)");

        Statement p =  Statement.getStatementFromString("Parent(z,y)");
        Statement q =  Statement.getStatementFromString("Parent(x,z)");

        System.out.println(p+" "+q);

        Unify unify = new Unify();

        try {

            Hashtable<Variable,FonctionalSymbol> substitutions =  unify.unify(p,q, new Hashtable<Variable,FonctionalSymbol>());


            System.out.println("Substitutions : "+substitutions);

            p.replaceVars(substitutions);
            q.replaceVars(substitutions);


            System.out.println(p);
            System.out.println(q);


        }catch (Statement.SubstitutionException se){
            System.out.println(se.getMessage());
        } catch (UnifyException e) {
            System.out.println("ECHEC DE L UNIFICATION "+e.getMessage());
            //e.printStackTrace();
        }

    }

}
