package ai.agent.logic.expressions;

import util.Util;

import java.util.*;

public class Symbol extends Statement {

    protected String label;
    protected boolean value;
    protected boolean init = false;
    protected int totaNeg, totalPos;

    protected String signature;
    protected int arity;
    protected List<FonctionalSymbol> fonctionalSymbols;

    boolean skolem;

    public boolean isSkolem() {
        return skolem;
    }

    public void setSkolem(boolean skolem) {
        this.skolem = skolem;
    }

    public Symbol(String label, boolean value, int arity) {
        this.value = value;
        this.label = label;
        this.arity = arity;
        this.fonctionalSymbols = new ArrayList<>(arity);
    }

    public Symbol(String label, int arity) {
        this(label, true,  arity);
    }

    public Symbol(String label) {
        this(label,true,0);
    }

    public Symbol(String label, boolean value) {
        this(label,value,0);
    }

    public Symbol(String label, boolean value, List<FonctionalSymbol> terms){
        this(label,terms);
        this.value = value;
    }

    public Symbol(String label, List<FonctionalSymbol> terms){
        this(label, terms != null ? terms.size() : 0);

        if(terms != null) {
            for (FonctionalSymbol fonctionalSymbol : terms) {
                this.addFonctionalSymbol(fonctionalSymbol);
            }
        }

    }

    public Symbol(String label, FonctionalSymbol... terms){
        this(label, terms != null ? terms.length : 0);
        if(terms != null) {
            for (FonctionalSymbol fonctionalSymbol : terms) {
                this.addFonctionalSymbol(fonctionalSymbol);
            }
        }

    }

    public int getTotaNeg() {
        return totaNeg;
    }

    public int getTotalPos() {
        return totalPos;
    }

    public void addNeg() {
        this.totaNeg++;
    }

    public void remNeg() {
        this.totaNeg--;
    }

    public void addPos() {
        this.totalPos++;
    }

    public void remPos() {
        this.totalPos--;
    }

    public void initLiteralsCpt(){
        this.totaNeg = 0;
        this.totalPos = 0;
    }

    public boolean isPure(){
        return  this.totalPos == 0 || this.totaNeg == 0;
    }
    //ne fonctionne que si le symbole est pure
    public boolean isNeg(){
        return this.totalPos == 0;
    }

    public void setPurevalue() {
        if(this.isNeg()){
            this.setValue(false);
        }else{
            this.setValue(true);
        }
    }

    @Override
    public boolean isTrue() {
        return this.value;
    }

    @Override
    public String toString() {

        if(this.signature == null) {

            this.signature = label;

            if (this.fonctionalSymbols == null || this.fonctionalSymbols.isEmpty()) {
                return this.signature.replaceAll(" ","");
            }

            this.signature += "( ";

            for (FonctionalSymbol component : this.fonctionalSymbols) {

                this.signature += component.toString() + ", ";
            }

            this.signature = this.signature.substring(0, this.signature.length() - 2);

            this.signature += " )";

            this.signature = this.signature.replaceAll(" ","");

        }

        return this.signature;

    }


    private String noVarStr;

    public String noVarsSignature() {

        if(noVarStr == null) {

            noVarStr = label;

            if (this.fonctionalSymbols == null || this.fonctionalSymbols.isEmpty()) {
                return noVarStr.replaceAll(" ","");
            }

            noVarStr += "( ";
/*
            for (FonctionalSymbol component : this.fonctionalSymbols) {
                if (!this.skolem && component instanceof Variable) {
                    noVarStr += ", ";
                } else {
                    noVarStr += component.noVarsSignature() + ", ";
                }
            }
*/

            for (FonctionalSymbol component : this.fonctionalSymbols) {
                noVarStr += component.noVarsSignature() + ", ";
            }

            noVarStr = noVarStr.substring(0, noVarStr.length() - 2);

            noVarStr += " )";

            this.noVarStr = this.noVarStr.replaceAll(" ","");
        }

        return noVarStr;

    }

