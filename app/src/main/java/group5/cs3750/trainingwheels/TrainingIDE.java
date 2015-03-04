package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final String PROGRAMMING_OBJECT_LIST = "group5.cs3750.trainingwheels.TrainingIDE.PROGRAMMING_OBJECT_LIST";
    private Button bIf, bWhile, bFor, bFunction, bVariable, bPrint;
    private Button bBack, bRun, bClear;

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

    // Drag/drop variables
    private View draggedButton;
    private WebView webView;
    private boolean didDrop = false, draggingExistingObject = false, editingExistingObject = false;
    private ProgrammingObject draggedObject; // temporary dragged object

    // Object dialog variables
    private boolean didVariableTypeChange = false; // used to track if the user changed the type of a variable, used exclusively on the Variable entry dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_ide);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            programmingObjects = (ArrayList<ProgrammingObject>) savedInstanceState.getSerializable("POs");
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(PROGRAMMING_OBJECT_LIST)) {
                programmingObjects = (ArrayList<ProgrammingObject>) extras.getSerializable(PROGRAMMING_OBJECT_LIST);
            }
        }

        initButtons();
        initCanvas();
        initOutputWindow();

        canvasThread = new CanvasThread(canvas.getHolder(), canvas);
        canvasThread.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("POs", programmingObjects);

        super.onSaveInstanceState(outState);
    }

    private void initButtons() {
        /*
         Programming Object buttons
        */
        bIf = (Button) findViewById(R.id.bIf);
        bWhile = (Button) findViewById(R.id.bWhile);
        bFor = (Button) findViewById(R.id.bFor);
        bFunction = (Button) findViewById(R.id.bProcedure);
        bVariable = (Button) findViewById(R.id.bVariable);
        bPrint = (Button) findViewById(R.id.bPrint);

        // Long click listeners
        bWhile.setOnLongClickListener(new CustomOnLongPressListener());
        bIf.setOnLongClickListener(new CustomOnLongPressListener());
        bFor.setOnLongClickListener(new CustomOnLongPressListener());
        bVariable.setOnLongClickListener(new CustomOnLongPressListener());
        bFunction.setOnLongClickListener(new CustomOnLongPressListener());
        bPrint.setOnLongClickListener(new CustomOnLongPressListener());

        // Custom backgrounds
        bIf.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), new If().getDrawColor(), 12, 0));
        bWhile.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), new While().getDrawColor(), 12, 0));
        bFor.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), new For().getDrawColor(), 12, 0));
        //bFunction.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), R.color.button_purple, 12, 0));
        bVariable.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), new Variable().getDrawColor(), 12, 0));
        bPrint.setBackgroundDrawable(getBackgroundGradientDrawable(getResources(), new Print().getDrawColor(), 12, 0));

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
                    System.out.println(stringBuilder.toString());
                }

                String content = stringBuilder.toString();
                content = content.replaceAll("\\{field\\}", "output");
                String finalContainer = String.format(container, content);

                webView.loadData(finalContainer, "text/html", null);

                ((SlidingDrawer) findViewById(R.id.output_drawer)).open();
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String container = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        "</body>\n" +
                        "</html>";

                webView.loadData(container, "text/html", null);

                programmingObjects.clear();
            }
        });
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
                        draggingExistingObject = false; // reset this

                        break; // No need to return anything here
                    case DragEvent.ACTION_DROP:
                        Log.i("IDEA", v.getTag() + " received drop.");
                        TextView tv = new TextView(TrainingIDE.this);
                        tv.setText(event.getClipData().getItemAt(0).getText());

                        canvas.setLastDropLocation(new Point((int) event.getX(), (int) event.getY()));
                        findCurrentHoveredObject(programmingObjects);
