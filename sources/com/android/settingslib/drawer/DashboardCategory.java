package com.android.settingslib.drawer;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class DashboardCategory implements Parcelable {
    public String key;
    private List<Tile> mTiles = new ArrayList();
    public int priority;
    public CharSequence title;
    private static final boolean DEBUG = Log.isLoggable("DashboardCategory", 3);
    public static final Parcelable.Creator<DashboardCategory> CREATOR = new Parcelable.Creator<DashboardCategory>() { // from class: com.android.settingslib.drawer.DashboardCategory.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DashboardCategory createFromParcel(Parcel source) {
            return new DashboardCategory(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DashboardCategory[] newArray(int size) {
            return new DashboardCategory[size];
        }
    };
    public static final Comparator<Tile> TILE_COMPARATOR = new Comparator<Tile>() { // from class: com.android.settingslib.drawer.DashboardCategory.2
        @Override // java.util.Comparator
        public int compare(Tile lhs, Tile rhs) {
            return rhs.priority - lhs.priority;
        }
    };

    public DashboardCategory() {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(this.title, dest, flags);
        dest.writeString(this.key);
        dest.writeInt(this.priority);
        int count = this.mTiles.size();
        dest.writeInt(count);
        for (int n = 0; n < count; n++) {
            Tile tile = this.mTiles.get(n);
            tile.writeToParcel(dest, flags);
        }
    }

    public void readFromParcel(Parcel in) {
        this.title = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.key = in.readString();
        this.priority = in.readInt();
        int count = in.readInt();
        for (int n = 0; n < count; n++) {
            Tile tile = Tile.CREATOR.createFromParcel(in);
            this.mTiles.add(tile);
        }
    }

    DashboardCategory(Parcel in) {
        readFromParcel(in);
    }
}
