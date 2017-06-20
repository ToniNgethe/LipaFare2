package com.example.toni.lipafare.Operator.Model;

/**
 * Created by toni on 6/19/17.
 */

public class AllFundsModel {

  private int sits;
    private double total;

    public AllFundsModel() {
    }

    public AllFundsModel(int sits, double total) {
        this.sits = sits;
        this.total = total;
    }

    public int getSits() {
        return sits;
    }

    public void setSits(int sits) {
        this.sits = sits;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
