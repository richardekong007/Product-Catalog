package com.daveace.productcatalog.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daveace.productcatalog.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loadSplashScreen();
    }

    private void loadSplashScreen() {
        final int LOAD_TIME = 3000;
        new Handler().postDelayed(() -> {
            Intent mainActivityIntent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }, LOAD_TIME);
    }
}
