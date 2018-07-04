package ai.agent.logic.test;

import ai.agent.logic.CNF_convert;
import ai.agent.logic.DPLL_CLEAN;
import ai.agent.logic.KB_Agent;
import ai.agent.logic.KnowledgeBase;
import ai.agent.logic.expressions.*;
import util.Util;


import java.awt.*;
import java.util.*;
import java.util.List;

import static ai.agent.logic.test.WumpusKBagent.SYMBOL.*;

public class WumpusKBagent extends KB_Agent {

    private int worldWidth, wordHeight;

    private WumpusWorld wumpusWorld;

    private Hashtable<String,Symbol> symbolsInstances = new Hashtable<>();

    public void setWumpusWorld(WumpusWorld wumpusWorld) {
        this.wumpusWorld = wumpusWorld;
    }

    public WumpusKBagent(int wordLenght, int worldWidth, WumpusWorld wumpusWorld) {

        this.wordHeight = wordLenght;
        this.worldWidth = worldWidth;
        this.wumpusWorld = wumpusWorld;

        // chargement de la kb pour les regles de bases et initiales
        LinkedList<Statement> kbStatements = new LinkedList<>();

        this.loadInitialRules(kbStatements);
        this.loadBasicsRules(kbStatements);
        this.loadPerceptsInterpretation(kbStatements);
        this.loadNextStateAxioms(kbStatements);

        List<Statement> clauses = CNF_convert.convert(kbStatements);

        this.KB = new KnowledgeBase(clauses);

    }

    private Symbol getSymBol(Symbol symbol){

        if( this.symbolsInstances.containsKey(symbol.toShortString())){
            return this.symbolsInstances.get(symbol.toShortString());
        }else{
            this.symbolsInstances.put(symbol.toShortString(),symbol);
            return symbol;
        }
    }

    private void loadInitialRules(List<Statement> kbStatements) {


        kbStatements.add(getSymBol(new Symbol(WUMPUS.getCode()+time)));
        kbStatements.add(getSymBol(new Symbol(GOLD.getCode()+time)));
        kbStatements.add(getSymBol(new Symbol(SYMBOL.getNSEWsymbol(wumpusWorld.agentOrientation).getCode()+time)));
        kbStatements.add(getSymBol(new Symbol(ARROW.getCode()+time)));
        kbStatements.add(getSymBol(new Symbol(INSIDE.getCode()+time)));
        kbStatements.add(getSymBol(new Symbol(AGENT.getCode()+time+pos(wumpusWorld.agentPosition.x,wumpusWorld.agentPosition.y))));

        kbStatements.add(new Not(getSymBol(new Symbol(WEL.getCode()+pos(wumpusWorld.agentPosition.x,wumpusWorld.agentPosition.y)))));


        //CASES OK
        for( int x = 1 ; x <= this.worldWidth ; x++ ) {

            for (int y = 1; y <= this.wordHeight; y++) {

                Statement ok =  new DoubleImply(
                        getSymBol(new Symbol(OK.getCode() + time + pos(x, y))),
                        new And(
                                new Not(getSymBol(new Symbol(WEL.getCode()+pos(x,y)))),
                                new Not(
                                        new And(
                                                getSymBol(new Symbol(WUMPUS.getCode()+pos(x,y))),
                                                getSymBol(new Symbol(WUMPUS.getCode()+time))
                                        )
                                )
                        )
                );

                kbStatements.add(ok);

            }
        }

    }

