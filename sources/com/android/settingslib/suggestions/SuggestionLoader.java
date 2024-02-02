package com.android.settingslib.suggestions;

import android.content.Context;
import android.service.settings.suggestions.Suggestion;
import android.util.Log;
import com.android.settingslib.utils.AsyncLoader;
import java.util.List;
/* loaded from: classes.dex */
public class SuggestionLoader extends AsyncLoader<List<Suggestion>> {
    private final SuggestionController mSuggestionController;

    public SuggestionLoader(Context context, SuggestionController controller) {
        super(context);
        this.mSuggestionController = controller;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoader
    public void onDiscardResult(List<Suggestion> result) {
    }

    @Override // android.content.AsyncTaskLoader
    public List<Suggestion> loadInBackground() {
        List<Suggestion> data = this.mSuggestionController.getSuggestions();
        if (data == null) {
            Log.d("SuggestionLoader", "data is null");
        } else {
            Log.d("SuggestionLoader", "data size " + data.size());
        }
        return data;
    }
}
