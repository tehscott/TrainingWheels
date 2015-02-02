package group5.cs3750.trainingwheels.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
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
    private Point drawnObjectsAreaSize = new Point(); // the width and height (unadjusted by offset) of the area containing drawn objects

    private Point currentHoverLocation; // location of the users finger when they are hovering (by dragging a programming object)
    private Point currentTouchLocation, lastTouchLocation; // current and previous locations of where the user is/was touching
    private Point lastDropLocation; // location of where the user last dropped an object
    private Point drawOffset = new Point(); // offset of the canvas origin when drawing objects. dragging the drawing area (canvas) will simulate scrolling by using the offset

    private final boolean DEBUG = true;

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.trainingIDE = (TrainingIDE) context;

        if(!isInEditMode())
            setZOrderOnTop(true);

        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        // make the GamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
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

    public void render(Canvas canvas) {
        if(canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            // Draw the top-most objects, their children will be drawn recursively
            int height = 0;
            for(ProgrammingObject programmingObject : trainingIDE.getProgrammingObjects()) {
                height = drawProgrammingObject(programmingObject, canvas, height, 0);
                height++;
            }

            drawHoverLocation(canvas); // Debug function, show where the user is dragging
            drawDebugInfo(canvas); // Debug function, show various variable values
        }
    }

    public void update() {

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

        paint.setColor(pObj.getDrawColor());

        if(trainingIDE.getCurrentHoveredObject() != null && trainingIDE.getCurrentHoveredObject().equals(pObj))
            paint.setAlpha(50);

        // For testing
        if(DEBUG) {
            if (trainingIDE.getClosestHoverObjectAbove() != null && trainingIDE.getClosestHoverObjectAbove().equals(pObj))
                paint.setColor(Color.BLUE);

            if (trainingIDE.getClosestHoverObjectBelow() != null && trainingIDE.getClosestHoverObjectBelow().equals(pObj))
                paint.setColor(Color.GREEN);
        }

        // Draw the start of this parent object
        canvas.drawRect(left - drawOffset.x, top - drawOffset.y, right - drawOffset.x, bottom - drawOffset.y, paint);
        pObj.setCurrentDrawnLocation(new Rect(left, top, right, bottom));

        paint.setAlpha(255);
        paint.setColor(Color.WHITE);
        canvas.drawText(pObj.getTypeName(), left - drawOffset.x + 5, top - drawOffset.y + 15, paint);
        canvas.drawText(pObj.toString(), left - drawOffset.x + 5, top - drawOffset.y + 30, paint);

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

        paint.setColor(pObj.getDrawColor());
        canvas.drawRect(left - drawOffset.x, top - drawOffset.y, right - drawOffset.x, bottom - drawOffset.y, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("End " + pObj.getTypeName(), left - drawOffset.x + 5, top - drawOffset.y + 15, paint);

        // Update the size of the area that contains the drawn objects
        if((depth * drawnObjectHorizontalSpacing) + drawnObjectWidth > drawnObjectsAreaSize.x) // only update the width if this section is wider than any other
            drawnObjectsAreaSize.x = (depth * drawnObjectHorizontalSpacing) + drawnObjectWidth; // width
        drawnObjectsAreaSize.y = (height * drawnObjectHeight) + ((drawnObjectVerticalSpacing * height) + drawnObjectHeight); // height

        return height;
    }

    /*
     * This method will draw a line where the user is attempting to drop a programming object.
     * This line is only drawn if the user is dragging an object and if they are not hovering
     * over an existing programming object.
     */
    private void drawDropLine(Canvas canvas) {

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

            if(drawOffset != null)
                canvas.drawText("Offset: " + drawOffset.toString(), 400, 20, paint);

            if(drawnObjectsAreaSize != null)
                canvas.drawText("Drawn objects area size: " + drawnObjectsAreaSize.toString(), 400, 40, paint);

            if(drawnObjectsAreaSize != null) {
                Paint.Style oldStyle = paint.getStyle();
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(0 - drawOffset.x, 0 - drawOffset.y, drawnObjectsAreaSize.x - drawOffset.x, drawnObjectsAreaSize.y - drawOffset.y, paint);
                paint.setStyle(oldStyle);
            }
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

            drawOffset.x = drawOffset.x + xChange; // plus or minus here determines which way the screen drags when you move (like apple's "natural" drag option)
            drawOffset.y = drawOffset.y + yChange; // plus or minus here determines which way the screen drags when you move (like apple's "natural" drag option)

            // TODO: The stuff below doesn't work well
            // Restrict dragging so that they can't drag infinitely
            // Adjust x offset
            if(drawOffset.x < 0) drawOffset.x = 0;

            if(drawnObjectsAreaSize.x > getWidth()) {
                //if(drawOffset.x + getWidth() > drawnObjectsAreaSize.x)
                //    drawOffset.x = getWidth() - drawnObjectsAreaSize.x;
            } else
                drawOffset.x = 0;

            // Adjust y offset
            if(drawOffset.y < 0) drawOffset.y = 0;

            if(drawnObjectsAreaSize.y > getHeight()) {
                // Not sure why this request an adjustment of 1, but it works
                if (getHeight() + drawOffset.y - 1 > drawnObjectsAreaSize.y)
                    drawOffset.y = drawnObjectsAreaSize.y - getHeight() + 1;
            } else
                drawOffset.y = 0;
        }
    }

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
}