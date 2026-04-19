package com.example.applock;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PinActivity extends AppCompatActivity {

    String targetApp;
    boolean fromLock = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_pin);

        MainActivity.isPinOpen = true;

        EditText pin = findViewById(R.id.pinInput);
        Button btn = findViewById(R.id.unlockBtn);

        targetApp = getIntent().getStringExtra("APP");
        fromLock = getIntent().getBooleanExtra("FROM_LOCK", false);

        SharedPreferences p = getSharedPreferences("AppLockPrefs", MODE_PRIVATE);

        if (!p.contains("pin")) {
            p.edit().putString("pin", "1234").apply();
        }

        btn.setOnClickListener(v -> {

            String enteredPin = pin.getText().toString();
            String savedPin = p.getString("pin", "1234");

            if (enteredPin.equals(savedPin)) {

                MainActivity.isLocked = false;
                MainActivity.isPinOpen = false;
                MainActivity.lockedAppNow = "";

                MainActivity.temporarilyUnlockedApp = targetApp;
                MainActivity.pauseMonitoring = true;

                if (fromLock && targetApp != null) {
                    Intent i = getPackageManager().getLaunchIntentForPackage(targetApp);
                    if (i != null) startActivity(i);
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }

                finish();

            } else {

                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.addCategory(Intent.CATEGORY_HOME);
                    home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                }, 500);

                MainActivity.isLocked = false;
                MainActivity.isPinOpen = false;
                MainActivity.lockedAppNow = "";
                MainActivity.temporarilyUnlockedApp = "";
                MainActivity.pauseMonitoring = false;
            }
        });
    }
}