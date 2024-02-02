package android.support.v17.internal.widget;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
/* loaded from: classes.dex */
public class OutlineOnlyWithChildrenFrameLayout extends FrameLayout {
    private ViewOutlineProvider mInnerOutlineProvider;
    private ViewOutlineProvider mMagicalOutlineProvider;

    public OutlineOnlyWithChildrenFrameLayout(Context context) {
        super(context);
    }

    public OutlineOnlyWithChildrenFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlineOnlyWithChildrenFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OutlineOnlyWithChildrenFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        invalidateOutline();
    }

    @Override // android.view.View
    public void setOutlineProvider(ViewOutlineProvider provider) {
        this.mInnerOutlineProvider = provider;
        if (this.mMagicalOutlineProvider == null) {
            this.mMagicalOutlineProvider = new ViewOutlineProvider() { // from class: android.support.v17.internal.widget.OutlineOnlyWithChildrenFrameLayout.1
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    if (OutlineOnlyWithChildrenFrameLayout.this.getChildCount() > 0) {
                        OutlineOnlyWithChildrenFrameLayout.this.mInnerOutlineProvider.getOutline(view, outline);
                    } else {
                        ViewOutlineProvider.BACKGROUND.getOutline(view, outline);
                    }
                }
            };
        }
        super.setOutlineProvider(this.mMagicalOutlineProvider);
    }
}
