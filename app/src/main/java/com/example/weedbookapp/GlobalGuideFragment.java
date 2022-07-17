package com.example.weedbookapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.core.util.TimeUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GlobalGuideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GlobalGuideFragment extends Fragment {

    private RecyclerView globalRecyclerView;
    private DatabaseReference dataBaseWeed;
    private MyAdapter myAdapter;
    private ArrayList<WeedData> list;
    private EditText searchInput;
    private LinearLayout buttonLayout;
    private ImageButton dateButton, areaButton;
    private MaterialDatePicker materialDatePicker;
    private Date dateRangeStart, dateRangeEnd;
    private SimpleDateFormat sdf;
    private int[] state;
    private WeedData weed;
    private ArrayList<String> adminAreaList;
    private ArrayAdapter<String> adminAreaAdapter;
    private String[] adminArea = {"Все", "Алтайский край", "Амурская область", "Архангельская область", "Астраханская область", "Белгородская область", "Брянская область", "Владимирская область", "Волгоградская область", "Вологодская область", "Воронежская область", "Иркутская область", "Калининградская область",
            "Калужская область", "Камчатский край", "Кемеровская область", "Кировская область", "Костромская область", "Краснодарский край", "Красноярский край", "Курганская область", "Курская область", "Ленинградская область", "Липецкая область", "Магаданская область",
            "Московская область", "Мурманская область", "Нижегородская область", "Новгородская область", "Новосибирская область", "Омская область", "Оренбургская область", "Орловская область", "Пензенская область", "Псковская область", "Республика Адыгея", "Республика Алтай",
            "Республика Бурятия", "Республика Дагестан", "Республика Крым", "Республика Северная Осетия-Алания", "Ростовская область", "Рязанская область", "Тамбовская область", "Тульская область", "Челябинская область", "Чеченская Республика"};
    private String adminAreaChoice;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GlobalGuideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GlobalGuideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GlobalGuideFragment newInstance(String param1, String param2) {
        GlobalGuideFragment fragment = new GlobalGuideFragment();
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
        View root = inflater.inflate(R.layout.fragment_global_guide, container, false);
        globalRecyclerView = (RecyclerView) root.findViewById(R.id.globalRecyclerView);
        dateButton = (ImageButton) root.findViewById(R.id.dateButton);
        areaButton = (ImageButton) root.findViewById(R.id.areaButton);
        dataBaseWeed = FirebaseDatabase.getInstance().getReference("Weed");
        globalRecyclerView.setHasFixedSize(true);
        globalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchInput = (EditText) root.findViewById(R.id.searchInput);
        buttonLayout = (LinearLayout) root.findViewById(R.id.buttonLayout);
        sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        state = new int[1];
        list = new ArrayList<>();
        materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())).build();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getFragmentManager(), "Tag_picker");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                            Date dateRangeStart = new Date(selection.first);
                            Date dateRangeEnd = new Date(selection.second + 86400000);
                            Query query = dataBaseWeed.orderByChild("dateAdded").startAt(dateRangeStart.getTime()).endAt(dateRangeEnd.getTime());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    showListener(snapshot);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    }
                });
            }
        });

        areaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminAdreaDialog();
            }
        });

        dataWeedShow();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    filter(s.toString());
                }
            }
        });

        globalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                state[0] = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy>0 && (state[0] == 0 || state[0] == 2)){
                    hideSearch();
                }else if(dy<-10){
                    showSearch();
                }
            }
        });

        return root;

    }

    private void hideSearch(){
        searchInput.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
    }

    private void showSearch(){
        searchInput.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);
    }

    private void filter (String text) {
        ArrayList<WeedData> filterList = new ArrayList<>();

        for(WeedData item: list){
            if(item.getNameWeed().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }

        myAdapter.filteredList(filterList);

    }

    private void showListener(DataSnapshot snapshot) {
        list.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
            weed = dataSnapshot.getValue(WeedData.class);
            list.add(weed);
        }

        myAdapter.notifyDataSetChanged();
        myAdapter = new MyAdapter(getActivity(), list);
        globalRecyclerView.setAdapter(myAdapter);
    }

    private void adminAdreaDialog (){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        RelativeLayout dialogLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.admin_area_dialog, null);
        Spinner adminAreaSpinner = dialogLayout.findViewById(R.id.spinnerAdminArea);
        adminAreaList = new ArrayList<>(Arrays.asList(adminArea));
        adminAreaAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, adminAreaList);
        adminAreaSpinner.setAdapter(adminAreaAdapter);
        builder.setView(dialogLayout);
        builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adminAreaChoice = adminAreaSpinner.getSelectedItem().toString();
                if (adminAreaChoice != "Все") {
                    Query query = dataBaseWeed.orderByChild("area").equalTo(adminAreaChoice);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            showListener(snapshot);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    list.clear();
                    dataWeedShow();
                }
            }
        });
        builder.show();
    }

    private void dataWeedShow(){
        dataBaseWeed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAdapter = new MyAdapter(getActivity(), list);
                globalRecyclerView.setAdapter(myAdapter);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    weed = dataSnapshot.getValue(WeedData.class);
                    list.add(weed);

                }

                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}