//                        if (draggedObject != null) {
//                            // TODO: I think these will not be necessary any more
//                            deleteProgrammingObject(programmingObjects, draggedObject);
//                            addExistingProgrammingObject(draggedObject);
//                            showParametersDialog(draggedObject);
//                        } else {
//                            //showParametersDialog(draggedObject);
//                        }

                        if(draggedObject != null) {
                            deleteProgrammingObject(programmingObjects, draggedObject);
                            addExistingProgrammingObject(draggedObject);

                            if (!draggingExistingObject)
                                showParametersDialog(draggedObject);
                        }

                        closestHoverObjectAbove = null;
                        closestHoverObjectBelow = null;

                        showTutorial((String) event.getClipData().getItemAt(0).getText());

                        didDrop = true;
                        return true; // Return true/false here based on whether or not the drop is valid

                    case DragEvent.ACTION_DRAG_LOCATION:
                        canvas.setCurrentHoverLocation(new Point((int) event.getX(), (int) event.getY()));

                        findCurrentHoveredObject(programmingObjects);

                        //if(currentHoveredObject == null || !currentHoveredObject.equals(lastHoveredObject)) { // The user has not moved outside of the last object they hovered over, so don't delete it
                        if (draggedObject != null) {
                            //Log.d("IDEa", "Creating new PO");
                            deleteProgrammingObject(programmingObjects, draggedObject);
                            addExistingProgrammingObject(draggedObject);
                        } else {
                            draggedObject = addProgrammingObject((String) event.getClipDescription().getLabel());
                        }

