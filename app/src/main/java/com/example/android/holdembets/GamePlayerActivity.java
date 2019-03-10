package com.example.android.holdembets;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GamePlayerActivity extends AppCompatActivity {
    private Socket socket;
    private Room room;
    private String username;

    private TextView roomKeyDisplay;
    private ListView mListView;
    private ConstraintLayout clGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_player);

        roomKeyDisplay = findViewById(R.id.tvRoomKey);
        mListView = findViewById(R.id.listView);
        clGame = findViewById(R.id.clGame);

        if (savedInstanceState != null) {
            username = savedInstanceState.getString("usernickname");
            room = savedInstanceState.getParcelable("room");
            roomKeyDisplay.setText(room.getName());
            PlayerListAdapter adapter = new PlayerListAdapter(this, R.layout.player_adapter_view_layout, room.getPlayers(), username, GamePlayerActivity.this, clGame, room);
            mListView.setAdapter(adapter);
        } else {
            username = getIntent().getExtras().getString("usernickname");
        }

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

        SocketSingleton.getInstance().on("userrefilled", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update users balance in list that refilled balance
                        Player updatedPlayer = room.updatePlayerBalance((String)args[0], ((Integer)args[1]).doubleValue());
                        View v = mListView.getChildAt((Integer) args[2]);
                        TextView tvBalance = v.findViewById(R.id.tvLvBalance);
                        tvBalance.setText(updatedPlayer.getBalance().toString());
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketSingleton.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("usernickname", username);
        outState.putParcelable("room", room);
    }
}
