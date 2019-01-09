package com.sbs.jamiathcollection.util;

public class Collector {
    private String collectorName;
    private String collectorId;
    private String collectorPhone;
    public Collector() {
    }

    public Collector(String collectorId,String collectorName,String collectorPhone) {
        this.collectorId = collectorId;
        this.collectorName=collectorName;
        this.collectorPhone=collectorPhone;
    }
    public String getCollectorPhone() {
        return collectorPhone;
    }

    public void setCollectorPhone(String collectorPhone) {
        this.collectorPhone = collectorPhone;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }
}