    @Override
    public Symbol getSymbol() {
        return this;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isInit() {
        return init;
    }

    @Override
    public String toShortString() {
        return this.toString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public List<TransformAction> getActions() {
        return new LinkedList<>();
    }

    @Override
    public Statement getTransformationResult(TransformAction.TRANFORMATION action,boolean cloneSymbol) {
        return null;
    }


    @Override
    public Statement clone(boolean cloneSymbol) {

        if (cloneSymbol) {

            List<FonctionalSymbol> fonctionalSymbols1 = new LinkedList<>();

            for (FonctionalSymbol fonctionalSymbol : this.fonctionalSymbols) {
                fonctionalSymbols1.add((FonctionalSymbol) fonctionalSymbol.clone(true));
            }

            Symbol symbol = this.getNew(new String(this.label),fonctionalSymbols1);
            symbol.setSkolem(skolem);

            return symbol;
        }

        return this;
    }

    protected Symbol getNew(String label, List<FonctionalSymbol> params){
        return new Symbol(label,params);
    }

    @Override
    protected void addSymbols(List<Symbol> symbols) {
        symbols.add(this);
    }

    @Override
    protected void addConsts(HashSet<FonctionalSymbol> consts) { }

    @Override
    protected void addLiterals(List<Statement> literals) {
        literals.add(this);
    }

    @Override
    protected void addOrClauses(List<Statement> orClauses) {
        orClauses.add(this);
    }

    @Override
    protected void addExistentials(List<Existential> existentials) { }

    @Override
    protected String getUniqueString() {
        return this.toString();
    }

    @Override
    public HashSet<Statement> getLiterals() {
        HashSet<Statement> literals = new HashSet<>();
        literals.add(this);
        return literals;
    }

    public boolean sameId(Symbol b){
        return this.label.equals(b.label);
    }

    public boolean isComplement(Statement complementPotential){

        return this.getSymbol().sameId(complementPotential.getSymbol()) &&
                ((this.getClass().equals(Not.class) && complementPotential instanceof Symbol)
                        ||(this.getClass().equals(Symbol.class) && complementPotential instanceof Not));

    }

    /**
     *
     * Un literal ne peut contenir sont opposés ...
     * */

    @Override
    public boolean containOpposite(){
        return false;
    }

    /**
     * pour un literal il suffit de comparer les deux literaux
     * */
    @Override
    public boolean containOpposite(Statement opposite) {
        return this.equals(opposite);
    }

    public int getArity() {
        return arity;
    }

    public List<FonctionalSymbol> getFonctionalSymbols() {
        return fonctionalSymbols;
    }

    public void setFonctionalSymbols(List<FonctionalSymbol> fonctionalSymbols) {
        this.arity = fonctionalSymbols.size();
        this.fonctionalSymbols = fonctionalSymbols;
    }

    public void addFonctionalSymbol(FonctionalSymbol fonctionalSymbol){
        this.fonctionalSymbols.add(fonctionalSymbol);
    }

    public void setTrem(FonctionalSymbol fonctionalSymbol, int index){
        this.fonctionalSymbols.set(index,fonctionalSymbol);
    }

    public boolean containVar(Variable v){
        for(FonctionalSymbol fonctionalSymbol : fonctionalSymbols){
            if(fonctionalSymbol.containVar(v)){
                return true;
            }
        }

        return false;
    }

    @Override
    protected void replaceVarBySkolemConst(Hashtable<String, FonctionalSymbol> vars) {

        for(int i = 0 ; i < fonctionalSymbols.size() ; i ++){

            FonctionalSymbol fonctionalSymbol = fonctionalSymbols.get(i);

            if(fonctionalSymbol instanceof Variable && vars.containsKey(fonctionalSymbol.getLabel())){
                fonctionalSymbols.set(i, (FonctionalSymbol) vars.get(fonctionalSymbol.getLabel()).clone(true));
            }else if(fonctionalSymbol.getArity() > 0 ){
                fonctionalSymbol.replaceVarBySkolemConst(vars);
            }

        }

        this.signature = null;
    }

/*
    public void replaceVars(Hashtable<Variable, FonctionalSymbol> substitutions) {

        for (int i = 0; i < fonctionalSymbols.size(); i++) {

            FonctionalSymbol fonctionalSymbol = fonctionalSymbols.get(i);

            if (fonctionalSymbol instanceof Variable && substitutions.containsKey(fonctionalSymbol)) {
                ((Variable) fonctionalSymbol).setConstante(substitutions.get(fonctionalSymbol));
                //fonctionalSymbols.set(i, (FonctionalSymbol) substitutions.get(fonctionalSymbol).clone(true));
            } else {
                fonctionalSymbol.replaceVars(substitutions);
            }

        }

        this.signature = null;

    }

*/

    public void replaceVars(Hashtable<Variable, FonctionalSymbol> substitutions)throws SubstitutionException {

        for( Map.Entry<Variable,FonctionalSymbol> entry : substitutions.entrySet()) {
            replaceVars(entry.getKey(),entry.getValue());
        }

    }

    public void replaceVars(Variable v1, FonctionalSymbol sub)throws SubstitutionException {
/*
        if(skolem && sub.skolem){
            System.out.println("-----Insertion d' une fonction de skolem "+sub+" dans une autre "+this+" à travers la variable "+v1);
        }
*/
        for (int i = 0; i < fonctionalSymbols.size(); i++) {
            FonctionalSymbol fonctionalSymbol = fonctionalSymbols.get(i);
            if (fonctionalSymbol instanceof Variable ) {
                if(fonctionalSymbol.label.equals(v1.label)) {


                    if(skolem && sub.skolem){
                        throw new SubstitutionException("Insertion d' une fonction de skolem "+sub+" dans une autre "+this+" à travers la variable "+v1);
                    }


                    fonctionalSymbols.set(i, (FonctionalSymbol) sub.clone(true));

                }
            } else {
                fonctionalSymbol.replaceVars(v1,sub);
            }
        }

        this.signature = null;

    }


    public List<String> getIndexKeys(){

        List<String> argsCombis = new LinkedList();

        if(fonctionalSymbols == null || fonctionalSymbols.isEmpty()){
            argsCombis.add(label);
        }else{
            getArgsCombi( 0, argsCombis,new LinkedList<String>());
        }

        return argsCombis;
    }


    private void getArgsCombi( int i, List<String> argsCombis, LinkedList<String> args) {


        if(i == fonctionalSymbols.size()){

            String finalKey = label+"(";

            for(String arg : args){
                finalKey+= arg+",";
            }

            finalKey = finalKey.substring(0, finalKey.length() - 1);
            finalKey+=")";
            argsCombis.add(finalKey);

            return;

        }

        args.add("");
        getArgsCombi(i+1,argsCombis,args);
        args.removeLast();

        for(String combi : fonctionalSymbols.get(i).getIndexKeys()){
            args.add(combi);
            getArgsCombi(i+1,argsCombis,args);
            args.removeLast();
        }

    }

    @Override
    protected void addVariables(Set<Variable> variables) {

        if(fonctionalSymbols != null) {
            for (FonctionalSymbol fonctionalSymbol : fonctionalSymbols) {
                fonctionalSymbol.addVariables(variables);
            }
        }

    }


    public boolean skolemIntoSkolem(){

        for(FonctionalSymbol fonctionalSymbol : this.fonctionalSymbols){

            if(!fonctionalSymbol.getFonctionalSymbols().isEmpty()){

                if( (this.skolem & fonctionalSymbol.skolem) || fonctionalSymbol.skolemIntoSkolem()){
                    return true;
                }

            }
        }

        return false;
    }

/*
    public Set<String> getPatterns(){
        return getPatterns(false,false);
    }

    //Si on passe par la classe symbole toujours positive on cherche un pattern opposé négatif
    public Set<String> getMatchesPatterns(){
        return getPatterns(true,true);
    }
*/

    private Set<String> patterns;
    public Set<String> getPatterns(boolean neg){
        if(patterns == null) {
            patterns = getPatterns(neg, false);
        }
        return patterns;
    }

    private Set<String> matchesPatterns;
    public Set<String> getMatchesPatterns(boolean neg){
        if(matchesPatterns == null) {
            matchesPatterns = getPatterns(neg, true);
        }
        return matchesPatterns;
    }


    public Set<String> getPatterns(boolean neg, boolean complementaire){

        String sign = neg ? "!" : "";

        LinkedList<String> pattern = new LinkedList<>();

        pattern.add(sign+label);

        Set<String> patterns = new LinkedHashSet<>();

        patterns.add(sign+this.toString());

        generatePatterns(pattern, patterns,0,complementaire);

        return patterns;

    }

    private void generatePatterns(LinkedList<String> pattern, Set<String> matchespattern, int i, boolean complementaire){

            if(i == this.fonctionalSymbols.size()){

                StringBuffer patternStr = new StringBuffer();

                patternStr.append(pattern.get(0));
                patternStr.append('(');

                for(int p = 1 ; p < pattern.size() ; p ++){
                    patternStr.append(pattern.get(p));
                    patternStr.append(',');
                }

                patternStr.setCharAt(patternStr.length()-1,')');

                matchespattern.add(patternStr.toString());

                return;
            }

            FonctionalSymbol currentSymbol = this.fonctionalSymbols.get(i);
            //constante
            if(currentSymbol.getFonctionalSymbols() == null || currentSymbol.getFonctionalSymbols().isEmpty()){

                //variable
                if(currentSymbol instanceof Variable){

                    pattern.add("");
                    this.generatePatterns(pattern,matchespattern,i+1,complementaire);
                    pattern.removeLast();

                    if(complementaire) {
                        pattern.add("?");
                        this.generatePatterns(pattern, matchespattern, i + 1, complementaire);
                        pattern.removeLast();
                    }

                }//constante
                else{
                    pattern.add(currentSymbol.getLabel());
                    this.generatePatterns(pattern,matchespattern,i+1,complementaire);
                    pattern.removeLast();

                    if(complementaire) {
                        pattern.add("");
                    }else{
                        pattern.add("?");
                    }

                    this.generatePatterns(pattern,matchespattern,i+1,complementaire);
                    pattern.removeLast();
                }

            }//terme commpose
            else{

                Set<String> subPatterns = currentSymbol.getPatterns(false, complementaire);

                for(String subPattern : subPatterns){

                    pattern.add(subPattern);
                    this.generatePatterns(pattern,matchespattern,i+1,complementaire);
                    pattern.removeLast();

                    if(complementaire) {
                        pattern.add("");
                    }else{
                        pattern.add("?");
                    }

                    this.generatePatterns(pattern,matchespattern,i+1,complementaire);
                    pattern.removeLast();

                }

            }

    }


    public static void main(String[] args) {

        PredicateSymbol predicat = (PredicateSymbol) Statement.getStatementFromString("A(B,y)");

        Set<String> matchesPatterns = predicat.getMatchesPatterns(true);

        for(String pattern : matchesPatterns){
            System.out.println(pattern);
        }

        System.out.println();

        Set<String> patterns = predicat.getPatterns(false);

        for(String pattern : patterns){
            System.out.println(pattern);
        }

    }


}
