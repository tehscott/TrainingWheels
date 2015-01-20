package group5.cs3750.trainingwheels.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CanvasView.class.getSimpleName();

    private CanvasThread thread;
    private Context context;

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

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
        }
    }

    public void update() {

    }
}