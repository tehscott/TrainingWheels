package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.canvas.CanvasThread;
import group5.cs3750.trainingwheels.canvas.CanvasView;
import group5.cs3750.trainingwheels.programmingobjects.For;
import group5.cs3750.trainingwheels.programmingobjects.ProgrammingObject;


public class TrainingIDE extends Activity{
    //
    Button bIf, bWhile, bFor, bString, bProcedure, bVariable, bRun, bPrint;
    EditText notePad;
    TextView console;
    //ScrollView programmingAreaScrollview;
    //LinearLayout programmingArea;

    private CanvasView canvas;
    private CanvasThread canvasThread;
    private ArrayList<ProgrammingObject> programmingObjects = new ArrayList<ProgrammingObject>();
    private ProgrammingObject currentHoveredObject; // The object that the user is currently hovering over when dragging out a programming object, can be null

    // Tutorial variables
    private ViewFlipper tutorialFlipper;
    private Button tutorialPrevButton, tutorialCloseButton, tutorialNextButton;
    private AlertDialog tutorialDialog;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    private String difficulty;
    private SharedPreferences settings;

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
        bVariable = (Button) findViewById(R.id.bVariable);
        notePad = (EditText) findViewById(R.id.tvNotePad);
        bRun = (Button) findViewById(R.id.bRun);
        bPrint = (Button) findViewById(R.id.bPrint);
        console = (TextView) findViewById(R.id.outputText);
        //programmingAreaScrollview = (ScrollView) findViewById(R.id.programmingAreaScrollview);
        //programmingArea = (LinearLayout) findViewById(R.id.programmingArea);
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        difficulty = settings.getString("difficulty", "Beginner");
        canvas = (CanvasView) findViewById(R.id.canvas_view);

        bRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Long click listeners
        bWhile.setOnLongClickListener(new CustomOnLongPressListener());
        bIf.setOnLongClickListener(new CustomOnLongPressListener());
        bFor.setOnLongClickListener(new CustomOnLongPressListener());
        bString.setOnLongClickListener(new CustomOnLongPressListener());
        bVariable.setOnLongClickListener(new CustomOnLongPressListener());
        bProcedure.setOnLongClickListener(new CustomOnLongPressListener());
        bPrint.setOnLongClickListener(new CustomOnLongPressListener());

        //programmingAreaScrollview.setOnDragListener(new View.OnDragListener() {
        initCanvas();

        // Create some test data
//        For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
//        For forObj2 = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, 0, forObj);
//        For forObj3 = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, 0, forObj2);
//        For forObj4 = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, 0, forObj3);
//        For forObj5 = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, 0, forObj3);
//        For forObj6 = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
//        For forObj7 = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
//        For forObj8 = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
//        forObj.addChild(forObj2);
//        forObj2.addChild(forObj3);
//        forObj3.addChild(forObj4);
//        forObj3.addChild(forObj5);
//
//        programmingObjects.add(forObj6);
//        programmingObjects.add(forObj); // Only add topmost "parent" objects. Each parent will draw its own children
//        programmingObjects.add(forObj7);
//        programmingObjects.add(forObj8);

