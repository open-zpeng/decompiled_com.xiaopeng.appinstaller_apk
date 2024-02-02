package com.xiaopeng.xui.vui;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import b.a.a.a.a;
import b.a.a.a.g;
import b.a.a.a.h;
import b.a.a.a.i;
import b.a.a.a.j;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.vui.utils.VuiCommonUtils;
import com.xiaopeng.xui.vui.utils.VuiUtils;
import org.json.JSONObject;
/* loaded from: classes.dex */
public interface VuiView extends a {
    public static final SparseArray<XAttr> msMap = new SparseArray<>();

    /* loaded from: classes.dex */
    public static class XAttr {
        private boolean performVuiAction;
        private String vuiAction;
        private String vuiBizId;
        private boolean vuiDisableHitEffect;
        private String vuiElementId;
        private String vuiFatherElementId;
        private String vuiFatherLabel;
        private h vuiFeedbackType;
        private String vuiLabel;
        private boolean vuiLayoutLoadable;
        private g vuiElementType = VuiCommonUtils.getElementType(-1);
        private Integer vuiPosition = -1;
        private i vuiMode = i.NORMAL;
        private boolean vuiEnableViewVuiMode = false;
        private j vuiPriority = VuiCommonUtils.getViewLeveByPriority(2);
        private JSONObject vuiProps = null;

        XAttr() {
        }
    }

    default XAttr checkVuiExit() {
        XAttr xAttr = msMap.get(hashCode());
        if (xAttr == null) {
            XAttr xAttr2 = new XAttr();
            if (xAttr2.vuiElementType == g.UNKNOWN) {
                xAttr2.vuiElementType = VuiUtils.getElementType(this);
            }
            synchronized (msMap) {
                msMap.put(hashCode(), xAttr2);
            }
            return xAttr2;
        }
        return xAttr;
    }

    default void enableViewVuiMode(boolean z) {
        checkVuiExit().vuiEnableViewVuiMode = z;
    }

    default String getVuiAction() {
        return checkVuiExit().vuiAction;
    }

    default String getVuiBizId() {
        return checkVuiExit().vuiBizId;
    }

    default boolean getVuiDisableHitEffect() {
        return checkVuiExit().vuiDisableHitEffect;
    }

    default String getVuiElementId() {
        return checkVuiExit().vuiElementId;
    }

    default g getVuiElementType() {
        return checkVuiExit().vuiElementType;
    }

    default String getVuiFatherElementId() {
        return checkVuiExit().vuiFatherElementId;
    }

    default String getVuiFatherLabel() {
        return checkVuiExit().vuiFatherLabel;
    }

    default h getVuiFeedbackType() {
        return checkVuiExit().vuiFeedbackType;
    }

    default String getVuiLabel() {
        return checkVuiExit().vuiLabel;
    }

    default i getVuiMode() {
        return checkVuiExit().vuiMode;
    }

    default int getVuiPosition() {
        return checkVuiExit().vuiPosition.intValue();
    }

    default j getVuiPriority() {
        return checkVuiExit().vuiPriority;
    }

    default JSONObject getVuiProps() {
        return checkVuiExit().vuiProps;
    }

