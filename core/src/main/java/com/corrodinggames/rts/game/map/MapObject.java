package com.corrodinggames.rts.game.map;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.LanguagePart;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.EmptyArrays;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.RectF;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/a.class */
public class MapObject {

    /* JADX INFO: renamed from: a */
    public int objectIndex;

    /* JADX INFO: renamed from: b */
    public String name;

    /* JADX INFO: renamed from: c */
    public String normalizedName;

    /* JADX INFO: renamed from: d */
    public String type;

    /* JADX INFO: renamed from: e */
    public float x;

    /* JADX INFO: renamed from: f */
    public float y;

    /* JADX INFO: renamed from: g */
    public float width;

    /* JADX INFO: renamed from: h */
    public float height;

    /* JADX INFO: renamed from: i */
    public float rotation;

    /* JADX INFO: renamed from: p */
    private String imageSource;

    /* JADX INFO: renamed from: j */
    public RectF tileRect;

    /* JADX INFO: renamed from: k */
    public int globalTileId;

    /* JADX INFO: renamed from: l */
    public Tileset tileset;

    /* JADX INFO: renamed from: m */
    public int tileIndex;

    /* JADX INFO: renamed from: n */
    public Properties properties;

    /* JADX INFO: renamed from: o */
    public FastArrayList visitedProperties = new FastArrayList();

    /* JADX INFO: renamed from: a */
    static float getFloatAttribute(Element element, String str) throws MapLoadException {
        String attribute = element.getAttribute(str);
        try {
            return Float.parseFloat(attribute);
        } catch (NumberFormatException e) {
            throw new MapLoadException("Invalid map: Error reading '" + str + "' invalid float: " + attribute, e);
        }
    }

