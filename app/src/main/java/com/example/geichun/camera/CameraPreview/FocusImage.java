package com.example.geichun.camera.CameraPreview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;

/**
 * Created by geichun on 16/8/3.
 */
public class FocusImage extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "CameraPreview";
    private Context mContext;
    private SurfaceHolder holderTransparent;
    private SurfaceView transparentView;

    public FocusImage(Context context,AttributeSet attrs){
        super(context,attrs);
     //   mContext = context;

     //   transparentView = (SurfaceView)findViewById(R.id.TransparentView);
        holderTransparent =getHolder();
        holderTransparent.addCallback(this);
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        // empty. surfaceChanged will take care of stuff
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        //empty.The method is called when the size of the viewport is changed or first created.
    }

    public void DrawFocusRect(Rect rec, int color)
    {
        Canvas canvas = holderTransparent.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(3);
        canvas.drawRect(rec, paint);
        holderTransparent.unlockCanvasAndPost(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "second event!!!!!!!");
        if (event.getPointerCount() == 1) {
            Rect touchRect = new Rect(
                    (int) (event.getX() - 100),
                    (int) (event.getY() - 100),
                    (int) (event.getX() + 100),
                    (int) (event.getY() + 100));
            DrawFocusRect(touchRect, Color.YELLOW);
        }
        return true;
    }

}