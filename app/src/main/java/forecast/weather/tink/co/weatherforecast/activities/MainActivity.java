package forecast.weather.tink.co.weatherforecast.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.fragments.HistoryFragment;
import forecast.weather.tink.co.weatherforecast.fragments.PreferencesFragment;
import forecast.weather.tink.co.weatherforecast.fragments.WeatherFragment;
import forecast.weather.tink.co.weatherforecast.helpers.Animations;
import forecast.weather.tink.co.weatherforecast.services.NotificationService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    public static FrameLayout content_frame;
    public static ProgressBar progressSpinner;
    public static Spinner spinnerCity;

    SharedPreferences prefs;

    String alert_message = "";
    private static boolean showing = false;
    static int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    Vibrator vibrator;
    Uri notification;
    Ringtone ring;
    AlertDialog.Builder builder;
    AlertDialog alert;

    public static boolean isShowingActivity = true;

    public static final String APP_TAG = "WeatherForecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("night_mode", true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_views();
        request_permission(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getString("refresh_range", "").length()!=0) {

            Intent i = new Intent(this, NotificationService.class);
            i.putExtra("refresh_range", prefs.getString("refresh_range", ""));
            startService(i);

        } else {

            Intent i = new Intent(this, NotificationService.class);
            i.putExtra("refresh_range", "1");
            startService(i);

        }
    }

    public void init_views() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        progressSpinner = (ProgressBar) findViewById(R.id.progressSpinner);
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShowingActivity = true;
        Intent intent = getIntent();
        if (intent.hasExtra("show_prefs")) {
            if (intent.getStringExtra("show_prefs").equals("night_mode")) {
                navigationView.setCheckedItem(R.id.nav_settings);
                show_fragment(getResources().getString(R.string.settings), new PreferencesFragment());
            } else if (intent.getStringExtra("show_prefs").equals("import_prefs")) {
                navigationView.setCheckedItem(R.id.nav_settings);
                show_fragment(getResources().getString(R.string.settings), new PreferencesFragment());
                PreferencesFragment.snack_bar(getResources().getString(R.string.import_done));
            }

        } else if (intent.hasExtra("notify_when")){

            if (System.currentTimeMillis() - intent.getLongExtra("notify_when", 1L)>3600000L){
                navigationView.setCheckedItem(R.id.nav_history);
                show_fragment(getResources().getString(R.string.history), new HistoryFragment());
            }else{
                navigationView.setCheckedItem(R.id.nav_weather);
                show_fragment(getResources().getString(R.string.weather), new WeatherFragment());
            }

        } else {
            if (prefs.getString("city", "").length() == 0) {
                navigationView.setCheckedItem(R.id.nav_settings);
                show_fragment(getResources().getString(R.string.settings), new PreferencesFragment());
            } else {
                navigationView.setCheckedItem(R.id.nav_weather);
                show_fragment(getResources().getString(R.string.weather), new WeatherFragment());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isShowingActivity = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isShowingActivity = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        isShowingActivity = hasFocus;

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
            if (prefs.getString("city", "").length() == 0) {
                navigationView.setCheckedItem(R.id.nav_settings);
                navigationView.getMenu().performIdentifierAction(R.id.nav_settings, 0);
                show_fragment(getResources().getString(R.string.settings), new PreferencesFragment());
                Snackbar.make(content_frame, getResources().getString(R.string.warning), Snackbar.LENGTH_LONG).show();
            } else {
                show_fragment(getResources().getString(R.string.weather), new WeatherFragment());
            }
        } else if (id == R.id.nav_history) {
            if (prefs.getString("city", "").length() == 0) {
                navigationView.setCheckedItem(R.id.nav_settings);
                navigationView.getMenu().performIdentifierAction(R.id.nav_settings, 0);
                show_fragment(getResources().getString(R.string.settings), new PreferencesFragment());
                Snackbar.make(content_frame, getResources().getString(R.string.warning), Snackbar.LENGTH_LONG).show();
            } else {
                show_fragment(getResources().getString(R.string.history), new HistoryFragment());
            }
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
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
        toolbar.setTitle(string);
    }

    private static void request_permission(final Context context) {
        if (!showing) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        showing = true;
    }

    public void blink(View view) {
        view.startAnimation(Animations.blink());
    }

    public void show_dialog(int param) {

        if (param == 1) {
            alert_message = getResources().getString(R.string.alert_dialog_message_temp);
        } else if (param == 2) {
            alert_message = getResources().getString(R.string.alert_dialog_message_press);
        } else if (param == 3) {
            alert_message = getResources().getString(R.string.alert_dialog_message_humid);
        } else if (param == 4) {
            alert_message = getResources().getString(R.string.alert_dialog_message_wind);
        }

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ring = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ring.play();


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.alert_dialog_title))
                .setMessage(alert_message)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        alert = builder.create();
        alert.show();
    }

}
