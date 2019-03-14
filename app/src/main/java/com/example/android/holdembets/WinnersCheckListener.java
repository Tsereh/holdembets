// Custom onClickListener for WinnerPickerDialogFragments positiveBtn, to validate that admin picked at least one winner

package com.example.android.holdembets;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

public class WinnersCheckListener implements View.OnClickListener {
    private final Dialog dialog;
    private ArrayList<String> winners;
    private Context context;
    private Room room;
    public WinnersCheckListener(Dialog dialog, ArrayList<String> winners, Context context, Room room) {
        this.dialog = dialog;
        this.winners = winners;
        this.context = context;
        this.room = room;
    }

    @Override
    public void onClick(View v) {
        if (winners.size()>0) {
            JSONArray winnersJSON = new JSONArray(winners);
            SocketSingleton.getInstance().emit("winnerspicked", room.getName(), winnersJSON);
            dialog.dismiss();
        } else {
            Toast.makeText(context, R.string.select_at_least_one_winner, Toast.LENGTH_SHORT).show();
        }
    }
}
