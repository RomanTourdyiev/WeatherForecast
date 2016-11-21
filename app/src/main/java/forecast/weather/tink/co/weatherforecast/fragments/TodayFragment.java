package forecast.weather.tink.co.weatherforecast.fragments;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import forecast.weather.tink.co.weatherforecast.activities.MainActivity;
import forecast.weather.tink.co.weatherforecast.adapters.ListViewAdapter;
import forecast.weather.tink.co.weatherforecast.helpers.Animations;
import forecast.weather.tink.co.weatherforecast.helpers.DBHelper;
import forecast.weather.tink.co.weatherforecast.helpers.ImageLoader;
import forecast.weather.tink.co.weatherforecast.helpers.JSONfunctions;
import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.helpers.NetworkCheck;

/**
 * Created by Повелитель on 22.10.2016.
 */

public class TodayFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    TextView
            city,
            date,
            forecast,
            temp,
            temp_max,
            temp_min,
            humidity,
            pressure,
            wind,
            sunrise,
            sunset,
            temp_cels,
            no_connection,
            temp_blink,
            press_blink,
            humid_blink,
            wind_blink,
            temp_alert_descr,
            press_alert_descr,
            humid_alert_descr,
            wind_alert_descr;

    ImageView icon;

    JSONObject jsonobject;

    ImageLoader imageLoader;

    SharedPreferences prefs;

    DBHelper dbHelper;

    String
            city_string,
            weather_string,
            date_string,
            humidity_string,
            pressure_string,
            wind_string,
            sunrise_string,
            sunset_string,
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

    public TodayFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        init_views(rootView);
        init_prefs();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetch_data();
            }
        });
        imageLoader = new ImageLoader(getActivity());

