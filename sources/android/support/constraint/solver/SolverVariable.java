package android.support.constraint.solver;

import java.util.Arrays;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class SolverVariable {
    private static int uniqueId = 1;
    public float computedValue;
    private String mName;
    Type mType;
    public int id = -1;
    int definitionId = -1;
    public int strength = 0;
    float[] strengthVector = new float[6];
    ArrayRow[] mClientEquations = new ArrayRow[8];
    int mClientEquationsCount = 0;

    /* loaded from: classes.dex */
    public enum Type {
        UNRESTRICTED,
        CONSTANT,
        SLACK,
        ERROR,
        UNKNOWN
    }

    public SolverVariable(Type type) {
        this.mType = type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearStrengths() {
        for (int i = 0; i < 6; i++) {
            this.strengthVector[i] = 0.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String strengthsToString() {
        String representation = this + "[";
        for (int j = 0; j < this.strengthVector.length; j++) {
            String representation2 = representation + this.strengthVector[j];
            representation = j < this.strengthVector.length - 1 ? representation2 + ", " : representation2 + "] ";
        }
        return representation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addClientEquation(ArrayRow equation) {
        for (int i = 0; i < this.mClientEquationsCount; i++) {
            if (this.mClientEquations[i] == equation) {
                return;
            }
        }
        int i2 = this.mClientEquationsCount;
        if (i2 >= this.mClientEquations.length) {
            this.mClientEquations = (ArrayRow[]) Arrays.copyOf(this.mClientEquations, this.mClientEquations.length * 2);
        }
        this.mClientEquations[this.mClientEquationsCount] = equation;
        this.mClientEquationsCount++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeClientEquation(ArrayRow equation) {
        for (int i = 0; i < this.mClientEquationsCount; i++) {
            if (this.mClientEquations[i] == equation) {
                for (int j = 0; j < (this.mClientEquationsCount - i) - 1; j++) {
                    this.mClientEquations[i + j] = this.mClientEquations[i + j + 1];
                }
                int j2 = this.mClientEquationsCount;
                this.mClientEquationsCount = j2 - 1;
                return;
            }
        }
    }

    public void reset() {
        this.mName = null;
        this.mType = Type.UNKNOWN;
        this.strength = 0;
        this.id = -1;
        this.definitionId = -1;
        this.computedValue = 0.0f;
        this.mClientEquationsCount = 0;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    public String toString() {
        String result = BuildConfig.FLAVOR + this.mName;
        return result;
    }
}
