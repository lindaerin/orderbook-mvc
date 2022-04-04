package com.flooringmastery.dao;

import java.text.ParseException;
import java.util.List;

import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.Tax;

public interface FlooringMasteryDao {

    List<Order> getAllOrders() throws FlooringMasteryPersistenceException;

    void removeAnOrder(int removedOrderNumber) throws FlooringMasteryPersistenceException;

    void writeOrderData() throws FlooringMasteryPersistenceException, ParseException;

    Tax getTax(String state);

    List<Product> getProductList() throws FlooringMasteryPersistenceException;

    Product getProduct(String productType);

    Order addAnOrder(Order order) throws FlooringMasteryPersistenceException;

    Order editOrder(int orderNumber, int fieldNumber, String newField) throws FlooringMasteryPersistenceException;

    void exportAllData() throws FlooringMasteryPersistenceException;

    Order getSpecifiedOrder(int orderNumber) throws FlooringMasteryPersistenceException;


}
