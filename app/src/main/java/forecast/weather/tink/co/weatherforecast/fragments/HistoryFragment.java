package forecast.weather.tink.co.weatherforecast.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.activities.MainActivity;
import forecast.weather.tink.co.weatherforecast.adapters.ListViewAdapter;
import forecast.weather.tink.co.weatherforecast.helpers.DBHelper;

/**
 * Created by Повелитель on 24.10.2016.
 */

public class HistoryFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemSelectedListener {

    ListView history_listView;
    ProgressBar progress;

    ListViewAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist;
    List<String> cities, all_cities, ids, all_ids;

    ArrayAdapter<String> spinnerAdapter;

    DBHelper dbHelper;

    public boolean sort_by_city = false;
    String spinner_selection = "all";
    int sort_selection = 0;


    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        init_views(rootView);


        new SpinnerEntries().execute();

        MainActivity.spinnerCity.setOnItemSelectedListener(this);
        setHasOptionsMenu(true);
        return rootView;
    }

    public void init_views(View rootView) {
        history_listView = (ListView) rootView.findViewById(R.id.history_listView);
        progress = (ProgressBar) rootView.findViewById(R.id.progress);

        all_cities = Arrays.asList((getResources().getStringArray(R.array.cities)));
        all_ids = Arrays.asList((getResources().getStringArray(R.array.id_s)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.date:
                sort_selection = 0;
                fetch_data(sort_selection, spinner_selection);
                item.setChecked(true);
                return true;

            case R.id.temp:
                sort_selection = 1;
                fetch_data(sort_selection, spinner_selection);
                item.setChecked(true);
                return true;

            case R.id.humidity:
                sort_selection = 2;
                fetch_data(sort_selection, spinner_selection);
                item.setChecked(true);
                return true;

            case R.id.pressure:
                sort_selection = 3;
                fetch_data(sort_selection, spinner_selection);
                item.setChecked(true);
                return true;

            case R.id.wind:
                sort_selection = 4;
                fetch_data(sort_selection, spinner_selection);
                item.setChecked(true);
                return true;
        }

        return false;
    }

    public void fetch_data(int param, String city) {
        new FetchHistoryWeather().execute(param, city);
    }

    private class FetchHistoryWeather extends AsyncTask<Object, Void, Void> {

        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Object... params) {

            dbHelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            arraylist = new ArrayList<HashMap<String, String>>();

            if ((params[1]).equals("all")) {
                if ((Integer) params[0] == 0) {
                    cursor = db.rawQuery("SELECT * FROM city_table ORDER BY date ASC", null);
                } else if ((Integer) params[0] == 1) {
                    cursor = db.rawQuery("SELECT * FROM city_table ORDER BY temp ASC", null);
                } else if ((Integer) params[0] == 2) {
                    cursor = db.rawQuery("SELECT * FROM city_table ORDER BY humidity ASC", null);
                } else if ((Integer) params[0] == 3) {
                    cursor = db.rawQuery("SELECT * FROM city_table ORDER BY pressure ASC", null);
                } else if ((Integer) params[0] == 4) {
                    cursor = db.rawQuery("SELECT * FROM city_table ORDER BY wind ASC", null);
                }
            } else {
                if ((Integer) params[0] == 0) {
                    cursor = db.rawQuery("SELECT * FROM city_table WHERE city =" + params[1] + " ORDER BY date ASC", null);
                } else if ((Integer) params[0] == 1) {
                    cursor = db.rawQuery("SELECT * FROM city_table WHERE city =" + params[1] + " ORDER BY temp ASC", null);
                } else if ((Integer) params[0] == 2) {
                    cursor = db.rawQuery("SELECT * FROM city_table WHERE city =" + params[1] + " ORDER BY humidity ASC", null);
                } else if ((Integer) params[0] == 3) {
                    cursor = db.rawQuery("SELECT * FROM city_table WHERE city =" + params[1] + " ORDER BY pressure ASC", null);
                } else if ((Integer) params[0] == 4) {
                    cursor = db.rawQuery("SELECT * FROM city_table WHERE city =" + params[1] + " ORDER BY wind ASC", null);
                }
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
            ListViewAdapter adapter = new ListViewAdapter(getActivity(), arraylist, true);

            if (!adapter.isEmpty()) {
                history_listView.setAdapter(adapter);
            }

            progress.setVisibility(View.GONE);
        }
    }

    private class SpinnerEntries extends AsyncTask<Void, Void, Void> {

        Cursor cursor;
        String entry = "";
        int position = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.progressSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            dbHelper = new DBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            cities = new ArrayList<>();
            ids = new ArrayList<>();
            cursor = db.rawQuery("SELECT * FROM city_table ORDER BY city ASC", null);

            cursor.moveToFirst();
            do {
                entry = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                if (!ids.contains(entry)) {
                    ids.add(entry);

                    position = all_ids.indexOf(entry);
                    cities.add(all_cities.get(position));
                }

            } while (cursor.moveToNext());
            cursor.close();
            db.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (ids.size() > 1) {
                cities.add(0, getResources().getString(R.string.all_cities));
                ids.add(0, "all");
            }

            spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, cities);
            spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

            if (!spinnerAdapter.isEmpty()) {
                MainActivity.spinnerCity.setAdapter(spinnerAdapter);
                MainActivity.spinnerCity.setVisibility(View.VISIBLE);
            }

            MainActivity.progressSpinner.setVisibility(View.GONE);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinner_selection = ids.get(position);
        fetch_data(sort_selection, spinner_selection);
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPause(){
        super.onPause();
        MainActivity.progressSpinner.setVisibility(View.GONE);
        MainActivity.spinnerCity.setVisibility(View.GONE);
    }

}
