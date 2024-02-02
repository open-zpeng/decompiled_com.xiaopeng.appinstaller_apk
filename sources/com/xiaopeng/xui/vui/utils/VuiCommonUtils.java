package com.xiaopeng.xui.vui.utils;

import b.a.a.a.g;
import b.a.a.a.h;
import b.a.a.a.i;
import b.a.a.a.j;
/* loaded from: classes.dex */
public class VuiCommonUtils {
    public static g getElementType(int i) {
        switch (i) {
            case 1:
                return g.BUTTON;
            case 2:
                return g.LISTVIEW;
            case 3:
                return g.CHECKBOX;
            case 4:
                return g.RADIOBUTTON;
            case 5:
                return g.RADIOGROUP;
            case 6:
                return g.GROUP;
            case 7:
                return g.EDITTEXT;
            case 8:
                return g.PROGRESSBAR;
            case 9:
                return g.SEEKBAR;
            case 10:
                return g.TABHOST;
            case 11:
                return g.SEARCHVIEW;
            case 12:
                return g.RATINGBAR;
            case 13:
                return g.IMAGEBUTTON;
            case 14:
                return g.IMAGEVIEW;
            case 15:
                return g.SCROLLVIEW;
            case 16:
                return g.TEXTVIEW;
            case 17:
                return g.RECYCLEVIEW;
            case 18:
                return g.SWITCH;
            case 19:
                return g.CUSTOM;
            case 20:
                return g.XSLIDER;
            case 21:
                return g.XTABLAYOUT;
            case 22:
                return g.XGROUPHEADER;
            default:
                return g.UNKNOWN;
        }
    }

    public static h getFeedbackType(int i) {
        return (i == 1 || i != 2) ? h.SOUND : h.TTS;
    }

    public static j getViewLeveByPriority(int i) {
        if (i != 3) {
            switch (i) {
                case 0:
                    return j.LEVEL1;
                case 1:
                    return j.LEVEL2;
                default:
                    return j.LEVEL3;
            }
        }
        return j.LEVEL4;
    }

    public static i getVuiMode(int i) {
        return i != 1 ? i != 2 ? i != 3 ? i.NORMAL : i.UNDERSTOOD : i.SILENT : i.DISABLED;
    }
}
