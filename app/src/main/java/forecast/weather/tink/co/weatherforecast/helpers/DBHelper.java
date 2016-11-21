package forecast.weather.tink.co.weatherforecast.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Created by Cantador on 05.10.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH = Environment.getExternalStorageDirectory() + "/WeatherForecast/";
    private static final String DB_NAME = "weatherforecast.sqlite";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_PATH + DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists city_table ("
                + "_id integer primary key autoincrement,"
                + "city text,"
                + "date long,"
                + "temp double,"
                + "temp_min double,"
                + "temp_max double,"
                + "icon text,"
                + "forecast text,"
                + "humidity integer,"
                + "pressure double,"
                + "wind double" + ");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
