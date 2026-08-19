package com.corrodinggames.rts.game.map;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.Rect;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/j.class */
public class Tileset {

    /* JADX INFO: renamed from: a */
    public String tilesetSource;

    /* JADX INFO: renamed from: b */
    public Texture tilesetBitmap;

    /* JADX INFO: renamed from: c */
    public String imageKey;

    /* JADX INFO: renamed from: d */
    int tileWidth;

    /* JADX INFO: renamed from: e */
    int tileHeight;

    /* JADX INFO: renamed from: f */
    int tileStridePixelsX;

    /* JADX INFO: renamed from: g */
    int tileStridePixelsY;

    /* JADX INFO: renamed from: j */
    int columns;

    /* JADX INFO: renamed from: k */
    float invColumns;

    /* JADX INFO: renamed from: l */
    public int firstGid;

    /* JADX INFO: renamed from: n */
    public short firstGlobalTileIndex;

    /* JADX INFO: renamed from: o */
    public TileMap tileMap;

    /* JADX INFO: renamed from: t */
    static String EMBED_PREFIX = "[EMBED]";

    /* JADX INFO: renamed from: u */
    static ArrayList<TilesetImageDescriptor> imageDescriptors = new ArrayList();

    /* JADX INFO: renamed from: h */
    int atlasOriginX = 0;

    /* JADX INFO: renamed from: i */
    int atlasOriginY = 0;

    /* JADX INFO: renamed from: m */
    public int lastGid = Integer.MAX_VALUE;

    /* JADX INFO: renamed from: p */
    public boolean usedInMap = false;

    /* JADX INFO: renamed from: q */
    public boolean usedInFogLayer = false;

    /* JADX INFO: renamed from: r */
    public boolean usedInNonGroundLayer = false;

    /* JADX INFO: renamed from: s */
    public boolean containsUnits = false;

    /* JADX INFO: renamed from: x */
    private HashMap<Integer, Properties> propertiesByTileId = new HashMap<>();

    /* JADX INFO: renamed from: v */
    Rect cachedRect = new Rect();

    /* JADX INFO: renamed from: w */
    int cachedRectIndex = -1;

    public Tileset(TileMap tileMap, String str, int i) throws MapLoadException {
        this.tileMap = tileMap;
        this.firstGid = i;
        Element elementLoadTilesetTexture = loadTilesetXml(tileMap, str);
        this.tilesetSource = str;
        parseTileset(elementLoadTilesetTexture);
    }

