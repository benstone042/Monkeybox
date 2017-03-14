package com.sujityadav.monkeybox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("loginDetail", MODE_PRIVATE);
                String restoredText = prefs.getString("name", null);
                if (restoredText == null) {
                    Intent i= new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(i);
                }
                else{
                    Intent i= new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, 2000);
    }
}
