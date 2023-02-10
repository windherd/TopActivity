package com.windherd.topactivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AppShortcutsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (WatchingAccessibilityService.getInstance() == null || !Settings.canDrawOverlays(this)) {
            SPHelper.setIsShowWindow(this, true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        boolean isShow = !SPHelper.isShowWindow(this);
        SPHelper.setIsShowWindow(this, isShow);
        if (!isShow) {
            TasksWindow.dismiss(this);
            NotificationActionReceiver.showNotification(this, true);
        } else {
            TasksWindow.show(this, getPackageName() + "\n" + getClass().getName());
            NotificationActionReceiver.showNotification(this, false);
        }
        sendBroadcast(new Intent(MainActivity.ACTION_STATE_CHANGED));
        finish();
    }
}
