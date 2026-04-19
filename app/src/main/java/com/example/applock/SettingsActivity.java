package com.example.applock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    EditText oldPin, newPin;
    Button changePinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        oldPin = findViewById(R.id.oldPin);
        newPin = findViewById(R.id.newPin);
        changePinBtn = findViewById(R.id.changePinBtn);

        SharedPreferences prefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE);

        changePinBtn.setOnClickListener(v -> {

            String old = oldPin.getText().toString();
            String newP = newPin.getText().toString();

            String savedPin = prefs.getString("pin", "1234");

            if (!old.equals(savedPin)) {
                Toast.makeText(this, "Wrong old PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newP.length() < 4) {
                Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit().putString("pin", newP).apply();

            Toast.makeText(this, "PIN changed successfully", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}