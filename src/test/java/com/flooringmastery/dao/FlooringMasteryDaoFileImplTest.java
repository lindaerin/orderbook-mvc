package com.flooringmastery.dao;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;

public class FlooringMasteryDaoFileImplTest {

    final private static String DATA_PATH = "./src/main/resources/TestFileData/";
    
    @BeforeAll
    void setUp() {
        File dir;
        
        dir = new File(DATA_PATH + "Orders/");
        
        for (File file: dir.listFiles()) {
            file.delete();
        }
    }
    
}
