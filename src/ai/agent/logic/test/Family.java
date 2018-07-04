package ai.agent.logic.test;

import ai.agent.logic.CNF_convert;
import ai.agent.logic.PlSolveFirstOrderV2;
import ai.agent.logic.PlSolveFirstOrderV3LiteralPattern;
import ai.agent.logic.expressions.*;
import util.Util;

import java.util.*;

public class Family {

    private static boolean show = false;

    public static void main(String[] args) {

        // ∀ : _A
        // ∃ : _E
        // ⇒ : >
        // ⇔ : <>
        // ∧ : &
        // ∨ :
        // ¬ : !

        //clauseHorn();
        solve();

      //  indexFact();
    }


    private static void show(Collection<Statement> statements){
        for( Statement statement : statements ){
            System.out.println(statement);
        }
    }

    private static void solve(){

        List<Statement> axioms = getAxioms();

        List<Statement> facts = getFacts();


        //Conjoint(Sarah,Andrew)
        //Conjoint(Sophie,Edward)

        //Statement find = Statement.getStatementFromString("!Ancetre(ElizabethI,Harry)");

        //Statement find = Statement.getStatementFromString("Egal(John,Jean)");

        Statement find = Statement.getStatementFromString("!BeauParent(Diana,Bastard)");

        System.out.println("\n-------------ENONCE A PROUVER------------\n"+new Not(find));


        for(Statement axiom : axioms)
            System.out.println(axiom.hashCode()+" "+axiom.toString().hashCode());

        Set<Statement> useFullAxioms = new LinkedHashSet<>(axioms);
       // Set<Statement> useFullAxioms = usefullAxioms(find,axioms);
        System.out.println("\n---------------AXIOMES UTILES ("+useFullAxioms.size()+")-----------------\n");

        show(useFullAxioms);

        Set<Statement> useFullFacts = new LinkedHashSet<>(facts);
        //Set<Statement> useFullFacts = usefullFacts(find,facts);
        System.out.println("\n---------------FAITS UTILES ("+ useFullFacts.size()+")-----------------\n");

        show(useFullFacts);

        List<Statement> useFullStatements = new LinkedList<>();
        useFullStatements.addAll(useFullAxioms);

        System.out.println("\n-------------CNF------------\n");

        List<Statement> CNFstatements  = new LinkedList<>();

        for(Statement statement : useFullStatements){
            System.out.println("CONVERSION "+statement);
            List<Statement> disjonctions = CNF_convert.convertAndClone(statement);
            CNFstatements.addAll(disjonctions);

            for(Statement disjonction : disjonctions)
                System.out.println("RS "+disjonction);

            System.out.println();
        }

        System.out.println();

        try {

            Statement.normalize(CNFstatements);
        } catch (Statement.SubstitutionException e) {
            System.out.println(e.getMessage());

        }

        System.out.println("\n-------------CNF NORMALISE------------\n");

        for(Statement statement : CNFstatements){
            System.out.println(" "+statement);
        }

        CNFstatements.addAll(useFullFacts);

        CNFstatements.add(find);

        System.out.println("\n-------------RESULTAT------------\n");

        Util.initTime();
        if(PlSolveFirstOrderV3LiteralPattern.valid(CNFstatements)){

        //if(PlSolveFirstOrderV3LiteralPattern.valid(CNFstatements)){
            System.out.println("ENONCE "+new Not(find)+" PROUVE");
        }else{
            System.out.println("ENONCE "+new Not(find)+" NON PROUVABLE");
        }

        Util.printTimeDelta();

    }

