package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.sound.XSoundEffectManager;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.utils.XInputUtils;
/* loaded from: classes.dex */
public abstract class XTextInput extends XRelativeLayout implements TextWatcher, View.OnFocusChangeListener {
    protected int mColorError;
    protected int mColorErrorId;
    protected int mColorFocus;
    protected int mColorFocusId;
    protected int mColorNormal;
    protected int mColorNormalId;
    protected EditText mEditText;
    protected boolean mErrorEnable;
    protected TextView mErrorTextView;
    protected boolean mResetEnable;
    protected ImageButton mResetView;
    protected View mStatusView;

    /* loaded from: classes.dex */
    class a implements View.OnClickListener {
        a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            XTextInput.this.mResetView.setVisibility(8);
            XTextInput.this.mEditText.setText((CharSequence) null);
            XTextInput.this.mEditText.requestFocus();
            XSoundEffectManager.get().play(5);
        }
    }

    public XTextInput(Context context) {
        this(context, null);
    }

    public XTextInput(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XTextInput(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XTextInput(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XTextInput);
        CharSequence text = obtainStyledAttributes.getText(R.styleable.XTextInput_input_edit_hint);
        CharSequence text2 = obtainStyledAttributes.getText(R.styleable.XTextInput_input_edit_text);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.XTextInput_input_reset_enabled, false);
        boolean z2 = obtainStyledAttributes.getBoolean(R.styleable.XTextInput_input_error_enabled, false);
        int resourceId = obtainStyledAttributes.hasValue(R.styleable.XTextInput_input_edit_appearance) ? obtainStyledAttributes.getResourceId(R.styleable.XTextInput_input_edit_appearance, -1) : -1;
        int i3 = obtainStyledAttributes.hasValue(R.styleable.XTextInput_input_edit_max_length) ? obtainStyledAttributes.getInt(R.styleable.XTextInput_input_edit_max_length, -1) : -1;
        int i4 = obtainStyledAttributes.hasValue(R.styleable.XTextInput_android_inputType) ? obtainStyledAttributes.getInt(R.styleable.XTextInput_android_inputType, -1) : -1;
        this.mColorNormalId = obtainStyledAttributes.getResourceId(R.styleable.XTextInput_input_normal_color, -1);
        this.mColorFocusId = obtainStyledAttributes.getResourceId(R.styleable.XTextInput_input_focus_color, -1);
        this.mColorErrorId = obtainStyledAttributes.getResourceId(R.styleable.XTextInput_input_error_color, -1);
        obtainStyledAttributes.recycle();
        initView();
        setResetEnable(z);
        setErrorEnable(z2);
        if (resourceId > 0) {
            this.mEditText.setTextAppearance(resourceId);
        }
        if (i3 > 0) {
            this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(i3)});
        }
        if (i4 > 0) {
            this.mEditText.setInputType(i4);
        }
        this.mEditText.setOnFocusChangeListener(this);
        this.mEditText.addTextChangedListener(this);
        this.mResetView.setSoundEffectsEnabled(false);
        XInputUtils.ignoreHiddenInput(this.mResetView);
        this.mResetView.setOnClickListener(new a());
        setEditHint(text);
        setEditContent(text2);
        initColor();
        updateEditTextBackground();
    }

    private void hideError() {
        this.mErrorTextView.setText((CharSequence) null);
    }

    private void initColor() {
        this.mColorNormal = getResources().getColor(this.mColorNormalId, getContext().getTheme());
        this.mColorFocus = getResources().getColor(this.mColorFocusId, getContext().getTheme());
        this.mColorError = getResources().getColor(this.mColorErrorId, getContext().getTheme());
    }

    private boolean isErrorShow() {
        return isErrorEnable() && !TextUtils.isEmpty(this.mErrorTextView.getText().toString());
    }

    private void showError(CharSequence charSequence) {
        this.mErrorTextView.setText(charSequence);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if (textWatcher != null) {
            this.mEditText.addTextChangedListener(textWatcher);
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public EditText getEditText() {
        return this.mEditText;
    }

    public String getText() {
        return this.mEditText.getText().toString();
    }

    protected abstract void initView();

    public boolean isErrorEnable() {
        return this.mErrorEnable;
    }

    public boolean isResetEnable() {
        return this.mResetEnable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (XThemeManager.isThemeChanged(configuration)) {
            initColor();
            updateEditTextBackground();
        }
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View view, boolean z) {
        if (view == this.mEditText) {
            updateEditTextBackground();
        }
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        ImageButton imageButton;
        int i4;
        if (this.mResetEnable) {
            if (charSequence.length() > 0) {
                imageButton = this.mResetView;
                i4 = 0;
            } else {
                imageButton = this.mResetView;
                i4 = 8;
            }
            imageButton.setVisibility(i4);
        }
    }

    public void setEditContent(int i) {
        this.mEditText.setText(i);
    }

    public void setEditContent(CharSequence charSequence) {
        this.mEditText.setText(charSequence);
    }

    public void setEditHint(int i) {
        this.mEditText.setHint(i);
    }

    public void setEditHint(CharSequence charSequence) {
        this.mEditText.setHint(charSequence);
    }

    public void setErrorEnable(boolean z) {
        if (this.mErrorEnable != z) {
            this.mErrorEnable = z;
            this.mErrorTextView.setVisibility(z ? 0 : 8);
            updateEditTextBackground();
        }
    }

    public void setErrorMsg(CharSequence charSequence) {
        if (!isErrorEnable()) {
            if (TextUtils.isEmpty(charSequence)) {
                return;
            }
            setErrorEnable(true);
        }
        if (TextUtils.isEmpty(charSequence)) {
            hideError();
        } else {
            showError(charSequence);
        }
        updateEditTextBackground();
    }

    public void setInputType(int i) {
        this.mEditText.setInputType(i);
    }

    public void setMaxLength(int i) {
        this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});
    }

    public void setResetEnable(boolean z) {
        if (this.mResetEnable != z) {
            this.mResetEnable = z;
            this.mResetView.setVisibility(z ? 0 : 8);
        }
    }

    void updateEditTextBackground() {
        Drawable mutate;
        int i;
        Drawable background = this.mStatusView.getBackground();
        if (background == null) {
            return;
        }
        if (isErrorShow()) {
            mutate = background.mutate();
            i = this.mColorError;
        } else if (this.mEditText.isFocused()) {
            mutate = background.mutate();
            i = this.mColorFocus;
        } else {
            mutate = background.mutate();
            i = this.mColorNormal;
        }
        mutate.setTint(i);
    }
}
