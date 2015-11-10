package yanyan.drawingapp;

import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
/**
 * Created by Alex on 2015-10-20.
 */
public class DrawingView extends View{

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private float brushSize, lastBrushSize;

    private  boolean erase = false;

    private  int width, height;

    //comment to attached on the image
    private  String comment;

    public DrawingView(Context context, AttributeSet attrs ){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        //initialize drawPaint
        drawPaint.setColor(paintColor);

        //initialize drawPath
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        //drawCanvas.drawText("test", 20, 20, null);
    }

    public void setImageBitmap(Bitmap bmp){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        /*
        * */
        int bmp_width = bmp.getWidth();
        int bmp_height = bmp.getHeight();
        float scaleWidth = ((float) width) / bmp_width;
        float scaleHeight = ((float) height) / bmp_height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bmp, 0, 0, bmp_width, bmp_height, matrix, false);
        //

        drawCanvas.drawBitmap(resizedBitmap, 0, 0, paint);
        invalidate();
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setComment(String commentText){
        this.comment = commentText;
    }

    public void setErase(boolean isErase){
        //set erase true or false
        erase=isErase;
        if(erase){
            drawPaint.setAlpha(0xFF);
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


        }
        else {
            drawPaint.setXfermode(null);

        }
    }

    public void setBrushSize(float newSize){
        //update size

        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());

        brushSize=pixelAmount;

        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(comment != "" && comment != null ){
                    canvasPaint.setTextSize(60);
                    canvasPaint.setColor(paintColor);
                    drawCanvas.drawText(comment, touchX, touchY, canvasPaint);
                    comment = "";
                    return true;
                }
                else {
                    drawPath.moveTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor){
        //set color
        invalidate();

        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setColor(int newColor){
        invalidate();
        drawPaint.setColor(newColor);
    }

}


