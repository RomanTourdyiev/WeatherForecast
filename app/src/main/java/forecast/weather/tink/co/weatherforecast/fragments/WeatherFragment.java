package forecast.weather.tink.co.weatherforecast.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.adapters.ViewPagerAdapter;

public class WeatherFragment extends android.support.v4.app.Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;

    public WeatherFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager.setAdapter(buildAdapter());

        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }



    private PagerAdapter buildAdapter() {
        return (new ViewPagerAdapter(getChildFragmentManager()));
    }

}
