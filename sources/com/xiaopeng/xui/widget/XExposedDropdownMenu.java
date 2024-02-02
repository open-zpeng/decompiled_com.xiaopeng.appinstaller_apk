package com.xiaopeng.xui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.utils.XuiUtils;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XExposedDropdownMenu extends XTextView implements AdapterView.OnItemClickListener {
    private static final String TAG = XExposedDropdownMenu.class.getSimpleName();
    private ArrayAdapter<String> mAdapter;
    private final int mHoriOffset;
    private ListView mListView;
    private OnItemSelectedListener mOnItemClickListener;
    private PopupWindow mPopupWindow;
    private final int mVerticalOffset;

    /* loaded from: classes.dex */
    public interface OnItemSelectedListener {
        void onItemSelected(int i, String str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements View.OnClickListener {
        a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            XExposedDropdownMenu.this.show();
        }
    }

    public XExposedDropdownMenu(Context context) {
        this(context, null);
    }

    public XExposedDropdownMenu(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XExposedDropdownMenu(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, 0);
        this.mVerticalOffset = XuiUtils.dip2px(16.0f) - getResources().getDimensionPixelOffset(R.dimen.x_exposed_dropdown_menu_inset_vertical);
        this.mHoriOffset = getResources().getDimensionPixelOffset(R.dimen.x_exposed_dropdown_menu_inset_horizontal);
        init(attributeSet);
    }

    private void createPopupWindow() {
        this.mPopupWindow = new PopupWindow(getContext());
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.x_exposed_dropdown_menu_list, (ViewGroup) null);
        this.mPopupWindow.setContentView(inflate);
        this.mListView = (ListView) inflate.findViewById(R.id.list_view);
        this.mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        this.mPopupWindow.setOutsideTouchable(true);
        this.mPopupWindow.setClippingEnabled(false);
        this.mListView.setAdapter((ListAdapter) this.mAdapter);
        this.mListView.setOnItemClickListener(this);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray obtainAttributes = getResources().obtainAttributes(attributeSet, R.styleable.XExposedDropdownMenu);
        CharSequence[] textArray = obtainAttributes.getTextArray(R.styleable.XExposedDropdownMenu_edmDropdownEntries);
        int integer = obtainAttributes.getInteger(R.styleable.XExposedDropdownMenu_edmDropdownSelection, 0);
        this.mAdapter = new ArrayAdapter<>(getContext(), R.layout.x_exposed_dropdown_menu_item);
        createPopupWindow();
        setEntries(textArray);
        setSelection(integer);
        setOnClickListener(new a());
        obtainAttributes.recycle();
    }

    private void setDropdownHeight(int i) {
        int i2;
        float f;
        if (i > 5) {
            f = 408.0f;
        } else if (i <= 0) {
            i2 = 0;
            this.mPopupWindow.setHeight(i2);
        } else {
            f = (i * 80) + ((i - 1) * 2);
        }
        i2 = XuiUtils.dip2px(f);
        this.mPopupWindow.setHeight(i2);
    }

    private void setListViewTheme() {
        ListView listView = this.mListView;
        if (listView != null) {
            listView.setBackground(getContext().getDrawable(R.drawable.x_exposed_dropdown_menu_list_bg));
            this.mListView.setDivider(getContext().getDrawable(R.drawable.x_exposed_dropdown_menu_divider));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show() {
        this.mPopupWindow.setWidth(getWidth() - (this.mHoriOffset * 2));
        int[] iArr = new int[2];
        getLocationOnScreen(iArr);
        int height = iArr[1] + getHeight() + this.mPopupWindow.getHeight() + this.mVerticalOffset;
        Display defaultDisplay = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int i = point.y;
        int i2 = this.mVerticalOffset;
        if (height > i) {
            i2 = ((-getHeight()) - this.mPopupWindow.getHeight()) - this.mVerticalOffset;
        }
        this.mPopupWindow.showAsDropDown(this, this.mHoriOffset, i2);
    }

    public int getSelection() {
        return this.mListView.getSelectedItemPosition();
    }

    public String getSelectionTitle() {
        return getTitleWithIndex(getSelection());
    }

    public String getTitleWithIndex(int i) {
        return (i < 0 || i >= this.mAdapter.getCount()) ? BuildConfig.FLAVOR : this.mAdapter.getItem(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XTextView, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setListViewTheme();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XTextView, android.widget.TextView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (XThemeManager.isThemeChanged(configuration)) {
            setListViewTheme();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XTextView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setListViewTheme();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        setSelection(i);
        String titleWithIndex = getTitleWithIndex(i);
        setText(titleWithIndex);
        OnItemSelectedListener onItemSelectedListener = this.mOnItemClickListener;
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(i, titleWithIndex);
        }
        if (this.mPopupWindow.isShowing()) {
            this.mPopupWindow.dismiss();
        }
    }

    public void setEntries(CharSequence[] charSequenceArr) {
        this.mAdapter.clear();
        if (charSequenceArr != null && charSequenceArr.length > 0) {
            for (CharSequence charSequence : charSequenceArr) {
                this.mAdapter.add(charSequence.toString());
            }
            setSelection(0);
        }
        setDropdownHeight(this.mAdapter.getCount());
    }

    public void setEntries(String[] strArr) {
        this.mAdapter.clear();
        if (strArr != null && strArr.length > 0) {
            this.mAdapter.addAll(strArr);
            setSelection(0);
        }
        setDropdownHeight(this.mAdapter.getCount());
    }

    public void setOnItemClickListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemClickListener = onItemSelectedListener;
    }

    public void setSelection(int i) {
        if (i < 0 || i >= this.mAdapter.getCount()) {
            return;
        }
        this.mListView.setSelection(i);
        setText(getTitleWithIndex(i));
    }
}
