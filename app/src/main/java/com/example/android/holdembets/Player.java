package com.example.android.holdembets;

public class Player {
    private String name;
    private Double balance;
    private boolean admin;

    public Player(String name, Double balance, boolean admin) {
        this.name = name;
        this.balance = balance;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public Double getBalance() {
        return balance;
    }

    public boolean isAdmin() {
        return admin;
    }
}
