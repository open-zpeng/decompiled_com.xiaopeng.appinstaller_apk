package android.support.design.widget;

import android.widget.ImageButton;
/* loaded from: classes.dex */
class VisibilityAwareImageButton extends ImageButton {
    private int userSetVisibility;

    @Override // android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        internalSetVisibility(visibility, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalSetVisibility(int visibility, boolean fromUser) {
        super.setVisibility(visibility);
        if (fromUser) {
            this.userSetVisibility = visibility;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getUserSetVisibility() {
        return this.userSetVisibility;
    }
}
