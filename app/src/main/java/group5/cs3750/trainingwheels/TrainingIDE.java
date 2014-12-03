package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class TrainingIDE extends Activity{
    //
    Button bIf, bWhile, bFor, bString, bProcedure, bInt, bRun;
    //EditText notePad;
    //TextView console;
    ScrollView programmingAreaScrollview;
    LinearLayout programmingArea;
    TextView output;
    Boolean forPressed = false;
    Boolean whilePressed = false;

    //StringBuilder notePadOut = new StringBuilder();
    String notePadOut;
    StringBuilder consoleOut;

    // Tutorial variables
    private ViewFlipper tutorialFlipper;
    private Button tutorialPrevButton, tutorialCloseButton, tutorialNextButton;
    private AlertDialog tutorialDialog;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    // Drag/drop variables
    private View draggedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_ide_2); // R.layout.activity_training_ide

        bIf = (Button) findViewById(R.id.bIf);
        bWhile = (Button) findViewById(R.id.bWhile);
        bFor = (Button) findViewById(R.id.bFor);
        bString = (Button) findViewById(R.id.bString);
        bProcedure = (Button) findViewById(R.id.bProcedure);
        bInt = (Button) findViewById(R.id.bInt);
        //notePad = (EditText) findViewById(R.id.tvNotePad);
        bRun = (Button) findViewById(R.id.bRun);
        //console = (TextView) findViewById(R.id.tvConsole);
        //notePad = (TextView) findViewById(R.id.tvNotePad);
        //FileOutputStream fos;

        programmingAreaScrollview = (ScrollView) findViewById(R.id.programmingAreaScrollview);
        programmingArea = (LinearLayout) findViewById(R.id.programmingArea);
        output = (TextView) findViewById(R.id.outputText);


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
                //console.setText("");
                //notePad.setText("");

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
                //notePad.setText(Html.fromHtml(notePadOut));

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
                //console.setText("");
                //notePad.setText("");
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
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        " <font color='red'><i>statement</i></font> <br>"+
                "}<br><br>";
                //notePad.setText(Html.fromHtml(notePadOut));

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

        // Long click listeners
        bWhile.setOnLongClickListener(new CustomOnLongPressListener());
        bIf.setOnLongClickListener(new CustomOnLongPressListener());
        bFor.setOnLongClickListener(new CustomOnLongPressListener());
        bString.setOnLongClickListener(new CustomOnLongPressListener());
        bInt.setOnLongClickListener(new CustomOnLongPressListener());
        bProcedure.setOnLongClickListener(new CustomOnLongPressListener());

        // On drag listeners
        programmingAreaScrollview.setOnDragListener(new View.OnDragListener() {
            // http://developer.android.com/guide/topics/ui/drag-drop.html

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        v.setBackgroundColor(Color.GREEN);

                        return true; // Returning true is NECESSARY for the listener to receive the drop event

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundColor(Color.WHITE);
                        draggedButton.setEnabled(true);
                        draggedButton = null;

                        break; // No need to return anything here

                    case DragEvent.ACTION_DROP:
                        Log.i("IDEA", v.getTag() + " received drop.");

                        TextView tv = new TextView(TrainingIDE.this);
                        tv.setText(event.getClipData().getItemAt(0).getText());
                        programmingArea.addView(tv);

                        return true; // Return true/false here based on whether or not the drop is valid
                }

                return false;
            }
        });

        // On touch listeners
