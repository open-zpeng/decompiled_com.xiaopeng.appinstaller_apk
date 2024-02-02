package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaopeng.xpui.R;
/* loaded from: classes.dex */
public class XListMultiple extends XRelativeLayout {
    private ViewStub mBottomViewStub;
    private ViewStub mRightViewStub;
    private TextView mText;
    private TextView mTextSub;

    public XListMultiple(Context context) {
        this(context, null);
    }

    public XListMultiple(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XListMultiple(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XListMultiple(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        LayoutInflater.from(context).inflate(R.layout.x_list_multiple, this);
        initView();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XListMultiple);
        setText(obtainStyledAttributes.getString(R.styleable.XListMultiple_list_multiple_text));
        setTextSub(obtainStyledAttributes.getString(R.styleable.XListMultiple_list_multiple_text_sub));
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XListMultiple_list_multiple_bottom, -1);
        if (resourceId != -1) {
            this.mBottomViewStub.setLayoutResource(resourceId);
            this.mBottomViewStub.inflate();
        }
        int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.XListMultiple_list_multiple_right, -1);
        if (resourceId2 != -1) {
            this.mRightViewStub.setLayoutResource(resourceId2);
            this.mRightViewStub.inflate();
        }
        ((LinearLayout) findViewById(R.id.x_list_multiple_layout)).setOrientation(obtainStyledAttributes.getInteger(R.styleable.XListMultiple_list_multiple_text_orientation, 0));
        this.mTextSub.setPadding(0, (int) obtainStyledAttributes.getDimension(R.styleable.XListMultiple_list_multiple_text_sub_padding_top, 0.0f), 0, 0);
        obtainStyledAttributes.recycle();
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_list_tv);
        this.mTextSub = (TextView) findViewById(R.id.x_list_tv_sub);
        this.mBottomViewStub = (ViewStub) findViewById(R.id.x_list_bottom);
        this.mRightViewStub = (ViewStub) findViewById(R.id.x_list_right);
    }

    private void setChildEnabled(ViewGroup viewGroup, boolean z) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                setChildEnabled((ViewGroup) childAt, z);
            }
            childAt.setEnabled(z);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        setEnabled(z, true);
    }

    public void setEnabled(boolean z, boolean z2) {
        super.setEnabled(z);
        if (z2) {
            setChildEnabled(this, z);
        }
    }

    public void setText(CharSequence charSequence) {
        this.mText.setText(charSequence);
    }

    public void setTextSub(CharSequence charSequence) {
        this.mTextSub.setText(charSequence);
    }
}
