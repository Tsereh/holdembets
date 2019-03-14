// Stores game players data. Implements Parceable so it can be stored in savedInstanceState.

package com.example.android.holdembets;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
    private String name;
    private Double balance, currentBet;
    private boolean admin, turn, fold;
    private int seat;// Players seat. 0 = SB, 1 = BB, 2 = UTG ... LAST = D

    public Player(String name, Double balance, Double currentBet, boolean admin, int seat, boolean fold) {
        this.name = name;
        this.balance = balance;
        this.currentBet = currentBet;
        this.admin = admin;
        this.seat = seat;
        this.turn = false;
        this.fold = fold;
    }

    public String getName() {
        return name;
    }

    public Double getBalance() {
        return balance;
    }

    public Double getCurrentBet() { return currentBet; }

    public void setCurrentBet(Double currentBet) {
        this.currentBet = currentBet;
    }

    public void matchCurrentBet(Double betToMatch) {
        Double betRaise = betToMatch - this.currentBet;
        this.balance = this.balance - betRaise;
        this.currentBet = betToMatch;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isFold() {
        return fold;
    }

    public void setFold(boolean fold) {
        this.fold = fold;
    }

    public int getSeat() { return seat; }

    public void setSeat(int seat) { this.seat = seat; }

    public boolean isTurn() { return turn; }

    public void setTurn(boolean turn) { this.turn = turn ; }

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
