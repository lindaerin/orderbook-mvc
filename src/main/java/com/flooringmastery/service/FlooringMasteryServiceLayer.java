package com.flooringmastery.service;

import java.util.List;

import com.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.flooringmastery.dto.Order;

public interface FlooringMasteryServiceLayer {

    List<Order> getAllOrders() throws FlooringMasteryPersistenceException;

    List<Order> getOrderForADate(String orderDate) throws FlooringMasteryPersistenceException;

    void removeSelectedOrder();

    // void loadAllData() throws FlooringMasteryPersistenceException;
    
}
