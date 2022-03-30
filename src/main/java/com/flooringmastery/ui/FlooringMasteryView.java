package com.flooringmastery.ui;

import java.math.BigDecimal;
import java.util.List;

import com.flooringmastery.dto.Order;

public class FlooringMasteryView {

    private UserIO io;

    public FlooringMasteryView(UserIO io) {
        this.io = io;
    }

    public int printMenuAndGetSelection() {
        // io.print(" * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("\n* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Data");
        io.print("* 6. Quit");
        // io.print(" * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

        int selection = io.readInt("Please select from the" + " above choices.", 1, 7);
        return selection;
    }

    public String getOrderDate() {
        return io.readString("Enter order date in mmddyyyy format.");
    }

    public int getRemoveOrderNumber() {
        return io.readInt("Enter order number to be removeed: ");
    }

    public void displayOrders(List<Order> getOrdersBasedOnDate) {
        for (Order order : getOrdersBasedOnDate) {
            int orderNumber = order.getOrderNumber();
            String customerName = order.getCustomerName();
            String state = order.getState();
            BigDecimal taxRate = order.getTaxRate();
            String productType = order.getProductType();
            BigDecimal area = order.getArea();
            BigDecimal costPerSquareFoot = order.getCostPerSquareFoot();
            BigDecimal laborCostPerSquareFoot = order.getLaborCostPerSquareFoot();
            BigDecimal materialCost = order.getMaterialCost();
            BigDecimal laborCost = order.getLaborCost();
            BigDecimal tax = order.getTax();
            BigDecimal total = order.getTotal();

            io.print("\nOrder number: " + orderNumber + " Customer Name: " + customerName 
            + " State: " + state + " Tax Rate: " + taxRate + " Product Type: " + productType + " Area: " + area
            + " Cost per Square Foot: " + costPerSquareFoot + "\n" + "Labor cost per Square Foot: " 
            + laborCostPerSquareFoot + " Material cost: " + materialCost + " Labor cost: " + laborCost
            + " Tax: " + tax + " Total: " + total);
        }
    }


    public void displayBanner(String string) {
        String displayText = String.format("\n====== %s ======", string);
        io.print(displayText);
    }

    public void displayMessage(String message) {
        io.print(message);
    }

    public void displayErrorMessage(String string) {
        io.print("Error: " + string);
    }

}