    private static List<Statement> getFacts() {

        List<Statement> facts = new LinkedList<>();

        String[] man = new String[]{"George", "Philip", "EdwardI", "Charles", "Mark", "Andrew",
                "Edward", "William", "Harry", "Peter", "James", "Bastard"};

        String[] woman = new String[]{
                "ElizabethI", "ElizabethII", "Frances", "Diana", "Anne", "Sophie", "Sarah", "Zara", "Beatrice", "Eugenie", "Louise", "Prostitue"};

        ArrayList<String> people = new ArrayList<>();
        people.addAll(Arrays.asList(man));
        people.addAll(Arrays.asList(woman));


        for(String men : man){
            facts.add(Statement.getStatementFromString("Masculin("+men+")"));
        }

        for(String women : woman){
            facts.add(Statement.getStatementFromString("Feminin("+women+")"));
        }



        for(int i = 0 ; i < people.size() ; i ++)
            for(int j = i + 1 ; j < people.size() ; j ++)
                facts.add(Statement.getStatementFromString("!Egal("+people.get(i)+","+people.get(j)+")"));


        /*
        facts.add(Statement.getStatementFromString("Conjoint(George,Elizabeth I)"));
        facts.add(Statement.getStatementFromString("Conjoint(Edward I,Frances)"));
        facts.add(Statement.getStatementFromString("Conjoint(Philip,Elizabeth II)"));
*/
        facts.add(Statement.getStatementFromString("Conjoint(Charles,Diana)"));
/*
        facts.add(Statement.getStatementFromString("Conjoint(Mark,Anne)"));
        facts.add(Statement.getStatementFromString("Conjoint(Sarah,Andrew)"));
        facts.add(Statement.getStatementFromString("Conjoint(Sophie,Edward)"));
*/



        /*
        facts.add(Statement.getStatementFromString("Parent(George,Elizabeth II)"));
        facts.add(Statement.getStatementFromString("Parent(Elizabeth I,Elizabeth II)"));
        facts.add(Statement.getStatementFromString("Parent(George,Margaret)"));
        facts.add(Statement.getStatementFromString("Parent(Elizabeth I,Margaret)"));

        facts.add(Statement.getStatementFromString("Parent(Edward,Diana)"));
        facts.add(Statement.getStatementFromString("Parent(Frances,Diana)"));

        facts.add(Statement.getStatementFromString("Parent(Elizabeth II,Charles)"));
        facts.add(Statement.getStatementFromString("Parent(Philip,Charles)"));
        facts.add(Statement.getStatementFromString("Parent(Elizabeth II,Anne)"));
        facts.add(Statement.getStatementFromString("Parent(Philip,Anne)"));
        facts.add(Statement.getStatementFromString("Parent(Elizabeth II,Andrew)"));
        facts.add(Statement.getStatementFromString("Parent(Philip,Andrew)"));
        facts.add(Statement.getStatementFromString("Parent(Elizabeth II,Edward)"));
        facts.add(Statement.getStatementFromString("Parent(Philip,Edward)"));
*/
        facts.add(Statement.getStatementFromString("Parent(Diana,William)"));
        facts.add(Statement.getStatementFromString("Parent(Charles,William)"));
        facts.add(Statement.getStatementFromString("Parent(Diana,Harry)"));
        facts.add(Statement.getStatementFromString("Parent(Charles,Harry)"));
        facts.add(Statement.getStatementFromString("Parent(Charles,Bastard)"));
        facts.add(Statement.getStatementFromString("Parent(Prostitue,Bastard)"));

/*
        facts.add(Statement.getStatementFromString("Parent(Anne,Peter)"));
        facts.add(Statement.getStatementFromString("Parent(Mark,Peter)"));
        facts.add(Statement.getStatementFromString("Parent(Anne,Zara)"));
        facts.add(Statement.getStatementFromString("Parent(Mark,Zara)"));

        facts.add(Statement.getStatementFromString("Parent(Andrew,Beatrice)"));
        facts.add(Statement.getStatementFromString("Parent(Sarah,Beatrice)"));
        facts.add(Statement.getStatementFromString("Parent(Andrew,Eugenie)"));
        facts.add(Statement.getStatementFromString("Parent(Sarah,Eugenie)"));

        facts.add(Statement.getStatementFromString("Parent(Edward,Louise)"));
        facts.add(Statement.getStatementFromString("Parent(Sophie,Louise)"));
        facts.add(Statement.getStatementFromString("Parent(Edward,James)"));
        facts.add(Statement.getStatementFromString("Parent(Sophie,James)"));
*/
/*
        facts.add(Statement.getStatementFromString("Egal(Bastard,Jean)"));
        facts.add(Statement.getStatementFromString("Egal(Bastard,EnfantIllegitime)"));

        facts.add(Statement.getStatementFromString("Egal(Jo,John)"));
        facts.add(Statement.getStatementFromString("Egal(Jojo,John)"));
*/

        if(show) {
            System.out.println("\n-------------FAITS (" + facts.size() + ")------------\n");

            for (Statement statement : facts) {
                System.out.println(statement.toString().replaceAll(" ", ""));
            }
        }

        return facts;

    }

