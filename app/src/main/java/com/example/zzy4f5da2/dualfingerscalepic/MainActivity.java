package com.example.zzy4f5da2.dualfingerscalepic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
    /*implements ScaleGestureDetector.OnScaleGestureListener*/ {


    private ImageView iv;
    private Bitmap bitmap1;
    private Bitmap alertBitmap;
    private Canvas canvas;
    Paint paint = new Paint();
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;//操作模式,执行移动还是缩放或者什么都不做
    float oldDist = 1f;
    PointF start = new PointF();
    PointF mid = new PointF();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = ((ImageView) findViewById(R.id.img));

        //  获取手机屏幕的信息
        DisplayMetrics display = getResources().getDisplayMetrics();

        bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.lanuch);
        //  放大的时候宽高也要适当改变,否则是原图大小,放大后不能完全显示
        alertBitmap = Bitmap.createBitmap(display.widthPixels, display.heightPixels, bitmap1.getConfig());

        canvas = new Canvas(alertBitmap);

        //把图片画在屏幕中心
        int left = display.widthPixels/2 - bitmap1.getWidth()/2;
        int top = display.heightPixels/2 - bitmap1.getHeight()/2;
        canvas.drawBitmap(bitmap1,left,top,paint);


        iv.setScaleType(ImageView.ScaleType.MATRIX);
        iv.setImageBitmap(alertBitmap);

/*
        TouchScaler scaler = new TouchScaler(display.widthPixels,display.heightPixels);
        iv.setOnTouchListener(scaler);
*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float scale;
        switch (event.getAction() & MotionEvent.ACTION_MASK){

            //只放下第一根手指
            case MotionEvent.ACTION_DOWN:
                //保存图片最后操作的状态
                savedMatrix.set(matrix);//!!!!不可以少  //保存上次的缩放状态
                start.set(event.getX(),event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;

            // 两根手指同时按下
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist>5f){
                    midPoint(mid,event);
                    mode = ZOOM;
                }
                break;

            /**
             *      savedmatrix是保存上次的缩放状态，
             *      setmatrix就是先把图片设置成上次的缩放状态，
             *      在上次缩放的基础上进行新的缩放。
             *      如果不调用setmatrix就是每次缩放都从图片的原始尺寸开始计算缩放
             */

            case MotionEvent.ACTION_MOVE:
                if(mode == DRAG){   //单指移动时
                    matrix.set(savedMatrix);    //todo
                    //从上次保存的状态进行相对的移动?
                    matrix.postTranslate(event.getX()-start.x,event.getY()-start.y);
                }else if(mode == ZOOM){ //双指移动时
                    float newDist = spacing(event); //计算两指距离
                    if(newDist>5f){
                        matrix.set(savedMatrix);    //todo
                        scale = newDist/oldDist;    //放大倍数 = 新的两指距离/触摸时两指距离
                        //从上次保存的状态进行相对的放大?
                        matrix.postScale(scale,scale,mid.x,mid.y);
                    }
                }
                break;
        }

        iv.setImageMatrix(matrix);

        return super.onTouchEvent(event);
    }
/*
    public class TouchScaler implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return true;
        }
    }*/

    /**检查两个手指接触时的间距*/
    private float spacing(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x*x+y*y);
    }

    /**计算两个手指的中点*/
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

/*
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
    */
}
