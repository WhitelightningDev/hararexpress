package com.whitelightningdev.hararexpress;

import java.util.List;

public class Store {
    private String name;
    private List<Item> items;

    public Store() {
    }

    public Store(String name, List<Item> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }
}

