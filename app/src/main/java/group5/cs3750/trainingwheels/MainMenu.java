package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;

import java.io.File;
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

        initDrawer();
        initButtons();
    }

    private void initDrawer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final SharedPreferences.Editor editor = prefs.edit();

        boolean sound = prefs.getBoolean("sound", true);
        boolean hints = prefs.getBoolean("hints", true);

        Switch soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        Switch hintsSwitch = (Switch) findViewById(R.id.hintsSwitch);

        soundSwitch.setChecked(sound);
        hintsSwitch.setChecked(hints);

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("sound", isChecked);
            }
        });

        hintsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hints", isChecked);
            }
        });
    }

    private void initButtons() {
        findViewById(R.id.main_settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

                if(drawerLayout.isDrawerOpen(Gravity.RIGHT))
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                else
                    drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });

        findViewById(R.id.main_addButtonMiddle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, TrainingIDE.class);
                startActivity(user);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSavedFilesList();
    }

    private void loadSavedFilesList() {
        final String[] files = fileList();
        ListView listView = (ListView) findViewById(R.id.main_programList);
        ListAdapter adapter = new ProgramsListAdapter(MainMenu.this, files);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(openFileInput((files[position])));
                    //noinspection unchecked
                    ArrayList<ProgrammingObject> list = (ArrayList<ProgrammingObject>) ois.readObject();

                    Intent intent = new Intent(MainMenu.this, TrainingIDE.class);
                    intent.putExtra(TrainingIDE.PROGRAMMING_OBJECT_LIST, list);
                    intent.putExtra(TrainingIDE.OPENED_FILE_NAME, files[position]);

                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainMenu.this)
                        .setTitle("Select Action")
                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final View renameView = View.inflate(MainMenu.this, R.layout.rename_dialog, null);

                                new AlertDialog.Builder(MainMenu.this)
                                        .setTitle("Rename " + files[position])
                                        .setView(renameView)
                                        .setNeutralButton(android.R.string.cancel, null)
                                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newTitle = ((EditText) renameView.findViewById(R.id.newTitle)).getText().toString();
                                                File oldFile = getFileStreamPath(files[position]);
                                                File newFile = new File(oldFile.getParent(), newTitle);
                                                oldFile.renameTo(newFile);

                                            }
                                        })
                                        .create()
                                        .show();
                            }
                        })
                        .setNeutralButton(android.R.string.cancel, null)
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFile(files[position]);

                                loadSavedFilesList();
                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });
    }
}
