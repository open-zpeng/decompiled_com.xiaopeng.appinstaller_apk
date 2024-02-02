package com.xiaopeng.xui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.xiaopeng.xpui.R;
/* loaded from: classes.dex */
public class XSearchBar extends XTextInput {
    protected ImageButton mVoiceView;

    /* loaded from: classes.dex */
    class a implements View.OnClickListener {
        a(XSearchBar xSearchBar) {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
        }
    }

    public XSearchBar(Context context) {
        this(context, null);
    }

    public XSearchBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XSearchBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XSearchBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
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

    @Override // com.xiaopeng.xui.widget.XTextInput
    protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.x_searchbar, this);
        this.mResetView = (ImageButton) findViewById(R.id.x_search_reset);
        this.mEditText = (EditText) findViewById(R.id.x_search_edit);
        this.mErrorTextView = (TextView) findViewById(R.id.x_search_error);
        this.mStatusView = findViewById(R.id.x_search_status);
        this.mVoiceView = (ImageButton) findViewById(R.id.x_search_voice);
        this.mVoiceView.setOnClickListener(new a(this));
    }

    @Override // com.xiaopeng.xui.widget.XTextInput, android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        if (this.mResetEnable) {
            this.mVoiceView.setVisibility(charSequence.length() > 0 ? 8 : 0);
        } else {
            this.mVoiceView.setVisibility(0);
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
}
