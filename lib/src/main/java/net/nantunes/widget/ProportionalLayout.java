/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nantunes.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Layout that calculates its height based on its width, or vice versa (depending on the set
 * {@link #setDirection(Direction)}. The factor is specified in {@link #setRatio(float)}.
 * <p>For {@link Direction#heightToWidth}: width := height * factor</p>
 * <p>For {@link Direction#widthToHeight}: height := width * factor</p>
 * <p>Only one child is allowed; if more are required, another ViewGroup can be used as the direct
 * child of this layout.</p>
 */
public class ProportionalLayout extends ViewGroup {
    private Direction mDirection;
    private float mRatio;

    public ProportionalLayout(Context context) {
        super(context);
    }

    public ProportionalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromAttributes(context, attrs);
    }

    public ProportionalLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFromAttributes(context, attrs);
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, net.nantunes.widget.R.styleable.ProportionalLayout);

        mDirection = Direction.parse(a.getString(net.nantunes.widget.R.styleable.ProportionalLayout_direction));

        float f = Float.NaN;
        try {
            f = a.getFloat(R.styleable.ProportionalLayout_ratio, Float.NaN);
        } catch (NumberFormatException ignored) {
        }

        if (Float.isNaN(f)) {
            try {
                f = a.getFraction(net.nantunes.widget.R.styleable.ProportionalLayout_ratio, 1, 1, Float.NaN);
            } catch (UnsupportedOperationException ignored) {
            }

            if (Float.isNaN(f)) {
                f = parseProportion(a.getString(R.styleable.ProportionalLayout_ratio));
            }
        } else if (f < 0) {
            switch ((int) f) {
                case -1:
                    f = 3f / 4f;
                    break;
                case -2:
                    f = 9f / 16f;
                    break;
                case -3:
                    f = 1f / 1.85f;
                    break;
                case -4:
                    f = 1f / 2.39f;
                    break;
                case -5:
                    f = 1f / 1.375f;
                    break;
                case -6:
                    f = 1f / 1.43f;
                    break;
                case -7:
                    f = 1f / 2.00f;
                    break;
                case -8:
                    f = (float) (1 / ((1 + Math.sqrt(5)) / 2));
                    break;
                case -9:
                    f = (float) (1 / (1 + Math.sqrt(2)));
                    break;
                case -10:
                    f = (float) Math.sqrt(2);
                    break;
                case -11:
                    f = 11f / 8.5f;
                    break;
            }
        }

        if (Float.isNaN(f)) {
            throw new IllegalStateException("ratio must be a valid proportion");
        }

        mRatio = f;

        a.recycle();
    }

    private float parseProportion(String s) {
        float f = Float.NaN;

        if (s.contains(":")) {
            String[] p = s.split(":");
            if (p.length == 2) {
                try {
                    f = Float.parseFloat(p[1]) / Float.parseFloat(p[0]);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() != 1) {
            throw new IllegalStateException("ProportionalLayout requires exactly one child");
        }

        final View child = getChildAt(0);
        // Do a first pass to get the optimal size
        measureChild(child, widthMeasureSpec, heightMeasureSpec);

        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();

        final int width;
        final int height;
        if (mDirection == Direction.heightToWidth) {
            width = Math.round(childHeight * mRatio);
            height = childHeight;
        } else {
            width = childWidth;
            height = Math.round(childWidth * mRatio);
        }

        // Do a second pass so that all children are informed of the new size
        measureChild(child,
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() != 1) {
            throw new IllegalStateException("ProportionalLayout requires exactly one child");
        }

        final View child = getChildAt(0);
        child.layout(0, 0, right - left, bottom - top);
    }

    public Direction getDirection() {
        return mDirection;
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    public void setRatio(String ratio) {
        float f = this.parseProportion(ratio);

        if (Float.isNaN(f)) {
            throw new IllegalStateException("ratio must be a valid proportion");
        }

        mRatio = f;
    }

    /**
     * Specifies whether the width should be calculated based on the height or vice-versa
     */
    public enum Direction {
        widthToHeight("width:height"),
        heightToWidth("height:width");

        public final String XmlName;

        private Direction(String xmlName) {
            XmlName = xmlName;
        }

        /**
         * Parses the given direction string and returns the Direction instance. This
         * should be used when inflating from xml
         */
        public static Direction parse(String value) {
            if (value == null || widthToHeight.XmlName.equals(value)) {
                return Direction.widthToHeight;
            } else if (heightToWidth.XmlName.equals(value)) {
                return Direction.heightToWidth;
            } else {
                throw new IllegalStateException("direction must be either " +
                        widthToHeight.XmlName + " or " + heightToWidth.XmlName);
            }
        }
    }
}