    private static List<Statement> getAxioms(){

        List<Statement> axioms = new LinkedList<>();

        //axioms.add(Statement.getStatementFromString("_A[a,b,c,d,e]( ( ((Parent(b,a) & Parent(c,a)) & Parent(d,a)) & Parent(e,a) ) > ( Egal(b,c) & Egal(d,e) ) )"));

        //axioms.add(Statement.getStatementFromString("_A[a,b,c]( (Conjoint(a,c) & Conjoint(b,c) ) >  Egal(a,b)  )"));
        //axioms.add(Statement.getStatementFromString("_A[a,b]( Conjoint(b,a) <>  Conjoint(a,b)  )"));

/*
        //Toute les personnes du domaine sont differentes
        ////statementList.add(Statement.getStatementFromString("_A[x,y]( !Egal(x,y) )"));
        //Toute personne masuline n'est pas feminine
        axioms.add(Statement.getStatementFromString("_A[x]( Masculin(x) <> !Feminin(x) )"));
        //Deux personne sont de la meme fraterie si et seulement si ils ont un parent en commum et sont différents
        //axioms.add(Statement.getStatementFromString("_A[x,y]( Fraterie(x,y) <> (_E[z]( Parent(z,x) & Parent(z,y) )  ) )"));
        //axioms.add(Statement.getStatementFromString("_A[x,y]( Egal(x,y) > !Fraterie(x,y) )"));
        axioms.add(Statement.getStatementFromString("_A[x,y]( Fraterie(x,y) <> ( !Egal(x,y) & (_E[z]( Parent(z,x) & Parent(z,y) ) ) ) )"));

        //Une personne est frere d'une autre si il est de la meme fraterie et de sexe masculin
        axioms.add(Statement.getStatementFromString("_A[p,q]( Frere(x,y) <> ( Fraterie(x,y) & Masculin(x) ) )"));
        //Une personne est soeur d'une autre si il est de la meme fraterie et de sexe feminin
        axioms.add(Statement.getStatementFromString("_A[p,q]( Soeur(x,y) <> ( Fraterie(x,y) & Feminin(x) ) )"));
        */
        //m est mere de c si m est une femme et m parent de c
        axioms.add(Statement.getStatementFromString("_A[x,y](Mere(x,y)<>(Feminin(x)&Parent(x,y))))"));
        //p est pere de c si m est un homme et p parent de c
        axioms.add(Statement.getStatementFromString("_A[x,y](Pere(x,y)<>(Masculin(x)&Parent(x,y))))"));
/*
        //statementList.add(Statement.getStatementFromString("_A[m,c]( Egal(Mere(c),m) <>(Feminin(m)&Parent(m,c))))"));
        //statementList.add(Statement.getStatementFromString("_A[p,c]( Egal(Pere(c),p) <>(Masculin(p)&Parent(p,c))))"));

        //x et mari de y si x et y sont conjoints et x est de sex masculin
        axioms.add(Statement.getStatementFromString("_A[x,y]( Mari(x,y) <> ( Conjoint(x,y) & Masculin(x) ) )"));
        //x et epouse de y si x et y sont conjoints et x est de sex feminin
        axioms.add(Statement.getStatementFromString("_A[x,y]( Epouse(x,y) <> ( Conjoint(x,y) & Feminin(x) ) )"));
        //u est oncle de p si et seulement si il est le frere d'un des parent de p
        axioms.add(Statement.getStatementFromString("_A[x,y]( Oncle(x,y) <> ( _E[z]( Parent(z,y) & ( Frere(x,z) | BeauFrere(x,z) ) ) ) )"));
        //t est tante de p si et seulement si elle est la soeur d'un des parent de p
        axioms.add(Statement.getStatementFromString("_A[x,y]( Tante(x,y) <> ( _E[z]( Parent(z,y) & ( Soeur(x,z) | BelleSoeur(x,z) ) ) ) )"));

        //g est grand parent de c si est seulement si g est parent d'un des parents de c
        axioms.add(Statement.getStatementFromString("_A[x,y]( GrandParent(x,y) <> ( _E[z]( Parent(z,y) & Parent(x,z) ) )  )"));

        //g est grand pere de c si est seulement si g grand parent de c est de sexe masculin
        // statementList.add(Statement.getStatementFromString("_A[g,c]( GrandPere(g,c) <> ( Masculin(g) & ( _E[p]( Parent(p,c) & Parent(g,p) ) ) )  )"));
        axioms.add(Statement.getStatementFromString("_A[x,y]( GrandPere(x,y) <> ( GrandParent(x,y) & Masculin(x) ) )"));
        //g est grand mere de c si est seulement si g grand parent de c est de sexe feminin
        // statementList.add(Statement.getStatementFromString("_A[g,c]( GrandMere(g,c) <> ( Feminin(g) & ( _E[p]( Parent(p,c) & Parent(g,p) ) ) )  )"));
        axioms.add(Statement.getStatementFromString("_A[x,y]( GrandMere(x,y) <> ( GrandParent(x,y) & Feminin(x) ) )"));

        //x et l'ancetre de y si il existe une personne z qui est le parent de y et dont y est l'ancetre
        axioms.add(Statement.getStatementFromString("_A[x,y]( Ancetre(x,y) <> (Parent(x,y) | ( _E[z] ( Parent(z,y) & Ancetre(x,z) ) ) ) )"));


        //statementList.add(Statement.getStatementFromString("_A[x,y]( (( Parent(x,y) | GrandParent(x,y) ) | ArriereGrandParent(x,y) ) <> Ancetre(x,y) )"));
        //un descendant est l' inverse d'un ancetre
        axioms.add(Statement.getStatementFromString("_A[x,y]( Descendant(x,y) <> Ancetre(y,x) )"));
        //g est grand parent de c si est seulement si g est parent d'un des parents de c
        axioms.add(Statement.getStatementFromString("_A[x,y]( ArriereGrandParent(x,y) <> ( _E[z]( Parent(z,y) & GrandParent(x,z) ) )  )"));
        //c est fils de p
        // axioms.add(Statement.getStatementFromString("_A[c,p] ( Fils(x,y) <> ( Parent(y,x) & Masculin(x) ) )"));
        //c est fille de p
        //  axioms.add(Statement.getStatementFromString("_A[c,p] ( Fille(x,y) <> ( Parent(y,x) & Feminin(x) ) )"));
        //c est petit enfant de g si g est grand parent de c
        //  axioms.add(Statement.getStatementFromString("_A[c,g] ( PetitEnfant(x,y) <> GrandParent(y,x) )"));
        */
        //x et beau parent de y si x est conjoint à un parent de y mais n'est pas parent de y
        //axioms.add(Statement.getStatementFromString("_A[x,y]( BeauParent(x,y) <> ( (!Mere(x,y) | !Pere(x,y)) & (_E[z]( (Mere(z,y) | Pere(z,y)) & Conjoint(z,x) ) ) ) )"));
        //axioms.add(Statement.getStatementFromString("_A[x,y]( BeauParent(x,y) <> ( !Parent(x,y) & (_E[z]( Parent(z,y) & Conjoint(z,x) ) ) ) )"));
        axioms.add(Statement.getStatementFromString("_A[x,y]( BeauParent(x,y) <>  (_E[z]( Parent(z,y) & Conjoint(z,x) ) ) )"));

/*

        //beau pere = beau parent masculin
        axioms.add(Statement.getStatementFromString("_A[x,y]( BeauPere(x,y) <> ( BeauParent(x,y) & Masculin(x) ) )"));
        //belle mere = beau parent feminin
        axioms.add(Statement.getStatementFromString("_A[x,y]( BelleMere(x,y) <> ( BeauParent(x,y) & Feminin(x) ) )"));
        //x fait parti de la belle fraterie de y si il ont un beau parent et que ses beau parent son conjoint
        axioms.add(Statement.getStatementFromString("_A[x,y]( DemiFraterie(x,y) <> ( _E[a,b]( Conjoint(a,b) & ( BeauParent(a,x) & BeauParent(b,y) ) ) ) )"));
        axioms.add(Statement.getStatementFromString("_A[x,y]( DemiFrere(x,y) <> ( DemiFraterie(x,y) & Masculin(x) ) )"));
        axioms.add(Statement.getStatementFromString("_A[x,y]( DemiSoeur(x,y) <> ( DemiFraterie(x,y) & Feminin(x) ) )"));

*/

        //axioms.add(Statement.getStatementFromString("_A[x,y](!Egal(x,y) > !Egal(y,x))"));
      // axioms.add(Statement.getStatementFromString("_A[a,b,c]( (Conjoint(a,c) & Conjoint(b,c) ) > Egal(a,b))"));

        axioms.add(Statement.getStatementFromString("_A[a,b,c]( (Mere(a,c) & Mere(b,c) ) > Egal(a,b))"));
        axioms.add(Statement.getStatementFromString("_A[a,b,c]( (Pere(a,c) & Pere(b,c) ) > Egal(a,b))"));

         //axioms.add(Statement.getStatementFromString("_A[a,b]( Conjoint(a,b) <> Conjoint(b,a)  )"));
/*
       axioms.add(Statement.getStatementFromString("_A[x](Egal(x,x))"));

       axioms.add(Statement.getStatementFromString("_A[x,y](Egal(x,y) > Egal(y,x))"));

       axioms.add(Statement.getStatementFromString("_A[x,y,z]( ( Egal(x,y) & Egal(y,z) ) > Egal(x,z))"));

       axioms.add(Statement.getStatementFromString("_A[x](Egal(x,x) > Egal(x,x))"));

       //axioms.add(Statement.getStatementFromString("_A[x,y,z]( ( Egal(x,z) & Egal(y,z) ) <> Egal(x,y))"));

       // axioms.add(Statement.getStatementFromString("_A[x,y]( Egal(x,y) > ( Masculin(x) <> Masculin(y) ) )"));
       // axioms.add(Statement.getStatementFromString("_A[x,y]( Egal(x,y) > ( Feminin(x) <> Feminin(y) ) )"));

        //Belle fraterie : p1 est beau frere de p2 si :
        // p2 est une femme et ( p2 et marié au frere de p1 ou p1 est marié à la soeur de p2 )
        // p2 est une homme et ( p2 et marié à la soeur de p1 ou p1 est marié à la soeur de p2 )
/*
        axioms.add(Statement.getStatementFromString("_A[x,y](  BelleFraterie(x,y) <> ( _E[z]( Conjoint(x,z) & Fraterie(z,y) ) ) )"));
        axioms.add(Statement.getStatementFromString("_A[x,y](  BeauFrere(x,y) <> (  BelleFraterie(x,y) & Masculin(x) ) )"));
        axioms.add(Statement.getStatementFromString("_A[x,y](  BelleSoeur(x,y) <> (  BelleFraterie(x,y) & Feminin(x) ) )"));
*/
        //c est cousin germain de p si il l'enfant d'une personne de la meme fraterie d'un le parent
        //statementList.add(Statement.getStatementFromString("_A[x,y]( CousinGermain(x,y) <> ( _E[a,b]( ( Fraterie(a,b) & Parent(a,y) ) & Parent(b,x) ) ) )"));
        //statementList.add(Statement.getStatementFromString("_A[x,y]( CousinGermain(x,y) <> CousinGermain(y,x) )"));
//        statementList.add(Statement.getStatementFromString("_A[x,y](  () <> () )"));


        if(show) {
            System.out.println("\n-------------AXIOMES (" + axioms.size() + ")------------\n");

            for (Statement statement : axioms) {
                System.out.println(statement.toString().replaceAll(" ", ""));
            }
        }

        return axioms;

    }

