package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import b.a.a.a.i;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XCountDownHandler;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XImageButton;
import com.xiaopeng.xui.widget.XLinearLayout;
import com.xiaopeng.xui.widget.XScrollView;
import com.xiaopeng.xui.widget.XTextView;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
import org.json.JSONObject;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class c extends XDialogViewDelegate {

    /* renamed from: a  reason: collision with root package name */
    private C0006c f123a;

    /* renamed from: b  reason: collision with root package name */
    private ViewGroup f124b;
    private TextView c;
    private ViewGroup d;
    private TextView e;
    private TextView f;
    private View g;
    private View h;
    private com.xiaopeng.xui.widget.dialogview.b i;
    private com.xiaopeng.xui.widget.dialogview.a j;
    private XScrollView k;
    private XDialogViewInterface.OnCloseListener l;
    private XDialogViewInterface.OnCountDownListener m;
    private XDialogViewInterface.OnDismissListener n;
    private XDialogViewInterface.OnClickListener o;
    private XDialogViewInterface.OnClickListener p;
    private XDialogViewInterface.OnClickListener q;
    private boolean r;
    private boolean s;
    private XCountDownHandler t;
    private View.OnClickListener u;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements View.OnAttachStateChangeListener {
        a() {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
            c.this.b();
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            if (c.this.t != null) {
                c.this.t.stop();
            }
        }
    }

    /* loaded from: classes.dex */
    class b implements View.OnClickListener {
        b() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            c cVar;
            String str;
            int id = view.getId();
            boolean z = c.this.r;
            boolean z2 = c.this.s;
            if (id == R.id.x_dialog_button1) {
                if (c.this.o != null) {
                    c cVar2 = c.this;
                    cVar2.a("onClick  positiveIntercept " + z + " , mPositiveListener " + c.this.o);
                    c.this.o.onClick(c.this.mXDelegator, -1);
                    if (z) {
                        return;
                    }
                } else {
                    cVar = c.this;
                    str = "onClick mPositiveListener is null";
                    cVar.a(str);
                }
            } else if (id == R.id.x_dialog_button2) {
                if (c.this.p != null) {
                    c cVar3 = c.this;
                    cVar3.a("onClick negativeIntercept " + z2 + " , mNegativeListener " + c.this.p);
                    c.this.p.onClick(c.this.mXDelegator, -2);
                    if (z2) {
                        return;
                    }
                } else {
                    cVar = c.this;
                    str = "onClick mNegativeListener is null";
                    cVar.a(str);
                }
            } else if (id == R.id.x_dialog_close) {
                c.this.a("onClick close");
                if (c.this.l != null && c.this.l.onClose(c.this.mXDelegator)) {
                    c.this.a("onClick close intercept dismiss ");
                    return;
                }
            } else {
                c cVar4 = c.this;
                cVar4.a("onClick invalid id " + id);
            }
            c.this.a();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.xiaopeng.xui.widget.dialogview.c$c  reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public class C0006c extends XLinearLayout {

        /* renamed from: a  reason: collision with root package name */
        private int f127a;

        public C0006c(c cVar, Context context) {
            this(context, null);
        }

        public C0006c(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public void a(int i) {
            this.f127a = i;
        }

        public void a(ThemeViewModel.OnCallback onCallback) {
            XViewDelegate xViewDelegate = this.mXViewDelegate;
            if (xViewDelegate == null || xViewDelegate.getThemeViewModel() == null) {
                return;
            }
            this.mXViewDelegate.getThemeViewModel().setCallback(onCallback);
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (z) {
                c cVar = c.this;
                cVar.a("onLayout-w:" + getWidth() + ",h:" + getHeight() + ", mMaxHeight " + this.f127a);
            }
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            if (this.f127a > 0) {
                i2 = View.MeasureSpec.makeMeasureSpec(this.f127a, View.MeasureSpec.getMode(i2));
            }
            super.onMeasure(i, i2);
        }

        @Override // android.view.View
        public void onWindowFocusChanged(boolean z) {
            super.onWindowFocusChanged(z);
            c cVar = c.this;
            cVar.a("hasWindowFocus " + z);
            XViewDelegate xViewDelegate = this.mXViewDelegate;
            if (xViewDelegate != null) {
                xViewDelegate.onWindowFocusChanged(z);
            }
        }
    }

    /* loaded from: classes.dex */
    private class d implements XCountDownHandler.CallBack {

        /* renamed from: a  reason: collision with root package name */
        private TextView f129a;

        /* renamed from: b  reason: collision with root package name */
        private int f130b;

        d(TextView textView, int i) {
            this.f129a = textView;
            this.f130b = i;
        }

        @Override // com.xiaopeng.xui.utils.XCountDownHandler.CallBack
        public void onCountDown(String str, int i, int i2) {
            this.f129a.setText(String.format("%s(%ss)", str, Integer.valueOf(i2)));
        }

        @Override // com.xiaopeng.xui.utils.XCountDownHandler.CallBack
        public void onCountDownOver(String str) {
            this.f129a.setText(str);
            if (c.this.m == null || !c.this.m.onCountDown(c.this.mXDelegator, this.f130b)) {
                c.this.u.onClick(this.f129a);
            } else {
                c.this.a("onCountDown intercept dismiss ");
            }
        }

        @Override // com.xiaopeng.xui.utils.XCountDownHandler.CallBack
        public void onCountDownStop(String str) {
            this.f129a.setText(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public c(XDialogView xDialogView, Context context, int i) {
        super(xDialogView, context, i);
        this.u = new b();
        d();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a() {
        a("dismiss");
        XDialogViewInterface.OnDismissListener onDismissListener = this.n;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this.mXDelegator);
        }
    }

    private void a(View view) {
        this.f124b = (ViewGroup) view.findViewById(R.id.x_dialog_title_layout);
        this.g = view.findViewById(R.id.x_dialog_close);
        this.c = (TextView) view.findViewById(R.id.x_dialog_title);
        this.d = (ViewGroup) view.findViewById(R.id.x_dialog_content);
        this.e = (TextView) view.findViewById(R.id.x_dialog_button1);
        this.f = (TextView) view.findViewById(R.id.x_dialog_button2);
        this.h = view.findViewById(R.id.x_dialog_has_buttons);
        this.g.setOnClickListener(this.u);
        this.e.setOnClickListener(this.u);
        this.f.setOnClickListener(this.u);
        this.f123a.addOnAttachStateChangeListener(new a());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(AdapterView adapterView, View view, int i, long j) {
        XDialogViewInterface.OnClickListener onClickListener = this.q;
        if (onClickListener != null) {
            onClickListener.onClick(this.mXDelegator, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str) {
        Log.d("XDialogView", str + "--hashcode " + this.mXDelegator.hashCode());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        int[] iArr = {0, 20, 0, 0};
        XTouchAreaUtils.extendTouchArea(this.e, this.f123a, iArr);
        XTouchAreaUtils.extendTouchArea(this.f, this.f123a, iArr);
        int dimension = (int) this.g.getContext().getResources().getDimension(R.dimen.x_dialog_close_padding);
        XTouchAreaUtils.extendTouchArea(this.g, this.f123a, new int[]{dimension, dimension, dimension, dimension});
    }

    private Context c() {
        return this.mContext;
    }

    private void d() {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes((AttributeSet) null, R.styleable.XDialog);
        obtainStyledAttributes.recycle();
        this.f123a = new C0006c(this, this.mContext);
        this.f123a.a((int) obtainStyledAttributes.getDimension(R.styleable.XDialog_dialog_max_height, 0.0f));
        LayoutInflater.from(this.mContext).inflate(R.layout.x_dialog, this.f123a);
        a(this.f123a);
    }

    private void e() {
        if (this.i == null) {
            this.i = new com.xiaopeng.xui.widget.dialogview.b(this.mContext);
        }
        this.d.removeAllViews();
        this.d.addView(this.i);
    }

    private void f() {
        View view;
        int i;
        if (this.e.getVisibility() == 0 || this.f.getVisibility() == 0) {
            view = this.h;
            i = 0;
        } else {
            view = this.h;
            i = 8;
        }
        view.setVisibility(i);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public ViewGroup getContentView() {
        return this.f123a;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isNegativeButtonEnable() {
        TextView textView = this.f;
        return textView != null && textView.isEnabled();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isNegativeButtonShowing() {
        TextView textView = this.f;
        return textView != null && textView.getVisibility() == 0;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isPositiveButtonEnable() {
        TextView textView = this.e;
        return textView != null && textView.isEnabled();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean isPositiveButtonShowing() {
        TextView textView = this.e;
        return textView != null && textView.getVisibility() == 0;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void onBuildScenePrepare() {
        try {
            ((XTextView) this.f123a.findViewById(R.id.x_dialog_title)).getText();
            XImageButton xImageButton = (XImageButton) this.f123a.findViewById(R.id.x_dialog_close);
            if (xImageButton != null) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("generalAct", "System.Close");
                jSONObject.put("canVoiceControl", true);
                xImageButton.setVuiProps(jSONObject);
            }
            XButton xButton = (XButton) this.f123a.findViewById(R.id.x_dialog_button1);
            if (xButton.getVisibility() != 0) {
                xButton.setVuiMode(i.DISABLED);
            } else if (this.f123a.getContext().getString(R.string.vui_label_confirm).equals(xButton.getText().toString())) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("generalAct", "System.Confirm");
                xButton.setVuiProps(jSONObject2);
            }
            XButton xButton2 = (XButton) this.f123a.findViewById(R.id.x_dialog_button2);
            if (xButton2.getVisibility() != 0) {
                xButton2.setVuiMode(i.DISABLED);
            } else if (this.f123a.getContext().getString(R.string.vui_label_cancel).equals(xButton2.getText().toString())) {
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put("generalAct", "System.Cancel");
                xButton2.setVuiProps(jSONObject3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public boolean onKey(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCloseVisibility(boolean z) {
        this.g.setVisibility(z ? 0 : 4);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(int i) {
        setCustomView(i, true);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(int i, boolean z) {
        setCustomView(LayoutInflater.from(c()).inflate(i, this.d, false), z);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(View view) {
        setCustomView(view, true);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setCustomView(View view, boolean z) {
        this.d.removeAllViews();
        if (!z) {
            this.d.addView(view);
            return;
        }
        if (this.k == null) {
            this.k = (XScrollView) LayoutInflater.from(this.mContext).inflate(R.layout.x_dialog_scroll, this.d, false);
        }
        this.k.removeAllViews();
        this.d.addView(this.k);
        this.k.addView(view);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setIcon(int i) {
        e();
        this.i.a(i);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setIcon(Drawable drawable) {
        e();
        this.i.a(drawable);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setMessage(int i) {
        e();
        this.i.b(i);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setMessage(CharSequence charSequence) {
        e();
        this.i.a(charSequence);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButton(CharSequence charSequence) {
        this.f.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.f.setText(charSequence);
        f();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButton(CharSequence charSequence, XDialogViewInterface.OnClickListener onClickListener) {
        this.f.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.f.setText(charSequence);
        this.p = onClickListener;
        f();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButtonEnable(boolean z) {
        this.f.setEnabled(z);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setNegativeButtonInterceptDismiss(boolean z) {
        this.s = z;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setOnCloseListener(XDialogViewInterface.OnCloseListener onCloseListener) {
        this.l = onCloseListener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setOnCountDownListener(XDialogViewInterface.OnCountDownListener onCountDownListener) {
        this.m = onCountDownListener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setOnDismissListener(XDialogViewInterface.OnDismissListener onDismissListener) {
        this.n = onDismissListener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButton(CharSequence charSequence) {
        this.e.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.e.setText(charSequence);
        f();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButton(CharSequence charSequence, XDialogViewInterface.OnClickListener onClickListener) {
        this.e.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.e.setText(charSequence);
        this.o = onClickListener;
        f();
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButtonEnable(boolean z) {
        this.e.setEnabled(z);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setPositiveButtonInterceptDismiss(boolean z) {
        this.r = z;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    @Deprecated
    public void setSingleChoiceItems(CharSequence[] charSequenceArr, int i, XDialogViewInterface.OnClickListener onClickListener) {
        if (this.j == null) {
            this.j = new com.xiaopeng.xui.widget.dialogview.a(c());
        }
        this.d.removeAllViews();
        this.d.addView(this.j);
        this.j.a(charSequenceArr, i, new AdapterView.OnItemClickListener() { // from class: com.xiaopeng.xui.widget.dialogview.-$$Lambda$c$xt2tYbKOwDQpNnmgHjTUI2vpsIA
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i2, long j) {
                c.this.a(adapterView, view, i2, j);
            }
        });
        this.q = onClickListener;
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
        this.f123a.a(onCallback);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setTitle(int i) {
        this.c.setText(i);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setTitle(CharSequence charSequence) {
        this.c.setText(charSequence);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void setTitleVisibility(boolean z) {
        this.f124b.setVisibility(z ? 0 : 8);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void startNegativeButtonCountDown(int i) {
        a("startNegativeButtonCountDown");
        XCountDownHandler xCountDownHandler = this.t;
        if (xCountDownHandler != null) {
            xCountDownHandler.stop();
        }
        if (this.f.getVisibility() != 0 || i <= 0) {
            return;
        }
        this.t = new XCountDownHandler(new d(this.f, -2));
        this.t.start(this.f.getText().toString(), i);
    }

    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewDelegate
    public void startPositiveButtonCountDown(int i) {
        a("startPositiveButtonCountDown");
        XCountDownHandler xCountDownHandler = this.t;
        if (xCountDownHandler != null) {
            xCountDownHandler.stop();
        }
        if (this.e.getVisibility() != 0 || i <= 0) {
            return;
        }
        this.t = new XCountDownHandler(new d(this.e, -1));
        this.t.start(this.e.getText().toString(), i);
    }
}
