package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TrainingIDE extends Activity{

    Button bIf, bWhile, bFor, bString, bProcedure, bInt;
    TextView console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_ide);

        bIf = (Button) findViewById(R.id.bIf);
        bWhile = (Button) findViewById(R.id.bWhile);
        bFor = (Button) findViewById(R.id.bFor);
        bString = (Button) findViewById(R.id.bString);
        bProcedure = (Button) findViewById(R.id.bProcedure);
        bInt = (Button) findViewById(R.id.bInt);
        console = (TextView) findViewById(R.id.tvConsole);

        bIf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

            }
        });
        bWhile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                console.setText(String.format("while (<condition>) {\n\n  <statement>\n\n}"));
            }
        });
        bFor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

            }
        });
        bString.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

            }
        });
        bProcedure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

            }
        });
        bInt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

            }
        });


//
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                counter++;
//                display.setText("You total is "+ counter);
//            }
//        });
//
//        sub.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                counter--;
//                display.setText("You total is "+ counter);
//            }
//        });
    }
}
