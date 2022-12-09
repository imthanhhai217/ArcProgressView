package com.jaroid.arcview;

import static android.graphics.Bitmap.Config.ARGB_8888;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class ArcView extends View {

    private static final String TAG = "ArcView";

    private static final int START_ANGLE = 180;
    private final int COLOR_PROGRESS_DEFAULT = Color.parseColor("#91c4d1");
    private final int COLOR_BACKGROUND_DEFAULT = Color.parseColor("#23363b");
    private final long ARC_DURATION_DEFAULT = 500;
    private final int ARC_MAX_PROGRESS = 100;
    private final int DEFAULT_WIDTH = 300;
    private final int DEFAULT_HEIGHT = 150;
    private final int ARC_PROGRESS = 0;
    private final int ARC_PADDING = 0;
    private final float STROKE_WIDTH = 20;
    private int arcProgressColor = COLOR_PROGRESS_DEFAULT;
    private int arcProgressBackground = COLOR_BACKGROUND_DEFAULT;

    private Paint stripePaint;
    private Paint progressPaint;
    private Paint backgroundPaint;
    private RectF mRect;
    private float arcAngle;
    private long arcDuration = ARC_DURATION_DEFAULT;


    float width;
    float height;
    private float strokeProgressWidth;
    private float strokeBackgroundWidth;
    private float padding;
    private int userProgress;
    private int maxProgress;

    private boolean striped;
    private boolean animated;
    private boolean rounded;

    private boolean showPercentage;
    private Bitmap stripeTile;
    private Paint tilePaint;

    public ArcView(Context context) {
        super(context);
        initView(null);
    }


    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        this.arcAngle = 0;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;

        tilePaint = new Paint();

        stripePaint = new Paint();
        stripePaint.setStyle(Paint.Style.FILL);
        stripePaint.setAntiAlias(true);
        stripePaint.setColor(Color.YELLOW);

        // get attributes
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcView);

        try {
            this.width = a.getDimension(R.styleable.ArcView_android_layout_width, DEFAULT_WIDTH);
            this.height = a.getDimension(R.styleable.ArcView_android_layout_width, DEFAULT_HEIGHT);
            this.animated = a.getBoolean(R.styleable.ArcView_animated, false);
            this.rounded = a.getBoolean(R.styleable.ArcView_roundedCorners, false);
            this.striped = a.getBoolean(R.styleable.ArcView_striped, false);
            this.showPercentage = a.getBoolean(R.styleable.ArcView_showPercentage, false);
            this.userProgress = a.getInt(R.styleable.ArcView_arc_progress, ARC_PROGRESS);
            this.maxProgress = a.getInt(R.styleable.ArcView_arc_max_progress, ARC_MAX_PROGRESS);
            this.strokeProgressWidth = a.getDimension(R.styleable.ArcView_arc_progress_width, STROKE_WIDTH);
            this.strokeBackgroundWidth = a.getDimension(R.styleable.ArcView_arc_background_width, STROKE_WIDTH);
            this.padding = a.getDimension(R.styleable.ArcView_arc_padding, ARC_PADDING);
            this.arcProgressColor = a.getInt(R.styleable.ArcView_arc_progress_color, COLOR_PROGRESS_DEFAULT);
            this.arcProgressBackground = a.getInt(R.styleable.ArcView_arc_progress_background, COLOR_BACKGROUND_DEFAULT);
        } finally {
            a.recycle();
        }
    }

    private void initProgressPaint() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(arcProgressColor);
        progressPaint.setStrokeWidth(strokeProgressWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        if (rounded) {
            progressPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    private void initBackgroundPaint() {
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeWidth(strokeBackgroundWidth);
        backgroundPaint.setColor(arcProgressBackground);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        if (rounded) {
            backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        int halfStrokeSpace = (int) (Math.max(strokeProgressWidth, strokeBackgroundWidth) / 2);
        int removeSpace = (int) (halfStrokeSpace + padding);
        int max = (int) Math.max(width, height);
        Log.d(TAG, "onMeasure: " + removeSpace);
        if (rounded) {
            mRect = new RectF(0 + removeSpace, 0 + removeSpace, max - removeSpace, max - removeSpace);
            setMeasuredDimension(max, (max + removeSpace * 2) / 2);
        } else {
            mRect = new RectF(0 + removeSpace, 0 + removeSpace, max - removeSpace, max - removeSpace);
            setMeasuredDimension(max, max / 2);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        initBackgroundPaint();
        initProgressPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        Log.d(TAG, "onDraw: " + w + " | " + h);
        if (w <= 0 || h <= 0) {
            return;
        }

        //Draw background
        canvas.drawArc(mRect, START_ANGLE, START_ANGLE, false, backgroundPaint);

        //Draw progress
        canvas.drawArc(mRect, START_ANGLE, getArcAngle(), false, progressPaint);

//        float ratio = (drawnProgress / (float) maxProgress);
//        int lineEnd = (int) (w * ratio);
//        Log.d(TAG, "onDraw: lineEnd " + lineEnd);
//
//        float offset = 0;
//        float offsetFactor = (System.currentTimeMillis() % STRIPE_CYCLE_MS) / (float) STRIPE_CYCLE_MS;
//        if (striped && animated) { // determine offset for current animation frame of progress bar
//            offset = (h * 2) * offsetFactor;
//        }

//        if (striped) { // draw a regular striped bar
//            float pH = 0.5f;
//            if (stripeTile == null) {
//                stripeTile = createTile(20, pH, stripePaint, progressPaint);
//            }
//
//            float start = offset - stripeTile.getWidth();
//            Log.d(TAG, "onDraw: start " + start);
//
//            while (start < lineEnd) { // FIXME
//                canvas.drawBitmap(stripeTile, start, 0, mPaint);
//                start += stripeTile.getWidth() * pH;
//            }
//        } else { // draw a filled bar
//            canvas.drawRect(0, 0, lineEnd, h, progressPaint);
//        }

    }

    private static Bitmap createTile(float h, float pH, Paint stripePaint, Paint progressPaint) {
        Bitmap bm = Bitmap.createBitmap((int) h * 2, (int) h, ARGB_8888);
        Canvas tile = new Canvas(bm);

        float x = 0;

        Path path = new Path();

        path.moveTo(x, 0);
        path.lineTo(x, h);
        path.lineTo(h * pH, 0);
        tile.drawPath(path, stripePaint); // draw striped triangle

        path.reset();
        path.moveTo(x + h * pH, 0);
        path.lineTo(x, h);
        path.lineTo(x + h * pH, h);
        path.lineTo(x + (h * pH * 2), 0);
        tile.drawPath(path, progressPaint); // draw progress parallelogram

        x = h * pH;
        path.reset();
        path.moveTo(x, h);
        path.lineTo(x + h * pH, 0);
        path.lineTo(x + h * pH, h);
        tile.drawPath(path, stripePaint); // draw striped triangle (completing tile)

        return bm;
    }

    public void setArcAngle(float arcAngle) {
        this.arcAngle = arcAngle;
    }

    public float getArcAngle() {
        return arcAngle;
    }

    public void setProgress(int progress) {
        this.userProgress = progress;
        runArcAnimation(progress);
        invalidate();
    }

    public void setPadding(float padding) {
        this.padding = padding;
        invalidate();
    }

    public void setRounded(boolean rounded) {
        this.rounded = rounded;
        invalidate();
    }

    public void setArcDuration(long arcDuration) {
        this.arcDuration = arcDuration;
        invalidate();
    }

    public void setStrokeWidth(float strokeProgressWidth, float strokeBackgroundWidth) {
        this.strokeProgressWidth = strokeProgressWidth;
        this.strokeBackgroundWidth = strokeBackgroundWidth;
        invalidate();
    }

    private void runArcAnimation(int progress) {
        float angle = START_ANGLE * progress / maxProgress;
        ArcAnimation arcAnimation = new ArcAnimation(this, angle);
        arcAnimation.setDuration(arcDuration);
        this.startAnimation(arcAnimation);
    }
}
