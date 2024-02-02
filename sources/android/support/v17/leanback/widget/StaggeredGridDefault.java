package android.support.v17.leanback.widget;

import android.support.v17.leanback.widget.StaggeredGrid;
import android.support.v7.preference.Preference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class StaggeredGridDefault extends StaggeredGrid {
    int getRowMax(int rowIndex) {
        StaggeredGrid.Location loc;
        if (this.mFirstVisibleIndex < 0) {
            return Integer.MIN_VALUE;
        }
        if (this.mReversedFlow) {
            int edge = this.mProvider.getEdge(this.mFirstVisibleIndex);
            if (getLocation(this.mFirstVisibleIndex).row == rowIndex) {
                return edge;
            }
            int i = this.mFirstVisibleIndex;
            do {
                i++;
                if (i <= getLastIndex()) {
                    loc = getLocation(i);
                    edge += loc.offset;
                }
            } while (loc.row != rowIndex);
            return edge;
        }
        int edge2 = this.mProvider.getEdge(this.mLastVisibleIndex);
        StaggeredGrid.Location loc2 = getLocation(this.mLastVisibleIndex);
        if (loc2.row == rowIndex) {
            return loc2.size + edge2;
        }
        int i2 = this.mLastVisibleIndex;
        while (true) {
            i2--;
            if (i2 < getFirstIndex()) {
                break;
            }
            edge2 -= loc2.offset;
            loc2 = getLocation(i2);
            if (loc2.row == rowIndex) {
                return loc2.size + edge2;
            }
        }
        return Integer.MIN_VALUE;
    }

    int getRowMin(int rowIndex) {
        StaggeredGrid.Location loc;
        if (this.mFirstVisibleIndex < 0) {
            return Preference.DEFAULT_ORDER;
        }
        if (this.mReversedFlow) {
            int edge = this.mProvider.getEdge(this.mLastVisibleIndex);
            StaggeredGrid.Location loc2 = getLocation(this.mLastVisibleIndex);
            if (loc2.row == rowIndex) {
                return edge - loc2.size;
            }
            int i = this.mLastVisibleIndex;
            while (true) {
                i--;
                if (i < getFirstIndex()) {
                    break;
                }
                edge -= loc2.offset;
                loc2 = getLocation(i);
                if (loc2.row == rowIndex) {
                    return edge - loc2.size;
                }
            }
        } else {
            int edge2 = this.mProvider.getEdge(this.mFirstVisibleIndex);
            if (getLocation(this.mFirstVisibleIndex).row == rowIndex) {
                return edge2;
            }
            int i2 = this.mFirstVisibleIndex;
            do {
                i2++;
                if (i2 <= getLastIndex()) {
                    loc = getLocation(i2);
                    edge2 += loc.offset;
                }
            } while (loc.row != rowIndex);
            return edge2;
        }
        return Preference.DEFAULT_ORDER;
    }

    @Override // android.support.v17.leanback.widget.Grid
    public int findRowMax(boolean findLarge, int indexLimit, int[] indices) {
        int value;
        int edge = this.mProvider.getEdge(indexLimit);
        StaggeredGrid.Location loc = getLocation(indexLimit);
        int row = loc.row;
        int index = indexLimit;
        int visitedRows = 1;
        int visitRow = row;
        if (this.mReversedFlow) {
            value = edge;
            for (int i = indexLimit + 1; visitedRows < this.mNumRows && i <= this.mLastVisibleIndex; i++) {
                StaggeredGrid.Location loc2 = getLocation(i);
                edge += loc2.offset;
                if (loc2.row != visitRow) {
                    visitRow = loc2.row;
                    visitedRows++;
                    if (findLarge) {
                        if (edge <= value) {
                        }
                        row = visitRow;
                        value = edge;
                        index = i;
                    } else {
                        if (edge >= value) {
                        }
                        row = visitRow;
                        value = edge;
                        index = i;
                    }
                }
            }
        } else {
            value = this.mProvider.getSize(indexLimit) + edge;
            for (int i2 = indexLimit - 1; visitedRows < this.mNumRows && i2 >= this.mFirstVisibleIndex; i2--) {
                edge -= loc.offset;
                loc = getLocation(i2);
                if (loc.row != visitRow) {
                    visitRow = loc.row;
                    visitedRows++;
                    int newValue = this.mProvider.getSize(i2) + edge;
                    if (findLarge) {
                        if (newValue <= value) {
                        }
                        row = visitRow;
                        value = newValue;
                        index = i2;
                    } else {
                        if (newValue >= value) {
                        }
                        row = visitRow;
                        value = newValue;
                        index = i2;
                    }
                }
            }
        }
        if (indices != null) {
            indices[0] = row;
            indices[1] = index;
        }
        return value;
    }

    @Override // android.support.v17.leanback.widget.Grid
    public int findRowMin(boolean findLarge, int indexLimit, int[] indices) {
        int value;
        int edge = this.mProvider.getEdge(indexLimit);
        StaggeredGrid.Location loc = getLocation(indexLimit);
        int row = loc.row;
        int index = indexLimit;
        int visitedRows = 1;
        int visitRow = row;
        if (this.mReversedFlow) {
            value = edge - this.mProvider.getSize(indexLimit);
            for (int i = indexLimit - 1; visitedRows < this.mNumRows && i >= this.mFirstVisibleIndex; i--) {
                edge -= loc.offset;
                loc = getLocation(i);
                if (loc.row != visitRow) {
                    visitRow = loc.row;
                    visitedRows++;
                    int newValue = edge - this.mProvider.getSize(i);
                    if (findLarge) {
                        if (newValue <= value) {
                        }
                        value = newValue;
                        row = visitRow;
                        index = i;
                    } else {
                        if (newValue >= value) {
                        }
                        value = newValue;
                        row = visitRow;
                        index = i;
                    }
                }
            }
        } else {
            value = edge;
            for (int i2 = indexLimit + 1; visitedRows < this.mNumRows && i2 <= this.mLastVisibleIndex; i2++) {
                StaggeredGrid.Location loc2 = getLocation(i2);
                edge += loc2.offset;
                if (loc2.row != visitRow) {
                    visitRow = loc2.row;
                    visitedRows++;
                    if (findLarge) {
                        if (edge <= value) {
                        }
                        value = edge;
                        row = visitRow;
                        index = i2;
                    } else {
                        if (edge >= value) {
                        }
                        value = edge;
                        row = visitRow;
                        index = i2;
                    }
                }
            }
        }
        if (indices != null) {
            indices[0] = row;
            indices[1] = index;
        }
        return value;
    }

    private int findRowEdgeLimitSearchIndex(boolean append) {
        boolean wrapped = false;
        if (append) {
            for (int index = this.mLastVisibleIndex; index >= this.mFirstVisibleIndex; index--) {
                int row = getLocation(index).row;
                if (row == 0) {
                    wrapped = true;
                } else if (wrapped && row == this.mNumRows - 1) {
                    return index;
                }
            }
            return -1;
        }
        for (int index2 = this.mFirstVisibleIndex; index2 <= this.mLastVisibleIndex; index2++) {
            int row2 = getLocation(index2).row;
            if (row2 == this.mNumRows - 1) {
                wrapped = true;
            } else if (wrapped && row2 == 0) {
                return index2;
            }
        }
        return -1;
    }

    /* JADX WARN: Code restructure failed: missing block: B:107:0x0144, code lost:
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x015c, code lost:
        return r2;
     */
    @Override // android.support.v17.leanback.widget.StaggeredGrid
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected boolean appendVisibleItemsWithoutCache(int r16, boolean r17) {
        /*
            Method dump skipped, instructions count: 369
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.StaggeredGridDefault.appendVisibleItemsWithoutCache(int, boolean):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:105:0x013a, code lost:
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x0152, code lost:
        return r0;
     */
    @Override // android.support.v17.leanback.widget.StaggeredGrid
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected boolean prependVisibleItemsWithoutCache(int r14, boolean r15) {
        /*
            Method dump skipped, instructions count: 362
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.StaggeredGridDefault.prependVisibleItemsWithoutCache(int, boolean):boolean");
    }
}
