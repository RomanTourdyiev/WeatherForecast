package forecast.weather.tink.co.weatherforecast.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.activities.MainActivity;
import forecast.weather.tink.co.weatherforecast.services.NotificationService;


public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    SharedPreferences prefs;

    JSONObject jsonobjecttoday;

    DBHelper dbHelper;

    String
            city,
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

    private static int id = 1;
    Context context;

    public void onReceive(Context context, Intent intent) {

        init_prefs(context);
        this.context = context;

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Notification notification = intent.getParcelableExtra(NOTIFICATION);
//        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
//        notificationManager.notify(id, notification);

        TodayNotifJSON todayNotifJson = new TodayNotifJSON();
        todayNotifJson.execute(city);

    }

    private void init_prefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        city = prefs.getString("city", "");

        temp_alert_min = prefs.getString("temp_alert_min", String.valueOf(context.getResources().getInteger(R.integer.temp_min)));
        temp_alert_max = prefs.getString("temp_alert_max", String.valueOf(context.getResources().getInteger(R.integer.temp_max)));
        press_alert_min = prefs.getString("press_alert_min", String.valueOf(context.getResources().getInteger(R.integer.press_min)));
        press_alert_max = prefs.getString("press_alert_max", String.valueOf(context.getResources().getInteger(R.integer.press_max)));
        humid_alert_min = prefs.getString("humid_alert_min", String.valueOf(context.getResources().getInteger(R.integer.humid_min)));
        humid_alert_max = prefs.getString("humid_alert_max", String.valueOf(context.getResources().getInteger(R.integer.humid_max)));
        wind_alert_min = prefs.getString("wind_alert_min", String.valueOf(context.getResources().getInteger(R.integer.wind_min)));
        wind_alert_max = prefs.getString("wind_alert_max", String.valueOf(context.getResources().getInteger(R.integer.wind_max)));

        chkbox_temp = prefs.getBoolean("chkbox_temp", true);
        chkbox_press = prefs.getBoolean("chkbox_press", true);
        chkbox_humid = prefs.getBoolean("chkbox_humid", true);
        chkbox_wind = prefs.getBoolean("chkbox_wind", true);

        refresh_range = Integer.parseInt(prefs.getString("refresh_range", ""));
    }

    private void create_notification(Context context) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,  notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.today))
                .setContentText(context.getResources().getString(R.string.caution))
                .setWhen(when)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL);
        notificationManager.notify(id, mNotifyBuilder.build());
        id++;
    }

    private class TodayNotifJSON extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            dbHelper = new DBHelper(context);
            jsonobjecttoday = JSONfunctions.getJSONfromURL(context.getResources().getString(R.string.today_json) +
                    "?id=" + params[0] +
                    "&units=" + context.getResources().getString(R.string.units) +
                    "&lang=" + context.getResources().getString(R.string.lang) +
                    "&appid=" + context.getResources().getString(R.string.appid));

            if (jsonobjecttoday != null)

                try {
                    city_string = jsonobjecttoday.getString("name") + ", " + jsonobjecttoday.getJSONObject("sys").getString("country");
                    weather_string = jsonobjecttoday.getJSONArray("weather").getJSONObject(0).getString("description");
                    date_long = jsonobjecttoday.getLong("dt");
                    temp_double = jsonobjecttoday.getJSONObject("main").getDouble("temp");
                    temp_max_double = jsonobjecttoday.getJSONObject("main").getDouble("temp_max");
                    temp_min_double = jsonobjecttoday.getJSONObject("main").getDouble("temp_min");
                    humidity_string = jsonobjecttoday.getJSONObject("main").getString("humidity");
                    pressure_string = jsonobjecttoday.getJSONObject("main").getString("pressure");
                    wind_string = jsonobjecttoday.getJSONObject("wind").getString("speed");
                    sunrise_long = jsonobjecttoday.getJSONObject("sys").getLong("sunrise");
                    sunset_long = jsonobjecttoday.getJSONObject("sys").getLong("sunset");
                    icon_string = jsonobjecttoday.getJSONArray("weather").getJSONObject(0).getString("icon");

                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    Cursor c = db.rawQuery("SELECT * FROM city_table WHERE date = " + String.valueOf(date_long), null);
                    if (c.getCount() <= 0) {
                        cv.put("date", date_long);
                        cv.put("city", params[0]);
                        cv.put("temp", temp_double);
                        cv.put("temp_min", temp_min_double);
                        cv.put("temp_max", temp_max_double);
                        cv.put("icon", icon_string);
                        cv.put("forecast", weather_string);
                        cv.put("humidity", humidity_string);
                        cv.put("pressure", pressure_string);
                        cv.put("wind", wind_string);

                        db.insert("city_table", null, cv);
                    }
                    c.close();
                    db.close();

                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }


            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (NetworkCheck.isNetworkAvailable(context)) {
                if (prefs.getString("city", "").length() != 0) {
                    if (temp_double <= Float.parseFloat(temp_alert_min) || temp_double >= Float.parseFloat(temp_alert_max) ||
                            Float.parseFloat(pressure_string) <= Float.parseFloat(press_alert_min) || Float.parseFloat(pressure_string) >= Float.parseFloat(press_alert_max) ||
                            Float.parseFloat(humidity_string) <= Float.parseFloat(humid_alert_min) || Float.parseFloat(humidity_string) >= Float.parseFloat(humid_alert_max) ||
                            Float.parseFloat(wind_string) <= Float.parseFloat(wind_alert_min) || Float.parseFloat(wind_string) >= Float.parseFloat(wind_alert_max)) {
                        if (!MainActivity.isShowingActivity) {
                            create_notification(context);
                        }
                    }
                }
            }
        }
    }
}