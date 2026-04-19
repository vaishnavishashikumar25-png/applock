package com.example.applock;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<AppModel> appList = new ArrayList<>();
    AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        listView = findViewById(R.id.appListView);

        loadApps();

        adapter = new AppAdapter(this, appList);
        listView.setAdapter(adapter);
    }

    private void loadApps() {

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        SharedPreferences prefs = getSharedPreferences("LOCKED_APPS", MODE_PRIVATE);

        for (ApplicationInfo app : apps) {

            if (pm.getLaunchIntentForPackage(app.packageName) != null) {

                String name = pm.getApplicationLabel(app).toString();
                boolean isLocked = prefs.getBoolean(app.packageName, false);

                appList.add(new AppModel(
                        name,
                        app.packageName,
                        pm.getApplicationIcon(app),
                        isLocked
                ));
            }
        }
    }
}