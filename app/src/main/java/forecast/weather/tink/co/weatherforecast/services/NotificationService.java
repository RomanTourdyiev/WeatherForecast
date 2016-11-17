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
    JSONObject jsonobjecttoday;

    DBHelper dbHelper;

    String
            city_string,
            weather_string,
            humidity_string,
            pressure_string,
            wind_string,
            icon_string,
            temp_alert_min,
            temp_alert_max,
            press_alert_min,
            press_alert_max,
            humid_alert_min,
            humid_alert_max,
            wind_alert_min,
            wind_alert_max;

    long date_long, sunset_long, sunrise_long;
    double temp_double, temp_max_double, temp_min_double;

    boolean chkbox_temp, chkbox_press, chkbox_humid, chkbox_wind;

    int refresh_range = 1;//hours
    int hour = 3600000;
    int minute = 60000;

    @Override
    public void onCreate() {
        super.onCreate();

        start_timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void init_prefs() {

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//        temp_alert_min = prefs.getString("temp_alert_min", String.valueOf(getResources().getInteger(R.integer.temp_min)));
//        temp_alert_max = prefs.getString("temp_alert_max", String.valueOf(getResources().getInteger(R.integer.temp_max)));
//        press_alert_min = prefs.getString("press_alert_min", String.valueOf(getResources().getInteger(R.integer.press_min)));
//        press_alert_max = prefs.getString("press_alert_max", String.valueOf(getResources().getInteger(R.integer.press_max)));
//        humid_alert_min = prefs.getString("humid_alert_min", String.valueOf(getResources().getInteger(R.integer.humid_min)));
//        humid_alert_max = prefs.getString("humid_alert_max", String.valueOf(getResources().getInteger(R.integer.humid_max)));
//        wind_alert_min = prefs.getString("wind_alert_min", String.valueOf(getResources().getInteger(R.integer.wind_min)));
//        wind_alert_max = prefs.getString("wind_alert_max", String.valueOf(getResources().getInteger(R.integer.wind_max)));
//
//        chkbox_temp = prefs.getBoolean("chkbox_temp", true);
//        chkbox_press = prefs.getBoolean("chkbox_press", true);
//        chkbox_humid = prefs.getBoolean("chkbox_humid", true);
//        chkbox_wind = prefs.getBoolean("chkbox_wind", true);
//
        refresh_range = Integer.parseInt(prefs.getString("refresh_range", ""));
    }

    public void start_timer() {

        init_prefs();

        Calendar calendar = Calendar.getInstance();
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY);
        int hour_new = hour_now + refresh_range;
        calendar.set(Calendar.HOUR_OF_DAY, hour_new);

        Intent intent1 = new Intent(this, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), hour*refresh_range, pendingIntent);

//        am.cancel(pendingIntent);


    }

//    private class TodayNotifJSON extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//
//            dbHelper = new DBHelper(getApplicationContext());
//            jsonobjecttoday = JSONfunctions.getJSONfromURL(getResources().getString(R.string.today_json) +
//                    "?id=" + params[0] +
//                    "&units=" + getResources().getString(R.string.units) +
//                    "&lang=" + getResources().getString(R.string.lang) +
//                    "&appid=" + getResources().getString(R.string.appid));
//
//            if (jsonobjecttoday != null)
//
//                try {
//                    city_string = jsonobjecttoday.getString("name") + ", " + jsonobjecttoday.getJSONObject("sys").getString("country");
//                    weather_string = jsonobjecttoday.getJSONArray("weather").getJSONObject(0).getString("description");
//                    date_long = jsonobjecttoday.getLong("dt");
//                    temp_double = jsonobjecttoday.getJSONObject("main").getDouble("temp");
//                    temp_max_double = jsonobjecttoday.getJSONObject("main").getDouble("temp_max");
//                    temp_min_double = jsonobjecttoday.getJSONObject("main").getDouble("temp_min");
//                    humidity_string = jsonobjecttoday.getJSONObject("main").getString("humidity");
//                    pressure_string = jsonobjecttoday.getJSONObject("main").getString("pressure");
//                    wind_string = jsonobjecttoday.getJSONObject("wind").getString("speed");
//                    sunrise_long = jsonobjecttoday.getJSONObject("sys").getLong("sunrise");
//                    sunset_long = jsonobjecttoday.getJSONObject("sys").getLong("sunset");
//                    icon_string = jsonobjecttoday.getJSONArray("weather").getJSONObject(0).getString("icon");
//
//                    ContentValues cv = new ContentValues();
//                    SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//                    Cursor c = db.rawQuery("SELECT * FROM city_table WHERE date = " + String.valueOf(date_long), null);
//                    if (c.getCount() <= 0) {
//                        cv.put("date", date_long);
//                        cv.put("city", params[0]);
//                        cv.put("temp", temp_double);
//                        cv.put("temp_min", temp_min_double);
//                        cv.put("temp_max", temp_max_double);
//                        cv.put("icon", icon_string);
//                        cv.put("forecast", weather_string);
//                        cv.put("humidity", humidity_string);
//                        cv.put("pressure", pressure_string);
//                        cv.put("wind", wind_string);
//
//                        db.insert("city_table", null, cv);
//                    }
//                    c.close();
//                    db.close();
//
//                } catch (JSONException e) {
//                    Log.e("Error", e.getMessage());
//                    e.printStackTrace();
//                }
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void args) {
//
//            if (NetworkCheck.isNetworkAvailable(getApplicationContext())) {
//                if (prefs.getString("city", "").length() != 0) {
//                    if (temp_double <= Float.parseFloat(temp_alert_min) || temp_double >= Float.parseFloat(temp_alert_max) ||
//                            Float.parseFloat(pressure_string) <= Float.parseFloat(press_alert_min) || Float.parseFloat(pressure_string) >= Float.parseFloat(press_alert_max) ||
//                            Float.parseFloat(humidity_string) <= Float.parseFloat(humid_alert_min) || Float.parseFloat(humidity_string) >= Float.parseFloat(humid_alert_max) ||
//                            Float.parseFloat(wind_string) <= Float.parseFloat(wind_alert_min) || Float.parseFloat(wind_string) >= Float.parseFloat(wind_alert_max)) {
//                        if (!MainActivity.isShowingActivity) {
//                            showNotification(getNotification(getResources().getString(R.string.caution)), 1);
//                        }
//                    }
//                }
//            }
//        }
//    }

//    private void showNotification(Notification notification, int id) {
//
//        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
//        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
//        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
//    }
//
//    private Notification getNotification(String content) {
//
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, 0);
//
//        Notification.Builder builder = new Notification.Builder(this)
//                .setWhen(System.currentTimeMillis())
//                .setContentTitle(getResources().getString(R.string.today))
//                .setContentText(content)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setContentIntent(pIntent)
//                .setAutoCancel(true)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .setSmallIcon(R.mipmap.ic_launcher);
//
//        Notification notification = builder.getNotification();
////        Notification notification = new Notification.BigTextStyle(builder).bigText(getResources().getString(R.string.match_start)  + " " + string_delay).build();
//        return notification;
//    }

}
