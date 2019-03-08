package com.example.android.holdembets;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Room implements Parcelable {
    private String name;
    private Double minBuyIn, maxBuyIn, smallBlind, bigBlind;
    private ArrayList<Player> players = new ArrayList<>();

    public Room(String name, Double minBuyIn, Double maxBuyIn, Double smallBlind, Double bigBlind) {
        this.name = name;
        this.minBuyIn = minBuyIn;
        this.maxBuyIn = maxBuyIn;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
    }

    public void addUser(Player player) {
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public Double getMinBuyIn() {
        return minBuyIn;
    }

    public Double getMaxBuyIn() {
        return maxBuyIn;
    }

    public Double getSmallBlind() {
        return smallBlind;
    }

    public Double getBigBlind() {
        return bigBlind;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(minBuyIn);
        parcel.writeDouble(maxBuyIn);
        parcel.writeDouble(smallBlind);
        parcel.writeDouble(bigBlind);
        parcel.writeList(players);
    }

    protected Room(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            minBuyIn = null;
        } else {
            minBuyIn = in.readDouble();
        }
        if (in.readByte() == 0) {
            maxBuyIn = null;
        } else {
            maxBuyIn = in.readDouble();
        }
        if (in.readByte() == 0) {
            smallBlind = null;
        } else {
            smallBlind = in.readDouble();
        }
        if (in.readByte() == 0) {
            bigBlind = null;
        } else {
            bigBlind = in.readDouble();
        }
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}