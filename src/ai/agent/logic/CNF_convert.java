package ai.agent.logic;

import ai.agent.logic.expressions.*;
import util.Util;

import java.util.LinkedList;
import java.util.List;

public class CNF_convert {

    public static int cptrSkolem = 0 ;

    public static List<Statement> convert(LinkedList<Statement> statements){
        Statement statement = new And().getStatement(statements);
        return convert(statement,false).getOrClauses();
    }

    public static List<Statement> convertAndClone(LinkedList<Statement> statements){
        Statement statement = new And().getStatement(statements);
        List<Statement> rs = convert(statement, true).getOrClauses();
        return rs;
    }

    public static List<Statement> convertAndClone(Statement statement){
        List<Statement> rs = convert(statement, true).getOrClauses();
        return rs;
    }

    public static Statement convert(Statement statement){
        return convert(statement,false);
    }

    public static Statement convert(Statement statement, boolean clone){

        List<FonctionalSymbol> vars;

        if(statement instanceof Universal){
            vars = ((Universal) statement).getVariables();
        }else{
            vars = new LinkedList<>();
        }

        //cptrSkolem = 0;
        return convert(statement,0, new LinkedList<FonctionalSymbol>(),clone);
    }

    private static Statement convert(Statement statement, int d, List<FonctionalSymbol> vars, boolean clone){

        boolean showlog = false;

        String ident = "";

        if(showlog) {
            ident = Util.getIdent(d);
            System.out.println(ident + " CONVERSION DE " + statement);
            System.out.println(ident + "VARIABLE "+vars);
        }

        Statement rs = statement;


            if (statement instanceof DoubleImply) {
                if(showlog)
                    System.out.println(ident + " DoubleImply ");
                rs = statement.getTransformationResult(TransformAction.TRANFORMATION.REMOVE_BICONDITIONAL,clone);
                if(showlog)
                System.out.println(ident + " REMOVE_BICONDITIONAL "+rs);

            } else if (statement instanceof Imply) {
                if(showlog)
                    System.out.println(ident + " Imply ");
                rs = statement.getTransformationResult(TransformAction.TRANFORMATION.REMOVE_IMPLICATION,clone);
                if(showlog)
                System.out.println(ident + " REMOVE_IMPLICATION "+rs);
            } else if (statement instanceof Not) {
                if(showlog)
                    System.out.println(ident + " Not ");
                //récupere l'expression encapsulé dans la négation
                Statement st = ((Not) statement).getSt1();

                //si cet expression est une autre négation
                if (st instanceof Not) {
                    //on clone l'expression encapsulée dans cette sous négation;
                    rs = ((Not) st).getSt1().clone(false);
                    if(showlog)
                    System.out.println(ident + " Not Not "+rs);
                    //Not suivit d'un AND applique la loie de morgan sur AND
                } else if (st instanceof And) {

                    rs = statement.getTransformationResult(TransformAction.TRANFORMATION.MORGAN_AND,clone);
                    if(showlog)
                    System.out.println(ident + " MORGAN_AND "+rs);
                    //Not suivit d'un OU applique la loi de morgan sur OU
                } else if (st instanceof Or) {
                    if(showlog)
                        System.out.println(ident + "Not Or ");

                    rs = statement.getTransformationResult(TransformAction.TRANFORMATION.MORGAN_OR,clone);
                    if(showlog)
                    System.out.println(ident + " MORGAN_OR "+rs);
                }else if(st instanceof Universal){
                    if(showlog)
                        System.out.println(ident + "Not Universal ");

                    rs = new Existential(new Not(((Universal) st).getSt1()),((Universal) st).getVariables()).clone(clone);
                    if(showlog)
                    System.out.println(ident + " Existential Not statement "+rs);

                }else if(st instanceof Existential){
                    if(showlog)
                        System.out.println(ident + "Not Existential ");

                    rs = new Universal(new Not(((Existential) st).getSt1()),((Existential) st).getVariables()).clone(clone);
                    if(showlog)
                    System.out.println(ident + " Universal Not statement "+rs);

                }

            }


        //System.out.println(ident+" RESULTAT : "+rs);
        //si l'enonce est composé de deux expressions
        //il faut retravailler les deux parties separement et les recombiner une fois le travail terminé
        if(/* !rs.equals(statement) && */ rs instanceof BinaryStatement){

            BinaryStatement tse = (BinaryStatement) rs;

           // tse.setSt1(convert(tse.getSt1()));
           // tse.setSt2(convert(tse.getSt2()));

            Statement st1 = tse.getSt1();
            Statement st2 = tse.getSt2();
            //convertion de chaque sous partie
            Statement rs1 = convert(st1, d+1,vars,clone);
            Statement rs2 = convert(st2, d+1,vars,clone);

            if(showlog) {
                System.out.println(ident+" CONVERT ST1 "+rs1);
                System.out.println(ident+" CONVERT ST2 "+rs2);
            }

            rs1 = removeQuantifier(rs1,showlog,ident);
            rs2 = removeQuantifier(rs2,showlog,ident);

            tse.setSt1(rs1);
            tse.setSt2(rs2);

            if(showlog) {
                System.out.println(ident+" RECOMBINAISON : "+rs);
            }


        }else if(rs instanceof UnaryStatement){

            List<FonctionalSymbol> vars2;

            if(rs instanceof Universal){
                vars2 = ((Universal) rs).getVariables();
            }else{
                vars2 = vars;
            }

            UnaryStatement ose = (UnaryStatement) rs;
            Statement st1 = ose.getSt1();
            String st1Str = st1.toString();
            Statement rs1 = convert(st1, d+1,vars2,clone);

            ose.setSt1(rs1);
            //l'enonce est une negation est il a été converti
            if(rs instanceof Not && (!st1Str.equals(rs1.toString()))){
               rs =  convert(rs,d+1,vars2,clone);
            }

            if(rs instanceof Existential){
                if(showlog)
                System.out.println(ident+"EXISTENCIEL "+cptrSkolem);
                ((Existential) rs).skolemisation(cptrSkolem,vars);
                if(showlog)
                System.out.println(ident+"SKOLEMISATION "+rs);
                cptrSkolem ++;
            }

            rs = removeQuantifier(rs,showlog,ident);

        }


        //application de la distributivite pour changer une expression OU en ET
        if( rs instanceof Or ){

           // System.out.println(ident+" DISTRIBUTION ");

            Or or = (Or) rs;

            boolean applyDistrib = false;

            if( or.getSt1() instanceof And ){

                applyDistrib = true;
                rs = rs.getTransformationResult(TransformAction.TRANFORMATION.COMMUTATIVITY,clone);
                rs = rs.getTransformationResult(TransformAction.TRANFORMATION.DISTRIBUTIVE_OR,clone);

            }else if( or.getSt2() instanceof And){

                applyDistrib = true;
                rs = rs.getTransformationResult(TransformAction.TRANFORMATION.DISTRIBUTIVE_OR,clone);
            }

            if(showlog)
            System.out.println(ident+" DISTRIBUTION RESULTAT "+applyDistrib+" "+rs);

            //si l'expression à été modifié il faut rappeller la fonction pour reappliquer la distributivité
            //si elle ne s'est pas appliqué suffisemment en profondeur
            if(applyDistrib){
                rs = convert(rs,d+1,vars,clone);
            }

        }

        return rs;

    }

    private static Statement removeQuantifier(Statement rs, boolean showlog, String ident){

        if(rs instanceof Universal){

            if(showlog)
                System.out.println(ident + "Universal ");

            rs = ((Universal) rs).getSt1();

            if(showlog)
                System.out.println(ident + "Retrait quantificateur Universal pour RS "+rs);

        }else if(rs instanceof  Existential){

            rs = ((Existential) rs).getSt1();

            if(showlog)
                System.out.println(ident + "Retrait quantificateur Existential pour RS "+rs);

        }

        return rs;
    }



}
