package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/* loaded from: classes.dex */
public class TitleView extends FrameLayout {
    private int flags;
    private ImageView mBadgeView;
    private boolean mHasSearchListener;
    private SearchOrbView mSearchOrbView;
    private TextView mTextView;
    private final TitleViewAdapter mTitleViewAdapter;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.browseTitleViewStyle);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.flags = 6;
        this.mHasSearchListener = false;
        this.mTitleViewAdapter = new TitleViewAdapter() { // from class: android.support.v17.leanback.widget.TitleView.1
        };
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.lb_title_view, this);
        this.mBadgeView = (ImageView) rootView.findViewById(R.id.title_badge);
        this.mTextView = (TextView) rootView.findViewById(R.id.title_text);
        this.mSearchOrbView = (SearchOrbView) rootView.findViewById(R.id.title_orb);
        setClipToPadding(false);
        setClipChildren(false);
    }
}
