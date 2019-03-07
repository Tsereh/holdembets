package com.example.android.holdembets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

public class GamePlayerActivity extends AppCompatActivity {

    private TextView tvUsernameIndicator, tvUsersInRoom;

    private Socket socket;
    private String username, users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_player);

        username = getIntent().getExtras().getString("username");
        tvUsernameIndicator = findViewById(R.id.tvUsernameIndicator);
        tvUsernameIndicator.setText(username);

        users = getIntent().getExtras().getString("usersinroom");
        tvUsersInRoom = findViewById(R.id.tvUsersInRoom);
        tvUsersInRoom.setText(users);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketSingleton.disconnect();
    }
}
