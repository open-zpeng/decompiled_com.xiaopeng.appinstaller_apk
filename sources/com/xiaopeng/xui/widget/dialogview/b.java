package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XFrameLayout;
/* loaded from: classes.dex */
class b extends XFrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f120a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f121b;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements Runnable {
        a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            TextView textView;
            int i;
            if (b.this.f120a.getLineCount() < 2) {
                textView = b.this.f120a;
                i = 1;
            } else {
                textView = b.this.f120a;
                i = 8388611;
            }
            textView.setGravity(i);
            b.this.f120a.setVisibility(0);
        }
    }

    public b(Context context) {
        this(context, null);
    }

    public b(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public b(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public b(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        LayoutInflater.from(context).inflate(R.layout.x_dialog_message, this);
        b();
    }

    private void a() {
        this.f120a.post(new a());
    }

    private void b() {
        this.f120a = (TextView) findViewById(R.id.x_dialog_message);
        this.f121b = (ImageView) findViewById(R.id.x_dialog_icon);
    }

    public void a(int i) {
        this.f121b.setImageResource(i);
        this.f121b.setVisibility(i != 0 ? 0 : 8);
    }

    public void a(Drawable drawable) {
        this.f121b.setImageResource(0);
        this.f121b.setImageDrawable(drawable);
        this.f121b.setVisibility(drawable == null ? 8 : 0);
    }

    public void a(CharSequence charSequence) {
        this.f120a.setText(charSequence);
        this.f120a.setVisibility(4);
        a();
    }

    public void b(int i) {
        this.f120a.setText(i);
        this.f120a.setVisibility(4);
        a();
    }
}
