// Stores game players data. Implements Parceable so it can be stored in savedInstanceState.

package com.example.android.holdembets;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
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

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    // Parceable stuff
    protected Player(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            balance = null;
        } else {
            balance = in.readDouble();
        }
        admin = in.readByte() != 0;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(balance);
        parcel.writeValue(admin);
    }
}