        canvasThread = new CanvasThread(canvas.getHolder(), canvas);
        canvasThread.start();
    }

    private void showTutorial(String tutorial) {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final boolean hints = getPrefs.getBoolean("hints", true);
        final View tutorialView;
        if(hints) {

            if(tutorial.contentEquals("for")){
                tutorialView = getLayoutInflater().inflate(R.layout.tutorial_dialog, null);
            }
            else if(tutorial.contentEquals("while")){
                tutorialView = getLayoutInflater().inflate(R.layout.while_tutorial_dialog, null);
            }
            else if(tutorial.contentEquals("variable")){
                tutorialView = getLayoutInflater().inflate(R.layout.variable_tutorial_dialog, null);
            }
            else{
                tutorialView = getLayoutInflater().inflate(R.layout.tutorial_dialog, null);
            }

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
    }

    /**
     * Finds the current programming object being hovered over.
     * Sets the currentHoveredObject variable to null if nothing is found.
     * This will search recursively through all programming objects and their children.
     *
     * @param programmingObjectList
     *  the list of programming objects to look through.
     */
    private void findCurrentHoveredObject(List<ProgrammingObject> programmingObjectList) {
        currentHoveredObject = null;

        if(canvas.getCurrentHoverLocation() != null) {
            for (ProgrammingObject programmingObject : programmingObjectList) {
                if (programmingObject.getCurrentDrawnLocation() != null) {
                    // Modify the rectangle of the programming object using the canvas offset
                    Rect adjustedRect = new Rect(
                            programmingObject.getCurrentDrawnLocation().left - canvas.getDrawOffset().x,
                            programmingObject.getCurrentDrawnLocation().top - canvas.getDrawOffset().y,
                            programmingObject.getCurrentDrawnLocation().right - canvas.getDrawOffset().x,
                            programmingObject.getCurrentDrawnLocation().bottom - canvas.getDrawOffset().y);

                    //Log.d("IDEa", "Checking " + adjustedRect.toShortString() + " against " + canvas.getCurrentHoverLocation().toString());

                    if (adjustedRect.contains(canvas.getCurrentHoverLocation().x, canvas.getCurrentHoverLocation().y)) {
                        currentHoveredObject = programmingObject;

                        return; // Found it, return
                    }
                }

                if(programmingObject.getChildren() != null) // Look through this object's children
                    findCurrentHoveredObject(programmingObject.getChildren());
            }
        }
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

    public ArrayList<ProgrammingObject> getProgrammingObjects() {
        return programmingObjects;
    }

    public ProgrammingObject getCurrentHoveredObject() {
        return currentHoveredObject;
    }

    private void initCanvas() {
        // Initialize the OnDragListener
        canvas.setOnDragListener(new View.OnDragListener() {
            // http://developer.android.com/guide/topics/ui/drag-drop.html
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        canvas.setCurrentHoverLocation(new Point((int)event.getX(), (int)event.getY()));

                        return true; // Returning true is NECESSARY for the listener to receive the drop event
                    case DragEvent.ACTION_DRAG_ENDED:
                        draggedButton.setEnabled(true);
                        draggedButton = null;
                        canvas.setCurrentHoverLocation(null);
                        findCurrentHoveredObject(programmingObjects);

                        break; // No need to return anything here
                    case DragEvent.ACTION_DROP:
                        Log.i("IDEA", v.getTag() + " received drop.");
                        String buttonDragged = event.getClipData().getItemAt(0).getText().toString();
                        TextView tv = new TextView(TrainingIDE.this);
                        String tutorialEvent = (String) event.getClipData().getItemAt(0).getText();
                        tv.setText(event.getClipData().getItemAt(0).getText());

                        canvas.setLastDropLocation(new Point((int)event.getX(), (int)event.getY()));
                        findCurrentHoveredObject(programmingObjects);

                        if(currentHoveredObject != null) {
                            For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, currentHoveredObject.getChildren().size(), currentHoveredObject);
                            currentHoveredObject.addChild(forObj);
                        } else {
                            For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
                            programmingObjects.add(forObj);
                        }

                        //programmingArea.addView(tv);
                        showTutorial(tutorialEvent);
                        return true; // Return true/false here based on whether or not the drop is valid

                    case DragEvent.ACTION_DRAG_LOCATION:
                        canvas.setCurrentHoverLocation(new Point((int)event.getX(), (int)event.getY()));

                        findCurrentHoveredObject(programmingObjects);

                        break;
                }
                return false;
            }
        });

        // Initialize the OnTouchListener
        // This listener will be used to move the canvas around (to allow you to scroll, etc)
        canvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        canvas.setCurrentTouchLocation(new Point((int) event.getX(), (int) event.getY()));

                        break;

                    case MotionEvent.ACTION_UP:
                        canvas.setLastTouchLocation(canvas.getCurrentTouchLocation());
                        canvas.setCurrentTouchLocation(null);

                        break;

                    case MotionEvent.ACTION_MOVE:
                        canvas.setLastTouchLocation(canvas.getCurrentTouchLocation());
                        canvas.setCurrentTouchLocation(new Point((int) event.getX(), (int) event.getY()));

                        canvas.calculateOffset();

                        break;
                }

                return true;
            }
        });
    }
}
