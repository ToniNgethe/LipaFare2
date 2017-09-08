package com.toni.lipafare.Passanger.PassModel;

/**
 * Created by toni on 5/25/17.
 */

public class TicketModel {
    private String image;
    private String matatu;
    private int sits;
    private int status;
    private int total;
    private String from;
    private String to;

    public TicketModel() {
    }

    public TicketModel(String image, String matatu, int sits, int status, int total, String from, String to) {
        this.image = image;
        this.matatu = matatu;
        this.sits = sits;
        this.status = status;
        this.total = total;
        this.from = from;
        this.to = to;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMatatu() {
        return matatu;
    }

    public void setMatatu(String matatu) {
        this.matatu = matatu;
    }

    public int getSits() {
        return sits;
    }

    public void setSits(int sits) {
        this.sits = sits;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