    public MapObject(Element element, TileMap tileMap, MapObjectLayer mapObjectLayer) throws MapLoadException {
        PlayerTeam playerTeamK;
        BaseUnit baseUnitA;
        NodeList elementsByTagName;
        String textContent;
        this.globalTileId = -1;
        this.tileIndex = -1;
        this.name = element.getAttribute("name");
        if (this.name != null) {
            this.normalizedName = this.name.trim().toLowerCase(Locale.ENGLISH);
        }
        this.type = element.getAttribute("type");
        this.x = Float.parseFloat(element.getAttribute("x"));
        this.y = Float.parseFloat(element.getAttribute("y"));
        if (element.hasAttribute("rotation")) {
            this.rotation = Float.parseFloat(element.getAttribute("rotation")) - 90.0f;
        }
        if (!element.getAttribute("width").equals(VariableScope.nullOrMissingString)) {
            this.width = getFloatAttribute(element, "width");
        }
        if (!element.getAttribute("height").equals(VariableScope.nullOrMissingString)) {
            this.height = getFloatAttribute(element, "height");
        }
        Element element2 = (Element) element.getElementsByTagName("image").item(0);
        if (element2 != null) {
            this.imageSource = element2.getAttribute("source");
        }
        Element element3 = (Element) element.getElementsByTagName("properties").item(0);
        if (element3 != null && (elementsByTagName = element3.getElementsByTagName("property")) != null) {
            this.properties = new Properties();
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Element element4 = (Element) elementsByTagName.item(i);
                String attribute = element4.getAttribute("name");
                if (element4.hasAttribute("value")) {
                    textContent = element4.getAttribute("value");
                } else {
                    textContent = element4.getTextContent();
                }
                this.properties.setProperty(attribute, textContent);
            }
        }
        if (element.hasAttribute("gid")) {
            this.globalTileId = Integer.parseInt(element.getAttribute("gid"));
            this.tileset = tileMap.findTilesetByGlobalTileId(this.globalTileId);
            if (this.tileset != null) {
                this.tileset.usedInMap = true;
                this.tileset.usedInNonGroundLayer = true;
                this.tileIndex = this.globalTileId - this.tileset.firstGid;
            } else {
                throw new RuntimeException("Unable to decode base 64 block, could not find tileId:" + this.globalTileId);
            }
        }
        Properties properties = this.properties;
        this.tileRect = new RectF(this.x, this.y, this.x + this.width, this.y + this.height);
        tileMap.convertWorldRectToTileRect(this.tileRect);
        this.x = this.tileRect.a;
        this.y = this.tileRect.b;
        this.width = this.tileRect.b();
        this.height = this.tileRect.c();
        float fD = this.tileRect.d();
        float fE = this.tileRect.e();
        String attribute2 = element.getAttribute("type");
        if (attribute2 != null && !attribute2.equals(VariableScope.nullOrMissingString) && !attribute2.equals("unit") && !attribute2.equals("comment") && !mapObjectLayer.name.equalsIgnoreCase("triggers")) {
            logTriggerMessage("Triggers should be on triggers layer");
        }
        if (properties != null) {
            String property = properties.getProperty("unit");
            String property2 = properties.getProperty("customUnit");
            if (property != null || property2 != null) {
                String property3 = properties.getProperty("team");
                if (property3 == null) {
                    throw new MapLoadException("Unit object team missing for:" + (property != null ? property : property2));
                }
                if ("none".equalsIgnoreCase(property3)) {
                    playerTeamK = PlayerTeam.k(-1);
                } else {
                    try {
                        playerTeamK = PlayerTeam.k(Integer.valueOf(property3).intValue());
                        if (playerTeamK == null) {
                            GameEngine.log("map", "Unit object without team:" + property + " (skipping unit)");
                            return;
                        } else if (playerTeamK.isSpectatorTeamColor()) {
                            GameEngine.log("map", "Unit team is marked as spectator:" + property + " (skipping unit)");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        throw new MapLoadException("Unit object team invalid: " + e.getMessage(), e);
                    }
                }
                if (property2 != null) {
                    CustomUnitConfig customUnitConfigFindConfigByName = CustomUnitConfig.findConfigByName(property2);
                    if (customUnitConfigFindConfigByName == null) {
                        throw new MapLoadException("Could not find custom unit of:" + property2 + " at x:" + this.x + ", y:" + this.y);
                    }
                    UnitType unitTypeC = CustomUnitConfig.c(customUnitConfigFindConfigByName);
                    if (unitTypeC != null) {
                        if (unitTypeC instanceof CustomUnitConfig) {
                            customUnitConfigFindConfigByName = (CustomUnitConfig) unitTypeC;
                        } else {
                            GameEngine.logColored("replacement not a custom unit:" + unitTypeC.getUnitTypeDescriptionShort());
                        }
                    }
                    baseUnitA = CustomUnitConfig.a(false, customUnitConfigFindConfigByName);
                    if (baseUnitA == null) {
                        throw new RuntimeException("Metadata unit is null for:" + property2);
                    }
                } else {
                    UnitType unitTypeByName = UnitTypeEnum.getUnitTypeByName(property);
                    if (unitTypeByName != null) {
                        baseUnitA = unitTypeByName.a();
                    } else {
                        throw new MapLoadException("Could not find unit type of:" + property + " at x:" + this.x + ", y:" + this.y);
                    }
                }
                baseUnitA.posX = fD;
                baseUnitA.posY = fE;
                if (!baseUnitA.bI()) {
                    baseUnitA.h(this.rotation);
                }
                if (playerTeamK != null) {
                    baseUnitA.setUnitTeam(playerTeamK);
                    if (properties.getProperty("type") != null) {
                        baseUnitA.a_(properties.getProperty("type"));
                    }
                    if (properties.getProperty("randomRotate") != null && !baseUnitA.bI()) {
                        baseUnitA.h(Utility.getDeterministicRandomInt(baseUnitA, -180, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT));
                    }
                    baseUnitA.changeTeam = "builder".equalsIgnoreCase(property) || "builder".equalsIgnoreCase(property2);
                    baseUnitA.isTargetable = "commandCenter".equalsIgnoreCase(property) || "commandCenter".equalsIgnoreCase(property2);
                    baseUnitA.isActive = true;
                    baseUnitA.n();
                    PlayerTeam.c(baseUnitA);
                    GameObject.dL();
                    return;
                }
                throw new MapLoadException("team is null:" + property);
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public boolean containsUnitPosition(BaseUnit baseUnit) {
        return this.tileRect.b((int) baseUnit.posX, (int) baseUnit.posY);
    }

    /* JADX INFO: renamed from: a */
    public void markPropertyVisited(String str) {
        if (!this.visitedProperties.contains(str)) {
            this.visitedProperties.add(str);
        }
    }

    /* JADX INFO: renamed from: a */
    public String[] getUnvisitedPropertyNames() {
        if (this.properties == null) {
            return EmptyArrays.EMPTY_STRING;
        }
        FastArrayList fastArrayList = new FastArrayList();
        Enumeration<?> enumerationPropertyNames = this.properties.propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            String str = (String) enumerationPropertyNames.nextElement();
            if (!this.visitedProperties.contains(str)) {
                fastArrayList.add(str);
            }
        }
        return (String[]) fastArrayList.toArray(EmptyArrays.EMPTY_STRING);
    }

    /* JADX INFO: renamed from: b */
    public String getDescription(String str) {
        markPropertyVisited(str);
        if (this.properties == null) {
            return null;
        }
        return this.properties.getProperty(str);
    }

    /* JADX INFO: renamed from: a */
    public String getPropertyOrDefault(String str, String str2) {
        markPropertyVisited(str);
        if (this.properties == null) {
            return null;
        }
        return this.properties.getProperty(str, str2);
    }

    /* JADX INFO: renamed from: c */
    public Integer getIntegerProperty(String str) throws MapLoadException {
        String propertyOrDefault = getPropertyOrDefault(str, (String) null);
        if (propertyOrDefault == null) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(propertyOrDefault));
        } catch (NumberFormatException e) {
            throw new MapLoadException(str + ": Unexpected integer value:'" + propertyOrDefault + "'");
        }
    }

