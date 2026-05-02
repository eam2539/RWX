package com.corrodinggames.rts.game.map;

import java.util.ArrayList;
import java.util.Properties;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/i.class */
public class MapObjectLayer {

    /* JADX INFO: renamed from: a */
    public int layerIndex;

    /* JADX INFO: renamed from: b */
    public String name;

    /* JADX INFO: renamed from: c */
    public ArrayList<MapObject> mapObjects;

    /* JADX INFO: renamed from: d */
    public int widthTiles;

    /* JADX INFO: renamed from: e */
    public int heightTiles;

    /* JADX INFO: renamed from: f */
    public Properties properties;

    /* JADX INFO: renamed from: a */
    public MapObject findObjectByName(String str) {
        if (this.mapObjects != null) {
            for (MapObject mapObject : this.mapObjects) {
                if (str.equalsIgnoreCase(mapObject.name)) {
                    return mapObject;
                }
            }
            return null;
        }
        return null;
    }

    public MapObjectLayer(Element element, TileMap tileMap) throws MapLoadException {
        NodeList elementsByTagName;
        this.name = element.getAttribute("name");
        if (element.hasAttribute("width")) {
            this.widthTiles = Integer.parseInt(element.getAttribute("width"));
        }
        if (element.hasAttribute("height")) {
            this.heightTiles = Integer.parseInt(element.getAttribute("height"));
        }
        this.mapObjects = new ArrayList();
        Element element2 = (Element) element.getElementsByTagName("properties").item(0);
        if (element2 != null && (elementsByTagName = element2.getElementsByTagName("property")) != null) {
            this.properties = new Properties();
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Element element3 = (Element) elementsByTagName.item(i);
                this.properties.setProperty(element3.getAttribute("name"), element3.getAttribute("value"));
            }
        }
        NodeList elementsByTagName2 = element.getElementsByTagName("object");
        for (int i2 = 0; i2 < elementsByTagName2.getLength(); i2++) {
            MapObject mapObject = new MapObject((Element) elementsByTagName2.item(i2), tileMap, this);
            mapObject.objectIndex = i2;
            this.mapObjects.add(mapObject);
        }
    }
}
