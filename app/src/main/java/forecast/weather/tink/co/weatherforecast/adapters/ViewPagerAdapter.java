package forecast.weather.tink.co.weatherforecast.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import forecast.weather.tink.co.weatherforecast.fragments.TodayFragment;
import forecast.weather.tink.co.weatherforecast.fragments.WeekFragment;

/**
 * Created by Повелитель on 22.10.2016.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = {"Сегодня", "Неделя"};

    public ViewPagerAdapter(FragmentManager childFragmentManager) {
        super(childFragmentManager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int index) {
        Fragment fragment = null;
        if (index == 0) {
            fragment = new TodayFragment();
        } else if (index == 1) {
            fragment = new WeekFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}