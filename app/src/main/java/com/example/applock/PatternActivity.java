package com.example.applock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class PatternActivity extends AppCompatActivity {

    PatternLockView patternLockView;
    boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        patternLockView = findViewById(R.id.patternLockView);

        SharedPreferences prefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE);

        isRegister = !prefs.contains("pattern");

        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override public void onStarted() {}
            @Override public void onProgress(List<PatternLockView.Dot> progressStack) {}
            @Override public void onCleared() {}

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String patternString = PatternLockUtils.patternToString(patternLockView, pattern);

                if (isRegister) {
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