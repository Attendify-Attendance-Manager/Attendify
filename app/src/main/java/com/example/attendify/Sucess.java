package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Sucess extends AppCompatActivity {

    TextView textView;
    String rollNo;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucess);
        getSupportActionBar().hide();
        textView = findViewById(R.id.textView);
        textView.setText("Your attendance is marked");

    }
}