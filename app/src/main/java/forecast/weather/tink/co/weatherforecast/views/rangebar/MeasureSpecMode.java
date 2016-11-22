package forecast.weather.tink.co.weatherforecast.views.rangebar;

import android.view.View;

public enum MeasureSpecMode {

    AT_MOST(View.MeasureSpec.AT_MOST),
    EXACTLY(View.MeasureSpec.EXACTLY),
    UNSPECIFIED(View.MeasureSpec.UNSPECIFIED);

    private final int mModeValue;

    MeasureSpecMode(int modeValue) {
        mModeValue = modeValue;
    }

    public int getModeValue() {
        return mModeValue;
    }

    public static MeasureSpecMode getMode(int measureSpec) {

        final int modeValue = View.MeasureSpec.getMode(measureSpec);

        for (MeasureSpecMode mode : MeasureSpecMode.values()) {
            if (mode.getModeValue() == modeValue) {
                return mode;
            }
        }
        return null;
    }
}
