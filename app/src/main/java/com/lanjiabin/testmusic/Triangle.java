package com.lanjiabin.testmusic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class Triangle extends View {
    private Paint mRadarPaint, mValuePaint;
    private int mCenterX; //中心X
    private int mCenterY; //中心Y
    private Path mLinePath; //外部容器的形状
    private Path mValuePath; //内部填充的形状
    private double mPercent;

    public Triangle(Context context) {
        super(context);
    }

    public Triangle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Triangle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 中心坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setPercent(double percent){
        mPercent=percent;
        init();
        invalidate();
    }

    public void init() {

        // 绘制外部容器
        mRadarPaint = new Paint();
        mRadarPaint.setAntiAlias(true);
        mRadarPaint.setStrokeWidth(2);
        mRadarPaint.setStyle(Paint.Style.STROKE);
        mRadarPaint.setColor(Color.WHITE);

        // 绘制填充内容
        mValuePaint = new Paint();
        mValuePaint.setStrokeWidth(2);
        mValuePaint.setColor(Color.GREEN);
        mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //外部容器路径
        mLinePath = new Path();

        //内部填充物路径
        mValuePath = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制外部容器
        drawLines(canvas);

        //绘制内部填充
        drawRegion(canvas, mPercent);
    }

    //绘制外部容器，顺时针 o-a-b-c
    public void drawLines(Canvas canvas) {
        mLinePath.reset();

        float xo = mCenterX;
        float yo = mCenterY;
        mLinePath.moveTo(xo, yo);

        float xa = (float) (mCenterX + 130);
        float ya = (float) (mCenterY);
        mLinePath.lineTo(xa, ya);

        float xb = (float) (mCenterX + 130);
        float yb = (float) (mCenterY - 43);
        mLinePath.lineTo(xb, yb);

        float xc = (float) (mCenterX);
        float yc = (float) (mCenterY - 15);
        mLinePath.lineTo(xc, yc);

        mLinePath.close();
        canvas.drawPath(mLinePath, mRadarPaint);

    }


    //绘制覆盖图层，顺时针 O-Q-P-C
    public void drawRegion(Canvas canvas, double Percent) {

        //直线CB 与QH 求出交点坐标P,
        // 其中H点=(是以B点的Y坐标，Q点的X坐标)，其实就是QP的延长线。
        double lineCX = mCenterX;
        double lineCY = mCenterY - 15;

        double lineBX = mCenterX + 130;
        double lineBY = mCenterY - 43;

        double lineQX = Percent * 130 + mCenterX;
        double lineQY = mCenterY;

        double lineHX = lineQX;
        double lineHY = lineBY;

        //求出交点P的坐标
        double[] result = reckon(lineCX, lineCY, lineBX, lineBY, lineQX, lineQY, lineHX, lineHY);

        double px;
        double py;

        px = result[0];
        py = result[1];


        float xo = mCenterX;
        float yo = mCenterY;

        float xq = (float) lineQX;
        float yq = (float) (mCenterY);

        float xp = (float) px;
        float yp = (float) py;

        float xc = (float) lineCX;
        float yc = (float) lineCY;

        mValuePath.reset();
        mValuePath.moveTo(xo, yo);
        mValuePath.lineTo(xq, yq);
        mValuePath.lineTo(xp, yp);
        mValuePath.lineTo(xc, yc);

        mValuePath.close();
        canvas.drawPath(mValuePath, mValuePaint);

    }

    //求出两直线相交的坐标
    public double[] reckon(double x0, double y0,
                           double x1, double y1,
                           double x2, double y2,
                           double x3, double y3) {
        double[] result = new double[2];
        double a, b, c, d, e, f;

        a = (y0 - y1) * (y3 - y2) * x0;
        b = (y3 - y2) * (x1 - x0) * y0;
        c = (y1 - y0) * (y3 - y2) * x2;
        d = (x2 - x3) * (y1 - y0) * y2;
        e = (x1 - x0) * (y3 - y2);
        f = (y0 - y1) * (x3 - x2);

        double resultY = (a + b + c + d) / (e + f);
        double resultX = x2 + (x3 - x2) * (resultY - y2) / (y3 - y2);

        result[0] = resultX;
        result[1] = resultY;

        return result;
    }
}
