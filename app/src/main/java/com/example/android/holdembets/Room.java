// Stores game rooms data. Implements Parceable so it can be stored in savedInstanceState.

package com.example.android.holdembets;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

public class Room implements Parcelable {
    private String name;
    private Double minBuyIn, maxBuyIn, smallBlind, bigBlind, pot, currentBiggestBet;
    private ArrayList<Player> players = new ArrayList<>();
    private int stage;// Games stage. 0 = not started, 1 = pre-flop, 2 = flop, 3 = turn, 4 = river.
    private Context context;


    public Room(String name, Double minBuyIn, Double maxBuyIn, Double smallBlind, Double bigBlind, Double currentBiggestBet, Double pot, int stage, Context context) {
        this.name = name;
        this.minBuyIn = minBuyIn;
        this.maxBuyIn = maxBuyIn;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.pot = pot;
        this.stage = stage;
        this.currentBiggestBet = currentBiggestBet;
        this.context = context;
    }

    public void addUser(Player player) {
        players.add(player);
    }

    public void setCurrentBiggestBet(Double nCurrentBiggestBet) {
        this.currentBiggestBet = nCurrentBiggestBet;
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

    public Double getCurrentBiggestBet() { return currentBiggestBet; }

    public Double getPot() { return pot; }

    public void setPot(Double pot) {
        this.pot = pot;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getStage() { return stage; }

    public String getReadableStage() {
        String s = "";
        switch (stage) {
            case 0:
                return context.getString(R.string.not_started);
            case 1:
                return context.getString(R.string.pre_flop);
            case 2:
                return context.getString(R.string.flop);
            case 3:
                return context.getString(R.string.turn);
            case 4:
                return context.getString(R.string.river);
        }
        return s;
    }

    // Finds player from the players list by its name, and updates its balance
    public Player updatePlayerBalance(String name, Double newBalance) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equals(name)) {
                player.setBalance(newBalance);
                return player;
            }
        }
        return null;
    }

    // Sets new SB, BB, D players, sets blinds
    public void setSeats(JSONArray newSeats, int dealersSeat) {
        for (int i = 0; i < newSeats.length(); i++) {
            try {
                String playerName = newSeats.getString(i);

                Iterator<Player> iterator = players.iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    if (player.getName().equals(playerName)) {
                        player.setSeat(i);
                        if (i==0) {
                            // New SB, set bet to SB
                            player.matchCurrentBet(smallBlind);
                        } else if (i==1) {
                            // New BB, set bet to BB
                            player.matchCurrentBet(bigBlind);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Finds player from the players list by its name, and sets its turn
    public void setTurn(String name, boolean turn) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equals(name)) {
                player.setTurn(turn);
            }
        }
    }

    // Finds player from the players list by its name, and sets its turn
    public void setFold(String name, boolean fold) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equals(name)) {
                player.setFold(fold);
            }
        }
    }

    // Finds player from the players list and sets its bet to biggest current bet
    public void playerCalled(String name) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equals(name)) {
                player.matchCurrentBet(currentBiggestBet);
            }
        }
    }

    // Finds player from the players list and raises its bet
    public void playerRaised(String name, Double raisedBet) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equals(name)) {
                player.matchCurrentBet(raisedBet);
            }
        }
    }

    // Finds player from the players list by its name, and deletes it from the list
    public void deletePlayer(String name) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equals(name)) {
                players.remove(player);
                return;
            }
        }
    }

    // Find player from the players list by its name
    public Player getPlayer(String name) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    // Reset every players bets in this room, for example in the end of the round
    public void resetPlayersBets() {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            player.setCurrentBet(0.00);
        }
    }

    // Reset every players folds in this room, for example in the end of the game
    public void resetPlayersFolds() {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            player.setFold(false);
        }
    }


    // Parceable stuff
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
