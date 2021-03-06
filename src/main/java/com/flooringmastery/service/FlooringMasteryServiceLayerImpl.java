package com.flooringmastery.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.flooringmastery.dao.FlooringMasteryDao;
import com.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.Tax;

public class FlooringMasteryServiceLayerImpl implements FlooringMasteryServiceLayer {

    private FlooringMasteryDao dao;

    public FlooringMasteryServiceLayerImpl(FlooringMasteryDao dao) {
        this.dao = dao;
    }

    @Override
    public Order getOrder(int orderNumber) throws FlooringMasteryPersistenceException {
        return dao.getSpecifiedOrder(orderNumber);
        
    }

    @Override
    public List<Order> getOrderForADate(String orderDate) throws FlooringMasteryPersistenceException {
        List<Order> allOrderList = getAllOrders();
        return allOrderList.stream().filter(order -> order.getOrderDate().equals(orderDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getAllOrders() throws FlooringMasteryPersistenceException {
        return dao.getAllOrders();
    }

    @Override
    public void removeSelectedOrder(int removedOrderNumber) throws FlooringMasteryPersistenceException {
        dao.removeAnOrder(removedOrderNumber);
    }

    @Override
    public int getNewOrderNumber() throws FlooringMasteryPersistenceException {
        int newOrderNumber = 0;
        int currentOrderNumber = 1;

        List<Order> allOrderList = dao.getAllOrders();

        for (Order order : allOrderList) {
            currentOrderNumber = order.getOrderNumber();

            if (currentOrderNumber > newOrderNumber) {
                newOrderNumber = currentOrderNumber;
            }
        }
        return newOrderNumber + 1;
    }


    @Override
    public void addNewOrder(Order order) throws FlooringMasteryPersistenceException {
        dao.addAnOrder(order);
        
    }

    @Override
    public Order processNewOrder(Order newOrder)
            throws FlooringMasteryInvalidDateInputException, FlooringMasteryInvalidFieldInputException {

        validateNewOrderFields(newOrder);        
        calculateFields(newOrder);

        return newOrder;
    }


    private void validateNewOrderFields(Order newOrder)
            throws FlooringMasteryInvalidDateInputException, FlooringMasteryInvalidFieldInputException {

        if (newOrder.getCustomerName().trim().length() == 0) {
            throw new FlooringMasteryInvalidFieldInputException("Customer Name should not be blank. \n");

        } else if (dao.getTax(newOrder.getState()) == null) {
            throw new FlooringMasteryInvalidFieldInputException("Enter valid state. \n");

        } else if (dao.getProduct(newOrder.getProductType()) == null) {
            throw new FlooringMasteryInvalidFieldInputException("Enter valid product type. \n");

        } else if (newOrder.getArea().compareTo(new BigDecimal("100")) < 0) {
            throw new FlooringMasteryInvalidFieldInputException("Enter valid Area (Min: 100sqft). \n");
        }

        // order date needs to be in the future - convert string to localdate
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMddyyyy", Locale.ENGLISH);
        LocalDate orderDate;

        try {
            orderDate = LocalDate.parse(newOrder.getOrderDate(), dateFormatter);

        } catch (DateTimeParseException e) {
            throw new FlooringMasteryInvalidDateInputException("Invalid Order Date Format: Enter Order Date as mmddyyyy. \n");
        }

        if (orderDate.isBefore(LocalDate.now())) {
            throw new FlooringMasteryInvalidDateInputException("The Order Date must be in the future. \n");
        }
    }

    private Order calculateFields(Order newOrder) {
        String state = newOrder.getState();
        String productType = newOrder.getProductType();

        Tax tax = dao.getTax(state);
        Product product = dao.getProduct(productType);

        BigDecimal area = newOrder.getArea();
        BigDecimal costPerSquareFoot = product.getCostPerSquareFoot();
        BigDecimal laborCostPerSquareFoot = product.getLaborCostPerSquareFoot();
        BigDecimal taxRate = tax.getTaxRate();
        BigDecimal hundred = new BigDecimal(100);

        BigDecimal materialCost = area.multiply(costPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
        BigDecimal laborCost = area.multiply(laborCostPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
        BigDecimal calculatedTax = (materialCost.add(laborCost)).multiply(taxRate.divide(hundred)).setScale(2,
                RoundingMode.HALF_UP);
        BigDecimal calculatedTotal = materialCost.add(laborCost).add(calculatedTax).setScale(2, RoundingMode.HALF_UP);

        newOrder.setTaxRate(taxRate);
        newOrder.setCostPerSquareFoot(costPerSquareFoot);
        newOrder.setLaborCostPerSquareFoot(laborCostPerSquareFoot);

        newOrder.setMaterialCost(materialCost);
        newOrder.setLaborCost(laborCost);
        newOrder.setTax(calculatedTax);
        newOrder.setTotal(calculatedTotal);

        return newOrder;
    }


    @Override
    public Order editSelectedOrder(int orderNumber, int fieldNumber, String newField) throws FlooringMasteryInvalidFieldInputException, FlooringMasteryInvalidDateInputException, FlooringMasteryPersistenceException {
        Order order = new Order(0);
        Order oldOrder = getOrder(orderNumber);

        try {
            
            order = dao.editOrder(orderNumber, fieldNumber, newField);

            validateFields(order, oldOrder);

            calculateFields(order);

        }
        catch (FlooringMasteryInvalidFieldInputException e) {
            throw new FlooringMasteryInvalidFieldInputException("Invalid input for this field.\n");
       }
       return order;
    }


    private void validateFields(Order newOrder, Order oldOlder) throws FlooringMasteryInvalidFieldInputException {
        if (newOrder.getCustomerName() == null) {
            newOrder.setCustomerName(oldOlder.getCustomerName());
            throw new FlooringMasteryInvalidFieldInputException("Customer Name should not be blank. \n");

        } else if (dao.getTax(newOrder.getState()) == null) {
            newOrder.setState(oldOlder.getState());
            throw new FlooringMasteryInvalidFieldInputException("Enter valid state. \n");

        } else if (dao.getProduct(newOrder.getProductType()) == null) {
            newOrder.setProductType(oldOlder.getProductType());
            throw new FlooringMasteryInvalidFieldInputException("Enter valid product type. \n");

        } else if (newOrder.getArea().compareTo(new BigDecimal("100")) < 0) {
            newOrder.setArea(oldOlder.getArea());
            throw new FlooringMasteryInvalidFieldInputException("Enter valid Area (Min: 100sqft). \n");
        }
    }



    @Override
    public void exportAllData() throws FlooringMasteryPersistenceException {
        dao.exportAllData();
    }

    @Override
    public List<Product> getProductList() throws FlooringMasteryPersistenceException {
        return dao.getProductList();
    }

}
