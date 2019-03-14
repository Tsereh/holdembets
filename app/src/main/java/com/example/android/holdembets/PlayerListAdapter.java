// List adapter for showing players inside game activities.

package com.example.android.holdembets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

class PlayerListAdapter extends ArrayAdapter<Player> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private String clientsUsername;
    private AppCompatActivity activity;
    private PopupWindow mPopupWindow;
    private ConstraintLayout clGame;
    private Room room;

    private static class ViewHolder {
        TextView name;
        TextView balance;
    }

    public PlayerListAdapter(Context context, int resource, ArrayList<Player> objects, String clientsUsername, AppCompatActivity activity, ConstraintLayout clGame, Room room) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.clientsUsername = clientsUsername;
        this.activity = activity;
        this.clGame = clGame;
        this.room = room;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get players information
        String name = getItem(position).getName();
        Double balance = getItem(position).getBalance();
        Double currentBet = getItem(position).getCurrentBet();
        boolean admin = getItem(position).isAdmin();
        int seat = getItem(position).getSeat();
        boolean turn = getItem(position).isTurn();
        boolean fold = getItem(position).isFold();

        // Create player object with the information
        Player player = new Player(name, balance, currentBet, admin, seat, fold);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        final ViewGroup layout = (ViewGroup) convertView.findViewById(R.id.layoutPlayerAdapter);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvLvUsername);
        TextView tvSeat = (TextView) convertView.findViewById(R.id.tvSeat);
        TextView tvFoldBet = (TextView) convertView.findViewById(R.id.tvFoldBet);
        TextView tvBalance = (TextView) convertView.findViewById(R.id.tvLvBalance);

        tvName.setText(name);
        tvBalance.setText(balance.toString());

        // Set background color of player that's turn it is
        if (turn) {
            layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentTransparent));
        }

        // Indicate if player is SB, BB, or D
        if (seat == 0) {
            tvSeat.setText("SB");
        } else if (seat == 1) {
            tvSeat.setText("BB");
        } else if (seat == (room.getPlayers().size()-1)) {
            tvSeat.setText("D");
        }

        if (fold) {
            tvFoldBet.setText("FOLD");
        } else if (currentBet > 0) {
            tvFoldBet.setText(currentBet.toString());
        }

        // If list item belongs to user, add "buy" for topping up the balance
        if (clientsUsername.equals(name)) {
            Button btnBuy = new Button(mContext);
            btnBuy.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btnBuy.setText(R.string.buy);
            layout.addView(btnBuy, 3);

            // Show BuyDialogFragment if "Buy" button is clicked
            btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
                    if (prev!= null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    BuyDialogFragment buyDialog = new BuyDialogFragment();
                    buyDialog.setData(room, clientsUsername);
                    buyDialog.show(ft, "dialog");
                }
            });
        }

        return convertView;
    }
}