    private static  Set<Statement> usefullFacts(Statement statement, List<Statement> statements){
        Set<Statement> usefullFacts = new LinkedHashSet<>();
        usefullFacts(statement.getConsts(), statements, usefullFacts, 0);
        return usefullFacts;
    }

    /*
    * Pour etre plus selectif on peut classer les fait par relations et par types
    *
    * bidirectionelles comme CONJOINT
    * directionelle comme PARENT
    *   sous categorifier les relations directionelles en relation ascendantes et descendantes
    *   avec une source et une destination
    *
    *
    *
    *   ensuite pour prouver que GrandParent(George,Harry) ou George est le grand pere de Harry
    *
    *  1 - le sujet : George
    *  2 - le verbe et le complement d'objet direct GrandParent
    *  3 - le complement d'objet indirect Harry
    *
    *   il faut ajouter les fait directionnel ou George est le sujet et ceux ou Harry et le complement d'objet indirect
    *   de maniere recursif
    *
    * */

    private static void usefullFacts(Set<FonctionalSymbol> fonctionalSymbols, List<Statement> statements,
                                               Set<Statement> usefullFacts, int d){
       //String ident = Util.getIdent(d);

        //pour chaque fait
        for( Statement fact : statements){

            //System.out.println(ident+"FAIT : "+fact);

            if(usefullFacts.contains(fact)){
                //System.out.println(ident+"CONNU ");

                continue;
            }

            //pour chaque termes recherchés
            for( FonctionalSymbol fonctionalSymbol : fonctionalSymbols){
                //si le fait contient le terme
                if(fact.getConsts().contains(fonctionalSymbol)){
                    //System.out.println(ident+" TERME "+fonctionalSymbol);

                    //ajout du fait à la liste des faits utiles
                    usefullFacts.add(fact);
                    //rapelle de la fonction pour ajouter d'autres faits liés au fait courant
                    usefullFacts(fact.getConsts(),statements,usefullFacts,d+1);
                }

            }

        }


    }


