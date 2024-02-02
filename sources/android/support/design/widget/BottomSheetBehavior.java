package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public class BottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    int activePointerId;
    private BottomSheetCallback callback;
    int collapsedOffset;
    private final ViewDragHelper.Callback dragCallback;
    private boolean fitToContents;
    int fitToContentsOffset;
    int halfExpandedOffset;
    boolean hideable;
    private boolean ignoreEvents;
    private int initialY;
    private int lastNestedScrollDy;
    private int lastPeekHeight;
    private float maximumVelocity;
    private boolean nestedScrolled;
    WeakReference<View> nestedScrollingChildRef;
    int parentHeight;
    private int peekHeight;
    private boolean peekHeightAuto;
    private int peekHeightMin;
    private boolean skipCollapsed;
    int state;
    boolean touchingScrollingChild;
    private VelocityTracker velocityTracker;
    ViewDragHelper viewDragHelper;
    WeakReference<V> viewRef;

    /* loaded from: classes.dex */
    public static abstract class BottomSheetCallback {
        public abstract void onSlide(View view, float f);

        public abstract void onStateChanged(View view, int i);
    }

    public BottomSheetBehavior() {
        this.fitToContents = true;
        this.state = 4;
        this.dragCallback = new ViewDragHelper.Callback() { // from class: android.support.design.widget.BottomSheetBehavior.2
            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public boolean tryCaptureView(View child, int pointerId) {
                View scroll;
                if (BottomSheetBehavior.this.state == 1 || BottomSheetBehavior.this.touchingScrollingChild) {
                    return false;
                }
                if (BottomSheetBehavior.this.state == 3 && BottomSheetBehavior.this.activePointerId == pointerId && (scroll = BottomSheetBehavior.this.nestedScrollingChildRef.get()) != null && scroll.canScrollVertically(-1)) {
                    return false;
                }
                return BottomSheetBehavior.this.viewRef != null && BottomSheetBehavior.this.viewRef.get() == child;
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                BottomSheetBehavior.this.dispatchOnSlide(top);
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public void onViewDragStateChanged(int state) {
                if (state == 1) {
                    BottomSheetBehavior.this.setStateInternal(1);
                }
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int top;
                int top2;
                int top3;
                int targetState = 4;
                if (yvel < 0.0f) {
                    if (BottomSheetBehavior.this.fitToContents) {
                        top = BottomSheetBehavior.this.fitToContentsOffset;
                        targetState = 3;
                    } else {
                        if (releasedChild.getTop() > BottomSheetBehavior.this.halfExpandedOffset) {
                            top3 = BottomSheetBehavior.this.halfExpandedOffset;
                            targetState = 6;
                        } else {
                            top3 = 0;
                            targetState = 3;
                        }
                        top = top3;
                    }
                } else if (BottomSheetBehavior.this.hideable && BottomSheetBehavior.this.shouldHide(releasedChild, yvel)) {
                    top = BottomSheetBehavior.this.parentHeight;
                    targetState = 5;
                } else {
                    int top4 = (yvel > 0.0f ? 1 : (yvel == 0.0f ? 0 : -1));
                    if (top4 == 0) {
                        int currentTop = releasedChild.getTop();
                        if (BottomSheetBehavior.this.fitToContents) {
                            if (Math.abs(currentTop - BottomSheetBehavior.this.fitToContentsOffset) < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                                top2 = BottomSheetBehavior.this.fitToContentsOffset;
                                targetState = 3;
                            } else {
                                top2 = BottomSheetBehavior.this.collapsedOffset;
                                targetState = 4;
                            }
                        } else if (currentTop < BottomSheetBehavior.this.halfExpandedOffset) {
                            if (currentTop < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                                top2 = 0;
                                targetState = 3;
                            } else {
                                top2 = BottomSheetBehavior.this.halfExpandedOffset;
                                targetState = 6;
                            }
                        } else if (Math.abs(currentTop - BottomSheetBehavior.this.halfExpandedOffset) < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                            top2 = BottomSheetBehavior.this.halfExpandedOffset;
                            targetState = 6;
                        } else {
                            top2 = BottomSheetBehavior.this.collapsedOffset;
                        }
                        top = top2;
                    } else {
                        top = BottomSheetBehavior.this.collapsedOffset;
                    }
                }
                int targetState2 = targetState;
                if (BottomSheetBehavior.this.viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                    BottomSheetBehavior.this.setStateInternal(2);
                    ViewCompat.postOnAnimation(releasedChild, new SettleRunnable(releasedChild, targetState2));
                    return;
                }
                BottomSheetBehavior.this.setStateInternal(targetState2);
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public int clampViewPositionVertical(View child, int top, int dy) {
                return MathUtils.constrain(top, BottomSheetBehavior.this.getExpandedOffset(), BottomSheetBehavior.this.hideable ? BottomSheetBehavior.this.parentHeight : BottomSheetBehavior.this.collapsedOffset);
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return child.getLeft();
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public int getViewVerticalDragRange(View child) {
                if (BottomSheetBehavior.this.hideable) {
                    return BottomSheetBehavior.this.parentHeight;
                }
                return BottomSheetBehavior.this.collapsedOffset;
            }
        };
    }

    public BottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.fitToContents = true;
        this.state = 4;
        this.dragCallback = new ViewDragHelper.Callback() { // from class: android.support.design.widget.BottomSheetBehavior.2
            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public boolean tryCaptureView(View child, int pointerId) {
                View scroll;
                if (BottomSheetBehavior.this.state == 1 || BottomSheetBehavior.this.touchingScrollingChild) {
                    return false;
                }
                if (BottomSheetBehavior.this.state == 3 && BottomSheetBehavior.this.activePointerId == pointerId && (scroll = BottomSheetBehavior.this.nestedScrollingChildRef.get()) != null && scroll.canScrollVertically(-1)) {
                    return false;
                }
                return BottomSheetBehavior.this.viewRef != null && BottomSheetBehavior.this.viewRef.get() == child;
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                BottomSheetBehavior.this.dispatchOnSlide(top);
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public void onViewDragStateChanged(int state) {
                if (state == 1) {
                    BottomSheetBehavior.this.setStateInternal(1);
                }
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int top;
                int top2;
                int top3;
                int targetState = 4;
                if (yvel < 0.0f) {
                    if (BottomSheetBehavior.this.fitToContents) {
                        top = BottomSheetBehavior.this.fitToContentsOffset;
                        targetState = 3;
                    } else {
                        if (releasedChild.getTop() > BottomSheetBehavior.this.halfExpandedOffset) {
                            top3 = BottomSheetBehavior.this.halfExpandedOffset;
                            targetState = 6;
                        } else {
                            top3 = 0;
                            targetState = 3;
                        }
                        top = top3;
                    }
                } else if (BottomSheetBehavior.this.hideable && BottomSheetBehavior.this.shouldHide(releasedChild, yvel)) {
                    top = BottomSheetBehavior.this.parentHeight;
                    targetState = 5;
                } else {
                    int top4 = (yvel > 0.0f ? 1 : (yvel == 0.0f ? 0 : -1));
                    if (top4 == 0) {
                        int currentTop = releasedChild.getTop();
                        if (BottomSheetBehavior.this.fitToContents) {
                            if (Math.abs(currentTop - BottomSheetBehavior.this.fitToContentsOffset) < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                                top2 = BottomSheetBehavior.this.fitToContentsOffset;
                                targetState = 3;
                            } else {
                                top2 = BottomSheetBehavior.this.collapsedOffset;
                                targetState = 4;
                            }
                        } else if (currentTop < BottomSheetBehavior.this.halfExpandedOffset) {
                            if (currentTop < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                                top2 = 0;
                                targetState = 3;
                            } else {
                                top2 = BottomSheetBehavior.this.halfExpandedOffset;
                                targetState = 6;
                            }
                        } else if (Math.abs(currentTop - BottomSheetBehavior.this.halfExpandedOffset) < Math.abs(currentTop - BottomSheetBehavior.this.collapsedOffset)) {
                            top2 = BottomSheetBehavior.this.halfExpandedOffset;
                            targetState = 6;
                        } else {
                            top2 = BottomSheetBehavior.this.collapsedOffset;
                        }
                        top = top2;
                    } else {
                        top = BottomSheetBehavior.this.collapsedOffset;
                    }
                }
                int targetState2 = targetState;
                if (BottomSheetBehavior.this.viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                    BottomSheetBehavior.this.setStateInternal(2);
                    ViewCompat.postOnAnimation(releasedChild, new SettleRunnable(releasedChild, targetState2));
                    return;
                }
                BottomSheetBehavior.this.setStateInternal(targetState2);
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public int clampViewPositionVertical(View child, int top, int dy) {
                return MathUtils.constrain(top, BottomSheetBehavior.this.getExpandedOffset(), BottomSheetBehavior.this.hideable ? BottomSheetBehavior.this.parentHeight : BottomSheetBehavior.this.collapsedOffset);
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return child.getLeft();
            }

            @Override // android.support.v4.widget.ViewDragHelper.Callback
            public int getViewVerticalDragRange(View child) {
                if (BottomSheetBehavior.this.hideable) {
                    return BottomSheetBehavior.this.parentHeight;
                }
                return BottomSheetBehavior.this.collapsedOffset;
            }
        };
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetBehavior_Layout);
        TypedValue value = a2.peekValue(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
        if (value != null && value.data == -1) {
            setPeekHeight(value.data);
        } else {
            setPeekHeight(a2.getDimensionPixelSize(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, -1));
        }
        setHideable(a2.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
        setFitToContents(a2.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_fitToContents, true));
        setSkipCollapsed(a2.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed, false));
        a2.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), this.state);
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        if (ss.state == 1 || ss.state == 2) {
            this.state = 4;
        } else {
            this.state = ss.state;
        }
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child.setFitsSystemWindows(true);
        }
        int savedTop = child.getTop();
        parent.onLayoutChild(child, layoutDirection);
        this.parentHeight = parent.getHeight();
        if (this.peekHeightAuto) {
            if (this.peekHeightMin == 0) {
                this.peekHeightMin = parent.getResources().getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
            }
            this.lastPeekHeight = Math.max(this.peekHeightMin, this.parentHeight - ((parent.getWidth() * 9) / 16));
        } else {
            this.lastPeekHeight = this.peekHeight;
        }
        this.fitToContentsOffset = Math.max(0, this.parentHeight - child.getHeight());
        this.halfExpandedOffset = this.parentHeight / 2;
        calculateCollapsedOffset();
        if (this.state == 3) {
            ViewCompat.offsetTopAndBottom(child, getExpandedOffset());
        } else if (this.state == 6) {
            ViewCompat.offsetTopAndBottom(child, this.halfExpandedOffset);
        } else if (this.hideable && this.state == 5) {
            ViewCompat.offsetTopAndBottom(child, this.parentHeight);
        } else if (this.state == 4) {
            ViewCompat.offsetTopAndBottom(child, this.collapsedOffset);
        } else if (this.state == 1 || this.state == 2) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
        }
        if (this.viewDragHelper == null) {
            this.viewDragHelper = ViewDragHelper.create(parent, this.dragCallback);
        }
        this.viewRef = new WeakReference<>(child);
        this.nestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0098 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:57:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onInterceptTouchEvent(android.support.design.widget.CoordinatorLayout r9, V r10, android.view.MotionEvent r11) {
        /*
            r8 = this;
            boolean r0 = r10.isShown()
            r1 = 0
            r2 = 1
            if (r0 != 0) goto Lb
            r8.ignoreEvents = r2
            return r1
        Lb:
            int r0 = r11.getActionMasked()
            if (r0 != 0) goto L14
            r8.reset()
        L14:
            android.view.VelocityTracker r3 = r8.velocityTracker
            if (r3 != 0) goto L1e
            android.view.VelocityTracker r3 = android.view.VelocityTracker.obtain()
            r8.velocityTracker = r3
        L1e:
            android.view.VelocityTracker r3 = r8.velocityTracker
            r3.addMovement(r11)
            r3 = 3
            r4 = -1
            r5 = 0
            if (r0 == r3) goto L6e
            switch(r0) {
                case 0: goto L2c;
                case 1: goto L6e;
                default: goto L2b;
            }
        L2b:
            goto L79
        L2c:
            float r3 = r11.getX()
            int r3 = (int) r3
            float r6 = r11.getY()
            int r6 = (int) r6
            r8.initialY = r6
            java.lang.ref.WeakReference<android.view.View> r6 = r8.nestedScrollingChildRef
            if (r6 == 0) goto L45
            java.lang.ref.WeakReference<android.view.View> r6 = r8.nestedScrollingChildRef
            java.lang.Object r6 = r6.get()
            android.view.View r6 = (android.view.View) r6
            goto L46
        L45:
            r6 = r5
        L46:
            if (r6 == 0) goto L5c
            int r7 = r8.initialY
            boolean r7 = r9.isPointInChildBounds(r6, r3, r7)
            if (r7 == 0) goto L5c
            int r7 = r11.getActionIndex()
            int r7 = r11.getPointerId(r7)
            r8.activePointerId = r7
            r8.touchingScrollingChild = r2
        L5c:
            int r7 = r8.activePointerId
            if (r7 != r4) goto L6a
            int r4 = r8.initialY
            boolean r4 = r9.isPointInChildBounds(r10, r3, r4)
            if (r4 != 0) goto L6a
            r4 = r2
            goto L6b
        L6a:
            r4 = r1
        L6b:
            r8.ignoreEvents = r4
            goto L79
        L6e:
            r8.touchingScrollingChild = r1
            r8.activePointerId = r4
            boolean r3 = r8.ignoreEvents
            if (r3 == 0) goto L79
            r8.ignoreEvents = r1
            return r1
        L79:
            boolean r3 = r8.ignoreEvents
            if (r3 != 0) goto L86
            android.support.v4.widget.ViewDragHelper r3 = r8.viewDragHelper
            boolean r3 = r3.shouldInterceptTouchEvent(r11)
            if (r3 == 0) goto L86
            return r2
        L86:
            java.lang.ref.WeakReference<android.view.View> r3 = r8.nestedScrollingChildRef
            if (r3 == 0) goto L94
            java.lang.ref.WeakReference<android.view.View> r3 = r8.nestedScrollingChildRef
            java.lang.Object r3 = r3.get()
            r5 = r3
            android.view.View r5 = (android.view.View) r5
        L94:
            r3 = r5
            r4 = 2
            if (r0 != r4) goto Lcb
            if (r3 == 0) goto Lcb
            boolean r4 = r8.ignoreEvents
            if (r4 != 0) goto Lcb
            int r4 = r8.state
            if (r4 == r2) goto Lcb
            float r4 = r11.getX()
            int r4 = (int) r4
            float r5 = r11.getY()
            int r5 = (int) r5
            boolean r4 = r9.isPointInChildBounds(r3, r4, r5)
            if (r4 != 0) goto Lcb
            int r4 = r8.initialY
            float r4 = (float) r4
            float r5 = r11.getY()
            float r4 = r4 - r5
            float r4 = java.lang.Math.abs(r4)
            android.support.v4.widget.ViewDragHelper r5 = r8.viewDragHelper
            int r5 = r5.getTouchSlop()
            float r5 = (float) r5
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto Lcb
            r1 = r2
            goto Lcc
        Lcb:
        Lcc:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.BottomSheetBehavior.onInterceptTouchEvent(android.support.design.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = event.getActionMasked();
        if (this.state == 1 && action == 0) {
            return true;
        }
        if (this.viewDragHelper != null) {
            this.viewDragHelper.processTouchEvent(event);
        }
        if (action == 0) {
            reset();
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(event);
        if (action == 2 && !this.ignoreEvents && Math.abs(this.initialY - event.getY()) > this.viewDragHelper.getTouchSlop()) {
            this.viewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
        }
        return !this.ignoreEvents;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int axes, int type) {
        this.lastNestedScrollDy = 0;
        this.nestedScrolled = false;
        return (axes & 2) != 0;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, int type) {
        if (type == 1) {
            return;
        }
        View scrollingChild = this.nestedScrollingChildRef.get();
        if (target != scrollingChild) {
            return;
        }
        int currentTop = child.getTop();
        int newTop = currentTop - dy;
        if (dy > 0) {
            if (newTop < getExpandedOffset()) {
                consumed[1] = currentTop - getExpandedOffset();
                ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                setStateInternal(3);
            } else {
                consumed[1] = dy;
                ViewCompat.offsetTopAndBottom(child, -dy);
                setStateInternal(1);
            }
        } else if (dy < 0 && !target.canScrollVertically(-1)) {
            if (newTop > this.collapsedOffset && !this.hideable) {
                consumed[1] = currentTop - this.collapsedOffset;
                ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                setStateInternal(4);
            } else {
                consumed[1] = dy;
                ViewCompat.offsetTopAndBottom(child, -dy);
                setStateInternal(1);
            }
        }
        dispatchOnSlide(child.getTop());
        this.lastNestedScrollDy = dy;
        this.nestedScrolled = true;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int type) {
        int top;
        int targetState;
        int top2;
        if (child.getTop() == getExpandedOffset()) {
            setStateInternal(3);
        } else if (target != this.nestedScrollingChildRef.get() || !this.nestedScrolled) {
        } else {
            int targetState2 = 4;
            if (this.lastNestedScrollDy > 0) {
                top = getExpandedOffset();
                targetState2 = 3;
            } else if (this.hideable && shouldHide(child, getYVelocity())) {
                top = this.parentHeight;
                targetState2 = 5;
            } else {
                int top3 = this.lastNestedScrollDy;
                if (top3 == 0) {
                    int currentTop = child.getTop();
                    if (this.fitToContents) {
                        if (Math.abs(currentTop - this.fitToContentsOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                            top2 = this.fitToContentsOffset;
                            targetState = 3;
                        } else {
                            top2 = this.collapsedOffset;
                            targetState = 4;
                        }
                    } else if (currentTop < this.halfExpandedOffset) {
                        if (currentTop < Math.abs(currentTop - this.collapsedOffset)) {
                            top2 = 0;
                            targetState = 3;
                        } else {
                            top2 = this.halfExpandedOffset;
                            targetState = 6;
                        }
                    } else if (Math.abs(currentTop - this.halfExpandedOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                        top2 = this.halfExpandedOffset;
                        targetState = 6;
                    } else {
                        int top4 = this.collapsedOffset;
                        top = top4;
                        targetState = 4;
                        targetState2 = targetState;
                    }
                    top = top2;
                    targetState2 = targetState;
                } else {
                    top = this.collapsedOffset;
                }
            }
            if (this.viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                setStateInternal(2);
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState2));
            } else {
                setStateInternal(targetState2);
            }
            this.nestedScrolled = false;
        }
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        return target == this.nestedScrollingChildRef.get() && (this.state != 3 || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY));
    }

    public void setFitToContents(boolean fitToContents) {
        if (this.fitToContents == fitToContents) {
            return;
        }
        this.fitToContents = fitToContents;
        if (this.viewRef != null) {
            calculateCollapsedOffset();
        }
        setStateInternal((this.fitToContents && this.state == 6) ? 3 : this.state);
    }

    public final void setPeekHeight(int peekHeight) {
        V view;
        boolean layout = false;
        if (peekHeight == -1) {
            if (!this.peekHeightAuto) {
                this.peekHeightAuto = true;
                layout = true;
            }
        } else if (this.peekHeightAuto || this.peekHeight != peekHeight) {
            this.peekHeightAuto = false;
            this.peekHeight = Math.max(0, peekHeight);
            this.collapsedOffset = this.parentHeight - peekHeight;
            layout = true;
        }
        if (layout && this.state == 4 && this.viewRef != null && (view = this.viewRef.get()) != null) {
            view.requestLayout();
        }
    }

    public void setHideable(boolean hideable) {
        this.hideable = hideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed) {
        this.skipCollapsed = skipCollapsed;
    }

    void setStateInternal(int state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        View bottomSheet = this.viewRef.get();
        if (bottomSheet != null && this.callback != null) {
            this.callback.onStateChanged(bottomSheet, state);
        }
    }

    private void calculateCollapsedOffset() {
        if (this.fitToContents) {
            this.collapsedOffset = Math.max(this.parentHeight - this.lastPeekHeight, this.fitToContentsOffset);
        } else {
            this.collapsedOffset = this.parentHeight - this.lastPeekHeight;
        }
    }

    private void reset() {
        this.activePointerId = -1;
        if (this.velocityTracker != null) {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    boolean shouldHide(View child, float yvel) {
        if (this.skipCollapsed) {
            return true;
        }
        if (child.getTop() < this.collapsedOffset) {
            return false;
        }
        float newTop = child.getTop() + (0.1f * yvel);
        return Math.abs(newTop - ((float) this.collapsedOffset)) / ((float) this.peekHeight) > 0.5f;
    }

    View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
            return null;
        }
        return null;
    }

    private float getYVelocity() {
        this.velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
        return this.velocityTracker.getYVelocity(this.activePointerId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getExpandedOffset() {
        if (this.fitToContents) {
            return this.fitToContentsOffset;
        }
        return 0;
    }

    void dispatchOnSlide(int top) {
        View bottomSheet = this.viewRef.get();
        if (bottomSheet != null && this.callback != null) {
            if (top > this.collapsedOffset) {
                this.callback.onSlide(bottomSheet, (this.collapsedOffset - top) / (this.parentHeight - this.collapsedOffset));
            } else {
                this.callback.onSlide(bottomSheet, (this.collapsedOffset - top) / (this.collapsedOffset - getExpandedOffset()));
            }
        }
    }

    int getPeekHeightMin() {
        return this.peekHeightMin;
    }

    /* loaded from: classes.dex */
    private class SettleRunnable implements Runnable {
        private final int targetState;
        private final View view;

        SettleRunnable(View view, int targetState) {
            this.view = view;
            this.targetState = targetState;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (BottomSheetBehavior.this.viewDragHelper != null && BottomSheetBehavior.this.viewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this);
            } else {
                BottomSheetBehavior.this.setStateInternal(this.targetState);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.design.widget.BottomSheetBehavior.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, (ClassLoader) null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        final int state;

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.state = source.readInt();
        }

        public SavedState(Parcelable superState, int state) {
            super(superState);
            this.state = state;
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.state);
        }
    }
}