//        bWhile.setOnTouchListener(new CustomOnTouchListener());
//        bIf.setOnTouchListener(new CustomOnTouchListener());
//        bFor.setOnTouchListener(new CustomOnTouchListener());
//        bString.setOnTouchListener(new CustomOnTouchListener());
//        bInt.setOnTouchListener(new CustomOnTouchListener());
//        bProcedure.setOnTouchListener(new CustomOnTouchListener());

        showTutorial();
    }

    public void printFor(){
        consoleOut = new StringBuilder();
        for(int i = 0; i < 10; i++){
            consoleOut.append(" Hello, world!\n");
        }

        //console.setText(consoleOut.toString());
    }

    public void printWhile(){
        consoleOut = new StringBuilder();
        Boolean isTrue = true;
        while (isTrue){
            consoleOut.append(" I am truest to do it!\n");
            isTrue = false;
        }
        //console.setText(consoleOut.toString());
    }

    private void showTutorial() {
        final View tutorialView = getLayoutInflater().inflate(R.layout.tutorial_dialog, null);
        tutorialFlipper = (ViewFlipper) tutorialView.findViewById(R.id.tutorial_flipper);
        tutorialFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        tutorialPrevButton = (Button) tutorialView.findViewById(R.id.tutorial_prev_button);
        tutorialCloseButton = (Button) tutorialView.findViewById(R.id.tutorial_close_button);
        tutorialNextButton = (Button) tutorialView.findViewById(R.id.tutorial_next_button);

        tutorialPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialFlipper.setInAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.right_in));
                tutorialFlipper.setOutAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.right_out));
                tutorialFlipper.showPrevious();

                tutorialPrevButton.setEnabled(tutorialFlipper.getDisplayedChild() > 0);
                tutorialNextButton.setEnabled(tutorialFlipper.getDisplayedChild() < tutorialFlipper.getChildCount() - 1);
            }
        });
        tutorialPrevButton.setEnabled(false);

        tutorialCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialDialog.dismiss();
            }
        });

        tutorialNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialFlipper.setInAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.left_in));
                tutorialFlipper.setOutAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.left_out));
                tutorialFlipper.showNext();

                tutorialPrevButton.setEnabled(tutorialFlipper.getDisplayedChild() > 0);
                tutorialNextButton.setEnabled(tutorialFlipper.getDisplayedChild() < tutorialFlipper.getChildCount() - 1);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(TrainingIDE.this);
        builder.setTitle("IDEA Tutorial");
        builder.setView(tutorialView);
        builder.setCancelable(false); // False so that they are forced to dismiss and fire the OnDismiss event

        tutorialDialog = builder.create();
        tutorialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //prefs.edit().putBoolean("showTutorial", false).commit();
            }
        });
        tutorialDialog.show();
    }

    private class CustomOnLongPressListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            Log.i("IDEA", v.getTag() + " drag started.");

            ClipData clipData = ClipData.newPlainText("", v.getTag().toString());
            View.DragShadowBuilder dsb = new View.DragShadowBuilder(v);
            v.startDrag(clipData, dsb, v, 0);
            v.setEnabled(false);

            draggedButton = v;

            return false;
        }
    };

//    private class CustomOnTouchListener implements View.OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            Log.i("IDEA", event.toString());
//
//            switch(event.getAction()) {
//                case MotionEvent.ACTION_UP:
//                    v.setEnabled(true);
//                    draggedButton = null;
//
//                    break;
//            }
//
//            return false;
//        }
//    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(tutorialNextButton.isEnabled()) {
                        tutorialFlipper.setInAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.left_in));
                        tutorialFlipper.setOutAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.left_out));
                        tutorialFlipper.showNext();

                        tutorialPrevButton.setEnabled(tutorialFlipper.getDisplayedChild() > 0);
                        tutorialNextButton.setEnabled(tutorialFlipper.getDisplayedChild() < tutorialFlipper.getChildCount() - 1);
                    }

                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(tutorialPrevButton.isEnabled()) {
                        tutorialFlipper.setInAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.right_in));
                        tutorialFlipper.setOutAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.right_out));
                        tutorialFlipper.showPrevious();

                        tutorialPrevButton.setEnabled(tutorialFlipper.getDisplayedChild() > 0);
                        tutorialNextButton.setEnabled(tutorialFlipper.getDisplayedChild() < tutorialFlipper.getChildCount() - 1);
                    }
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
