package com.example.android.holdembets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class PlayerListAdapter extends ArrayAdapter<Player> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView name;
        TextView balance;
    }

    public PlayerListAdapter(Context context, int resource, ArrayList<Player> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get players information
        String name = getItem(position).getName();
        Double balance = getItem(position).getBalance();
        boolean admin = getItem(position).isAdmin();

        // Create player object with the information
        Player player = new Player(name, balance, admin);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.tvLvUsername);
        TextView tvBalance = (TextView) convertView.findViewById(R.id.tvLvBalance);

        tvName.setText(name);
        tvBalance.setText(balance.toString());

        return convertView;
    }
}