    /* JADX INFO: renamed from: a */
    public static Element loadTilesetXml(TileMap tileMap, String str) throws MapLoadException {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tileMap.openAssetStreamFromPair("tilesets/", str)).getDocumentElement();
        } catch (Exception e) {
            GameEngine.getInstance().alert("Unable to load or parse sourced tileset: tilesets/" + str, 1);
            throw new MapLoadException("Unable to load or parse sourced tileset: tilesets/" + str, e);
        }
    }

    public Tileset(TileMap tileMap, Element element) throws MapLoadException {
        this.tileMap = tileMap;
        this.firstGid = Integer.parseInt(element.getAttribute("firstgid"));
        String attribute = element.getAttribute("source");
        if (attribute != null && !attribute.equals(VariableScope.nullOrMissingString)) {
            element = loadTilesetXml(tileMap, attribute);
            this.tilesetSource = attribute;
        }
        parseTileset(element);
    }

    /* JADX INFO: renamed from: a */
    public static void markAllDescriptorsUnused() {
        for (TilesetImageDescriptor imageDescriptor : imageDescriptors) {
            imageDescriptor.inUse = false;
        }
    }

    /* JADX INFO: renamed from: b */
    public static void freeUnusedImages() {
        Iterator<TilesetImageDescriptor> it = imageDescriptors.iterator();
        while (it.hasNext()) {
            TilesetImageDescriptor tilesetImageDescriptor = it.next();
            if (!tilesetImageDescriptor.inUse) {
                if (tilesetImageDescriptor.texture != null) {
                    tilesetImageDescriptor.texture.o();
                    tilesetImageDescriptor.texture = null;
                }
                tilesetImageDescriptor.embeddedBase64 = null;
                it.remove();
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public static String registerEmbeddedImage(String str, String str2) {
        for (TilesetImageDescriptor tilesetImageDescriptor : imageDescriptors) {
            if (str.equalsIgnoreCase(tilesetImageDescriptor.embeddedBase64)) {
                return tilesetImageDescriptor.imageKey;
            }
        }
        TilesetImageDescriptor tilesetImageDescriptor2 = new TilesetImageDescriptor();
        tilesetImageDescriptor2.inUse = false;
        tilesetImageDescriptor2.texture = null;
        tilesetImageDescriptor2.embeddedBase64 = str;
        tilesetImageDescriptor2.pathPrefix = EMBED_PREFIX;
        tilesetImageDescriptor2.imageKey = EMBED_PREFIX + TilesetImageDescriptor.nextEmbedId;
        tilesetImageDescriptor2.originalImageName = str2;
        TilesetImageDescriptor.nextEmbedId++;
        imageDescriptors.add(tilesetImageDescriptor2);
        return tilesetImageDescriptor2.imageKey;
    }

    /* JADX INFO: renamed from: a */
    public static Texture loadTilesetTexture(String str) throws MapLoadException {
        GameEngine gameEngine = GameEngine.getInstance();
        String str2 = "tilesets/bitmaps/";
        if (str.startsWith(EMBED_PREFIX)) {
            str2 = EMBED_PREFIX;
        }
        TilesetImageDescriptor tilesetImageDescriptor = null;
        Iterator it = imageDescriptors.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            TilesetImageDescriptor tilesetImageDescriptor2 = (TilesetImageDescriptor) it.next();
            if (str.equalsIgnoreCase(tilesetImageDescriptor2.imageKey) && str2.equalsIgnoreCase(tilesetImageDescriptor2.pathPrefix)) {
                tilesetImageDescriptor = tilesetImageDescriptor2;
                break;
            }
        }
        if (tilesetImageDescriptor != null) {
            if (tilesetImageDescriptor.embeddedBase64 != null) {
                try {
                    Texture textureA = gameEngine.renderGraphicsEngine.a((InputStream) new BufferedInputStream(MapLayer.decodeCompressedBase64Stream(tilesetImageDescriptor.embeddedBase64, "base64", VariableScope.nullOrMissingString)), false);
                    if (textureA == null) {
                        throw new MapLoadException("Embedded tilesetBitmap is null for: " + str);
                    }
                    tilesetImageDescriptor.texture = textureA;
                    tilesetImageDescriptor.embeddedBase64 = null;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    throw new MapLoadException("Error loading embedded base64 image:" + tilesetImageDescriptor.originalImageName + " - " + e.getMessage());
                }
            }
            tilesetImageDescriptor.inUse = true;
            return tilesetImageDescriptor.texture;
        }
        try {
            InputStream inputStreamOpenAssetStreamFromPair = gameEngine.tileMap.openAssetStreamFromPair(str2, str);
            Texture textureA2 = gameEngine.renderGraphicsEngine.a(inputStreamOpenAssetStreamFromPair, false);
            if (inputStreamOpenAssetStreamFromPair != null) {
                try {
                    inputStreamOpenAssetStreamFromPair.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (textureA2 == null) {
                throw new RuntimeException("tilesetBitmap is null for: " + str);
            }
            textureA2.a("tilesets/" + str);
            TilesetImageDescriptor tilesetImageDescriptor3 = new TilesetImageDescriptor();
            tilesetImageDescriptor3.inUse = true;
            tilesetImageDescriptor3.texture = textureA2;
            tilesetImageDescriptor3.pathPrefix = str2;
            tilesetImageDescriptor3.imageKey = str;
            imageDescriptors.add(tilesetImageDescriptor3);
            return tilesetImageDescriptor3.texture;
        } catch (IOException e3) {
            throw new MapLoadException("Image file could not be found or loaded: " + str2 + str, e3);
        }
    }

    /* JADX INFO: renamed from: a */
    public String getEmbeddedPngBase64(TileMap tileMap, Element element) {
        Element element2 = (Element) element.getElementsByTagName("properties").item(0);
        if (element2 != null) {
            NodeList elementsByTagName = element2.getElementsByTagName("property");
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Element element3 = (Element) elementsByTagName.item(i);
                if (element3.getAttribute("name").equals("embedded_png")) {
                    String attribute = element3.getAttribute("value");
                    if (attribute != null && !attribute.equals(VariableScope.nullOrMissingString)) {
                        return attribute;
                    }
                    Node firstChild = element3.getFirstChild();
                    if (firstChild != null) {
                        return firstChild.getNodeValue();
                    }
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public void parseTileset(Element element) throws MapLoadException {
        NodeList elementsByTagName = element.getElementsByTagName("image");
        if (elementsByTagName.getLength() > 0) {
            this.imageKey = GameEngine.getFilename(((Element) elementsByTagName.item(0)).getAttribute("source").trim());
        }
        String strComputeTileRect = getEmbeddedPngBase64(this.tileMap, element);
        if (strComputeTileRect != null) {
            this.imageKey = registerEmbeddedImage(strComputeTileRect, this.imageKey);
        }
        if (this.imageKey == null) {
            throw new MapLoadException("Map tileset is missing an image tag or embedded image data");
        }
        this.tileWidth = this.tileMap.tileWorldSizeX;
        this.tileHeight = this.tileMap.tileWorldSizeY;
        if (element.hasAttribute("tilewidth")) {
            this.tileWidth = Integer.parseInt(element.getAttribute("tilewidth"));
            this.tileHeight = Integer.parseInt(element.getAttribute("tileheight"));
        }
        if (GameEngine.isSpaceGame()) {
            this.tileWidth = this.tileMap.tileWorldSizeX;
            this.tileHeight = this.tileMap.tileWorldSizeY;
        }
        int i = 0;
        if (element.hasAttribute("spacing")) {
            i = Integer.parseInt(element.getAttribute("spacing"));
        }
        this.tileStridePixelsX = this.tileWidth + i;
        this.tileStridePixelsY = this.tileHeight + i;
        NodeList elementsByTagName2 = element.getElementsByTagName("tile");
        for (int i2 = 0; i2 < elementsByTagName2.getLength(); i2++) {
            Element element2 = (Element) elementsByTagName2.item(i2);
            int i3 = Integer.parseInt(element2.getAttribute("id")) + this.firstGid;
            Properties properties = new Properties();
            Element element3 = (Element) element2.getElementsByTagName("properties").item(0);
            if (element3 != null) {
                NodeList elementsByTagName3 = element3.getElementsByTagName("property");
                for (int i4 = 0; i4 < elementsByTagName3.getLength(); i4++) {
                    Element element4 = (Element) elementsByTagName3.item(i4);
                    String attribute = element4.getAttribute("name");
                    String attribute2 = element4.getAttribute("value");
                    if ("unit".equalsIgnoreCase(attribute) || "customUnit".equalsIgnoreCase(attribute)) {
                        this.containsUnits = true;
                    }
                    properties.setProperty(attribute, attribute2);
                }
            }
            this.propertiesByTileId.put(i3, properties);
        }
    }

    /* JADX INFO: renamed from: c */
    void initTextureMetrics() throws MapLoadException {
        this.tilesetBitmap = loadTilesetTexture(this.imageKey);
        this.columns = this.tilesetBitmap.m() / this.tileStridePixelsX;
        if (this.columns == 0) {
            this.columns = 1;
        }
        this.invColumns = 1.0f / this.columns;
    }

    /* JADX INFO: renamed from: a */
    public Properties getPropertiesByTileId(int i) {
        return this.propertiesByTileId.get(i);
    }

    /* JADX INFO: renamed from: a */
    public final void getTileRect(int i, Rect rect) {
        int i2 = i % this.columns;
        int i3 = (int) (i * this.invColumns);
        int i4 = this.atlasOriginX + (i2 * this.tileStridePixelsX);
        int i5 = this.atlasOriginY + (i3 * this.tileStridePixelsY);
        rect.a = i4;
        rect.b = i5;
        rect.c = i4 + this.tileWidth;
        rect.d = i5 + this.tileHeight;
    }

    /* JADX INFO: renamed from: b */
    public final Rect getTileRectCached(int i) {
        if (this.cachedRectIndex == i) {
            return this.cachedRect;
        }
        this.cachedRectIndex = i;
        getTileRect(i, this.cachedRect);
        return this.cachedRect;
    }

    /* JADX INFO: renamed from: c */
    public void setLastGid(int i) {
        this.lastGid = i;
    }

    /* JADX INFO: renamed from: d */
    public boolean containsGid(int i) {
        return i >= this.firstGid && i <= this.lastGid;
    }

    /* JADX INFO: renamed from: d */
    public void cleanup() {
        this.tilesetBitmap = null;
        this.tileMap = null;
        this.propertiesByTileId = null;
    }

    /* JADX INFO: renamed from: b */
    public Integer findTileIdByProperty(String str, String str2) {
        for (Map.Entry entry : this.propertiesByTileId.entrySet()) {
            Integer num = (Integer) entry.getKey();
            String property = ((Properties) entry.getValue()).getProperty(str);
            if (property != null && property.equals(str2)) {
                return num;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public int getIndexOffsetByPosition(int i, int i2) {
        int iM;
        if (this.tilesetBitmap == null) {
            GameEngine.log("getIndexOffsetByPosition tilesetBitmap == null");
            iM = 3;
        } else if (this.tileWidth == 0) {
            GameEngine.log("getIndexOffsetByPosition tileWidth==0");
            iM = 3;
        } else {
            iM = this.tilesetBitmap.m() / this.tileWidth;
        }
        return i + (i2 * iM);
    }
}
