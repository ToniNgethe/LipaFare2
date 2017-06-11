package com.example.toni.lipafare.Passanger.PassModel;

/**
 * Created by toni on 5/8/17.
 */

public class PassangerMatatuModel {
    private String conductor, driver,from,logo, name,plate,sacco, sits,status, to;

    public PassangerMatatuModel() {
    }

    public PassangerMatatuModel(String conductor, String driver, String from, String logo, String name, String plate, String sacco, String sits, String status, String to) {
        this.conductor = conductor;
        this.driver = driver;
        this.from = from;
        this.logo = logo;
        this.name = name;
        this.plate = plate;
        this.sacco = sacco;
        this.sits = sits;
        this.status = status;
        this.to = to;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getSacco() {
        return sacco;
    }

    public void setSacco(String sacco) {
        this.sacco = sacco;
    }

    public String getSits() {
        return sits;
    }

    public void setSits(String sits) {
        this.sits = sits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