    default void initVui(View view, AttributeSet attributeSet) {
        if (!Xui.isVuiEnable() || view == null || attributeSet == null) {
            return;
        }
        XAttr xAttr = new XAttr();
        TypedArray obtainStyledAttributes = view.getContext().obtainStyledAttributes(attributeSet, R.styleable.vui);
        xAttr.vuiAction = obtainStyledAttributes.getString(R.styleable.vui_vuiAction);
        xAttr.vuiElementType = VuiCommonUtils.getElementType(obtainStyledAttributes.getInteger(R.styleable.vui_vuiElementType, -1));
        if (xAttr.vuiElementType == g.UNKNOWN) {
            xAttr.vuiElementType = VuiUtils.getElementType(view);
        }
        xAttr.vuiPosition = Integer.valueOf(obtainStyledAttributes.getInteger(R.styleable.vui_vuiPosition, -1));
        xAttr.vuiFatherElementId = obtainStyledAttributes.getString(R.styleable.vui_vuiFatherElementId);
        xAttr.vuiLabel = obtainStyledAttributes.getString(R.styleable.vui_vuiLabel);
        xAttr.vuiFatherLabel = obtainStyledAttributes.getString(R.styleable.vui_vuiFatherLabel);
        xAttr.vuiElementId = obtainStyledAttributes.getString(R.styleable.vui_vuiElementId);
        xAttr.vuiLayoutLoadable = obtainStyledAttributes.getBoolean(R.styleable.vui_vuiLayoutLoadable, false);
        xAttr.vuiMode = VuiCommonUtils.getVuiMode(obtainStyledAttributes.getInteger(R.styleable.vui_vuiMode, 4));
        xAttr.vuiBizId = obtainStyledAttributes.getString(R.styleable.vui_vuiBizId);
        xAttr.vuiPriority = VuiCommonUtils.getViewLeveByPriority(obtainStyledAttributes.getInt(R.styleable.vui_vuiPriority, 2));
        xAttr.vuiFeedbackType = VuiCommonUtils.getFeedbackType(obtainStyledAttributes.getInteger(R.styleable.vui_vuiFeedbackType, 1));
        xAttr.vuiDisableHitEffect = obtainStyledAttributes.getBoolean(R.styleable.vui_vuiDisableHitEffect, false);
        xAttr.vuiEnableViewVuiMode = obtainStyledAttributes.getBoolean(R.styleable.vui_vuiEnableViewVuiMode, false);
        obtainStyledAttributes.recycle();
        synchronized (msMap) {
            msMap.put(hashCode(), xAttr);
        }
    }

    default boolean isPerformVuiAction() {
        return checkVuiExit().performVuiAction;
    }

    default boolean isVuiLayoutLoadable() {
        return checkVuiExit().vuiLayoutLoadable;
    }

    default boolean isVuiModeEnabled() {
        return checkVuiExit().vuiEnableViewVuiMode;
    }

    default void log(String str) {
        Log.d("xui", " hashCode " + hashCode() + str);
    }

    default void releaseVui() {
        synchronized (msMap) {
            msMap.remove(hashCode());
        }
    }

    @Override // b.a.a.a.a
    default void setPerformVuiAction(boolean z) {
        checkVuiExit().performVuiAction = z;
    }

    default void setVuiAction(String str) {
        checkVuiExit().vuiAction = str;
    }

    default void setVuiBizId(String str) {
        checkVuiExit().vuiBizId = str;
    }

    default void setVuiDisableHitEffect(boolean z) {
        checkVuiExit().vuiDisableHitEffect = z;
    }

    default void setVuiElementId(String str) {
        checkVuiExit().vuiElementId = str;
    }

    default void setVuiElementType(g gVar) {
        checkVuiExit().vuiElementType = gVar;
    }

    default void setVuiFatherElementId(String str) {
        checkVuiExit().vuiFatherElementId = str;
    }

    default void setVuiFatherLabel(String str) {
        checkVuiExit().vuiFatherLabel = str;
    }

    default void setVuiFeedbackType(h hVar) {
        checkVuiExit().vuiFeedbackType = hVar;
    }

    default void setVuiLabel(String str) {
        checkVuiExit().vuiLabel = str;
    }

    default void setVuiLayoutLoadable(boolean z) {
        checkVuiExit().vuiLayoutLoadable = z;
    }

    default void setVuiMode(i iVar) {
        checkVuiExit().vuiMode = iVar;
    }

    default void setVuiPosition(int i) {
        checkVuiExit().vuiPosition = Integer.valueOf(i);
    }

    default void setVuiPriority(j jVar) {
        checkVuiExit().vuiPriority = jVar;
    }

    default void setVuiProps(JSONObject jSONObject) {
        checkVuiExit().vuiProps = jSONObject;
    }
}