    /* JADX INFO: renamed from: a */
    public LocaleString createLocaleStringFromProperty(String str, LocaleString localeString) {
        String propertyOrDefault = getPropertyOrDefault(str, (String) null);
        if (propertyOrDefault == null) {
            return localeString;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(new LanguagePart(null, propertyOrDefault));
        String str2 = str + "_";
        FastArrayList<String> fastArrayList = new FastArrayList();
        for (Object obj : this.properties.keySet()) {
            if (obj instanceof String) {
                String str3 = (String) obj;
                if (str3.startsWith(str2)) {
                    fastArrayList.add(str3);
                }
            } else {
                GameEngine.logColored("createLocaleStringFromProperty: Non string:" + obj);
            }
        }
        for (String str4 : fastArrayList) {
            String lowerCase = str4.substring(str2.length()).toLowerCase(Locale.ROOT);
            GameEngine.logColored("createLocaleStringFromProperty checking: " + str4);
            if (lowerCase.length() <= 4) {
                String description = getDescription(str4);
                GameEngine.logColored("createLocaleStringFromProperty got: " + description);
                GameEngine.logColored("createLocaleStringFromProperty code: " + lowerCase);
                arrayList.add(new LanguagePart(lowerCase, description));
            }
        }
        LocaleString localeString2 = new LocaleString((LanguagePart[]) arrayList.toArray(new LanguagePart[0]));
        localeString2.resolveText();
        GameEngine.logColored("createLocaleStringFromProperty final: " + localeString2.resolveText());
        GameEngine.logColored("createLocaleStringFromProperty locate: " + com.corrodinggames.rts.gameFramework.local.Locale.getLanguage());
        return localeString2;
    }

    /* JADX INFO: renamed from: d */
    public void logTriggerMessage(String str) {
        NetworkEngine.reportDesync("(Map trigger: " + this.name + ", type:" + this.type + "): " + str);
    }

    /* JADX INFO: renamed from: b */
    public String getTriggerTag() {
        return "(Map trigger: " + this.name + ", type:" + this.type + ")";
    }
}
