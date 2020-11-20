package com.example.hydroponicsapp;

import android.content.SharedPreferences;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Set;

public class Users {
    private String user, password;
    private ArrayList<String> systemIDs;

    public Users(){}

    public Users(String username, String password, ArrayList<String> systemIDs) {
        this.user = username;
        this.password = password;
        this.systemIDs = systemIDs;
    }

    public Users(String user, String password, Set<String> systemIDs){
        this.user = user;
        this.password = password;
        this.systemIDs = new ArrayList<>();
        for(String s:systemIDs){
            this.systemIDs.add(s);
        }
    }

    public Users(QuerySnapshot document){
        this.user = (String) document.getDocuments().get(0).get("user");
        this.password = (String) document.getDocuments().get(0).get("password");
        this.systemIDs = (ArrayList<String>) document.getDocuments().get(0).get("systemIDs");
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<String> getSystemIDs() {
        return systemIDs;
    }

    public void setSystemIDs(ArrayList<String> systemIDs) {
        this.systemIDs = systemIDs;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
