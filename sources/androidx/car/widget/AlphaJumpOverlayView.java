package androidx.car.widget;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.car.R;
import androidx.car.widget.IAlphaJumpAdapter;
import java.util.Collection;
/* loaded from: classes.dex */
public class AlphaJumpOverlayView extends GridLayout {
    private IAlphaJumpAdapter mAdapter;
    private Collection<IAlphaJumpAdapter.Bucket> mBuckets;
    private PagedListView mPagedListView;

    public AlphaJumpOverlayView(Context context) {
        super(context);
        setBackgroundResource(R.color.car_card);
        setColumnCount(context.getResources().getInteger(R.integer.alpha_jump_button_columns));
        setUseDefaultMargins(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(PagedListView plv, IAlphaJumpAdapter adapter) {
        this.mPagedListView = plv;
        this.mAdapter = adapter;
        this.mBuckets = adapter.getAlphaJumpBuckets();
        createButtons();
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (visibility == 0 && changedView == this) {
            this.mAdapter.onAlphaJumpEnter();
        }
    }

    private void createButtons() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        removeAllViews();
        for (IAlphaJumpAdapter.Bucket bucket : this.mBuckets) {
            View container = inflater.inflate(R.layout.car_alpha_jump_button, (ViewGroup) this, false);
            TextView btn = (TextView) container.findViewById(R.id.button);
            btn.setText(bucket.getLabel());
            btn.setOnClickListener(new View.OnClickListener() { // from class: androidx.car.widget.-$$Lambda$AlphaJumpOverlayView$jvBkp9orbcY37Ll81Kas50b1n0U
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlphaJumpOverlayView.this.onButtonClick(view);
                }
            });
            btn.setTag(bucket);
            if (bucket.isEmpty()) {
                btn.setEnabled(false);
            }
            addView(container);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onButtonClick(View v) {
        setVisibility(8);
        IAlphaJumpAdapter.Bucket bucket = (IAlphaJumpAdapter.Bucket) v.getTag();
        if (bucket != null) {
            this.mAdapter.onAlphaJumpLeave(bucket);
            this.mPagedListView.snapToPosition(bucket.getIndex());
        }
    }
}
