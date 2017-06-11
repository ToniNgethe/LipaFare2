package com.example.toni.lipafare.Operator.Model;

/**
 * Created by toni on 4/19/17.
 */

public class Matatu {
    private String name, plate, sits, logo, driver, conductor, from, to, status;

    public Matatu() {
    }

    public Matatu(String name, String plate, String sits, String logo, String driver, String conductor, String from, String to, String status) {
        this.name = name;
        this.plate = plate;
        this.sits = sits;
        this.logo = logo;
        this.driver = driver;
        this.conductor = conductor;
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getSits() {
        return sits;
    }

    public void setSits(String sits) {
        this.sits = sits;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
