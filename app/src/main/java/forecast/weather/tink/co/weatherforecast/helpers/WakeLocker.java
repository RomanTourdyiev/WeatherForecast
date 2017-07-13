package forecast.weather.tink.co.weatherforecast.helpers;

import android.content.Context;
import android.os.PowerManager;

import forecast.weather.tink.co.weatherforecast.activities.MainActivity;

public abstract class WakeLocker {
  private static PowerManager.WakeLock wakeLock;

  public static void acquire(Context context) {
    if (wakeLock != null) wakeLock.release();

    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
        PowerManager.ACQUIRE_CAUSES_WAKEUP |
        PowerManager.ON_AFTER_RELEASE, ((MainActivity)context).getAppTag());
    wakeLock.acquire();
  }

  public static void release() {
    if (wakeLock != null) wakeLock.release();
    wakeLock = null;
  }
}