    private static  Set<Statement> usefullAxioms(Statement statement, List<Statement> statements){
        Set<Statement> usefullAxioms = new LinkedHashSet<>();
        usefullAxioms(statement, statements, usefullAxioms, 0);
        return usefullAxioms;
    }


    private static void usefullAxioms(Statement statement, List<Statement> statements, Set<Statement> usefullStatements, int d){


        //String ident = Util.getIdent(d);

        //System.out.println(ident+"----------------- RECHERCHE : "+statement);
        //System.out.println();

        for(Statement statement1 : statements){

            //System.out.println(ident+">>>>> ENONCE  : "+statement1);

            if(usefullStatements.contains(statement1))
                continue;

            if(statement1 instanceof Universal){

                //System.out.println(ident+"UNIVERSEL");


                //regle universel on recupere son enoncé unique
                Statement st1 = ((Universal)statement1).getSt1();

                //si il s'agit d'une implication ou d'une double implication
                if( /*st1 instanceof Imply ||*/ st1 instanceof DoubleImply ){
                    //on recupere la partie gauche contenant le predicat defini à partir d'autre plus primaire
                    BinaryStatement tws = (BinaryStatement) st1;
                    //pour chaque symbole contenu dans l'enonce que l'on cherche à prouver
                    for( Symbol symbol : statement.getSymbols()) {
                        //pour chaque symbole dans la premise
                        for( Symbol symbol2 : tws.getSt1().getSymbols()) {
                            //si un des symboles correspond
                            //on considere la clause comme utile
                            if(symbol.getLabel().equals(symbol2.getLabel())){
                                //System.out.println(ident+"UTILE");
                                usefullStatements.add(statement1);

                                //on recommence le processus avec les symboles des predicats de base
                                //situé à droite de la double implication
                                //idée générale
                                // double implication : nouveau predicat <> predicat de bases
                                //implication : predicat de bases > nouveau predicat
                                for(Symbol symbol3 : tws.getSt2().getSymbols()){
                                    //System.out.println(ident+"NOUVEAU SYMBOLE A TESTER "+symbol3);
                                    usefullAxioms(symbol3,statements,usefullStatements,d+1);
                                }

                                break;
                            }
                        }
                    }

                }else {
                    usefullStatements.add(statement1);
                }

            }

        }


    }


}
