package com.example.weedbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.weedbookapp.databinding.ActivityGuideBinding;
import com.example.weedbookapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class GuideActivity extends AppCompatActivity {

    ActivityGuideBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new MapFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.mapMenu:
                    replaceFragment(new MapFragment());
                    break;
                case R.id.globalGuideMenu:
                    replaceFragment(new GlobalGuideFragment());
                    break;
                case R.id.addWeedMenu:
                    replaceFragment(new AddWeedFragment());
                    break;
                case R.id.myGuideMenu:
                    replaceFragment(new MyGuideFragment());
                    break;
                case R.id.logoutMenu:
                    showInfoAlert("Вы хотите выйти из учётной записи?");
                    break;
            }

            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SelectItemId", binding.bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectItemId = savedInstanceState.getInt("SelectItemId");
        switchFragment(selectItemId);
    }

    public void onClickSingOut(View view){
        FirebaseAuth.getInstance().signOut();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout,fragment);
        fragmentTransaction.commit();
    }

    private void showInfoAlert(String Text){
        AlertDialog.Builder builder = new AlertDialog.Builder(GuideActivity.this);
        builder.setTitle("Выход")
                .setMessage(Text)
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onClickSingOut();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onClickSingOut (){
        FirebaseAuth.getInstance().signOut();
        switchingToPage();
    }

    public void switchingToPage (){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void switchFragment(int item){
        switch (item){

            case R.id.mapMenu:
                replaceFragment(new MapFragment());
                break;
            case R.id.globalGuideMenu:
                replaceFragment(new GlobalGuideFragment());
                break;
            case R.id.addWeedMenu:
                replaceFragment(new AddWeedFragment());
                break;
            case R.id.myGuideMenu:
                replaceFragment(new MyGuideFragment());
                break;
            case R.id.logoutMenu:
                showInfoAlert("Вы хотите выйти из приложения?");
                break;
        }
    }
}