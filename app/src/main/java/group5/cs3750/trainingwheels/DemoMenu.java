package group5.cs3750.trainingwheels;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DemoMenu extends ListActivity {

    String activities[] = {"Splash","MainMenu","Tutorial","TrainingIDE","Settings","task5","task6","task7","task8"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(DemoMenu.this, android.R.layout.simple_list_item_1, activities));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String activity = activities[position];
        super.onListItemClick(l, v, position, id);
        try {
            Class ourClass = Class.forName("group5.cs3750.trainingwheels." + activity);
            Intent ourIntent = new Intent(DemoMenu.this, ourClass);
            startActivity(ourIntent);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}