package ai.agent.logic.expressions;

import util.Util;

import java.util.*;

public abstract class Statement {


    protected Statement previousState;
    protected Statement notNormalized;
    protected Statement p1,p2,l1,l2;
    protected Hashtable<Variable,FonctionalSymbol> subs, subs2;
    protected String facto;

    public String getFacto() {
        return facto;
    }

    public void setFacto(String facto) {
        this.facto = facto;
    }

    protected HashSet<Statement> literals;
    protected List<Statement> literalList;

    protected Statement parent;

    protected String uniqueString;

    protected boolean trueClause;

    protected int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private static final String HORN = "HORN", DEFINE = "DEFINE";

    private String hornType;

    //symbole
    protected boolean initialized;

    /*-- FONCTIONNE POUR LES CLAUSES DEFINIS OU UN SEUL LITERAL COMPOSE LA CONCLUSION*/

    protected int cptrFacts = 0;


    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public abstract boolean isTrue();

    public abstract List<TransformAction> getActions();

    public abstract Statement getTransformationResult(TransformAction.TRANFORMATION action, boolean cloneSymbol);

    public abstract Statement clone(boolean cloneSymbol);

    public boolean isTrueClause() {
        return trueClause;
    }

    public void setTrueClause(boolean trueClause) {
        this.trueClause = trueClause;
    }

    //public abstract List<Statement> getStatements();


    public Statement getNotNormalized() {
        return notNormalized;
    }

    public void setNotNormalized(Statement notNormalized) {
        this.notNormalized = notNormalized;
    }

    public Statement getP1() {
        return p1;
    }

    public void setP1(Statement p1) {
        this.p1 = p1;
    }

    public Statement getP2() {
        return p2;
    }

    public void setP2(Statement p2) {
        this.p2 = p2;
    }

    public Hashtable<Variable, FonctionalSymbol> getSubs() {
        return subs;
    }

    public void setSubs(Hashtable<Variable, FonctionalSymbol> subs) {
        this.subs = subs;
    }

    public Hashtable<Variable, FonctionalSymbol> getSubs2() {
        return subs2;
    }

    public void setSubs2(Hashtable<Variable, FonctionalSymbol> subs2) {
        this.subs2 = subs2;
    }

    public Statement getL1() {
        return l1;
    }

    public void setL1(Statement l1) {
        this.l1 = l1;
    }

    public Statement getL2() {
        return l2;
    }

    public void setL2(Statement l2) {
        this.l2 = l2;
    }

    public String getHornType() {
        return hornType;
    }

    public void setHornType(String hornType) {
        this.hornType = hornType;
    }

    public Statement getPreviousState() {
        return previousState;
    }

    public void setPreviousState(Statement previousState) {
        this.previousState = previousState;
    }

    /**
     *
     * */


    public Statement horn(){

        if(getLiteralList().size() == 1){
            return getLiteralList().iterator().next();
        }

        HashSet<Statement> neg = new LinkedHashSet<>();
        HashSet<Statement> pos = new LinkedHashSet<>();

        for(Statement literal : getLiteralList()){

            if(literal instanceof Not){
                neg.add(literal);
            }else{
                pos.add(literal);
            }

        }

        //exactement un positif clause defini
        if(pos.size() == 1 ){

            LinkedList<Statement> left = new LinkedList<>();
            //copie tout le contenu des literaux negatifs
            for(Statement n : neg ){
                left.add(n.getSymbol().clone(true));
            }

            Statement st1 = new And().getStatement(left);
            Statement st2 = pos.iterator().next();
            Statement define = new Imply(st1,st2);
            define.setHornType(DEFINE);
            return define;

        }//tous négatif clause de horn
        else if(pos.isEmpty()){


            LinkedList<Statement> left = new LinkedList<>();
            Iterator<Statement> negs = neg.iterator();
            //copie tout le contenu des literaux negatifs
            for(int i = 0 ; i < neg.size() - 1 ; i ++){
                left.add(negs.next().clone(true));
            }

            Statement st1 = new And().getStatement(left);
            Statement st2 = negs.next();

            Statement horn = new Imply(st1,st2);
            horn.setHornType(HORN);
            return horn;

        }
        else{
            return new EmptyStatement();
        }


    }

