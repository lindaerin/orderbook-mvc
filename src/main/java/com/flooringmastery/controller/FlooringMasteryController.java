package com.flooringmastery.controller;

import java.util.List;

import com.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.flooringmastery.dto.Order;
import com.flooringmastery.service.FlooringMasteryServiceLayer;
import com.flooringmastery.ui.FlooringMasteryView;

public class FlooringMasteryController {
    private FlooringMasteryView view;
    private FlooringMasteryServiceLayer service;

    public FlooringMasteryController(FlooringMasteryView view, FlooringMasteryServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run() throws FlooringMasteryPersistenceException {
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
                    System.out.print("Add orders");
                    break;
                case 3:
                    System.out.print("Edit Orders");
                    break;
                case 4:
                    System.out.print("Remove Orders");
                    removeAnOrder();
                    break;
                case 5:
                    System.out.print("Export Data");
                    break;
                case 6: 
                    keepGoing = false;      // quit program    
                    break;
                default:
                    unknownCommand();
            }
        }

        exitMessage();

        } catch(Exception e) {              // change exception to persistance
            view.displayErrorMessage(e.getMessage());
        }

    }

    


    private void removeAnOrder() throws FlooringMasteryPersistenceException {
        view.displayBanner("Remove An Order");

        String orderDate = view.getOrderDate();
        int removeOrderNumber = view.getRemoveOrderNumber(); 
        List<Order> getOrdersBasedOnDate = service.getOrderForADate(orderDate);

        System.out.print(removeOrderNumber);

        for(int i = 0; i < getOrdersBasedOnDate.size(); i++){
            System.out.print(getOrdersBasedOnDate.get(i).getCustomerName());

            int currentOrderNumber = getOrdersBasedOnDate.get(i).getOrderNumber();

            System.out.println(currentOrderNumber);
        }

    }

    private void displayOrders() throws FlooringMasteryPersistenceException {
        // ask user for order date then display order for that date
        // if order does not exist display error and return to main menu

        view.displayBanner("Display Orders");
        String orderDate = view.getOrderDate();
        List<Order> getOrdersBasedOnDate = service.getOrderForADate(orderDate);

        if(getOrdersBasedOnDate.size() > 0){
            view.displayOrders(getOrdersBasedOnDate);
        }else {
            view.displayMessage("No existing orders for " + orderDate);
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
