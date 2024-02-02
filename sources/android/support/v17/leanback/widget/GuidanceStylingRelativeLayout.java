package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.R;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
/* loaded from: classes.dex */
class GuidanceStylingRelativeLayout extends RelativeLayout {
    private float mTitleKeylinePercent;

    public GuidanceStylingRelativeLayout(Context context) {
        this(context, null);
    }

    public GuidanceStylingRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuidanceStylingRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTitleKeylinePercent = getKeyLinePercent(context);
    }

    public static float getKeyLinePercent(Context context) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.LeanbackGuidedStepTheme);
        float percent = ta.getFloat(R.styleable.LeanbackGuidedStepTheme_guidedStepKeyline, 40.0f);
        ta.recycle();
        return percent;
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b2) {
        super.onLayout(changed, l, t, r, b2);
        View mTitleView = getRootView().findViewById(R.id.guidance_title);
        View mBreadcrumbView = getRootView().findViewById(R.id.guidance_breadcrumb);
        View mDescriptionView = getRootView().findViewById(R.id.guidance_description);
        ImageView mIconView = (ImageView) getRootView().findViewById(R.id.guidance_icon);
        int mTitleKeylinePixels = (int) ((getMeasuredHeight() * this.mTitleKeylinePercent) / 100.0f);
        if (mTitleView != null && mTitleView.getParent() == this) {
            int titleViewBaseline = mTitleView.getBaseline();
            int mBreadcrumbViewHeight = mBreadcrumbView.getMeasuredHeight();
            int guidanceTextContainerTop = ((mTitleKeylinePixels - titleViewBaseline) - mBreadcrumbViewHeight) - mTitleView.getPaddingTop();
            int offset = guidanceTextContainerTop - mBreadcrumbView.getTop();
            if (mBreadcrumbView != null && mBreadcrumbView.getParent() == this) {
                mBreadcrumbView.offsetTopAndBottom(offset);
            }
            mTitleView.offsetTopAndBottom(offset);
            if (mDescriptionView != null && mDescriptionView.getParent() == this) {
                mDescriptionView.offsetTopAndBottom(offset);
            }
        }
        if (mIconView != null && mIconView.getParent() == this) {
            Drawable drawable = mIconView.getDrawable();
            if (drawable != null) {
                mIconView.offsetTopAndBottom(mTitleKeylinePixels - (mIconView.getMeasuredHeight() / 2));
            }
        }
    }
}
