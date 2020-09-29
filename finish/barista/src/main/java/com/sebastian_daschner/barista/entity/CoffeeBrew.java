package com.sebastian_daschner.barista.entity;

import javax.json.bind.annotation.JsonbProperty;

public class CoffeeBrew {

    private CoffeeType type;
    
    public CoffeeType getType() {
        return type;
    }
    
    public void setType(CoffeeType type) {
        this.type = type;
    }    
}
