package android.support.v7.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.PorterDuff;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuItemWrapperICS;
import android.support.v7.widget.DrawableUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SupportMenuInflater extends MenuInflater {
    final Object[] mActionProviderConstructorArguments;
    final Object[] mActionViewConstructorArguments;
    Context mContext;
    private Object mRealOwner;
    static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE = {Context.class};
    static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;

    public SupportMenuInflater(Context context) {
        super(context);
        this.mContext = context;
        this.mActionViewConstructorArguments = new Object[]{context};
        this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
    }

    @Override // android.view.MenuInflater
    public void inflate(int menuRes, Menu menu) {
        if (!(menu instanceof SupportMenu)) {
            super.inflate(menuRes, menu);
            return;
        }
        XmlResourceParser parser = null;
        try {
            try {
                try {
                    parser = this.mContext.getResources().getLayout(menuRes);
                    AttributeSet attrs = Xml.asAttributeSet(parser);
                    parseMenu(parser, attrs, menu);
                } catch (XmlPullParserException e) {
                    throw new InflateException("Error inflating menu XML", e);
                }
            } catch (IOException e2) {
                throw new InflateException("Error inflating menu XML", e2);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x003d, code lost:
        r4 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x003e, code lost:
        if (r4 != false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0040, code lost:
        switch(r1) {
            case 1: goto L55;
            case 2: goto L39;
            case 3: goto L10;
            default: goto L59;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0045, code lost:
        r5 = r9.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0049, code lost:
        if (r2 == false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x004f, code lost:
        if (r5.equals(r3) == false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0051, code lost:
        r2 = false;
        r3 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x005b, code lost:
        if (r5.equals("group") == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x005d, code lost:
        r0.resetGroup();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0068, code lost:
        if (r5.equals("item") == false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x006e, code lost:
        if (r0.hasAddedItem() != false) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0072, code lost:
        if (r0.itemActionProvider == null) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x007a, code lost:
        if (r0.itemActionProvider.hasSubMenu() == false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x007c, code lost:
        r0.addSubMenuItem();
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0080, code lost:
        r0.addItem();
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x008a, code lost:
        if (r5.equals("menu") == false) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x008c, code lost:
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x008e, code lost:
        if (r2 == false) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0091, code lost:
        r5 = r9.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x009b, code lost:
        if (r5.equals("group") == false) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x009d, code lost:
        r0.readGroup(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00a7, code lost:
        if (r5.equals("item") == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00a9, code lost:
        r0.readItem(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00b3, code lost:
        if (r5.equals("menu") == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00b5, code lost:
        r6 = r0.addSubMenuItem();
        parseMenu(r9, r10, r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00bd, code lost:
        r2 = true;
        r3 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00c7, code lost:
        throw new java.lang.RuntimeException("Unexpected end of document");
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00c8, code lost:
        r1 = r9.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00ce, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void parseMenu(org.xmlpull.v1.XmlPullParser r9, android.util.AttributeSet r10, android.view.Menu r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r8 = this;
            android.support.v7.view.SupportMenuInflater$MenuState r0 = new android.support.v7.view.SupportMenuInflater$MenuState
            r0.<init>(r11)
            int r1 = r9.getEventType()
            r2 = 0
            r3 = 0
        Lb:
            r4 = 2
            if (r1 != r4) goto L36
            java.lang.String r4 = r9.getName()
            java.lang.String r5 = "menu"
            boolean r5 = r4.equals(r5)
            if (r5 == 0) goto L1f
            int r1 = r9.next()
            goto L3d
        L1f:
            java.lang.RuntimeException r5 = new java.lang.RuntimeException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Expecting menu, got "
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        L36:
            int r1 = r9.next()
            r4 = 1
            if (r1 != r4) goto Lb
        L3d:
            r4 = 0
        L3e:
            if (r4 != 0) goto Lce
            switch(r1) {
                case 1: goto Lc0;
                case 2: goto L8e;
                case 3: goto L45;
                default: goto L43;
            }
        L43:
            goto Lc8
        L45:
            java.lang.String r5 = r9.getName()
            if (r2 == 0) goto L55
            boolean r6 = r5.equals(r3)
            if (r6 == 0) goto L55
            r2 = 0
            r3 = 0
            goto Lc8
        L55:
            java.lang.String r6 = "group"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto L62
            r0.resetGroup()
            goto Lc8
        L62:
            java.lang.String r6 = "item"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto L84
            boolean r6 = r0.hasAddedItem()
            if (r6 != 0) goto Lc8
            android.support.v4.view.ActionProvider r6 = r0.itemActionProvider
            if (r6 == 0) goto L80
            android.support.v4.view.ActionProvider r6 = r0.itemActionProvider
            boolean r6 = r6.hasSubMenu()
            if (r6 == 0) goto L80
            r0.addSubMenuItem()
            goto Lc8
        L80:
            r0.addItem()
            goto Lc8
        L84:
            java.lang.String r6 = "menu"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto Lc8
            r4 = 1
            goto Lc8
        L8e:
            if (r2 == 0) goto L91
            goto Lc8
        L91:
            java.lang.String r5 = r9.getName()
            java.lang.String r6 = "group"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto La1
            r0.readGroup(r10)
            goto Lc8
        La1:
            java.lang.String r6 = "item"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto Lad
            r0.readItem(r10)
            goto Lc8
        Lad:
            java.lang.String r6 = "menu"
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto Lbd
            android.view.SubMenu r6 = r0.addSubMenuItem()
            r8.parseMenu(r9, r10, r6)
            goto Lc8
        Lbd:
            r2 = 1
            r3 = r5
            goto Lc8
        Lc0:
            java.lang.RuntimeException r5 = new java.lang.RuntimeException
            java.lang.String r6 = "Unexpected end of document"
            r5.<init>(r6)
            throw r5
        Lc8:
            int r1 = r9.next()
            goto L3e
        Lce:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.view.SupportMenuInflater.parseMenu(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.Menu):void");
    }

    Object getRealOwner() {
        if (this.mRealOwner == null) {
            this.mRealOwner = findRealOwner(this.mContext);
        }
        return this.mRealOwner;
    }

    private Object findRealOwner(Object owner) {
        if (owner instanceof Activity) {
            return owner;
        }
        if (owner instanceof ContextWrapper) {
            return findRealOwner(((ContextWrapper) owner).getBaseContext());
        }
        return owner;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InflatedOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        private static final Class<?>[] PARAM_TYPES = {MenuItem.class};
        private Method mMethod;
        private Object mRealOwner;

        public InflatedOnMenuItemClickListener(Object realOwner, String methodName) {
            this.mRealOwner = realOwner;
            Class<?> c = realOwner.getClass();
            try {
                this.mMethod = c.getMethod(methodName, PARAM_TYPES);
            } catch (Exception e) {
                InflateException ex = new InflateException("Couldn't resolve menu item onClick handler " + methodName + " in class " + c.getName());
                ex.initCause(e);
                throw ex;
            }
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            try {
                if (this.mMethod.getReturnType() == Boolean.TYPE) {
                    return ((Boolean) this.mMethod.invoke(this.mRealOwner, item)).booleanValue();
                }
                this.mMethod.invoke(this.mRealOwner, item);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MenuState {
        private int groupCategory;
        private int groupCheckable;
        private boolean groupEnabled;
        private int groupId;
        private int groupOrder;
        private boolean groupVisible;
        ActionProvider itemActionProvider;
        private String itemActionProviderClassName;
        private String itemActionViewClassName;
        private int itemActionViewLayout;
        private boolean itemAdded;
        private int itemAlphabeticModifiers;
        private char itemAlphabeticShortcut;
        private int itemCategoryOrder;
        private int itemCheckable;
        private boolean itemChecked;
        private CharSequence itemContentDescription;
        private boolean itemEnabled;
        private int itemIconResId;
        private ColorStateList itemIconTintList = null;
        private PorterDuff.Mode itemIconTintMode = null;
        private int itemId;
        private String itemListenerMethodName;
        private int itemNumericModifiers;
        private char itemNumericShortcut;
        private int itemShowAsAction;
        private CharSequence itemTitle;
        private CharSequence itemTitleCondensed;
        private CharSequence itemTooltipText;
        private boolean itemVisible;
        private Menu menu;

        public MenuState(Menu menu) {
            this.menu = menu;
            resetGroup();
        }

        public void resetGroup() {
            this.groupId = 0;
            this.groupCategory = 0;
            this.groupOrder = 0;
            this.groupCheckable = 0;
            this.groupVisible = true;
            this.groupEnabled = true;
        }

        public void readGroup(AttributeSet attrs) {
            TypedArray a2 = SupportMenuInflater.this.mContext.obtainStyledAttributes(attrs, R.styleable.MenuGroup);
            this.groupId = a2.getResourceId(R.styleable.MenuGroup_android_id, 0);
            this.groupCategory = a2.getInt(R.styleable.MenuGroup_android_menuCategory, 0);
            this.groupOrder = a2.getInt(R.styleable.MenuGroup_android_orderInCategory, 0);
            this.groupCheckable = a2.getInt(R.styleable.MenuGroup_android_checkableBehavior, 0);
            this.groupVisible = a2.getBoolean(R.styleable.MenuGroup_android_visible, true);
            this.groupEnabled = a2.getBoolean(R.styleable.MenuGroup_android_enabled, true);
            a2.recycle();
        }

        public void readItem(AttributeSet attrs) {
            TypedArray a2 = SupportMenuInflater.this.mContext.obtainStyledAttributes(attrs, R.styleable.MenuItem);
            this.itemId = a2.getResourceId(R.styleable.MenuItem_android_id, 0);
            int category = a2.getInt(R.styleable.MenuItem_android_menuCategory, this.groupCategory);
            int order = a2.getInt(R.styleable.MenuItem_android_orderInCategory, this.groupOrder);
            this.itemCategoryOrder = ((-65536) & category) | (65535 & order);
            this.itemTitle = a2.getText(R.styleable.MenuItem_android_title);
            this.itemTitleCondensed = a2.getText(R.styleable.MenuItem_android_titleCondensed);
            this.itemIconResId = a2.getResourceId(R.styleable.MenuItem_android_icon, 0);
            this.itemAlphabeticShortcut = getShortcut(a2.getString(R.styleable.MenuItem_android_alphabeticShortcut));
            this.itemAlphabeticModifiers = a2.getInt(R.styleable.MenuItem_alphabeticModifiers, 4096);
            this.itemNumericShortcut = getShortcut(a2.getString(R.styleable.MenuItem_android_numericShortcut));
            this.itemNumericModifiers = a2.getInt(R.styleable.MenuItem_numericModifiers, 4096);
            if (a2.hasValue(R.styleable.MenuItem_android_checkable)) {
                this.itemCheckable = a2.getBoolean(R.styleable.MenuItem_android_checkable, false) ? 1 : 0;
            } else {
                this.itemCheckable = this.groupCheckable;
            }
            this.itemChecked = a2.getBoolean(R.styleable.MenuItem_android_checked, false);
            this.itemVisible = a2.getBoolean(R.styleable.MenuItem_android_visible, this.groupVisible);
            this.itemEnabled = a2.getBoolean(R.styleable.MenuItem_android_enabled, this.groupEnabled);
            this.itemShowAsAction = a2.getInt(R.styleable.MenuItem_showAsAction, -1);
            this.itemListenerMethodName = a2.getString(R.styleable.MenuItem_android_onClick);
            this.itemActionViewLayout = a2.getResourceId(R.styleable.MenuItem_actionLayout, 0);
            this.itemActionViewClassName = a2.getString(R.styleable.MenuItem_actionViewClass);
            this.itemActionProviderClassName = a2.getString(R.styleable.MenuItem_actionProviderClass);
            boolean hasActionProvider = this.itemActionProviderClassName != null;
            if (hasActionProvider && this.itemActionViewLayout == 0 && this.itemActionViewClassName == null) {
                this.itemActionProvider = (ActionProvider) newInstance(this.itemActionProviderClassName, SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionProviderConstructorArguments);
            } else {
                if (hasActionProvider) {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");
                }
                this.itemActionProvider = null;
            }
            this.itemContentDescription = a2.getText(R.styleable.MenuItem_contentDescription);
            this.itemTooltipText = a2.getText(R.styleable.MenuItem_tooltipText);
            if (a2.hasValue(R.styleable.MenuItem_iconTintMode)) {
                this.itemIconTintMode = DrawableUtils.parseTintMode(a2.getInt(R.styleable.MenuItem_iconTintMode, -1), this.itemIconTintMode);
            } else {
                this.itemIconTintMode = null;
            }
            if (a2.hasValue(R.styleable.MenuItem_iconTint)) {
                this.itemIconTintList = a2.getColorStateList(R.styleable.MenuItem_iconTint);
            } else {
                this.itemIconTintList = null;
            }
            a2.recycle();
            this.itemAdded = false;
        }

        private char getShortcut(String shortcutString) {
            if (shortcutString == null) {
                return (char) 0;
            }
            return shortcutString.charAt(0);
        }

        private void setItem(MenuItem item) {
            boolean z;
            MenuItem enabled = item.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled);
            if (this.itemCheckable < 1) {
                z = false;
            } else {
                z = true;
            }
            enabled.setCheckable(z).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId);
            if (this.itemShowAsAction >= 0) {
                item.setShowAsAction(this.itemShowAsAction);
            }
            if (this.itemListenerMethodName != null) {
                if (SupportMenuInflater.this.mContext.isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
                item.setOnMenuItemClickListener(new InflatedOnMenuItemClickListener(SupportMenuInflater.this.getRealOwner(), this.itemListenerMethodName));
            }
            if (item instanceof MenuItemImpl) {
                MenuItemImpl menuItemImpl = (MenuItemImpl) item;
            }
            if (this.itemCheckable >= 2) {
                if (item instanceof MenuItemImpl) {
                    ((MenuItemImpl) item).setExclusiveCheckable(true);
                } else if (item instanceof MenuItemWrapperICS) {
                    ((MenuItemWrapperICS) item).setExclusiveCheckable(true);
                }
            }
            boolean actionViewSpecified = false;
            if (this.itemActionViewClassName != null) {
                View actionView = (View) newInstance(this.itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionViewConstructorArguments);
                item.setActionView(actionView);
                actionViewSpecified = true;
            }
            if (this.itemActionViewLayout > 0) {
                if (!actionViewSpecified) {
                    item.setActionView(this.itemActionViewLayout);
                } else {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                }
            }
            if (this.itemActionProvider != null) {
                MenuItemCompat.setActionProvider(item, this.itemActionProvider);
            }
            MenuItemCompat.setContentDescription(item, this.itemContentDescription);
            MenuItemCompat.setTooltipText(item, this.itemTooltipText);
            MenuItemCompat.setAlphabeticShortcut(item, this.itemAlphabeticShortcut, this.itemAlphabeticModifiers);
            MenuItemCompat.setNumericShortcut(item, this.itemNumericShortcut, this.itemNumericModifiers);
            if (this.itemIconTintMode != null) {
                MenuItemCompat.setIconTintMode(item, this.itemIconTintMode);
            }
            if (this.itemIconTintList != null) {
                MenuItemCompat.setIconTintList(item, this.itemIconTintList);
            }
        }

        public void addItem() {
            this.itemAdded = true;
            setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
        }

        public SubMenu addSubMenuItem() {
            this.itemAdded = true;
            SubMenu subMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
            setItem(subMenu.getItem());
            return subMenu;
        }

        public boolean hasAddedItem() {
            return this.itemAdded;
        }

        private <T> T newInstance(String className, Class<?>[] constructorSignature, Object[] arguments) {
            try {
                Class<?> clazz = SupportMenuInflater.this.mContext.getClassLoader().loadClass(className);
                Constructor<?> constructor = clazz.getConstructor(constructorSignature);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(arguments);
            } catch (Exception e) {
                Log.w("SupportMenuInflater", "Cannot instantiate class: " + className, e);
                return null;
            }
        }
    }
}
