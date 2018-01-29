package com.example.exact_it_dev.lifoutapos.adapter;

/**
 * Created by EXACT-IT-DEV on 1/29/2018.
 */

public class DataModel {
    String txtAmount;
    String txtTransaction;
    String txtCommission;
    String txtTranstype;

    public DataModel(String txtAmount, String txtTransaction, String txtCommission, String txtTranstype) {
        this.txtAmount = txtAmount;
        this.txtTransaction = txtTransaction;
        this.txtCommission = txtCommission;
        this.txtTranstype = txtTranstype;
    }

    public String getTxtAmount() {
        return txtAmount;
    }

    public void setTxtAmount(String txtAmount) {
        this.txtAmount = txtAmount;
    }

    public String getTxtTransaction() {
        return txtTransaction;
    }

    public void setTxtTransaction(String txtTransaction) {
        this.txtTransaction = txtTransaction;
    }

    public String getTxtCommission() {
        return txtCommission;
    }

    public void setTxtCommission(String txtCommission) {
        this.txtCommission = txtCommission;
    }

    public String getTxtTranstype() {
        return txtTranstype;
    }

    public void setTxtTranstype(String txtTranstype) {
        this.txtTranstype = txtTranstype;
    }
}
