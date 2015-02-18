package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.canvas.CanvasThread;
import group5.cs3750.trainingwheels.canvas.CanvasView;
import group5.cs3750.trainingwheels.programmingobjects.For;
import group5.cs3750.trainingwheels.programmingobjects.If;
import group5.cs3750.trainingwheels.programmingobjects.Print;
import group5.cs3750.trainingwheels.programmingobjects.ProgrammingObject;
import group5.cs3750.trainingwheels.programmingobjects.Variable;
import group5.cs3750.trainingwheels.programmingobjects.While;


public class TrainingIDE extends Activity {
    private Button bIf, bWhile, bFor, bString, bFunction, bVariable, bPrint;
    private Button bBack, bRun, bClear;
    private TextView outputTextView;

    private CanvasView canvas;
    private CanvasThread canvasThread;
    private ArrayList<ProgrammingObject> programmingObjects = new ArrayList<ProgrammingObject>();
    private ProgrammingObject lastHoveredObject, currentHoveredObject; // The object that the user is currently hovering over when dragging out a programming object, can be null
    private ProgrammingObject closestHoverObjectAbove, closestHoverObjectBelow; // The object that is closest (and above) the current hover location

    // Tutorial variables
    private ViewFlipper tutorialFlipper;
    private Button tutorialPrevButton, tutorialCloseButton, tutorialNextButton;
    private AlertDialog tutorialDialog;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    private SharedPreferences settings;

