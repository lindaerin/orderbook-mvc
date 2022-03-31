package com.flooringmastery.ui;

import java.math.BigDecimal;


public interface UserIO {
    void print(String msg);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    String readString(String prompt);

    BigDecimal readBigDecimal(String prompt);
}
