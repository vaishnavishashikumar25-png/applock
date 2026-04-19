package com.example.applock;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();

    public static String lockedAppNow = "";
    public static String temporarilyUnlockedApp = "";

    public static boolean isLocked = false;
    public static boolean isPinOpen = false;
    public static boolean pauseMonitoring = false;

    boolean isMonitoring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.canDrawOverlays(this)) {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        }

        if (!hasUsageAccess()) {
            Toast.makeText(this, "Enable Usage Access", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        Button startBtn = findViewById(R.id.startBtn);
        Button galleryBtn = findViewById(R.id.galleryBtn);
        Button selectBtn = findViewById(R.id.selectAppsBtn);
        Button faceBtn = findViewById(R.id.registerFaceBtn);

        // 🔥 NEW BUTTONS
        Button pinBtn = findViewById(R.id.usePinBtn);
        Button patternBtn = findViewById(R.id.usePatternBtn);

        SharedPreferences prefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE);

        // Start AppLock
        startBtn.setOnClickListener(v -> {
            if (!isMonitoring) {
                isMonitoring = true;
                startChecking();
                Toast.makeText(this, "AppLock Started", Toast.LENGTH_SHORT).show();
            }
        });

        // Open gallery
        galleryBtn.setOnClickListener(v ->
                startActivity(new Intent(this, GalleryActivity.class))
        );

        // Open app selection
        selectBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AppSelectionActivity.class))
        );

        // Optional face register
        faceBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterFaceActivity.class))
        );

        // 🔥 SELECT PIN
        pinBtn.setOnClickListener(v -> {
            prefs.edit().putString("lock_type", "PIN").apply();
            Toast.makeText(this, "PIN selected", Toast.LENGTH_SHORT).show();
        });

        // 🔥 SELECT PATTERN
        patternBtn.setOnClickListener(v -> {
            prefs.edit().putString("lock_type", "PATTERN").apply();
            Toast.makeText(this, "Pattern selected", Toast.LENGTH_SHORT).show();

            // Open pattern setup if not saved
            if (!prefs.contains("pattern")) {
                startActivity(new Intent(this, PatternActivity.class));
            }
        });
    }

    private boolean isAppLocked(String packageName) {
        SharedPreferences prefs =
                getSharedPreferences("LOCKED_APPS", MODE_PRIVATE);
        return prefs.getBoolean(packageName, false);
    }

    private boolean hasUsageAccess() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                getPackageName()
        );

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void startChecking() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!isMonitoring) return;

                if (pauseMonitoring) {
                    handler.postDelayed(this, 500);
                    return;
                }

                String currentApp = getForegroundApp();
                Log.e("APPLOCK", currentApp);

                if (currentApp.equals(getPackageName())) {
                    handler.postDelayed(this, 500);
                    return;
                }

                if (isPinOpen) {
                    handler.postDelayed(this, 500);
                    return;
                }

                if (currentApp.equals(temporarilyUnlockedApp)) {
                    handler.postDelayed(this, 500);
                    return;
                }

                if (isAppLocked(currentApp)) {

                    if (!isLocked) {
                        lockedAppNow = currentApp;
                        isLocked = true;

                        SharedPreferences prefs =
                                getSharedPreferences("AppLockPrefs", MODE_PRIVATE);

                        String type = prefs.getString("lock_type", "PIN");

                        Intent i;

                        if (type.equals("PATTERN")) {
                            i = new Intent(MainActivity.this, PatternActivity.class);
                        } else {
                            i = new Intent(MainActivity.this, PinActivity.class);
                        }

                        i.putExtra("APP", currentApp);
                        i.putExtra("FROM_LOCK", true);
                        startActivity(i);
                    }

                } else {
                    isLocked = false;
                    lockedAppNow = "";
                    temporarilyUnlockedApp = "";
                    pauseMonitoring = false;
                }

                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private String getForegroundApp() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();

        List<UsageStats> stats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - 5000,
                time
        );

        if (stats == null || stats.isEmpty()) return "NONE";

        SortedMap<Long, UsageStats> map = new TreeMap<>();
        for (UsageStats u : stats) {
            map.put(u.getLastTimeUsed(), u);
        }

        return map.get(map.lastKey()).getPackageName();
    }
}