    public Statement isUnitaryClause( HashSet<Statement> model ){

        int totalLiterals = this.getLiteralList().size();

        if(totalLiterals == 1 ){

            Statement literal = this.getLiteralList().iterator().next();

            if(!model.contains(literal.getSymbol())){
                literal.getSymbol().setValue(literal instanceof Symbol);
                return literal.getSymbol();
            }

        }

        int totalFalse = 0;
        int totalTrue = 0;
        Statement literalNotInit = null;
        for( Statement literal : this.getLiteralList()){
            if( model.contains(literal.getSymbol()) ){

                if(!literal.isTrue()) {
                    totalFalse++;
                }else{
                    totalTrue++;
                }

            }else{
                //si la clause ne contient qu'un symbole non initalisé et que tout les autres sont faux
                //il sera stocké dans cette variable
                literalNotInit = literal;
            }
        }

        //tout les literaux sauf 1 sont faux et celui restant n'est pas vrai donc pas dans le model
        if( (totalLiterals - totalFalse) == 1 && totalTrue == 0 ){
            literalNotInit.getSymbol().setValue(literalNotInit instanceof Symbol);
            return literalNotInit.getSymbol();
        }else{
            return null;
        }

    }

    public Symbol isUnitaryClauseTest(){

        int totalLiterals = this.getLiteralList().size();

        if(totalLiterals == 1 ){

            Statement literal = this.getLiteralList().iterator().next();

            if(!literal.getSymbol().isInitialized()){
                literal.getSymbol().setValue(literal instanceof Symbol);
                return literal.getSymbol();
            }

        }

        int totalFalse = 0;
        int totalTrue = 0;
        Statement literalNotInit = null;
        for( Statement literal : this.getLiteralList()){
            if( literal.getSymbol().isInitialized() ){

                if(!literal.isTrue()) {
                    totalFalse++;
                }else{
                    totalTrue++;
                }

            }else{
                //si la clause ne contient qu'un symbole non initalisé et que tout les autres sont faux
                //il sera stocké dans cette variable
                literalNotInit = literal;
            }
        }

        //tout les literaux sauf 1 sont faux et celui restant n'est pas vrai donc pas dans le model
        if( (totalLiterals - totalFalse) == 1 && totalTrue == 0 ){
            literalNotInit.getSymbol().setValue(literalNotInit instanceof Symbol);
            return literalNotInit.getSymbol();
        }else{
            return null;
        }

    }


    private Hashtable<String,List<Statement>> patternsliterals;
    public List<Statement> getMatchLiterals(String pattern){

        if(patternsliterals == null) {
            patternsliterals = new Hashtable<>();
            //System.out.println("STATEMENT "+this);
            for (Statement literal : getLiteralList()) {
               // System.out.println( literal.getSymbol().getPatterns(literal instanceof Not));
                for(String p : literal.getSymbol().getPatterns(literal instanceof Not)){
                    //System.out.println("Pattern : "+p);
                    List<Statement> literals = patternsliterals.get(p);

                    if(literals == null){
                        literals = new LinkedList<>();
                        patternsliterals.put(p,literals);
                    }

                    literals.add(literal);

                }

            }

        }

        return patternsliterals.get(pattern);

    }

    public Collection<Statement> getLiterals() {

        if(this.literals == null){
            this.initLiteralsSet();
        }

        return this.literals;
    }

    protected Set<Variable> variables;

    public Set<Variable> getAllVariables(){

        if(variables == null){

            this.variables = new LinkedHashSet<>();
            this.addVariables(this.variables);

        }

        return variables;
    }

    protected abstract void addVariables(Set<Variable> variables);

    protected abstract void addLiterals(List<Statement> literalList);

    public abstract Symbol getSymbol();

    private Hashtable<String,Statement> tabMaskLiteral;

    public Hashtable<String, Statement> getTabMaskLiteral() {
        if(tabMaskLiteral == null){
            tabMaskLiteral = new Hashtable<>();
            for(Statement literal: getLiteralList()){
                tabMaskLiteral.put(literal.noVarsSignature(),literal);
            }
        }
        return tabMaskLiteral;
    }


