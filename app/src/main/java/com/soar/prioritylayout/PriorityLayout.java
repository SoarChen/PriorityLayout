package com.soar.prioritylayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Soar on 2015/9/23.
 */
public class PriorityLayout extends ViewGroup {

    private static final String TAG = "PriorityLayout";

    private int mTotalLength = 0;

    public PriorityLayout(Context context) {
        super(context);

    }

    public PriorityLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriorityLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutHorizontal(changed, l, t, r, b);
    }

    private void layoutHorizontal(boolean changed, int l , int t, int r, int b) {
        final int count = getChildCount();

        int childLeft = 0;
        int childTop = 0;
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            child.layout(childLeft, childTop, childLeft + width, childTop + height);
            childLeft += width;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        mTotalLength = 0;
        final int childCount = getChildCount();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "Soar aaaa width Size = " + parentWidthSize + " height Size = " + parentHeightSize);
        Log.d(TAG, "Soar aaaa widthMode = " + widthMode + " heightMode = " + heightMode);
        PriorityQueue<View> childPriorityQueue = new PriorityQueue(childCount, new ViewPriorityComparator());

        // Add children to priority queue
        View child = null;
        for (int i = 0; i < childCount; ++i) {
            child = getChildAt(i);
            if (child == null) {
                continue;
            }
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            childPriorityQueue.add(child);
        }

        int maxChildHeight = 0;
        while (!childPriorityQueue.isEmpty()) {
            child = childPriorityQueue.poll();
            PriorityLayout.LayoutParams lp = (PriorityLayout.LayoutParams)
                    child.getLayoutParams();
            final int childWidthMeasureSpec;

            childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                                lp.leftMargin + lp.rightMargin + mTotalLength,
                        lp.width);

            int childHeightMeasureSpec;

            childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                    lp.topMargin + lp.bottomMargin,
                    lp.height);


            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            maxChildHeight = Math.max(childHeight, maxChildHeight);
            mTotalLength += childWidth + lp.leftMargin + lp.rightMargin;
            Log.d(TAG, "Soar xxxx widthSize = " + widthSize);
            Log.d(TAG, "Soar xxxx heightSize = " + heightSize);
        }
        Log.d(TAG, "Soar xxxx mTotalLength = " + mTotalLength);
        MarginLayoutParams params = (MarginLayoutParams)getLayoutParams();
        int finalHeight;
        Log.d(TAG, "Soar xxxx heightMode = " + heightMode);
        if (heightMode == MeasureSpec.AT_MOST)
            finalHeight = maxChildHeight;
        else
            finalHeight = parentHeightSize;

        int finalWidth;
        if (widthMode == MeasureSpec.AT_MOST)
            finalWidth = mTotalLength;
        else
           finalWidth = parentWidthSize;

        setMeasuredDimension(finalWidth, finalHeight);
        
        Log.d(TAG, "Soar xxxx getMeasuredWidth = " +  getMeasuredWidth());
        Log.d(TAG, "Soar xxxx getMeasuredHeight = " +  getMeasuredHeight());
    }

    class ViewPriorityComparator implements Comparator<View> {
        @Override
        public int compare(View lhs, View rhs) {
            final PriorityLayout.LayoutParams leftLp = (PriorityLayout.LayoutParams)
                    lhs.getLayoutParams();
            final PriorityLayout.LayoutParams rightLp = (PriorityLayout.LayoutParams)
                    rhs.getLayoutParams();

            int leftPriority = leftLp.priority;
            int rightPriority = rightLp.priority;

            if (leftPriority <= rightPriority)
                return -1;
            else
                return 1;

        }


    }

    public static class LayoutParams extends MarginLayoutParams {

        public int priority;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PriorityLayout_Layout);

            priority = a.getInt(R.styleable.PriorityLayout_Layout_layout_priority, 0);

            a.recycle();
        }


        public LayoutParams(int width, int height) {
            super(width, height);
            priority = -1;
        }


        public LayoutParams(int width, int height, int priority) {
            super(width, height);
            this.priority = priority;
        }


        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return generateDefaultLayoutParams(); // TODO Change this?
    }




}
