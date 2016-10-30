package forecast.weather.tink.co.weatherforecast.adapters;

import android.content.Context;
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
import forecast.weather.tink.co.weatherforecast.helpers.Animations;
import forecast.weather.tink.co.weatherforecast.helpers.ImageLoader;
import forecast.weather.tink.co.weatherforecast.items.Constants;


/**
 * Created by Flash on 09.04.2015.
 */
public class ListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    ImageLoader imageLoader;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context, ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
        imageLoader = new ImageLoader(context);
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

//    @Override
//    public int getViewTypeCount() {
//        return getCount();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView icon;
        final LinearLayout details;
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
                wind;

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


        long dv = Long.valueOf(resultp.get(Constants.DATE)) * 1000;
        Date df = new java.util.Date(dv);
        date_string = new SimpleDateFormat("dd MMMM yyyy, cccc", Locale.getDefault()).format(df);
        date.setText(date_string);

        double temp_day_double = Double.valueOf(resultp.get(Constants.TEMP_DAY));
        if (Math.round(temp_day_double) > 0) {
            temp_day.setText("+" + String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
            day_temp.setText("+" + String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
            day_temp.setTextColor(context.getResources().getColor(R.color.red));
        } else if (Math.round(temp_day_double) < 0) {
//            temp_day.setText("-" + String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
//            day_temp.setText("-" + String.valueOf(Math.round(temp_day_double)) + context.getResources().getString(R.string.cels));
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
//            temp_min.setText("-" + String.valueOf(Math.round(temp_min_double)) + context.getResources().getString(R.string.cels));
            temp_min.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (Math.round(temp_min_double) == 0) {
            temp_min.setText(String.valueOf(Math.round(temp_min_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_max_double = Double.valueOf(resultp.get(Constants.TEMP_MAX));
        if (Math.round(temp_max_double) > 0) {
            temp_max.setText("+" + String.valueOf(Math.round(temp_max_double)) + context.getResources().getString(R.string.cels));
            temp_max.setTextColor(context.getResources().getColor(R.color.red));
        } else if (Math.round(temp_max_double) < 0) {
//            temp_max.setText("-" + String.valueOf(Math.round(temp_max_double)) + context.getResources().getString(R.string.cels));
            temp_max.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (Math.round(temp_max_double) == 0) {
            temp_max.setText(String.valueOf(Math.round(temp_max_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_morn_double = Double.valueOf(resultp.get(Constants.TEMP_MORN));
        if (Math.round(temp_morn_double) > 0) {
            temp_morn.setText("+" + String.valueOf(Math.round(temp_morn_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_morn_double) < 0) {
//            temp_morn.setText("-" + String.valueOf(Math.round(temp_morn_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_morn_double) == 0) {
            temp_morn.setText(String.valueOf(Math.round(temp_morn_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_eve_double = Double.valueOf(resultp.get(Constants.TEMP_EVE));
        if (Math.round(temp_eve_double) > 0) {
            temp_eve.setText("+" + String.valueOf(Math.round(temp_eve_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_eve_double) < 0) {
//            temp_eve.setText("-" + String.valueOf(Math.round(temp_eve_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_eve_double) == 0) {
            temp_eve.setText(String.valueOf(Math.round(temp_eve_double)) + context.getResources().getString(R.string.cels));
        }

        double temp_night_double = Double.valueOf(resultp.get(Constants.TEMP_NIGHT));
        if (Math.round(temp_night_double) > 0) {
            temp_night.setText("+" + String.valueOf(Math.round(temp_night_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_night_double) < 0) {
//            temp_night.setText("-" + String.valueOf(Math.round(temp_night_double)) + context.getResources().getString(R.string.cels));
        } else if (Math.round(temp_night_double) == 0) {
            temp_night.setText(String.valueOf(Math.round(temp_night_double)) + context.getResources().getString(R.string.cels));
        }

        forecast.setText(resultp.get(Constants.FORECAST));
        humidity.setText(context.getResources().getString(R.string.humidity) + " " + resultp.get(Constants.HUMIDITY) + "%");
        pressure.setText(context.getResources().getString(R.string.pressure) + " " + resultp.get(Constants.PRESSURE) + " " + context.getResources().getString(R.string.mm));
        wind.setText(context.getResources().getString(R.string.wind) + " " + resultp.get(Constants.WIND) + " " + context.getResources().getString(R.string.ms));
        imageLoader.DisplayImage(context.getResources().getString(R.string.icon_url) + resultp.get(Constants.ICON) + ".png", icon);

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
