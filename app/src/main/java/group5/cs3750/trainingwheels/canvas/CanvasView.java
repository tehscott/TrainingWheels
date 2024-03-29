package group5.cs3750.trainingwheels.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import group5.cs3750.trainingwheels.TrainingIDE;
import group5.cs3750.trainingwheels.programmingobjects.ProgrammingObject;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CanvasView.class.getSimpleName();

    private CanvasThread thread;
    private TrainingIDE trainingIDE;

    private int drawnObjectVerticalSpacing = 10; // pixels between drawn objects
    private int drawnObjectHorizontalSpacing = 20; // pixel width of the space each level of depth adds to a drawn object
    private int drawnObjectHeight = 80; // pixel height of drawn objects
    private int drawnObjectWidth = 200; // pixel width of drawn objects
    private int topMargin = -20; // margin at the top of the canvas to allow the user to drag an object to the top. negative on purpose.
    private Point drawnObjectsAreaSize = new Point(); // the width and height (unadjusted by offset) of the area containing drawn objects
    private float screenDensityMultiplier = 1.0f;

    private Point currentHoverLocation; // location of the users finger when they are hovering (by dragging a programming object)
    private Point currentTouchLocation, lastTouchLocation; // current and previous locations of where the user is/was touching
    private Point lastDropLocation; // location of where the user last dropped an object
    private Point drawOffset = new Point(); // offset of the canvas origin when drawing objects. dragging the drawing area (canvas) will simulate scrolling by using the offset
    private Point screenSize = new Point();
    private boolean doDrawAreaSizeCalculate = true;

    private final boolean DEBUG = false;

    private GestureDetector gestureDetector;
    final Handler handler = new Handler();
    private Runnable longPressRunnable = new Runnable() {
        // This is a pretty neat way to handle the long press event. It allows us to set the amount
        // of time required for a long press and gives greater control in general.
        public void run() {
            trainingIDE.handleLongPress();
        }
    };

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //if(!isInEditMode())
        //    setZOrderOnTop(true);

        init();

        // make the canvas focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) { }

    private void init() {
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        // init top margin
        // TODO: This is supposed to make it easier/possible to drag an object to the top, but it does not work well.
        drawOffset.y = topMargin; // add space above so the user can drag an object to the top

        // init gestures, for double tapping an object to edit
        gestureDetector = new GestureDetector(getContext(), new GestureListener());

        /* init density scaling
         * 0.75 - ldpi
         * 1.0 - mdpi
         * 1.5 - hdpi
         * 2.0 - xhdpi
         * 3.0 - xxhdpi
         * 4.0 - xxxhdpi
         */
        screenDensityMultiplier = getResources().getDisplayMetrics().density;

        drawnObjectWidth *= screenDensityMultiplier;
        drawnObjectHeight *= screenDensityMultiplier;
        //drawnObjectVerticalSpacing *= screenDensityMultiplier;
        //drawnObjectHorizontalSpacing *= screenDensityMultiplier;

        screenSize.x = getResources().getDisplayMetrics().widthPixels;
        screenSize.y = getResources().getDisplayMetrics().heightPixels;
    }

    // This listener will be used to move the canvas around (to allow you to scroll, etc)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setCurrentTouchLocation(new Point((int) event.getX(), (int) event.getY()));
                handler.postDelayed(longPressRunnable, 500); // fire a long press event after delay

                break;

            case MotionEvent.ACTION_UP:
                setLastTouchLocation(getCurrentTouchLocation());
                setCurrentTouchLocation(null);
                handler.removeCallbacks(longPressRunnable); // they let go, cancel long press event

                break;

            case MotionEvent.ACTION_MOVE:
                setLastTouchLocation(getCurrentTouchLocation());
                setCurrentTouchLocation(new Point((int) event.getX(), (int) event.getY()));
                handler.removeCallbacks(longPressRunnable); // they moved, cancel long press event

                calculateOffset();

                break;
        }

        gestureDetector.onTouchEvent(event); // handle a double-tap event

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // create the game loop thread
        thread = new CanvasThread(getHolder(), this);

        // at this point the surface is created and
        // we can safely start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is being destroyed");
        // tell the thread to shut down and wait for it to finish
        // this is a clean shutdown
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }

    /**
     * Called by the canvas thread to draw on the canvas
     *
     * @param canvas
     */
    public void render(Canvas canvas) {
        if(canvas != null) {
            //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

            // Draw the top-most objects, their children will be drawn recursively
            int height = 0;
            synchronized (trainingIDE.getProgrammingObjects()) {
                for (ProgrammingObject programmingObject : trainingIDE.getProgrammingObjects()) {
                    height = drawProgrammingObject(programmingObject, canvas, height, 0);
                    height++;
                }
            }

            doDrawAreaSizeCalculate = false;

            // Debug functions
            drawHoverLocation(canvas); // Debug function, show where the user is dragging
            drawDebugInfo(canvas); // Debug function, show various variable values
        }
    }

    /**
     * Draws a programming object and its children (recursively)
     *
     * @param pObj
     *  the programming object to draw.
     * @param canvas
     *  the canvas object to draw on.
     *  @param height
     *  the current height level (the number of objects from the top of the list)
     *  @param depth
     *  the current depth level (the number of levels deep we are from a parent object)
     *  @return the current height we are at
     */
    private int drawProgrammingObject(ProgrammingObject pObj, Canvas canvas, int height, int depth) {
        Paint paint = new Paint();

        int top = (height * drawnObjectHeight) + (drawnObjectVerticalSpacing * height);
        int left = depth * drawnObjectHorizontalSpacing;
        int right = left + drawnObjectWidth;
        int bottom = top + drawnObjectHeight;
        int parentObjectStartX = 0;
        int parentObjectStartY = 0;

        paint.setColor(pObj.getDrawColor());

        if(trainingIDE.getCurrentHoveredObject() != null && trainingIDE.getCurrentHoveredObject().equals(pObj))
            paint.setAlpha(50);

        if(DEBUG) {
            if (trainingIDE.getClosestHoverObjectAbove() != null && trainingIDE.getClosestHoverObjectAbove().equals(pObj))
                paint.setColor(Color.BLUE);

            if (trainingIDE.getClosestHoverObjectBelow() != null && trainingIDE.getClosestHoverObjectBelow().equals(pObj))
                paint.setColor(Color.GREEN);
        }

        // TODO: Change the alpha (or something else) of the object if it is being dragged (this should include children)

        // Draw the start of this parent object

        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{getResources().getColor(pObj.getDrawColor()), getResources().getColor(pObj.getDrawColor())});
        drawable.setCornerRadius(12);
        drawable.setBounds(left - drawOffset.x, top - drawOffset.y, right - drawOffset.x, bottom - drawOffset.y);
        drawable.draw(canvas);

        parentObjectStartX = left - drawOffset.x;
        parentObjectStartY = top - drawOffset.y;

        //canvas.drawRect(left - drawOffset.x, top - drawOffset.y, right - drawOffset.x, bottom - drawOffset.y, paint);
        pObj.setCurrentDrawnLocation(new Rect(left, top, right, bottom));

        paint.setAlpha(255);
        paint.setColor(Color.WHITE);
        paint.setTextSize(34 * screenDensityMultiplier);
        paint.setAntiAlias(true);
        Rect objectNameBounds = new Rect();
        paint.getTextBounds(pObj.getTypeName(), 0, pObj.getTypeName().length(), objectNameBounds);

        int xPadding = 10;
        int yPadding = objectNameBounds.height() + 5;

        // Draw object name
        canvas.drawText(pObj.getTypeName(), left - drawOffset.x + xPadding, top - drawOffset.y + yPadding, paint);

        // Draw object subtext
        paint.setTextSize(20 * screenDensityMultiplier);
        String objectSubText = TextUtils.ellipsize(pObj.toString(), new TextPaint(paint), right - left, TextUtils.TruncateAt.END).toString(); // TODO: This doesn't truncate far enough to the left

        Rect objectSubtextBounds = new Rect();
        paint.getTextBounds(objectSubText, 0, objectSubText.length(), objectSubtextBounds);
        canvas.drawText(objectSubText, left - drawOffset.x + xPadding + 2, top - drawOffset.y + yPadding + objectSubtextBounds.height() + 5, paint);

        if(pObj.getAllowedChildTypes().size() > 0) {
            // Draw any children
            //
            for (ProgrammingObject programmingObject : pObj.getChildren()) {
                height = drawProgrammingObject(programmingObject, canvas, height + 1, depth + 1);
            }
            //
            //

            // End of this parent object, draw its second half
            height++;
            top = (height * drawnObjectHeight) + (drawnObjectVerticalSpacing * height);
            left = depth * drawnObjectHorizontalSpacing;
            right = left + drawnObjectWidth;
            bottom = top + drawnObjectHeight;

            //paint.setColor(pObj.getDrawColor());
            //canvas.drawRect(left - drawOffset.x, top - drawOffset.y, right - drawOffset.x, bottom - drawOffset.y, paint);
            drawable.setBounds(left - drawOffset.x, top - drawOffset.y, right - drawOffset.x, bottom - drawOffset.y);
            drawable.draw(canvas);

            // Draw the bar that connects the parent and child
            paint.setColor(getResources().getColor(pObj.getDrawColor()));
            canvas.drawRect(parentObjectStartX, parentObjectStartY, parentObjectStartX + 10, bottom - drawOffset.y, paint);

            //paint.setColor(Color.WHITE);
            //canvas.drawText("End " + pObj.getTypeName(), left - drawOffset.x + 5, top - drawOffset.y + 15, paint);
        }
        // Update the size of the area that contains the drawn objects
        if(doDrawAreaSizeCalculate) {
            // Only recalculate when necessary
            if ((depth * drawnObjectHorizontalSpacing) + drawnObjectWidth > drawnObjectsAreaSize.x) // only update the width if this section is wider than any other
                drawnObjectsAreaSize.x = (depth * drawnObjectHorizontalSpacing) + drawnObjectWidth; // width
            drawnObjectsAreaSize.y = (height * drawnObjectHeight) + ((drawnObjectVerticalSpacing * height) + drawnObjectHeight); // height
        }

        return height;
    }

    private void drawHoverLocation(Canvas canvas) {
        if(DEBUG && canvas != null && currentHoverLocation != null) {
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            canvas.drawCircle(currentHoverLocation.x, currentHoverLocation.y, 10, paint);
        }
    }

    private void drawDebugInfo(Canvas canvas) {
        if(DEBUG) {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);

            if(drawOffset != null)
                canvas.drawText("Offset: " + drawOffset.toString(), 400, 20, paint);

            if(drawnObjectsAreaSize != null)
                canvas.drawText("Drawn objects area size: " + drawnObjectsAreaSize.toString(), 400, 40, paint);

//            if(drawnObjectsAreaSize != null) {
//                Paint.Style oldStyle = paint.getStyle();
//                paint.setStyle(Paint.Style.STROKE);
//                canvas.drawRect(0 - drawOffset.x, 0 - drawOffset.y, drawnObjectsAreaSize.x - drawOffset.x, drawnObjectsAreaSize.y - drawOffset.y, paint);
//                paint.setStyle(oldStyle);
//            }

            if(trainingIDE.getLastHoveredObject() != null)
                canvas.drawText("LastHoveredObject: " + trainingIDE.getLastHoveredObject().toString(), 400, 60, paint);

            if(trainingIDE.getCurrentHoveredObject() != null)
                canvas.drawText("CurrentHoveredObject: " + trainingIDE.getCurrentHoveredObject().toString(), 400, 80, paint);

            if(trainingIDE.getClosestHoverObjectAbove() != null)
                canvas.drawText("ClosestHoverObjectAbove: " + trainingIDE.getClosestHoverObjectAbove().toString(), 400, 100, paint);

            if(trainingIDE.getClosestHoverObjectBelow() != null)
                canvas.drawText("ClosestHoverObjectBelow: " + trainingIDE.getClosestHoverObjectBelow().toString(), 400, 120, paint);

            if(trainingIDE.getDraggedObject() != null)
                canvas.drawText("DraggedObject: " + trainingIDE.getDraggedObject().toString(), 400, 140, paint);
        }
    }

    /**
     * Determines what the canvas drawing offset is based on the current and previous touch locations
     * This is used to simulate scrolling on the canvas
     */
    public void calculateOffset() {
        if(currentTouchLocation != null && lastTouchLocation != null) {
            int xChange = lastTouchLocation.x - currentTouchLocation.x;
            int yChange = lastTouchLocation.y - currentTouchLocation.y;

            // a positive x offset means the origin is off the screen to the lEFT
            // a positive y offset means the origin is off the screen to the TOP
            drawOffset.x = drawOffset.x + xChange; // plus or minus here determines which way the screen drags when you move (like apple's "natural" drag option)
            drawOffset.y = drawOffset.y + yChange; // plus or minus here determines which way the screen drags when you move (like apple's "natural" drag option)

            // TODO: The stuff below doesn't work well
            // Restrict dragging so that they can't drag infinitely

            // restrict dragging the screen to the left
            if(drawnObjectsAreaSize.x > getWidth()) {
                if (getWidth() + drawOffset.x > drawnObjectsAreaSize.x)
                    drawOffset.x = drawnObjectsAreaSize.x - getWidth();
            } else
                drawOffset.x = 0;

            // restrict dragging the screen to the right
            if(drawOffset.x < 0)
                drawOffset.x = 0;

            // restrict dragging the screen to the top
            if(drawnObjectsAreaSize.y > getHeight()) {
                if (getHeight() + drawOffset.y > drawnObjectsAreaSize.y)
                    drawOffset.y = drawnObjectsAreaSize.y - getHeight();
            } else
                drawOffset.y = topMargin;

            // restrict dragging the screen to the right
            if(drawOffset.y < topMargin)
                drawOffset.y = topMargin;
        }
    }



    /**********************************************************************************************
     **********************************GETTERS AND SETTERS*****************************************
     **********************************************************************************************/
    public Point getCurrentHoverLocation() {
        return currentHoverLocation;
    }

    public void setCurrentHoverLocation(Point currentHoverLocation) {
        this.currentHoverLocation = currentHoverLocation;
    }

    public Point getCurrentTouchLocation() {
        return currentTouchLocation;
    }

    public void setCurrentTouchLocation(Point currentTouchLocation) {
        this.currentTouchLocation = currentTouchLocation;
    }

    public Point getLastTouchLocation() {
        return lastTouchLocation;
    }

    public void setLastTouchLocation(Point lastTouchLocation) {
        this.lastTouchLocation = lastTouchLocation;
    }

    public Point getLastDropLocation() {
        return lastDropLocation;
    }

    public void setLastDropLocation(Point lastDropLocation) {
        this.lastDropLocation = lastDropLocation;
    }

    public Point getDrawOffset() {
        return drawOffset;
    }

    public void setDrawOffset(Point drawOffset) {
        this.drawOffset = drawOffset;
    }

    public Point getDrawnObjectsAreaSize() {
        return drawnObjectsAreaSize;
    }

    public void setDrawnObjectsAreaSize(Point drawnObjectsAreaSize) {
        this.drawnObjectsAreaSize = drawnObjectsAreaSize;
    }

    public int getDrawnObjectVerticalSpacing() {
        return drawnObjectVerticalSpacing;
    }

    public int getDrawnObjectHeight() {
        return drawnObjectHeight;
    }

    public TrainingIDE getTrainingIDE() {
        return trainingIDE;
    }

    public void setTrainingIDE(TrainingIDE trainingIDE) {
        this.trainingIDE = trainingIDE;
    }

    public boolean isDoDrawAreaSizeCalculate() {
        return doDrawAreaSizeCalculate;
    }

    public void setDoDrawAreaSizeCalculate(boolean doDrawAreaSizeCalculate) {
        this.doDrawAreaSizeCalculate = doDrawAreaSizeCalculate;
    }

    /**********************************************************************************************
     ************************************NESTED CLASSES********************************************
     **********************************************************************************************/

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            trainingIDE.handleDoubleTap((int)e.getX(), (int)e.getY());

            return true;
        }
    }
}