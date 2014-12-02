package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class TrainingIDE extends Activity{
    //
    Button bIf, bWhile, bFor, bString, bProcedure, bInt, bRun;
    EditText notePad;
    TextView console;
    Boolean forPressed = false;
    Boolean whilePressed = false;

    //StringBuilder notePadOut = new StringBuilder();
    String notePadOut;
    StringBuilder consoleOut;

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
        notePad = (EditText) findViewById(R.id.tvNotePad);
        bRun = (Button) findViewById(R.id.bRun);
        console = (TextView) findViewById(R.id.tvConsole);
        //notePad = (TextView) findViewById(R.id.tvNotePad);
        //FileOutputStream fos;


        bRun.setOnClickListener(new View.OnClickListener() {
            //FileOutputStream fos;
            @Override
            public void onClick(View v) {


                if(forPressed){
                    printFor();
                    forPressed = false;
                }
                if(whilePressed){
                    printWhile();
                    whilePressed = false;
                }

//                String getCode = String.valueOf(notePad.getText());
//                getCode.replace("\n", "");
//                getCode.replace("\\s", "");
//
//                char[] code = getCode.toCharArray();
//
//                char[] codeHolder;

                //char[] code;

//                for(int i = 0; i < code.length; i++){
//                   // System.out.println(code[i]);
//                }




                //Interpreter interpret = new Interpreter();
               //System.out.println("Files dir is "+getFilesDir());
//                String FILENAME = "hello_file";
//                String string = "hello world!";
//                try {
//                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//                    fos.write(string.getBytes());
//                    fos.close();
//                }catch(Exception e){
//
//                }
//
//                System.out.println("Files last is "+fileList().toString());
                //interpret.dummyFile();
                //create object and send
                //get text from console
            }
        });

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
                console.setText("");
                notePad.setText("");

                /*
                beginner
                 */

                notePadOut = "";
                notePadOut += " Boolean isTrue = true;<br>" +
                        "        while ( <font color='red'><i>condition</i></font> ){<br>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                        "             <font color='red'><i>statement</i></font> <br>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                        "            isTrue = false;<br>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "        }<br>" +
                        "    }<br><br>";
                notePad.setText(Html.fromHtml(notePadOut));

                /*
                amateur
                 */
//                notePadOut = "";
//                notePadOut += " Boolean isTrue =  <font color='red'><i>set_condition</i></font> ;<br>" +
//                        "        while ( <font color='red'><i>condition</i></font> ){<br>" +
//                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
//                        " <font color='red'><i>statement</i></font> <br>" +
//                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
//                        "isTrue = false;<br>" +
//                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>" +
//                        "    }<br><br>";
//                notePad.setText(Html.fromHtml(notePadOut));

//                /*
//                professional
//                 */
//                notePadOut = "<br><br>";
//                notePadOut += "" +
//                        "        while ( ){<br>" +
//                        "           <br>" +
//                        "            " +
//                        "    }<br><br>";
//                notePad.setText(Html.fromHtml(notePadOut));
                whilePressed = true;
            }
        });
        bFor.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                console.setText("");
                notePad.setText("");
                notePadOut = "";

                /*
                beginner
                 */
//                notePadOut += "for (int i = 0; <font color='red'><i>condition</i></font>; i++){<br><br> " +
//                        "System.out.println(\"Hello, world!\");<br><br>" +
//                        "}<br><br>";
//                notePad.setText(Html.fromHtml(notePadOut));

                /*
                amatuer
                 */
                notePadOut += "for (<font color='red'><i>init</i></font>; <font color='red'><i>condition</i></font>; <font color='red'><i>iterate</i></font>){<br>" +
                        " <font color='red'><i>statement</i></font> <br>"+
                "}<br><br>";
                notePad.setText(Html.fromHtml(notePadOut));

                /*
                professional
                 */
//                notePadOut += "for ( ){\n\n  " +
//                        "}\n\n";
//                notePad.setText(notePadOut);
                forPressed = true;
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
    }

    public void printFor(){
        consoleOut = new StringBuilder();
        for(int i = 0; i < 10; i++){
            consoleOut.append(" Hello, world!\n");
        }

        console.setText(consoleOut.toString());
    }

    public void printWhile(){
        consoleOut = new StringBuilder();
        Boolean isTrue = true;
        while (isTrue){
            consoleOut.append(" I am truest to do it!\n");
            isTrue = false;
        }
        console.setText(consoleOut.toString());
    }
}
