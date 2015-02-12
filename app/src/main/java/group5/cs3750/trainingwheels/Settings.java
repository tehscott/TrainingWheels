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
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean sound;
    private boolean hints;
    private Switch soundSwitch;
    private Switch hintsSwitch;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = settings.edit();

        sound = settings.getBoolean("sound", true);
        hints = settings.getBoolean("hints", false);

        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        hintsSwitch = (Switch) findViewById(R.id.hintsSwitch);
        backButton = (Button) findViewById(R.id.back_button);

        soundSwitch.setChecked(sound);
        hintsSwitch.setChecked(hints);

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        editor.putBoolean("sound", sound);
        editor.putBoolean("hints", hints);
        editor.commit();
    }
}
