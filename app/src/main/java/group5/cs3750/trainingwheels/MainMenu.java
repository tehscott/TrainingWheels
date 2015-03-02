package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import group5.cs3750.trainingwheels.programmingobjects.ProgrammingObject;

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

        findViewById(R.id.btnOpenProgram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSavedFilesList();
            }
        });

        findViewById(R.id.btnNewProgram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, TrainingIDE.class);
                startActivity(user);
            }
        });

        findViewById(R.id.btnNewProgram).setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_green, 12, 0));
        findViewById(R.id.btnOpenProgram).setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_purple, 12, 0));
    }

    private void loadSavedFilesList() {
        final String[] files = fileList();
        new AlertDialog.Builder(this)
                .setTitle("Select saved file")
                .setItems(files, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            ObjectInputStream ois = new ObjectInputStream(openFileInput((files[i])));
                            //noinspection unchecked
                            ArrayList<ProgrammingObject> list = (ArrayList<ProgrammingObject>) ois.readObject();

                            Intent intent = new Intent(MainMenu.this, TrainingIDE.class);
                            intent.putExtra(TrainingIDE.PROGRAMMING_OBJECT_LIST, list);

                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create()
                .show();
    }
}