    private void loadNextStateAxioms(List<Statement> kbStatements) {

        //FLUENTS :
        // WUMPUS(VIVANT OU PAS)
        // GOLD(ATTRAPE OU PAS)
        // AGENT(POSITION CHANGE OU PAS)
        // NORTH SOUTH EAST WEST ( ORIENTATION DE L AGENT CHANGE OU PAS )
        // ARROW(DISPONIBLE OU PAS)
        // INSIDE (DANS LA GROTTE OU PAS)

        //STRAIGHT, TURN_RIGHT, TURN_LEFT, GRAB, SHOOT, CLIMB

        //STRAIGHT un etat sucesseur pour s'être depacé tout droit est de se retrouver dans une autre case
        //en fonction de l'orientation de l'agent, percevoir un choc implique que l'action a échoué

        //  LE FLUENT POSITION A EST INFLUENCE PAR LES ACTIONS ALLER TOUT DROIT STRAIGHT

        SYMBOL symbolMove[] = new SYMBOL[]{NORTH, SOUTH, EAST, WEST};

        for( int x = 1 ; x <= this.worldWidth ; x++ ) {

            for (int y = 1; y <= this.wordHeight; y++) {

                //AGENT-t+1(x,y)
                Statement implyLeft = getSymBol(new Symbol(AGENT.getCode()+nextTime+pos(x,y)));

                LinkedList<Statement> statements = new LinkedList<>();

                //AGENT-t(x,y) AND ( NOT STRAIGHT-t OR IMPACT-t+1)
                Statement onTheSpot = new And(
                        getSymBol(new Symbol(AGENT.getCode()+time+pos(x,y))),
                        new Or(
                                new Not(getSymBol(new Symbol(STRAIGHT.getCode()+time)))
                                ,getSymBol(new Symbol(IMPACT.getCode()+nextTime))
                        )
                );

                statements.add(onTheSpot);

                for(NSEW_MOVE move : NSEW_MOVE.values()){

                    int xPrev = x - NSEW_MOVE.getXdel(move), yPrev = y - NSEW_MOVE.getYdel(move);

                    //AGENT-t(x,y) AND ( NSEW_MOVE-t AND STRAIGHT-t)
                    if( xPrev > 0 && xPrev <= this.worldWidth && yPrev > 0 && yPrev <= this.wordHeight){

                        Statement previousMove = new And(

                                getSymBol(new Symbol(AGENT.getCode()+time+pos(xPrev,yPrev))),
                                new And(
                                        getSymBol(new Symbol(symbolMove[move.ordinal()].getCode()+time)),
                                        getSymBol(new Symbol(STRAIGHT.getCode()+time))
                                )
                        );

                        statements.add(previousMove);
                    }

                }

                Statement implyRight = new Or().getStatement(statements);

                kbStatements.add(new DoubleImply(implyLeft,implyRight));

            }
        }

        //envoyer une action au la base de conaissance se fait comme pour les world
        // avec toutes els actions au moment t indiqiant si elles ont eu lieu ou pa

        //ainsi pour l'action tourner à gauche ou tourner à droite par exemple
        //il n'est pas necessaire d'indiquer que la consequence est le fait d'être toujours à la meme position
        //à l'étape suivante mais uniquement la nouvelle orientation.
        //le fait de faire du sur place sera connu dans la KBpercepts par le fait que l'aciton STRAIGHT sera négative NOT STRAIGHT-t
        //dans la liste


        //LES FLUENTS ORIENTATION (NORTH,SOUTH,EAST,WEST) EST INFLUENCE PAR L ACTION TOURNER A GAUCHE OU TOURNER A   DROITE

        //NORTH(t+) <=> ( WEST(t) AND TURN_RIGHT(t) ) OR ( EAST(t) AND TURN_LEFT(t) ) OR ( NORTH(t) AND ( NOT TURN_LEFT(t) OR NOT TURN RIGHT(t) ) )


        SYMBOL previousDirRightLeft [][] = new SYMBOL[][]{{WEST,EAST},{EAST,WEST},{NORTH,SOUTH},{SOUTH,NORTH}};
        int LEFT = 0;
        int RIGHT = 1;
        for( int d = 0 ; d < symbolMove.length ; d ++ ) {

            DoubleImply dirt2 = new DoubleImply(
                    getSymBol(new Symbol(symbolMove[d].getCode() + nextTime)),
                    new Or(
                            new Or(
                                    new And(
                                            getSymBol(new Symbol(previousDirRightLeft[d][LEFT].getCode() + time)),
                                            getSymBol(new Symbol(TURN_RIGHT.getCode() + time))
                                    ),
                                    new And(
                                            getSymBol(new Symbol(previousDirRightLeft[d][RIGHT].getCode() + time)),
                                            getSymBol(new Symbol(TURN_LEFT.getCode() + time))
                                    )
                            ),
                            new And(
                                    getSymBol(new Symbol(symbolMove[d].getCode() + time)),
                                    new Or(
                                            new Not( getSymBol(new Symbol(TURN_RIGHT.getCode() + time))),
                                            new Not( getSymBol(new Symbol(TURN_LEFT.getCode() + time)))
                                    )
                            )

                    )
            );

            kbStatements.add(dirt2);

        }

        //ACTION GRAB POUR ATTRAPER L OR INFLUE SUR LE FAIT QUE L' OR A ETE OBTENU PAR L AGENT
        //GOLD(t+) <=> GOLD(t) AND NOT GRAB(t)

        SYMBOL fluents[] = new SYMBOL[]{GOLD,ARROW,WUMPUS,INSIDE};
        SYMBOL actions[] = new SYMBOL[]{GRAB,SHOOT,SHOOT,CLIMB};

        for(int f = 0 ; f < fluents.length ; f ++){

            DoubleImply fluent = new DoubleImply(
                    getSymBol(new Symbol(fluents[f].getCode()+nextTime)),
                    new And(
                            getSymBol(new Symbol(fluents[f].getCode()+time)),
                            new Not(getSymBol(new Symbol(actions[f].getCode()+time)))
                    )
            );

            kbStatements.add(fluent);

        }


    }