    public void initLiteralLIst(){

        literalList = new LinkedList<>();
        this.addLiterals(literalList);

        Collections.sort(literalList, new Comparator<Statement>() {
            @Override
            public int compare(Statement o1, Statement o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });


        for(Statement literal : getLiteralList()) {
            if (literal instanceof Not) {
                literal.litSign = "!" + literal.getSymbol().label;
                literal.litSignComp = literal.getSymbol().label;
            } else {
                literal.litSign = literal.getSymbol().label;
                literal.litSignComp = "!" + literal.getSymbol().label;
            }
        }



    }

    public List<Statement> getLiteralList(){
        if(literalList == null){
            this.initLiteralLIst();
        }
        return literalList;
    }

    public void initLiteralsSet() {

        this.literals = new LinkedHashSet<>(getLiteralList());

    }

    private Hashtable<String,List<Statement>> literalsTab;

    private String litSign, litSignComp;

    public Hashtable<String, List<Statement>> getLiteralsTab() {

        if(this.literalsTab == null) {

            this.literalsTab = new Hashtable<>();

            for(Statement literal : this.getLiteralList()) {

                if(literal instanceof Not){
                    literal.litSign = "!"+literal.getSymbol().label;
                    literal.litSignComp = literal.getSymbol().label;
                }else{
                    literal.litSign = literal.getSymbol().label;
                    literal.litSignComp = "!"+literal.getSymbol().label;
                }

                //System.out.println(literalsTab+" "+literal.litSign);
                if(!literalsTab.containsKey(literal.litSign)){
                    literalsTab.put(literal.litSign,new LinkedList<Statement>());
                }

                literalsTab.get(literal.litSign).add(literal);
            }

        }
        return literalsTab;

    }

    public String getLitSign() {
        return litSign;
    }

    public String getLitSignComp() {
        return litSignComp;
    }


    private List<Symbol> symbols;
    public List<Symbol> getSymbols(){

        if(this.symbols == null) {
            this.symbols = new LinkedList<>();
            this.addSymbols(this.symbols);
        }

        return this.symbols;
    }


    protected abstract void addSymbols( List<Symbol> symbols );

    private HashSet<FonctionalSymbol> consts;
    public HashSet<FonctionalSymbol> getConsts(){

        if(this.consts == null) {
            this.consts = new LinkedHashSet<>();
            this.addConsts(this.consts);
        }

        return this.consts;
    }

    protected abstract void addConsts(HashSet<FonctionalSymbol> consts);


    public List<Statement> getOrClauses(){
        List<Statement> orClauses = new LinkedList<>();
        this.addOrClauses(orClauses);
        return orClauses;
    }

    protected abstract void addOrClauses(List<Statement> orClauses);

    public abstract String noVarsSignature();

    protected List<Existential> getExistentials(){
        List<Existential> existentials = new LinkedList<>();
        this.addExistentials(existentials);
        return existentials;
    }

    protected abstract void addExistentials(List<Existential> existentials);

    protected abstract void replaceVarBySkolemConst(Hashtable<String, FonctionalSymbol> vars);


    private void initUniqueString(){
        this.uniqueString = "";
        for(Statement statement : this.getLiteralList()){
            this.uniqueString += statement.toShortString();
        }
    }

    protected String getUniqueString(){

        if(this.uniqueString == null ){
            this.initUniqueString();
        }

        return this.uniqueString;
    }

    @Override
    public int hashCode() {
        return this.getUniqueString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!this.getClass().equals(obj.getClass()))
            return false;

        return this.getUniqueString().equals(((Statement)obj).getUniqueString());
    }


    /**GET UNIQUE STRING recupere une chaine de charactere reprenant
     * tout les literaux positifs et négatif d'une expression
     *
     * **/

    public abstract String toShortString();

    public boolean isEmpty() {
        return this.getLiteralList().isEmpty();
    }


    /**
     * Verifie si une clause contient un literaux opposés appartenant à une autre clause
     * */
    public boolean containOpposite(Statement opposite) {

        boolean rs = this.getLiterals().contains(opposite);

        return rs;
    }


