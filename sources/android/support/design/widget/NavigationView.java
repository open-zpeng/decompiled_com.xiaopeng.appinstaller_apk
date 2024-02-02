package android.support.design.widget;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.NavigationMenu;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.view.AbsSavedState;
import android.view.View;
/* loaded from: classes.dex */
public class NavigationView extends ScrimInsetsFrameLayout {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private int maxWidth;
    private final NavigationMenu menu;

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.menuState = new Bundle();
        this.menu.savePresenterStates(state.menuState);
        return state;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable savedState) {
        if (!(savedState instanceof SavedState)) {
            super.onRestoreInstanceState(savedState);
            return;
        }
        SavedState state = (SavedState) savedState;
        super.onRestoreInstanceState(state.getSuperState());
        this.menu.restorePresenterStates(state.menuState);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthSpec, int heightSpec) {
        int mode = View.MeasureSpec.getMode(widthSpec);
        if (mode == Integer.MIN_VALUE) {
            widthSpec = View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(widthSpec), this.maxWidth), 1073741824);
        } else if (mode == 0) {
            widthSpec = View.MeasureSpec.makeMeasureSpec(this.maxWidth, 1073741824);
        } else if (mode != 1073741824) {
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    /* loaded from: classes.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.design.widget.NavigationView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public Bundle menuState;

        public SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.menuState = in.readBundle(loader);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(this.menuState);
        }
    }
}
