package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class WidgetContainer extends ConstraintWidget {
    protected ArrayList<ConstraintWidget> mChildren = new ArrayList<>();

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void reset() {
        this.mChildren.clear();
        super.reset();
    }

    public void add(ConstraintWidget widget) {
        this.mChildren.add(widget);
        if (widget.getParent() != null) {
            WidgetContainer container = (WidgetContainer) widget.getParent();
            container.remove(widget);
        }
        widget.setParent(this);
    }

    public void remove(ConstraintWidget widget) {
        this.mChildren.remove(widget);
        widget.setParent(null);
    }

    public ConstraintWidgetContainer getRootConstraintContainer() {
        ConstraintWidget parent = getParent();
        ConstraintWidgetContainer container = null;
        if (this instanceof ConstraintWidgetContainer) {
            container = (ConstraintWidgetContainer) this;
        }
        while (parent != null) {
            ConstraintWidget item = parent;
            parent = item.getParent();
            if (item instanceof ConstraintWidgetContainer) {
                container = (ConstraintWidgetContainer) item;
            }
        }
        return container;
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void setOffset(int x, int y) {
        super.setOffset(x, y);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = this.mChildren.get(i);
            widget.setOffset(getRootX(), getRootY());
        }
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void updateDrawPosition() {
        super.updateDrawPosition();
        if (this.mChildren == null) {
            return;
        }
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = this.mChildren.get(i);
            widget.setOffset(getDrawX(), getDrawY());
            if (!(widget instanceof ConstraintWidgetContainer)) {
                widget.updateDrawPosition();
            }
        }
    }

    public void layout() {
        updateDrawPosition();
        if (this.mChildren == null) {
            return;
        }
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = this.mChildren.get(i);
            if (widget instanceof WidgetContainer) {
                ((WidgetContainer) widget).layout();
            }
        }
    }

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public void resetSolverVariables(Cache cache) {
        super.resetSolverVariables(cache);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = this.mChildren.get(i);
            widget.resetSolverVariables(cache);
        }
    }

    public void removeAllChildren() {
        this.mChildren.clear();
    }
}
