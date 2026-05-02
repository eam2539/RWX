package com;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/* JADX INFO: loaded from: game-lib.jar:com/Element.class */
public class Element {
    private long nativeHandle;
    boolean loadedChinese = false;
    HashSet charsetsLoaded;

    public native Element getElementById(String str);

    public native String getTagName();

    public native boolean focus();

    public native void blur();

    public native void click();

    public native void addReference();

    public native void removeReference();

    public native String getAttribute(String str, String str2);

    public native void setAttribute(String str, String str2);

    public native String getAttributeKey(int i);

    public native String getAttributeValue(int i);

    public native int getNumAttributes();

    public native Element getChild(int i);

    public native int getNumChildren();

    public native String getInnerRML();

    public native void setInnerRML(String str);

    public native void setClassNames(String str);

    public native String getClassNames();

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public native Element m29clone();

    public native void appendChild(Element element);

    public native void insertBefore(Element element, Element element2);

    public native void removeChild(Element element);

    public native String getProperty(String str, String str2);

    public native void setProperty(String str, String str2);

    public native boolean isPseudoClassSet(String str);

    public native float getAbsoluteLeft();

    public native float getAbsoluteTop();

    public native float getOffsetLeft();

    public native float getOffsetTop();

    public native float getOffsetWidth();

    public native float getOffsetHeight();

    public native float getScrollTop();

    public native void setScrollTop(float f);

