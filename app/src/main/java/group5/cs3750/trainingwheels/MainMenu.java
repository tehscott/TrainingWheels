package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import group5.cs3750.trainingwheels.programmingobjects.ProgrammingObject;

/**
 * Created by Brady on 11/13/2014.
 */
public class MainMenu extends Activity {
    private AlertDialog loadDialog;
    private View loadButton;

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

        loadButton = findViewById(R.id.btnOpenProgram);
        View newButton = findViewById(R.id.btnNewProgram);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSavedFilesList();
            }
        });
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainMenu.this, TrainingIDE.class);
                startActivity(user);
            }
        });

        newButton.setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_green, 12, 0));
        loadButton.setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_purple, 12, 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoadButton();
    }

    private void checkLoadButton() {
        if (fileList().length == 0) {
            loadButton.setEnabled(false);
            loadButton.setClickable(false);
        } else {
            loadButton.setEnabled(true);
            loadButton.setClickable(true);
        }
    }

    private void loadSavedFilesList() {
        final String[] files = fileList();
        View fileView = View.inflate(this, R.layout.load_dialog, null);
        ListView listView = (ListView) fileView.findViewById(R.id.list_view);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files);

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
                                checkLoadButton();
                            }
                        })
                        .create()
                        .show();
                if (loadDialog != null) {
                    loadDialog.dismiss();
                }
                return true;
            }
        });

        loadDialog = new AlertDialog.Builder(this)
                .setTitle("Select saved file")
                .setView(fileView)
                .create();
        loadDialog.show();
    }
}
