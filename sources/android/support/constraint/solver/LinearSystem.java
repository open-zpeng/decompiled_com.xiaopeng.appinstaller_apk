package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import java.util.Arrays;
import java.util.HashMap;
/* loaded from: classes.dex */
public class LinearSystem {
    private static int POOL_SIZE = 1000;
    final Cache mCache;
    private ArrayRow[] mRows;
    int mVariablesID = 0;
    private HashMap<String, SolverVariable> mVariables = null;
    private Goal mGoal = new Goal();
    private int TABLE_SIZE = 32;
    private int mMaxColumns = this.TABLE_SIZE;
    private boolean[] mAlreadyTestedCandidates = new boolean[this.TABLE_SIZE];
    int mNumColumns = 1;
    private int mNumRows = 0;
    private int mMaxRows = this.TABLE_SIZE;
    private SolverVariable[] mPoolVariables = new SolverVariable[POOL_SIZE];
    private int mPoolVariablesCount = 0;
    private ArrayRow[] tempClientsCopy = new ArrayRow[this.TABLE_SIZE];

    public LinearSystem() {
        this.mRows = null;
        this.mRows = new ArrayRow[this.TABLE_SIZE];
        releaseRows();
        this.mCache = new Cache();
    }

    private void increaseTableSize() {
        this.TABLE_SIZE *= 2;
        this.mRows = (ArrayRow[]) Arrays.copyOf(this.mRows, this.TABLE_SIZE);
        this.mCache.mIndexedVariables = (SolverVariable[]) Arrays.copyOf(this.mCache.mIndexedVariables, this.TABLE_SIZE);
        this.mAlreadyTestedCandidates = new boolean[this.TABLE_SIZE];
        this.mMaxColumns = this.TABLE_SIZE;
        this.mMaxRows = this.TABLE_SIZE;
        this.mGoal.variables.clear();
    }

    private void releaseRows() {
        for (int i = 0; i < this.mRows.length; i++) {
            ArrayRow row = this.mRows[i];
            if (row != null) {
                this.mCache.arrayRowPool.release(row);
            }
            this.mRows[i] = null;
        }
    }

    public void reset() {
        for (int i = 0; i < this.mCache.mIndexedVariables.length; i++) {
            SolverVariable variable = this.mCache.mIndexedVariables[i];
            if (variable != null) {
                variable.reset();
            }
        }
        this.mCache.solverVariablePool.releaseAll(this.mPoolVariables, this.mPoolVariablesCount);
        this.mPoolVariablesCount = 0;
        Arrays.fill(this.mCache.mIndexedVariables, (Object) null);
        if (this.mVariables != null) {
            this.mVariables.clear();
        }
        this.mVariablesID = 0;
        this.mGoal.variables.clear();
        this.mNumColumns = 1;
        for (int i2 = 0; i2 < this.mNumRows; i2++) {
            this.mRows[i2].used = false;
        }
        releaseRows();
        this.mNumRows = 0;
    }

