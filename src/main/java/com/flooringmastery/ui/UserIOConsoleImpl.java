package com.flooringmastery.ui;

import java.math.BigDecimal;
import java.util.Scanner;


public class UserIOConsoleImpl implements UserIO{
    Scanner console = new Scanner(System.in);

    @Override
    public void print(String msg) {
        System.out.println(msg);
    }

    @Override
    public int readInt(String msgPrompt) {
        boolean invalidInput = true;
        int num = 0;
        while (invalidInput) {
            try {
                System.out.print(msgPrompt);
                String stringValue = console.nextLine();
                num = Integer.parseInt(stringValue);
                invalidInput = false;
            } catch (NumberFormatException e) {
                this.print("Input Error. Please enter number only.");
            }
        }

        return num;
    }

    @Override
    public int readInt(String msgPrompt, int min, int max) {
        int result;

        do {
            result = readInt(msgPrompt);
        } while (result < min || result > max);

        return result;
    }

    @Override
    public String readString(String msgPrompt) {
        System.out.print(msgPrompt);
        return console.nextLine();
    }

    @Override
    public BigDecimal readBigDecimal(String prompt) {
        BigDecimal bigDecimalInput = null;
        boolean invalidInput = true;

        while (invalidInput) {
            try {
                System.out.print(prompt);
                String stringInput = console.nextLine();
                bigDecimalInput = new BigDecimal(stringInput);
                invalidInput = false;
            } catch (NumberFormatException e) {
                this.print("Error: Cannot be blank"); 
                
            }
        }
        return bigDecimalInput;
    }

}
