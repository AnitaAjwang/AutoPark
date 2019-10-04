package com.example.smartparking;

/**
 * Created by vinrithi on 5/28/2019.
 */

public class SharedValues {
    private static final SharedValues ourInstance = new SharedValues();

    public static SharedValues getInstance() {
        return ourInstance;
    }

    private String mpesaCheckoutID;

    private SharedValues() {
    }

    public String getMpesaCheckoutID() {
        return mpesaCheckoutID;
    }

    public void setMpesaCheckoutID(String mpesaCheckoutID) {
        this.mpesaCheckoutID = mpesaCheckoutID;
    }
}