//        MainActivity.spinnerCity.setVisibility(View.GONE);

        return rootView;
    }

    public void init_views(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        city = (TextView) rootView.findViewById(R.id.city);
        date = (TextView) rootView.findViewById(R.id.date);
        forecast = (TextView) rootView.findViewById(R.id.forecast);
        temp = (TextView) rootView.findViewById(R.id.temp);
        temp_max = (TextView) rootView.findViewById(R.id.temp_max);
        temp_min = (TextView) rootView.findViewById(R.id.temp_min);
        humidity = (TextView) rootView.findViewById(R.id.humidity);
        pressure = (TextView) rootView.findViewById(R.id.pressure);
        wind = (TextView) rootView.findViewById(R.id.wind);
        sunrise = (TextView) rootView.findViewById(R.id.sunrise);
        sunset = (TextView) rootView.findViewById(R.id.sunset);
        temp_cels = (TextView) rootView.findViewById(R.id.temp_cels);
        no_connection = (TextView) rootView.findViewById(R.id.no_connection);
        icon = (ImageView) rootView.findViewById(R.id.icon);
        temp_blink = (TextView) rootView.findViewById(R.id.temp_blink);
        press_blink = (TextView) rootView.findViewById(R.id.press_blink);
        humid_blink = (TextView) rootView.findViewById(R.id.humid_blink);
        wind_blink = (TextView) rootView.findViewById(R.id.wind_blink);
        temp_alert_descr = (TextView) rootView.findViewById(R.id.temp_alert_descr);
        press_alert_descr = (TextView) rootView.findViewById(R.id.press_alert_descr);
        humid_alert_descr = (TextView) rootView.findViewById(R.id.humid_alert_descr);
        wind_alert_descr = (TextView) rootView.findViewById(R.id.wind_alert_descr);
    }

    public void init_prefs() {
        temp_alert_min = prefs.getString("temp_alert_min", String.valueOf(getResources().getInteger(R.integer.temp_min)));
        temp_alert_max = prefs.getString("temp_alert_max", String.valueOf(getResources().getInteger(R.integer.temp_max)));
        press_alert_min = prefs.getString("press_alert_min", String.valueOf(getResources().getInteger(R.integer.press_min)));
        press_alert_max = prefs.getString("press_alert_max", String.valueOf(getResources().getInteger(R.integer.press_max)));
        humid_alert_min = prefs.getString("humid_alert_min", String.valueOf(getResources().getInteger(R.integer.humid_min)));
        humid_alert_max = prefs.getString("humid_alert_max", String.valueOf(getResources().getInteger(R.integer.humid_max)));
        wind_alert_min = prefs.getString("wind_alert_min", String.valueOf(getResources().getInteger(R.integer.wind_min)));
        wind_alert_max = prefs.getString("wind_alert_max", String.valueOf(getResources().getInteger(R.integer.wind_max)));

        chkbox_temp = prefs.getBoolean("chkbox_temp", true);
        chkbox_press = prefs.getBoolean("chkbox_press", true);
        chkbox_humid = prefs.getBoolean("chkbox_humid", true);
        chkbox_wind = prefs.getBoolean("chkbox_wind", true);
    }

    @Override
    public void onRefresh() {
        fetch_data();
    }

    public void fetch_data() {
        if (NetworkCheck.isNetworkAvailable(getActivity())) {
            no_connection.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            if (prefs.getString("city", "").length()!=0) {
                new FetchTodayWeather().execute(prefs.getString("city", ""));
            }

        } else {
            no_connection.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class FetchTodayWeather extends AsyncTask<String, Void, Void> {

        boolean isSaved = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            dbHelper = new DBHelper(getActivity());
            jsonobject = JSONfunctions.getJSONfromURL(getResources().getString(R.string.today_json) +
                    "?id=" + params[0] +
                    "&units=" + getResources().getString(R.string.units) +
                    "&lang=" + getResources().getString(R.string.lang) +
                    "&appid=" + getResources().getString(R.string.appid));

            if (jsonobject != null)

                try {
                    city_string = jsonobject.getString("name") + ", " + jsonobject.getJSONObject("sys").getString("country");
                    weather_string = jsonobject.getJSONArray("weather").getJSONObject(0).getString("description");
                    date_long = jsonobject.getLong("dt");
                    temp_double = jsonobject.getJSONObject("main").getDouble("temp");
                    temp_max_double = jsonobject.getJSONObject("main").getDouble("temp_max");
                    temp_min_double = jsonobject.getJSONObject("main").getDouble("temp_min");
                    humidity_string = jsonobject.getJSONObject("main").getString("humidity");
                    pressure_string = jsonobject.getJSONObject("main").getString("pressure");
                    wind_string = jsonobject.getJSONObject("wind").getString("speed");
                    sunrise_long = jsonobject.getJSONObject("sys").getLong("sunrise");
                    sunset_long = jsonobject.getJSONObject("sys").getLong("sunset");
                    icon_string = jsonobject.getJSONArray("weather").getJSONObject(0).getString("icon");

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
                        isSaved = true;
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
            swipeRefreshLayout.setRefreshing(false);
            city.setText(city_string);
            forecast.setText(weather_string);

            long dv = date_long * 1000;
            Date df = new java.util.Date(dv);
            date_string = new SimpleDateFormat("dd MMMM yyyy, cccc", Locale.getDefault()).format(df);
            date.setText(date_string);

            if (Math.round(temp_double) > 0) {
                temp.setText("+" + String.valueOf(Math.round(temp_double)));
                temp.setTextColor(getResources().getColor(R.color.red));
            } else if (Math.round(temp_double) < 0) {
                temp.setText(String.valueOf(Math.round(temp_double)));
                temp.setTextColor(getResources().getColor(R.color.blue));
            } else if (Math.round(temp_double) == 0) {
                temp.setText(String.valueOf(Math.round(temp_double)));
            }

            if (Math.round(temp_max_double) > 0) {
                temp_max.setText("+" + String.valueOf(Math.round(temp_max_double)) + getResources().getString(R.string.cels));
                temp_max.setTextColor(getResources().getColor(R.color.red));
            } else if (Math.round(temp_max_double) < 0) {
                temp_max.setText(String.valueOf(Math.round(temp_max_double)) + getResources().getString(R.string.cels));
                temp_max.setTextColor(getResources().getColor(R.color.blue));
            } else if (Math.round(temp_max_double) == 0) {
                temp_max.setText(String.valueOf(Math.round(temp_double)) + getResources().getString(R.string.cels));
            }

            if (Math.round(temp_min_double) > 0) {
                temp_min.setText("+" + String.valueOf(Math.round(temp_min_double)) + getResources().getString(R.string.cels));
                temp_min.setTextColor(getResources().getColor(R.color.red));
            } else if (Math.round(temp_min_double) < 0) {
                temp_min.setText(String.valueOf(Math.round(temp_min_double)) + getResources().getString(R.string.cels));
                temp_min.setTextColor(getResources().getColor(R.color.blue));
            } else if (Math.round(temp_min_double) == 0) {
                temp_min.setText(String.valueOf(Math.round(temp_double)) + getResources().getString(R.string.cels));
            }

            humidity.setText(getResources().getString(R.string.humidity) + " " + humidity_string + "%");
            pressure.setText(getResources().getString(R.string.pressure) + " " + pressure_string + " " + getResources().getString(R.string.mm));
            wind.setText(getResources().getString(R.string.wind) + " " + wind_string + " " + getResources().getString(R.string.ms));

            long srv = sunrise_long * 1000;
            Date srf = new java.util.Date(srv);
            sunrise_string = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(srf);
            sunrise.setText(getResources().getString(R.string.sun_up) + " " + sunrise_string);

            long ssv = sunset_long * 1000;
            Date ssf = new java.util.Date(ssv);
            sunset_string = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(ssf);
            sunset.setText(getResources().getString(R.string.sun_down) + " " + sunset_string);

            imageLoader.DisplayImage(getResources().getString(R.string.icon_url) + icon_string + ".png", icon);

            if (chkbox_temp) {
                if (temp_double <= Float.parseFloat(temp_alert_min) || temp_double >= Float.parseFloat(temp_alert_max)) {
                    temp_blink.setVisibility(View.VISIBLE);
                    temp_alert_descr.setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).blink(temp_blink);
                    if (isSaved) {
                        ((MainActivity) getActivity()).show_dialog(1);
                    }
                } else {
                    temp_blink.setVisibility(View.GONE);
                    temp_alert_descr.setVisibility(View.GONE);
                }
            }

            if (chkbox_press) {
                if (Float.parseFloat(pressure_string) <= Float.parseFloat(press_alert_min) || Float.parseFloat(pressure_string) >= Float.parseFloat(press_alert_max)) {
                    press_blink.setVisibility(View.VISIBLE);
                    press_alert_descr.setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).blink(press_blink);
                    if (isSaved) {
                        ((MainActivity) getActivity()).show_dialog(2);
                    }
                } else {
                    press_blink.setVisibility(View.GONE);
                    press_alert_descr.setVisibility(View.GONE);
                }
            }

            if (chkbox_humid) {
                if (Float.parseFloat(humidity_string) <= Float.parseFloat(humid_alert_min) || Float.parseFloat(humidity_string) >= Float.parseFloat(humid_alert_max)) {
                    humid_blink.setVisibility(View.VISIBLE);
                    humid_alert_descr.setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).blink(humid_blink);
                    if (isSaved)
                        ((MainActivity) getActivity()).show_dialog(3);
                } else {
                    humid_blink.setVisibility(View.GONE);
                    humid_alert_descr.setVisibility(View.GONE);
                }
            }

            if (chkbox_wind) {
                if (Float.parseFloat(wind_string) <= Float.parseFloat(wind_alert_min) || Float.parseFloat(wind_string) >= Float.parseFloat(wind_alert_max)) {
                    wind_blink.setVisibility(View.VISIBLE);
                    wind_alert_descr.setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).blink(wind_blink);
                    if (isSaved) {
                        ((MainActivity) getActivity()).show_dialog(4);
                    }
                } else {
                    wind_blink.setVisibility(View.GONE);
                    wind_alert_descr.setVisibility(View.GONE);
                }
            }

            animate();
        }
    }

    public void animate() {
        city.setAnimation(Animations.fade_in());
        forecast.setAnimation(Animations.fade_in());
        date.setAnimation(Animations.fade_in());
        temp.setAnimation(Animations.fade_in());
        temp_max.setAnimation(Animations.fade_in());
        temp_min.setAnimation(Animations.fade_in());
        humidity.setAnimation(Animations.fade_in());
        pressure.setAnimation(Animations.fade_in());
        wind.setAnimation(Animations.fade_in());
        sunrise.setAnimation(Animations.fade_in());
        sunset.setAnimation(Animations.fade_in());
        temp_cels.setAnimation(Animations.fade_in());
    }


}
