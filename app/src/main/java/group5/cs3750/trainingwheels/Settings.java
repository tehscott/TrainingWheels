package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.Map;
import java.util.Set;

/**
 * Created by Zachary on 11/12/2014.
 */
public class Settings extends Activity {

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    public boolean sound;
    public boolean hints;
    public String difficulty;
    public Switch soundSwitch;
    public Switch hintsSwitch;
    public Spinner difficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = settings.edit();

        sound = settings.getBoolean("sound", true);
        hints = settings.getBoolean("hints", false);
        difficulty = settings.getString("difficulty", "Beginner");

        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        hintsSwitch = (Switch) findViewById(R.id.hintsSwitch);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);

        soundSwitch.setChecked(sound);
        hintsSwitch.setChecked(hints);
        if (difficulty.equals("Beginner"))
            difficultySpinner.setSelection(0);
        else if (difficulty.equals("Amateur"))
            difficultySpinner.setSelection(1);
        else
            difficultySpinner.setSelection(2);

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] difficulties = getResources().getStringArray(R.array.settingsSpinnerArray);

                difficulty = difficulties[position];
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                sound = isChecked;
            }
        });

        hintsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                hints = isChecked;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        editor.putBoolean("sound", sound);
        editor.putBoolean("hints", hints);
        editor.putString("difficulty", difficulty);
        editor.commit();
    }
}
