package mylib.list;

import java.util.*;

/**
 * Created by monsio on 13/07/16.
 */
public class MyLinkedList<T> implements List<T>,Iterator<T> {


    private Node<T> nil, cursor;
    private int size = 0;

    public MyLinkedList() {
        this.nil = new Node(null);
        this.nil.setNext(this.nil);
        this.nil.setPrevious(this.nil);
    }

    @Override
    public int size() {
        return this.size;
    }

    public T getLast(){
        return this.nil.next.key;
    }

    public T getFirst(){
        return this.nil.previous.key;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0 && this.nil.next.equals(nil) && this.nil.previous.equals(nil);
    }

    private Node<T> search(Object o){

        Node<T> current = nil.next;

        while( !current.equals(nil) && !current.key.equals(o)  ){
            current = current.next;
        }

        return current;

    }

    @Override
    public boolean contains(Object o) {
        return this.search(o).key != null ;
    }

    @Override
    public Iterator<T> iterator() {
        this.cursor = this.nil;
        return this;
    }

    @Override
    public Object[] toArray() {

        Object[] tab = new Object[this.size];

        Iterator it = this.iterator();
        int i = 0;
        while(it.hasNext()){
            tab[i++] = it.next();
        }
        return tab;
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {

        Node<T> n = new Node<>(t);

        try {
            n.setNext(nil.next);
            nil.next.setPrevious(n);
            n.setPrevious(nil);
            nil.setNext(n);
        }catch (Exception e){
            return false;
        }

        this.size ++;

        return true;

    }

    @Override
    public boolean remove(Object o) {

        Node<T> n = search(o);

        if(n.key == null)
            return false;

        n.previous.next = n.next;
        n.next.previous = n.previous;

        this.size--;

        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {

        try {

            Iterator it = collection.iterator();

            while (it.hasNext()) {
                this.add((T) it.next());
            }

        }catch (Exception e){
            return false;
        }

        return true;
    }


    public boolean addAll(T[] tab) {

        try {

            for (T v : tab) {
                this.add(v);
            }

        }catch (Exception e){
            return false;
        }

        return true;
    }

    /**
     * append directly the entire collection
     * complexity : O(1)
     * **/
    public void append(MyLinkedList<T> l2){

        if(l2.size > 0 ) {
            l2.nil.previous.next = this.nil.next;
            this.nil.next.previous = l2.nil.previous;
            l2.nil.next.previous = this.nil;
            this.nil.next = l2.nil.next;
            this.size += l2.size;
        }

    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.nil.next = this.nil;
        this.nil.previous = this.nil;
        this.size = 0;
    }

    @Override
    public T get(int i) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public T set(int i, T t) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public void add(int i, T t) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public T remove(int i) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public List<T> subList(int start, int end) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public boolean hasNext() {
        this.cursor = this.cursor.next;
        return !this.cursor.equals(nil);
    }

    @Override
    public T next() {
        return this.cursor.key;
    }

    private class Node<T>{

        private T key;
        private Node<T> previous, next;

        public Node(T key) {
            this.key = key;
        }

        public void setPrevious(Node<T> previous) {
            this.previous = previous;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }
    }

    public static void main(String[] args) {

        MyLinkedList l1 = new MyLinkedList(),
                     l2 = new MyLinkedList();

        Integer[] v1 = new Integer[]{1,2,3,4};
        Integer[] v2 = new Integer[]{5,6,7,8,9,10};

        l1.addAll(v1);
        l2.addAll(v2);

        Iterator it = l1.iterator();

        while(it.hasNext()){
            System.out.print(it.next()+" ");
        }

        System.out.println();

        it = l2.iterator();

        while(it.hasNext()){
            System.out.print(it.next()+" ");
        }

        System.out.println();

        l1.append(l2);

        it = l1.iterator();

        while(it.hasNext()){
            System.out.print(it.next()+" ");
        }

    }

}
