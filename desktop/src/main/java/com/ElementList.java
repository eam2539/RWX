package com;

import java.util.ArrayList;

/* JADX INFO: loaded from: game-lib.jar:com/ElementList.class */
public class ElementList {
    ArrayList elements = new ArrayList();

    public Element getFirst() {
        if (this.elements.size() > 0) {
            return (Element) this.elements.get(0);
        }
        return null;
    }
}
