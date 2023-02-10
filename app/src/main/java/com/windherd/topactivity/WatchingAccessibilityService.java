package com.windherd.topactivity;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class WatchingAccessibilityService extends AccessibilityService {
    private static WatchingAccessibilityService sInstance;

    public static WatchingAccessibilityService getInstance() {
        return sInstance;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (SPHelper.isShowWindow(this)) {
                TasksWindow.show(this, event.getPackageName() + "\n" + event.getClassName());
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        sInstance = this;
        if (SPHelper.isShowWindow(this)) {
            NotificationActionReceiver.showNotification(this, false);
        }
        sendBroadcast(new Intent(QuickSettingTileService.ACTION_UPDATE_TITLE));
        super.onServiceConnected();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sInstance = null;
        TasksWindow.dismiss(this);
        NotificationActionReceiver.cancelNotification(this);
        sendBroadcast(new Intent(QuickSettingTileService.ACTION_UPDATE_TITLE));
        return super.onUnbind(intent);
    }
}
