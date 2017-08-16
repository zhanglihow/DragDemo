package com.sikan.dragdemo;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private List<CustomBitmap> _bitmaps;
    private Context _context;
    private CustomBitmap _curCustomBitmap;//当前操作的图形

    private Matrix currentMatrix = new Matrix();

    private float oldDist;
    private float newDist;
    private float scale;


    /**
     * 模式 NONE：无 DRAG：拖拽. ZOOM:缩放
     *
     * @author zhangjia
     */
    private enum MODE {
        NONE, DRAG, ZOOM
    }

    /**
     * 设置偏移
     */
    public enum TYPE {
        UP, DOWN, LEFT, RIGHT
    }

    private MODE mode = MODE.NONE;// 默认模式

    public DrawingView(Context context) {
        super(context);
        this._context = context;
        _bitmaps = new ArrayList<>();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._context = context;
        _bitmaps = new ArrayList<>();
    }

    public void addBitmap(CustomBitmap bitmap) {
        _bitmaps.add(bitmap);
    }

    //设置偏移
    public void setOffset(TYPE type) {
        if (type == TYPE.UP) {
            _curCustomBitmap.matrix.postTranslate(0, -1);
        } else if (type == TYPE.DOWN) {
            _curCustomBitmap.matrix.postTranslate(0, 1);
        } else if (type == TYPE.LEFT) {
            _curCustomBitmap.matrix.postTranslate(-1, 0);
        } else if (type == TYPE.RIGHT) {
            _curCustomBitmap.matrix.postTranslate(1, 0);
        }
        invalidate();
    }


    public List<CustomBitmap> getViews() {
        return _bitmaps;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        //画在后面的图显示在上面
        for (CustomBitmap bitmap : _bitmaps) {
            canvas.drawBitmap(bitmap.getBitmap(), bitmap.matrix, paint);
        }
    }


    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return
     */
    public float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两点之间的中间点
     *
     * @param event
     * @return
     */
    public PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:// 手指压下屏幕
                mode = MODE.DRAG;
                if (_curCustomBitmap == null && _bitmaps.size() > 0) {
                    _curCustomBitmap = _bitmaps.get(_bitmaps.size() - 1);
                }

                for (CustomBitmap bitmap : _bitmaps) {
                    float[] values = new float[9];
                    bitmap.matrix.getValues(values);
                    float globalX = values[Matrix.MTRANS_X];
                    float globalY = values[Matrix.MTRANS_Y];
                    float width = bitmap.scaleW;
                    float height = bitmap.scaleH;
                    float midX = globalX + width / 2;
                    float midY = globalY + height / 2;
                    PointF pointF = new PointF(midX, midY);
                    bitmap.midPoint = pointF;
                }

                for (CustomBitmap bitmap : _bitmaps) {
                    float[] values = new float[9];
                    bitmap.matrix.getValues(values);
                    float globalX = values[Matrix.MTRANS_X];
                    float globalY = values[Matrix.MTRANS_Y];
                    float width= bitmap.scaleW;
                    float height = bitmap.scaleH;

                    Rect rect = new Rect((int) globalX, (int) globalY, (int) (globalX + width), (int) (globalY + height));

                    //正常
                    if (event.getX() > rect.left && event.getX() < rect.right && event.getY() > rect.top && event.getY() < rect.bottom){
                        _curCustomBitmap = bitmap;
//                        KLog.e(_curCustomBitmap.getId()+" 正常");
                        change(true,event);
                        break;
                    }
                    //上下颠倒
                    else if(event.getX() < rect.left && event.getX() > rect.left-width && event.getY() < rect.top && event.getY() > rect.top-height){
                        _curCustomBitmap = bitmap;
//                        KLog.e(_curCustomBitmap.getId()+" 颠倒");
                        change(true,event);
                        break;
                    }
                    //右转
                    else if(event.getX() > rect.left-height && event.getX() < rect.left && event.getY() > rect.top && event.getY() < rect.top+width){
                        _curCustomBitmap = bitmap;
//                        KLog.e(_curCustomBitmap.getId()+" 右");
                        change(true,event);
                        break;
                    }
                    //左转
                    else if(event.getX() > rect.left && event.getX() < rect.left+height && event.getY() > rect.top-width && event.getY() <rect.top) {
                        _curCustomBitmap = bitmap;
//                        KLog.e(_curCustomBitmap.getId()+" 左");
                        change(true,event);
                        break;
                    }
                }
                change(false,event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:// 当屏幕上还有触点（手指），再有一个手指压下屏幕
                mode = MODE.ZOOM;
                oldDist = spacing(event);
                _curCustomBitmap.oldRotation = rotation(event);
                _curCustomBitmap.startDis = distance(event);
                if (_curCustomBitmap.startDis > 10f) {
                    _curCustomBitmap.midPoint = mid(event);
                    currentMatrix.set(_curCustomBitmap.matrix);// 记录ImageView当前的缩放倍数
                }
                break;
            case MotionEvent.ACTION_MOVE:// 手指在屏幕移动，该 事件会不断地触发
                if (mode == MODE.DRAG) {
                    float dx = event.getX() - _curCustomBitmap.startPoint.x;// 得到在x轴的移动距离
                    float dy = event.getY() - _curCustomBitmap.startPoint.y;// 得到在y轴的移动距离
                    _curCustomBitmap.matrix.set(currentMatrix);// 在没有进行移动之前的位置基础上进行移动
                    _curCustomBitmap.matrix.postTranslate(dx, dy);
                } else if (mode == MODE.ZOOM) {// 缩放与旋转
                    float endDis = distance(event);// 结束距离
                    _curCustomBitmap.rotation = rotation(event) - _curCustomBitmap.oldRotation;
                    if (endDis > 10f) {
                        scale = endDis / _curCustomBitmap.startDis;// 得到缩放倍数
                        _curCustomBitmap.matrix.set(currentMatrix);
                        _curCustomBitmap.matrix.postScale(scale, scale, _curCustomBitmap.midPoint.x, _curCustomBitmap.midPoint.y);
                        _curCustomBitmap.matrix.postRotate(_curCustomBitmap.rotation, _curCustomBitmap.midPoint.x, _curCustomBitmap.midPoint.y);
                    }
                    newDist = spacing(event);
                    if (newDist > oldDist + 1 || newDist < oldDist - 1) {
                        scaleAll(newDist / oldDist);
                        oldDist = newDist;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:// 手指离开屏
                break;
            case MotionEvent.ACTION_POINTER_UP:// 有手指离开屏幕,但屏幕还有触点（手指）
                mode = MODE.NONE;
                setWh(scale);
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 获取旋转角度
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 缩放倍数
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void scaleAll(float scale) {
        for (CustomBitmap bit : _bitmaps) {
            bit.matrix.postScale(scale, scale, bit.midPoint.x, bit.midPoint.y);
        }
    }

    private void setWh(float scale) {
        for (CustomBitmap bit : _bitmaps) {
            bit.scaleW = (int) (bit.scaleW * scale);
            bit.scaleH = (int) (bit.scaleH * scale);

            float[] values = new float[9];
            bit.matrix.getValues(values);
            float globalX = values[Matrix.MTRANS_X];
            float globalY = values[Matrix.MTRANS_Y];
            float width= bit.scaleW;
            float height = bit.scaleH;

            Rect rect = new Rect((int) globalX, (int) globalY, (int) (globalX + width), (int) (globalY + height));

            bit.x=rect.left;
            bit.y=rect.top;
            bit.scale=scale;
        }
    }

    private void change(boolean isChanged,MotionEvent event){
        //切换操作对象，只要把这个对象添加到栈底就行
        if (isChanged) {
            _bitmaps.remove(_curCustomBitmap);
            _bitmaps.add(_curCustomBitmap);
        }
        currentMatrix.set(_curCustomBitmap.matrix);// 记录ImageView当前的移动位置
        _curCustomBitmap.matrix.set(currentMatrix);
        _curCustomBitmap.startPoint.set(event.getX(), event.getY());
        postInvalidate();
    }


}
