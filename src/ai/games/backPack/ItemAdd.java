package ai.games.backPack;

import ai.Action;

import java.util.List;

public class ItemAdd extends Action {

    private List<Item> items;

    public ItemAdd(List<Item> items) {
        this.items = items;
    }

    @Override
    public String getActionName() {
        return "add "+items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
