package com.smwl.translate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class FloatingBallLayout extends LinearLayout {
    private float lastX = 0;
    private float lastY = 0;
    private boolean isMove = false;
    Runnable runnable;
    WindowManager.LayoutParams layoutParams;
    /*
    public FloatingBallLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        common_ctor(context);
    }

     */
    public FloatingBallLayout(Context context, WindowManager.LayoutParams layoutParams, Runnable runnable) {
        super(context);
        this.layoutParams = layoutParams;
        this.runnable = runnable;
        common_constructor(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void common_constructor(Context context) {
        LayoutInflater.from(context).inflate(R.layout.floating_ball, this);
        // https://blog.csdn.net/Ben_Fade/article/details/128218940 Movable
        final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMove = false;
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMove = true;
                        // delta of new click pos relative to init click pos
                        int dx = (int) (motionEvent.getRawX() - lastX);
                        int dy = (int) (motionEvent.getRawY() - lastY);
                        layoutParams.x += dx;
                        layoutParams.y += dy;
                        windowManager.updateViewLayout(FloatingBallLayout.this, layoutParams);
                        lastX = motionEvent.getRawX();
                        lastY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        runnable.run();
                        return isMove;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public float getLastX() {
        return lastX;
    }
    public float getLastY() {
        return lastY;
    }
}