    // Drag/drop variables
    private View draggedButton;
    private WebView webView;
    private boolean didDrop;
    private ProgrammingObject draggedObject; // temporary dragged object

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_ide_2);

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        initButtons();
        initCanvas();

        canvasThread = new CanvasThread(canvas.getHolder(), canvas);
        canvasThread.start();
    }

    private void initButtons() {
        /*
         Programming Object buttons
        */
        bIf = (Button) findViewById(R.id.bIf);
        bWhile = (Button) findViewById(R.id.bWhile);
        bFor = (Button) findViewById(R.id.bFor);
        bString = (Button) findViewById(R.id.bString);
        bFunction = (Button) findViewById(R.id.bProcedure);
        bVariable = (Button) findViewById(R.id.bVariable);
        bPrint = (Button) findViewById(R.id.bPrint);

        // Long click listeners
        bWhile.setOnLongClickListener(new CustomOnLongPressListener());
        bIf.setOnLongClickListener(new CustomOnLongPressListener());
        bFor.setOnLongClickListener(new CustomOnLongPressListener());
        bString.setOnLongClickListener(new CustomOnLongPressListener());
        bVariable.setOnLongClickListener(new CustomOnLongPressListener());
        bFunction.setOnLongClickListener(new CustomOnLongPressListener());
        bPrint.setOnLongClickListener(new CustomOnLongPressListener());

        // Custom backgrounds
        bIf.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_teal, 12));
        bWhile.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_orange, 12));
        bFor.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_red, 12));
        bString.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_blue, 12));
        bFunction.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_purple, 12));
        bVariable.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_green, 12));
        bPrint.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_teal, 12));

        /*
         Back, Run, Clear buttons
        */
        bBack = (Button) findViewById(R.id.back_button);
        bRun = (Button) findViewById(R.id.run_button);
        bClear = (Button) findViewById(R.id.clear_button);


        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        final String container = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<p id=\"output\"></p>\n" +
                "\n" +
                "<script>%s</script>\n" +
                "\n" +
                "</body>\n" +
                "</html> ";

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ProgrammingObject programmingObject : programmingObjects) {
                    programmingObject.toScript(stringBuilder);
                }

                String content = stringBuilder.toString();
                content = content.replaceAll("\\{field\\}", "output");
                String finalContainer = String.format(container, content);

                webView.loadData(finalContainer, "text/html", null);
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputTextView.setText("");
            }
        });
    }

    private void showTutorial(String tutorial) {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final boolean hints = getPrefs.getBoolean("hints", true);
        final View tutorialView;
        if (hints) {

            if (tutorial.contentEquals("for")) {
                tutorialView = getLayoutInflater().inflate(R.layout.tutorial_dialog, null);
            } else if (tutorial.contentEquals("while")) {
                tutorialView = getLayoutInflater().inflate(R.layout.while_tutorial_dialog, null);
            } else if (tutorial.contentEquals("variable")) {
                tutorialView = getLayoutInflater().inflate(R.layout.variable_tutorial_dialog, null);
            } else if (tutorial.contentEquals("function")) {
                tutorialView = getLayoutInflater().inflate(R.layout.function_tuorial_dialog, null);
            } else if (tutorial.contentEquals("print")) {
                tutorialView = getLayoutInflater().inflate(R.layout.print_tutorial_dialog, null);
            } else if (tutorial.contentEquals("if")) {
                tutorialView = getLayoutInflater().inflate(R.layout.if_tutorial_dialog, null);
            } else if (tutorial.contentEquals("string")) {
                tutorialView = getLayoutInflater().inflate(R.layout.string_tutorial_dialog, null);
            } else {
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
     * @param programmingObjectList the list of programming objects to look through.
     */
    private void findCurrentHoveredObject(List<ProgrammingObject> programmingObjectList) {
        currentHoveredObject = null;

        if (canvas.getCurrentHoverLocation() != null) {
            for (ProgrammingObject programmingObject : programmingObjectList) {
                if (programmingObject.getCurrentDrawnLocation() != null) { // Should never be False
                    // Modify the rectangle of the programming object using the canvas offset
                    Rect adjustedRect = new Rect(programmingObject.getCurrentDrawnLocation().left - canvas.getDrawOffset().x, programmingObject.getCurrentDrawnLocation().top - canvas.getDrawOffset().y, programmingObject.getCurrentDrawnLocation().right - canvas.getDrawOffset().x, programmingObject.getCurrentDrawnLocation().bottom - canvas.getDrawOffset().y);

                    //Log.d("IDEa", "Checking " + adjustedRect.toShortString() + " against " + canvas.getCurrentHoverLocation().toString() + ". contains: " + adjustedRect.contains(canvas.getCurrentHoverLocation().x, canvas.getCurrentHoverLocation().y));

                    boolean canAdd = adjustedRect.contains(canvas.getCurrentHoverLocation().x, canvas.getCurrentHoverLocation().y); // Did the user touch inside this object's bounds?
                    canAdd = canAdd && (draggedObject == null || !programmingObject.equals(draggedObject)); // Is this object NOT the current dragged object?
                    //canAdd = canAdd && !isPOChildOfPO(draggedObject, programmingObject); // Does programmingObject exist BENEATH draggedObject (is programmingObject a child of draggedObject). If so, do NOT allow this.

                    try {
                        if (canAdd && draggedObject != null)
                            canAdd = canAdd && programmingObject.getAllowedChildTypes().contains(draggedObject.getType());
                    } catch (Exception e) {
                        Log.e("IDEa", e.getMessage());
                    }

                    if (canAdd) {
                        currentHoveredObject = programmingObject;

                        if(lastHoveredObject == null)
                            lastHoveredObject = currentHoveredObject;

                        //Log.d("IDEa", "Found hovered object: " + currentHoveredObject.getTypeName());

                        return; // Found it, return
                    }
                }

                if (programmingObject.getChildren() != null) // Look through this object's children
                    findCurrentHoveredObject(programmingObject.getChildren());
            }
        }
    }

    /*
     * This finds the programming object just above where the user is dragging a new programming object.
     * This only does anything if the user is dragging and if they are not hovering over
     * an existing programming object. The user must also be within the bounds of the drawn object area.
     */
    private void findObjectJustAboveHoverLocation(List<ProgrammingObject> programmingObjectList) {
        if (currentHoveredObject == null && canvas.getCurrentHoverLocation() != null) {
            // Only draw a line if they are dragging within the bounding box of the drawn objects area
            if (canvas.getDrawnObjectsAreaSize() != null &&
                    canvas.getCurrentHoverLocation().x > 0 && canvas.getCurrentHoverLocation().x < canvas.getDrawnObjectsAreaSize().x &&
                    canvas.getCurrentHoverLocation().y > 0 && canvas.getCurrentHoverLocation().y < canvas.getDrawnObjectsAreaSize().y) {

                for (ProgrammingObject programmingObject : programmingObjectList) {
                    if (programmingObject.getCurrentDrawnLocation() != null) {
                        // Modify the rectangle of the programming object using the canvas offset
                        Rect adjustedRect = new Rect(
                                programmingObject.getCurrentDrawnLocation().left - canvas.getDrawOffset().x,
                                programmingObject.getCurrentDrawnLocation().top - canvas.getDrawOffset().y,
                                programmingObject.getCurrentDrawnLocation().right - canvas.getDrawOffset().x,
                                programmingObject.getCurrentDrawnLocation().bottom - canvas.getDrawOffset().y);

                        // Get object above
                        if (canvas.getCurrentHoverLocation().y > adjustedRect.bottom && canvas.getCurrentHoverLocation().y < adjustedRect.bottom + (2 * canvas.getDrawnObjectVerticalSpacing()) + canvas.getDrawnObjectHeight()) {
                            // The current hover location is BELOW this object. This object MIGHT be the closest one. Keep track of it.
                            if (draggedObject == null || !draggedObject.equals(programmingObject))
                                closestHoverObjectAbove = programmingObject;
                        }

                        // Get object below
                        if (canvas.getCurrentHoverLocation().y < adjustedRect.top && canvas.getCurrentHoverLocation().y > adjustedRect.top - (2 * canvas.getDrawnObjectVerticalSpacing()) - canvas.getDrawnObjectVerticalSpacing()) {
                            // The current hover location is ABOVE this object. This object IS the closest one (below). Keep track of it.
                            if (draggedObject == null || !draggedObject.equals(programmingObject))
                                closestHoverObjectBelow = programmingObject;
                        }
                    }

                    if (programmingObject.getChildren() != null) // Look through this object's children
                        findObjectJustAboveHoverLocation(programmingObject.getChildren());
                }
            }
        }
    }

    private class CustomOnLongPressListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            Log.i("IDEA", v.getTag() + " drag started.");

            ClipData clipData = ClipData.newPlainText(v.getTag().toString(), v.getTag().toString()); // The first value can be gotten from getClipDescription(), the second value can be gotten from getClipData()
            View.DragShadowBuilder dsb = new View.DragShadowBuilder(v);
            v.startDrag(clipData, dsb, v, 0);
            v.setEnabled(false);

            draggedButton = v;

            return false;
        }
    }

    ;

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (tutorialNextButton.isEnabled()) {
                        tutorialFlipper.setInAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.left_in));
                        tutorialFlipper.setOutAnimation(AnimationUtils.loadAnimation(TrainingIDE.this, R.anim.left_out));
                        tutorialFlipper.showNext();

                        tutorialPrevButton.setEnabled(tutorialFlipper.getDisplayedChild() > 0);
                        tutorialNextButton.setEnabled(tutorialFlipper.getDisplayedChild() < tutorialFlipper.getChildCount() - 1);
                    }

                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (tutorialPrevButton.isEnabled()) {
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

    public ProgrammingObject getClosestHoverObjectAbove() {
        return closestHoverObjectAbove;
    }

    public ProgrammingObject getClosestHoverObjectBelow() {
        return closestHoverObjectBelow;
    }

    public ProgrammingObject getDraggedObject() {
        return draggedObject;
    }

    public void setDraggedObject(ProgrammingObject draggedObject) {
        this.draggedObject = draggedObject;
    }

    public View getDraggedButton() {
        return draggedButton;
    }

    public void setDraggedButton(View draggedButton) {
        this.draggedButton = draggedButton;
    }

    public ProgrammingObject getLastHoveredObject() {
        return lastHoveredObject;
    }

    public void setLastHoveredObject(ProgrammingObject lastHoveredObject) {
        this.lastHoveredObject = lastHoveredObject;
    }

    private void initCanvas() {
        canvas = (CanvasView) findViewById(R.id.canvas_view);
        canvas.setTrainingIDE(this);

        // Initialize the OnDragListener
        canvas.setOnDragListener(new View.OnDragListener() {
            // http://developer.android.com/guide/topics/ui/drag-drop.html
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        canvas.setCurrentHoverLocation(new Point((int) event.getX(), (int) event.getY()));

                        return true; // Returning true is NECESSARY for the listener to receive the drop event
                    case DragEvent.ACTION_DRAG_ENDED:
                        draggedButton.setEnabled(true);
                        draggedButton.setPressed(false);
                        draggedButton = null;

                        canvas.setCurrentHoverLocation(null);
                        //findCurrentHoveredObject(programmingObjects); // shouldn't be necessary any more...or ever?
                        currentHoveredObject = null;
                        lastHoveredObject = null;
                        closestHoverObjectAbove = null;
                        closestHoverObjectBelow = null;

                        if (!didDrop) {
                            // Didn't drop, remove the temporary programming object
                            deleteProgrammingObject(programmingObjects, draggedObject);
                        }

                        draggedObject = null;
                        didDrop = false; // reset this

                        break; // No need to return anything here

                    case DragEvent.ACTION_DROP:
                        Log.i("IDEA", v.getTag() + " received drop.");
                        TextView tv = new TextView(TrainingIDE.this);
                        tv.setText(event.getClipData().getItemAt(0).getText());

                        canvas.setLastDropLocation(new Point((int) event.getX(), (int) event.getY()));
                        findCurrentHoveredObject(programmingObjects);

                        if (draggedObject != null) {
                            // TODO: I think these will not be necessary any more
                            deleteProgrammingObject(programmingObjects, draggedObject);
                            addExistingProgrammingObject(draggedObject);
                        } else {
                          final ProgrammingObject programmingObject = addProgrammingObject((String) event.getClipDescription().getLabel());
                          if (programmingObject instanceof Print) {
                            View view = View.inflate(TrainingIDE.this, R.layout.print_dialog, null);
                            final EditText editText = (EditText) view.findViewById(R.id.print_dialog_edit_text);
                            AlertDialog.Builder builder = new AlertDialog.Builder(TrainingIDE.this);
                            builder.setTitle("Enter some text");
                            builder.setView(view);
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {
                                ((Print)programmingObject).setText(editText.getText().toString() + "<br>");
                                dialogInterface.dismiss();
                              }
                            });
                            builder.create().show();
                          }

                        }

                        closestHoverObjectAbove = null;
                        closestHoverObjectBelow = null;

                        showTutorial((String) event.getClipData().getItemAt(0).getText());
                        didDrop = true;
                        return true; // Return true/false here based on whether or not the drop is valid

                    case DragEvent.ACTION_DRAG_LOCATION:
                        canvas.setCurrentHoverLocation(new Point((int) event.getX(), (int) event.getY()));

                        findCurrentHoveredObject(programmingObjects);

                        if(currentHoveredObject == null || !currentHoveredObject.equals(lastHoveredObject)) { // The user has not moved outside of the last object they hovered over, so don't delete it
                            if (draggedObject != null) {
                                Log.d("IDEa", "Creating new PO");
                                deleteProgrammingObject(programmingObjects, draggedObject);
                                addExistingProgrammingObject(draggedObject);
                            } else {
                                draggedObject = addProgrammingObject((String) event.getClipDescription().getLabel());
                            }

                            lastHoveredObject = currentHoveredObject; // being null is fine
                            closestHoverObjectAbove = null;
                            closestHoverObjectBelow = null;
                            findObjectJustAboveHoverLocation(programmingObjects);
                        }

                        break;
                }
                return false;
            }
        });

        canvas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO: allow user to pick up a programming object
                // They should be able to move or delete it (show a trash can icon, I think)

                // Get object we are hovered over, if any
                canvas.setCurrentHoverLocation(new Point((int) canvas.getCurrentTouchLocation().x, (int) canvas.getCurrentTouchLocation().y));
                findCurrentHoveredObject(programmingObjects);
                lastHoveredObject = currentHoveredObject; // null is fine

                if (currentHoveredObject != null) {
                    draggedButton = getButtonForProgrammingObject(currentHoveredObject);

                    if (draggedButton != null) {
                        ClipData clipData = ClipData.newPlainText(draggedButton.getTag().toString(), draggedButton.getTag().toString()); // The first value can be gotten from getClipDescription(), the second value can be gotten from getClipData()
                        View.DragShadowBuilder dsb = new View.DragShadowBuilder(draggedButton);
                        draggedButton.startDrag(clipData, dsb, draggedButton, 0);

                        draggedObject = currentHoveredObject;
                        deleteProgrammingObject(programmingObjects, currentHoveredObject);
                        currentHoveredObject = null;
                    } else {
                        Log.e("IDEa", "Button not found for object being picked up! This should NEVER happen (unless a PO was added).");
                    }
                }

                return true;
            }
        });
    }

    private ProgrammingObject addProgrammingObject(String objectName) {
        final ProgrammingObject pObj;

        if (objectName.contentEquals("for")) {
            pObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
        } else if (objectName.contentEquals("while")) {
            pObj = new While(0, new Variable(0, "whileVariable", Variable.VariableType.STRING, "beep"), "boop");
        } else if (objectName.contentEquals("variable")) {
            pObj = new Variable(0, "testVariable", Variable.VariableType.STRING, "test");
        } else if (objectName.contentEquals("if")) {
            pObj = new If(0, new Variable(0, "ifLeft", Variable.VariableType.STRING, "left"), "left", Variable.VariableType.STRING, ProgrammingObject.ComparisonOperator.EQUAL);
        } else if (objectName.contentEquals("print")) {
          pObj = new Print("");
        } else {
            pObj = new Variable(0, "unsupportedType", Variable.VariableType.STRING, "unsupportedType");
        }

        pObj.setButtonDrawable(draggedButton.getBackground()); // Save the BG of the button to use for drawing

        if (currentHoveredObject != null) {
            //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, currentHoveredObject.getChildren().size(), currentHoveredObject);
            synchronized (programmingObjects) {
                pObj.setPositionUnderParent(currentHoveredObject.getChildren().size());
                pObj.setParent(currentHoveredObject);
                currentHoveredObject.addChild(pObj);
            }
        } else if (closestHoverObjectAbove != null || closestHoverObjectBelow != null) {
            insertProgrammingObject(pObj);
        } else {
            //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
            synchronized (programmingObjects) {
                programmingObjects.add(pObj);
            }
        }

        return pObj;
    }

    /*
     * Adds an EXISTING programming object. Particularly useful for moving a PO that has been added previously.
     */
    private ProgrammingObject addExistingProgrammingObject(ProgrammingObject programmingObject) {
        if (currentHoveredObject != null) {
            //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, currentHoveredObject.getChildren().size(), currentHoveredObject);
            synchronized (programmingObjects) {
                programmingObject.setPositionUnderParent(currentHoveredObject.getChildren().size());
                programmingObject.setParent(currentHoveredObject);
                currentHoveredObject.addChild(programmingObject);
            }
        } else if (closestHoverObjectAbove != null || closestHoverObjectBelow != null) {
            insertProgrammingObject(programmingObject);
        } else {
            //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
            synchronized (programmingObjects) {
                programmingObjects.add(programmingObject);
            }
        }

        return programmingObject;
    }

    private void insertProgrammingObject(ProgrammingObject pObj) {
        // Inserting an object between other objects
        synchronized (programmingObjects) {
            if (closestHoverObjectAbove != null) {
                if (closestHoverObjectAbove.getChildren().size() > 0) {
                    // The closest object to the hover location has children, so add this new object to the front of chose children
                    //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, 0, closestHoverObjectAbove);
                    pObj.setParent(closestHoverObjectAbove);
                    closestHoverObjectAbove.insertChild(0, pObj);
                } else {
                    // The closest object to the hover location has no children, so add this new object to the closest object's parent (if it has one),
                    // inserting the new object just after the closest object
                    if (closestHoverObjectAbove.getParent() != null) {
                        //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, 0, closestHoverObjectAbove.getParent());
                        pObj.setParent(closestHoverObjectAbove.getParent());
                        closestHoverObjectAbove.getParent().insertChild(closestHoverObjectAbove.getParent().getChildren().indexOf(closestHoverObjectAbove) + 1, pObj);
                    } else {
                        //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
                        programmingObjects.add(pObj);
                    }
                }
            } else if (closestHoverObjectBelow != null) {
                // This is a less frequent case. When this happens, only insert into the closest object's parent above the closest object
                if (closestHoverObjectBelow.getParent() != null) {
                    //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN, 0, closestHoverObjectAbove.getParent());
                    pObj.setParent(closestHoverObjectBelow.getParent());
                    closestHoverObjectBelow.getParent().insertChild(closestHoverObjectBelow.getParent().getChildren().indexOf(closestHoverObjectBelow), pObj);
                } else {
                    //For forObj = new For(0, 0, 10, ProgrammingObject.ComparisonOperator.LESS_THAN);
                    programmingObjects.add(programmingObjects.indexOf(closestHoverObjectBelow), pObj);
                }
            }
        }
    }

    private Button getButtonForProgrammingObject(ProgrammingObject programmingObject) {
        // bIf, bWhile, bFor, bString, bFunction, bVariable, bPrint;
        // TODO: Update this any time a button is added to the left side bar
        String objectName = programmingObject.getTypeName().toLowerCase();

        if (objectName.contentEquals("for")) {
            return bFor;
        } else if (objectName.contentEquals("while")) {
            return bWhile;
        } else if (objectName.contentEquals("variable")) {
            return bVariable;
        } else if (objectName.contentEquals("if")) {
            return bIf;
        } else if (objectName.contentEquals("string")) {
            return bString;
        } else if (objectName.contentEquals("function")) {
            return bFunction;
        } else if (objectName.contentEquals("print")) {
            return bPrint;
        }

        return null;
    }

    /*
     * Recursively searches for and removes the given programming object
     */
    private boolean deleteProgrammingObject(List<ProgrammingObject> programmingObjectList, ProgrammingObject programmingObject) {
        if (programmingObject != null) {
            for (ProgrammingObject pObj : programmingObjectList) {
                if (programmingObject.equals(pObj)) {
                    synchronized (programmingObjects) {
                        programmingObjectList.remove(pObj);
                    }

                    return true;
                }

                if (pObj.getChildren() != null) // Look through this object's children
                    deleteProgrammingObject(pObj.getChildren(), programmingObject);
            }

            return false;
        }

        return true;
    }

    /**
     * Determines if childPO is a child (directly, or a child of a child, etc) of parentPO.
     * This is a recursive method.
     *
     * @param childPO the PO to find as a child of parentPO
     * @param parentPO the PO to search through to find childPO
     *
     * @return true if childPO is a child of parentPO
     */
    private boolean isPOChildOfPO(ProgrammingObject childPO, ProgrammingObject parentPO) {
        if (childPO != null && parentPO != null && parentPO.getChildren() != null && parentPO.getChildren().size() > 0) {
            for (ProgrammingObject pObj : parentPO.getChildren()) {
                if (childPO.equals(pObj))
                    return true;

                if (pObj.getChildren() != null) // Look through this object's children
                    isPOChildOfPO(childPO, pObj);
            }
        }

        return false;
    }

    public static GradientDrawable getBackgroundGradientDrawable(Resources resources, int colorResourceId, int cornerRadius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(resources.getColor(colorResourceId));
        gd.setCornerRadius(cornerRadius);

        return gd;
    }

  public void doSave(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Save Program");
    builder.setView(View.inflate(this, R.layout.save_dialog, null));
    builder.setNeutralButton(android.R.string.ok, null);

    AlertDialog alertDialog = builder.create();
    alertDialog.show();
    alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new SaveClickListener(alertDialog));
  }

  class SaveClickListener implements View.OnClickListener {
    private final AlertDialog mDialog;

    public SaveClickListener(AlertDialog dialogView) {
      mDialog = dialogView;
    }

    @Override
    public void onClick(View view) {
      EditText title = (EditText) mDialog.findViewById(R.id.save_title);
      if (title.getText() == null) {
        title.setError("You must supply a name");
        return;
      }

      String titleText = title.getText().toString();
      if (titleText != null && !titleText.trim().isEmpty()) {
        titleText = titleText.trim();

        for (String s : fileList()) {
          if (s.equals(titleText)) {
            title.setError("Name already in use");
            return;
          }
        }
        try {
          ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(titleText, MODE_PRIVATE));
          oos.writeObject(programmingObjects);
          oos.close();
          mDialog.dismiss();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
