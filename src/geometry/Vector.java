package geometry;

public class Vector {

    protected Point p1, p2;

    public Vector(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(PolygonPoint p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(PolygonPoint p2) {
        this.p2 = p2;
    }

    /**
     *
     * Determine si le vecteur Xi succede à Xj dans le sens des aiguilles d'un montre
     *
     * transite les deux vecteurs et deplacant le point d'origine p1 en (0,0),
     * p2 = ( (p2.x - p1.x), (p2.y - p1.y))
     *
     * le produit en croix des deux vecteurs translaté
     * (x1y2 - x2y1) determine le sens si il est positif oui sinon le sens inverse.
     *
     * **/
    public static double clockwise(Vector v1, Vector v2){
        return (v1.p2.x - v1.p1.x) * (v2.p2.y - v2.p1.y) - (v2.p2.x - v2.p1.x) * (v1.p2.y - v1.p1.y);
    }

    public boolean clockwise(Vector v1){

       v1 = v1.translationOrigin();
       Vector v2 = this.translationOrigin();

        Point p1 = v1.p2,
             p2 = v2.p2;

        System.out.println("("+p1.x+" * "+p2.y+") - ("+p2.x+" * "+p1.y+")");

       return  ((p1.x * p2.y) - (p2.x * p1.y)) >= 0 ;

    }

    public Vector translationOrigin(){

        return new Vector(
                new PolygonPoint(0,0),
                new PolygonPoint(this.p2.x - this.p1.x, this.p2.y - this.p1.y)
        );
    }


    public static int ONSEGMENT = 0, CROSS = 1, NOCROSS = 2, SAMEDIRECTION = 3;
    public static int crossOrTouch(Vector v1, Vector v2){
        double d1 = clockwise(v2, new Vector(v2.p1.clone(), v1.p1.clone()));
        double d2 = clockwise(v2, new Vector(v2.p1.clone(), v1.p2.clone()));
        double d3 = clockwise(v1, new Vector(v1.p1.clone(), v2.p1.clone()));
        double d4 = clockwise(v1, new Vector(v1.p1.clone(), v2.p2.clone()));

        if(d1 == 0 && d2 == 0 && d3 == 0 && d4 == 0)
            return SAMEDIRECTION;

        if( ((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0 )) && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0)) )
            return CROSS;

        if( d1 == 0 && onSegment(v2.p1, v2.p2, v1.p1) )
            return ONSEGMENT;

        if( d2 == 0 && onSegment(v2.p1, v2.p2, v1.p2) )
            return ONSEGMENT;

        if( d3 == 0 && onSegment(v1.p1, v1.p2, v2.p1) )
            return ONSEGMENT;

        if( d4 == 0 && onSegment(v1.p1, v1.p2, v2.p2) )
            return ONSEGMENT;

        return NOCROSS;
    }

    public static boolean cross(Vector v1, Vector v2){

        double d1 = clockwise(v2, new Vector(v2.p1.clone(), v1.p1.clone()));
        double d2 = clockwise(v2, new Vector(v2.p1.clone(), v1.p2.clone()));
        double d3 = clockwise(v1, new Vector(v1.p1.clone(), v2.p1.clone()));
        double d4 = clockwise(v1, new Vector(v1.p1.clone(), v2.p2.clone()));
        System.out.println(v1+" "+v2+" "+d1+" "+d2+" "+d3+" "+d4);
        if( ((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0 )) && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0)) )
            return true;

        return false;
    }


    protected static boolean onSegment(Point pi, Point pj, Point pk){

        return ( Math.min(pi.x,pj.x) <= pk.x && pk.x <= Math.max(pi.x,pj.x) && Math.min(pi.y,pj.y) <= pk.y && pk.y <= Math.max(pi.y,pj.y) );
    }

    /**
     * Determine le sens de la bifurcation à la fin de Xi pour parcourir Xj
     * Determine d'abord le point d'intersection çàd le point commum entre les deux segment
     * sui est considéré comme le point de fin de Xi et le point de depart de Xj
     * */
    public static double turnRight(Vector v1, Vector v2) throws Exception{

        //Si le point d'intersection est le point de depart du vecteur 1 on l'inverse
        if( v1.p1.equals(v2.p1) || v1.p1.equals(v2.p2) ) {
            System.out.println("Inversion de V1");
            v1 = new Vector(new PolygonPoint(v1.p2.x, v1.p2.y), new PolygonPoint(v1.p1.x, v1.p1.y));
        }else if( ! (v1.p2.equals(v2.p1) || v1.p2.equals(v2.p2)))
            throw new Exception("Les segments n'ont aucun point commun");
        //determine le point de terminaison du segment Xj
        Point v2End = v1.p2.equals(v2.p1) ? v2.p2 : v2.p1;
        Vector v3 = new Vector( new PolygonPoint(v1.p1.x,v1.p1.y), new PolygonPoint(v2End.x,v2End.y));

        return clockwise(v3,v1);

    }


    public boolean sameVector(Vector v){
        return (v.p1.equals(this.p1 ) || v.p1.equals(this.p2)) && (v.p2.equals(this.p1 )|| v.p2.equals(this.p2));
    }


    @Override
    public String toString() {
        return p1+" -> "+p2;
    }

    public static void main(String[] args) {


        Vector v2 = new Vector(new PolygonPoint(2,2), new PolygonPoint(4,4));
        Vector v1 = new Vector(new PolygonPoint(2,4), new PolygonPoint(5,1));


        System.out.println(Vector.crossOrTouch(v1, v2));

    }



}
