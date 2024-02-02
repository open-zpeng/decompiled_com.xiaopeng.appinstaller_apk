package android.support.v7.util;

import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class DiffUtil {
    private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator<Snake>() { // from class: android.support.v7.util.DiffUtil.1
        @Override // java.util.Comparator
        public int compare(Snake o1, Snake o2) {
            int cmpX = o1.x - o2.x;
            return cmpX == 0 ? o1.y - o2.y : cmpX;
        }
    };

    public static DiffResult calculateDiff(Callback cb) {
        return calculateDiff(cb, true);
    }

    public static DiffResult calculateDiff(Callback cb, boolean detectMoves) {
        int oldSize = cb.getOldListSize();
        int newSize = cb.getNewListSize();
        List<Snake> snakes = new ArrayList<>();
        List<Range> stack = new ArrayList<>();
        stack.add(new Range(0, oldSize, 0, newSize));
        int max = oldSize + newSize + Math.abs(oldSize - newSize);
        int[] forward = new int[max * 2];
        int[] backward = new int[max * 2];
        List<Range> rangePool = new ArrayList<>();
        while (true) {
            List<Range> rangePool2 = rangePool;
            if (!stack.isEmpty()) {
                Range range = stack.remove(stack.size() - 1);
                Snake snake = diffPartial(cb, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, forward, backward, max);
                if (snake != null) {
                    if (snake.size > 0) {
                        snakes.add(snake);
                    }
                    snake.x += range.oldListStart;
                    snake.y += range.newListStart;
                    Range left = rangePool2.isEmpty() ? new Range() : rangePool2.remove(rangePool2.size() - 1);
                    left.oldListStart = range.oldListStart;
                    left.newListStart = range.newListStart;
                    if (snake.reverse) {
                        left.oldListEnd = snake.x;
                        left.newListEnd = snake.y;
                    } else if (snake.removal) {
                        left.oldListEnd = snake.x - 1;
                        left.newListEnd = snake.y;
                    } else {
                        left.oldListEnd = snake.x;
                        left.newListEnd = snake.y - 1;
                    }
                    stack.add(left);
                    if (!snake.reverse) {
                        range.oldListStart = snake.x + snake.size;
                        range.newListStart = snake.y + snake.size;
                    } else if (snake.removal) {
                        range.oldListStart = snake.x + snake.size + 1;
                        range.newListStart = snake.y + snake.size;
                    } else {
                        range.oldListStart = snake.x + snake.size;
                        range.newListStart = snake.y + snake.size + 1;
                    }
                    stack.add(range);
                } else {
                    rangePool2.add(range);
                }
                rangePool = rangePool2;
            } else {
                Collections.sort(snakes, SNAKE_COMPARATOR);
                return new DiffResult(cb, snakes, forward, backward, detectMoves);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0053, code lost:
        if (r26[(r28 + r13) - 1] < r26[(r28 + r13) + 1]) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00f6, code lost:
        if (r27[(r28 + r3) - 1] < r27[(r28 + r3) + 1]) goto L88;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:28:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x008d A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.support.v7.util.DiffUtil.Snake diffPartial(android.support.v7.util.DiffUtil.Callback r21, int r22, int r23, int r24, int r25, int[] r26, int[] r27, int r28) {
        /*
            Method dump skipped, instructions count: 386
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.util.DiffUtil.diffPartial(android.support.v7.util.DiffUtil$Callback, int, int, int, int, int[], int[], int):android.support.v7.util.DiffUtil$Snake");
    }

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public abstract boolean areContentsTheSame(int i, int i2);

        public abstract boolean areItemsTheSame(int i, int i2);

        public abstract int getNewListSize();

        public abstract int getOldListSize();

        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Snake {
        boolean removal;
        boolean reverse;
        int size;
        int x;
        int y;

        Snake() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Range {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;

        public Range() {
        }

        public Range(int oldListStart, int oldListEnd, int newListStart, int newListEnd) {
            this.oldListStart = oldListStart;
            this.oldListEnd = oldListEnd;
            this.newListStart = newListStart;
            this.newListEnd = newListEnd;
        }
    }

    /* loaded from: classes.dex */
    public static class DiffResult {
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;
        private final List<Snake> mSnakes;

        DiffResult(Callback callback, List<Snake> snakes, int[] oldItemStatuses, int[] newItemStatuses, boolean detectMoves) {
            this.mSnakes = snakes;
            this.mOldItemStatuses = oldItemStatuses;
            this.mNewItemStatuses = newItemStatuses;
            Arrays.fill(this.mOldItemStatuses, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = callback;
            this.mOldListSize = callback.getOldListSize();
            this.mNewListSize = callback.getNewListSize();
            this.mDetectMoves = detectMoves;
            addRootSnake();
            findMatchingItems();
        }

        private void addRootSnake() {
            Snake firstSnake = this.mSnakes.isEmpty() ? null : this.mSnakes.get(0);
            if (firstSnake == null || firstSnake.x != 0 || firstSnake.y != 0) {
                Snake root = new Snake();
                root.x = 0;
                root.y = 0;
                root.removal = false;
                root.size = 0;
                root.reverse = false;
                this.mSnakes.add(0, root);
            }
        }

        private void findMatchingItems() {
            int posOld = this.mOldListSize;
            int posNew = this.mNewListSize;
            for (int i = this.mSnakes.size() - 1; i >= 0; i--) {
                Snake snake = this.mSnakes.get(i);
                int endX = snake.x + snake.size;
                int endY = snake.y + snake.size;
                if (this.mDetectMoves) {
                    while (posOld > endX) {
                        findAddition(posOld, posNew, i);
                        posOld--;
                    }
                    while (posNew > endY) {
                        findRemoval(posOld, posNew, i);
                        posNew--;
                    }
                }
                for (int j = 0; j < snake.size; j++) {
                    int oldItemPos = snake.x + j;
                    int newItemPos = snake.y + j;
                    boolean theSame = this.mCallback.areContentsTheSame(oldItemPos, newItemPos);
                    int changeFlag = theSame ? 1 : 2;
                    this.mOldItemStatuses[oldItemPos] = (newItemPos << 5) | changeFlag;
                    this.mNewItemStatuses[newItemPos] = (oldItemPos << 5) | changeFlag;
                }
                posOld = snake.x;
                posNew = snake.y;
            }
        }

        private void findAddition(int x, int y, int snakeIndex) {
            if (this.mOldItemStatuses[x - 1] != 0) {
                return;
            }
            findMatchingItem(x, y, snakeIndex, false);
        }

        private void findRemoval(int x, int y, int snakeIndex) {
            if (this.mNewItemStatuses[y - 1] != 0) {
                return;
            }
            findMatchingItem(x, y, snakeIndex, true);
        }

        private boolean findMatchingItem(int x, int y, int snakeIndex, boolean removal) {
            int myItemPos;
            int curX;
            int curY;
            int changeFlag;
            if (removal) {
                myItemPos = y - 1;
                curX = x;
                curY = y - 1;
            } else {
                myItemPos = x - 1;
                curX = x - 1;
                curY = y;
            }
            int curY2 = curY;
            int curX2 = curX;
            for (int curX3 = snakeIndex; curX3 >= 0; curX3--) {
                Snake snake = this.mSnakes.get(curX3);
                int endX = snake.x + snake.size;
                int endY = snake.y + snake.size;
                if (removal) {
                    for (int pos = curX2 - 1; pos >= endX; pos--) {
                        if (this.mCallback.areItemsTheSame(pos, myItemPos)) {
                            boolean theSame = this.mCallback.areContentsTheSame(pos, myItemPos);
                            changeFlag = theSame ? 8 : 4;
                            this.mNewItemStatuses[myItemPos] = (pos << 5) | 16;
                            this.mOldItemStatuses[pos] = (myItemPos << 5) | changeFlag;
                            return true;
                        }
                    }
                    continue;
                } else {
                    for (int pos2 = curY2 - 1; pos2 >= endY; pos2--) {
                        if (this.mCallback.areItemsTheSame(myItemPos, pos2)) {
                            boolean theSame2 = this.mCallback.areContentsTheSame(myItemPos, pos2);
                            changeFlag = theSame2 ? 8 : 4;
                            this.mOldItemStatuses[x - 1] = (pos2 << 5) | 16;
                            this.mNewItemStatuses[pos2] = ((x - 1) << 5) | changeFlag;
                            return true;
                        }
                    }
                    continue;
                }
                curX2 = snake.x;
                curY2 = snake.y;
            }
            return false;
        }

        public void dispatchUpdatesTo(RecyclerView.Adapter adapter) {
            dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }

        public void dispatchUpdatesTo(ListUpdateCallback updateCallback) {
            BatchingListUpdateCallback batchingCallback;
            BatchingListUpdateCallback batchingListUpdateCallback;
            int endY;
            int snakeSize;
            ListUpdateCallback listUpdateCallback = updateCallback;
            if (listUpdateCallback instanceof BatchingListUpdateCallback) {
                batchingCallback = (BatchingListUpdateCallback) listUpdateCallback;
                batchingListUpdateCallback = listUpdateCallback;
            } else {
                batchingCallback = new BatchingListUpdateCallback(listUpdateCallback);
                batchingListUpdateCallback = batchingCallback;
            }
            BatchingListUpdateCallback batchingCallback2 = batchingCallback;
            List<PostponedUpdate> postponedUpdates = new ArrayList<>();
            int posOld = this.mOldListSize;
            int posNew = this.mNewListSize;
            int snakeIndex = this.mSnakes.size() - 1;
            int posOld2 = posOld;
            int posNew2 = posNew;
            while (true) {
                int snakeIndex2 = snakeIndex;
                if (snakeIndex2 >= 0) {
                    Snake snake = this.mSnakes.get(snakeIndex2);
                    int snakeSize2 = snake.size;
                    int endX = snake.x + snakeSize2;
                    int endY2 = snake.y + snakeSize2;
                    if (endX < posOld2) {
                        endY = endY2;
                        dispatchRemovals(postponedUpdates, batchingCallback2, endX, posOld2 - endX, endX);
                    } else {
                        endY = endY2;
                    }
                    if (endY < posNew2) {
                        snakeSize = snakeSize2;
                        dispatchAdditions(postponedUpdates, batchingCallback2, endX, posNew2 - endY, endY);
                    } else {
                        snakeSize = snakeSize2;
                    }
                    int i = snakeSize - 1;
                    while (true) {
                        int i2 = i;
                        if (i2 >= 0) {
                            if ((this.mOldItemStatuses[snake.x + i2] & 31) == 2) {
                                batchingCallback2.onChanged(snake.x + i2, 1, this.mCallback.getChangePayload(snake.x + i2, snake.y + i2));
                            }
                            i = i2 - 1;
                        }
                    }
                    posOld2 = snake.x;
                    posNew2 = snake.y;
                    snakeIndex = snakeIndex2 - 1;
                } else {
                    batchingCallback2.dispatchLastEvent();
                    return;
                }
            }
        }

        private static PostponedUpdate removePostponedUpdate(List<PostponedUpdate> updates, int pos, boolean removal) {
            for (int i = updates.size() - 1; i >= 0; i--) {
                PostponedUpdate update = updates.get(i);
                if (update.posInOwnerList == pos && update.removal == removal) {
                    updates.remove(i);
                    for (int j = i; j < updates.size(); j++) {
                        updates.get(j).currentPos += removal ? 1 : -1;
                    }
                    return update;
                }
            }
            return null;
        }

        private void dispatchAdditions(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (!this.mDetectMoves) {
                updateCallback.onInserted(start, count);
                return;
            }
            for (int i = count - 1; i >= 0; i--) {
                int status = this.mNewItemStatuses[globalIndex + i] & 31;
                if (status == 0) {
                    updateCallback.onInserted(start, 1);
                    for (PostponedUpdate update : postponedUpdates) {
                        update.currentPos++;
                    }
                } else if (status == 4 || status == 8) {
                    int pos = this.mNewItemStatuses[globalIndex + i] >> 5;
                    updateCallback.onMoved(removePostponedUpdate(postponedUpdates, pos, true).currentPos, start);
                    if (status == 4) {
                        updateCallback.onChanged(start, 1, this.mCallback.getChangePayload(pos, globalIndex + i));
                    }
                } else if (status == 16) {
                    postponedUpdates.add(new PostponedUpdate(globalIndex + i, start, false));
                } else {
                    throw new IllegalStateException("unknown flag for pos " + (globalIndex + i) + " " + Long.toBinaryString(status));
                }
            }
        }

        private void dispatchRemovals(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (!this.mDetectMoves) {
                updateCallback.onRemoved(start, count);
                return;
            }
            for (int i = count - 1; i >= 0; i--) {
                int status = this.mOldItemStatuses[globalIndex + i] & 31;
                if (status == 0) {
                    updateCallback.onRemoved(start + i, 1);
                    for (PostponedUpdate update : postponedUpdates) {
                        update.currentPos--;
                    }
                } else if (status == 4 || status == 8) {
                    int pos = this.mOldItemStatuses[globalIndex + i] >> 5;
                    PostponedUpdate update2 = removePostponedUpdate(postponedUpdates, pos, false);
                    updateCallback.onMoved(start + i, update2.currentPos - 1);
                    if (status == 4) {
                        updateCallback.onChanged(update2.currentPos - 1, 1, this.mCallback.getChangePayload(globalIndex + i, pos));
                    }
                } else if (status == 16) {
                    postponedUpdates.add(new PostponedUpdate(globalIndex + i, start + i, true));
                } else {
                    throw new IllegalStateException("unknown flag for pos " + (globalIndex + i) + " " + Long.toBinaryString(status));
                }
            }
        }

        List<Snake> getSnakes() {
            return this.mSnakes;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PostponedUpdate {
        int currentPos;
        int posInOwnerList;
        boolean removal;

        public PostponedUpdate(int posInOwnerList, int currentPos, boolean removal) {
            this.posInOwnerList = posInOwnerList;
            this.currentPos = currentPos;
            this.removal = removal;
        }
    }
}
