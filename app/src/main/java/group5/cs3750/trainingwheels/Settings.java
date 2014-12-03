package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
    SharedPreferences.Editor editor;
    public boolean sound;
    public boolean hints;
    public String difficulty;
    public Switch soundSwitch;
    public Switch hintsSwitch;
    public Spinner difficultySpinner;
    public Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        sound = settings.getBoolean("sound", true);
        hints = settings.getBoolean("hints", false);
        difficulty = settings.getString("difficulty", "Beginner");

        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        hintsSwitch = (Switch) findViewById(R.id.hintsSwitch);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        saveButton = (Button) findViewById(R.id.saveButton);

        soundSwitch.setChecked(sound);
        hintsSwitch.setChecked(hints);
        if (difficulty.equals("Beginner"))
            difficultySpinner.setSelection(0);
        else if (difficulty.equals("Amateur"))
            difficultySpinner.setSelection(1);
        else
            difficultySpinner.setSelection(2);

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    sound = true;
                } else {
                    sound = false;
                }
            }
        });

        hintsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    hints = true;
                } else {
                    hints = false;
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.putBoolean("sound", sound);
                editor.putBoolean("hints", hints);
                editor.putString("difficulty", difficulty);
                editor.commit();
            }
        });
    }
}
