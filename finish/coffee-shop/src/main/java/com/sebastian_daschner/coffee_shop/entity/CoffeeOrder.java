package com.sebastian_daschner.coffee_shop.entity;

import javax.json.bind.annotation.JsonbTransient;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class CoffeeOrder {

    @JsonbTransient
    private UUID id;

    @NotNull
    private CoffeeType type;

    private OrderStatus status = OrderStatus.PREPARING;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CoffeeType getType() {
        return type;
    }

    public void setType(CoffeeType type) {
        this.type = type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CoffeeOrder{" +
                "id=" + id +
                ", type=" + type +
                ", status=" + status +
                '}';
    }

}
