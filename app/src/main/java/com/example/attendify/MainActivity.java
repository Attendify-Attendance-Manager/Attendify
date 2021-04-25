package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
// THIS IS SPLASH ACTIVITY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Toast.makeText(getApplicationContext(), "By : $udhanshu $hridhar", Toast.LENGTH_LONG).show();
        new CountDownTimer(2000,2000){
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }.start();

    }

}