package android.jason.mobileapps;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

public class NotificationService extends AccessibilityService {
    private static final String TAG = "NotificationFetcherService";

    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!(event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) ){
            return;
        }

        Notification localNotification = (Notification)event.getParcelableData();

        if (localNotification != null) {
            Intent intent=new Intent();
            intent.putExtra("NotifyData", localNotification);
                    intent.setAction(".NotificationService");
                            sendBroadcast(intent);
        }

    }

    @Override
    protected void onServiceConnected() {

        // Define it in both xml file and here,  for compatibility with pre-ICS devices
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {
        System.out.println("onInterrupt");
    }
}
