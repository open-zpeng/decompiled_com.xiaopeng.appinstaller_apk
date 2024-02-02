package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import android.support.constraint.solver.SolverVariable;
import android.support.v7.preference.Preference;
import java.util.HashSet;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class ConstraintAnchor {
    final ConstraintWidget mOwner;
    SolverVariable mSolverVariable;
    ConstraintAnchor mTarget;
    final Type mType;
    public int mMargin = 0;
    int mGoneMargin = -1;
    private Strength mStrength = Strength.NONE;
    private ConnectionType mConnectionType = ConnectionType.RELAXED;
    private int mConnectionCreator = 0;
    int mGroup = Preference.DEFAULT_ORDER;

    /* loaded from: classes.dex */
    public enum ConnectionType {
        RELAXED,
        STRICT
    }

    /* loaded from: classes.dex */
    public enum Strength {
        NONE,
        STRONG,
        WEAK
    }

    /* loaded from: classes.dex */
    public enum Type {
        NONE,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        BASELINE,
        CENTER,
        CENTER_X,
        CENTER_Y
    }

    public ConstraintAnchor(ConstraintWidget owner, Type type) {
        this.mOwner = owner;
        this.mType = type;
    }

    public SolverVariable getSolverVariable() {
        return this.mSolverVariable;
    }

    public void resetSolverVariable(Cache cache) {
        if (this.mSolverVariable == null) {
            this.mSolverVariable = new SolverVariable(SolverVariable.Type.UNRESTRICTED);
        } else {
            this.mSolverVariable.reset();
        }
    }

    public ConstraintWidget getOwner() {
        return this.mOwner;
    }

    public Type getType() {
        return this.mType;
    }

    public int getMargin() {
        if (this.mOwner.getVisibility() == 8) {
            return 0;
        }
        if (this.mGoneMargin > -1 && this.mTarget != null && this.mTarget.mOwner.getVisibility() == 8) {
            return this.mGoneMargin;
        }
        return this.mMargin;
    }

    public Strength getStrength() {
        return this.mStrength;
    }

    public ConstraintAnchor getTarget() {
        return this.mTarget;
    }

    public ConnectionType getConnectionType() {
        return this.mConnectionType;
    }

    public void setConnectionType(ConnectionType type) {
        this.mConnectionType = type;
    }

    public int getConnectionCreator() {
        return this.mConnectionCreator;
    }

    public void reset() {
        this.mTarget = null;
        this.mMargin = 0;
        this.mGoneMargin = -1;
        this.mStrength = Strength.STRONG;
        this.mConnectionCreator = 0;
        this.mConnectionType = ConnectionType.RELAXED;
    }

    public boolean connect(ConstraintAnchor toAnchor, int margin, Strength strength, int creator) {
        return connect(toAnchor, margin, -1, strength, creator, false);
    }

    public boolean connect(ConstraintAnchor toAnchor, int margin, int goneMargin, Strength strength, int creator, boolean forceConnection) {
        if (toAnchor == null) {
            this.mTarget = null;
            this.mMargin = 0;
            this.mGoneMargin = -1;
            this.mStrength = Strength.NONE;
            this.mConnectionCreator = 2;
            return true;
        } else if (forceConnection || isValidConnection(toAnchor)) {
            this.mTarget = toAnchor;
            if (margin > 0) {
                this.mMargin = margin;
            } else {
                this.mMargin = 0;
            }
            this.mGoneMargin = goneMargin;
            this.mStrength = strength;
            this.mConnectionCreator = creator;
            return true;
        } else {
            return false;
        }
    }

    public boolean isConnected() {
        return this.mTarget != null;
    }

    public boolean isValidConnection(ConstraintAnchor anchor) {
        boolean isCompatible = false;
        if (anchor == null) {
            return false;
        }
        Type target = anchor.getType();
        if (target == this.mType) {
            if (this.mType == Type.CENTER) {
                return false;
            }
            return this.mType != Type.BASELINE || (anchor.getOwner().hasBaseline() && getOwner().hasBaseline());
        }
        switch (this.mType) {
            case CENTER:
                return (target == Type.BASELINE || target == Type.CENTER_X || target == Type.CENTER_Y) ? false : true;
            case LEFT:
            case RIGHT:
                boolean isCompatible2 = target == Type.LEFT || target == Type.RIGHT;
                if (anchor.getOwner() instanceof Guideline) {
                    if (isCompatible2 || target == Type.CENTER_X) {
                        isCompatible = true;
                    }
                    return isCompatible;
                }
                return isCompatible2;
            case TOP:
            case BOTTOM:
                boolean isCompatible3 = target == Type.TOP || target == Type.BOTTOM;
                if (anchor.getOwner() instanceof Guideline) {
                    if (isCompatible3 || target == Type.CENTER_Y) {
                        isCompatible = true;
                    }
                    return isCompatible;
                }
                return isCompatible3;
            case BASELINE:
            case CENTER_X:
            case CENTER_Y:
            case NONE:
                return false;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public String toString() {
        String str;
        HashSet<ConstraintAnchor> visited = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        sb.append(this.mOwner.getDebugName());
        sb.append(":");
        sb.append(this.mType.toString());
        if (this.mTarget != null) {
            str = " connected to " + this.mTarget.toString(visited);
        } else {
            str = BuildConfig.FLAVOR;
        }
        sb.append(str);
        return sb.toString();
    }

    private String toString(HashSet<ConstraintAnchor> visited) {
        String str;
        if (visited.add(this)) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mOwner.getDebugName());
            sb.append(":");
            sb.append(this.mType.toString());
            if (this.mTarget != null) {
                str = " connected to " + this.mTarget.toString(visited);
            } else {
                str = BuildConfig.FLAVOR;
            }
            sb.append(str);
            return sb.toString();
        }
        return "<-";
    }
}
