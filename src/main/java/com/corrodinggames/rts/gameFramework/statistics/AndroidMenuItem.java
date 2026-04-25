package com.corrodinggames.rts.gameFramework.statistics;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/w.class */
public class AndroidMenuItem implements MenuItem {
    CharSequence a;
    Drawable b;
    int c;

    @Override // android.view.MenuItem
    public MenuItem setTitle(CharSequence charSequence) {
        this.a = charSequence;
        return this;
    }

    @Override // android.view.MenuItem
    public CharSequence getTitle() {
        return this.a;
    }

    @Override // android.view.MenuItem
    public MenuItem setIcon(Drawable drawable) {
        this.b = drawable;
        return this;
    }

    @Override // android.view.MenuItem
    public MenuItem setIcon(int i) {
        return this;
    }

    @Override // android.view.MenuItem
    public Drawable getIcon() {
        return this.b;
    }

    @Override // android.view.MenuItem
    public int getItemId() {
        return this.c;
    }

    public MenuItem a(int i) {
        this.c = i;
        return this;
    }

    @Override // android.view.MenuItem
    public boolean collapseActionView() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public boolean expandActionView() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public ActionProvider getActionProvider() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public View getActionView() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public char getAlphabeticShortcut() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public int getGroupId() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public Intent getIntent() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public char getNumericShortcut() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public int getOrder() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public SubMenu getSubMenu() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public CharSequence getTitleCondensed() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public boolean hasSubMenu() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public boolean isActionViewExpanded() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public boolean isCheckable() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public boolean isChecked() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public boolean isEnabled() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public boolean isVisible() {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setActionProvider(ActionProvider actionProvider) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setActionView(View view) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setActionView(int i) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setAlphabeticShortcut(char c) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setCheckable(boolean z) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setChecked(boolean z) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setEnabled(boolean z) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setIntent(Intent intent) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setNumericShortcut(char c) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setShortcut(char c, char c2) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public void setShowAsAction(int i) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setShowAsActionFlags(int i) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setTitle(int i) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setTitleCondensed(CharSequence charSequence) {
        throw new RuntimeException("not Implemented");
    }

    @Override // android.view.MenuItem
    public MenuItem setVisible(boolean z) {
        throw new RuntimeException("not Implemented");
    }
}