    public native void scrollIntoView(boolean z);

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Element) && ((Element) obj).nativeHandle == this.nativeHandle) {
            return true;
        }
        return false;
    }

    public String getAttribute(String str) {
        return getAttribute(str, null);
    }

    public boolean getAttributeBoolean(String str, boolean z) {
        String attribute = getAttribute(str, null);
        if (attribute == null) {
            return z;
        }
        if (attribute.equalsIgnoreCase("true")) {
            return true;
        }
        if (attribute.equalsIgnoreCase("false")) {
            return false;
        }
        return z;
    }

    public String getId() {
        return getAttribute("id");
    }

    public void addStyle(String str) {
        String attribute = getAttribute("style");
        if (attribute == null) {
            attribute = VariableScope.nullOrMissingString;
        }
        setAttribute("style", attribute + str);
    }

    public void setStyle(String str) {
        setAttribute("style", str);
    }

    public void setValue(String str) {
        setAttribute("value", str);
    }

    public void setCheckbox(boolean z) {
        if (z) {
            setAttribute("checked", VariableScope.nullOrMissingString);
        } else {
            setAttribute("checked", null);
        }
    }

    public boolean getCheckbox() {
        String attribute = getAttribute("checked");
        if (attribute == null || "false".equals(attribute)) {
            return false;
        }
        return true;
    }

    public String getValue() {
        return getAttribute("value");
    }

    public Integer getValueAsInt(Integer num) {
        String value = getValue();
        if (value == null || value.equals(VariableScope.nullOrMissingString) || value.equals("null")) {
            return num;
        }
        return Integer.valueOf(Integer.parseInt(value));
    }

    public Float getValueAsFloat(Float f) {
        String value = getValue();
        if (value == null || value.equals(VariableScope.nullOrMissingString)) {
            return f;
        }
        return Float.valueOf(Float.parseFloat(value.replace(",", ".")));
    }

    public Boolean getValueAsBoolean(Boolean bool) {
        String value = getValue();
        if (value == null || value.equals(VariableScope.nullOrMissingString)) {
            return bool;
        }
        return Boolean.valueOf(Boolean.parseBoolean(value));
    }

    public static String excapeHTML(String str) {
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("${", "$ {");
    }

    public void setTextNoCharset(String str, boolean z) {
        if (str == null) {
            str = VariableScope.nullOrMissingString;
        }
        String strExcapeHTML = excapeHTML(str);
        if (z && strExcapeHTML.contains("\n")) {
            strExcapeHTML = strExcapeHTML.replaceAll("\n", "<br/>\n");
        }
        setInnerRML(strExcapeHTML);
    }

    public boolean loadCharsetIfNeededWithCurrentText() {
        return loadCharsetIfNeeded(getInnerRML());
    }

    public boolean loadCharsetIfNeeded(String str) {
        String str2 = VariableScope.nullOrMissingString;
        int length = str.length();
        int iCharCount = 0;
        while (true) {
            int i = iCharCount;
            if (i >= length) {
                break;
            }
            int iCodePointAt = str.codePointAt(i);
            if (iCodePointAt > 128) {
                boolean z = false;
                if (iCodePointAt >= 196 && iCodePointAt <= 252) {
                    z = true;
                }
                if (!z) {
                    if (this.charsetsLoaded == null) {
                        this.charsetsLoaded = new HashSet();
                    }
                    if (!this.charsetsLoaded.contains(Integer.valueOf(iCodePointAt))) {
                        this.charsetsLoaded.add(Integer.valueOf(iCodePointAt));
                        if (!str2.equals(VariableScope.nullOrMissingString)) {
                            str2 = str2 + ",";
                        }
                        str2 = str2 + keycodeToHex(iCodePointAt);
                    }
                }
            }
            iCharCount = i + Character.charCount(iCodePointAt);
        }
        if (!str2.equals(VariableScope.nullOrMissingString)) {
            compareAndAddClass("needsUnicodeFont");
            return true;
        }
        return false;
    }

    public static final String keycodeToHex(int i) {
        return String.format("U+%04X", Integer.valueOf(i));
    }

    public void compareAndSetText(String str) {
        if (str == null) {
            str = VariableScope.nullOrMissingString;
        }
        if (!str.equals(getInnerRML())) {
            setText(str);
        }
    }

    public void setText(String str) {
        loadCharsetIfNeeded(str);
        setTextNoCharset(str, false);
    }

    public void setTextWithNewlines(String str) {
        loadCharsetIfNeeded(str);
        setTextNoCharset(str, true);
    }

    public void addClass(String str) {
        setClassNames(getClassNames() + " " + str);
    }

    public void compareAndAddClass(String str) {
        if (!hasClassName(str)) {
            addClass(str);
        }
    }

    public void removeClass(String str) {
        String classNames = getClassNames();
        String strReplaceAll = classNames.replaceAll("\\b" + str + "\\b", VariableScope.nullOrMissingString);
        if (!classNames.equals(strReplaceAll)) {
            setClassNames(strReplaceAll);
        }
    }

    public void compareAndSetClassNames(String str) {
        if (!str.equals(getClassNames())) {
            setClassNames(str);
        }
    }

    public Element cloneAndFix() {
        Element elementM29clone = m29clone();
        elementM29clone.setClassNames(getClassNames());
        return elementM29clone;
    }

    public boolean isFocused() {
        return isPseudoClassSet("focus");
    }

    public boolean isHovering() {
        return isPseudoClassSet("hover");
    }

    public void prependChild(Element element) {
        if (getNumChildren() == 0) {
            appendChild(element);
        } else {
            insertBefore(element, getChild(0));
        }
    }

    public void clearAllChildren() {
        for (int numChildren = getNumChildren(); numChildren >= 0; numChildren--) {
            Element child = getChild(numChildren);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + numChildren);
            }
            if (!child.getTagName().equals("scrollbarvertical")) {
                removeChild(child);
            }
        }
    }

    public ArrayList getChildren() {
        ArrayList arrayList = new ArrayList();
        int numChildren = getNumChildren();
        for (int i = 0; i < numChildren; i++) {
            Element child = getChild(i);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + i);
            }
            arrayList.add(child);
        }
        return arrayList;
    }

    public Element getTopLevelFocusedElement() {
        int numChildren = getNumChildren();
        for (int i = 0; i < numChildren; i++) {
            Element child = getChild(i);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + i);
            }
            Element topLevelFocusedElement = child.getTopLevelFocusedElement();
            if (topLevelFocusedElement != null) {
                return topLevelFocusedElement;
            }
        }
        if (isFocused()) {
            return this;
        }
        return null;
    }

    public ArrayList getChainFromChildElement(Element element) {
        if (element.equals(this)) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this);
            return arrayList;
        }
        int numChildren = getNumChildren();
        for (int i = 0; i < numChildren; i++) {
            Element child = getChild(i);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + i);
            }
            ArrayList chainFromChildElement = child.getChainFromChildElement(element);
            if (chainFromChildElement != null) {
                chainFromChildElement.add(this);
                return chainFromChildElement;
            }
        }
        return null;
    }

    public ArrayList<Element> getAllNestedChildren() {
        ArrayList arrayList = new ArrayList();
        getAllNestedChildren(arrayList);
        return arrayList;
    }

    public void getAllNestedChildren(ArrayList arrayList) {
        int numChildren = getNumChildren();
        for (int i = 0; i < numChildren; i++) {
            Element child = getChild(i);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + i);
            }
            arrayList.add(child);
            child.getAllNestedChildren(arrayList);
        }
    }

    public boolean hasClassName(String str) {
        if ((" " + getClassNames() + " ").contains(" " + str + " ")) {
            return true;
        }
        return false;
    }

    public Element findByClassName(String str) {
        int numChildren = getNumChildren();
        for (int i = 0; i < numChildren; i++) {
            Element child = getChild(i);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + i);
            }
            if (child.hasClassName(str)) {
                return child;
            }
            Element elementFindByClassName = child.findByClassName(str);
            if (elementFindByClassName != null) {
                return elementFindByClassName;
            }
        }
        return null;
    }

    public Element findByTagName(String str) {
        int numChildren = getNumChildren();
        for (int i = 0; i < numChildren; i++) {
            Element child = getChild(i);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + i);
            }
            if (child.getTagName().equalsIgnoreCase(str)) {
                return child;
            }
            Element elementFindByTagName = child.findByTagName(str);
            if (elementFindByTagName != null) {
                return elementFindByTagName;
            }
        }
        return null;
    }

    public ArrayList<Element> findElementsByClassName(String str) {
        ArrayList arrayList = new ArrayList();
        findElementsByClassName(str, arrayList);
        return arrayList;
    }

    public void findElementsByClassName(String str, List list) {
        int numChildren = getNumChildren();
        for (int i = 0; i < numChildren; i++) {
            Element child = getChild(i);
            if (child == null) {
                throw new RuntimeException("child==null, i:" + i);
            }
            if ((" " + child.getClassNames() + " ").contains(" " + str + " ")) {
                list.add(child);
            }
            child.findElementsByClassName(str, list);
        }
    }

    public void hide() {
        show(false);
    }

    public void show() {
        show(true);
    }

    public void show(boolean z) {
        if (!z) {
            compareAndAddClass("hide");
        } else {
            removeClass("hide");
        }
    }
}
