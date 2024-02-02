package android.support.design.widget;

import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
/* loaded from: classes.dex */
public class TextInputEditText extends AppCompatEditText {
    @Override // android.support.v7.widget.AppCompatEditText, android.widget.TextView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic != null && outAttrs.hintText == null) {
            ViewParent parent = getParent();
            while (true) {
                if (!(parent instanceof View)) {
                    break;
                } else if (parent instanceof TextInputLayout) {
                    outAttrs.hintText = ((TextInputLayout) parent).getHint();
                    break;
                } else {
                    parent = parent.getParent();
                }
            }
        }
        return ic;
    }
}
