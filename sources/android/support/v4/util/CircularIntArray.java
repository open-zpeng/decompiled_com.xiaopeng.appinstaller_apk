package android.support.v4.util;
/* loaded from: classes.dex */
public final class CircularIntArray {
    private int mCapacityBitmask;
    private int[] mElements;
    private int mHead;
    private int mTail;

    private void doubleCapacity() {
        int n = this.mElements.length;
        int r = n - this.mHead;
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new RuntimeException("Max array capacity exceeded");
        }
        int[] a2 = new int[newCapacity];
        System.arraycopy(this.mElements, this.mHead, a2, 0, r);
        System.arraycopy(this.mElements, 0, a2, r, this.mHead);
        this.mElements = a2;
        this.mHead = 0;
        this.mTail = n;
        this.mCapacityBitmask = newCapacity - 1;
    }

    public CircularIntArray() {
        this(8);
    }

    public CircularIntArray(int minCapacity) {
        int arrayCapacity;
        if (minCapacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        if (minCapacity <= 1073741824) {
            if (Integer.bitCount(minCapacity) != 1) {
                arrayCapacity = Integer.highestOneBit(minCapacity - 1) << 1;
            } else {
                arrayCapacity = minCapacity;
            }
            this.mCapacityBitmask = arrayCapacity - 1;
            this.mElements = new int[arrayCapacity];
            return;
        }
        throw new IllegalArgumentException("capacity must be <= 2^30");
    }

    public void addLast(int e) {
        this.mElements[this.mTail] = e;
        this.mTail = (this.mTail + 1) & this.mCapacityBitmask;
        if (this.mTail == this.mHead) {
            doubleCapacity();
        }
    }

    public int popLast() {
        if (this.mHead == this.mTail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int t = (this.mTail - 1) & this.mCapacityBitmask;
        int result = this.mElements[t];
        this.mTail = t;
        return result;
    }

    public void clear() {
        this.mTail = this.mHead;
    }

    public int getLast() {
        if (this.mHead == this.mTail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.mElements[(this.mTail - 1) & this.mCapacityBitmask];
    }

    public int get(int n) {
        if (n < 0 || n >= size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.mElements[(this.mHead + n) & this.mCapacityBitmask];
    }

    public int size() {
        return (this.mTail - this.mHead) & this.mCapacityBitmask;
    }
}
