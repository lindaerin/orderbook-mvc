package com.flooringmastery;

import com.flooringmastery.controller.FlooringMasteryController;
import com.flooringmastery.dao.FlooringMasteryPersistenceException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public final class App {

    private static ApplicationContext context;
    public static void main(String[] args) throws FlooringMasteryPersistenceException {
        
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        FlooringMasteryController controller = context.getBean("controller", FlooringMasteryController.class);
        controller.run();
    }
}
