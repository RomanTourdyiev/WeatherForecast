package forecast.weather.tink.co.weatherforecast.helpers;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import forecast.weather.tink.co.weatherforecast.R;

/**
 * Created by Cantador on 29.10.16.
 */

public class DialogPreferenceNew extends DialogPreference {

    public DialogPreferenceNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DialogPreferenceNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        // load shared preferences
        // init views
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // save shared preferences
        }
    }

    private void init(Context context) {
        setPersistent(false);
        setDialogLayoutResource(R.layout.layout_autocomplete);
    }
}