    /**
     * Verifie si une clause contient des literaux opposés au sein d'une même clause
     * rendant la clause vrai et dont inutile
     * */
    public boolean containOpposite(){

        for( Statement statement : this.getLiterals() ){

            Statement opposite = null;

            if(statement instanceof Symbol){
                opposite = new Not(statement);
            }else if (statement instanceof Not){
                opposite = ((Not)statement).getSt1();
            }
            //si la clause 2 contient la contraposée
            if(opposite != null && this.getLiterals().contains(opposite)){
                return true;
            }

        }

        return false;

    }

    public static void show(Collection<Statement> statements){

        System.out.println("\n\n================STATEMENTS "+statements.size()+" ===================\n\n");



        for(Statement statement : statements)
            System.out.println(statement);

    }


    static int i;
    static boolean log = false;
    static Hashtable<String,Variable> vars;
    static Hashtable<String,Integer> varsCpt = new Hashtable<>();
    public static Hashtable<String,FonctionalSymbol> constantes = new Hashtable<>();

    public static Statement getStatementFromString(String statementStr){

        statementStr = statementStr.replaceAll(" ","");

        // ∀ : _A
        // ∃ : _E
        // ⇒ : >
        // ⇔ : <>
        // ∧ : &
        // ∨ :
        // ¬ : !

        // System.out.println("A "+statementStr);

        statementStr = statementStr.replaceAll("_A","∀");
        statementStr = statementStr.replaceAll("_E","∃");
        statementStr = statementStr.replaceAll("<>","⇔");
        statementStr = statementStr.replaceAll(">","⇒");
        statementStr = statementStr.replaceAll("&","∧");
        statementStr = statementStr.replaceAll("\\|","∨");
        statementStr = statementStr.replaceAll("!","¬");

        // System.out.println("B "+statementStr);

        i = 0;
        vars = new Hashtable<>();
        return loadStatement(statementStr, 0);

    }

    private static List<FonctionalSymbol> getFonctionalSymbols(String statementStr){

        List<FonctionalSymbol> fonctionalSymbols = new ArrayList<>();

        while( ")]}".indexOf(statementStr.charAt(i)) == -1){

            char car = statementStr.charAt(i++);
            //System.out.println();
            //System.out.println("CAR : "+car);
            if( "([{".indexOf(car) >= 0 ){
                List<FonctionalSymbol> subList = getFonctionalSymbols(statementStr);
                fonctionalSymbols.get(fonctionalSymbols.size()-1).setFonctionalSymbols(subList);
            }else if(Character.isAlphabetic(car) && Character.isUpperCase(car)){

                String label = ""+car;

                while("[](){},".indexOf(statementStr.charAt(i)) == -1){
                    label+=statementStr.charAt(i++);
                }

                FonctionalSymbol fonctionalSymbol;

                //un parenthese ouvrante suit le symbole il s'agit d'un symbole fonctionelle composé
                if("[({".indexOf(statementStr.charAt(i)) != -1){
                    fonctionalSymbol = new FonctionalSymbol(label);
                    //il s'agit d'une constante
                }else{
                    if(constantes.containsKey(label)){
                        fonctionalSymbol = constantes.get(label);
                    }else{
                        fonctionalSymbol = new FonctionalSymbol(label);
                        constantes.put(label,fonctionalSymbol);
                    }
                }

                //ajout le symbole fonctionnel 0 aire si c'est une constante
                //si une parenthese precede le label une sous liste sera crée recursivement est charge dans le symbole
                //par après
                //System.out.println("SYMBOLE FONCTIONEL "+label);
                fonctionalSymbols.add(fonctionalSymbol);

            }else if(Character.isAlphabetic(car) && Character.isLowerCase(car)){

                String label = ""+car;

                while("])},".indexOf(statementStr.charAt(i)) == -1){
                    label+=statementStr.charAt(i++);
                }

                Variable var = new Variable(label);

                fonctionalSymbols.add(var);
            }

        }

        i++;

        return fonctionalSymbols;

    }

