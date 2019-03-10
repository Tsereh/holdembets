package com.example.android.holdembets;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class BuyActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView sliderMinLabel, sliderMaxLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        seekBar = findViewById(R.id.seekbar);
        sliderMinLabel = findViewById(R.id.sliderMinLabel);
        sliderMaxLabel = findViewById(R.id.sliderMaxLabel);

        sliderMaxLabel.setText(seekBar.getMax());
        if (Build.VERSION.SDK_INT>=26) {
            sliderMinLabel.setText(seekBar.getMin());
        }
    }
}
