package com.example.applock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PatternActivity extends AppCompatActivity {

    PatternView patternLockView;
    boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        patternLockView = findViewById(R.id.patternLockView);

        SharedPreferences prefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE);

        isRegister = !prefs.contains("pattern");

        patternLockView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override public void onStarted() {}
            @Override public void onProgress(List<Integer> progressStack) {}
            @Override public void onCleared() {}

            @Override
            public void onComplete(List<Integer> pattern) {
                StringBuilder sb = new StringBuilder();
                for (Integer i : pattern) sb.append(i);
                String patternString = sb.toString();

                if (isRegister) {
                    if (pattern.size() < 4) {
                        Toast.makeText(PatternActivity.this, "At least 4 dots required", Toast.LENGTH_SHORT).show();
                        patternLockView.clearPattern();
                        return;
                    }
                    prefs.edit().putString("pattern", patternString).apply();
                    Toast.makeText(PatternActivity.this,
                            "Pattern saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String saved = prefs.getString("pattern", "");

                    if (patternString.equals(saved)) {
                        Toast.makeText(PatternActivity.this,
                                "Unlocked", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PatternActivity.this,
                                "Wrong pattern", Toast.LENGTH_SHORT).show();
                        patternLockView.clearPattern();
                    }
                }
            }
        });
    }
}