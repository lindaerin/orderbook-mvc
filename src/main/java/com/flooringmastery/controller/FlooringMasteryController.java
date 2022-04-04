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
                        editAnOrder();
                        break;
                    case 4:
                        removeAnOrder();
                        break;
                    case 5:
                        exportAllData();
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

    private void exportAllData() {
        view.displayBanner("Export All Data");

        try {
            service.exportAllData();
        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }

        view.displayMessage("Successfully Exported Data to file.");
    }

    private void editAnOrder() throws FlooringMasteryPersistenceException, FlooringMasteryInvalidDateInput,
            FlooringMasteryInvalidFieldInput {
        view.displayBanner("Edit An Order");

        // get list of orders for that date
        List<Order> getOrdersBasedOnDate = getOrderListForDate();

        view.displayMessage("\nList of orders for specified date.");
        view.displayOrders(getOrdersBasedOnDate);

        int editOrderNumber = view.getOrderNumber("edit");
        Order order = service.getOrder(editOrderNumber);

        if(order == null){
            view.displayErrorMessage("Order Number does not exist. ");
        } else {
            view.displayBanner("Editing Order Number " + editOrderNumber);
            order = getNewFields(order, editOrderNumber);
            view.showOrderSummary(order);
            getAnswerToOrderPrompt("edit");
        }

        // prompt user to save edit: yes -> save no -> main menu

    }

    private Order getNewFields(Order orderToEdit, int editOrderNumber)
            throws FlooringMasteryInvalidFieldInput, FlooringMasteryInvalidDateInput,
            FlooringMasteryPersistenceException {
        String newField = "";
        boolean hasError = false;
        Order order = new Order(0);

        for (int i = 0; i <= 4; i++) {

            do {
                try {
                    newField = view.getNewFields(orderToEdit, i);

                    if (!newField.equals("")) {
                        order = service.editSelectedOrder(editOrderNumber, i, newField);
                    }
                    hasError = false;

                } catch (FlooringMasteryInvalidFieldInput e) {
                    hasError = true;
                    view.displayErrorMessage(e.getMessage());
                }

            } while (hasError);

        }

        view.displayMessage("Complete: Order has been edited");

        return order;
    }

    private void displayOrders() throws FlooringMasteryPersistenceException, FlooringMasteryInvalidDateInput,
            FlooringMasteryInvalidFieldInput {
        view.displayBanner("Display Orders");
        List<Order> getOrdersBasedOnDate = getOrderListForDate();
        view.displayOrders(getOrdersBasedOnDate);
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

        // get new order and validate fields
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

        // loop to ask user whether they want to place order
        getAnswerToOrderPrompt("add");
        service.addNewOrder(processedOrder);
        view.displayMessage("\nSuccessfully Added Order");
    }

    private void removeAnOrder() throws FlooringMasteryPersistenceException, FlooringMasteryInvalidDateInput,
            FlooringMasteryInvalidFieldInput {

        view.displayBanner("Remove An Order");

        List<Order> getOrdersBasedOnDate = getOrderListForDate();

        view.displayMessage("\nList of orders for specified date.");
        view.displayOrders(getOrdersBasedOnDate);

        int removedOrderNumber;

        Order order;
        boolean hasError = false;

        do {
            removedOrderNumber = view.getOrderNumber("remove");
            order = service.getOrder(removedOrderNumber);
            if(order == null){
                view.displayErrorMessage("Order Number does not exist. ");
                hasError = true;
            } else {
                getAnswerToOrderPrompt("remove");
                service.removeSelectedOrder(removedOrderNumber);
                view.displayMessage("Successfully Removed Order Number: " + order.getOrderNumber());
            }

        }while(hasError);

    }

    private String getOrderDate() {
        String orderDate = view.getOrderDate();
        return orderDate;
    }

    private List<Order> getOrderListForDate() throws FlooringMasteryPersistenceException,
            FlooringMasteryInvalidDateInput, FlooringMasteryInvalidFieldInput {

        List<Order> getOrdersBasedOnDate;
        boolean hasError = false;
        
        do {
            String orderDate = getOrderDate();
            getOrdersBasedOnDate = service.getOrderForADate(orderDate);

            if (getOrdersBasedOnDate.size() == 0) {
                view.displayErrorMessage("Order Date does not exist.");
                hasError = true;
            }
            else{
                hasError = false;
            }

        }while(hasError);



        return getOrdersBasedOnDate;
    }

    private void getAnswerToOrderPrompt(String input) throws FlooringMasteryPersistenceException,
            FlooringMasteryInvalidDateInput, FlooringMasteryInvalidFieldInput {
        boolean hasError = false;
        ;
        do {
            String answer = view.orderPrompt(input);

            switch (answer) {
                case "y": // if yes continue back to method
                    break;
                case "n":
                    view.displayMessage("\nAction cancelled. Going Back to Main Menu.");
                    run(); // if no return to main menu
                    break;
                default:
                    view.displayMessage("Please enter [y/n] as answer.");
                    hasError = true;
                    break;
            }

        } while (hasError);

    }

    private int getMenuSelection() {
        return view.printMenuAndGetSelection();
    }

    private void exitMessage() {
        view.displayMessage("Quitting Program...Goodbye!");
    }

    private void unknownCommand() {
        view.displayMessage("Error: Unknown Command!");
    }

}
