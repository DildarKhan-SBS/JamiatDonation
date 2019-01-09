package com.sbs.jamiathcollection.util;

public class Donor {

    private String donorName;
    private double amount;
    private String donorId;
    private String donorPhone;
    public Donor() {
    }

    public Donor(String donorId,String donorPhone,String donorName,double amount) {
        this.donorName = donorName;
        this.amount=amount;
        this.donorId=donorId;
        this.donorPhone=donorPhone;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }
    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }


}
