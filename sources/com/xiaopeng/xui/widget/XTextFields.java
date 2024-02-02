package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import b.a.a.a.b;
import b.a.a.a.c;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XInputUtils;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import com.xiaopeng.xui.vui.utils.VuiUtils;
/* loaded from: classes.dex */
public class XTextFields extends XTextInput implements View.OnClickListener, c {
    private CheckStateChangeListener checkStateChangeListener;
    private Context mContext;
    private ImageButton mPassWordView;
    private boolean mPasswordCheck;
    private boolean mPasswordEnable;

    /* loaded from: classes.dex */
    public interface CheckStateChangeListener {
        void onCheckStateChanged();
    }

    /* loaded from: classes.dex */
    class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ View f112a;

        a(View view) {
            this.f112a = view;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiFloatingLayerManager.show(this.f112a);
            XTextFields.this.passwordCheckToggleRequested();
        }
    }

    public XTextFields(Context context) {
        this(context, null);
    }

    public XTextFields(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XTextFields(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XTextFields(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XTextFields);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.XTextFields_text_fields_password_enabled, false);
        obtainStyledAttributes.recycle();
        setPasswordEnable(z);
        this.mContext = context;
    }

    private boolean hasPasswordTransformation() {
        EditText editText = this.mEditText;
        return editText != null && (editText.getTransformationMethod() instanceof PasswordTransformationMethod);
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
        LayoutInflater.from(getContext()).inflate(R.layout.x_text_fields, this);
        this.mResetView = (ImageButton) findViewById(R.id.x_text_fields_reset);
        this.mPassWordView = (ImageButton) findViewById(R.id.x_text_fields_pass);
        this.mEditText = (EditText) findViewById(R.id.x_text_fields_edit);
        this.mErrorTextView = (TextView) findViewById(R.id.x_text_fields_error);
        this.mStatusView = findViewById(R.id.x_text_fields_line);
        this.mPassWordView.setOnClickListener(this);
        XInputUtils.ignoreHiddenInput(this.mPassWordView);
    }

    public boolean isPasswordEnable() {
        return this.mPasswordEnable;
    }

    public b.a.a.a.k.a onBuildVuiElement(String str, b bVar) {
        if (this.mPassWordView instanceof VuiView) {
            VuiUtils.setStatefulButtonAttr((VuiView) this.mPassWordView, !this.mPasswordCheck ? 1 : 0, new String[]{this.mContext.getString(R.string.vui_label_hide_password), this.mContext.getString(R.string.vui_label_display_password)});
            return null;
        }
        return null;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.x_text_fields_pass) {
            passwordCheckToggleRequested();
        }
    }

    @Override // b.a.a.a.c
    public boolean onVuiElementEvent(final View view, b.a.a.a.k.b bVar) {
        log("textfields onVuiElementEvent");
        if (view == null) {
            return false;
        }
        if (view.getId() != this.mPassWordView.getId()) {
            if (view.getVisibility() == 0 && view.isEnabled()) {
                post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XTextFields$8Faq1My5FKY1LrYxH3lJX7IiTN8
                    @Override // java.lang.Runnable
                    public final void run() {
                        VuiFloatingLayerManager.show(view);
                    }
                });
            }
            return false;
        }
        String str = (String) bVar.a(bVar);
        if (!(this.mPasswordCheck && str.equals(this.mContext.getString(R.string.vui_label_hide_password))) && (this.mPasswordCheck || !str.equals(this.mContext.getString(R.string.vui_label_display_password)))) {
            post(new a(view));
            return true;
        }
        return true;
    }

    public void passwordCheckToggleRequested() {
        boolean z;
        CheckStateChangeListener checkStateChangeListener;
        if (this.mPasswordEnable) {
            int selectionEnd = this.mEditText.getSelectionEnd();
            if (hasPasswordTransformation()) {
                this.mEditText.setTransformationMethod(null);
                z = false;
            } else {
                this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                z = true;
            }
            this.mPasswordCheck = z;
            this.mPassWordView.setSelected(this.mPasswordCheck);
            this.mEditText.setSelection(selectionEnd);
            if (!Xui.isVuiEnable() || (checkStateChangeListener = this.checkStateChangeListener) == null) {
                return;
            }
            checkStateChangeListener.onCheckStateChanged();
        }
    }

    public void setCheckStateChangeListener(CheckStateChangeListener checkStateChangeListener) {
        this.checkStateChangeListener = checkStateChangeListener;
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

    public void setPasswordEnable(boolean z) {
        if (this.mPasswordEnable != z) {
            this.mPasswordEnable = z;
            int selectionEnd = this.mEditText.getSelectionEnd();
            this.mPassWordView.setVisibility(z ? 0 : 8);
            if (z) {
                this.mPasswordCheck = true;
                this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.mEditText.setTransformationMethod(null);
                this.mPasswordCheck = false;
            }
            this.mPassWordView.setSelected(this.mPasswordCheck);
            this.mEditText.setSelection(selectionEnd);
        }
    }
}
