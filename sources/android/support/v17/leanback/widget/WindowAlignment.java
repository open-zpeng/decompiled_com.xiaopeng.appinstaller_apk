package android.support.v17.leanback.widget;

import android.support.v7.preference.Preference;
/* loaded from: classes.dex */
class WindowAlignment {
    private int mOrientation = 0;
    public final Axis vertical = new Axis("vertical");
    public final Axis horizontal = new Axis("horizontal");
    private Axis mMainAxis = this.horizontal;
    private Axis mSecondAxis = this.vertical;

    /* loaded from: classes.dex */
    public static class Axis {
        private int mMaxEdge;
        private int mMaxScroll;
        private int mMinEdge;
        private int mMinScroll;
        private String mName;
        private int mPaddingMax;
        private int mPaddingMin;
        private boolean mReversedFlow;
        private int mSize;
        private int mPreferredKeyLine = 2;
        private int mWindowAlignment = 3;
        private int mWindowAlignmentOffset = 0;
        private float mWindowAlignmentOffsetPercent = 50.0f;

        public Axis(String name) {
            reset();
            this.mName = name;
        }

        public final void setWindowAlignment(int windowAlignment) {
            this.mWindowAlignment = windowAlignment;
        }

        final boolean isPreferKeylineOverHighEdge() {
            return (this.mPreferredKeyLine & 2) != 0;
        }

        final boolean isPreferKeylineOverLowEdge() {
            return (this.mPreferredKeyLine & 1) != 0;
        }

        public final int getMinScroll() {
            return this.mMinScroll;
        }

        public final void invalidateScrollMin() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMinScroll = Integer.MIN_VALUE;
        }

        public final int getMaxScroll() {
            return this.mMaxScroll;
        }

        public final void invalidateScrollMax() {
            this.mMaxEdge = Preference.DEFAULT_ORDER;
            this.mMaxScroll = Preference.DEFAULT_ORDER;
        }

        void reset() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMaxEdge = Preference.DEFAULT_ORDER;
        }

        public final boolean isMinUnknown() {
            return this.mMinEdge == Integer.MIN_VALUE;
        }

        public final boolean isMaxUnknown() {
            return this.mMaxEdge == Integer.MAX_VALUE;
        }

        public final void setSize(int size) {
            this.mSize = size;
        }

        public final int getSize() {
            return this.mSize;
        }

        public final void setPadding(int paddingMin, int paddingMax) {
            this.mPaddingMin = paddingMin;
            this.mPaddingMax = paddingMax;
        }

        public final int getPaddingMin() {
            return this.mPaddingMin;
        }

        public final int getPaddingMax() {
            return this.mPaddingMax;
        }

        public final int getClientSize() {
            return (this.mSize - this.mPaddingMin) - this.mPaddingMax;
        }

        final int calculateKeyline() {
            int keyLine;
            int keyLine2;
            if (!this.mReversedFlow) {
                if (this.mWindowAlignmentOffset >= 0) {
                    keyLine2 = this.mWindowAlignmentOffset;
                } else {
                    int keyLine3 = this.mSize;
                    keyLine2 = keyLine3 + this.mWindowAlignmentOffset;
                }
                if (this.mWindowAlignmentOffsetPercent != -1.0f) {
                    return keyLine2 + ((int) ((this.mSize * this.mWindowAlignmentOffsetPercent) / 100.0f));
                }
                return keyLine2;
            }
            int keyLine4 = this.mWindowAlignmentOffset;
            if (keyLine4 >= 0) {
                keyLine = this.mSize - this.mWindowAlignmentOffset;
            } else {
                int keyLine5 = this.mWindowAlignmentOffset;
                keyLine = -keyLine5;
            }
            if (this.mWindowAlignmentOffsetPercent != -1.0f) {
                return keyLine - ((int) ((this.mSize * this.mWindowAlignmentOffsetPercent) / 100.0f));
            }
            return keyLine;
        }

        final int calculateScrollToKeyLine(int viewCenterPosition, int keyLine) {
            return viewCenterPosition - keyLine;
        }

        /* JADX WARN: Code restructure failed: missing block: B:11:0x0027, code lost:
            r6.mMinScroll = r6.mMinEdge - r6.mPaddingMin;
         */
        /* JADX WARN: Code restructure failed: missing block: B:21:0x0048, code lost:
            r6.mMaxScroll = (r6.mMaxEdge - r6.mPaddingMin) - r0;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final void updateMinMax(int r7, int r8, int r9, int r10) {
            /*
                Method dump skipped, instructions count: 235
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.WindowAlignment.Axis.updateMinMax(int, int, int, int):void");
        }

        public final int getScroll(int viewCenter) {
            int size = getSize();
            int keyLine = calculateKeyline();
            boolean isMinUnknown = isMinUnknown();
            boolean isMaxUnknown = isMaxUnknown();
            if (!isMinUnknown) {
                int keyLineToMinEdge = keyLine - this.mPaddingMin;
                if (this.mReversedFlow ? (this.mWindowAlignment & 2) != 0 : (this.mWindowAlignment & 1) != 0) {
                    if (viewCenter - this.mMinEdge <= keyLineToMinEdge) {
                        int alignToMin = this.mMinEdge - this.mPaddingMin;
                        if (!isMaxUnknown && alignToMin > this.mMaxScroll) {
                            return this.mMaxScroll;
                        }
                        return alignToMin;
                    }
                }
            }
            if (!isMaxUnknown) {
                int keyLineToMaxEdge = (size - keyLine) - this.mPaddingMax;
                if (this.mReversedFlow ? (this.mWindowAlignment & 1) != 0 : (this.mWindowAlignment & 2) != 0) {
                    if (this.mMaxEdge - viewCenter <= keyLineToMaxEdge) {
                        int alignToMax = this.mMaxEdge - (size - this.mPaddingMax);
                        if (!isMinUnknown && alignToMax < this.mMinScroll) {
                            return this.mMinScroll;
                        }
                        return alignToMax;
                    }
                }
            }
            int keyLineToMaxEdge2 = calculateScrollToKeyLine(viewCenter, keyLine);
            return keyLineToMaxEdge2;
        }

        public final void setReversedFlow(boolean reversedFlow) {
            this.mReversedFlow = reversedFlow;
        }

        public String toString() {
            return " min:" + this.mMinEdge + " " + this.mMinScroll + " max:" + this.mMaxEdge + " " + this.mMaxScroll;
        }
    }

    public final Axis mainAxis() {
        return this.mMainAxis;
    }

    public final Axis secondAxis() {
        return this.mSecondAxis;
    }

    public final void setOrientation(int orientation) {
        this.mOrientation = orientation;
        if (this.mOrientation == 0) {
            this.mMainAxis = this.horizontal;
            this.mSecondAxis = this.vertical;
            return;
        }
        this.mMainAxis = this.vertical;
        this.mSecondAxis = this.horizontal;
    }

    public final void reset() {
        mainAxis().reset();
    }

    public String toString() {
        return "horizontal=" + this.horizontal + "; vertical=" + this.vertical;
    }
}
