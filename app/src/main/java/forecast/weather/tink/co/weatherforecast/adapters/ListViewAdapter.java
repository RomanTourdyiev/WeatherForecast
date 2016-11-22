package forecast.weather.tink.co.weatherforecast.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.activities.MainActivity;
import forecast.weather.tink.co.weatherforecast.helpers.Animations;
import forecast.weather.tink.co.weatherforecast.helpers.ImageLoader;
import forecast.weather.tink.co.weatherforecast.items.Constants;

public class ListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    ImageLoader imageLoader;
    HashMap<String, String> resultp = new HashMap<String, String>();
    boolean isHistory = false;
    SharedPreferences prefs;

    String
            temp_alert_min,
            temp_alert_max,
            press_alert_min,
            press_alert_max,
            humid_alert_min,
            humid_alert_max,
            wind_alert_min,
            wind_alert_max;

    boolean chkbox_temp, chkbox_press, chkbox_humid, chkbox_wind;

    public ListViewAdapter(Context context, ArrayList<HashMap<String, String>> arraylist, boolean flag) {
        this.context = context;
        data = arraylist;
        isHistory = flag;
        imageLoader = new ImageLoader(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        init_prefs();
    }

    public void init_prefs() {
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
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView icon;
        final LinearLayout details, day_temp_ll;
        TextView
                date,
                day_temp,
                temp_day,
                temp_min,
                temp_max,
                temp_morn,
                temp_eve,
                temp_night,
                forecast,
                humidity,
                pressure,
                wind,
                temp_blink,
                press_blink,
                humid_blink,
                wind_blink,
                temp_morn_blink,
                temp_day_blink,
                temp_eve_blink,
                temp_night_blink,
        temp_alert_descr,
                press_alert_descr,
                humid_alert_descr,
                wind_alert_descr;

        String date_string;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_week, parent, false);
        resultp = data.get(position);

        date = (TextView) itemView.findViewById(R.id.date);
        day_temp = (TextView) itemView.findViewById(R.id.day_temp);
        temp_day = (TextView) itemView.findViewById(R.id.temp_day);
        temp_min = (TextView) itemView.findViewById(R.id.temp_min);
        temp_max = (TextView) itemView.findViewById(R.id.temp_max);
        temp_morn = (TextView) itemView.findViewById(R.id.temp_morn);
        temp_eve = (TextView) itemView.findViewById(R.id.temp_eve);
        temp_night = (TextView) itemView.findViewById(R.id.temp_night);
        forecast = (TextView) itemView.findViewById(R.id.forecast);
        humidity = (TextView) itemView.findViewById(R.id.humidity);
        pressure = (TextView) itemView.findViewById(R.id.pressure);
        wind = (TextView) itemView.findViewById(R.id.wind);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        details = (LinearLayout) itemView.findViewById(R.id.details);
        day_temp_ll = (LinearLayout) itemView.findViewById(R.id.day_temp_ll);

        temp_blink = (TextView) itemView.findViewById(R.id.temp_blink);
        press_blink = (TextView) itemView.findViewById(R.id.press_blink);
        humid_blink = (TextView) itemView.findViewById(R.id.humid_blink);
        wind_blink = (TextView) itemView.findViewById(R.id.wind_blink);

        temp_morn_blink = (TextView) itemView.findViewById(R.id.temp_morn_blink);
        temp_day_blink = (TextView) itemView.findViewById(R.id.temp_day_blink);
        temp_eve_blink = (TextView) itemView.findViewById(R.id.temp_eve_blink);
        temp_night_blink = (TextView) itemView.findViewById(R.id.temp_night_blink);

        temp_alert_descr = (TextView) itemView.findViewById(R.id.temp_alert_descr);
        press_alert_descr = (TextView) itemView.findViewById(R.id.press_alert_descr);
        humid_alert_descr = (TextView) itemView.findViewById(R.id.humid_alert_descr);
        wind_alert_descr = (TextView) itemView.findViewById(R.id.wind_alert_descr);

        if (isHistory) {
            day_temp_ll.setVisibility(View.GONE);
        } else {
            day_temp_ll.setVisibility(View.VISIBLE);
        }


        long dv = Long.valueOf(resultp.get(Constants.DATE)) * 1000;
        Date df = new java.util.Date(dv);
        if (isHistory) {
            date_string = new SimpleDateFormat("dd MMM yyyy,\ncccc HH:mm:ss", Locale.getDefault()).format(df);
            date.setText(date_string);
        } else {
            date_string = new SimpleDateFormat("dd MMM,\ncccc", Locale.getDefault()).format(df);
            date.setText(date_string);
        }


        double temp_day_double = Double.valueOf(resultp.get(Constants.TEMP_DAY));
        if (Math.round(temp_day_double) > 0) {
            temp_day.setText("+" + String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
            day_temp.setText("+" + String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
            day_temp.setTextColor(context.getResources().getColor(R.color.red));
        } else if (Math.round(temp_day_double) < 0) {
            temp_day.setText(String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
            day_temp.setText(String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
            day_temp.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (Math.round(temp_day_double) == 0) {
            temp_day.setText(String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
            day_temp.setText(String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_min_double = Double.valueOf(resultp.get(Constants.TEMP_MIN));
        if (Math.round(temp_min_double) > 0) {
            temp_min.setText("+" + String.valueOf(Math.round(temp_min_double)) + context.getResources().getString(R.string.cels));
            temp_min.setTextColor(context.getResources().getColor(R.color.red));
        } else if (Math.round(temp_min_double) < 0) {
            temp_min.setText(String.valueOf(Math.round(temp_min_double)) + context.getResources().getString(R.string.cels));
            temp_min.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (Math.round(temp_min_double) == 0) {
            temp_min.setText(String.valueOf(Math.round(temp_min_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_max_double = Double.valueOf(resultp.get(Constants.TEMP_MAX));
        if (Math.round(temp_max_double) > 0) {
            temp_max.setText("+" + String.valueOf(Math.round(temp_max_double)) + context.getResources().getString(R.string.cels));
            temp_max.setTextColor(context.getResources().getColor(R.color.red));
        } else if (Math.round(temp_max_double) < 0) {
            temp_max.setText(String.valueOf(Math.round(temp_max_double)) + context.getResources().getString(R.string.cels));
            temp_max.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (Math.round(temp_max_double) == 0) {
            temp_max.setText(String.valueOf(Math.round(temp_max_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_morn_double = Double.valueOf(resultp.get(Constants.TEMP_MORN));
        if (Math.round(temp_morn_double) > 0) {
            temp_morn.setText("+" + String.valueOf(Math.round(temp_morn_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_morn_double) < 0) {
            temp_morn.setText(String.valueOf(Math.round(temp_morn_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_morn_double) == 0) {
            temp_morn.setText(String.valueOf(Math.round(temp_morn_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_eve_double = Double.valueOf(resultp.get(Constants.TEMP_EVE));
        if (Math.round(temp_eve_double) > 0) {
            temp_eve.setText("+" + String.valueOf(Math.round(temp_eve_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_eve_double) < 0) {
            temp_eve.setText(String.valueOf(Math.round(temp_eve_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_eve_double) == 0) {
            temp_eve.setText(String.valueOf(Math.round(temp_eve_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_night_double = Double.valueOf(resultp.get(Constants.TEMP_NIGHT));
        if (Math.round(temp_night_double) > 0) {
            temp_night.setText("+" + String.valueOf(Math.round(temp_night_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_night_double) < 0) {
            temp_night.setText(String.valueOf(Math.round(temp_night_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_night_double) == 0) {
            temp_night.setText(String.valueOf(Math.round(temp_night_double)) + context.getResources().getString(R.string.cels));
        }

        forecast.setText(resultp.get(Constants.FORECAST));
        humidity.setText(context.getResources().getString(R.string.humidity) + " " + resultp.get(Constants.HUMIDITY) + "%");
        pressure.setText(context.getResources().getString(R.string.pressure) + " " + resultp.get(Constants.PRESSURE) + " " + context.getResources().getString(R.string.mm));
        wind.setText(context.getResources().getString(R.string.wind) + " " + resultp.get(Constants.WIND) + " " + context.getResources().getString(R.string.ms));
        imageLoader.DisplayImage(context.getResources().getString(R.string.icon_url) + resultp.get(Constants.ICON) + ".png", icon);


        if (chkbox_temp) {
            if (temp_day_double <= Float.parseFloat(temp_alert_min) || temp_day_double >= Float.parseFloat(temp_alert_max)) {
                temp_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(temp_blink);
                temp_day_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(temp_day_blink);
                temp_alert_descr.setVisibility(View.VISIBLE);
            } else {
                temp_blink.setVisibility(View.GONE);
                temp_day_blink.setVisibility(View.GONE);
            }
        
            if (temp_morn_double <= Float.parseFloat(temp_alert_min) || temp_morn_double >= Float.parseFloat(temp_alert_max)) {
                temp_morn_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(temp_morn_blink);
                temp_alert_descr.setVisibility(View.VISIBLE);
            } else {
                temp_morn_blink.setVisibility(View.GONE);
            }

            if (temp_eve_double <= Float.parseFloat(temp_alert_min) || temp_eve_double >= Float.parseFloat(temp_alert_max)) {
                temp_eve_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(temp_eve_blink);
                temp_alert_descr.setVisibility(View.VISIBLE);
            } else {
                temp_eve_blink.setVisibility(View.GONE);
            }

            if (temp_night_double <= Float.parseFloat(temp_alert_min) || temp_night_double >= Float.parseFloat(temp_alert_max)) {
                temp_night_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(temp_night_blink);
                temp_alert_descr.setVisibility(View.VISIBLE);
            } else {
                temp_night_blink.setVisibility(View.GONE);
            }
        }

        if (chkbox_press) {
            if (Float.parseFloat(resultp.get(Constants.PRESSURE)) <= Float.parseFloat(press_alert_min) || Float.parseFloat(resultp.get(Constants.PRESSURE)) >= Float.parseFloat(press_alert_max)) {
                press_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(press_blink);
                press_alert_descr.setVisibility(View.VISIBLE);
            } else {
                press_blink.setVisibility(View.GONE);
            }
        }

        if (chkbox_humid) {
            if (Float.parseFloat(resultp.get(Constants.HUMIDITY)) <= Float.parseFloat(humid_alert_min) || Float.parseFloat(resultp.get(Constants.HUMIDITY)) >= Float.parseFloat(humid_alert_max)) {
                humid_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(humid_blink);
                humid_alert_descr.setVisibility(View.VISIBLE);
            } else {
                humid_blink.setVisibility(View.GONE);
            }
        }

        if (chkbox_wind) {
            if (Float.parseFloat(resultp.get(Constants.WIND)) <= Float.parseFloat(wind_alert_min) || Float.parseFloat(resultp.get(Constants.WIND)) >= Float.parseFloat(wind_alert_max)) {
                wind_blink.setVisibility(View.VISIBLE);
                ((MainActivity)context).blink(wind_blink);
                wind_alert_descr.setVisibility(View.VISIBLE);
            } else {
                wind_blink.setVisibility(View.GONE);
            }
        }


        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                details.setVisibility(View.VISIBLE);
                details.setAnimation(Animations.fade_in());
            }
        });

        if (position % 2 == 0) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.grey));
        }

        return itemView;
    }

}
