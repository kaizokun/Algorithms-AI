package ai.problem;

import ai.Action;
import ai.State;
import ai.games.backPack.BackPackState;
import ai.games.backPack.Item;
import ai.games.backPack.ItemAdd;
import ai.games.backPack.ItemReplace;

import java.util.*;

public class BackPackProblem extends SimpleProblem {

    private List<Item> allItems;
    private double maxWeigh;

    public BackPackProblem(List<Item> items, double maxWeigh) {
        super();
        this.allItems = items;
        this.maxWeigh = maxWeigh;

        Collections.sort(this.allItems, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return Double.compare(o1.getWeigh(), o2.getWeigh());
            }
        });
    }


    @Override
    public List<Action> getActions(State state) {
        //System.out.println("===============GET ACTIONS============="+state);
        BackPackState backPackState = (BackPackState) state;
        List<Action> actions = new LinkedList<>();

        int iItem = 0;
        for(Item item : backPackState.getValue()){
            // System.out.println("****"+item+"***");
            int iItemDispo = 0;
            for(Item itemDispo : allItems){
                // System.out.println("--------- ITEM DISPO "+itemDispo+" containOpposite ? : "+backPackState.getObjectRef().contains(itemDispo));
                if(!backPackState.contain(itemDispo)){

                    Action action = new ItemReplace(item,itemDispo,iItem,iItemDispo);
                    actions.add(action);
                    //  System.out.println("Action "+actions.get(actions.size()-1).getActionName());
                }
                iItemDispo ++;
            }

            iItem ++;
        }

        List<Item> addItem = this.addItem(backPackState.getValue());

        if(!addItem.isEmpty()){
            actions.add(new ItemAdd(addItem));
        }

        return actions;
    }

    private List<Item> addItem(List<Item> items){

        List<Item> itemsAdd = new LinkedList<>();

        double totalWeigh = BackPackState.totalWeigh(items);

        for( Item item : allItems){

            if( !items.contains(item)){

                if( totalWeigh + item.getWeigh() <= maxWeigh ){
                    itemsAdd.add(item);
                    totalWeigh += item.getWeigh();
                }else{
                    break;
                }

            }

        }

        return itemsAdd;

    }


    private State getStateResult(State state, Action action) {

        BackPackState backPackState = (BackPackState) state;
        ItemReplace itemReplace = (ItemReplace) action;
        ArrayList<Item> newItemsList = new ArrayList<>(backPackState.getValue());

        newItemsList.set(itemReplace.getiItemA(), allItems.get(itemReplace.getiItemB()));

        BackPackState newBackPack = new BackPackState(newItemsList);

        //si le poids ne depasse pas la limite on retourne le nouveau sac à dos
        if(newBackPack.totalWeigh() <= maxWeigh)
            return newBackPack;

        return null;
    }


    @Override
    public State getResult(State state, Action action) {

        BackPackState backPackState = (BackPackState) state;
        ArrayList<Item> newItemsList = new ArrayList<>(backPackState.getValue());

        if(action instanceof ItemReplace) {

            ItemReplace itemReplace = (ItemReplace) action;

            newItemsList.set(itemReplace.getiItemA(), allItems.get(itemReplace.getiItemB()));

            BackPackState newBackPack = new BackPackState(newItemsList);

            //si le poids ne depasse pas la limite on retourne le nouveau sac à dos
            if (newBackPack.totalWeigh() <= maxWeigh)
                return newBackPack;


        }else if (action instanceof ItemAdd){

            ItemAdd itemAdd = (ItemAdd) action;

            newItemsList.addAll(itemAdd.getItems());

            BackPackState newBackPack = new BackPackState(newItemsList);

            return newBackPack;
        }

        return null;

    }

    @Override
    public double getGoalCostEstimation(State state) {
        return - ((BackPackState)state).totalValue();
    }

    @Override
    public boolean isGoal(State state) {
        return this.goal.isGoal(state) ;
    }

    @Override
    public double getStateValue(State state) {
        return ((BackPackState)state).totalValue();
    }

    @Override
    public State rdmState() {

        double packWeigh = 0;
        ArrayList<Item> itemList;
        ArrayList<Item> itemsDispo;

        itemList = new ArrayList<>();
        itemsDispo = new ArrayList<>(this.allItems);

        while (packWeigh < this.maxWeigh ) {

            int rdmId = util.Util.rdnInt(0, itemsDispo.size() - 1);
            Item rdmItem = itemsDispo.get(rdmId);

            if (!itemList.contains(rdmItem)) {

                itemList.add(rdmItem);
                packWeigh += rdmItem.getWeigh();
                itemsDispo.remove(rdmId);

            }
        }

        //System.out.println(itemList);

        while(packWeigh > maxWeigh){
            packWeigh -= itemList.remove(itemList.size()-1).getWeigh();
        }

        return new BackPackState(itemList);
    }


    public void removeTooHeavyItems(List<Item> items, double weighAvalaible){

        List<Item> removeItems = new LinkedList<>();
        for(Item item : items){
            if(item.getWeigh() > weighAvalaible ){
                removeItems.add(item);
            }
        }

        items.removeAll(removeItems);

    }

    public List<Item> allItemsCopy(){
        return new ArrayList<>(this.allItems);
    }

    public double getMaxWeigh() {
        return maxWeigh;
    }
}
