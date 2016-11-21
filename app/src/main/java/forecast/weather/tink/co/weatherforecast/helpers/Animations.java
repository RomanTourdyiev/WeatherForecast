package forecast.weather.tink.co.weatherforecast.helpers;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Повелитель on 23.10.2016.
 */

public class Animations {

    public static Animation fade_in() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        return animation;
    }

    public static Animation blink(){
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        return animation;
    }

}
