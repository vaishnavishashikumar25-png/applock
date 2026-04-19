package com.example.applock;

import android.app.*;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.*;
import android.widget.TextView;

public class LockService extends Service {

    private WindowManager wm;
    private TextView view;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String app = intent.getStringExtra("APP");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "lock_channel",
                    "AppLock",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, "lock_channel")
                    .setContentTitle("AppLock Running")
                    .setSmallIcon(android.R.drawable.ic_lock_lock)
                    .build();

            startForeground(1, notification);
        }

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        view = new TextView(this);
        view.setText("APP LOCKED 🔒");
        view.setTextSize(30);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundColor(0xFF000000);
        view.setTextColor(0xFFFFFFFF);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                -1, -1,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );

        wm.addView(view, params);

        new Handler().postDelayed(() -> {

            if (view != null) {
                wm.removeView(view);
                view = null;
            }

            Intent i = new Intent(this, PinActivity.class);
            i.putExtra("APP", app);
            i.putExtra("FROM_LOCK", true); // 🔥 IMPORTANT FIX
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }, 300);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (view != null) {
            wm.removeView(view);
            view = null;
        }
    }

    public IBinder onBind(Intent intent) { return null; }
}