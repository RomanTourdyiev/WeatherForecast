package forecast.weather.tink.co.weatherforecast.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Flash on 17.06.2016.
 */
public class NetworkCheck {

    public static boolean connected = false;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        return connected;
    }

}
