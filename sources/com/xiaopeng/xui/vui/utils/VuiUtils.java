package com.xiaopeng.xui.vui.utils;

import android.view.View;
import android.view.ViewGroup;
import b.a.a.a.g;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XCheckBox;
import com.xiaopeng.xui.widget.XEditText;
import com.xiaopeng.xui.widget.XGroupHeader;
import com.xiaopeng.xui.widget.XImageButton;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XProgressBar;
import com.xiaopeng.xui.widget.XRadioButton;
import com.xiaopeng.xui.widget.XRadioGroup;
import com.xiaopeng.xui.widget.XRecyclerView;
import com.xiaopeng.xui.widget.XScrollView;
import com.xiaopeng.xui.widget.XSeekBar;
import com.xiaopeng.xui.widget.XSwitch;
import com.xiaopeng.xui.widget.XTabLayout;
import com.xiaopeng.xui.widget.XTextView;
import com.xiaopeng.xui.widget.XViewPager;
import com.xiaopeng.xui.widget.slider.XSlider;
import com.xiaopeng.xui.widget.timepicker.XTimePicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class VuiUtils {
    public static JSONObject createStatefulButtonData(int i, String[] strArr) {
        if (strArr == null || strArr.length == 0 || i < 0 || i > strArr.length - 1) {
            return null;
        }
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        try {
            Object[] objArr = new String[strArr.length];
            int i2 = 0;
            while (i2 < strArr.length) {
                JSONObject jSONObject2 = new JSONObject();
                StringBuilder sb = new StringBuilder();
                sb.append("state_");
                int i3 = i2 + 1;
                sb.append(i3);
                String sb2 = sb.toString();
                objArr[i2] = sb2;
                jSONObject2.put(sb2, strArr[i2]);
                jSONArray.put(jSONObject2);
                i2 = i3;
            }
            jSONObject.put("states", jSONArray);
            jSONObject.put("curState", objArr[i]);
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static g getElementType(Object obj) {
        return obj instanceof XImageView ? g.IMAGEVIEW : obj instanceof XImageButton ? g.IMAGEBUTTON : obj instanceof XButton ? g.BUTTON : obj instanceof XTextView ? g.TEXTVIEW : obj instanceof XRadioButton ? g.RADIOBUTTON : obj instanceof XCheckBox ? g.CHECKBOX : obj instanceof XSwitch ? g.SWITCH : obj instanceof XRecyclerView ? g.RECYCLEVIEW : obj instanceof XProgressBar ? g.PROGRESSBAR : obj instanceof XScrollView ? g.SCROLLVIEW : obj instanceof XSlider ? g.XSLIDER : obj instanceof XTabLayout ? g.XTABLAYOUT : obj instanceof XRadioGroup ? g.RADIOGROUP : obj instanceof XEditText ? g.EDITTEXT : obj instanceof XGroupHeader ? g.XGROUPHEADER : obj instanceof XSeekBar ? g.SEEKBAR : obj instanceof XTimePicker ? g.TIMEPICKER : obj instanceof XViewPager ? g.VIEWPAGER : obj instanceof ViewGroup ? g.GROUP : g.UNKNOWN;
    }

    public static boolean isPerformVuiAction(View view) {
        if (view instanceof VuiView) {
            VuiView vuiView = (VuiView) view;
            boolean isPerformVuiAction = vuiView.isPerformVuiAction();
            vuiView.setPerformVuiAction(false);
            return isPerformVuiAction;
        }
        return false;
    }

    public static void setStatefulButtonAttr(VuiView vuiView, int i, String[] strArr) {
        JSONObject createStatefulButtonData = createStatefulButtonData(i, strArr);
        if (createStatefulButtonData != null) {
            vuiView.setVuiElementType(g.STATEFULBUTTON);
            vuiView.setVuiAction("SetValue");
            vuiView.setVuiProps(createStatefulButtonData);
        }
    }
}
