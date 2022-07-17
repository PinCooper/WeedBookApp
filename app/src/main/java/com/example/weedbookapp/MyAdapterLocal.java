package com.example.weedbookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyAdapterLocal extends RecyclerView.Adapter<MyAdapterLocal.MyViewHolderLocal> {

    Context context;
    ArrayList<WeedData> list;
    Date dateAddedLocal;
    SimpleDateFormat sdf;
    private FirebaseAuth auth;

    public MyAdapterLocal(Context context, ArrayList<WeedData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolderLocal onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_local, parent, false);
        auth = FirebaseAuth.getInstance();
        return new MyViewHolderLocal(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderLocal holder, int position) {

        WeedData weed = list.get(position);

        holder.nameWeedLocal.setText(weed.getNameWeed());
        holder.nameWeedLatLocal.setText(weed.getNameWeedLat());
        holder.dateAddedLocal.setText(new SimpleDateFormat("dd-MM-yyyy").format(weed.getDateAdded()));
        Picasso.get().load(weed.getImageID()).into(holder.localImageWeed);

        holder.relativeLayoutLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity)view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new pageWeed(weed.getNameWeed(), weed.getNameWeedLat(), new SimpleDateFormat("dd-MM-yyyy").format(weed.getDateAdded()), weed.getImageID(), weed.getLatitude(), weed.getLongitude(), weed.getArea(), weed.getAddress(), weed.getCulture(),  weed.getNumCulture(),
                        weed.getCoating(), weed.getNumCoating(), weed.getChemical(), weed.getNumChemical(), weed.getDate(), weed.getEfficiency(), weed.getNumEfficiency(), weed.getComment(), weed.getUserUID(), weed.getId())).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolderLocal extends  RecyclerView.ViewHolder {

        ImageView localImageWeed;
        TextView nameWeedLocal, nameWeedLatLocal, dateAddedLocal;
        RelativeLayout relativeLayoutLocal;


        public MyViewHolderLocal(@NonNull View itemView) {
            super(itemView);

            nameWeedLocal = itemView.findViewById(R.id.nameWeedLocal);
            nameWeedLatLocal = itemView.findViewById(R.id.nameWeedLatLocal);
            dateAddedLocal = itemView.findViewById(R.id.dateAddedLocal);
            localImageWeed = itemView.findViewById(R.id.localImageWeed);
            relativeLayoutLocal = itemView.findViewById(R.id.relativeLayoutLocal);
        }
    }

    public void filteredList(ArrayList<WeedData> filterList) {

        list = filterList;
        notifyDataSetChanged();

    }

}
