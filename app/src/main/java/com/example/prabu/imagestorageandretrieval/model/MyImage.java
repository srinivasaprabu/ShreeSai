package com.example.prabu.imagestorageandretrieval.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Admin on 29/01/2018.
 */

public class MyImage {
    private long cusId;
    private String name, mobile, model, complaint, amount, status, date, path;

    public MyImage(long cusId, String name, String mobile, String model, String complaint, String amount, String status, String date, String path) {
        this.cusId = cusId;
        this.name = name;
        this.mobile = mobile;
        this.model = model;
        this.complaint = complaint;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.path = path;
    }


    public MyImage() {
    }

    public long getCusId() {
        return cusId;
    }

    public void setCusId(long cusId) {
        this.cusId = cusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDate() {
        return date;

    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public String toString() {
        return
                "cusId=" + cusId + "\n" +
                        "Name=" + name + "\n" +
                        "Mobileno=" + mobile + "\n" +
                        "Model=" + model + "\n" +
                        "Complaint=" + complaint + "\n" +
                        "Amount=" + amount + "\n" +
                        "Status=" + status + "\n" +
                        "Date=" + date;
    }
}
