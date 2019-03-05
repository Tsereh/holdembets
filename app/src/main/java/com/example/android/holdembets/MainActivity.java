 package com.example.android.holdembets;

 import android.content.Intent;
 import android.os.Bundle;
 import android.support.v7.app.AppCompatActivity;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.Toast;

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
                    Intent i = new Intent(MainActivity.this, GameAdminActivity.class);
                    i.putExtra("usernickname", usernameField.getText().toString());
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, R.string.empty_name_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usernameField.getText().toString().isEmpty() && !roomKeyField.getText().toString().isEmpty()) {
                    Intent i = new Intent(MainActivity.this, GamePlayerActivity.class);
                    i.putExtra("usernickname", usernameField.getText().toString());
                    i.putExtra("roomkey", roomKeyField.getText().toString());
                    startActivity(i);
                } else if (usernameField.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.empty_name_toast, Toast.LENGTH_SHORT).show();
                } else if (roomKeyField.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.empty_roomkey_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