    private void loadPerceptsInterpretation(List<Statement> kbStatements) {

        String [] percepts = new String []{BREEZE.getCode(), SMELL.getCode(), LIGHT.getCode()};

        for(String p : percepts)
            for( int x = 1 ; x <= this.worldWidth ; x++ ) {

                for (int y = 1; y <= this.wordHeight; y++) {

                    Statement statement = new Imply(
                            getSymBol(new Symbol(AGENT.getCode()+time+pos(x,y))),
                            new DoubleImply(
                                    getSymBol(new Symbol(p+time)),
                                    getSymBol(new Symbol(p+pos(x,y)))
                            )
                    );

                    kbStatements.add(statement);

                }

            }

    }

    private void loadBasicsRules(List<Statement> kbStatements) {


        //chargement des regles world et leur implications

        String [] percepts = new String[]{BREEZE.getCode(), SMELL.getCode()};
        String [] perceptsImply = new String[]{WEL.getCode(), WUMPUS.getCode()};

        for( int c = 0 ; c < percepts.length ; c ++ ){

            for( int x = 1 ; x <= this.worldWidth ; x ++ ){

                for(int y = 1; y <= this.wordHeight; y ++ ){

                    String symbolPercept = percepts[c];
                    String symbolPerceptImply = perceptsImply[c];

                    String labelPercept = symbolPercept+pos(x,y);

                    Symbol doubleImplyLeft = getSymBol(new Symbol(labelPercept));

                    LinkedList<Statement> symbolList = new LinkedList<>();

                    for(NSEW_MOVE move : NSEW_MOVE.values()){

                        int x2 = x + NSEW_MOVE.getXdel(move);
                        int y2 = y + NSEW_MOVE.getYdel(move);

                        if( x2 > 0 && x2 <= this.worldWidth && y2 > 0 && y2 <= this.wordHeight){
                            String labelPerceptImply = symbolPerceptImply+"("+x2+","+y2+")";
                            symbolList.add( getSymBol(new Symbol(labelPerceptImply) ));
                        }

                    }

                    Statement doubleImplyRight = new Or().getStatement(symbolList);

                    DoubleImply doubleImply = new DoubleImply(doubleImplyLeft,doubleImplyRight);

                    kbStatements.add(doubleImply);
                }

            }

        }

        //AU MOINS UN
        String [] onlyOne = new String[]{WUMPUS.getCode(), GOLD.getCode(), AGENT.getCode()+time};

        for( int c = 0 ; c < onlyOne.length ; c ++ ) {

            LinkedList<Statement> statements = new LinkedList<>();

            for (int x = 1; x <= this.worldWidth; x++) {
                for (int y = 1; y <= this.wordHeight; y++) {
                    String sym = onlyOne[c];
                    statements.add(getSymBol(new Symbol(sym+pos(x,y))));
                }
            }

            Statement everywhereStatement = new Or().getStatement(statements);
            kbStatements.add(everywhereStatement);
        }

        //AU PLUS UN

        List<String> positions = new LinkedList<>();
        for (int x = 1; x <= this.worldWidth; x++) {
            for (int y = 1; y <= this.wordHeight; y++){
                positions.add(pos(x,y));
            }
        }

        for( int c = 0 ; c < onlyOne.length ; c ++ ) {
            for (int i = 0; i < positions.size(); i++) {
                for (int j = i + 1; j < positions.size(); j++) {
                    String sym = onlyOne[c];
                    kbStatements.add(
                            new Or(
                                    new Not(
                                            getSymBol(new Symbol(sym+positions.get(i)))
                                    ),
                                    new Not(
                                            getSymBol(new Symbol(sym+positions.get(j)))
                                    )
                            )
                    );
                }
            }
        }

    }

