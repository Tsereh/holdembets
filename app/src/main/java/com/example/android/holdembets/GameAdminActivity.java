package com.example.android.holdembets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameAdminActivity extends AppCompatActivity {

    private String username;
    private String roomKey;
    private Room room;

    private TextView roomKeyDisplay;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_admin);

        roomKeyDisplay = findViewById(R.id.tvRoomKey);
        mListView = findViewById(R.id.listView);

        username = getIntent().getExtras().getString("usernickname");

        if (savedInstanceState == null) {
            SocketSingleton.getInstance().emit("createroom", username);
        } else {
            room = savedInstanceState.getParcelable("room");
            roomKeyDisplay.setText(room.getName());
            PlayerListAdapter adapter = new PlayerListAdapter(this, R.layout.player_adapter_view_layout, room.getPlayers());
            mListView.setAdapter(adapter);
        }

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

                        roomKeyDisplay.setText(room.getName());
                        PlayerListAdapter adapter = new PlayerListAdapter(getApplicationContext(), R.layout.player_adapter_view_layout, room.getPlayers());
                        mListView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            SocketSingleton.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("room", room);
    }
}
