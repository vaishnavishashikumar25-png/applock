package com.example.applock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PatternView extends View {

    public interface OnPatternListener {
        void onStarted();
        void onProgress(List<Integer> progressStack);
        void onCleared();
        void onComplete(List<Integer> pattern);
    }

    private Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path linePath = new Path();
    private List<Integer> selectedDots = new ArrayList<>();
    private OnPatternListener listener;
    private float lastX, lastY;
    private boolean isDrawing = false;

    public PatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dotPaint.setColor(Color.LTGRAY);
        dotPaint.setStyle(Paint.Style.FILL);
        
        linePaint.setColor(Color.BLUE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(15f);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setOnPatternListener(OnPatternListener listener) {
        this.listener = listener;
    }

    public void clearPattern() {
        selectedDots.clear();
        isDrawing = false;
        invalidate();
        if (listener != null) listener.onCleared();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cellWidth = getWidth() / 3f;
        float cellHeight = getHeight() / 3f;

        // Draw dots
        for (int i = 0; i < 9; i++) {
            float cx = (i % 3 + 0.5f) * cellWidth;
            float cy = (i / 3 + 0.5f) * cellHeight;
            dotPaint.setColor(selectedDots.contains(i) ? Color.BLUE : Color.LTGRAY);
            canvas.drawCircle(cx, cy, 30f, dotPaint);
        }

        // Draw lines
        if (!selectedDots.isEmpty()) {
            linePath.reset();
            for (int i = 0; i < selectedDots.size(); i++) {
                int dotIndex = selectedDots.get(i);
                float cx = (dotIndex % 3 + 0.5f) * cellWidth;
                float cy = (dotIndex / 3 + 0.5f) * cellHeight;
                if (i == 0) linePath.moveTo(cx, cy);
                else linePath.lineTo(cx, cy);
            }
            if (isDrawing) {
                linePath.lineTo(lastX, lastY);
            }
            canvas.drawPath(linePath, linePaint);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                clearPattern();
                isDrawing = true;
                if (listener != null) listener.onStarted();
                checkDot(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = x;
                lastY = y;
                checkDot(x, y);
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                if (listener != null) listener.onComplete(new ArrayList<>(selectedDots));
                break;
        }
        invalidate();
        return true;
    }

    private void checkDot(float x, float y) {
        float cellWidth = getWidth() / 3f;
        float cellHeight = getHeight() / 3f;
        float radius = 60f; // Detection radius

        for (int i = 0; i < 9; i++) {
            float cx = (i % 3 + 0.5f) * cellWidth;
            float cy = (i / 3 + 0.5f) * cellHeight;
            if (Math.hypot(x - cx, y - cy) < radius) {
                if (!selectedDots.contains(i)) {
                    selectedDots.add(i);
                    if (listener != null) listener.onProgress(new ArrayList<>(selectedDots));
                }
                break;
            }
        }
    }
}