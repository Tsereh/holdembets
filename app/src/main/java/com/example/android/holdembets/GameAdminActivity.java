package com.example.android.holdembets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
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

        if (savedInstanceState == null) {
            SocketSingleton.getInstance().emit("createroom", username);
        } else {
            roomKey = savedInstanceState.getString("roomKey");
            roomKeyDisplay.setText(roomKey);
        }

        SocketSingleton.getInstance().on("createdroom", new Emitter.Listener() {
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

        roomKeyDisplay.setText(roomKey);
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

        outState.putString("roomKey", roomKey);
    }
}
