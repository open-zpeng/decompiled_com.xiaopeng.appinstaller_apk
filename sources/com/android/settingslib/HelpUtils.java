package com.android.settingslib;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.android.internal.logging.MetricsLogger;
import java.net.URISyntaxException;
import java.util.Locale;
/* loaded from: classes.dex */
public class HelpUtils {
    private static final String TAG = HelpUtils.class.getSimpleName();
    private static String sCachedVersionCode = null;

    private HelpUtils() {
    }

    public static boolean prepareHelpMenuItem(Activity activity, Menu menu, int helpUriResource, String backupContext) {
        MenuItem helpItem = menu.add(0, xiaopeng.widget.R.styleable.AppCompatTheme_textAppearanceSearchResultSubtitle, 0, R.string.help_feedback_label);
        helpItem.setIcon(R.drawable.ic_help_actionbar);
        return prepareHelpMenuItem(activity, helpItem, activity.getString(helpUriResource), backupContext);
    }

    public static boolean prepareHelpMenuItem(final Activity activity, MenuItem helpMenuItem, String helpUriString, String backupContext) {
        if (Settings.Global.getInt(activity.getContentResolver(), "device_provisioned", 0) == 0) {
            return false;
        }
        if (TextUtils.isEmpty(helpUriString)) {
            helpMenuItem.setVisible(false);
            return false;
        }
        final Intent intent = getHelpIntent(activity, helpUriString, backupContext);
        if (intent != null) {
            helpMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.android.settingslib.HelpUtils.1
                @Override // android.view.MenuItem.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem item) {
                    MetricsLogger.action(activity, 513, intent.getStringExtra("EXTRA_CONTEXT"));
                    try {
                        activity.startActivityForResult(intent, 0);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        String str = HelpUtils.TAG;
                        Log.e(str, "No activity found for intent: " + intent);
                        return true;
                    }
                }
            });
            helpMenuItem.setShowAsAction(2);
            helpMenuItem.setVisible(true);
            return true;
        }
        helpMenuItem.setVisible(false);
        return false;
    }

    public static Intent getHelpIntent(Context context, String helpUriString, String backupContext) {
        if (Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) == 0) {
            return null;
        }
        try {
            Intent intent = Intent.parseUri(helpUriString, 3);
            addIntentParameters(context, intent, backupContext, true);
            ComponentName component = intent.resolveActivity(context.getPackageManager());
            if (component != null) {
                return intent;
            }
            if (intent.hasExtra("EXTRA_BACKUP_URI")) {
                return getHelpIntent(context, intent.getStringExtra("EXTRA_BACKUP_URI"), backupContext);
            }
            return null;
        } catch (URISyntaxException e) {
            Uri fullUri = uriWithAddedParameters(context, Uri.parse(helpUriString));
            Intent intent2 = new Intent("android.intent.action.VIEW", fullUri);
            intent2.setFlags(276824064);
            return intent2;
        }
    }

    public static void addIntentParameters(Context context, Intent intent, String backupContext, boolean sendPackageName) {
        if (!intent.hasExtra("EXTRA_CONTEXT")) {
            intent.putExtra("EXTRA_CONTEXT", backupContext);
        }
        Resources resources = context.getResources();
        boolean includePackageName = resources.getBoolean(17957020);
        if (sendPackageName && includePackageName) {
            String[] packageNameKey = {resources.getString(17039690)};
            String[] packageNameValue = {resources.getString(17039691)};
            String helpIntentExtraKey = resources.getString(17039688);
            String helpIntentNameKey = resources.getString(17039689);
            String feedbackIntentExtraKey = resources.getString(17039677);
            String feedbackIntentNameKey = resources.getString(17039678);
            intent.putExtra(helpIntentExtraKey, packageNameKey);
            intent.putExtra(helpIntentNameKey, packageNameValue);
            intent.putExtra(feedbackIntentExtraKey, packageNameKey);
            intent.putExtra(feedbackIntentNameKey, packageNameValue);
        }
        intent.putExtra("EXTRA_THEME", 0);
        TypedArray array = context.obtainStyledAttributes(new int[]{16843827});
        intent.putExtra("EXTRA_PRIMARY_COLOR", array.getColor(0, 0));
        array.recycle();
    }

    private static Uri uriWithAddedParameters(Context context, Uri baseUri) {
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter("hl", Locale.getDefault().toString());
        if (sCachedVersionCode == null) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                sCachedVersionCode = Long.toString(info.getLongVersionCode());
                builder.appendQueryParameter("version", sCachedVersionCode);
            } catch (PackageManager.NameNotFoundException e) {
                Log.wtf(TAG, "Invalid package name for context", e);
            }
        } else {
            builder.appendQueryParameter("version", sCachedVersionCode);
        }
        return builder.build();
    }
}
