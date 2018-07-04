package ai.games.backPack;

import ai.State;

import java.util.*;

public class BackPackState extends State<ArrayList<Item>> {


    private HashSet<Character> containItem = new HashSet<>();
    private int totalValue = 0;
    public BackPackState(ArrayList<Item> items) {
       this.value = items;
       sortItemList();
       for(Item item : items) {
           containItem.add(item.getId());
           totalValue += item.getValue();
       }

    }

    public void insertItem(Item item){

        int pos = 0;
        for(; pos < this.value.size() && Character.compare(item.getId(), this.value.get(pos).getId()) > 0 ; pos ++ );

        this.value.add(pos,item);
        this.containItem.add(item.getId());
        this.totalValue += item.getValue();
    }

    public void removeItem(int index3) {
        Item it = this.value.remove(index3);
        this.containItem.remove(it.getId());
        this.totalValue -= it.getValue();
    }

    @Override
    public int hashCode() {
        return this.itemListToSTring().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.itemListToSTring().equals(((BackPackState)obj).itemListToSTring());
    }

    @Override
    public String toString() {
        return itemListToSTring()+" - Poids total : "+this.totalWeigh()+" - Valeur total : "+this.totalValue();
    }

    private String itemListToSTring(){
        return this.itemListToSTring(this.value);
    }

    private String itemListToSTring(List<Item> items){

        StringBuffer rs = new StringBuffer(items.size());

        for(Item item : this.value) {
            rs.append(item.getId());
        }

        return rs.toString();
    }

    private void sortItemList(){

        Collections.sort(this.value, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return Character.compare(o1.getId(), o2.getId());
            }
        });

    }

    public double totalWeigh(){
        return totalWeigh(this.value);
    }

    public double totalValue(){
        /*
        double isTrue = 0;
        for(Item item : this.isTrue)
            isTrue += item.getObjectRef();
        return isTrue;
        */
        return totalValue;
    }

    public static double totalWeigh(List<Item> items){
        double weigh = 0;
        for(Item item : items)
            weigh += item.getWeigh();
        return weigh;
    }

    public boolean contain(Item item){
        return this.containItem.contains(item.getId());
    }


}
