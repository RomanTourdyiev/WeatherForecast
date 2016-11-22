package forecast.weather.tink.co.weatherforecast.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import forecast.weather.tink.co.weatherforecast.helpers.NotificationPublisher;

public class NotificationService extends Service {

    String refresh_range = "1";
    long hour = 3600000L;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent!=null) {
            Bundle extras = intent.getExtras();

            if (extras != null) {
                refresh_range = intent.getStringExtra("refresh_range");
                start_timer(refresh_range);
            }
        }

        return START_STICKY;
    }


    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void start_timer(String refresh_range) {

        int range = Integer.parseInt(refresh_range);

        Intent intent = new Intent(this, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + range*hour, pendingIntent);

    }
}
