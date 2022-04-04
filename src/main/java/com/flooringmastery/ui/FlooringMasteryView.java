package com.flooringmastery.ui;

import java.math.BigDecimal;
import java.util.List;

import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.service.FlooringMasteryInvalidFieldInput;

public class FlooringMasteryView {

    private UserIO io;

    public FlooringMasteryView(UserIO io) {
        this.io = io;
    }

    public int printMenuAndGetSelection() {
        io.print("\n * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Data");
        io.print("* 6. Quit");
        io.print(" * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");

        int selection = io.readInt("Please select from the" + " above choices: ", 1, 7);
        return selection;
    }

    public String getOrderDate() {
        return io.readString("Enter order date in mmddyyyy format: ");
    }

    public int getOrderNumber(String option) {
        return io.readInt("\nEnter order number to " + option + ": ");
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

            io.print("\nOrder number: " + orderNumber + "\n" + "Customer Name:" + customerName
                    + " State:" + state + " Tax Rate:" + taxRate + " Product Type:" + productType + " Area:" + area
                    + "\n" + "Cost per Square Foot:" + costPerSquareFoot + " Labor cost per Square Foot:"
                    + laborCostPerSquareFoot + " Material cost:" + materialCost + " Labor cost:" + laborCost
                    + " Tax:" + tax + " Total:" + total);
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

    public Order getNewOrderInfo(Order newOrder) throws FlooringMasteryInvalidFieldInput {
        String customerName = io.readString("Enter Customer name: ");
        String state = io.readString("Enter State Abbreviation: ");
        String productType = io.readString("Enter Product type: ");
        BigDecimal area = io.readBigDecimal("Enter Area (Min: 100sqft): ");
        String orderDate = io.readString("Enter Order Date in mmddyyyy: ");

        newOrder.setCustomerName(customerName);
        newOrder.setState(state);
        newOrder.setProductType(productType);
        newOrder.setArea(area);
        newOrder.setOrderDate(orderDate);

        return newOrder;
    }

    public void displayProductList(List<Product> productList) {
        for (Product product : productList) {
            String productType = product.getProductType();
            BigDecimal costPerSquareFoot = product.getCostPerSquareFoot();
            BigDecimal laborCostPerSquareFoot = product.getLaborCostPerSquareFoot();

            String productInfo = String.format("%s\tCost: %s\t Labor Cost: %s", productType, costPerSquareFoot,
                    laborCostPerSquareFoot);

            io.print(productInfo);
        }
    }

    public void showOrderSummary(Order order) {
        io.print("\n===Order Summary===");
        String orderInfo = String.format(
                "Customer Name: %s \nState: %s \nProduct: %s \nArea: %s\nMaterial Cost: %s \nLabor Cost: %s \nTax: %s \nTotal: %s\n",
                order.getCustomerName(), order.getState(), order.getProductType(), order.getArea(),
                order.getMaterialCost(), order.getLaborCost(), order.getTax(), order.getTotal());

        io.print(orderInfo);
    }


    public String orderPrompt(String option){
        switch(option) {
            case "add":
                return io.readString("Would you like to place the order [y/n]: ");
            case "remove":
                return io.readString("Are you sure you want to remove this order [y/n]: ");
            case "edit":
                return io.readString("Are you sure you want to make these edits [y/n]: ");
            default:
                return "";
        }
    }

    public String getNewFields(Order orderToEdit, int fieldNumber) {
        switch (fieldNumber) {
            case 1:
                return io.readString("Enter customer name (" + orderToEdit.getCustomerName() + "): ");
            case 2:
                return io.readString("Enter state (" + orderToEdit.getState() + ")");
            case 3:
                return io.readString("Enter product type (" + orderToEdit.getProductType() + ")");
            case 4:
                return io.readString("Enter area (" + orderToEdit.getArea() + ")");
            default:
                return "";
        }
    }

}
