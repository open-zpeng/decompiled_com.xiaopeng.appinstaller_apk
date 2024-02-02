package android.support.v7.app;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.view.WindowCallbackWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.DecorToolbar;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import java.util.ArrayList;
import xiaopeng.widget.R;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ToolbarActionBar extends ActionBar {
    DecorToolbar mDecorToolbar;
    private boolean mLastMenuVisibility;
    private boolean mMenuCallbackSet;
    boolean mToolbarMenuPrepared;
    Window.Callback mWindowCallback;
    private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList<>();
    private final Runnable mMenuInvalidator = new Runnable() { // from class: android.support.v7.app.ToolbarActionBar.1
        @Override // java.lang.Runnable
        public void run() {
            ToolbarActionBar.this.populateOptionsMenu();
        }
    };
    private final Toolbar.OnMenuItemClickListener mMenuClicker = new Toolbar.OnMenuItemClickListener() { // from class: android.support.v7.app.ToolbarActionBar.2
        @Override // android.support.v7.widget.Toolbar.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            return ToolbarActionBar.this.mWindowCallback.onMenuItemSelected(0, item);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToolbarActionBar(Toolbar toolbar, CharSequence title, Window.Callback windowCallback) {
        this.mDecorToolbar = new ToolbarWidgetWrapper(toolbar, false);
        this.mWindowCallback = new ToolbarCallbackWrapper(windowCallback);
        this.mDecorToolbar.setWindowCallback(this.mWindowCallback);
        toolbar.setOnMenuItemClickListener(this.mMenuClicker);
        this.mDecorToolbar.setWindowTitle(title);
    }

    public Window.Callback getWrappedWindowCallback() {
        return this.mWindowCallback;
    }

    @Override // android.support.v7.app.ActionBar
    public void setHomeButtonEnabled(boolean enabled) {
    }

    @Override // android.support.v7.app.ActionBar
    public void setElevation(float elevation) {
        ViewCompat.setElevation(this.mDecorToolbar.getViewGroup(), elevation);
    }

    @Override // android.support.v7.app.ActionBar
    public Context getThemedContext() {
        return this.mDecorToolbar.getContext();
    }

    @Override // android.support.v7.app.ActionBar
    public void setDefaultDisplayHomeAsUpEnabled(boolean enabled) {
    }

    @Override // android.support.v7.app.ActionBar
    public void setHomeActionContentDescription(int resId) {
        this.mDecorToolbar.setNavigationContentDescription(resId);
    }

    @Override // android.support.v7.app.ActionBar
    public void setShowHideAnimationEnabled(boolean enabled) {
    }

    @Override // android.support.v7.app.ActionBar
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override // android.support.v7.app.ActionBar
    public void setWindowTitle(CharSequence title) {
        this.mDecorToolbar.setWindowTitle(title);
    }

    @Override // android.support.v7.app.ActionBar
    public int getDisplayOptions() {
        return this.mDecorToolbar.getDisplayOptions();
    }

    @Override // android.support.v7.app.ActionBar
    public boolean openOptionsMenu() {
        return this.mDecorToolbar.showOverflowMenu();
    }

    @Override // android.support.v7.app.ActionBar
    public boolean closeOptionsMenu() {
        return this.mDecorToolbar.hideOverflowMenu();
    }

    @Override // android.support.v7.app.ActionBar
    public boolean invalidateOptionsMenu() {
        this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator);
        ViewCompat.postOnAnimation(this.mDecorToolbar.getViewGroup(), this.mMenuInvalidator);
        return true;
    }

    @Override // android.support.v7.app.ActionBar
    public boolean collapseActionView() {
        if (this.mDecorToolbar.hasExpandedActionView()) {
            this.mDecorToolbar.collapseActionView();
            return true;
        }
        return false;
    }

    void populateOptionsMenu() {
        Menu menu = getMenu();
        MenuBuilder mb = menu instanceof MenuBuilder ? (MenuBuilder) menu : null;
        if (mb != null) {
            mb.stopDispatchingItemsChanged();
        }
        try {
            menu.clear();
            if (!this.mWindowCallback.onCreatePanelMenu(0, menu) || !this.mWindowCallback.onPreparePanel(0, null, menu)) {
                menu.clear();
            }
        } finally {
            if (mb != null) {
                mb.startDispatchingItemsChanged();
            }
        }
    }

    @Override // android.support.v7.app.ActionBar
    public boolean onMenuKeyEvent(KeyEvent event) {
        if (event.getAction() == 1) {
            openOptionsMenu();
        }
        return true;
    }

    @Override // android.support.v7.app.ActionBar
    public boolean onKeyShortcut(int keyCode, KeyEvent ev) {
        Menu menu = getMenu();
        if (menu == null) {
            return false;
        }
        KeyCharacterMap kmap = KeyCharacterMap.load(ev != null ? ev.getDeviceId() : -1);
        menu.setQwertyMode(kmap.getKeyboardType() != 1);
        return menu.performShortcut(keyCode, ev, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.ActionBar
    public void onDestroy() {
        this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator);
    }

    @Override // android.support.v7.app.ActionBar
    public void dispatchMenuVisibilityChanged(boolean isVisible) {
        if (isVisible == this.mLastMenuVisibility) {
            return;
        }
        this.mLastMenuVisibility = isVisible;
        int count = this.mMenuVisibilityListeners.size();
        for (int i = 0; i < count; i++) {
            this.mMenuVisibilityListeners.get(i).onMenuVisibilityChanged(isVisible);
        }
    }

    /* loaded from: classes.dex */
    private class ToolbarCallbackWrapper extends WindowCallbackWrapper {
        public ToolbarCallbackWrapper(Window.Callback wrapped) {
            super(wrapped);
        }

        @Override // android.support.v7.view.WindowCallbackWrapper, android.view.Window.Callback
        public boolean onPreparePanel(int featureId, View view, Menu menu) {
            boolean result = super.onPreparePanel(featureId, view, menu);
            if (result && !ToolbarActionBar.this.mToolbarMenuPrepared) {
                ToolbarActionBar.this.mDecorToolbar.setMenuPrepared();
                ToolbarActionBar.this.mToolbarMenuPrepared = true;
            }
            return result;
        }

        @Override // android.support.v7.view.WindowCallbackWrapper, android.view.Window.Callback
        public View onCreatePanelView(int featureId) {
            if (featureId == 0) {
                return new View(ToolbarActionBar.this.mDecorToolbar.getContext());
            }
            return super.onCreatePanelView(featureId);
        }
    }

    private Menu getMenu() {
        if (!this.mMenuCallbackSet) {
            this.mDecorToolbar.setMenuCallbacks(new ActionMenuPresenterCallback(), new MenuBuilderCallback());
            this.mMenuCallbackSet = true;
        }
        return this.mDecorToolbar.getMenu();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        private boolean mClosingActionMenu;

        ActionMenuPresenterCallback() {
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            if (ToolbarActionBar.this.mWindowCallback != null) {
                ToolbarActionBar.this.mWindowCallback.onMenuOpened(R.styleable.AppCompatTheme_tooltipForegroundColor, subMenu);
                return true;
            }
            return false;
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            if (this.mClosingActionMenu) {
                return;
            }
            this.mClosingActionMenu = true;
            ToolbarActionBar.this.mDecorToolbar.dismissPopupMenus();
            if (ToolbarActionBar.this.mWindowCallback != null) {
                ToolbarActionBar.this.mWindowCallback.onPanelClosed(R.styleable.AppCompatTheme_tooltipForegroundColor, menu);
            }
            this.mClosingActionMenu = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class MenuBuilderCallback implements MenuBuilder.Callback {
        MenuBuilderCallback() {
        }

        @Override // android.support.v7.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return false;
        }

        @Override // android.support.v7.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menu) {
            if (ToolbarActionBar.this.mWindowCallback != null) {
                if (ToolbarActionBar.this.mDecorToolbar.isOverflowMenuShowing()) {
                    ToolbarActionBar.this.mWindowCallback.onPanelClosed(R.styleable.AppCompatTheme_tooltipForegroundColor, menu);
                } else if (ToolbarActionBar.this.mWindowCallback.onPreparePanel(0, null, menu)) {
                    ToolbarActionBar.this.mWindowCallback.onMenuOpened(R.styleable.AppCompatTheme_tooltipForegroundColor, menu);
                }
            }
        }
    }
}
