package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SchemeHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_scheme_homepage);
    }

    public void getApyPage(View view){
        Intent intent = new Intent (getApplicationContext (), Apydata.class);
        startActivity (intent);
    }

    public void getPmjjbyPage(View view){
        Intent intent = new Intent (getApplicationContext (), PmjjbyData.class);
        startActivity (intent);
    }

    public void getPmsbyPage(View view){
        Intent intent = new Intent (getApplicationContext (), PmsbyData.class);
        startActivity (intent);
    }

    public void getRdPage(View view){
        Intent intent = new Intent (getApplicationContext (), RdData.class);
        startActivity (intent);
    }
}