//                        if (draggedObject != null)
//                            deleteProgrammingObject(programmingObjects, draggedObject);
//                        draggedObject = addProgrammingObject((String) event.getClipDescription().getLabel());

                        //lastHoveredObject = currentHoveredObject; // being null is fine
                        closestHoverObjectAbove = null;
                        closestHoverObjectBelow = null;
                        findObjectJustAboveHoverLocation(programmingObjects);
                        //}

                        break;
                }
                return false;
            }
        });

        canvas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
                        draggingExistingObject = true;
                    } else {
                        Log.e("IDEa", "Button not found for object being picked up! This should NEVER happen (unless a PO was added).");
                    }
                }

                return true;
            }
        });
    }

    private void initOutputWindow() {
        final VerticalTextView bShowOutput = (VerticalTextView) findViewById(R.id.handle);
        final SlidingDrawer slidingDrawer = (SlidingDrawer) findViewById(R.id.output_drawer);

        bShowOutput.setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(getResources(), R.color.button_light_blue, 12, 2));

        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                bShowOutput.setText("Hide Output");
                bShowOutput.setTopDown(false);
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                bShowOutput.setText("Show Output");
                bShowOutput.setTopDown(true);
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

                        if (lastHoveredObject == null)
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

    private ProgrammingObject addProgrammingObject(String objectName) {
        final ProgrammingObject pObj;

        if (objectName.contentEquals("for")) {
            pObj = new For("for", ProgrammingObject.ComparisonOperator.LESS_THAN, true);
        } else if (objectName.contentEquals("while")) {
            pObj = new While(While.WhileConditionType.TRUE);
        } else if (objectName.contentEquals("variable")) {
            pObj = new Variable("", Variable.VariableType.STRING, Variable.VariableActionType.CREATE, "");
        } else if (objectName.contentEquals("if")) {
            pObj = new If(new Variable("", Variable.VariableType.STRING, Variable.VariableActionType.CREATE, ""), "", Variable.VariableType.STRING, ProgrammingObject.ComparisonOperator.EQUAL);
        } else if (objectName.contentEquals("print")) {
            pObj = new Print("");
        } else {
            pObj = new Variable("unsupportedType", Variable.VariableType.STRING, Variable.VariableActionType.CREATE, "unsupportedType");
        }

        if (currentHoveredObject != null) {
            synchronized (programmingObjects) {
                pObj.setParent(currentHoveredObject);
                currentHoveredObject.addChild(pObj);
            }
        } else if (closestHoverObjectAbove != null || closestHoverObjectBelow != null) {
            insertProgrammingObject(pObj);
        } else {
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
            synchronized (programmingObjects) {
                programmingObject.setParent(currentHoveredObject);
                currentHoveredObject.addChild(programmingObject);
            }
        } else if (closestHoverObjectAbove != null || closestHoverObjectBelow != null) {
            insertProgrammingObject(programmingObject);
        } else {
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
                    pObj.setParent(closestHoverObjectAbove);
                    closestHoverObjectAbove.insertChild(0, pObj);
                } else {
                    // The closest object to the hover location has no children, so add this new object to the closest object's parent (if it has one),
                    // inserting the new object just after the closest object
                    if (closestHoverObjectAbove.getParent() != null) {
                        pObj.setParent(closestHoverObjectAbove.getParent());
                        closestHoverObjectAbove.getParent().insertChild(closestHoverObjectAbove.getParent().getChildren().indexOf(closestHoverObjectAbove) + 1, pObj);
                    } else {
                        programmingObjects.add(pObj);
                    }
                }
            } else if (closestHoverObjectBelow != null) {
                // This is a less frequent case. When this happens, only insert into the closest object's parent above the closest object
                if (closestHoverObjectBelow.getParent() != null) {
                    pObj.setParent(closestHoverObjectBelow.getParent());
                    closestHoverObjectBelow.getParent().insertChild(closestHoverObjectBelow.getParent().getChildren().indexOf(closestHoverObjectBelow), pObj);
                } else {
                    programmingObjects.add(programmingObjects.indexOf(closestHoverObjectBelow), pObj);
                }
            }
        }
    }

    private Button getButtonForProgrammingObject(ProgrammingObject programmingObject) {
        // bIf, bWhile, bFor, bFunction, bVariable, bPrint;
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
     * @param childPO  the PO to find as a child of parentPO
     * @param parentPO the PO to search through to find childPO
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

    public static GradientDrawable getBackgroundGradientDrawable(Resources resources, int colorResourceId, int cornerRadius, int strokeSize) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(resources.getColor(colorResourceId));
        gd.setCornerRadius(cornerRadius);
        gd.setStroke(strokeSize, Color.BLACK);

        return gd;
    }

    public void showParametersDialog(final ProgrammingObject programmingObject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainingIDE.this);

        if (programmingObject instanceof Print) {
            showPrintDialog(programmingObject);
        } else if (programmingObject instanceof For) {
            showForDialog(programmingObject);
        } else if (programmingObject instanceof If) {
            showIfDialog(programmingObject);
        } else if (programmingObject instanceof Variable) {
            showVariableDialog(programmingObject);
        } else if (programmingObject instanceof While) {
            showWhileDialog(programmingObject);
        }
    }

    private void showIfDialog(final ProgrammingObject programmingObject) {
        final CustomDialog dialog = new CustomDialog(TrainingIDE.this, true, "If - Enter your parameters", R.layout.if_dialog, getString(android.R.string.cancel), getString(android.R.string.ok));
        final Spinner conditionTypeSpinner = (Spinner) dialog.getDialog().findViewById(R.id.ifConditionTypeSpinner);

        final LinearLayout conditionCustomExpressionContainer = (LinearLayout) dialog.getDialog().findViewById(R.id.ifConditionCustomExpressionContainer);
        final EditText conditionCustomExpressionET = (EditText) dialog.getDialog().findViewById(R.id.ifConditionCustomExpressionEditText);

        final TextView conditionVariableLabel = (TextView) dialog.getDialog().findViewById(R.id.ifConditionVariableLabel);
        final Spinner conditionVariableSpinner = (Spinner) dialog.getDialog().findViewById(R.id.ifConditionVariableSpinner);

        final TextView terminatingValueTypeLabel = (TextView) dialog.getDialog().findViewById(R.id.ifTerminatingValueTypeLabel);
        final Spinner terminatingValueTypeSpinner = (Spinner) dialog.getDialog().findViewById(R.id.ifTerminatingValueTypeSpinner);

        final TextView terminatingValueVariableLabel = (TextView) dialog.getDialog().findViewById(R.id.ifTerminatingValueVariableLabel);
        final Spinner terminatingValueVariableSpinner = (Spinner) dialog.getDialog().findViewById(R.id.ifTerminatingValueVariableSpinner);

        final TextView comparisonOperatorLabel = (TextView) dialog.getDialog().findViewById(R.id.ifComparisonOperatorLabel);
        final Spinner comparisonOperatorSpinner = (Spinner) dialog.getDialog().findViewById(R.id.ifComparisonOperatorSpinner);

        final LinearLayout customTerminatingValueContainer = (LinearLayout) dialog.getDialog().findViewById(R.id.ifTerminatingValueCustomContainer);
        final EditText customTerminatingValueET = (EditText) dialog.getDialog().findViewById(R.id.ifTerminatingValueCustomEditText);

        ArrayList<String> variableObjectNames = new ArrayList<String>();
        getVariableNamesAsList(variableObjectNames, programmingObjects, null); // get all variable types

        conditionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //<item>Variable</item>
            //<item>Custom Expression</item>

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conditionVariableLabel.setVisibility(position == 0 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                conditionVariableSpinner.setVisibility(position == 0 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                terminatingValueTypeLabel.setVisibility(position == 0 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                terminatingValueTypeSpinner.setVisibility(position == 0 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                terminatingValueVariableLabel.setVisibility((position == 0 && terminatingValueTypeSpinner.getSelectedItemPosition() == 2) ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                terminatingValueVariableSpinner.setVisibility((position == 0 && terminatingValueTypeSpinner.getSelectedItemPosition() == 2) ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                comparisonOperatorLabel.setVisibility(position == 0 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                comparisonOperatorSpinner.setVisibility(position == 0 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                customTerminatingValueContainer.setVisibility((position < 1 && terminatingValueTypeSpinner.getSelectedItemPosition() == 3) ? View.VISIBLE : View.GONE); // not visible for 'True' selection

                conditionCustomExpressionContainer.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // only visible for 'Custom Expression' selection
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        terminatingValueTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                terminatingValueVariableLabel.setVisibility(position == 2 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                terminatingValueVariableSpinner.setVisibility(position == 2 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                customTerminatingValueContainer.setVisibility(position == 3 ? View.VISIBLE : View.GONE); // only visible for 'Custom Value' selection
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        conditionVariableSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, variableObjectNames));
        terminatingValueVariableSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, variableObjectNames));
        conditionTypeSpinner.setSelection(0); // 'Variable' selection

        dialog.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editingExistingObject)
                    deleteProgrammingObject(programmingObjects, programmingObject);

                editingExistingObject = false;

                dialog.dismiss();
            }
        });
        dialog.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showWhileDialog(final ProgrammingObject programmingObject) {
        final CustomDialog dialog = new CustomDialog(TrainingIDE.this, true, "While - Enter your parameters", R.layout.while_dialog_alternate, getString(android.R.string.cancel), getString(android.R.string.ok));
        final Spinner conditionTypeSpinner = (Spinner) dialog.getDialog().findViewById(R.id.whileConditionTypeSpinner);

        final LinearLayout conditionCustomExpressionContainer = (LinearLayout) dialog.getDialog().findViewById(R.id.whileConditionCustomExpressionContainer);
        final EditText conditionCustomExpressionET = (EditText) dialog.getDialog().findViewById(R.id.whileConditionCustomExpressionEditText);

        final TextView conditionVariableLabel = (TextView) dialog.getDialog().findViewById(R.id.whileConditionVariableLabel);
        final Spinner conditionVariableSpinner = (Spinner) dialog.getDialog().findViewById(R.id.whileConditionVariableSpinner);

        final TextView terminatingValueTypeLabel = (TextView) dialog.getDialog().findViewById(R.id.whileTerminatingValueTypeLabel);
        final Spinner terminatingValueTypeSpinner = (Spinner) dialog.getDialog().findViewById(R.id.whileTerminatingValueTypeSpinner);

        final TextView terminatingValueVariableLabel = (TextView) dialog.getDialog().findViewById(R.id.whileTerminatingValueVariableLabel);
        final Spinner terminatingValueVariableSpinner = (Spinner) dialog.getDialog().findViewById(R.id.whileTerminatingValueVariableSpinner);

        final TextView comparisonOperatorLabel = (TextView) dialog.getDialog().findViewById(R.id.whileComparisonOperatorLabel);
        final Spinner comparisonOperatorSpinner = (Spinner) dialog.getDialog().findViewById(R.id.whileComparisonOperatorSpinner);

        final LinearLayout customTerminatingValueContainer = (LinearLayout) dialog.getDialog().findViewById(R.id.whileTerminatingValueCustomContainer);
        final EditText customTerminatingValueET = (EditText) dialog.getDialog().findViewById(R.id.whileTerminatingValueCustomEditText);

        final String[] operatorSymbols = getResources().getStringArray(R.array.operatorSymbolArray);
        ArrayList<String> variableObjectNames = new ArrayList<String>();
        getVariableNamesAsList(variableObjectNames, programmingObjects, null); // get all variable types

        //conditon type spinner listener
        conditionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conditionVariableLabel.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                conditionVariableSpinner.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                terminatingValueTypeLabel.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                terminatingValueTypeSpinner.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                terminatingValueVariableLabel.setVisibility((position == 1 && terminatingValueTypeSpinner.getSelectedItemPosition() == 2) ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                terminatingValueVariableSpinner.setVisibility((position == 1 && terminatingValueTypeSpinner.getSelectedItemPosition() == 2) ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                comparisonOperatorLabel.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                comparisonOperatorSpinner.setVisibility(position == 1 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                customTerminatingValueContainer.setVisibility((position == 1 && terminatingValueTypeSpinner.getSelectedItemPosition() == 3) ? View.VISIBLE : View.GONE); // only visible for the 'Variable' selection

                conditionCustomExpressionContainer.setVisibility(position == 2 ? View.VISIBLE : View.GONE); // only visible for 'Custom Expression' selection
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });


        terminatingValueTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                terminatingValueVariableLabel.setVisibility(position == 2 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection
                terminatingValueVariableSpinner.setVisibility(position == 2 ? View.VISIBLE : View.GONE); // only visible for 'Variable' selection

                customTerminatingValueContainer.setVisibility(position == 3 ? View.VISIBLE : View.GONE); // only visible for 'Custom Value' selection
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        conditionVariableSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, variableObjectNames));
        terminatingValueVariableSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, variableObjectNames));
        conditionTypeSpinner.setSelection(1);

        if(((While) programmingObject).getConditionType() == While.WhileConditionType.TRUE)
            conditionTypeSpinner.setSelection(0);
        else if(((While) programmingObject).getConditionType() == While.WhileConditionType.VARIABLE) {
            conditionTypeSpinner.setSelection(1);

            if(((While) programmingObject).getConditionVariable() != null)
                conditionVariableSpinner.setSelection(variableObjectNames.indexOf(((While) programmingObject).getConditionVariable().getName()));

            comparisonOperatorSpinner.setSelection(Arrays.asList(operatorSymbols).indexOf(((While) programmingObject).getComparisonOperator().toString()));

            if(((While) programmingObject).getTerminatingValueType() == While.WhileTerminatingValueType.TRUE) {
                terminatingValueVariableSpinner.setSelection(0);
            } else if(((While) programmingObject).getTerminatingValueType() == While.WhileTerminatingValueType.FALSE) {
                terminatingValueVariableSpinner.setSelection(1);
            } else if(((While) programmingObject).getTerminatingValueType() == While.WhileTerminatingValueType.VARIABLE) {
                terminatingValueVariableSpinner.setSelection(2);

                if(((While) programmingObject).getTerminatingVariable() != null)
                    terminatingValueVariableSpinner.setSelection(variableObjectNames.indexOf(((While) programmingObject).getTerminatingVariable().getName()));
            } if(((While) programmingObject).getTerminatingValueType() == While.WhileTerminatingValueType.CUSTOM_VALUE) {
                terminatingValueVariableSpinner.setSelection(3);
                customTerminatingValueET.setText(((While) programmingObject).getCustomTerminatingValue());
            }
        } else if(((While) programmingObject).getConditionType() == While.WhileConditionType.CUSTOM_EXPRESSION) {
            conditionTypeSpinner.setSelection(2);
            conditionCustomExpressionET.setText(((While) programmingObject).getCustomConditionExpression());
        }

        dialog.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editingExistingObject)
                    deleteProgrammingObject(programmingObjects, programmingObject);

                editingExistingObject = false;

                dialog.dismiss();
            }
        });
        dialog.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting while parameters
                if(conditionTypeSpinner.getSelectedItemPosition() == 0) {
                    ((While) programmingObject).setConditionType(While.WhileConditionType.TRUE);
                } else if(conditionTypeSpinner.getSelectedItemPosition() == 1) {
                    ((While) programmingObject).setConditionType(While.WhileConditionType.VARIABLE);
                    ((While) programmingObject).setConditionVariable((Variable) getVariableByName(conditionVariableSpinner.getSelectedItem().toString(), programmingObjects));

                } else if(conditionTypeSpinner.getSelectedItemPosition() == 2) {
                    ((While) programmingObject).setConditionType(While.WhileConditionType.CUSTOM_EXPRESSION);
                    ((While) programmingObject).setCustomConditionExpression(conditionCustomExpressionET.getText().toString());

                    if(terminatingValueTypeSpinner.getSelectedItemPosition() == 0) {
                        ((While) programmingObject).setTerminatingValueType(While.WhileTerminatingValueType.TRUE);
                    } else if(terminatingValueTypeSpinner.getSelectedItemPosition() == 1) {
                        ((While) programmingObject).setTerminatingValueType(While.WhileTerminatingValueType.FALSE);
                    } else if(terminatingValueTypeSpinner.getSelectedItemPosition() == 2) {
                        ((While) programmingObject).setTerminatingValueType(While.WhileTerminatingValueType.VARIABLE);
                        ((While) programmingObject).setTerminatingVariable((Variable) getVariableByName(terminatingValueVariableSpinner.getSelectedItem().toString(), programmingObjects));
                    } else if(terminatingValueTypeSpinner.getSelectedItemPosition() == 3) {
                        ((While) programmingObject).setTerminatingValueType(While.WhileTerminatingValueType.CUSTOM_VALUE);
                        ((While) programmingObject).setCustomTerminatingValue(customTerminatingValueET.getText().toString());
                    }
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }
    
    private void showVariableDialog(final ProgrammingObject programmingObject) {
        final CustomDialog dialog = new CustomDialog(TrainingIDE.this, true, "Variable - Enter your parameters", R.layout.variable_dialog, getString(android.R.string.cancel), getString(android.R.string.ok));
        final Spinner type = (Spinner) dialog.getDialog().findViewById(R.id.variableTypeSpinner);
        final EditText name = (EditText) dialog.getDialog().findViewById(R.id.variableName);
        final EditText value = (EditText) dialog.getDialog().findViewById(R.id.variableValue);
        final LinearLayout booleanContainer = (LinearLayout) dialog.getDialog().findViewById(R.id.variableBooleanValueContainer);
        final RadioButton trueRB = (RadioButton) dialog.getDialog().findViewById(R.id.variableTrueRadioButton);
        final RadioButton falseRB = (RadioButton) dialog.getDialog().findViewById(R.id.variableFalseRadioButton);

        // TODO: Add ACTION back in

        didVariableTypeChange = false;

        if(((Variable) programmingObject).getVariableType() == Variable.VariableType.STRING)
            type.setSelection(0);
        else if(((Variable) programmingObject).getVariableType() == Variable.VariableType.NUMBER)
            type.setSelection(1);
        else if(((Variable) programmingObject).getVariableType() == Variable.VariableType.BOOLEAN)
            type.setSelection(2);

        name.setText(((Variable) programmingObject).getName());
        value.setText(((Variable) programmingObject).getValue().toString());

        if(((Variable) programmingObject).getVariableType() == Variable.VariableType.BOOLEAN) {
            trueRB.setChecked((Boolean) ((Variable) programmingObject).getValue());
            falseRB.setChecked(!(Boolean) ((Variable) programmingObject).getValue());
        }

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                booleanContainer.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
                value.setVisibility(position == 2 ? View.GONE : View.VISIBLE);

                if(didVariableTypeChange)
                    value.setText(""); // clear this out so we don't save weird values

                didVariableTypeChange = true; // set to true AFTER the check on purpose, otherwise it'll wipe out the value when the dialog is first opened

                if(position == 0) {
                    // String
                    value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if(position == 1) {
                    // Number
                    value.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL); // class = this is numbers, flag = allow decimal numbs
                } else if(position == 2) {
                    // Boolean
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        dialog.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editingExistingObject)
                    deleteProgrammingObject(programmingObjects, programmingObject);

                editingExistingObject = false;

                dialog.dismiss();
            }
        });
        dialog.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean canSave = true;

                canSave = !name.getText().toString().equals("");

                if(canSave) {
                    if (type.getSelectedItem().equals("String") || type.getSelectedItem().equals("Number"))
                        canSave = !value.getText().toString().equals("");

                    if(canSave) {
                        if (type.getSelectedItem().equals("String")) {
                            ((Variable) programmingObject).setVariableType(Variable.VariableType.STRING);
                            ((Variable) programmingObject).setValue(value.getText().toString());
                        } else if (type.getSelectedItem().equals("Number")) {
                            ((Variable) programmingObject).setVariableType(Variable.VariableType.NUMBER);
                            ((Variable) programmingObject).setValue(Float.parseFloat(value.getText().toString()));
                        } else if (type.getSelectedItem().equals("Boolean")) {
                            ((Variable) programmingObject).setVariableType(Variable.VariableType.BOOLEAN);
                            ((Variable) programmingObject).setValue(trueRB.isChecked());
                        }

                        ((Variable) programmingObject).setName(name.getText().toString());

                        dialog.dismiss();
                    } else
                        Toast.makeText(TrainingIDE.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(TrainingIDE.this, "Please enter a variable name", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showPrintDialog(final ProgrammingObject programmingObject) {
        final CustomDialog dialog = new CustomDialog(TrainingIDE.this, true, "Print - Enter text to print", R.layout.print_dialog, getString(android.R.string.cancel), getString(android.R.string.ok));
        final EditText editText = (EditText) dialog.getDialog().findViewById(R.id.print_dialog_edit_text);
        final Spinner printSpinner = (Spinner) dialog.getDialog().findViewById(R.id.printSpinner);
        final Spinner printVariableSpinner = (Spinner) dialog.getDialog().findViewById(R.id.print_variable_spinner);

        //if position is on 0 show edit text
        //if position is on 1 show variable spinner

        final String[] printTypes = getResources().getStringArray(R.array.printVariableArray);
        ArrayList<String> variableObjectNames = new ArrayList<String>();
        getVariableNamesAsList(variableObjectNames, programmingObjects, null); // get all variable types


        printSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
       printSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, variableObjectNames));
       printSpinner.setSelection(1);
       printSpinner.setSelection(Arrays.asList(printTypes).indexOf(0));

        dialog.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editingExistingObject)
                    deleteProgrammingObject(programmingObjects, programmingObject);

                editingExistingObject = false;

                dialog.dismiss();
            }
        });
        dialog.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((Print) programmingObject).setText(editText.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showForDialog(final ProgrammingObject programmingObject) {
        final CustomDialog dialog = new CustomDialog(TrainingIDE.this, true, "For - Enter your parameters", R.layout.for_dialog, getString(android.R.string.cancel), getString(android.R.string.ok));
        final EditText labelET = (EditText) dialog.getDialog().findViewById(R.id.labelEditText);
        final LinearLayout startingValueContainer = (LinearLayout) dialog.getDialog().findViewById(R.id.startValueContainer);
        final TextView startingValueTV = (TextView) dialog.getDialog().findViewById(R.id.startValueTextView);
        final Spinner startingValueSpinner = (Spinner) dialog.getDialog().findViewById(R.id.startingValueSpinner);
        final LinearLayout endingValueContainer = (LinearLayout) dialog.getDialog().findViewById(R.id.endValueContainer);
        final TextView endingValueTV = (TextView) dialog.getDialog().findViewById(R.id.endValueTextView);
        final Spinner endingValueSpinner = (Spinner) dialog.getDialog().findViewById(R.id.endValueSpinner);
        final Spinner endingValueOperatorSpinner = (Spinner) dialog.getDialog().findViewById(R.id.endValueOperatorSpinner);
        final RadioButton countUpRB = (RadioButton) dialog.getDialog().findViewById(R.id.countUpRadioButton);
        final RadioButton countDownRB = (RadioButton) dialog.getDialog().findViewById(R.id.countDownRadioButton);

        // init fields
        final String[] operatorSymbols = getResources().getStringArray(R.array.operatorSymbolArray);
        ArrayList<String> variableObjectNames = new ArrayList<String>();
        getVariableNamesAsList(variableObjectNames, programmingObjects, Variable.VariableType.NUMBER); // only get number variables
        variableObjectNames.add(0, ""); // Blank value
        variableObjectNames.add(1, "--Manual Entry--");

        labelET.setText(((For) programmingObject).getLabel());

        startingValueSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, variableObjectNames));
        endingValueSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, variableObjectNames));

        if(((For) programmingObject).getStartingValue() != null) {
            startingValueTV.setText(((For) programmingObject).getStartingValue());
            startingValueContainer.setVisibility(View.VISIBLE);
            startingValueSpinner.setSelection(1); // manual entry
        } else if(((For) programmingObject).getStartingValueVariable() != null) {
            startingValueContainer.setVisibility(View.GONE); // should be gone already, but be sure
            startingValueSpinner.setSelection(variableObjectNames.indexOf(((For) programmingObject).getStartingValueVariable().getName())); // manual entry
        }

        if(((For) programmingObject).getEndingValue() != null) {
            endingValueTV.setText(((For) programmingObject).getEndingValue());
            endingValueContainer.setVisibility(View.VISIBLE);
            endingValueSpinner.setSelection(1); // manual entry
        } else if(((For) programmingObject).getStartingValueVariable() != null) {
            endingValueContainer.setVisibility(View.GONE); // should be gone already, but be sure
            endingValueSpinner.setSelection(variableObjectNames.indexOf(((For) programmingObject).getEndValueVariable().getName())); // manual entry
        }

        endingValueOperatorSpinner.setSelection(Arrays.asList(operatorSymbols).indexOf(((For) programmingObject).getEndingValueComparisonOperator().toString()));

        countUpRB.setChecked(((For) programmingObject).isCountUp());
        countDownRB.setChecked(!((For) programmingObject).isCountUp());

        startingValueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(TrainingIDE.this);
                    alert.setTitle("Enter starting value");

                    // Set an EditText view to get user input
                    final EditText input = new EditText(TrainingIDE.this);
                    input.setRawInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED); // class = this is numbers, flag = allow negative numbers

                    alert.setView(input);

                    if (((For) programmingObject).getStartingValue() != null) {
                        input.setText(((For) programmingObject).getStartingValue().toString());
                    }

                    alert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((For) programmingObject).setStartingValue(Integer.valueOf(input.getText().toString()));
                                    startingValueTV.setText(input.getText().toString());
                                    startingValueContainer.setVisibility(View.VISIBLE);
                                }
                            });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            startingValueSpinner.setSelection(0);
                        }
                    });
                    alert.show();
                } else {
                    startingValueContainer.setVisibility(View.GONE);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        endingValueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(TrainingIDE.this);
                    alert.setTitle("Enter ending value");

                    // Set an EditText view to get user input
                    final EditText input = new EditText(TrainingIDE.this);
                    input.setRawInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED); // class = this is numbers, flag = allow decimal numbs

                    alert.setView(input);

                    if (((For) programmingObject).getEndingValue() != null) {
                        input.setText(((For) programmingObject).getEndingValue().toString());
                    }

                    alert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((For) programmingObject).setEndingValue(Integer.valueOf(input.getText().toString()));
                                    endingValueTV.setText(input.getText().toString());
                                    endingValueContainer.setVisibility(View.VISIBLE);
                                }
                            });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            endingValueSpinner.setSelection(0);
                        }
                    });
                    alert.show();
                } else {
                    endingValueContainer.setVisibility(View.GONE);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        dialog.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editingExistingObject)
                    deleteProgrammingObject(programmingObjects, programmingObject);

                dialog.dismiss();
            }
        });
        dialog.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((For) programmingObject).setLabel(labelET.getText().toString());

                // starting value
                if(startingValueSpinner.getSelectedItemPosition() == 0) {
                    // nothing selected, should not be allowed
                } else if(startingValueSpinner.getSelectedItemPosition() == 1) {
                    // manual entry selected
                    ((For) programmingObject).setStartingValue(Integer.valueOf(startingValueTV.getText().toString()));
                } else {
                    // variable selected
                    ((For) programmingObject).setStartingValueVariable((Variable) getVariableByName(startingValueSpinner.getSelectedItem().toString(), programmingObjects));
                }

                // ending value
                if(endingValueSpinner.getSelectedItemPosition() == 0) {
                    // nothing selected, should not be allowed
                } else if(endingValueSpinner.getSelectedItemPosition() == 1) {
                    // manual entry selected
                    ((For) programmingObject).setEndingValue(Integer.valueOf(endingValueTV.getText().toString()));
                } else {
                    // variable selected
                    ((For) programmingObject).setEndValueVariable((Variable) getVariableByName(endingValueSpinner.getSelectedItem().toString(), programmingObjects));
                }

                ((For) programmingObject).setEndingValueComparisonOperator(ProgrammingObject.ComparisonOperator.fromString(operatorSymbols[endingValueOperatorSpinner.getSelectedItemPosition()]));

                ((For) programmingObject).setCountUp(countUpRB.isChecked());

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private ProgrammingObject getVariableByName(String name, ArrayList<ProgrammingObject> programmingObjects) {
        for (ProgrammingObject pObj : programmingObjects) {
            if (pObj.getType() == ProgrammingObject.ProgrammingObjectType.VARIABLE) {
                if(((Variable) pObj).getName().equals(name))
                    return pObj;
            }

            if (pObj.getChildren() != null) // Look through this object's children
                getVariableByName(name, pObj.getChildren());
        }

        return null;
    }

    // enter null for variableType to ignore type
    private void getVariableNamesAsList(ArrayList<String> list, ArrayList<ProgrammingObject> programmingObjects, Variable.VariableType variableType) {
        for (ProgrammingObject pObj : programmingObjects) {
            if (pObj.getType() == ProgrammingObject.ProgrammingObjectType.VARIABLE) {
                if(variableType == null || ((Variable) pObj).getVariableType() == variableType)
                    list.add(((Variable) pObj).getName());
            }

            if (pObj.getChildren() != null) // Look through this object's children
                getVariableNamesAsList(list, pObj.getChildren(), variableType);
        }
    }

    public void handleDoubleTap(int x, int y) {
        canvas.setCurrentHoverLocation(new Point(x, y));
        findCurrentHoveredObject(programmingObjects);

        if(currentHoveredObject != null) {
            editingExistingObject = true;
            showParametersDialog(currentHoveredObject);
        }
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
