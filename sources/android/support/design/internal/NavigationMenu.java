package android.support.design.internal;

import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.SubMenuBuilder;
import android.view.SubMenu;
/* loaded from: classes.dex */
public class NavigationMenu extends MenuBuilder {
    @Override // android.support.v7.view.menu.MenuBuilder, android.view.Menu
    public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
        MenuItemImpl item = (MenuItemImpl) addInternal(group, id, categoryOrder, title);
        SubMenuBuilder subMenu = new NavigationSubMenu(getContext(), this, item);
        item.setSubMenu(subMenu);
        return subMenu;
    }
}
