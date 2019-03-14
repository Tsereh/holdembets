// Dialog for admin to pick the winner

package com.example.android.holdembets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WinnerPickerDialogFragment extends DialogFragment {
    private Room room;
    private ArrayList<String> winners;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // If activity is recreated (for example after rotation)
        if (savedInstanceState != null) {
            room = savedInstanceState.getParcelable("room");
            winners = savedInstanceState.getStringArrayList("winners");
        } else {
            winners = new ArrayList<String>();
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_winner_picker, null);
        LinearLayout rootLayout = view.findViewById(R.id.layoutWinnerPicker);
        // Add each player who did not fold and a checkbox to the fragment, so admin can check all the winners
        for (final Player player : room.getPlayers()) {
            if (!player.isFold()) {
                LinearLayout l = new LinearLayout(getContext());
                l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView tvName = new TextView(getContext());
                tvName.setText(player.getName());
                tvName.setGravity(Gravity.LEFT);
                l.addView(tvName);

                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setGravity(Gravity.RIGHT);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            winners.add(player.getName());
                        } else {
                            winners.remove(player.getName());
                        }
                    }
                });
                l.addView(checkBox);

                rootLayout.addView(l);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_winners);
        builder.setView(view);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Overriden in WinnersCheckListener
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = ((AlertDialog) getDialog());
        Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveBtn.setOnClickListener(new WinnersCheckListener(dialog, winners, getContext(), room));
    }

    public void setData(Room room) {
        this.room = room;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("room", room);
        outState.putStringArrayList("winners", winners);
    }
}
