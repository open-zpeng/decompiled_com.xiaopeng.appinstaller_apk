package com.xiaopeng.xui.widget.prompt;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import com.xiaopeng.xui.widget.XFrameLayout;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class a extends XFrameLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Prompt f137a;

    /* renamed from: b  reason: collision with root package name */
    private ViewGroup f138b;
    private TextView c;
    private ImageButton d;
    private Button e;
    private int[] f;

    public a(Context context) {
        super(context);
        Log.d("XPromptView", "onCreate");
        LayoutInflater.from(context).inflate(R.layout.x_prompt, this);
        this.f138b = (ViewGroup) findViewById(R.id.x_prompt_layout);
        this.c = (TextView) findViewById(R.id.x_prompt_text);
        this.d = (ImageButton) findViewById(R.id.x_prompt_btn);
        this.e = (Button) findViewById(R.id.x_prompt_btn_2);
        findViewById(R.id.x_prompt_btn).setOnClickListener(this);
        findViewById(R.id.x_prompt_btn_2).setOnClickListener(this);
        setId(R.id.x_prompt);
        int dimension = (int) getResources().getDimension(R.dimen.x_prompt_padding_left);
        this.f = new int[]{dimension, dimension, dimension, dimension};
    }

    public View a() {
        return this.f138b;
    }

    public void a(Prompt prompt) {
        this.f137a = prompt;
    }

    public void a(CharSequence charSequence, int i, CharSequence charSequence2) {
        this.c.setText(charSequence);
        this.d.setImageResource(i);
        this.d.setVisibility(i > 0 ? 0 : 8);
        this.e.setText(charSequence2);
        this.e.setVisibility(TextUtils.isEmpty(charSequence2) ? 8 : 0);
    }

    public Prompt b() {
        return this.f137a;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XFrameLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        Log.d("XPromptView", "onAttachedToWindow");
        XTouchAreaUtils.extendTouchArea(this.d, this.f138b, this.f);
        XTouchAreaUtils.extendTouchArea(this.e, this.f138b, this.f);
        super.onAttachedToWindow();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Prompt prompt;
        int i;
        if (view.getId() == R.id.x_prompt_btn) {
            prompt = this.f137a;
            if (prompt == null) {
                return;
            }
            i = 0;
        } else if (view.getId() != R.id.x_prompt_btn_2 || (prompt = this.f137a) == null) {
            return;
        } else {
            i = 1;
        }
        prompt.onAction(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XFrameLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        Log.d("XPromptView", "onDetachedFromWindow");
        Prompt prompt = this.f137a;
        if (prompt != null) {
            prompt.clearQueue();
        }
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        Prompt prompt = this.f137a;
        if (prompt != null) {
            prompt.setUseExitAnim(i == 0);
        }
        Log.d("XPromptView", "onWindowVisibilityChanged--visibility:" + i);
    }
}
