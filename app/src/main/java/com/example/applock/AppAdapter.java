package com.example.applock;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class AppAdapter extends BaseAdapter {

    Context context;
    ArrayList<AppModel> list;

    public AppAdapter(Context context, ArrayList<AppModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int i) { return list.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.app_item, parent, false);
        }

        ImageView icon = view.findViewById(R.id.appIcon);
        TextView name = view.findViewById(R.id.appName);
        Switch toggle = view.findViewById(R.id.toggleLock);

        AppModel app = list.get(i);

        icon.setImageDrawable(app.icon);
        name.setText(app.name);

        toggle.setOnCheckedChangeListener(null);
        toggle.setChecked(app.isLocked);

        toggle.setOnCheckedChangeListener((btn, isChecked) -> {

            app.isLocked = isChecked;

            SharedPreferences prefs =
                    context.getSharedPreferences("LOCKED_APPS", Context.MODE_PRIVATE);

            prefs.edit().putBoolean(app.packageName, isChecked).apply();
        });

        return view;
    }
}