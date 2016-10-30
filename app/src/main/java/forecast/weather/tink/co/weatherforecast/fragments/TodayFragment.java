package forecast.weather.tink.co.weatherforecast.fragments;

import android.content.SharedPreferences;
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
import java.util.Date;
import java.util.Locale;

import forecast.weather.tink.co.weatherforecast.helpers.Animations;
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
            no_connection;

    ImageView icon;

    JSONObject jsonobject;

    ImageLoader imageLoader;

    SharedPreferences prefs;

    String city_string, weather_string, date_string, humidity_string, pressure_string, wind_string, sunrise_string, sunset_string, icon_string;
    long date_long, sunset_long, sunrise_long;
    double temp_double, temp_max_double, temp_min_double;

    public TodayFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        init_views(rootView);
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
    }

    @Override
    public void onRefresh() {
        fetch_data();
    }

    public void fetch_data(){
        if (NetworkCheck.isNetworkAvailable(getActivity())) {
            no_connection.setVisibility(View.GONE);
            city.setVisibility(View.VISIBLE);
            forecast.setVisibility(View.VISIBLE);
            date.setVisibility(View.VISIBLE);
            temp.setVisibility(View.VISIBLE);
            temp_max.setVisibility(View.VISIBLE);
            temp_min.setVisibility(View.VISIBLE);
            humidity.setVisibility(View.VISIBLE);
            pressure.setVisibility(View.VISIBLE);
            wind.setVisibility(View.VISIBLE);
            sunrise.setVisibility(View.VISIBLE);
            sunset.setVisibility(View.VISIBLE);
            temp_cels.setVisibility(View.VISIBLE);
            icon.setVisibility(View.VISIBLE);

            if (!prefs.getString("listCity", "").equals("null")) {
                new FetchTodayWeather().execute(prefs.getString("listCity", ""));
            }

        } else {
            no_connection.setVisibility(View.VISIBLE);
            city.setVisibility(View.GONE);
            forecast.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            temp.setVisibility(View.GONE);
            temp_max.setVisibility(View.GONE);
            temp_min.setVisibility(View.GONE);
            humidity.setVisibility(View.GONE);
            pressure.setVisibility(View.GONE);
            wind.setVisibility(View.GONE);
            sunrise.setVisibility(View.GONE);
            sunset.setVisibility(View.GONE);
            temp_cels.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class FetchTodayWeather extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            jsonobject = JSONfunctions.getJSONfromURL(getResources().getString(R.string.today_json) +
                    "?id=" + params[0] +
                    "&units=" + getResources().getString(R.string.units) +
                    "&lang=" + getResources().getString(R.string.lang) +
                    "&appid=" + getResources().getString(R.string.appid));

            if (jsonobject!=null)

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
//                temp.setText("-" + String.valueOf(Math.round(temp_double)));
                temp.setTextColor(getResources().getColor(R.color.blue));
            } else if (Math.round(temp_double) == 0) {
                temp.setText(String.valueOf(Math.round(temp_double)));
            }

            if (Math.round(temp_max_double) > 0) {
                temp_max.setText("+" + String.valueOf(Math.round(temp_max_double)) + getResources().getString(R.string.cels));
                temp_max.setTextColor(getResources().getColor(R.color.red));
            } else if (Math.round(temp_max_double) < 0) {
//                temp_max.setText("-" + String.valueOf(Math.round(temp_max_double)) + getResources().getString(R.string.cels));
                temp_max.setTextColor(getResources().getColor(R.color.blue));
            } else if (Math.round(temp_max_double) == 0) {
                temp_max.setText(String.valueOf(Math.round(temp_double)) + getResources().getString(R.string.cels));
            }

            if (Math.round(temp_min_double) > 0) {
                temp_min.setText("+" + String.valueOf(Math.round(temp_min_double)) + getResources().getString(R.string.cels));
                temp_min.setTextColor(getResources().getColor(R.color.red));
            } else if (Math.round(temp_min_double) < 0) {
//                temp_min.setText("-" + String.valueOf(Math.round(temp_min_double)) + getResources().getString(R.string.cels));
                temp_min.setTextColor(getResources().getColor(R.color.blue));
            }else if (Math.round(temp_min_double) == 0) {
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
