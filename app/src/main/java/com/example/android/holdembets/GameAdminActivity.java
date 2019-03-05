package com.example.android.holdembets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class GameAdminActivity extends AppCompatActivity {

    private Socket socket;
    private String username;
    private String roomKey;

    private TextView roomKeyDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_admin);

        roomKeyDisplay = findViewById(R.id.tvRoomKey);

        username = getIntent().getExtras().getString("usernickname");

        // connect socket client to the server
        try {
            socket = IO.socket("http://10.0.2.2:3000/");

            socket.connect();

            socket.emit("createroom", username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket.on("createdroom", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomKey = (String) args[0];

                        roomKeyDisplay.setText(roomKey);
                    }
                });
            }
        });
    }
}
