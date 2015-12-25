package android.jason.mobileapps;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private NotifyDataReceiver receiver;
    private LinearLayout rootLayout;
    private Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        registerBroadcast();

        button = (Button) findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void registerBroadcast() {
        receiver = new NotifyDataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(".NotificationService");
        this.registerReceiver(receiver, filter);
    }

    private class NotifyDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Parcelable notifyParcelable = intent.getParcelableExtra("NotifyData");

            if (notifyParcelable != null) {
                final Notification notification = (Notification) notifyParcelable;
                //Toast.makeText(getApplicationContext(), "msg coming!!!", Toast.LENGTH_LONG).show();
                ToastUtil.init(getApplicationContext());
                ToastUtil.show("hello world!");
                ToastUtil.eixt();
                RemoteViews remoteViews = notification.contentView ;
                if (remoteViews == null) {
                    return ;
                }
                View v = remoteViews.apply(MainActivity.this,rootLayout);
                rootLayout.addView(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            notification.contentIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }
    }
}
