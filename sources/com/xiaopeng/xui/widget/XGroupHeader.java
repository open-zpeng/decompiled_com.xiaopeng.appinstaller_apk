package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.font.XFontScaleHelper;
/* loaded from: classes.dex */
public class XGroupHeader extends XLinearLayout {
    private static final int LOCATION_LEFT = 0;
    private static final int LOCATION_RIGHT = 1;
    private static final int MODE_BUTTON = 2;
    private static final int MODE_CUSTOM = 4;
    private static final int MODE_ICON = 1;
    private static final int MODE_LOADING = 3;
    private static final int MODE_NONE = 0;
    private int mActionMode;
    private View mRightView;
    private TextView mText;

    public XGroupHeader(Context context) {
        this(context, null);
    }

    public XGroupHeader(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XGroupHeader(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XGroupHeader(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        LayoutInflater from;
        int i3;
        XViewDelegate xViewDelegate;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XGroupHeader);
        if (obtainStyledAttributes.getInt(R.styleable.XGroupHeader_group_header_action_location, 0) == 1) {
            from = LayoutInflater.from(context);
            i3 = R.layout.x_groupheader_right;
        } else {
            from = LayoutInflater.from(context);
            i3 = R.layout.x_groupheader;
        }
        from.inflate(i3, this);
        initView();
        setText(obtainStyledAttributes.getString(R.styleable.XGroupHeader_group_header_text));
        this.mActionMode = obtainStyledAttributes.getInt(R.styleable.XGroupHeader_group_header_action_mode, 0);
        setView(obtainStyledAttributes.getResourceId(R.styleable.XGroupHeader_group_header_action, -1));
        this.mText.setTextSize(0, obtainStyledAttributes.getDimensionPixelSize(R.styleable.XGroupHeader_group_header_text_size, 0));
        final XFontScaleHelper create = XFontScaleHelper.create(obtainStyledAttributes, R.styleable.XGroupHeader_group_header_text_size);
        if (create != null && (xViewDelegate = this.mXViewDelegate) != null) {
            xViewDelegate.setFontScaleChangeCallback(new XViewDelegate.onFontScaleChangeCallback() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XGroupHeader$C4gf1U2xUTEOlhFZfBfuy7iWI00
                @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
                public final void onFontScaleChanged() {
                    XGroupHeader.this.a(create);
                }
            });
        }
        obtainStyledAttributes.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(XFontScaleHelper xFontScaleHelper) {
        xFontScaleHelper.refreshTextSize(this.mText);
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_groupheader_tv);
    }

    private void setView(int i) {
        View inflate;
        switch (this.mActionMode) {
            case 0:
                return;
            case 1:
                this.mRightView = LayoutInflater.from(getContext()).inflate(R.layout.x_groupheader_action_icon, (ViewGroup) this, false);
                addView(this.mRightView);
                if (i > 0) {
                    setIcon(i);
                    return;
                }
                return;
            case 2:
                this.mRightView = LayoutInflater.from(getContext()).inflate(R.layout.x_groupheader_action_button, (ViewGroup) this, false);
                addView(this.mRightView);
                if (i > 0) {
                    ((TextView) this.mRightView).setText(i);
                    return;
                }
                return;
            case 3:
                inflate = LayoutInflater.from(getContext()).inflate(R.layout.x_groupheader_action_loading, (ViewGroup) this, false);
                break;
            case 4:
                if (i > 0) {
                    inflate = LayoutInflater.from(getContext()).inflate(i, (ViewGroup) this, false);
                    break;
                } else {
                    return;
                }
            default:
                return;
        }
        this.mRightView = inflate;
        addView(this.mRightView);
    }

    public View getRightView() {
        return this.mRightView;
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        if (view != null) {
            view.setVisibility(4);
        }
        super.removeView(view);
    }

    public void setButtonText(int i) {
        if (this.mActionMode == 2) {
            ((TextView) this.mRightView).setText(i);
            return;
        }
        this.mActionMode = 2;
        removeView(this.mRightView);
        setView(i);
    }

    public void setCustom(int i) {
        this.mActionMode = 4;
        removeView(this.mRightView);
        setView(i);
    }

    public void setIcon(int i) {
        if (this.mActionMode == 1) {
            ((ImageButton) this.mRightView).setImageResource(i);
            return;
        }
        this.mActionMode = 1;
        removeView(this.mRightView);
        setView(i);
    }

    public void setText(CharSequence charSequence) {
        this.mText.setText(charSequence);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001a, code lost:
        r1 = 4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0008, code lost:
        if (r4 != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0017, code lost:
        if (r4 != false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void showLoading(boolean r4) {
        /*
            r3 = this;
            int r0 = r3.mActionMode
            r1 = 0
            r2 = 3
            if (r0 != r2) goto Lb
            android.view.View r0 = r3.mRightView
            if (r4 == 0) goto L1a
            goto L1b
        Lb:
            r3.mActionMode = r2
            android.view.View r0 = r3.mRightView
            r3.removeView(r0)
            r3.setView(r1)
            android.view.View r0 = r3.mRightView
            if (r4 == 0) goto L1a
            goto L1b
        L1a:
            r1 = 4
        L1b:
            r0.setVisibility(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XGroupHeader.showLoading(boolean):void");
    }
}
