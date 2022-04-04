package com.flooringmastery.service;

import java.util.List;

import com.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;

public interface FlooringMasteryServiceLayer {

    List<Order> getAllOrders() throws FlooringMasteryPersistenceException;

    List<Order> getOrderForADate(String orderDate) throws FlooringMasteryPersistenceException;

    void removeSelectedOrder(int removedOrderNumber) throws FlooringMasteryPersistenceException;

    int getNewOrderNumber() throws FlooringMasteryPersistenceException;

    Order processNewOrder(Order newOrder) throws FlooringMasteryInvalidDateInputException, FlooringMasteryInvalidFieldInputException;

    List<Product> getProductList() throws FlooringMasteryPersistenceException;

    void addNewOrder(Order processedOrder) throws FlooringMasteryPersistenceException;

    Order editSelectedOrder(int editOrderNumber, int i, String newField) throws FlooringMasteryInvalidFieldInputException, FlooringMasteryInvalidDateInputException, FlooringMasteryPersistenceException;

    void exportAllData() throws FlooringMasteryPersistenceException;

    Order getOrder(int orderNumber) throws FlooringMasteryPersistenceException;

}
