package com.example.weedbookapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pageWeed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pageWeed extends Fragment {

    private String nameWeed, nameWeedLat, dateAdded, imageID, latitude, longitude, area, address, culture, coating, chemical, date, efficiency, comment, userUID, id;
    private TextView tvWeedName, tvWeedNameLat, tvLatitudeResult, tvLongitudeResult, tvAreaResult, tvAddressResult, tvCultureResult, tvCoatingResult, tvChemicalResult, tvDateResult, tvEfficiencyResult, tvCommentResult;
    private ImageView ivView;
    private ImageButton updateButton;
    private int numCulture, numCoating, numChemical, numEfficiency;
    private FirebaseAuth auth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public pageWeed() {
        // Required empty public constructor
    }

    public pageWeed(String nameWeed, String nameWeedLat, String dateAdded, String imageID, String latitude, String longitude, String area, String address, String culture, int numCulture, String coating, int numCoating, String chemical, int numChemical,
                    String date, String efficiency, int numEfficiency, String comment, String userUID, String id) {

        this.nameWeed = nameWeed;
        this.nameWeedLat = nameWeedLat;
        this.dateAdded = dateAdded;
        this.imageID = imageID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.address = address;
        this.culture = culture;
        this.numCulture = numCulture;
        this.coating = coating;
        this.numCoating = numCoating;
        this.chemical = chemical;
        this.numChemical = numChemical;
        this.dateAdded = dateAdded;
        this.date = date;
        this.efficiency = efficiency;
        this.numEfficiency = numEfficiency;
        this.comment = comment;
        this.userUID = userUID;
        this.id = id;

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pageWeed.
     */
    // TODO: Rename and change types and number of parameters
    public static pageWeed newInstance(String param1, String param2) {
        pageWeed fragment = new pageWeed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_page_weed, container, false);
        tvWeedName = root.findViewById(R.id.tvWeedName);
        tvWeedNameLat = root.findViewById(R.id.tvWeedNameLat);
        tvLatitudeResult = root.findViewById(R.id.tvLatitudeResult);
        tvLongitudeResult = root.findViewById(R.id.tvLongitudeResult);
        tvAreaResult = root.findViewById(R.id.tvAreaResult);
        tvAddressResult = root.findViewById(R.id.tvAddressResult);
        tvCultureResult = root.findViewById(R.id.tvCultureResult);
        tvCoatingResult = root.findViewById(R.id.tvCoatingResult);
        tvChemicalResult = root.findViewById(R.id.tvChemicalResult);
        tvDateResult = root.findViewById(R.id.tvDateResult);
        tvEfficiencyResult = root.findViewById(R.id.tvEfficiencyResult);
        tvCommentResult = root.findViewById(R.id.tvCommentResult);
        ivView = root.findViewById(R.id.ivWeed);
        updateButton = root.findViewById(R.id.updateButton);
        auth = FirebaseAuth.getInstance();

        tvWeedName.setText(nameWeed);
        tvWeedNameLat.setText(nameWeedLat);
        tvLatitudeResult.setText(latitude);
        tvLongitudeResult.setText(longitude);
        tvAreaResult.setText(area);
        tvAddressResult.setText(address);
        tvCultureResult.setText(culture);
        tvCoatingResult.setText(coating);
        tvChemicalResult.setText(chemical);
        tvDateResult.setText(date);
        tvEfficiencyResult.setText(efficiency);
        tvCommentResult.setText(comment);
        Picasso.get().load(imageID).into(ivView);
        Picasso.get().load(imageID).into(ivView);
        if (!TextUtils.isEmpty(userUID)) {
            if (userUID.equals(auth.getUid().toString())) {
                updateButton.setVisibility(View.VISIBLE);
            } else {
                updateButton.setVisibility(View.GONE);
            }
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity)view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new UpdatePageFragment(nameWeed, nameWeedLat, dateAdded, imageID, latitude, longitude, area, address, culture, numCulture, coating, numCoating,
                        chemical, numChemical, date, efficiency, numEfficiency, comment, userUID, id)).addToBackStack(null).commit();
            }
        });

        return root;

    }

}