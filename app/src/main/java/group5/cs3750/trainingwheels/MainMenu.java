package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

        findViewById(R.id.btnNewProgram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, TrainingIDE.class);
                startActivity(user);
            }
        });

        findViewById(R.id.btnOpenProgram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, TrainingIDE.class);
                startActivity(user);
            }
        });

        findViewById(R.id.javascript_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, JavaScriptDemo.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnNewProgram).setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_green, 12));
        findViewById(R.id.btnOpenProgram).setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_purple, 12));
        findViewById(R.id.javascript_demo).setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_red, 12));
    }
}
