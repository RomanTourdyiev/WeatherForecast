package forecast.weather.tink.co.weatherforecast.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.widget.AutoCompleteTextView;

import forecast.weather.tink.co.weatherforecast.R;

public class AutoCompletePreference extends Preference {
    AutoCompleteTextView autoComplete;
    String[] arrayCities =

            {
                    "a", "aaa", "aabb", "b", "bbc", "cbb", "c", "cdd", "caa", "d", "ddc", "dda", "e", "eea", "ebc", "aec"
            };


    public AutoCompletePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public AutoCompletePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.layout_autocomplete);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder viewHolder) {

        super.onBindViewHolder(viewHolder);

        viewHolder.itemView.setClickable(false);


//        arrayCities = getContext().getResources().getStringArray(R.array.cities);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getContext(),android.R.layout.select_dialog_item, arrayCities);

        autoComplete.setThreshold(2);
        autoComplete.setAdapter(adapter);
    }




}