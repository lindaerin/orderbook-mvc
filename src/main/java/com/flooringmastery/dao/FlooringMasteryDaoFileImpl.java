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
import com.flooringmastery.dto.Product;
import com.flooringmastery.dto.Tax;

public class FlooringMasteryDaoFileImpl implements FlooringMasteryDao {

    private Map<Integer, Order> orderMap = new HashMap<>();
    private Map<String, Tax> taxMap = new HashMap<>();
    private Map<String, Product> productMap = new HashMap<>();

    String DELIMITER = ",";
    String BASEDATADIR;
    String ORDERDIR;
    String TAXPATH;
    String PRODUCTPATH;
    String BACKUPPATH;
    
    public FlooringMasteryDaoFileImpl() {
        BASEDATADIR = "data/";
        ORDERDIR = BASEDATADIR + "Orders/";
        PRODUCTPATH = BASEDATADIR + "Data/Products.txt";
        TAXPATH = BASEDATADIR + "Data/Taxes.txt";
        BACKUPPATH = BASEDATADIR + "Backup/DataExport.txt";
    }

    public void loadAllData() throws FlooringMasteryPersistenceException {
        loadAllOrderData();
        loadTaxData();
        loadProductData();
    }

    @Override
    public List<Order> getAllOrders() throws FlooringMasteryPersistenceException {
        loadAllData();
        return new ArrayList<Order>(orderMap.values());
    }

    @Override
    public Order addAnOrder(Order order) throws FlooringMasteryPersistenceException {
        loadAllData();
        Order newOrder = orderMap.put(order.getOrderNumber(), order);
        writeOrderData();

        return newOrder;
        
    }

    @Override
    public void removeAnOrder(int removedOrderNumber) throws FlooringMasteryPersistenceException {
        loadAllData();
        orderMap.remove(removedOrderNumber);
        writeOrderData();

    }

    private void loadAllOrderData() throws FlooringMasteryPersistenceException {
        // get all the txt file under Order directory and store in List
        List<String> orderFileList = getAllOrderFiles(ORDERDIR);

        // get all the date from file path
        List<String> dateFromList = getDateFromList(orderFileList);

        for (int i = 0; i < orderFileList.size(); i++) {
            // go through each Orders_ txt file
            loadAnOrderData(orderFileList.get(i), dateFromList.get(i));
        }
    }

    // get the date section from "ex: Orders_date"
    private List<String> getDateFromList(List<String> orderFileList) {
        List<String> datesFromFile = new ArrayList<>();

        for (int i = 0; i < orderFileList.size(); i++) {
            String currentOrderFile = orderFileList.get(i);
            String trimString = currentOrderFile.substring(currentOrderFile.lastIndexOf("_") + 1);
            String getOrderDateFromFile = trimString.substring(0, trimString.length() - 4);
            datesFromFile.add(getOrderDateFromFile);
        }

        return datesFromFile;
    }

    // get all the text files in Orders Directory
    public List<String> getAllOrderFiles(String orderDir) {
        List<String> orderFiles = new ArrayList<>();
        File orderDirObj = new File(orderDir);
        File[] fileList;

        try {
            // get all files in the Orders directory
            fileList = orderDirObj.listFiles(); 

            // add to list only if the file starts with Orders_
            if (fileList != null) {
                orderFiles = Arrays.stream(fileList).filter(
                        path -> path.getName().startsWith("Orders_")).map(
                                file -> file.toString())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return orderFiles;
    }


    /// ==== LOAD ORDER TAX PRODUCT

    // load the data to be read
    public void loadAnOrderData(String filePath, String orderDate) throws FlooringMasteryPersistenceException {
        Scanner scanner;

        try {
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(filePath)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException(
                    "Could not load order data into memory.", e);
        }

        String currentLine;
        Order currentOrder;

        // skip the first line in txt file: header
        currentLine = scanner.nextLine();

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentOrder = unmarshallOrder(currentLine, orderDate);
            orderMap.put(currentOrder.getOrderNumber(), currentOrder);
        }

        scanner.close();
    }

    @Override
    public void writeOrderData() throws FlooringMasteryPersistenceException {
        String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType" +
        ",Area,CostPerSquareFoot,LaborCostPerSquareFoot" +
        ",MaterialCost,LaborCost,Tax,Total";

        // if the last order number is deleted from text file remove the text file
        File orderDir = new File(ORDERDIR);         
        File[] filelist = orderDir.listFiles();     // get all files in Orders dir

        for (File file : filelist) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        PrintWriter out;
        String orderAsText;
        List<Order> orderList = getAllOrders();

        // group orders by the order date
        Map<String, List<Order>> fileMap = orderList.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate()));
        for (String date : fileMap.keySet()) {

            List<Order> orderListPerDate = fileMap.get(date);

            // write to an individual order file
            String filename = "Orders_" + date + ".txt";
            String filepath = ORDERDIR + filename;

            // open file
            try {
                out = new PrintWriter(new FileWriter(filepath));
            } catch (IOException e) {
                throw new FlooringMasteryPersistenceException(
                        "Could not save order data.", e);
            }

            // write the header into the file
            out.println(HEADER);

            // write into the file
            for (Order currentOrder : orderListPerDate) {
                orderAsText = marshallOrder(currentOrder);
                out.println(orderAsText);
                out.flush();
            }

            out.close();
        }
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
        orderAsText += anOrder.getTotal();

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

    public void loadTaxData() throws FlooringMasteryPersistenceException {
        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(TAXPATH)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Error: Could not load tax data into memory.", e);
        }

        String currentLine;
        Tax currentTax;

        currentLine = scanner.nextLine(); // skip the header

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();

            if (!currentLine.isEmpty()) {
                currentTax = unmarshallTax(currentLine);
                taxMap.put(currentTax.getStateAbbreviation(), currentTax);
            } else {
                currentLine = scanner.nextLine();
            }
        }

        scanner.close();
    }

    private Tax unmarshallTax(String taxAsText) {
        String[] taxTokens = taxAsText.split(DELIMITER);

        String stateAbbreviation = taxTokens[0];
        String stateName = taxTokens[1];
        BigDecimal taxRate = new BigDecimal(taxTokens[2]);

        Tax taxFromFile = new Tax(stateAbbreviation, stateName, taxRate);

        return taxFromFile;
    }

    @Override
    public Tax getTax(String stateAbbreviation) {
        return taxMap.get(stateAbbreviation);
    }

    public void loadProductData() throws FlooringMasteryPersistenceException {
        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(PRODUCTPATH)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Error: Could not load product data into memory.", e);
        }

        String currentLine;
        Product currentProduct;

        currentLine = scanner.nextLine(); // skip the header

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();

            if (!currentLine.isEmpty()) {
                currentProduct = unmarshallProduct(currentLine);
                productMap.put(currentProduct.getProductType(), currentProduct);
            } else {
                currentLine = scanner.nextLine();
            }
        }

        scanner.close();
    }

    private Product unmarshallProduct(String productAsText) {
        String[] productTokens = productAsText.split(DELIMITER);

        String productType = productTokens[0];
        BigDecimal costPerSquareFoot = new BigDecimal(productTokens[1]);
        BigDecimal laborCostPerSquareFoot = new BigDecimal(productTokens[2]);

        Product productFromFile = new Product(productType, costPerSquareFoot, laborCostPerSquareFoot);

        return productFromFile;
    }

    @Override
    public List<Product> getProductList() throws FlooringMasteryPersistenceException {
        loadAllData();
        return new ArrayList<Product>(productMap.values());
    }

    @Override
    public Product getProduct(String productType) {
        return productMap.get(productType);
    }

}
