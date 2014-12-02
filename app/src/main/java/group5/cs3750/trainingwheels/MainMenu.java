package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Brady on 11/13/2014.
 */
public class MainMenu extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        initButtons();
    }

    private void initButtons() {
        findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(MainMenu.this, Settings.class);
                startActivity(settings);
            }
        });

        findViewById(R.id.btnContinueGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, TrainingIDE.class);
                startActivity(user);
            }
        });

        findViewById(R.id.btnStartNewGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, TrainingIDE.class);
                startActivity(user);
            }
        });

        findViewById(R.id.btnStats).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, User.class);
                startActivity(user);
            }
        });
    }
}
