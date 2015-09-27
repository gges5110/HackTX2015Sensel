package com.example.android.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.bluetoothchat.SenselInput;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gerry on 2015/9/26.
 */
public class CanvasView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    private HashMap<Path, Float> strokeWidthMemory;
    private static final String TAG = "CanvasView";


    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
        color_list = new ArrayList<>();
        color_list.add(Color.BLACK);
        color_list.add(Color.RED);
        color_list.add(Color.BLUE);
        color_list.add(Color.GREEN);
        color_index = 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //here you have the size of the view and you can do stuff
        Log.v(TAG, "left = " + l + ", t = " + t + ", r = " + r + ", b = " + b);

    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Log.v(TAG, "width = " + w + ", height = " + h);
        width = w;
        height = h;
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        canvas.drawPath(mPath, mPaint);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private ArrayList<Integer> color_list;
    private int color_index = 0;

    public void changeColor() {
        color_index++;
        mPaint.setColor(color_list.get(color_index % color_list.size()));
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    public boolean onSenselEvent(SenselInput event) {
        if(event.getForce() < 500 && !SenselInput.Event.END.equals(event.getEvent()) )
            return false;
        if(event.getContactID() > 0)
            return false;


        float x = event.getY()*width/120;
        float y = height - event.getX()*height/230;
        Log.v(TAG, "x = " + x + ", y = " + y);
        mPaint.setStrokeWidth(event.getForce()/1000) ;

        if(SenselInput.Event.START.equals(event.getEvent())) {
            startTouch(x, y);
            invalidate();
        }
        else if (SenselInput.Event.MOVE.equals(event.getEvent())) {
            moveTouch(x, y);
            invalidate();
        }
        else if (SenselInput.Event.END.equals(event.getEvent())) {
            upTouch();
            invalidate();
        }
        return true;
    }
}