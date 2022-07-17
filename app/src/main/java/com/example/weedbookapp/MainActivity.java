package com.example.weedbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser != null) {
            switchingToPage();

            Toast.makeText(this, "Такой пользователь уже есть", Toast.LENGTH_SHORT);
        }
        else {
            Toast.makeText(this, "Такого пользователя нет", Toast.LENGTH_SHORT);
        }
    }

    private void init() {
        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickSingIn (View view) {
        if (!TextUtils.isEmpty(emailInput.getText().toString()) && !TextUtils.isEmpty(passInput.getText().toString())) {
            mAuth.signInWithEmailAndPassword(emailInput.getText().toString(), passInput.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Вы успешно авторизировались", Toast.LENGTH_SHORT).show();
                        switchingToPage();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Вы ввели неправильный email или пароль", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void onClickSingInAnonymous (View view){
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Вы успешно авторизировались", Toast.LENGTH_SHORT).show();
                    switchingToPage();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Сбой авторизации", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void switchingToPage (){
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
    }

}