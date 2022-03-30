package com.flooringmastery.dao;

import java.util.List;

import com.flooringmastery.dto.Order;

public interface FlooringMasteryDao {

    List<Order> getAllOrders() throws FlooringMasteryPersistenceException;


    // Order addOrder(Order order);



}
