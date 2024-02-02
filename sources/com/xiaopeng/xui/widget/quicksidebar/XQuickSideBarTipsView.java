package com.xiaopeng.xui.widget.quicksidebar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XRelativeLayout;
import com.xiaopeng.xui.widget.quicksidebar.tipsview.XQuickSideBarTipsItemView;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public class XQuickSideBarTipsView extends XRelativeLayout {
    private static final int SIDEBAR_TIPS_DELAY_CODE = 1;
    private boolean isAnimationHideTips;
    private b mDelayHandler;
    private long mDelayedTime;
    private XQuickSideBarTipsItemView mTipsView;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a extends AnimatorListenerAdapter {
        a() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            XQuickSideBarTipsView.this.setVisibility(4);
            XQuickSideBarTipsView.this.setAlpha(1.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class b extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<XQuickSideBarTipsView> f140a;

        public b(XQuickSideBarTipsView xQuickSideBarTipsView) {
            this.f140a = new WeakReference<>(xQuickSideBarTipsView);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            XQuickSideBarTipsView xQuickSideBarTipsView = this.f140a.get();
            if (xQuickSideBarTipsView != null && message.what == 1) {
                xQuickSideBarTipsView.hideView(xQuickSideBarTipsView.isAnimationHideTips);
                xQuickSideBarTipsView.mDelayHandler.removeMessages(1);
            }
        }
    }

    public XQuickSideBarTipsView(Context context) {
        this(context, null);
    }

    public XQuickSideBarTipsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XQuickSideBarTipsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isAnimationHideTips = true;
        init(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideView(boolean z) {
        if (z) {
            animate().alpha(0.0f).setDuration(200L).setListener(new a());
        } else {
            setVisibility(4);
        }
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.mDelayHandler = new b(this);
        if (attributeSet != null) {
            this.mTipsView = new XQuickSideBarTipsItemView(context, attributeSet);
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.XQuickSideBarView);
            this.mDelayedTime = obtainStyledAttributes.getInteger(R.styleable.XQuickSideBarView_sidebarTipsDelayTime, 500);
            obtainStyledAttributes.recycle();
            addView(this.mTipsView, new RelativeLayout.LayoutParams(-2, -2));
        }
    }

    public void display(boolean z) {
        if (!z) {
            this.mDelayHandler.sendEmptyMessageDelayed(1, this.mDelayedTime);
            return;
        }
        setVisibility(0);
        this.mDelayHandler.removeMessages(1);
    }

    public void setAnimationHideTips(boolean z) {
        this.isAnimationHideTips = z;
    }

    public void setDelayedTime(long j) {
        this.mDelayedTime = j;
    }

    public void setText(String str) {
        this.mTipsView.setText(str);
    }
}
