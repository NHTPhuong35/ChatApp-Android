package com.example.chatapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}