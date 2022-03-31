package com.flooringmastery.controller;

import java.util.List;

import com.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.flooringmastery.dto.Order;
import com.flooringmastery.dto.Product;
import com.flooringmastery.service.FlooringMasteryInvalidDateInput;
import com.flooringmastery.service.FlooringMasteryInvalidFieldInput;
import com.flooringmastery.service.FlooringMasteryServiceLayer;
import com.flooringmastery.ui.FlooringMasteryView;

public class FlooringMasteryController {
    private FlooringMasteryView view;
    private FlooringMasteryServiceLayer service;

    public FlooringMasteryController(FlooringMasteryView view, FlooringMasteryServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run() throws FlooringMasteryPersistenceException, FlooringMasteryInvalidDateInput,
            FlooringMasteryInvalidFieldInput {
        boolean keepGoing = true;
        int menuSelection = 0;

        try {
            while (keepGoing) {

                menuSelection = getMenuSelection();

                switch (menuSelection) {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addAnOrder();
                        break;
                    case 3:
                        System.out.print("Edit Orders");
                        break;
                    case 4:
                        removeAnOrder();
                        break;
                    case 5:
                        System.out.print("Export Data");
                        break;
                    case 6:
                        keepGoing = false; // quit program
                        break;
                    default:
                        unknownCommand();
                }
            }

            exitMessage();

        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }

    }

    private void displayOrders() throws FlooringMasteryPersistenceException {
        // ask user for order date then display order for that date
        // if order does not exist display error and return to main menu

        view.displayBanner("Display Orders");
        String orderDate = view.getOrderDate();
        List<Order> getOrdersBasedOnDate = service.getOrderForADate(orderDate);

        if (getOrdersBasedOnDate.size() > 0) {
            view.displayOrders(getOrdersBasedOnDate);
        } else {
            view.displayMessage("No existing orders for " + orderDate);
        }
        
    }

    private void addAnOrder() throws FlooringMasteryPersistenceException, FlooringMasteryInvalidDateInput,
            FlooringMasteryInvalidFieldInput {
        view.displayBanner("Add An Order");

        int newOrderNumber = service.getNewOrderNumber();

        // display list of products to choose from
        view.displayMessage("\nList of Avaliable Products");
        List<Product> productList = service.getProductList();
        Order processedOrder = new Order(0);

        view.displayProductList(productList);
        view.displayMessage("");

        boolean hasError = true;

        do {
            try {
                Order newOrder = view.getNewOrderInfo(new Order(newOrderNumber));
                processedOrder = service.processNewOrder(newOrder);
                hasError = false;
            } catch (FlooringMasteryInvalidDateInput | FlooringMasteryInvalidFieldInput e) {
                view.displayErrorMessage(e.getMessage());
            }

        } while (hasError);

        view.showOrderSummary(processedOrder);

        do {
            String answer = view.placeOrderPrompt();

            if (answer.equals("y")) {
                service.addNewOrder(processedOrder);
                view.displayMessage("\nSuccessfully added Order");
                hasError = false;
            } else if (answer.equals("n")) {
                run();
                hasError = false;
            } else {
                view.displayMessage("Please enter [y/n] as answer.");
                hasError = true;
            }
        } while (hasError);

    }

    private void removeAnOrder() throws FlooringMasteryPersistenceException {

        view.displayBanner("Remove An Order");

        String orderDate = view.getOrderDate();

        // get all the orders for the date user input
        List<Order> getOrdersBasedOnDate = service.getOrderForADate(orderDate);

        // if the list size is 0 order date does not exist
        if (getOrdersBasedOnDate.size() == 0) {
            view.displayErrorMessage("Order Date does not exist.");
        }

        if (getOrdersBasedOnDate.size() > 0) {
            view.displayMessage("\nList of orders for " + orderDate);
            view.displayOrders(getOrdersBasedOnDate);

            int removedOrderNumber = view.getRemoveOrderNumber();
            int currentOrderNumber = 0;

            for (int i = 0; i < getOrdersBasedOnDate.size(); i++) {
                // get the order number of the orders in list
                currentOrderNumber = getOrdersBasedOnDate.get(i).getOrderNumber();

                // check order number equals to the user input:
                if (currentOrderNumber == removedOrderNumber) {
                    service.removeSelectedOrder(removedOrderNumber);
                    view.displayMessage("Successfully Removed Order Number: " + currentOrderNumber);
                    break;
                } else {
                    // if order number to be removed does not exist set num to 0
                    currentOrderNumber = 0;
                }
            }

            if (currentOrderNumber == 0) {
                view.displayErrorMessage("Order Number does not exist. ");
            }
        }
    }

    private int getMenuSelection() {
        return view.printMenuAndGetSelection();
    }

    private void exitMessage() {
        view.displayMessage("Exit: GoodBye!");
    }

    private void unknownCommand() {
        view.displayMessage("Error: Unknown Command!");
    }

}
