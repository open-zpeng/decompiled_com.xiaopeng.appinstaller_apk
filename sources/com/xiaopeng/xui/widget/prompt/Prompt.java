package com.xiaopeng.xui.widget.prompt;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.xiaopeng.xpui.R;
import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class Prompt {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    private static final String TAG = "XPrompt";
    private Animation mChangeAnim;
    private Context mContext;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private boolean mIsQueueShow;
    private boolean mIsUseExitAnim;
    private int mLongTime;
    private OnPromptActionListener mOnPromptActionListener;
    private int mShortTime;
    private XPromptMessage mXPromptMessage;
    com.xiaopeng.xui.widget.prompt.a mXPromptView;
    private Handler mHandler = new Handler();
    private ArrayList<XPromptMessage> mMessages = new ArrayList<>();
    private Runnable mRemoveRunnable = new b();
    private Runnable mShowRunnable = new c();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements Animation.AnimationListener {
        a() {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            Log.d(Prompt.TAG, "onAnimationEnd");
            Prompt.this.mHandler.post(Prompt.this.mRemoveRunnable);
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
            Log.d(Prompt.TAG, "onAnimationStart");
        }
    }

    /* loaded from: classes.dex */
    class b implements Runnable {
        b() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Prompt.this.removeView();
        }
    }

    /* loaded from: classes.dex */
    class c implements Runnable {
        c() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (Prompt.this.showNext()) {
                return;
            }
            Prompt.this.cancel();
        }
    }

    public Prompt(Context context) {
        this.mContext = context;
        this.mXPromptView = new com.xiaopeng.xui.widget.prompt.a(context);
        this.mXPromptView.a(this);
        this.mShortTime = context.getResources().getInteger(R.integer.x_prompt_shortTime);
        this.mLongTime = context.getResources().getInteger(R.integer.x_prompt_longTime);
        initAnim();
    }

    private void enqueuePrompt() {
        if (this.mXPromptView.getParent() == null || !this.mIsQueueShow) {
            showNext();
        }
    }

    private void initAnim() {
        this.mIsUseExitAnim = true;
        this.mEnterAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.x_prompt_enter_anim);
        this.mExitAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.x_prompt_exit_anim);
        this.mExitAnim.setAnimationListener(new a());
        this.mChangeAnim = AnimationUtils.loadAnimation(this.mContext, R.anim.x_prompt_change_anim);
    }

    private void setXPromptMessage(XPromptMessage xPromptMessage) {
        this.mXPromptMessage = xPromptMessage;
    }

    private boolean showCurrent() {
        View a2;
        Animation animation;
        if (this.mXPromptMessage == null) {
            return false;
        }
        this.mHandler.removeCallbacks(this.mRemoveRunnable);
        if (this.mXPromptView.getParent() != null) {
            a2 = this.mXPromptView.a();
            animation = this.mChangeAnim;
        } else if (!addView()) {
            return false;
        } else {
            a2 = this.mXPromptView.a();
            animation = this.mEnterAnim;
        }
        a2.startAnimation(animation);
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mHandler.postDelayed(this.mShowRunnable, this.mXPromptMessage.getDuration() == 0 ? this.mShortTime : this.mLongTime);
        this.mXPromptView.a(this.mXPromptMessage.getText(), this.mXPromptMessage.getIcon(), this.mXPromptMessage.getButton());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showNext() {
        if (this.mMessages.size() > 0) {
            setXPromptMessage(this.mMessages.remove(0));
            return showCurrent();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addMessage(XPromptMessage xPromptMessage) {
        if (!this.mIsQueueShow) {
            this.mMessages.clear();
        }
        this.mMessages.add(xPromptMessage);
    }

    protected abstract boolean addView();

    public void cancel() {
        this.mMessages.clear();
        this.mHandler.removeCallbacks(this.mShowRunnable);
        if (this.mIsUseExitAnim) {
            this.mXPromptView.a().startAnimation(this.mExitAnim);
        } else {
            this.mHandler.post(this.mRemoveRunnable);
        }
        Log.d(TAG, "cancel mIsUseExitAnim : " + this.mIsUseExitAnim);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearQueue() {
        this.mHandler.removeCallbacks(this.mShowRunnable);
    }

    public boolean isQueueShow() {
        return this.mIsQueueShow;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAction(int i) {
        OnPromptActionListener onPromptActionListener = this.mOnPromptActionListener;
        if (onPromptActionListener != null) {
            onPromptActionListener.onPromptAction(this.mXPromptMessage.getId(), i);
        }
        this.mShowRunnable.run();
    }

    protected abstract void removeView();

    public Prompt setOnPromptActionClickListener(OnPromptActionListener onPromptActionListener) {
        this.mOnPromptActionListener = onPromptActionListener;
        return this;
    }

    public Prompt setQueueShow(boolean z) {
        this.mIsQueueShow = z;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Prompt setUseExitAnim(boolean z) {
        this.mIsUseExitAnim = z;
        return this;
    }

    public void show() {
        enqueuePrompt();
    }

    public void show(XPromptMessage xPromptMessage) {
        addMessage(xPromptMessage);
        enqueuePrompt();
    }

    public void show(CharSequence charSequence) {
        show(charSequence, 0);
    }

    public void show(CharSequence charSequence, int i) {
        show(new XPromptMessage(i, charSequence));
    }

    public void show(CharSequence charSequence, int i, String str) {
        show(charSequence, i, str, 1);
    }

    public void show(CharSequence charSequence, int i, String str, int i2) {
        show(new XPromptMessage(i2, charSequence, i, str));
    }

    public void show(CharSequence charSequence, CharSequence charSequence2, String str) {
        show(charSequence, charSequence2, str, 1);
    }

    public void show(CharSequence charSequence, CharSequence charSequence2, String str, int i) {
        show(new XPromptMessage(i, charSequence, charSequence2, str));
    }
}
