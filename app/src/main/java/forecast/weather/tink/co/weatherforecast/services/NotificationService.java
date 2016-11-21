package forecast.weather.tink.co.weatherforecast.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.activities.MainActivity;
import forecast.weather.tink.co.weatherforecast.helpers.DBHelper;
import forecast.weather.tink.co.weatherforecast.helpers.JSONfunctions;
import forecast.weather.tink.co.weatherforecast.helpers.NetworkCheck;
import forecast.weather.tink.co.weatherforecast.helpers.NotificationPublisher;


/**
 * Created by imac on 12.08.16.
 */
public class NotificationService extends Service {

    SharedPreferences prefs;
    String refresh_range = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.hasExtra("refresh_range")) {
                refresh_range = (String) intent.getExtras().get("refresh_range");
                start_timer(refresh_range);
            }
        }

        return START_STICKY;
    }


    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void start_timer(String refresh_range) {

        Calendar calendar = Calendar.getInstance();
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY);
        int hour_new = hour_now + Integer.parseInt(refresh_range);
        calendar.set(Calendar.HOUR_OF_DAY, hour_new);

        Intent intent1 = new Intent(this, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Toast.makeText(this, "Service" + "\n" + "refresh=" + prefs.getString("refresh_range", "") + "\n"
//                + "hour=" + String.valueOf(hour_new) + "\n"
//                + "millis=" + String.valueOf(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();


    }
}
