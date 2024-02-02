package com.google.android.flexbox;

import android.support.v7.preference.Preference;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FlexLine {
    int mCrossSize;
    int mDividerLengthInMainSize;
    int mFirstIndex;
    int mGoneItemCount;
    int mItemCount;
    int mLastIndex;
    int mMainSize;
    int mMaxBaseline;
    int mSumCrossSizeBefore;
    float mTotalFlexGrow;
    float mTotalFlexShrink;
    int mLeft = Preference.DEFAULT_ORDER;
    int mTop = Preference.DEFAULT_ORDER;
    int mRight = Integer.MIN_VALUE;
    int mBottom = Integer.MIN_VALUE;
    List<Integer> mIndicesAlignSelfStretch = new ArrayList();

    public int getCrossSize() {
        return this.mCrossSize;
    }

    public int getItemCount() {
        return this.mItemCount;
    }

    public int getItemCountNotGone() {
        return this.mItemCount - this.mGoneItemCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updatePositionFromView(View view, int leftDecoration, int topDecoration, int rightDecoration, int bottomDecoration) {
        FlexItem flexItem = (FlexItem) view.getLayoutParams();
        this.mLeft = Math.min(this.mLeft, (view.getLeft() - flexItem.getMarginLeft()) - leftDecoration);
        this.mTop = Math.min(this.mTop, (view.getTop() - flexItem.getMarginTop()) - topDecoration);
        this.mRight = Math.max(this.mRight, view.getRight() + flexItem.getMarginRight() + rightDecoration);
        this.mBottom = Math.max(this.mBottom, view.getBottom() + flexItem.getMarginBottom() + bottomDecoration);
    }
}
