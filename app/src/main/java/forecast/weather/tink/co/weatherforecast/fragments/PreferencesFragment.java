package forecast.weather.tink.co.weatherforecast.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import forecast.weather.tink.co.weatherforecast.R;
import forecast.weather.tink.co.weatherforecast.activities.MainActivity;
import forecast.weather.tink.co.weatherforecast.helpers.PathUtils;
import forecast.weather.tink.co.weatherforecast.services.NotificationService;
import forecast.weather.tink.co.weatherforecast.views.rangebar.RangeBar;

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    AutoCompleteTextView autocomplete;
    RangeBar rangebar_temp, rangebar_press, rangebar_humid, rangebar_wind, rangebar_refresh;
    TextView temp_alert_min, temp_alert_max,
            humid_alert_min, humid_alert_max,
            press_alert_min, press_alert_max,
            wind_alert_min, wind_alert_max,
            refresh_rate;

    CheckBox chkbox_temp, chkbox_press, chkbox_humid, chkbox_wind;

    String pref_temp_alert_min, pref_temp_alert_max,
            pref_humid_alert_min, pref_humid_alert_max,
            pref_press_alert_min, pref_press_alert_max,
            pref_wind_alert_min, pref_wind_alert_max,
            pref_refresh_range;

    SharedPreferences sharedPreferences;

    String filename;

    ArrayAdapter<String> autocompleteTextAdapter;
    String[] cityArray, idArray;

    int chkbox_count = 0;
    public static int PICKFILE_RESULT_CODE;

    private static String EXPORT_PATH = Environment.getExternalStorageDirectory() + "/WeatherForecast/";
    private static final String EXPORT_NAME = "weatherforecastprefs.xml";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPreferences, "night_mode");
        onSharedPreferenceChanged(sharedPreferences, "import_settings");
        onSharedPreferenceChanged(sharedPreferences, "export_settings");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_views(view);
        init_prefs();
        pref_autocomplete();
        pref_chkboxes();
        pref_temp_alert_range();
        pref_press_alert_range();
        pref_humid_alert_range();
        pref_wind_alert_range();
        pref_refresh_range();

    }

    public void init_views(View view) {
        autocomplete = (AutoCompleteTextView) view.findViewById(R.id.autoComplete);
        rangebar_temp = (RangeBar) view.findViewById(R.id.rangebar_temp);
        rangebar_press = (RangeBar) view.findViewById(R.id.rangebar_press);
        rangebar_humid = (RangeBar) view.findViewById(R.id.rangebar_humid);
        rangebar_wind = (RangeBar) view.findViewById(R.id.rangebar_wind);
        rangebar_refresh = (RangeBar) view.findViewById(R.id.rangebar_refresh);

        chkbox_temp = (CheckBox) view.findViewById(R.id.chkbox_temp);
        chkbox_press = (CheckBox) view.findViewById(R.id.chkbox_press);
        chkbox_humid = (CheckBox) view.findViewById(R.id.chkbox_humid);
        chkbox_wind = (CheckBox) view.findViewById(R.id.chkbox_wind);

        temp_alert_min = (TextView) view.findViewById(R.id.temp_alert_min);
        temp_alert_max = (TextView) view.findViewById(R.id.temp_alert_max);

        press_alert_min = (TextView) view.findViewById(R.id.press_alert_min);
        press_alert_max = (TextView) view.findViewById(R.id.press_alert_max);

        humid_alert_min = (TextView) view.findViewById(R.id.humid_alert_min);
        humid_alert_max = (TextView) view.findViewById(R.id.humid_alert_max);

        wind_alert_min = (TextView) view.findViewById(R.id.wind_alert_min);
        wind_alert_max = (TextView) view.findViewById(R.id.wind_alert_max);

        refresh_rate = (TextView) view.findViewById(R.id.refresh_rate);
    }

    public void init_prefs() {

        chkbox_temp.setChecked(sharedPreferences.getBoolean("chkbox_temp", true));
        chkbox_press.setChecked(sharedPreferences.getBoolean("chkbox_press", true));
        chkbox_humid.setChecked(sharedPreferences.getBoolean("chkbox_humid", true));
        chkbox_wind.setChecked(sharedPreferences.getBoolean("chkbox_wind", true));

        rangebar_temp.setEnabled(sharedPreferences.getBoolean("chkbox_temp", true));
        rangebar_press.setEnabled(sharedPreferences.getBoolean("chkbox_press", true));
        rangebar_humid.setEnabled(sharedPreferences.getBoolean("chkbox_humid", true));
        rangebar_wind.setEnabled(sharedPreferences.getBoolean("chkbox_wind", true));

        if (sharedPreferences.getBoolean("chkbox_temp", true)) {
            chkbox_count++;
        }
        if (sharedPreferences.getBoolean("chkbox_press", true)) {
            chkbox_count++;
        }
        if (sharedPreferences.getBoolean("chkbox_humid", true)) {
            chkbox_count++;
        }
        if (sharedPreferences.getBoolean("chkbox_wind", true)) {
            chkbox_count++;
        }

        pref_temp_alert_min = sharedPreferences.getString("temp_alert_min", "");
        pref_temp_alert_max = sharedPreferences.getString("temp_alert_max", "");

        pref_press_alert_min = sharedPreferences.getString("press_alert_min", "");
        pref_press_alert_max = sharedPreferences.getString("press_alert_max", "");

        pref_humid_alert_min = sharedPreferences.getString("humid_alert_min", "");
        pref_humid_alert_max = sharedPreferences.getString("humid_alert_max", "");

        pref_wind_alert_min = sharedPreferences.getString("wind_alert_min", "");
        pref_wind_alert_max = sharedPreferences.getString("wind_alert_max", "");

        pref_refresh_range = sharedPreferences.getString("refresh_range", "");


        if (pref_temp_alert_min.length() != 0 && pref_temp_alert_max.length() != 0) {
            rangebar_temp.setRangePinsByValue(Float.parseFloat(pref_temp_alert_min), Float.parseFloat(pref_temp_alert_max));
            temp_alert_min.setText(pref_temp_alert_min);
            temp_alert_max.setText(pref_temp_alert_max);
        } else {
            temp_alert_min.setText(String.valueOf(getResources().getInteger(R.integer.temp_min)));
            temp_alert_max.setText(String.valueOf(getResources().getInteger(R.integer.temp_max)));
            sharedPreferences.edit().putString("temp_alert_min", String.valueOf(getResources().getInteger(R.integer.temp_min))).commit();
            sharedPreferences.edit().putString("temp_alert_max", String.valueOf(getResources().getInteger(R.integer.temp_max))).commit();
        }

        if (pref_press_alert_min.length() != 0 && pref_press_alert_max.length() != 0) {
            rangebar_press.setRangePinsByValue(Float.parseFloat(pref_press_alert_min), Float.parseFloat(pref_press_alert_max));
            press_alert_min.setText(pref_press_alert_min);
            press_alert_max.setText(pref_press_alert_max);
        } else {
            press_alert_min.setText(String.valueOf(getResources().getInteger(R.integer.press_min)));
            press_alert_max.setText(String.valueOf(getResources().getInteger(R.integer.press_max)));
            sharedPreferences.edit().putString("press_alert_min", String.valueOf(getResources().getInteger(R.integer.press_min))).commit();
            sharedPreferences.edit().putString("press_alert_max", String.valueOf(getResources().getInteger(R.integer.press_max))).commit();
        }

        if (pref_humid_alert_min.length() != 0 && pref_humid_alert_max.length() != 0) {
            rangebar_humid.setRangePinsByValue(Float.parseFloat(pref_humid_alert_min), Float.parseFloat(pref_humid_alert_max));
            humid_alert_min.setText(pref_humid_alert_min);
            humid_alert_max.setText(pref_humid_alert_max);
        } else {
            humid_alert_min.setText(String.valueOf(getResources().getInteger(R.integer.humid_min)));
            humid_alert_max.setText(String.valueOf(getResources().getInteger(R.integer.humid_max)));
            sharedPreferences.edit().putString("humid_alert_min", String.valueOf(getResources().getInteger(R.integer.humid_min))).commit();
            sharedPreferences.edit().putString("humid_alert_max", String.valueOf(getResources().getInteger(R.integer.humid_max))).commit();
        }

        if (pref_wind_alert_min.length() != 0 && pref_wind_alert_max.length() != 0) {
            rangebar_wind.setRangePinsByValue(Float.parseFloat(pref_wind_alert_min), Float.parseFloat(pref_wind_alert_max));
            wind_alert_min.setText(pref_wind_alert_min);
            wind_alert_max.setText(pref_wind_alert_max);
        } else {
            wind_alert_min.setText(String.valueOf(getResources().getInteger(R.integer.wind_min)));
            wind_alert_max.setText(String.valueOf(getResources().getInteger(R.integer.wind_max)));
            sharedPreferences.edit().putString("wind_alert_min", String.valueOf(getResources().getInteger(R.integer.wind_min))).commit();
            sharedPreferences.edit().putString("wind_alert_max", String.valueOf(getResources().getInteger(R.integer.wind_max))).commit();
        }

        if (pref_refresh_range.length() != 0) {
            rangebar_refresh.setRangePinsByValue(1, Float.parseFloat(pref_refresh_range));
            refresh_rate.setText(pref_refresh_range);
        } else {
            refresh_rate.setText(String.valueOf(getResources().getInteger(R.integer.refresh_min)));
            rangebar_refresh.setRangePinsByValue(1, 1);
            sharedPreferences.edit().putString("refresh_range", String.valueOf(getResources().getInteger(R.integer.refresh_min))).commit();
        }


    }

    public void pref_refresh_range() {

        rangebar_refresh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    sharedPreferences.edit().putString("refresh_range", rangebar_refresh.getRightPinValue()).commit();
                    getActivity().stopService(new Intent(getActivity(), NotificationService.class));

                    Intent intent = new Intent(getActivity(), NotificationService.class);
                    intent.putExtra("refresh_range", Integer.parseInt(rangebar_refresh.getRightPinValue()));
                    getActivity().startService(intent);

                }
                return false;
            }
        });


        rangebar_refresh.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, final String rightPinValue) {

                refresh_rate.setText(rightPinValue);

            }
        });
    }

    public void pref_chkboxes() {

        chkbox_temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("chkbox_temp", isChecked).commit();
                rangebar_temp.setEnabled(isChecked);
                if (isChecked) {
                    chkbox_count++;
                } else {
                    if (chkbox_count > 1) {
                        chkbox_count--;
                    } else {
                        chkbox_temp.setChecked(true);
                        sharedPreferences.edit().putBoolean("chkbox_temp", true).commit();
                        rangebar_temp.setEnabled(true);

                        snack_bar(getResources().getString(R.string.warning_alerts));
                    }
                }
            }
        });

        chkbox_press.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("chkbox_press", isChecked).commit();
                rangebar_press.setEnabled(isChecked);
                if (isChecked) {
                    chkbox_count++;
                } else {
                    if (chkbox_count > 1) {
                        chkbox_count--;
                    } else {
                        chkbox_press.setChecked(true);
                        sharedPreferences.edit().putBoolean("chkbox_press", true).commit();
                        rangebar_press.setEnabled(true);

                        snack_bar(getResources().getString(R.string.warning_alerts));
                    }
                }
            }
        });

        chkbox_humid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("chkbox_humid", isChecked).commit();
                rangebar_humid.setEnabled(isChecked);
                if (isChecked) {
                    chkbox_count++;
                } else {
                    if (chkbox_count > 1) {
                        chkbox_count--;
                    } else {
                        chkbox_humid.setChecked(true);
                        sharedPreferences.edit().putBoolean("chkbox_humid", true).commit();
                        rangebar_humid.setEnabled(true);

                        snack_bar(getResources().getString(R.string.warning_alerts));
                    }
                }
            }
        });

        chkbox_wind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("chkbox_wind", isChecked).commit();
                rangebar_wind.setEnabled(isChecked);
                if (isChecked) {
                    chkbox_count++;
                } else {
                    if (chkbox_count > 1) {
                        chkbox_count--;
                    } else {
                        chkbox_wind.setChecked(true);
                        sharedPreferences.edit().putBoolean("chkbox_wind", true).commit();
                        rangebar_wind.setEnabled(true);

                        snack_bar(getResources().getString(R.string.warning_alerts));
                    }
                }
            }
        });
    }

    public void pref_autocomplete() {
        cityArray = getResources().getStringArray(R.array.cities);
        idArray = getResources().getStringArray(R.array.id_s);
        autocompleteTextAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, cityArray);

        autocomplete.setAdapter(autocompleteTextAdapter);
        autocomplete.setThreshold(0);

        autocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                int pos = -1;

                for (int i = 0; i < cityArray.length; i++) {
                    if (cityArray[i].equals(selection)) {
                        pos = i;
                        break;
                    }
                }

                sharedPreferences.edit().putString("city", idArray[pos]).commit();
            }
        });

        if (sharedPreferences.getString("city", "").length() != 0) {
            for (int i = 0; i < idArray.length; i++) {
                if (idArray[i].equals(sharedPreferences.getString("city", ""))) {
                    autocomplete.setText(cityArray[i]);
                    break;
                }
            }
        }
    }

    public void pref_temp_alert_range() {

        rangebar_temp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    sharedPreferences.edit().putString("temp_alert_min", rangebar_temp.getLeftPinValue()).commit();
                    sharedPreferences.edit().putString("temp_alert_max", rangebar_temp.getRightPinValue()).commit();

                }
                return false;
            }
        });

        rangebar_temp.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                temp_alert_min.setText(leftPinValue);
                temp_alert_max.setText(rightPinValue);

            }
        });
    }

    public void pref_press_alert_range() {

        rangebar_press.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    sharedPreferences.edit().putString("press_alert_min", rangebar_press.getLeftPinValue()).commit();
                    sharedPreferences.edit().putString("press_alert_max", rangebar_press.getRightPinValue()).commit();

                }
                return false;
            }
        });

        rangebar_press.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                press_alert_min.setText(leftPinValue);
                press_alert_max.setText(rightPinValue);

            }
        });
    }

    public void pref_humid_alert_range() {

        rangebar_humid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    sharedPreferences.edit().putString("humid_alert_min", rangebar_humid.getLeftPinValue()).commit();
                    sharedPreferences.edit().putString("humid_alert_max", rangebar_humid.getRightPinValue()).commit();

                }
                return false;
            }
        });

        rangebar_humid.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                humid_alert_min.setText(leftPinValue);
                humid_alert_max.setText(rightPinValue);

            }
        });
    }

    public void pref_wind_alert_range() {

        rangebar_wind.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    sharedPreferences.edit().putString("wind_alert_min", rangebar_wind.getLeftPinValue()).commit();
                    sharedPreferences.edit().putString("wind_alert_max", rangebar_wind.getRightPinValue()).commit();

                }
                return false;
            }
        });

        rangebar_wind.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                wind_alert_min.setText(leftPinValue);
                wind_alert_max.setText(rightPinValue);

            }
        });
    }

    private boolean exportPreferences(File destination) {

        boolean result = false;
        ObjectOutputStream output = null;

        try {

            output = new ObjectOutputStream(new FileOutputStream(destination));
            output.writeObject(sharedPreferences.getAll());

            result = true;

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {

            try {

                if (output != null) {
                    output.flush();
                    output.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    @SuppressWarnings({"unchecked"})
    private boolean importPreferences(File src) {

        boolean result = false;
        ObjectInputStream input = null;

        try {

            input = new ObjectInputStream(new FileInputStream(src));

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();

            Map<String, ?> entries = (Map<String, ?>) input.readObject();

            for (Map.Entry<String, ?> entry : entries.entrySet()) {

                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    editor.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    editor.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    editor.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    editor.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    editor.putString(key, ((String) v));
            }

            editor.commit();
            result = true;
            reload_prefs_screen("import_prefs");

        } catch (FileNotFoundException fnfexception) {
            fnfexception.printStackTrace();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        } catch (ClassNotFoundException cnfexception) {
            cnfexception.printStackTrace();
        } finally {

            try {

                if (input != null) {
                    input.close();
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }


        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == MainActivity.RESULT_OK) {
                try {
                    Uri uri = data.getData();

                    String mimeType = getContext().getContentResolver().getType(uri);
                    if (mimeType == null) {
                        String path = PathUtils.getPath(getContext(), uri);
                        if (path == null) {
                            filename = FilenameUtils.getName(uri.toString());
                        } else {
                            File file = new File(path);
                            filename = file.getName();
                        }
                    } else {
                        Uri returnUri = data.getData();
                        Cursor returnCursor = getContext().getContentResolver().query(returnUri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        filename = returnCursor.getString(nameIndex);
                        String size = Long.toString(returnCursor.getLong(sizeIndex));
                    }
                    File fileSave = getContext().getExternalFilesDir(null);
                    String sourcePath = getContext().getExternalFilesDir(null).toString();
                    try {
                        PathUtils.copyFileStream(new File(sourcePath + "/" + filename), uri, getContext());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    importPreferences(new File(fileSave + "/" + filename));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, final String key) {
        Preference preference = findPreference(key);

        if (preference instanceof SwitchPreferenceCompat) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    reload_prefs_screen("night_mode");
                    return false;
                }
            });
        } else if (preference instanceof Preference) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (key.equals("export_settings")) {

                        File expDir = new File(EXPORT_PATH);
                        expDir.mkdirs();
                        File file = new File(expDir, EXPORT_NAME);
                        if (file.exists()) file.delete();

                        exportPreferences(file);
                        snack_bar(getResources().getString(R.string.export_done));

                    } else if (key.equals("import_settings")) {

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), PICKFILE_RESULT_CODE);
                        } catch (Exception ex) {

                        }
                    }
                    return false;
                }
            });
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void snack_bar(String string) {
        Snackbar.make(MainActivity.content_frame, string, Snackbar.LENGTH_LONG).show();
    }

    public void reload_prefs_screen(String string) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("show_prefs", string);
        getActivity().finish();
        startActivity(intent);
    }
}