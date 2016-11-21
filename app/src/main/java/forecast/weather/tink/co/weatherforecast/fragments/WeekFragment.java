package forecast.weather.tink.co.weatherforecast.fragments;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.activities.MainActivity;
import forecast.weather.tink.co.weatherforecast.adapters.ListViewAdapter;
import forecast.weather.tink.co.weatherforecast.helpers.DBHelper;
import forecast.weather.tink.co.weatherforecast.helpers.JSONfunctions;
import forecast.weather.tink.co.weatherforecast.helpers.NetworkCheck;

/**
 * Created by Повелитель on 22.10.2016.
 */

public class WeekFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ListView week_listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView no_connection;

    JSONObject jsonobject, jsonobjectday;
    JSONArray jsonarray;
    ListViewAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist;

    SharedPreferences prefs;

    public WeekFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_week, container, false);
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

//        MainActivity.spinnerCity.setVisibility(View.GONE);

        return rootView;
    }

    public void init_views(View rootView) {
        week_listView = (ListView) rootView.findViewById(R.id.week_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        no_connection = (TextView) rootView.findViewById(R.id.no_connection);
    }

    @Override
    public void onRefresh() {
        fetch_data();
    }

    public void fetch_data() {
        if (NetworkCheck.isNetworkAvailable(getActivity())) {
            no_connection.setVisibility(View.GONE);
            week_listView.setVisibility(View.VISIBLE);
            if (prefs.getString("city", "").length() != 0) {
                new FetchWeekWeather().execute(prefs.getString("city", ""));
            }
        } else {
            no_connection.setVisibility(View.VISIBLE);
            week_listView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class FetchWeekWeather extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            jsonobject = JSONfunctions.getJSONfromURL(getResources().getString(R.string.week_json) +
                    "?id=" + params[0] +
                    "&units=" + getResources().getString(R.string.units) +
                    "&lang=" + getResources().getString(R.string.lang) +
                    "&cnt=7" +
                    "&appid=" + getResources().getString(R.string.appid));

            if (jsonobject != null)

                try {
                    arraylist = new ArrayList<HashMap<String, String>>();
                    jsonarray = jsonobject.getJSONArray("list");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        HashMap<String, String> week = new HashMap<String, String>();
                        jsonobjectday = jsonarray.getJSONObject(i);

                        week.put("date", jsonobjectday.getString("dt"));
                        week.put("temp_day", jsonobjectday.getJSONObject("temp").getString("day"));
                        week.put("temp_min", jsonobjectday.getJSONObject("temp").getString("min"));
                        week.put("temp_max", jsonobjectday.getJSONObject("temp").getString("max"));
                        week.put("temp_morn", jsonobjectday.getJSONObject("temp").getString("morn"));
                        week.put("temp_eve", jsonobjectday.getJSONObject("temp").getString("eve"));
                        week.put("temp_night", jsonobjectday.getJSONObject("temp").getString("night"));
                        week.put("icon", jsonobjectday.getJSONArray("weather").getJSONObject(0).getString("icon"));
                        week.put("forecast", jsonobjectday.getJSONArray("weather").getJSONObject(0).getString("description"));
                        week.put("humidity", jsonobjectday.getString("humidity"));
                        week.put("pressure", jsonobjectday.getString("pressure"));
                        week.put("wind", jsonobjectday.getString("speed"));

                        arraylist.add(week);
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (isAdded()) {
                adapter = new ListViewAdapter(getActivity(), arraylist, false);
                week_listView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }

        }
    }


}
