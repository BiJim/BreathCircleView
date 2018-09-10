package com.github.bijim.breathcircleview;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * BiJim
 * A Circle View containing some animations, such as "breath" ,radian rotate
 * 带呼吸，渐变，圆弧旋转动画效果的圆环
 * View is square，onMeasure() constraint it
 */
public class BreathCircleView extends View {
    private static final int DEFAULT_SHOW_DURATION = 350;
    private static final int DEFAULT_BREATH_DURATION = 1000;
    private static final int DEFAULT_HANDLE_DURATION = 600;
    private static final int DEFAULT_HANDLE_SWEEP_ANGLE = 80;
    private static final int DEFAULT_STROKE_WIDTH = 10;

    private long showAnimDuration;
    private long breathAnimDuration;
    private long handleAnimDuration;
    private int strokeWidth;

    private float ringStrokeWidth;

    private Paint ringPaint;
    private RectF oval;

    private float sweepAngle = 0;
    private float handleSweepAngle;
    private float startAngle = 0;

    private int colors[] = new int[4];
    private float positions[] = new float[4];

    private LinearGradient linearGradient;

    private ValueAnimator breathAnimator, showAnim, handleAnim;
    private AnimatorSet animatorSet;

    private boolean mStarted = false;

    private boolean handleMode;//handle mode flag
    private int viewSize;

    public BreathCircleView(Context context) {
        this(context, null);
    }

