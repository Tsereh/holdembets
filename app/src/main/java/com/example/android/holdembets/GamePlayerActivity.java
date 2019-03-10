package com.example.android.holdembets;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GamePlayerActivity extends AppCompatActivity {
    private Socket socket;
    private Room room;
    private String username;

    private ListView mListView;
    private PlayerListAdapter adapter;
    private ConstraintLayout clGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_player);

        mListView = findViewById(R.id.listView);
        clGame = findViewById(R.id.clGame);

        if (savedInstanceState != null) {
            username = savedInstanceState.getString("usernickname");
            room = savedInstanceState.getParcelable("room");
            getSupportActionBar().setTitle("Room key: " + room.getName());
            adapter = new PlayerListAdapter(this, R.layout.player_adapter_view_layout, room.getPlayers(), username, GamePlayerActivity.this, clGame, room);
            mListView.setAdapter(adapter);
        } else {
            username = getIntent().getExtras().getString("usernickname");

            JSONObject roomObject = null;
            try {
                roomObject = new JSONObject(getIntent().getExtras().getString("roomdata"));

                String rName = roomObject.getString("name");
                Double rMinBuyIn = roomObject.getDouble("minBuyIn");
                Double rMaxBuyIn = roomObject.getDouble("maxBuyIn");
                Double rSmallBlind = roomObject.getDouble("smallBlind");
                Double rBigBlind = roomObject.getDouble("bigBlind");
                room = new Room(rName, rMinBuyIn, rMaxBuyIn, rSmallBlind, rBigBlind);

                JSONObject users = roomObject.getJSONObject("users");
                JSONArray userKeys = users.names();
                for (int i = 0; i < userKeys.length(); i++) {
                    String key = userKeys.getString(i);
                    JSONObject userObject = users.getJSONObject(key);

                    String uName = userObject.getString("name");
                    Double uBalance = userObject.getDouble("balance");
                    boolean uAdmin = userObject.getBoolean("admin");

                    Player player = new Player(uName, uBalance, uAdmin);
                    room.addUser(player);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getSupportActionBar().setTitle("Room key: " + room.getName());
            adapter = new PlayerListAdapter(getApplicationContext(), R.layout.player_adapter_view_layout, room.getPlayers(), username, GamePlayerActivity.this, clGame, room);
            mListView.setAdapter(adapter);
        }

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
                            boolean uAdmin = userObject.getBoolean("admin");

                            Player nPlayer = new Player(uName, uBalance, uAdmin);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketSingleton.disconnectUser(room.getName(), username);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("usernickname", username);
        outState.putParcelable("room", room);
    }
}