    public SolverVariable createObjectVariable(Object anchor) {
        if (anchor == null) {
            return null;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable variable = null;
        if (anchor instanceof ConstraintAnchor) {
            variable = ((ConstraintAnchor) anchor).getSolverVariable();
            if (variable == null) {
                ((ConstraintAnchor) anchor).resetSolverVariable(this.mCache);
                variable = ((ConstraintAnchor) anchor).getSolverVariable();
            }
            if (variable.id == -1 || variable.id > this.mVariablesID || this.mCache.mIndexedVariables[variable.id] == null) {
                if (variable.id != -1) {
                    variable.reset();
                }
                this.mVariablesID++;
                this.mNumColumns++;
                variable.id = this.mVariablesID;
                variable.mType = SolverVariable.Type.UNRESTRICTED;
                this.mCache.mIndexedVariables[this.mVariablesID] = variable;
            }
        }
        return variable;
    }

    public ArrayRow createRow() {
        ArrayRow row = this.mCache.arrayRowPool.acquire();
        if (row == null) {
            return new ArrayRow(this.mCache);
        }
        row.reset();
        return row;
    }

    public SolverVariable createSlackVariable() {
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable variable = acquireSolverVariable(SolverVariable.Type.SLACK);
        this.mVariablesID++;
        this.mNumColumns++;
        variable.id = this.mVariablesID;
        this.mCache.mIndexedVariables[this.mVariablesID] = variable;
        return variable;
    }

    private void addError(ArrayRow row) {
        SolverVariable error1 = createErrorVariable();
        SolverVariable error2 = createErrorVariable();
        row.addError(error1, error2);
    }

    private void addSingleError(ArrayRow row, int sign) {
        SolverVariable error = createErrorVariable();
        row.addSingleError(error, sign);
    }

    public SolverVariable createErrorVariable() {
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable variable = acquireSolverVariable(SolverVariable.Type.ERROR);
        this.mVariablesID++;
        this.mNumColumns++;
        variable.id = this.mVariablesID;
        this.mCache.mIndexedVariables[this.mVariablesID] = variable;
        return variable;
    }

    private SolverVariable acquireSolverVariable(SolverVariable.Type type) {
        SolverVariable variable = this.mCache.solverVariablePool.acquire();
        if (variable == null) {
            variable = new SolverVariable(type);
        } else {
            variable.reset();
            variable.setType(type);
        }
        if (this.mPoolVariablesCount >= POOL_SIZE) {
            POOL_SIZE *= 2;
            this.mPoolVariables = (SolverVariable[]) Arrays.copyOf(this.mPoolVariables, POOL_SIZE);
        }
        SolverVariable[] solverVariableArr = this.mPoolVariables;
        int i = this.mPoolVariablesCount;
        this.mPoolVariablesCount = i + 1;
        solverVariableArr[i] = variable;
        return variable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayRow getRow(int n) {
        return this.mRows[n];
    }

    public int getObjectVariableValue(Object anchor) {
        SolverVariable variable = ((ConstraintAnchor) anchor).getSolverVariable();
        if (variable != null) {
            return (int) (variable.computedValue + 0.5f);
        }
        return 0;
    }

    public void minimize() throws Exception {
        minimizeGoal(this.mGoal);
    }

    void minimizeGoal(Goal goal) throws Exception {
        goal.updateFromSystem(this);
        enforceBFS(goal);
        optimize(goal);
        computeValues();
    }

    private void updateRowFromVariables(ArrayRow row) {
        if (this.mNumRows > 0) {
            row.variables.updateFromSystem(row, this.mRows);
            if (row.variables.currentSize == 0) {
                row.isSimpleDefinition = true;
            }
        }
    }

    public void addConstraint(ArrayRow row) {
        if (row == null) {
            return;
        }
        if (this.mNumRows + 1 >= this.mMaxRows || this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        if (!row.isSimpleDefinition) {
            updateRowFromVariables(row);
            row.ensurePositiveConstant();
            row.pickRowVariable();
            if (!row.hasKeyVariable()) {
                return;
            }
        }
        if (this.mRows[this.mNumRows] != null) {
            this.mCache.arrayRowPool.release(this.mRows[this.mNumRows]);
        }
        if (!row.isSimpleDefinition) {
            row.updateClientEquations();
        }
        this.mRows[this.mNumRows] = row;
        row.variable.definitionId = this.mNumRows;
        this.mNumRows++;
        int count = row.variable.mClientEquationsCount;
        if (count > 0) {
            while (this.tempClientsCopy.length < count) {
                this.tempClientsCopy = new ArrayRow[this.tempClientsCopy.length * 2];
            }
            ArrayRow[] clients = this.tempClientsCopy;
            for (int i = 0; i < count; i++) {
                clients[i] = row.variable.mClientEquations[i];
            }
            for (int i2 = 0; i2 < count; i2++) {
                ArrayRow client = clients[i2];
                if (client != row) {
                    client.variables.updateFromRow(client, row);
                    client.updateClientEquations();
                }
            }
        }
    }

    private int optimize(Goal goal) {
        for (int i = 0; i < this.mNumColumns; i++) {
            this.mAlreadyTestedCandidates[i] = false;
        }
        int tries = 0;
        boolean done = false;
        int tested = 0;
        while (!done) {
            tries++;
            SolverVariable pivotCandidate = goal.getPivotCandidate();
            if (pivotCandidate != null) {
                if (this.mAlreadyTestedCandidates[pivotCandidate.id]) {
                    pivotCandidate = null;
                } else {
                    this.mAlreadyTestedCandidates[pivotCandidate.id] = true;
                    tested++;
                    if (tested >= this.mNumColumns) {
                        done = true;
                    }
                }
            }
            if (pivotCandidate != null) {
                int pivotRowIndex = -1;
                float min = Float.MAX_VALUE;
                for (int i2 = 0; i2 < this.mNumRows; i2++) {
                    ArrayRow current = this.mRows[i2];
                    SolverVariable variable = current.variable;
                    if (variable.mType != SolverVariable.Type.UNRESTRICTED && current.hasVariable(pivotCandidate)) {
                        float a_j = current.variables.get(pivotCandidate);
                        if (a_j < 0.0f) {
                            float value = (-current.constantValue) / a_j;
                            if (value < min) {
                                min = value;
                                pivotRowIndex = i2;
                            }
                        }
                    }
                }
                if (pivotRowIndex > -1) {
                    ArrayRow pivotEquation = this.mRows[pivotRowIndex];
                    pivotEquation.variable.definitionId = -1;
                    pivotEquation.pivot(pivotCandidate);
                    pivotEquation.variable.definitionId = pivotRowIndex;
                    for (int i3 = 0; i3 < this.mNumRows; i3++) {
                        this.mRows[i3].updateRowWithEquation(pivotEquation);
                    }
                    goal.updateFromSystem(this);
                    try {
                        enforceBFS(goal);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    done = true;
                }
            } else {
                done = true;
            }
        }
        return tries;
    }

    private int enforceBFS(Goal goal) throws Exception {
        int tries = 0;
        boolean infeasibleSystem = false;
        int i = 0;
        while (true) {
            if (i >= this.mNumRows) {
                break;
            }
            SolverVariable variable = this.mRows[i].variable;
            if (variable.mType == SolverVariable.Type.UNRESTRICTED || this.mRows[i].constantValue >= 0.0f) {
                i++;
            } else {
                infeasibleSystem = true;
                break;
            }
        }
        if (infeasibleSystem) {
            boolean done = false;
            tries = 0;
            while (!done) {
                tries++;
                int pivotRowIndex = -1;
                int pivotRowIndex2 = -1;
                int strength = 0;
                float min = Float.MAX_VALUE;
                for (int i2 = 0; i2 < this.mNumRows; i2++) {
                    ArrayRow current = this.mRows[i2];
                    SolverVariable variable2 = current.variable;
                    if (variable2.mType != SolverVariable.Type.UNRESTRICTED && current.constantValue < 0.0f) {
                        for (int j = 1; j < this.mNumColumns; j++) {
                            SolverVariable candidate = this.mCache.mIndexedVariables[j];
                            float a_j = current.variables.get(candidate);
                            if (a_j > 0.0f) {
                                int pivotColumnIndex = pivotRowIndex2;
                                int pivotRowIndex3 = pivotRowIndex;
                                float min2 = min;
                                for (int k = 0; k < 6; k++) {
                                    float value = candidate.strengthVector[k] / a_j;
                                    if ((value < min2 && k == strength) || k > strength) {
                                        min2 = value;
                                        pivotRowIndex3 = i2;
                                        pivotColumnIndex = j;
                                        int strength2 = k;
                                        strength = strength2;
                                    }
                                }
                                min = min2;
                                pivotRowIndex = pivotRowIndex3;
                                pivotRowIndex2 = pivotColumnIndex;
                            }
                        }
                    }
                }
                if (pivotRowIndex != -1) {
                    ArrayRow pivotEquation = this.mRows[pivotRowIndex];
                    pivotEquation.variable.definitionId = -1;
                    pivotEquation.pivot(this.mCache.mIndexedVariables[pivotRowIndex2]);
                    pivotEquation.variable.definitionId = pivotRowIndex;
                    for (int i3 = 0; i3 < this.mNumRows; i3++) {
                        this.mRows[i3].updateRowWithEquation(pivotEquation);
                    }
                    goal.updateFromSystem(this);
                } else {
                    done = true;
                }
            }
        }
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= this.mNumRows) {
                break;
            }
            SolverVariable variable3 = this.mRows[i5].variable;
            if (variable3.mType != SolverVariable.Type.UNRESTRICTED && this.mRows[i5].constantValue < 0.0f) {
                break;
            }
            i4 = i5 + 1;
        }
        return tries;
    }

    private void computeValues() {
        for (int i = 0; i < this.mNumRows; i++) {
            ArrayRow row = this.mRows[i];
            row.variable.computedValue = row.constantValue;
        }
    }

    public Cache getCache() {
        return this.mCache;
    }

    public void addGreaterThan(SolverVariable a2, SolverVariable b2, int margin, int strength) {
        ArrayRow row = createRow();
        SolverVariable slack = createSlackVariable();
        slack.strength = strength;
        row.createRowGreaterThan(a2, b2, slack, margin);
        addConstraint(row);
    }

    public void addLowerThan(SolverVariable a2, SolverVariable b2, int margin, int strength) {
        ArrayRow row = createRow();
        SolverVariable slack = createSlackVariable();
        slack.strength = strength;
        row.createRowLowerThan(a2, b2, slack, margin);
        addConstraint(row);
    }

    public void addCentering(SolverVariable a2, SolverVariable b2, int m1, float bias, SolverVariable c, SolverVariable d, int m2, int strength) {
        ArrayRow row = createRow();
        row.createRowCentering(a2, b2, m1, bias, c, d, m2);
        SolverVariable error1 = createErrorVariable();
        SolverVariable error2 = createErrorVariable();
        error1.strength = strength;
        error2.strength = strength;
        row.addError(error1, error2);
        addConstraint(row);
    }

    public ArrayRow addEquality(SolverVariable a2, SolverVariable b2, int margin, int strength) {
        ArrayRow row = createRow();
        row.createRowEquals(a2, b2, margin);
        SolverVariable error1 = createErrorVariable();
        SolverVariable error2 = createErrorVariable();
        error1.strength = strength;
        error2.strength = strength;
        row.addError(error1, error2);
        addConstraint(row);
        return row;
    }

    public void addEquality(SolverVariable a2, int value) {
        int idx = a2.definitionId;
        if (a2.definitionId != -1) {
            ArrayRow row = this.mRows[idx];
            if (row.isSimpleDefinition) {
                row.constantValue = value;
                return;
            }
            ArrayRow newRow = createRow();
            newRow.createRowEquals(a2, value);
            addConstraint(newRow);
            return;
        }
        ArrayRow row2 = createRow();
        row2.createRowDefinition(a2, value);
        addConstraint(row2);
    }

    public static ArrayRow createRowEquals(LinearSystem linearSystem, SolverVariable variableA, SolverVariable variableB, int margin, boolean withError) {
        ArrayRow row = linearSystem.createRow();
        row.createRowEquals(variableA, variableB, margin);
        if (withError) {
            linearSystem.addSingleError(row, 1);
        }
        return row;
    }

    public static ArrayRow createRowDimensionPercent(LinearSystem linearSystem, SolverVariable variableA, SolverVariable variableB, SolverVariable variableC, float percent, boolean withError) {
        ArrayRow row = linearSystem.createRow();
        if (withError) {
            linearSystem.addError(row);
        }
        return row.createRowDimensionPercent(variableA, variableB, variableC, percent);
    }

    public static ArrayRow createRowGreaterThan(LinearSystem linearSystem, SolverVariable variableA, SolverVariable variableB, int margin, boolean withError) {
        SolverVariable slack = linearSystem.createSlackVariable();
        ArrayRow row = linearSystem.createRow();
        row.createRowGreaterThan(variableA, variableB, slack, margin);
        if (withError) {
            float slackValue = row.variables.get(slack);
            linearSystem.addSingleError(row, (int) ((-1.0f) * slackValue));
        }
        return row;
    }

    public static ArrayRow createRowLowerThan(LinearSystem linearSystem, SolverVariable variableA, SolverVariable variableB, int margin, boolean withError) {
        SolverVariable slack = linearSystem.createSlackVariable();
        ArrayRow row = linearSystem.createRow();
        row.createRowLowerThan(variableA, variableB, slack, margin);
        if (withError) {
            float slackValue = row.variables.get(slack);
            linearSystem.addSingleError(row, (int) ((-1.0f) * slackValue));
        }
        return row;
    }

    public static ArrayRow createRowCentering(LinearSystem linearSystem, SolverVariable variableA, SolverVariable variableB, int marginA, float bias, SolverVariable variableC, SolverVariable variableD, int marginB, boolean withError) {
        ArrayRow row = linearSystem.createRow();
        row.createRowCentering(variableA, variableB, marginA, bias, variableC, variableD, marginB);
        if (withError) {
            SolverVariable error1 = linearSystem.createErrorVariable();
            SolverVariable error2 = linearSystem.createErrorVariable();
            error1.strength = 4;
            error2.strength = 4;
            row.addError(error1, error2);
        }
        return row;
    }
}
