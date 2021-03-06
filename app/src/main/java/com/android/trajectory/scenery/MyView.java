package com.android.trajectory.scenery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.View;


//实现战争迷雾部分代码
public class MyView extends View {
    private int SCREEN_W;
    private int SCREEN_H;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;




    public MyView(Context context) {
        super(context);
        //setFocusable(true);
        setScreenWH();
        setting();


        // 1.如果覆盖物为图像,你可以调用如下方法
        //Bitmap bm = createBitmapFromSRC();

        // 2.如果覆盖层是纯色
        Bitmap bm = createBitmapFromARGB(0xFF000000, SCREEN_W, SCREEN_H);
        bm = setBitmapAlpha(bm,200);  // 设置纯色图层的透明度
        setCoverBitmap(bm);

    }

    private void setScreenWH() {
        // 得到屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        dm = this.getResources().getDisplayMetrics();
        // 得到屏幕宽度
        int screenWidth = dm.widthPixels;
        // 得到屏幕高度
        int screenHeight = dm.heightPixels;

        SCREEN_W = screenWidth;
        SCREEN_H = screenHeight;
    }

    private void setting(){
        // setting paint
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAntiAlias(true);

        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(50);  //笔画宽度
        //绘制杂点轨迹
        mPaint.setPathEffect(new DiscretePathEffect(3.0F, 5.0F));

        //set path
        mPath = new Path();


        // converting bitmap into mutable bitmap
        mBitmap = Bitmap.createBitmap(SCREEN_W, SCREEN_H, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mBitmap);
    }

    private Bitmap createBitmapFromARGB(int colorARGB, int width, int height) {
        int[] argb = new int[width * height];

        for (int i = 0; i < argb.length; i++) {

            argb[i] = colorARGB;

        }
        return Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888);
    }

    private Bitmap setBitmapAlpha(Bitmap bm, int alpha) {
        int[] argb = new int[bm.getWidth() * bm.getHeight()];
        bm.getPixels(argb, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm
                .getHeight());

        for (int i = 0; i < argb.length; i++) {

            argb[i] = ((alpha << 24) | (argb[i] & 0x00FFFFFF));
        }
        return Bitmap.createBitmap(argb, bm.getWidth(), bm.getHeight(),
                Bitmap.Config.ARGB_8888);
    }

    private void setCoverBitmap(Bitmap bm) {
        // drawXY will result on that Bitmap
        // be sure parameter is bm, not mBitmap
        mCanvas.drawBitmap(bm, 0, 0, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        super.onDraw(canvas);
    }

    public void start_pot(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
    }

    public void line(float x,float y){
        mPath.lineTo(x,y);
        mCanvas.drawPath(mPath, mPaint);
        mPath.moveTo(x, y);
        //每次View组件上的图形状态数据发生改变时,都应该通知View组件重写调用onDraw(Canvas canvas)方法重绘该组件
        //通知View重绘可调用invalidate(在UI线程中) 或 postInvalidate(在非UI线程中)
        invalidate();
    }
}

