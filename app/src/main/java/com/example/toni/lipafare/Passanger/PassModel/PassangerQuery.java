package com.example.toni.lipafare.Passanger.PassModel;

/**
 * Created by toni on 5/6/17.
 */

public class PassangerQuery {
    public static final int LAYOUT_FULL = 1;
    public static final int LAYOUT_NULL = 0;

    private String From_address, To_address, From_city, To_city;

    public PassangerQuery() {
    }

    public PassangerQuery(String from_address, String to_address) {
        From_address = from_address;
        To_address = to_address;
    }

    public PassangerQuery(String from_address, String to_address, String from_city, String to_city) {
        From_address = from_address;
        To_address = to_address;
        From_city = from_city;
        To_city = to_city;
    }

    public String getFrom_address() {
        return From_address;
    }

    public void setFrom_address(String from_address) {
        From_address = from_address;
    }

    public String getTo_address() {
        return To_address;
    }

    public void setTo_address(String to_address) {
        To_address = to_address;
    }

    public String getFrom_city() {
        return From_city;
    }

    public void setFrom_city(String from_city) {
        From_city = from_city;
    }

    public String getTo_city() {
        return To_city;
    }

    public void setTo_city(String to_city) {
        To_city = to_city;
    }
}