    private static Statement loadStatement(String statementStr, int d){

        String ident = null;
        if(log)
            ident = Util.getIdent(d);

        Statement statement = null,
                st1 = null,
                st2 = null;

        boolean negSt1 = false, negSt2 = false;
        if(log)
            System.out.println(ident+" LOAD "+statementStr.charAt(i));

        while( i != statementStr.length() && statementStr.charAt(i) != ')' ){

            char car = statementStr.charAt(i++);

            if(car == '¬'){
                if(st1 == null) {
                    negSt1 = true;
                }else{
                    negSt2 = true;
                }
            }else if(car == '('){

                if(st1 == null){
                    st1 = loadStatement(statementStr,d+1);
                }else{
                    st2 = loadStatement(statementStr,d+1);
                }

            }else if( car == '∀' ){
                if(log)
                    System.out.println(ident+"OPERATEUR UNIVERS");
                statement = new Universal();
                i++;//passe la parenthese ouvrante qui suit obligatoirement
                ((Universal)statement).setVariables(getFonctionalSymbols(statementStr));
                if(log)
                    System.out.println(statement+" "+statementStr.charAt(i));
            }else if( car == '∃'){
                if(log)
                    System.out.println(ident+"OPERATEUR EXIST");
                statement = new Existential();
                i++;//passe la parenthese ouvrante qui suit obligatoirement
                ((Existential)statement).setVariables(getFonctionalSymbols(statementStr));
            }else if(car == '⇒' ){
                if(log)
                    System.out.println(ident+"OPERATEUR IMPLY");
                statement = new Imply();
            }else if(car == '⇔'){
                if(log)
                    System.out.println(ident+"OPERATEUR DOUBLE IMPLY");
                statement = new DoubleImply();
            }else if(car == '∧'){
                if(log)
                    System.out.println(ident+"OPERATEUR AND");
                statement = new And();
            }else if(car == '∨'){
                if(log)
                    System.out.println(ident+"OPERATEUR OR");
                statement = new Or();
            }else if(Character.isUpperCase(car) && Character.isAlphabetic(car)){
                if(log)
                    System.out.println(ident+"PREDICAT");
                //index du prochain caractere ( à partir du caractere courant
                int i2 = statementStr.indexOf('(',i-1);
                //recuperation du label
                String label = statementStr.substring(i-1, i2);
                //on repart juste aprèe la parenthese ouvrante
                i = i2 +1;
/*
                String label = ""+car;

                while(statementStr.charAt(i) != '('){
                    car = statementStr.charAt(i++);
                    label+=car;
                }

               // System.out.println();
               // System.out.println("LABEL PREDICAT : "+label);
                i++;//on incremente la parenthese ouvrante qui suit le label du predicat

*/


                List<FonctionalSymbol> fonctionalSymbols = getFonctionalSymbols(statementStr);

                if(st1 == null){
                    st1 = new PredicateSymbol(label, fonctionalSymbols);
                    //System.out.println("CREATION ST1 "+st1);
                }else{
                    st2 = new PredicateSymbol(label, fonctionalSymbols);
                    // System.out.println("CREATION ST2 "+st2);
                }
                if(log)
                    System.out.println(ident+"PREDICAT "+st1+" "+st2);

            }

        }

        i++;

        if(statement instanceof UnaryStatement){
            if(log)
                System.out.println(ident+"OneStatementExpression");
            ((UnaryStatement) statement).setSt1(st1);

            if(negSt1){

                statement = new Not(statement);
            }

        }else if(statement instanceof BinaryStatement){
            if(log)
                System.out.println(ident+"TwoStatementsExpression");

            ((BinaryStatement) statement).setSt1(negSt1 ? new Not(st1) : st1);
            ((BinaryStatement) statement).setSt2(negSt2 ? new Not(st2) : st2);

        }//si tout un enonce ne fait pas parti d'une operation et est
        // entouré de parenthese pouvant être precede d'un Not
        else if (statement == null){
            if(log)
                System.out.println(ident+"NULL STATEMENT");
            if(negSt1) {
                statement = new Not(st1);
            }else{
                statement = st1;
            }
            if(log)
                System.out.println(ident+"RETOUR STATEMENT "+statement);
        }

        return statement;

    }

