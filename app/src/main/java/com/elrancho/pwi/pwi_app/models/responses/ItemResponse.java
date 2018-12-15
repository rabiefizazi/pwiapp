package com.elrancho.pwi.pwi_app.models.responses;

import java.util.List;

public class ItemResponse {

    List<Item> items;

    public ItemResponse(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}