    public BreathCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BreathCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BreathCircleView);
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.BreathCircleView_bcv_stroke_width, DEFAULT_STROKE_WIDTH);
        handleSweepAngle = typedArray.getFloat(R.styleable.BreathCircleView_bcv_sweep_range, DEFAULT_HANDLE_SWEEP_ANGLE);
        showAnimDuration = typedArray.getInteger(R.styleable.BreathCircleView_bcv_anim_show_duration, DEFAULT_SHOW_DURATION);
        breathAnimDuration = typedArray.getInteger(R.styleable.BreathCircleView_bcv_anim_breath_duration, DEFAULT_BREATH_DURATION);
        handleAnimDuration = typedArray.getInteger(R.styleable.BreathCircleView_bcv_anim_handle_duration, DEFAULT_HANDLE_DURATION);
        typedArray.recycle();
        init();
    }

    private void init() {
        ringStrokeWidth = strokeWidth;
        ringPaint = new Paint();
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setAntiAlias(true);
        ringPaint.setStrokeWidth(ringStrokeWidth);
        initShader();
        //init animators
        showAnim = initShowAnim();
        breathAnimator = initBreathAnim();
        handleAnim = initHandleAnim();

        animatorSet = new AnimatorSet();
        animatorSet.play(breathAnimator).after(showAnim);
    }

    /**
     * init LinearGradient`s colors & positions
     */
    private void initShader() {
        //colors
        colors[0] = getContext().getColor(R.color.ring_one);
        colors[1] = getContext().getColor(R.color.blue_purple);
        colors[2] = getContext().getColor(R.color.ring_two);
        colors[3] = getContext().getColor(R.color.blue_purple);
        //percent
        positions[0] = 0.20f;
        positions[1] = 0.50f;
        positions[2] = 0.70f;
        positions[3] = 1.0f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec, true);
        int height = measure(heightMeasureSpec, false);
        if (width < height) {
            setMeasuredDimension(width, width);
        } else {
            setMeasuredDimension(height, height);
        }

    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight()
                : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth()
                    : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewSize = w;
        oval = new RectF(0, 0,
                viewSize, viewSize);
        linearGradient = new LinearGradient(0, 0,
                viewSize, viewSize, colors, null, Shader.TileMode.CLAMP);
        ringPaint.setShader(linearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw ring
        canvas.rotate(90, oval.centerX(), oval.centerY());
        canvas.drawArc(oval, startAngle, sweepAngle, false, ringPaint);
    }


    /**
     * show ring than start breath
     */
    public void startAnimSet() {
        if (!mStarted) {
            mStarted = true;
            animatorSet.start();
        }
    }

    /**
     * stop the anim set
     */
    public void stopAnimSet() {
        if (mStarted) {
            mStarted = false;
            breathAnimator.setFloatValues(0);
            animatorSet.end();
            animatorSet = null;
        }
    }

    /**
     * start with breath anim
     */
    public void startBreath() {
        if (!breathAnimator.isRunning()) {
            sweepAngle = 360;
            breathAnimator.setFloatValues(0, 1);
            breathAnimator.start();
        }


    }

    /**
     * stop breath anim
     */
    public void stopBreath() {
        if (breathAnimator.isRunning()) {
            breathAnimator.setFloatValues(0);
            breathAnimator.end();
        }

    }

    /**
     * start with handle anim
     */
    public void startHandle() {
        if (!handleAnim.isRunning()) {
            handleMode = true;
            handleAnim.start();
        }
    }

    /**
     * stop handle anim
     */
    public void stopHandle() {
        if (handleAnim.isRunning()) {
            handleMode = false;
            handleAnim.end();
        }
    }

    private ValueAnimator initShowAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(showAnimDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (Float) animation.getAnimatedValue();
                //change sweep angle
                sweepAngle = 360 * t + 0.5f;

                invalidate();
            }
        });
        return animator;
    }

    private ValueAnimator initBreathAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(breathAnimDuration);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (Float) animation.getAnimatedValue();
                int defaultAlpha = 80;
                int resultAlpha = (int) ((255 - defaultAlpha) * (1 - t) + defaultAlpha + 0.5f)
                        > 255 ? 255 : (int) ((255 - defaultAlpha) * (1 - t) + defaultAlpha + 0.5f);
                //change alpha
                ringPaint.setAlpha(resultAlpha);
                //change ring stroke width
                ringStrokeWidth += (ringStrokeWidth * t);
                ringPaint.setStrokeWidth(ringStrokeWidth);
                //reset to default width
                ringStrokeWidth = strokeWidth;

                invalidate();
            }
        });
        return animator;
    }

    private ValueAnimator initHandleAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(handleAnimDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (Float) animation.getAnimatedValue();
                //change start angle
                startAngle = t * 360;
                //judge mode flag than change sweep angle
                sweepAngle = handleMode ? handleSweepAngle : 360;

                invalidate();
            }
        });
        return animator;
    }

    public void setShowAnimDuration(long showAnimDuration) {
        this.showAnimDuration = showAnimDuration;
        showAnim.setDuration(showAnimDuration);
    }

    public void setBreathAnimDuration(long breathAnimDuration) {
        this.breathAnimDuration = breathAnimDuration;
        breathAnimator.setDuration(breathAnimDuration);
    }

    public void setHandleAnimDuration(long handleAnimDuration) {
        this.handleAnimDuration = handleAnimDuration;
        handleAnim.setDuration(handleAnimDuration);
    }

    public void setStrokeWidth(int dp) {
        strokeWidth = (int) dp2Pixel(dp, getContext());
        ringStrokeWidth = strokeWidth;
        ringPaint.setStrokeWidth(ringStrokeWidth);
        invalidate();
    }

    /**
     * use for handle anim
     *
     * @param handleSweepAngle from 0~360
     */
    public void setHandleSweepAngle(float handleSweepAngle) {
        this.handleSweepAngle = handleSweepAngle;
    }

    /**
     * set shader
     *
     * @param colors    int array about shader color, length must >=2
     * @param positions float array about color percent,this could be null,
     *                  if not null, colors length must equals position length
     */
    public void setShader(int colors[], float positions[]) {
        linearGradient = new LinearGradient(0, 0,
                viewSize, viewSize, colors, positions, Shader.TileMode.CLAMP);
        ringPaint.setShader(linearGradient);
        invalidate();
    }

    public void setColor(int color) {
        ringPaint.setShader(null);
        ringPaint.setColor(color);
        invalidate();
    }

    private float dp2Pixel(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }
}
