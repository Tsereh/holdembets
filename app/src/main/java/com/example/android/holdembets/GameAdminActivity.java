// Game admins activity, same as players activity, difference is in launching activity and getting initial data. Admin should have additional functionality compared to regular players.

package com.example.android.holdembets;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameAdminActivity extends AppCompatActivity {

    private String username;
    private Room room;
    private ConstraintLayout clGame;

    private ListView mListView;
    private PlayerListAdapter adapter;
    private Button btnDeal;
    private TextView tvGameStatus, tvPot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_admin);

        mListView = findViewById(R.id.listView);
        clGame = findViewById(R.id.clGame);
        btnDeal = findViewById(R.id.btnDeal);
        tvGameStatus = findViewById(R.id.tvGameStatus);
        tvPot = findViewById(R.id.tvPot);

        // Checks if activity is created for the first time, or recreated (for example after rotation), and assigns values accordingly.
        if (savedInstanceState == null) {
            // Activity created for the first time, get data from intents extras & call server to create new room
            username = getIntent().getExtras().getString("usernickname");
            SocketSingleton.getInstance().emit("createroom", username);
        } else {
            // Activity recreated, get data from savedInstanceState
            username = savedInstanceState.getString("usernickname");
            room = savedInstanceState.getParcelable("room");
            getSupportActionBar().setTitle("Room key: " + room.getName());
            adapter = new PlayerListAdapter(this, R.layout.player_adapter_view_layout, room.getPlayers(), username, GameAdminActivity.this, clGame, room);
            mListView.setAdapter(adapter);
        }

        // Admin starts the game
        btnDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketSingleton.getInstance().emit("startgame", room.getName());
                btnDeal.setVisibility(View.INVISIBLE);
            }
        });

        // Get newly created rooms data from the server
        SocketSingleton.getInstance().on("createdroom", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject roomObject = (JSONObject) args[0];

                            String rName = roomObject.getString("name");
                            Double rMinBuyIn = roomObject.getDouble("minBuyIn");
                            Double rMaxBuyIn = roomObject.getDouble("maxBuyIn");
                            Double rSmallBlind = roomObject.getDouble("smallBlind");
                            Double rBigBlind = roomObject.getDouble("bigBlind");
                            Double rCurrentBiggestBet = roomObject.getDouble("currentBiggestBet");
                            Double rPot = roomObject.getDouble("pot");
                            int rStage = roomObject.getInt("stage");
                            room = new Room(rName, rMinBuyIn, rMaxBuyIn, rSmallBlind, rBigBlind, rCurrentBiggestBet, rPot, rStage, getApplicationContext());
                            tvGameStatus.setText(room.getReadableStage());
                            tvPot.setText(room.getPot().toString());

                            JSONObject users = roomObject.getJSONObject("users");
                            JSONArray userKeys = users.names();;
                            // Add every player/user got from server to local room
                            for (int i = 0; i < userKeys.length(); i++) {
                                String key = userKeys.getString(i);
                                JSONObject userObject = users.getJSONObject(key);

                                String uName = userObject.getString("name");
                                Double uBalance = userObject.getDouble("balance");
                                Double uCurrentBet = userObject.getDouble("currentBet");
                                boolean uAdmin = userObject.getBoolean("admin");
                                int uSeat = userObject.getInt("seat");
                                boolean uFold = userObject.getBoolean("fold");

                                Player player = new Player(uName, uBalance, uCurrentBet, uAdmin, uSeat, uFold);
                                room.addUser(player);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getSupportActionBar().setTitle("Room key: " + room.getName());
                        // Build a ListView for players in room
                        adapter = new PlayerListAdapter(getApplicationContext(), R.layout.player_adapter_view_layout, room.getPlayers(), username, GameAdminActivity.this, clGame, room);
                        mListView.setAdapter(adapter);
                    }
                });
            }
        });

        // Gets from the server gamestarted info, contains info about new SB, BB, D, and dealers seat
        SocketSingleton.getInstance().on("gamestarted", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray gotNewSeats = (JSONArray)args[0];
                        int gotDealersSeat = (int)args[1];

                        room.setSeats(gotNewSeats, gotDealersSeat);
                    }
                });
            }
        });

        // Get notification from the server about new new player getting the turn
        SocketSingleton.getInstance().on("turngiven", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gotName = (String)args[0];
                        Double gotBiggestBet = ((Integer)args[1]).doubleValue();
                        int gotStage = (int)args[2];
                        room.setStage(gotStage);
                        tvGameStatus.setText(room.getReadableStage());
                        room.setTurn(gotName, true);
                        room.setCurrentBiggestBet(gotBiggestBet);
                        mListView.invalidateViews();
                        if (username.equals(gotName)) {
                            // This clients turn, ask what action user wants to take
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                            if (prev!=null) {
                                ft.remove(prev);
                            }
                            ft.addToBackStack(null);

                            PlayersActionDialogFragment actionDialog = new PlayersActionDialogFragment();
                            actionDialog.setData(room, room.getPlayer(username));
                            actionDialog.show(ft, "dialog");
                        }
                    }
                });
            }
        });

        // Got information from the server that round ended. Update pot from collected bets, reset players bets
        SocketSingleton.getInstance().on("roundended", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Double gotPot = ((Integer)args[0]).doubleValue();
                        room.setPot(gotPot);
                        tvPot.setText(room.getPot().toString());
                        room.resetPlayersBets();
                        mListView.invalidateViews();
                    }
                });
            }
        });

        // Got information from the server that player folded
        SocketSingleton.getInstance().on("playerfolded", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gotName = (String)args[0];
                        room.setTurn(gotName, false);
                        room.setFold(gotName, true);
                        String toastText = gotName + " " + getString(R.string.folded);
                        Toast.makeText(GameAdminActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Got information from the server that player checked
        SocketSingleton.getInstance().on("playerchecked", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gotName = (String)args[0];
                        room.setTurn(gotName, false);
                        String toastText = gotName + " " + getString(R.string.checked);
                        Toast.makeText(GameAdminActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Got information from the server that player called
        SocketSingleton.getInstance().on("playercalled", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gotName = (String)args[0];
                        room.playerCalled(gotName);
                        room.setTurn(gotName, false);
                        String toastText = gotName + " " + getString(R.string.called);
                        Toast.makeText(GameAdminActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Got information from the server that player raised
        SocketSingleton.getInstance().on("playerraised", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gotName = (String)args[0];
                        Double gotRaisedBet = ((Integer)args[1]).doubleValue();
                        room.playerRaised(gotName, gotRaisedBet);
                        room.setTurn(gotName, false);
                        String toastText = gotName + " " + getString(R.string.raised) + " to " + gotRaisedBet.toString();
                        Toast.makeText(GameAdminActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        mListView.invalidateViews();
                    }
                });
            }
        });

        // Got information from the server who won the game, update balance for those players who won
        SocketSingleton.getInstance().on("winnersannounced", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Double prize = ((Integer)args[1]).doubleValue();
                        String toast = "";
                        JSONArray data = (JSONArray)args[0];
                        for (int i = 0; i < data.length(); i++) {
                            try {
                                JSONObject player = data.getJSONObject(i);
                                room.updatePlayerBalance(player.getString("name"), player.getDouble("balance"));
                                if (i!=(data.length()-1)) {
                                    toast = toast + player.getString("name") + ", ";
                                } else {
                                    toast = toast + player.getString("name") + " ";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        toast = toast + getString(R.string.won) + " " + prize.toString();


                        room.resetPlayersFolds();
                        room.setPot(0.00);
                        tvPot.setText(room.getPot().toString());
                        Toast.makeText(GameAdminActivity.this, toast, Toast.LENGTH_LONG).show();
                        mListView.invalidateViews();
                        btnDeal.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        // Server notified admin about endend game, admin should pick the winner
        SocketSingleton.getInstance().on("pickwinner", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // show dialogfragment to pick the winner
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                        if (prev!=null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);

                        WinnerPickerDialogFragment winnerPickerDialog = new WinnerPickerDialogFragment();
                        winnerPickerDialog.setData(room);
                        winnerPickerDialog.show(ft, "dialog");
                    }
                });
            }
        });

        // Get information from server if some of the players top ups its balance, and update it locally
        SocketSingleton.getInstance().on("userrefilled", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update users balance in list that refilled balance
                        room.updatePlayerBalance((String)args[0], ((Integer)args[1]).doubleValue());
                        mListView.invalidateViews();
                    }
                });
            }
        });

        // Get information from the server if some of the players leaves the room, and delete it locally
        SocketSingleton.getInstance().on("userdisconnected", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        room.deletePlayer((String)args[0]);
                        mListView.invalidateViews();
                    }
                });
            }
        });

        // Get information from the server if new user joins the room, and add it locally
        SocketSingleton.getInstance().on("userjoined", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject userObject = null;
                        try {
                            userObject = (JSONObject)args[0];
                            String uName = userObject.getString("name");
                            Double uBalance = userObject.getDouble("balance");
                            Double uCurrentBet = userObject.getDouble("currentBet");
                            boolean uAdmin = userObject.getBoolean("admin");
                            int uSeat = userObject.getInt("seat");
                            boolean uFold = userObject.getBoolean("fold");

                            Player nPlayer = new Player(uName, uBalance, uCurrentBet, uAdmin, uSeat, uFold);
                            room.addUser(nPlayer);
                            mListView.invalidateViews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    // Disconnect socket if activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            SocketSingleton.disconnectUser(room.getName(), username);
            username = null;
            room = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("usernickname", username);
        outState.putParcelable("room", room);
    }
}
