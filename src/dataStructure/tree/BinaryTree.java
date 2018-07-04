package dataStructure.tree;

import sun.reflect.generics.tree.Tree;

import javax.swing.tree.TreeNode;
import java.util.LinkedList;

/**
 * Created by monsio on 11/05/16.
 */
public class BinaryTree {


    private TreeNode root;


    public void insert( Comparable value ){

        TreeNode x = this.root,
                y = null,
                z = new TreeNode(value);

        while( x != null ){

            y = x;
            x = value.compareTo(x.getValue()) <= 0 ? x.getLeft() : x.getRight();

        }

        z.setParent(y);

        if( y == null )
            this.root = z;
        else{

            if( z.getValue().compareTo(y.getValue()) <= 0 )
                y.setLeft(z);
            else
                y.setRight(z);

        }

    }


    public TreeNode search( Comparable val ){

        TreeNode r = this.root;
        int cmp;
        while( r != null && (cmp = val.compareTo(r.getValue())) != 0 ){
            r =  cmp < 0 ? r.getLeft() : r.getRight();
        }

        return r;

    }



    public void parcourInfixeRec(){
        parcourInfixeRec(this.root);
    }

    private void parcourInfixeRec(TreeNode t){

        if( t != null){

            parcourInfixeRec(t.left);
            System.out.print(t.getValue()+" ");
            parcourInfixeRec(t.right);

        }
    }


    private void parcourInfixeIt(){


        TreeNode c = this.root;

        while( c != null ){

            if( c.getLeft() != null && !c.getLeft().isVu() ) {
                c = c.getLeft();
            }else{

                if( !c.isVu() ) {
                    System.out.print(c.getValue()+" ");
                    c.setVu(true);
                }

                if( c.getRight() != null && !c.getRight().isVu()){
                    c = c.getRight();
                }else{
                    c = c.getParent();
                }

            }

        }

    }


    public class TreeNode{

        private Comparable value;
        private TreeNode parent,left,right;
        private boolean vu = false;


        public TreeNode(Comparable value) {
            this.value = value;
        }

        public Comparable getValue() {
            return value;
        }

        public void setValue(Comparable value) {
            this.value = value;
        }

        public TreeNode getParent() {
            return parent;
        }

        public void setParent(TreeNode parent) {
            this.parent = parent;
        }

        public TreeNode getLeft() {
            return left;
        }

        public void setLeft(TreeNode left) {
            this.left = left;
        }

        public TreeNode getRight() {
            return right;
        }

        public void setRight(TreeNode right) {
            this.right = right;
        }

        public boolean isVu() {
            return vu;
        }

        public void setVu(boolean vu) {
            this.vu = vu;
        }
    }


    public static void main(String[] args) {

        BinaryTree b = new BinaryTree();

        int vals[] = new int[]{15,6,18,3,7,2,4,13,9,17,20,25,41,85,9,2,31,17};


        for(int v : vals)
            b.insert(v);


        b.parcourInfixeIt();

    }

}
