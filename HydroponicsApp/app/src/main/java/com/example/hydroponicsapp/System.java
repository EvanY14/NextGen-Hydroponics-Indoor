package com.example.hydroponicsapp;

import com.google.firebase.firestore.QuerySnapshot;

public class System {
    private String systemID, systemName;
    private double ph, ec, tempC, tempF;

    public System(){

    }
    public System(String systemID, String systemName, double ph, double ec, double tempC, double tempF) {
        this.systemID = systemID;
        this.systemName = systemName;
        this.ph = ph;
        this.ec = ec;
        this.tempC = tempC;
        this.tempF = tempF;
    }

    public System(QuerySnapshot document){
        this.ph = (double) document.getDocuments().get(0).get("ph");
        this.ec = (double) document.getDocuments().get(0).get("ec");
        this.tempC = (double) document.getDocuments().get(0).get("tempC");
        this.tempF = (double) document.getDocuments().get(0).get("tempF");
        this.systemName = (String) document.getDocuments().get(0).get("systemName");
    }
    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public double getPh() {
        return ph;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }

    public double getEc() {
        return ec;
    }

    public void setEc(double ec) {
        this.ec = ec;
    }

    public double getTempC() {
        return tempC;
    }

    public void setTempC(double tempC) {
        this.tempC = tempC;
    }

    public double getTempF() {
        return tempF;
    }

    public void setTempF(double tempF) {
        this.tempF = tempF;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
