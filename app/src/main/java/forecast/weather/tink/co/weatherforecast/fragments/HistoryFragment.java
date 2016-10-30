package forecast.weather.tink.co.weatherforecast.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.adapters.ListViewAdapter;
import forecast.weather.tink.co.weatherforecast.helpers.DBHelper;
import forecast.weather.tink.co.weatherforecast.helpers.JSONfunctions;
import forecast.weather.tink.co.weatherforecast.helpers.NetworkCheck;

/**
 * Created by Повелитель on 24.10.2016.
 */

public class HistoryFragment extends android.support.v4.app.Fragment {

    ListView history_listView;
    ProgressBar progress;

    ListViewAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist;

    DBHelper dbHelper;

    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        init_views(rootView);
        fetch_data(0);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.date:
                fetch_data(0);
                item.setChecked(true);
                return true;

            case R.id.temp:
                fetch_data(1);
                item.setChecked(true);
                return true;

            case R.id.humidity:
                fetch_data(2);
                item.setChecked(true);
                return true;

            case R.id.pressure:
                fetch_data(3);
                item.setChecked(true);
                return true;

            case R.id.wind:
                fetch_data(4);
                item.setChecked(true);
                return true;

            case R.id.city:
                fetch_data(5);
                item.setChecked(true);
                return true;
        }

        return false;
    }

    public void init_views(View rootView) {
        history_listView = (ListView) rootView.findViewById(R.id.history_listView);
        progress = (ProgressBar) rootView.findViewById(R.id.progress);
    }

    public void fetch_data(int param) {
        new FetchHistoryWeather().execute(param);
    }

    private class FetchHistoryWeather extends AsyncTask<Integer, Void, Void> {

        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... params) {

            dbHelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            arraylist = new ArrayList<HashMap<String, String>>();

            if (params[0] == 0) {
                cursor = db.rawQuery("SELECT * FROM city_692194 ORDER BY date ASC", null);
            } else if (params[0] == 1) {
                cursor = db.rawQuery("SELECT * FROM city_692194 ORDER BY temp ASC", null);
            } else if (params[0] == 2) {
                cursor = db.rawQuery("SELECT * FROM city_692194 ORDER BY humidity ASC", null);
            } else if (params[0] == 3) {
                cursor = db.rawQuery("SELECT * FROM city_692194 ORDER BY pressure ASC", null);
            } else if (params[0] == 4) {
                cursor = db.rawQuery("SELECT * FROM city_692194 ORDER BY wind ASC", null);
            } else if (params[0] == 5) {
                cursor = db.rawQuery("SELECT * FROM * ORDER BY date ASC", null);
                //loop through array of cities and fetch every table saved//todo
            }
            cursor.moveToFirst();
            do {
                HashMap<String, String> history = new HashMap<String, String>();

                history.put("date", cursor.getString(cursor.getColumnIndexOrThrow("date")));
                history.put("temp_day", cursor.getString(cursor.getColumnIndexOrThrow("temp")));
                history.put("temp_min", cursor.getString(cursor.getColumnIndexOrThrow("temp_min")));
                history.put("temp_max", cursor.getString(cursor.getColumnIndexOrThrow("temp_max")));

                history.put("temp_morn", "0.0");
                history.put("temp_eve", "0.0");
                history.put("temp_night", "0.0");

                history.put("icon", cursor.getString(cursor.getColumnIndexOrThrow("icon")));
                history.put("forecast", cursor.getString(cursor.getColumnIndexOrThrow("forecast")));
                history.put("humidity", cursor.getString(cursor.getColumnIndexOrThrow("humidity")));
                history.put("pressure", cursor.getString(cursor.getColumnIndexOrThrow("pressure")));
                history.put("wind", cursor.getString(cursor.getColumnIndexOrThrow("wind")));

                arraylist.add(history);

            } while (cursor.moveToNext());
            cursor.close();
            db.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            ListViewAdapter adapter = new ListViewAdapter(getActivity(), arraylist);

            if (!adapter.isEmpty()) {
                history_listView.setAdapter(adapter);
            }

            progress.setVisibility(View.GONE);
        }
    }
}
