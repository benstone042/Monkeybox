package com.sujityadav.monkeybox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by sujit yadav on 3/14/2017.
 */

public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.MyViewHolder> {

    private ArrayList<Place> place_list;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.place_name);
            address = (TextView) view.findViewById(R.id.place_address);
            image = (ImageView) view.findViewById(R.id.place_image);

        }
    }


    public NearbyPlaceAdapter(ArrayList<Place> placeList, Context mcontext) {

        this.place_list = placeList;
        this.context=mcontext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

            Place place = place_list.get(position);
            holder.name.setText(place.getName());
            holder.address.setText(place.getAddress());
            Glide.with(context).load(place.getImagelink()).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return place_list.size();
    }
}
