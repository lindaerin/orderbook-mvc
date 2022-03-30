package com.flooringmastery.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.flooringmastery.dto.Order;

public class FlooringMasteryDaoFileImpl implements FlooringMasteryDao {

    String BASEDATADIR;
    String ORDERDIR;
    String TAXPATH;
    String PRODUCTPATH;
    String BACKUPPATH;

    private Map<Integer, Order> orderMap = new HashMap<>();
    // Map<String, Tax> taxMap = new HashMap<>();
    // Map<String, Product> productMap = new HashMap<>();
    String DELIMITER = ",";
    String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType" +
            ",Area,CostPerSquareFoot,LaborCostPerSquareFoot" +
            ",MaterialCost,LaborCost,Tax,Total,OrderDate";

    public FlooringMasteryDaoFileImpl() {
        BASEDATADIR = "data/";
        ORDERDIR = BASEDATADIR + "Orders/";
    }


    @Override
    public List<Order> getAllOrders() throws FlooringMasteryPersistenceException {
        loadAllOrderData();
        return new ArrayList<Order>(orderMap.values());
    }

    private void loadAllOrderData() throws FlooringMasteryPersistenceException {
        // get all the txt file under Order directory and store in List
        List<String> orderFileList = getAllOrderFiles(ORDERDIR);     
        
        // get all the date from file path  
        List<String> dateFromList = getDateFromList(orderFileList);

        for(int i = 0; i < orderFileList.size(); i++){
            // go through each Orders_ txt file and set in map
            loadAnOrderData(orderFileList.get(i), dateFromList.get(i));     
        }
    }


    // get the date section from "ex: Orders_date"
    private List<String> getDateFromList(List<String> orderFileList) {
        List<String> datesFromFile = new ArrayList<>();     

        for(int i = 0;  i < orderFileList.size(); i++){
            String currentOrderFile = orderFileList.get(i);
            // trim for all strings are Orders_ 
            String trimString = currentOrderFile.substring(currentOrderFile.lastIndexOf("_") + 1);
            // remove .txt from date.txt      
            String getOrderDateFromFile = trimString.substring(0, trimString.length() - 4);             
            datesFromFile.add(getOrderDateFromFile);                                           
        }

        return datesFromFile;
    }

    // get all the text files in Orders Directory
    public List<String> getAllOrderFiles(String orderDir) {
        List<String> orderFiles = new ArrayList<String>();
        File orderDirObj = new File(orderDir);
        File[] fileList;

        try {
            fileList = orderDirObj.listFiles();
            if (fileList != null) {
                orderFiles = Arrays.stream(fileList).filter(
                        path -> path.getName().startsWith("Orders_")).map(
                                file -> file.toString())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Unable to find the file.");
        }

        return orderFiles;
    }

    // load the data to be read
    public void loadAnOrderData(String filePath, String orderDate) throws FlooringMasteryPersistenceException {
        Scanner scanner;

        try {
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(filePath)));
        }
        catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException(
                    "Could not load order data into memory.", e);
        }

        String currentLine;
        Order currentOrder;

        // skip the first line in txt file which is header
        currentLine = scanner.nextLine();       

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentOrder = unmarshallOrder(currentLine, orderDate);
            orderMap.put(currentOrder.getOrderNumber(), currentOrder);
        }

        scanner.close();
    }

   

    private String marshallOrder(Order anOrder) {

        String orderAsText = anOrder.getOrderNumber() + DELIMITER;
        orderAsText += anOrder.getCustomerName() + DELIMITER;
        orderAsText += anOrder.getState() + DELIMITER;
        orderAsText += anOrder.getTaxRate() + DELIMITER;
        orderAsText += anOrder.getProductType() + DELIMITER;
        orderAsText += anOrder.getArea() + DELIMITER;
        orderAsText += anOrder.getCostPerSquareFoot() + DELIMITER;
        orderAsText += anOrder.getLaborCostPerSquareFoot() + DELIMITER;
        orderAsText += anOrder.getMaterialCost() + DELIMITER;
        orderAsText += anOrder.getLaborCost() + DELIMITER;
        orderAsText += anOrder.getTax() + DELIMITER;
        orderAsText += anOrder.getTotal() + DELIMITER;
        // orderAsText += anOrder.getOrderDate();

        return orderAsText;
    }
    

    private Order unmarshallOrder(String orderEntryText, String orderDate) {

        String[] orderTokens = orderEntryText.split(DELIMITER);

        int i = 0;
        int orderId = Integer.parseInt(orderTokens[i++]);

        Order orderFromFile = new Order(orderId);

        orderFromFile.setCustomerName(orderTokens[i++]);
        orderFromFile.setState(orderTokens[i++]);
        orderFromFile.setTaxRate(new BigDecimal(orderTokens[i++]));
        orderFromFile.setProductType(orderTokens[i++]);
        orderFromFile.setArea(new BigDecimal(orderTokens[i++]));
        orderFromFile.setCostPerSquareFoot(new BigDecimal(orderTokens[i++]));
        orderFromFile.setLaborCostPerSquareFoot(new BigDecimal(orderTokens[i++]));
        orderFromFile.setMaterialCost(new BigDecimal(orderTokens[i++]));
        orderFromFile.setLaborCost(new BigDecimal(orderTokens[i++]));
        orderFromFile.setTax(new BigDecimal(orderTokens[i++]));
        orderFromFile.setTotal(new BigDecimal(orderTokens[i++]));
        orderFromFile.setOrderDate(orderDate);
        
        return orderFromFile;
    }


    // @Override
    // public Order addOrder(Order order) {
    // return orderMap.put(order.getOrderNumber(), order);
    // }

}
