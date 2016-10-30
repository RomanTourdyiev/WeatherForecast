package forecast.weather.tink.co.weatherforecast.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.fragments.HistoryFragment;
import forecast.weather.tink.co.weatherforecast.fragments.PreferencesFragment;
import forecast.weather.tink.co.weatherforecast.fragments.TodayFragment;
import forecast.weather.tink.co.weatherforecast.fragments.WeatherFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    FrameLayout content_frame;

    SharedPreferences prefs;


    private static boolean showing = false;
    static int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_views();
        requestPermission(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (!prefs.getString("listCity", "").equals("null")) {
            navigationView.setCheckedItem(R.id.nav_settings);
            show_fragment(getResources().getString(R.string.settings), new PreferencesFragment());
        } else {
            navigationView.setCheckedItem(R.id.nav_weather);
            show_fragment(getResources().getString(R.string.weather), new WeatherFragment());
        }


    }

    public void init_views() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_weather) {
            show_fragment(getResources().getString(R.string.weather), new WeatherFragment());
        } else if (id == R.id.nav_history) {
            show_fragment(getResources().getString(R.string.history), new HistoryFragment());
        } else if (id == R.id.nav_settings) {
            show_fragment(getResources().getString(R.string.settings), new PreferencesFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void show_fragment(String string, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
        toolbar.setTitle(string);
    }

    private static void requestPermission(final Context context) {
        if (!showing) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        showing = true;
    }
}
