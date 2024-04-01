package com.example.springsoap;

import javax.annotation.PostConstruct;
import javax.xml.datatype.XMLGregorianCalendar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import io.foodmenu.gt.webservice.*;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class MealRepository {
    private static final Map<String, Meal> meals = new HashMap<String, Meal>();
    private static final Map<String, Order> orders = new HashMap<String, Order>();

    @PostConstruct
    public void initData() {

        Meal a = new Meal();
        a.setName("Steak");
        a.setDescription("Steak with fries");
        a.setMealtype(Mealtype.MEAT);
        a.setKcal(1100);
        a.setPrice(20);


        meals.put(a.getName(), a);

        Meal b = new Meal();
        b.setName("Portobello");
        b.setDescription("Portobello Mushroom Burger");
        b.setMealtype(Mealtype.VEGAN);
        b.setKcal(637);
        b.setPrice(15);


        meals.put(b.getName(), b);

        Meal c = new Meal();
        c.setName("Fish and Chips");
        c.setDescription("Fried fish with chips");
        c.setMealtype(Mealtype.FISH);
        c.setKcal(950);
        c.setPrice(15);


        meals.put(c.getName(), c);

        Meal d = new Meal();
        d.setName("Vegan Burger");
        d.setDescription("Vegan Burger with fries");
        d.setMealtype(Mealtype.VEGAN);
        d.setKcal(800);
        d.setPrice(10);

        meals.put(d.getName(), d);
    }

    public Meal findMeal(String name) {
        Assert.notNull(name, "The meal's code must not be null");
        return meals.get(name);
    }

    public Meal findBiggestMeal() {

        if (meals == null) return null;
        if (meals.size() == 0) return null;

        var values = meals.values();
        return values.stream().max(Comparator.comparing(Meal::getKcal)).orElseThrow(NoSuchElementException::new);

    }

    public Meal findCheapestMeal() {

        if (meals == null) return null;
        if (meals.size() == 0) return null;

        var values = meals.values();
        return values.stream().min(Comparator.comparing(Meal::getPrice)).orElseThrow(NoSuchElementException::new);

    }

// create new order with meal and quantity
    public Order createOrder(Meal meal, int quantity, XMLGregorianCalendar orderDate, String customerName, String address) {
        Assert.notNull(meal, "The meal must not be null");
        Order order = new Order();
        // generate a random and unique order id
        order.setId(java.util.UUID.randomUUID().toString());
        order.setMeal(meal);
        order.setQuantity(quantity);
        order.setOrderDate(orderDate);
        order.setCustomer(customerName);
        order.setAddress(address);
        return order;
    }

    // add order
    public OrderConfirmation addOrder(Order order) {
        Assert.notNull(order, "The order must not be null");
        // add order to orders map and return confirmation if successful based on return value
        // the return value is the previous value associated with the key, or null if there was no mapping for the key.
        Order addedOrder = orders.put(order.getCustomer(), order);
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setOrder(order);
        if (addedOrder == null) {
            orderConfirmation.setConfirmation("Order added successfully");
        } else {
            orderConfirmation.setConfirmation("Order already exists");
        }
        return orderConfirmation;
    }

    // find order
    public Order findOrder(String customerName) {
        Assert.notNull(customerName, "The customer's name must not be null");
        return orders.get(customerName);
    }

    // get all orders by converting the map to a list
    public List<Order> getAllOrders() {
        return new ArrayList<Order>(orders.values());
    }

}