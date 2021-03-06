// Dialog fragment that pops up when player clicks "buy" button, allowing user to top up own balance.

package com.example.android.holdembets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class BuyDialogFragment extends DialogFragment {
    private Room room;
    private String clientsUsername;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // If activity is recreated (for exaple after rotation)
        if (savedInstanceState != null) {
            clientsUsername = savedInstanceState.getString("clientsUsername");
            room = savedInstanceState.getParcelable("room");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.buyin_picker, null);
        final NumberPicker npBuyin = view.findViewById(R.id.npBuyin);

        // Setting numberpickers min & max values according to what is set as buy in limits inside room
        int min = (int) Math.round(room.getMinBuyIn());
        int max = (int) Math.round(room.getMaxBuyIn());
        npBuyin.setMinValue(min);
        npBuyin.setMaxValue(max);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.buy_more);
        builder.setView(view);
        builder.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SocketSingleton.getInstance().emit("userbuyin", room.getName(), clientsUsername, npBuyin.getValue());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setCancelable(true);
        return builder.create();
    }

    // Sets room and clientsUsername after the dialog fragment is created, so that they are accessible inside onCreateDialog()
    public void setData(Room room, String clientsUsername) {
        this.room = room;
        this.clientsUsername = clientsUsername;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("clientsUsername", clientsUsername);
        outState.putParcelable("room", room);
    }
}
