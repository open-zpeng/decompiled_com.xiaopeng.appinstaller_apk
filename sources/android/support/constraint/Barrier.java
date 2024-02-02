package android.support.constraint;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
/* loaded from: classes.dex */
public class Barrier extends ConstraintHelper {
    private android.support.constraint.solver.widgets.Barrier mBarrier;
    private int mIndicatedType;
    private int mResolvedType;

    public Barrier(Context context) {
        super(context);
        this.mIndicatedType = 0;
        this.mResolvedType = 0;
        super.setVisibility(8);
    }

    public int getType() {
        return this.mIndicatedType;
    }

    public void setType(int type) {
        this.mIndicatedType = type;
        this.mResolvedType = type;
        if (Build.VERSION.SDK_INT < 17) {
            if (this.mIndicatedType == 5) {
                this.mResolvedType = 0;
            } else if (this.mIndicatedType == 6) {
                this.mResolvedType = 1;
            }
        } else {
            Configuration config = getResources().getConfiguration();
            boolean isRtl = 1 == config.getLayoutDirection();
            if (isRtl) {
                if (this.mIndicatedType == 5) {
                    this.mResolvedType = 1;
                } else if (this.mIndicatedType == 6) {
                    this.mResolvedType = 0;
                }
            } else if (this.mIndicatedType == 5) {
                this.mResolvedType = 0;
            } else if (this.mIndicatedType == 6) {
                this.mResolvedType = 1;
            }
        }
        this.mBarrier.setBarrierType(this.mResolvedType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.constraint.ConstraintHelper
    public void init(AttributeSet attrs) {
        super.init(attrs);
        this.mBarrier = new android.support.constraint.solver.widgets.Barrier();
        if (attrs != null) {
            TypedArray a2 = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a2.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a2.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_barrierDirection) {
                    setType(a2.getInt(attr, 0));
                }
            }
        }
        this.mHelperWidget = this.mBarrier;
        validateParams();
    }
}
