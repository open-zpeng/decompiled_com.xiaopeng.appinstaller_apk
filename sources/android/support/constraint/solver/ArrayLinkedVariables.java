package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable;
import java.util.Arrays;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class ArrayLinkedVariables {
    private final Cache mCache;
    private final ArrayRow mRow;
    int currentSize = 0;
    private int ROW_SIZE = 8;
    private SolverVariable candidate = null;
    private int[] mArrayIndices = new int[this.ROW_SIZE];
    private int[] mArrayNextIndices = new int[this.ROW_SIZE];
    private float[] mArrayValues = new float[this.ROW_SIZE];
    private int mHead = -1;
    private int mLast = -1;
    private boolean mDidFillOnce = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayLinkedVariables(ArrayRow arrayRow, Cache cache) {
        this.mRow = arrayRow;
        this.mCache = cache;
    }

    public final void put(SolverVariable variable, float value) {
        if (value == 0.0f) {
            remove(variable);
        } else if (this.mHead == -1) {
            this.mHead = 0;
            this.mArrayValues[this.mHead] = value;
            this.mArrayIndices[this.mHead] = variable.id;
            this.mArrayNextIndices[this.mHead] = -1;
            this.currentSize++;
            if (!this.mDidFillOnce) {
                this.mLast++;
            }
        } else {
            int current = this.mHead;
            int previous = -1;
            int previous2 = current;
            for (int current2 = 0; previous2 != -1 && current2 < this.currentSize; current2++) {
                if (this.mArrayIndices[previous2] == variable.id) {
                    this.mArrayValues[previous2] = value;
                    return;
                }
                if (this.mArrayIndices[previous2] < variable.id) {
                    previous = previous2;
                }
                previous2 = this.mArrayNextIndices[previous2];
            }
            int availableIndice = this.mLast + 1;
            if (this.mDidFillOnce) {
                if (this.mArrayIndices[this.mLast] == -1) {
                    availableIndice = this.mLast;
                } else {
                    availableIndice = this.mArrayIndices.length;
                }
            }
            if (availableIndice >= this.mArrayIndices.length && this.currentSize < this.mArrayIndices.length) {
                int i = 0;
                while (true) {
                    if (i < this.mArrayIndices.length) {
                        if (this.mArrayIndices[i] != -1) {
                            i++;
                        } else {
                            availableIndice = i;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            if (availableIndice >= this.mArrayIndices.length) {
                availableIndice = this.mArrayIndices.length;
                this.ROW_SIZE *= 2;
                this.mDidFillOnce = false;
                this.mLast = availableIndice - 1;
                this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
                this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
                this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
            }
            this.mArrayIndices[availableIndice] = variable.id;
            this.mArrayValues[availableIndice] = value;
            if (previous != -1) {
                this.mArrayNextIndices[availableIndice] = this.mArrayNextIndices[previous];
                this.mArrayNextIndices[previous] = availableIndice;
            } else {
                this.mArrayNextIndices[availableIndice] = this.mHead;
                this.mHead = availableIndice;
            }
            this.currentSize++;
            if (!this.mDidFillOnce) {
                this.mLast++;
            }
            if (this.currentSize >= this.mArrayIndices.length) {
                this.mDidFillOnce = true;
            }
        }
    }

    public final void add(SolverVariable variable, float value) {
        if (value == 0.0f) {
            return;
        }
        if (this.mHead == -1) {
            this.mHead = 0;
            this.mArrayValues[this.mHead] = value;
            this.mArrayIndices[this.mHead] = variable.id;
            this.mArrayNextIndices[this.mHead] = -1;
            this.currentSize++;
            if (!this.mDidFillOnce) {
                this.mLast++;
                return;
            }
            return;
        }
        int current = this.mHead;
        int previous = -1;
        int previous2 = current;
        for (int current2 = 0; previous2 != -1 && current2 < this.currentSize; current2++) {
            int idx = this.mArrayIndices[previous2];
            if (idx == variable.id) {
                float[] fArr = this.mArrayValues;
                fArr[previous2] = fArr[previous2] + value;
                if (this.mArrayValues[previous2] == 0.0f) {
                    if (previous2 == this.mHead) {
                        this.mHead = this.mArrayNextIndices[previous2];
                    } else {
                        this.mArrayNextIndices[previous] = this.mArrayNextIndices[previous2];
                    }
                    this.mCache.mIndexedVariables[idx].removeClientEquation(this.mRow);
                    if (this.mDidFillOnce) {
                        this.mLast = previous2;
                    }
                    this.currentSize--;
                    return;
                }
                return;
            }
            if (this.mArrayIndices[previous2] < variable.id) {
                previous = previous2;
            }
            previous2 = this.mArrayNextIndices[previous2];
        }
        int availableIndice = this.mLast + 1;
        if (this.mDidFillOnce) {
            if (this.mArrayIndices[this.mLast] == -1) {
                availableIndice = this.mLast;
            } else {
                availableIndice = this.mArrayIndices.length;
            }
        }
        if (availableIndice >= this.mArrayIndices.length && this.currentSize < this.mArrayIndices.length) {
            int i = 0;
            while (true) {
                if (i < this.mArrayIndices.length) {
                    if (this.mArrayIndices[i] != -1) {
                        i++;
                    } else {
                        availableIndice = i;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (availableIndice >= this.mArrayIndices.length) {
            availableIndice = this.mArrayIndices.length;
            this.ROW_SIZE *= 2;
            this.mDidFillOnce = false;
            this.mLast = availableIndice - 1;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
        }
        this.mArrayIndices[availableIndice] = variable.id;
        this.mArrayValues[availableIndice] = value;
        if (previous != -1) {
            this.mArrayNextIndices[availableIndice] = this.mArrayNextIndices[previous];
            this.mArrayNextIndices[previous] = availableIndice;
        } else {
            this.mArrayNextIndices[availableIndice] = this.mHead;
            this.mHead = availableIndice;
        }
        this.currentSize++;
        if (!this.mDidFillOnce) {
            this.mLast++;
        }
        if (this.mLast >= this.mArrayIndices.length) {
            this.mDidFillOnce = true;
            this.mLast = this.mArrayIndices.length - 1;
        }
    }

    public final float remove(SolverVariable variable) {
        if (this.candidate == variable) {
            this.candidate = null;
        }
        if (this.mHead == -1) {
            return 0.0f;
        }
        int current = this.mHead;
        int previous = -1;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            int idx = this.mArrayIndices[current];
            if (idx == variable.id) {
                if (current == this.mHead) {
                    this.mHead = this.mArrayNextIndices[current];
                } else {
                    this.mArrayNextIndices[previous] = this.mArrayNextIndices[current];
                }
                this.mCache.mIndexedVariables[idx].removeClientEquation(this.mRow);
                this.currentSize--;
                this.mArrayIndices[current] = -1;
                if (this.mDidFillOnce) {
                    this.mLast = current;
                }
                return this.mArrayValues[current];
            }
            previous = current;
            current = this.mArrayNextIndices[current];
        }
        return 0.0f;
    }

    public final void clear() {
        this.mHead = -1;
        this.mLast = -1;
        this.mDidFillOnce = false;
        this.currentSize = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean containsKey(SolverVariable variable) {
        if (this.mHead == -1) {
            return false;
        }
        int current = this.mHead;
        int current2 = current;
        for (int current3 = 0; current2 != -1 && current3 < this.currentSize; current3++) {
            if (this.mArrayIndices[current2] == variable.id) {
                return true;
            }
            current2 = this.mArrayNextIndices[current2];
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invert() {
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            float[] fArr = this.mArrayValues;
            fArr[current] = fArr[current] * (-1.0f);
            current = this.mArrayNextIndices[current];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void divideByAmount(float amount) {
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            float[] fArr = this.mArrayValues;
            fArr[current] = fArr[current] / amount;
            current = this.mArrayNextIndices[current];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateClientEquations(ArrayRow row) {
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            this.mCache.mIndexedVariables[this.mArrayIndices[current]].addClientEquation(row);
            current = this.mArrayNextIndices[current];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SolverVariable pickPivotCandidate() {
        SolverVariable restrictedCandidate = null;
        SolverVariable unrestrictedCandidate = null;
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            float amount = this.mArrayValues[current];
            if (amount < 0.0f) {
                if (amount > (-0.001f)) {
                    this.mArrayValues[current] = 0.0f;
                    amount = 0.0f;
                }
            } else if (amount < 0.001f) {
                this.mArrayValues[current] = 0.0f;
                amount = 0.0f;
            }
            if (amount != 0.0f) {
                SolverVariable variable = this.mCache.mIndexedVariables[this.mArrayIndices[current]];
                if (variable.mType == SolverVariable.Type.UNRESTRICTED) {
                    if (amount < 0.0f) {
                        return variable;
                    }
                    if (unrestrictedCandidate == null) {
                        unrestrictedCandidate = variable;
                    }
                } else if (amount < 0.0f && (restrictedCandidate == null || variable.strength < restrictedCandidate.strength)) {
                    restrictedCandidate = variable;
                }
            }
            current = this.mArrayNextIndices[current];
        }
        if (unrestrictedCandidate != null) {
            return unrestrictedCandidate;
        }
        return restrictedCandidate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFromRow(ArrayRow self, ArrayRow definition) {
        int current = this.mHead;
        int current2 = current;
        int current3 = 0;
        while (current2 != -1 && current3 < this.currentSize) {
            if (this.mArrayIndices[current2] == definition.variable.id) {
                float value = this.mArrayValues[current2];
                remove(definition.variable);
                ArrayLinkedVariables definitionVariables = definition.variables;
                int definitionCurrent = definitionVariables.mHead;
                int definitionCurrent2 = definitionCurrent;
                for (int definitionCurrent3 = 0; definitionCurrent2 != -1 && definitionCurrent3 < definitionVariables.currentSize; definitionCurrent3++) {
                    SolverVariable definitionVariable = this.mCache.mIndexedVariables[definitionVariables.mArrayIndices[definitionCurrent2]];
                    float definitionValue = definitionVariables.mArrayValues[definitionCurrent2];
                    add(definitionVariable, definitionValue * value);
                    definitionCurrent2 = definitionVariables.mArrayNextIndices[definitionCurrent2];
                }
                self.constantValue += definition.constantValue * value;
                definition.variable.removeClientEquation(self);
                current2 = this.mHead;
                current3 = 0;
            } else {
                current2 = this.mArrayNextIndices[current2];
                current3++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFromSystem(ArrayRow self, ArrayRow[] rows) {
        int current = this.mHead;
        int current2 = current;
        int current3 = 0;
        while (current2 != -1 && current3 < this.currentSize) {
            SolverVariable variable = this.mCache.mIndexedVariables[this.mArrayIndices[current2]];
            if (variable.definitionId != -1) {
                float value = this.mArrayValues[current2];
                remove(variable);
                ArrayRow definition = rows[variable.definitionId];
                if (!definition.isSimpleDefinition) {
                    ArrayLinkedVariables definitionVariables = definition.variables;
                    int definitionCurrent = definitionVariables.mHead;
                    int definitionCurrent2 = definitionCurrent;
                    for (int definitionCurrent3 = 0; definitionCurrent2 != -1 && definitionCurrent3 < definitionVariables.currentSize; definitionCurrent3++) {
                        SolverVariable definitionVariable = this.mCache.mIndexedVariables[definitionVariables.mArrayIndices[definitionCurrent2]];
                        float definitionValue = definitionVariables.mArrayValues[definitionCurrent2];
                        add(definitionVariable, definitionValue * value);
                        definitionCurrent2 = definitionVariables.mArrayNextIndices[definitionCurrent2];
                    }
                }
                self.constantValue += definition.constantValue * value;
                definition.variable.removeClientEquation(self);
                current2 = this.mHead;
                current3 = 0;
            } else {
                current2 = this.mArrayNextIndices[current2];
                current3++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SolverVariable getVariable(int index) {
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            if (counter == index) {
                return this.mCache.mIndexedVariables[this.mArrayIndices[current]];
            }
            current = this.mArrayNextIndices[current];
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final float getVariableValue(int index) {
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            if (counter == index) {
                return this.mArrayValues[current];
            }
            current = this.mArrayNextIndices[current];
        }
        return 0.0f;
    }

    public final float get(SolverVariable v) {
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            if (this.mArrayIndices[current] == v.id) {
                return this.mArrayValues[current];
            }
            current = this.mArrayNextIndices[current];
        }
        return 0.0f;
    }

    public String toString() {
        String result = BuildConfig.FLAVOR;
        int current = this.mHead;
        for (int counter = 0; current != -1 && counter < this.currentSize; counter++) {
            result = ((result + " -> ") + this.mArrayValues[current] + " : ") + this.mCache.mIndexedVariables[this.mArrayIndices[current]];
            current = this.mArrayNextIndices[current];
        }
        return result;
    }
}
