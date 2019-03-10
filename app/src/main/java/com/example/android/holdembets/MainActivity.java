 package com.example.android.holdembets;

 import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

 public class MainActivity extends AppCompatActivity {

    private Button joinBtn, createBtn;
    private EditText usernameField, roomKeyField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinBtn = findViewById(R.id.btnJoinRoom);
        createBtn = findViewById(R.id.btnCreateRoom);
        usernameField = findViewById(R.id.etUsername);
        roomKeyField = findViewById(R.id.etRoomKey);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usernameField.getText().toString().isEmpty()) {
                    createBtn.setEnabled(false);
                    joinBtn.setEnabled(false);
                    Intent i = new Intent(MainActivity.this, GameAdminActivity.class);
                    i.putExtra("usernickname", usernameField.getText().toString());
                    startActivity(i);
                    createBtn.setEnabled(true);
                    joinBtn.setEnabled(true);
                } else {
                    Toast.makeText(MainActivity.this, R.string.empty_name_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usernameField.getText().toString().isEmpty() && !roomKeyField.getText().toString().isEmpty()) {
                    createBtn.setEnabled(false);
                    joinBtn.setEnabled(false);
                    final String username = usernameField.getText().toString();
                    final String roomKey = roomKeyField.getText().toString();

                    try {
                        JSONObject data = new JSONObject();
                        data.put("username", username);
                        data.put("roomKey", roomKey);

                        SocketSingleton.getInstance().emit("joinroom", data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SocketSingleton.getInstance().on("roomdata", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(MainActivity.this, GamePlayerActivity.class);
                                    i.putExtra("roomdata", args[0].toString());
                                    i.putExtra("usernickname", username);
                                    startActivity(i);
                                    createBtn.setEnabled(true);
                                    joinBtn.setEnabled(true);
                                    SocketSingleton.getInstance().off("noroomfound");
                                    SocketSingleton.getInstance().off("roomdata");
                                    return;
                                }
                            });
                        }
                    });

                    SocketSingleton.getInstance().on("noroomfound", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SocketSingleton.disconnect();
                                    String string = getString(R.string.room_not_found_toast) + " " + roomKey;
                                    Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
                                    createBtn.setEnabled(true);
                                    joinBtn.setEnabled(true);
                                    SocketSingleton.getInstance().off("noroomfound");
                                    SocketSingleton.getInstance().off("roomdata");
                                    return;
                                }
                            });
                        }
                    });

                } else if (usernameField.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.empty_name_toast, Toast.LENGTH_SHORT).show();
                } else if (roomKeyField.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.empty_roomkey_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
