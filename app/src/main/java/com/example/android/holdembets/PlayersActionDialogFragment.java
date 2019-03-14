// Dialog for player to choose action on own turn

package com.example.android.holdembets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class PlayersActionDialogFragment extends DialogFragment {
    private Room room;
    private Player clientPlayer;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // If activity is recreated (for example after rotation)
        if (savedInstanceState != null) {
            clientPlayer = savedInstanceState.getParcelable("clientPlayer");
            room = savedInstanceState.getParcelable("room");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.fragment_player_action, null);
        final NumberPicker npRaise = view.findViewById(R.id.npRaise);
        final NumberPicker npRaiseFraction = view.findViewById(R.id.npRaiseFraction);
        Button btnRaise = view.findViewById(R.id.btnRaise);
        TextView tvCurrentBet = view.findViewById(R.id.tvCurrentBet);

        tvCurrentBet.setText(clientPlayer.getCurrentBet().toString());

        // Setting numberpickers min & max values according to rooms currently biggest bet & users balance
        int min = 0;
        if (room.getCurrentBiggestBet()==0) {
            min =  (int) Math.round(room.getBigBlind());
        } else {
            min = (int) Math.round(room.getCurrentBiggestBet()*2);
        }
        int max = (int) Math.floor(clientPlayer.getBalance());
        npRaise.setMinValue(min);
        npRaise.setMaxValue(max);
        npRaiseFraction.setMinValue(00);
        npRaiseFraction.setMaxValue(99);

        if (clientPlayer.getBalance()<=0) {
            btnRaise.setEnabled(false);
        } else {
            btnRaise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String doubleS = npRaise.getValue() + "." + npRaiseFraction.getValue();
                    Double raiseAmount = Double.parseDouble(doubleS);
                    SocketSingleton.getInstance().emit("playerraised", room.getName(), clientPlayer.getName(), clientPlayer.getSeat(), raiseAmount);
                    dismiss();
                }
            });
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.your_turn);
        builder.setView(view);

        if (room.getCurrentBiggestBet().equals(clientPlayer.getCurrentBet())) {
            // Players current bet is equal to rooms biggest bet, ask player to check or raise
            builder.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Send check response
                    SocketSingleton.getInstance().emit("playerchecked", room.getName(), clientPlayer.getName(), clientPlayer.getSeat());
                }
            });
        } else {
            // Players current bet is less than rooms biggest bet, ask player to fold, call or raise
            Double raiseAmount = room.getCurrentBiggestBet() - clientPlayer.getCurrentBet();
            // Check if user have enough balance to bet
            if (raiseAmount<=clientPlayer.getBalance()) {
                final Double callAmount = room.getCurrentBiggestBet();
                String callStr = getString(R.string.call) + " " + callAmount;
                builder.setPositiveButton(callStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Tell server that user called
                        SocketSingleton.getInstance().emit("playercalled", room.getName(), clientPlayer.getName(), clientPlayer.getSeat());
                    }
                });
            }
            builder.setNegativeButton(R.string.fold, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Tell server that user folded
                    SocketSingleton.getInstance().emit("playerfolded", room.getName(), clientPlayer.getName(), clientPlayer.getSeat());
                }
            });
        }

        builder.setCancelable(false);
        return builder.create();
    }

    // Sets room and clientPlayer after the dialog fragment is created, so that they are accessible inside onCreateDialog()
    public void setData(Room room, Player clientPlayer) {
        this.room = room;
        this.clientPlayer = clientPlayer;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("clientPlayer", clientPlayer);
        outState.putParcelable("room", room);
    }
}