    private String pos(int x , int y){
        return "("+x+","+y+")";
    }

    @Override
    protected List<Statement> createStatementAction(Statement action) {
        return null;
    }

    @Override
    protected Statement ask(Statement request) {

        if( DPLL_CLEAN.explore(this.KB, request))
            return request;
        else
            return new Not(request);
    }

    private List<Statement> getAgentPosition(){

        List<Statement> rs = new LinkedList<>();

        for (int x = 1; x <= this.worldWidth; x++){
            for (int y = 1; y <= this.wordHeight; y++){

                Statement positionPosible = getSymBol(new Symbol(AGENT.getCode()+time+pos(x,y)));

                rs.add(ask(positionPosible));

            }
        }

        return rs;
    }

    private List<Statement> getNextCaseOk(){

        List<Statement> rs = new LinkedList<>();

        Point position = wumpusWorld.agentPosition;
        //NSEW_MOVE orientation = wumpusWorld.agentOrientation;

        for(NSEW_MOVE dir : NSEW_MOVE.values()) {

            int x = position.x + NSEW_MOVE.getXdel(dir),
                    y = position.y + NSEW_MOVE.getYdel(dir);

            if(x > 0 && x <= wumpusWorld.getWidth() && y > 0 && y <= wumpusWorld.getHeight() ) {
                Statement ok = getSymBol(new Symbol(SYMBOL.OK.getCode() + time + pos(x, y)));
                rs.add(this.ask(ok) );
            }

        }

        return rs;

    }

    @Override
    protected Statement createRequestAction() {

        return null;
    }

    @Override
    protected List<Statement> createStatement(List<Statement> purePercepts) {

        List<Statement> timePercepts = new LinkedList<>();

        for(Statement percept : purePercepts){

            String label = percept.getSymbol().getLabel()+time;
            Symbol symbol = getSymBol(new Symbol(label));

                if(percept instanceof Not) {
                    timePercepts.add(new Not(symbol));
                }else{
                    timePercepts.add(symbol);
                }
        }

        return timePercepts;

    }

    public static void main(String[] args) {

        int worldWidth = 4 , wordHeight = 4;


        WumpusWorld wumpusWorld = new WumpusWorld(worldWidth, wordHeight, new Point(1,4), NSEW_MOVE.EAST);


        wumpusWorld.addPercets(1,1, new SYMBOL[]{SMELL});
        wumpusWorld.addPercets(3,1, new SYMBOL[]{BREEZE});
        wumpusWorld.addPercets(2,2, new SYMBOL[]{BREEZE,SMELL,LIGHT});
        wumpusWorld.addPercets(4,2, new SYMBOL[]{BREEZE});
        wumpusWorld.addPercets(1,3, new SYMBOL[]{SMELL});
        wumpusWorld.addPercets(3,3, new SYMBOL[]{BREEZE});
        wumpusWorld.addPercets(2,4, new SYMBOL[]{BREEZE});
        wumpusWorld.addPercets(4,4, new SYMBOL[]{BREEZE});

        WumpusKBagent wumpusKBagent = new WumpusKBagent(worldWidth, wordHeight, wumpusWorld);

        List<Statement> purePercepts =  wumpusWorld.getPercept();

        List<Statement> percepts = wumpusKBagent.createStatement(purePercepts);

        wumpusKBagent.tell(percepts);

        Util.initTime();
        List<Statement> positions = wumpusKBagent.getAgentPosition();
        Util.printTimeDelta();

        for(Statement position : positions){
            System.out.println(position);
        }

        Util.initTime();
        List<Statement> casesOk = wumpusKBagent.getNextCaseOk();
        Util.printTimeDelta();

        for(Statement caseOk : casesOk){
            System.out.println(caseOk);
        }

    }



