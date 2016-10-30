package forecast.weather.tink.co.weatherforecast.helpers;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class UpdatingListPreference extends android.support.v7.preference.ListPreference
{
    public UpdatingListPreference(Context context)
    {
        super(context);
    }

    public UpdatingListPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void setValue( final String value )
    {
        super.setValue(value);
        notifyChanged();
    }
}
