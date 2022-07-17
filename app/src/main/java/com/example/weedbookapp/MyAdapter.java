package com.example.weedbookapp;

import android.content.Context;
import android.text.TextUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<WeedData> list;
    Date dateAdded;
    SimpleDateFormat sdf;

    public MyAdapter(Context context, ArrayList<WeedData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            WeedData weed = list.get(position);
            holder.nameWeed.setText(weed.getNameWeed());
            holder.nameWeedLat.setText(weed.getNameWeedLat());
            holder.dateAdded.setText(new SimpleDateFormat("dd-MM-yyyy").format(weed.getDateAdded()));
            Picasso.get().load(weed.getImageID()).into(holder.globalImageWeed);

        if(TextUtils.isEmpty(weed.getUserEmail())){
            holder.typeUser.setText("Гость");
            holder.userImage.setImageResource(R.drawable.ic_person_red);
        }else{
            holder.typeUser.setText("Пользователь");
            holder.userImage.setImageResource(R.drawable.ic_person_green);
        }

        holder.relativeLayoutGlobal.setOnClickListener(new View.OnClickListener() {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView globalImageWeed, userImage;
        TextView nameWeed, nameWeedLat, dateAdded, typeUser;
        RelativeLayout relativeLayoutGlobal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameWeed = itemView.findViewById(R.id.nameWeed);
            nameWeedLat = itemView.findViewById(R.id.nameWeedLat);
            dateAdded = itemView.findViewById(R.id.dateAdded);
            globalImageWeed = itemView.findViewById(R.id.globalImageWeed);
            userImage = itemView.findViewById(R.id.userImage);
            typeUser = itemView.findViewById(R.id.typeUser);
            relativeLayoutGlobal = itemView.findViewById(R.id.relativeLayoutGlobal);

        }
    }

    public void filteredList(ArrayList<WeedData> filterList) {

        list = filterList;
        notifyDataSetChanged();

    }

}
