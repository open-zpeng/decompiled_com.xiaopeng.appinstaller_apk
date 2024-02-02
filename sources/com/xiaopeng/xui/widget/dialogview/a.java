package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XFrameLayout;
@Deprecated
/* loaded from: classes.dex */
class a extends XFrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private ListView f119a;

    /* renamed from: com.xiaopeng.xui.widget.dialogview.a$a  reason: collision with other inner class name */
    /* loaded from: classes.dex */
    private static class C0005a extends ArrayAdapter<CharSequence> {
        C0005a(Context context, int i, int i2, CharSequence[] charSequenceArr) {
            super(context, i, i2, charSequenceArr);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            ((TextView) view2).setTextColor(view2.getContext().getResources().getColorStateList(R.color.x_dialog_choice_text_color, view2.getContext().getTheme()));
            return view2;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }
    }

    public a(Context context) {
        this(context, null);
    }

    public a(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public a(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public a(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        LayoutInflater.from(context).inflate(R.layout.x_dialog_list, this);
        this.f119a = (ListView) findViewById(R.id.x_dialog_listview);
    }

    public void a(CharSequence[] charSequenceArr, int i, AdapterView.OnItemClickListener onItemClickListener) {
        this.f119a.setChoiceMode(1);
        this.f119a.setOnItemClickListener(onItemClickListener);
        this.f119a.setAdapter((ListAdapter) new C0005a(getContext(), R.layout.x_dialog_item_singlechoice, R.id.x_dialog_item_text1, charSequenceArr));
        this.f119a.setItemChecked(i, true);
    }
}
