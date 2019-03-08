package com.example.android.holdembets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GamePlayerActivity extends AppCompatActivity {
    private Socket socket;
    private Room room;

    private TextView roomKeyDisplay;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_player);

        roomKeyDisplay = findViewById(R.id.tvRoomKey);
        mListView = findViewById(R.id.listView);

        if (savedInstanceState != null) {
            room = savedInstanceState.getParcelable("room");
            roomKeyDisplay.setText(room.getName());
            PlayerListAdapter adapter = new PlayerListAdapter(this, R.layout.player_adapter_view_layout, room.getPlayers());
            mListView.setAdapter(adapter);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketSingleton.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("room", room);
    }
}
