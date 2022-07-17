package com.example.weedbookapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mapWeed;
    private DatabaseReference dataBaseWeed;
    private WeedData weed;
    private SupportMapFragment mapFragment;
    private HashMap<String, WeedData> weedMarker = new HashMap<String, WeedData>();
    private SearchView svWeed;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //private double latitude, longitude;
    LatLng currentLocation = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        dataBaseWeed = FirebaseDatabase.getInstance().getReference("Weed");
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapWeed);
        svWeed = (SearchView) root.findViewById(R.id.svWeed);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapFragment.getMapAsync(this);

        /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }*/

        svWeed.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = svWeed.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng searchLatLang = new LatLng(address.getLatitude(), address.getLongitude());
                    mapWeed.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLatLang, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapWeed = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );

                            currentLocation = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                            mapWeed.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 9));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        dataBaseWeed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    weed = dataSnapshot.getValue(WeedData.class);
                    LatLng start = new LatLng(Double.parseDouble(weed.getLatitude()), Double.parseDouble(weed.getLongitude()));
                    if (!TextUtils.isEmpty(weed.getUserEmail())) {
                        weedMarker.put(mapWeed.addMarker(new MarkerOptions().position(start).title(weed.getNameWeed()).snippet("Адрес:" + weed.getAddress()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).getId(), weed);
                    } else {
                        weedMarker.put(mapWeed.addMarker(new MarkerOptions().position(start).title(weed.getNameWeed()).snippet("Адрес:" + weed.getAddress()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).getId(), weed);
                    }

                    mapWeed.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Nullable
                        @Override
                        public View getInfoContents(@NonNull Marker marker) {
                            weed = weedMarker.get(marker.getId());
                            View view = getLayoutInflater().inflate(R.layout.custom_info, null);
                            TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                            TextView tvSubTitle = (TextView) view.findViewById(R.id.tvSnippet);
                            ImageView ivWeedInfo = (ImageView) view.findViewById(R.id.ivWeedInfo);

                            tvTitle.setText(marker.getTitle());
                            tvSubTitle.setText(marker.getSnippet());
                            Picasso.get().load(weed.getImageID()).into(ivWeedInfo, new MarkerCallback(marker));

                            return view;
                        }

                        @Nullable
                        @Override
                        public View getInfoWindow(@NonNull Marker marker) {
                            return null;
                        }

                        class MarkerCallback implements Callback {
                            Marker marker = null;

                            MarkerCallback(Marker marker) {
                                this.marker = marker;
                            }

                            @Override
                            public void onSuccess() {
                                if (marker == null) {
                                    return;
                                }

                                if (!marker.isInfoWindowShown()) {
                                    return;
                                }

                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mapWeed.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                weed = weedMarker.get(marker.getId());
                replaceFragment(new pageWeed(weed.getNameWeed(), weed.getNameWeedLat(), new SimpleDateFormat("dd-MM-yyyy").format(weed.getDateAdded()), weed.getImageID(), weed.getLatitude(), weed.getLongitude(), weed.getArea(), weed.getAddress(), weed.getCulture(), weed.getNumCulture(),
                        weed.getCoating(), weed.getNumCoating(), weed.getChemical(), weed.getNumChemical(), weed.getDate(), weed.getEfficiency(), weed.getNumEfficiency(), weed.getComment(), weed.getUserUID(), weed.getId()));
            }
        });

        mapWeed.getUiSettings().setZoomControlsEnabled(true);
        mapWeed.getUiSettings().setZoomGesturesEnabled(true);

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment);
        fragmentTransaction.commit();
    }

}