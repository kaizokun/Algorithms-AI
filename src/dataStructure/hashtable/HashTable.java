package dataStructure.hashtable;

import dataStructure.tas.DataTest;

/**
 * Created by monsio on 09.05.16.
 */
public class HashTable {

    private Object[] T;
    private int m;

    public HashTable(int m) {
        T = new Object[m];
        this.m = m;
    }

    public int insert( Object o )throws Exception{

        System.out.print("\nInsert " + o + " : ");

        for( int i = 0 ; i < m ; ){

            int j = h(o.hashCode(),i);
            System.out.print("[" + j + "] ");
            if( T[j] == null ){
                T[j] = o;
                return j;
            }else{
                i++;
            }

        }
        throw new Exception("Hashtable overflow !");
    }

    public int search(Object o){

        System.out.print("\nStandardSearch " + o + " : ");

        int i = 0, j;

        do{
            j = h(o.hashCode(),i);
            System.out.print("[" + j + "] ");
            if( T[j].equals(o) ){
                return j;
            }
            i++;
        }while( T[j] != null && i < m );

        return -1;
    }

    private int h( int k, int i  ){
        return ( h1(k) + ( i * h2(k) ) ) % this.m ;
    }

    private int h1(int k){
        return k % this.m;
    }

    private int h2(int k){
        return 1 + ( k % ( this.m - 1 ) );
    }

    public static class DataTest{
        public int data;

        public DataTest(int data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataTest)) return false;

            DataTest dataTest = (DataTest) o;

            return data == dataTest.data;

        }

        @Override
        public int hashCode() {
            return data;
        }

        @Override
        public String toString() {
            return ""+this.data;
        }
    }


    public static void main(String[] args) {

        HashTable hashTable = new HashTable(10);

        try {

            hashTable.insert(new DataTest(10));
            hashTable.insert(new DataTest(22));
            hashTable.insert(new DataTest(31));
            hashTable.insert(new DataTest(4));
            hashTable.insert(new DataTest(15));
            hashTable.insert(new DataTest(28));
            hashTable.insert(new DataTest(17));
            hashTable.insert(new DataTest(88));
            hashTable.insert(new DataTest(59));
            hashTable.insert(new DataTest(98));

            System.out.println();
            System.out.println("recherche");

            hashTable.search(new DataTest(15));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
