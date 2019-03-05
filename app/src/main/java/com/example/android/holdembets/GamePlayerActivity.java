package com.example.android.holdembets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class GamePlayerActivity extends AppCompatActivity {

    private Socket socket;
    private String username;
    private String roomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_player);

        username = getIntent().getExtras().getString("usernickname");
        roomKey = getIntent().getExtras().getString("roomkey");

        // connect socket client to the server
        try {
            socket = IO.socket("http://10.0.2.2:3000/");

            socket.connect();

            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("roomKey", roomKey);

            socket.emit("joinroom", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
