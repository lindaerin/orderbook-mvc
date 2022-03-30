package com.flooringmastery.service;

import java.util.List;
import java.util.stream.Collectors;

import com.flooringmastery.dao.FlooringMasteryDao;
import com.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.flooringmastery.dto.Order;

public class FlooringMasteryServiceLayerImpl implements FlooringMasteryServiceLayer{

    private FlooringMasteryDao dao;

    public FlooringMasteryServiceLayerImpl(FlooringMasteryDao dao) {
        this.dao = dao;
        
    }

    @Override
    public List<Order> getOrderForADate(String orderDate) throws FlooringMasteryPersistenceException {
        List<Order> allOrderList = getAllOrders();
        return allOrderList.stream().filter(order -> order.getOrderDate().equals(orderDate)).collect(Collectors.toList());
    }

    @Override
    public List<Order> getAllOrders() throws FlooringMasteryPersistenceException {
        return dao.getAllOrders();
    }

    @Override
    public void removeSelectedOrder() {
        
    }
    
}
