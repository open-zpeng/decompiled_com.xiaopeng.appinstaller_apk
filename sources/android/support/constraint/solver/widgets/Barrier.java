package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
/* loaded from: classes.dex */
public class Barrier extends Helper {
    private int mBarrierType = 0;

    public void setBarrierType(int barrierType) {
        this.mBarrierType = barrierType;
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void addToSolver(LinearSystem system, int group) {
        this.mListAnchors[0] = this.mLeft;
        this.mListAnchors[2] = this.mTop;
        this.mListAnchors[1] = this.mRight;
        this.mListAnchors[3] = this.mBottom;
        for (int i = 0; i < this.mListAnchors.length; i++) {
            this.mListAnchors[i].mSolverVariable = system.createObjectVariable(this.mListAnchors[i]);
        }
        int i2 = this.mBarrierType;
        if (i2 >= 0 && this.mBarrierType < 4) {
            ConstraintAnchor position = this.mListAnchors[this.mBarrierType];
            for (int i3 = 0; i3 < this.mWidgetsCount; i3++) {
                SolverVariable target = system.createObjectVariable(this.mWidgets[i3].mListAnchors[this.mBarrierType]);
                this.mWidgets[i3].mListAnchors[this.mBarrierType].mSolverVariable = target;
                if (this.mBarrierType == 0 || this.mBarrierType == 2) {
                    system.addLowerThan(position.mSolverVariable, target, 0, 0);
                } else {
                    system.addGreaterThan(position.mSolverVariable, target, 0, 0);
                }
            }
            int i4 = this.mBarrierType;
            if (i4 == 0) {
                system.addEquality(this.mRight.mSolverVariable, this.mLeft.mSolverVariable, 0, 5);
            } else if (this.mBarrierType == 1) {
                system.addEquality(this.mLeft.mSolverVariable, this.mRight.mSolverVariable, 0, 5);
            } else if (this.mBarrierType == 2) {
                system.addEquality(this.mBottom.mSolverVariable, this.mTop.mSolverVariable, 0, 5);
            } else if (this.mBarrierType == 3) {
                system.addEquality(this.mTop.mSolverVariable, this.mBottom.mSolverVariable, 0, 5);
            }
        }
    }
}
