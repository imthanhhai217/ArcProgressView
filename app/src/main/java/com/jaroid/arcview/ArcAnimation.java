package com.jaroid.arcview;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ArcAnimation extends Animation {

    private ArcView arcView;

    private float oldAngle;
    private float newAngle;
    private static float currentAngle = 0;

    public ArcAnimation(ArcView arcView, float newAngle) {
        this.arcView = arcView;
        this.newAngle = newAngle;
    }

    public void setNewAngle(float newAngle) {
        this.newAngle = newAngle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        oldAngle = arcView.getArcAngle();
        float angle = 0 + ((newAngle - oldAngle) * interpolatedTime);
        arcView.setArcAngle(angle);
        arcView.requestLayout();
    }
}