    enum NSEW_MOVE {

        NORTH,SOUTH, EAST,WEST;

        public static final int [][] deltaMov = new int[][]{{0,-1},{0,1},{1,0},{-1,0}};

        public static int getXdel(NSEW_MOVE mv){
            return deltaMov[mv.ordinal()][0];
        }

        public static int getYdel(NSEW_MOVE mv){
            return deltaMov[mv.ordinal()][1];
        }
    }

    public enum SYMBOL{

        //FLUENTS :
        // WUMPUS(VIVANT OU PAS)
        // GOLD(ATTRAPE OU PAS)
        // AGENT(POSITION CHANGE OU PAS)
        // NORTH SOUTH EAST WEST ( ORIENTATION DE L AGENT CHANGE OU PAS )
        // ARROW(DISPONIBLE OU PAS)
        // INSIDE (DANS LA GROTTE OU PAS)

        //PERCEPTS
        BREEZE, SMELL, LIGHT, SCREAM, IMPACT,
        //STATES
        WEL, WUMPUS, GOLD, AGENT, OK,
        //AGENT STATE
        ARROW, NORTH, SOUTH, EAST, WEST, INSIDE,
        //ACTIONS
        STRAIGHT, TURN_RIGHT, TURN_LEFT, GRAB, SHOOT, CLIMB;

        public String getCode(){
            return this.ordinal()+"-";
        }

        public static String getReadableSymbol(String str){

            //System.out.println(str);
            //par convention un symbole complet possede le code en premier suivit d'un tiret
            String strCode = str.substring(0, str.indexOf("-"));

            String postCode = str.substring(str.indexOf("-"));

            String readableCode = values()[Integer.parseInt(strCode)].toString();

            return readableCode+postCode;
        }

        public static SYMBOL getNSEWsymbol(NSEW_MOVE nsew_move){
            switch (nsew_move){
                case EAST:
                    return EAST;
                case WEST:
                    return WEST;
                case NORTH:
                    return NORTH;
                case SOUTH:
                    return SOUTH;
                default:
                    return null;
            }
        }

    }


    static class WumpusWorld {

        private int width, height;
        private HashSet<SYMBOL> [][] world;
        private SYMBOL[] percepts = new SYMBOL[]{BREEZE, SMELL, LIGHT, SCREAM, IMPACT};
        private Point agentPosition;
        private NSEW_MOVE agentOrientation;

        public WumpusWorld(int width, int height, Point agentPosition, NSEW_MOVE agentOrientation) {
            this.width = width;
            this.height = height;
            this.world = new LinkedHashSet[this.width][this.height];

            this.agentOrientation = agentOrientation;
            this.agentPosition = agentPosition;

            for( int x = 0 ; x < this.width ; x ++ ){
                for( int y = 0 ; y < this.width ; y ++ ) {
                    this.world[x][y] = new LinkedHashSet<>();
                }
            }
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void addPercets(int x, int y, SYMBOL[] percepts){
            for(SYMBOL percept : percepts) {
                this.world[x-1][y-1].add(percept);
            }
        }

        public List<Statement> getPercept(){

            LinkedList<Statement> perceptsStatement = new LinkedList<>();

            for(SYMBOL percept : percepts){

                if(this.world[ agentPosition.x - 1 ][ agentPosition.y - 1 ].contains(percept)){
                    perceptsStatement.add(new Symbol(percept.getCode()));
                }else{
                    perceptsStatement.add(new Not(new Symbol(percept.getCode())));
                }

            }

            return perceptsStatement;
        }

        public Point getAgentPosition() {
            return agentPosition;
        }

        public void setAgentPosition(Point agentPosition) {
            this.agentPosition = agentPosition;
        }

        public NSEW_MOVE getAgentOrientation() {
            return agentOrientation;
        }

        public void setAgentOrientation(NSEW_MOVE agentOrientation) {
            this.agentOrientation = agentOrientation;
        }
    }

}
