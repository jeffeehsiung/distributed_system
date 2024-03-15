package com.example.springsoap;

import io.foodmenu.gt.webservice.Order;

public class OrderConfirmation {

    // properties
    protected Order order;
    protected String confirmation;

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setConfirmation(String string) {
        this.confirmation = string;
    }

}