    public static void indexFusion(Hashtable<String, List<Statement>> newFactIndex, Hashtable<String, List<Statement>> factsIndex) {

        for(Map.Entry<String,List<Statement>> entry :  newFactIndex.entrySet()){

            List<Statement> facts = factsIndex.get(entry.getKey());

            if(facts == null){
                factsIndex.put(entry.getKey(), entry.getValue());
            }else {
                facts.addAll(entry.getValue());
            }

        }

    }

    public static Hashtable<String,List<Statement>> getFactsIndex(List<Statement> facts) {
        Hashtable<String, List<Statement>> index = new Hashtable<String, List<Statement>>();
        addFactsToIndex(facts,index);
        return index;
    }

    public static void addFactsToIndex(List<Statement> facts, Hashtable<String, List<Statement>> indexfacts) {
        for (Statement fact : facts) {
            addFact(fact,indexfacts);
        }
    }

    public static void addFact(Statement fact, Hashtable<String, List<Statement>> indexfacts) {

            List<String> keys = ((PredicateSymbol) fact).getIndexKeys();

            for (String key : keys) {

                List<Statement> statementList = indexfacts.get(key);

                if (statementList == null) {
                    statementList = new LinkedList<>();
                    indexfacts.put(key, statementList);
                }

                statementList.add(fact);

            }

    }


    public static void showFactsIndexKeys(Hashtable<String,List<Statement>> index){

        System.out.println("\nINDEX DES FAITS\n");

        for( String key : index.keySet()){
            System.out.println();
            System.out.println("KEY "+key);
        }

    }

    public static void showFactsIndex(Hashtable<String,List<Statement>> index){

        System.out.println("\nINDEX DES FAITS\n");

        for( String key : index.keySet()){
            System.out.println();
            System.out.println("KEY "+key);

            for(Statement facts : index.get(key)){
                System.out.println("FAIT : "+facts);
            }

        }

    }



    public class SubstitutionException extends Exception{

        public SubstitutionException() {
        }

        public SubstitutionException(String message) {
            super(message);
        }

        public SubstitutionException(String message, Throwable cause) {
            super(message, cause);
        }

        public SubstitutionException(Throwable cause) {
            super(cause);
        }

        public SubstitutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public void replaceVars(Hashtable<Variable, FonctionalSymbol> substitutions)throws SubstitutionException {

        for( Map.Entry<Variable,FonctionalSymbol> entry : substitutions.entrySet()) {
            for (Symbol symbol : getSymbols()) {
                symbol.replaceVars(entry.getKey(),entry.getValue());
            }
        }

        this.uniqueString = null;
    }


    public void replaceVars(Variable v1, FonctionalSymbol v2)throws SubstitutionException {
        for(Symbol symbol : getSymbols()) {
            symbol.replaceVars(v1,v2);
        }
    }


    public static Hashtable<String,Integer> varCptr = new Hashtable<>();

    public static void normalize(List<Statement> axioms)throws SubstitutionException{
            //pour chaque clause
            for(Statement axiom : axioms){
                normalize(axiom);
            }
    }

    public static void normalize(Statement axiom)throws SubstitutionException{

        Hashtable<Variable,FonctionalSymbol> subs = new Hashtable<>();
        //on recupere un set des variables de la clause
        //System.out.println("normalize "+axiom+" "+axiom.getAllVariables());
        for( Variable var : axiom.getAllVariables() ){
            //si la variable n'est pas connu
            String varLab = var.toString().substring(0,1);
            if(!varCptr.containsKey(varLab)){
                varCptr.put(varLab,0);
            }else{
                varCptr.put(varLab,varCptr.get(varLab)+1);
            }

            //on creer une variable avec un nouveau label en fonction du nombre d'apparition de la variable
            String label = varLab+varCptr.get(varLab);
            Variable newVar = new Variable(label);
            subs.put(var,newVar);
        }

        axiom.replaceVars(subs);
        //System.out.println("normalized "+axiom);
        axiom.uniqueString = null;
    }
}
