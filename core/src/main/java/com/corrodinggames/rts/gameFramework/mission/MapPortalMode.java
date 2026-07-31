package com.corrodinggames.rts.gameFramework.mission;

import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.MapObject;
import com.corrodinggames.rts.game.map.MapObjectLayer;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolPaint;
import io.github.rwx.ui.CoreUiEventQueue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class MapPortalMode {
    public static final String LINKS_PROPERTY = "rwxMapLinks";
    public static final String LINKS_PROPERTY_ALIAS = "rwx_map_links";
    public static final String PORTAL_OBJECT_TYPE = "rwx_map_portal";

    private final LinkedHashMap<String, Link> links = new LinkedHashMap<String, Link>();
    private Portal[] portals;
    private final RectF drawRect = new RectF();
    private KoolPaint fillPaint;
    private KoolPaint borderPaint;
    private KoolPaint labelPaint;
    private float jumpCooldownFrames;

    private MapPortalMode(LinkedHashMap<String, Link> links, Portal[] portals) {
        this.links.putAll(links);
        this.portals = portals;
    }

    public static MapPortalMode createForEditor() {
        return new MapPortalMode(new LinkedHashMap<String, Link>(), new Portal[0]);
    }

    public static boolean hasMapLinks(MapObject mapInfo, MapObjectLayer layer) {
        if (readProperty(mapInfo, LINKS_PROPERTY) != null || readProperty(mapInfo, LINKS_PROPERTY_ALIAS) != null) {
            return true;
        }
        if (layer != null && layer.mapObjects != null) {
            for (MapObject object : layer.mapObjects) {
                if (isPortalObject(object)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPortalObject(MapObject object) {
        return object != null && object.type != null && PORTAL_OBJECT_TYPE.equalsIgnoreCase(object.type);
    }

    public static MapPortalMode fromMap(MapObject mapInfo, MapObjectLayer layer) throws MapLoadException {
        LinkedHashMap<String, Link> links = parseLinks(readProperty(mapInfo, LINKS_PROPERTY));
        if (links.isEmpty()) {
            links = parseLinks(readProperty(mapInfo, LINKS_PROPERTY_ALIAS));
        }
        java.util.ArrayList<Portal> portals = new java.util.ArrayList<Portal>();
        if (layer != null && layer.mapObjects != null) {
            for (MapObject object : layer.mapObjects) {
                if (isPortalObject(object)) {
                    portals.add(Portal.fromMapObject(object, portals.size(), links));
                }
            }
        }
        return new MapPortalMode(links, portals.toArray(new Portal[0]));
    }

    public int getPortalCount() {
        return this.portals.length;
    }

    public void writeSnapshot(GameOutputStream stream) throws IOException {
        stream.writeInt(this.portals.length);
        for (Portal portal : this.portals) {
            portal.writeSnapshot(stream);
        }
    }

    public static MapPortalMode readSnapshot(GameInputStream stream, MapObjectLayer layer) throws IOException {
        MapObjectLayer targetLayer = ensureSnapshotObjectLayer(layer);
        removeObjectsOfType(targetLayer, PORTAL_OBJECT_TYPE);
        int portalCount = stream.readInt();
        Portal[] portals = new Portal[portalCount];
        LinkedHashMap<String, Link> links = new LinkedHashMap<String, Link>();
        for (int i = 0; i < portalCount; i++) {
            portals[i] = Portal.readSnapshot(stream, i, targetLayer, links);
        }
        return new MapPortalMode(links, portals);
    }

    public String[] getTargetMapIds() {
        java.util.LinkedHashSet<String> targets = new java.util.LinkedHashSet<String>();
        for (Link link : this.links.values()) {
            if (link.targetMapId != null && link.targetMapId.trim().length() > 0) {
                targets.add(link.targetMapId.trim());
            }
        }
        for (Portal portal : this.portals) {
            if (portal.targetMapId != null && portal.targetMapId.trim().length() > 0) {
                targets.add(portal.targetMapId.trim());
            }
        }
        return targets.toArray(new String[0]);
    }

    public float[] getPortalCenter(String portalId) {
        Portal portal = findPortalById(portalId);
        if (portal == null && this.portals.length > 0) {
            portal = this.portals[0];
        }
        if (portal == null) {
            return null;
        }
        return new float[]{
                (portal.object.tileRect.a + portal.object.tileRect.c) * 0.5f,
                (portal.object.tileRect.b + portal.object.tileRect.d) * 0.5f
        };
    }

    public int addEditorPortal(MapObject object) throws MapLoadException {
        Portal portal = Portal.fromMapObject(object, this.portals.length, this.links);
        Portal[] updated = new Portal[this.portals.length + 1];
        System.arraycopy(this.portals, 0, updated, 0, this.portals.length);
        updated[this.portals.length] = portal;
        this.portals = updated;
        ensureLinkForPortal(portal);
        return this.portals.length;
    }

    public MapObject removeEditorPortalAt(float x, float y) {
        int index = findPortalIndexAt(x, y);
        if (index < 0) {
            return null;
        }
        Portal[] updated = new Portal[this.portals.length - 1];
        if (index > 0) {
            System.arraycopy(this.portals, 0, updated, 0, index);
        }
        if (index < this.portals.length - 1) {
            System.arraycopy(this.portals, index + 1, updated, index, this.portals.length - index - 1);
        }
        MapObject object = this.portals[index].object;
        this.portals = updated;
        return object;
    }

    public EditorPortalProperties getEditorPortalPropertiesAt(float x, float y) {
        int index = findPortalIndexAt(x, y);
        if (index < 0) {
            return null;
        }
        return EditorPortalProperties.fromPortal(this.portals[index]);
    }

    private static String required(String value, String label) throws MapLoadException {
        if (value == null || value.trim().length() == 0) {
            throw new MapLoadException("Map portal " + label + " must not be empty");
        }
        return value.trim();
    }

    public boolean requestJumpAt(float x, float y) {
        int index = findPortalIndexAt(x, y);
        if (index < 0) {
            return false;
        }
        Portal portal = this.portals[index];
        return requestJump(portal);
    }

    public void update(float deltaFrames) {
        this.jumpCooldownFrames = Math.max(0.0f, this.jumpCooldownFrames - deltaFrames);
        if (this.jumpCooldownFrames > 0.0f || this.portals.length == 0) {
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null || gameEngine.replayEngine.j()) {
            return;
        }
        for (Portal portal : this.portals) {
            ArrayList<BaseUnit> unitsToTransfer = new ArrayList<BaseUnit>();
            BaseUnit[] units = BaseUnit.bE.a();
            int size = BaseUnit.bE.size();
            for (int i = 0; i < size; i++) {
                BaseUnit unit = units[i];
                if (!isTransferableUnit(unit)) {
                    continue;
                }
                if (portal.containsPoint(unit.posX, unit.posY) && requestTransfer(gameEngine, portal, unit)) {
                    unitsToTransfer.add(unit);
                }
            }
            if (!unitsToTransfer.isEmpty()) {
                for (BaseUnit unit : unitsToTransfer) {
                    removeTransferredSourceUnit(gameEngine, unit);
                }
                this.jumpCooldownFrames = gameEngine.isNetworkGameActive() ? 180.0f : 30.0f;
                return;
            }
        }
    }

    private boolean requestJump(Portal portal) {
        if (this.jumpCooldownFrames > 0.0f) {
            return true;
        }
        CoreUiEventQueue.requestInGameMapJump(portal.targetMapId, portal.targetPortalId);
        this.jumpCooldownFrames = 120.0f;
        return true;
    }

    private static MapObjectLayer ensureSnapshotObjectLayer(MapObjectLayer layer) throws IOException {
        if (layer != null) {
            return layer;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null || gameEngine.tileMap == null) {
            throw new IOException("No map loaded for map portal snapshot");
        }
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element group = document.createElement("objectgroup");
            group.setAttribute("name", "Triggers");
            gameEngine.tileMap.objectsLayer = new MapObjectLayer(group, gameEngine.tileMap);
            return gameEngine.tileMap.objectsLayer;
        } catch (Exception e) {
            throw new IOException("Failed to create map portal snapshot object layer", e);
        }
    }

    private static MapObject createSnapshotObject(MapObjectLayer layer, String id, String type, float x, float y, float width, float height, Map<String, String> properties) throws IOException {
        try {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine == null || gameEngine.tileMap == null) {
                throw new IOException("No map loaded for snapshot object");
            }
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element object = document.createElement("object");
            object.setAttribute("id", String.valueOf(13000 + (layer.mapObjects == null ? 0 : layer.mapObjects.size())));
            object.setAttribute("name", id);
            object.setAttribute("type", type);
            object.setAttribute("x", trimFloat(x));
            object.setAttribute("y", trimFloat(y));
            object.setAttribute("width", trimFloat(width));
            object.setAttribute("height", trimFloat(height));
            Element propertyRoot = document.createElement("properties");
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                appendProperty(document, propertyRoot, entry.getKey(), entry.getValue());
            }
            object.appendChild(propertyRoot);
            MapObject mapObject = new MapObject(object, gameEngine.tileMap, layer);
            if (layer.mapObjects != null) {
                layer.mapObjects.add(mapObject);
            }
            return mapObject;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to create map portal snapshot object", e);
        }
    }

    private boolean isTransferableUnit(BaseUnit unit) {
        if (unit == null || unit.isDead || unit.team == null || unit.unitTransportTarget != null || unit.parentEntity != null) {
            return false;
        }
        if (!(unit instanceof OrderableUnit)) {
            return false;
        }
        UnitType unitType = unit.r();
        if (unitType == UnitTypeEnum.editorOrBuilder) {
            return false;
        }
        return !unitType.j();
    }

    public void draw(KoolPaint baseTextPaint) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null || gameEngine.renderGraphicsEngine == null || this.portals.length == 0) {
            return;
        }
        ensurePaints(baseTextPaint);
        float zoom = gameEngine.zoom;
        float screenWidth = gameEngine.currentScreenWidthPixels;
        float screenHeight = gameEngine.currentScreenHeightPixels;
        for (Portal portal : this.portals) {
            float left = (portal.object.tileRect.a - gameEngine.viewpointXSnapped) * zoom;
            float top = (portal.object.tileRect.b - gameEngine.viewpointYSnapped) * zoom;
            float right = (portal.object.tileRect.c - gameEngine.viewpointXSnapped) * zoom;
            float bottom = (portal.object.tileRect.d - gameEngine.viewpointYSnapped) * zoom;
            if (right < 0.0f || bottom < 0.0f || left > screenWidth || top > screenHeight) {
                continue;
            }
            if (portal.circle) {
                float centerX = (left + right) * 0.5f;
                float centerY = (top + bottom) * 0.5f;
                float radius = Math.min(right - left, bottom - top) * 0.5f;
                gameEngine.renderGraphicsEngine.a(centerX, centerY, radius, this.fillPaint);
                gameEngine.renderGraphicsEngine.a(centerX, centerY, radius, this.borderPaint);
            } else {
                this.drawRect.a(left, top, right, bottom);
                gameEngine.renderGraphicsEngine.a(this.drawRect, this.fillPaint);
                gameEngine.renderGraphicsEngine.a(this.drawRect, this.borderPaint);
            }
            gameEngine.renderGraphicsEngine.a(portal.id + " -> " + portal.targetMapId, (left + right) * 0.5f, (top + bottom) * 0.5f, this.labelPaint);
        }
    }

    private void ensurePaints(KoolPaint baseTextPaint) {
        if (this.fillPaint == null) {
            this.fillPaint = new KoolPaint();
            this.borderPaint = new KoolPaint();
            this.labelPaint = new KoolPaint();
        }
        this.fillPaint.a(KoolPaint.Style.FILL);
        this.fillPaint.a(40, 88, 170, 228);
        this.borderPaint.a(KoolPaint.Style.STROKE);
        this.borderPaint.a(2.0f);
        this.borderPaint.a(230, 88, 170, 228);
        this.labelPaint.a(baseTextPaint);
        this.labelPaint.a(KoolPaint.Align.CENTER);
        this.labelPaint.a(255, 238, 247, 255);
    }

    private int findPortalIndexAt(float x, float y) {
        for (int i = this.portals.length - 1; i >= 0; i--) {
            if (this.portals[i].containsPoint(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private Portal findPortalById(String portalId) {
        String requested = portalId == null ? null : portalId.trim();
        if (requested == null || requested.length() == 0) {
            return null;
        }
        for (Portal portal : this.portals) {
            if (requested.equalsIgnoreCase(portal.id)) {
                return portal;
            }
        }
        return null;
    }

    private void ensureLinkForPortal(Portal portal) {
        if (portal.targetMapId == null || portal.targetMapId.trim().length() == 0) {
            return;
        }
        String linkId = portal.linkId != null && portal.linkId.trim().length() > 0 ? portal.linkId.trim() : portal.id;
        if (!this.links.containsKey(linkId)) {
            this.links.put(linkId, new Link(linkId, portal.targetMapId, "oneWay"));
        }
    }

    public String updateEditorPortalAt(float x, float y, EditorPortalProperties properties) throws MapLoadException {
        int index = findPortalIndexAt(x, y);
        if (index < 0) {
            return null;
        }
        if (properties == null) {
            throw new MapLoadException("Map portal properties are missing");
        }
        String id = required(properties.id, "portal id");
        String targetMapId = required(properties.targetMapId, "target map id");
        float width = properties.width;
        float height = properties.height;
        if (properties.circle) {
            float diameter = Math.min(width, height);
            width = diameter;
            height = diameter;
        }
        if (width <= 0.0f || height <= 0.0f) {
            throw new MapLoadException("Map portal size must be greater than 0");
        }

        MapObject object = this.portals[index].object;
        object.name = id;
        object.normalizedName = id.trim().toLowerCase(java.util.Locale.ENGLISH);
        object.type = PORTAL_OBJECT_TYPE;
        object.x = properties.x;
        object.y = properties.y;
        object.width = width;
        object.height = height;
        object.tileRect.a(properties.x, properties.y, properties.x + width, properties.y + height);
        setObjectProperty(object, "id", id);
        setObjectProperty(object, "targetMapId", targetMapId);
        setObjectProperty(object, "targetPortalId", properties.targetPortalId == null ? "" : properties.targetPortalId.trim());
        if (properties.circle) {
            setObjectProperty(object, "shape", "circle");
        } else {
            removeObjectProperty(object, "shape");
            removeObjectProperty(object, "circle");
        }
        this.portals[index] = Portal.fromMapObject(object, index, this.links);
        ensureLinkForPortal(this.portals[index]);
        return id;
    }

    public void writeEditorPortalsToDocument(Document document) {
        Element root = document.getDocumentElement();
        Element triggers = findOrCreateTriggers(document, root);
        Element mapInfo = findOrCreateMapInfo(document, triggers);
        setProperty(document, mapInfo, "type", "skirmish");
        setProperty(document, mapInfo, LINKS_PROPERTY, serializeLinks());

        NodeList objects = triggers.getElementsByTagName("object");
        for (int i = objects.getLength() - 1; i >= 0; i--) {
            Element object = (Element) objects.item(i);
            if (PORTAL_OBJECT_TYPE.equalsIgnoreCase(object.getAttribute("type"))) {
                object.getParentNode().removeChild(object);
            }
        }

        int nextId = nextObjectId(root);
        for (Portal portal : this.portals) {
            Element object = document.createElement("object");
            object.setAttribute("id", String.valueOf(nextId++));
            object.setAttribute("name", portal.id);
            object.setAttribute("type", PORTAL_OBJECT_TYPE);
            object.setAttribute("x", trimFloat(portal.object.tileRect.a));
            object.setAttribute("y", trimFloat(portal.object.tileRect.b));
            object.setAttribute("width", trimFloat(portal.object.tileRect.b()));
            object.setAttribute("height", trimFloat(portal.object.tileRect.c()));
            Element properties = document.createElement("properties");
            appendProperty(document, properties, "id", portal.id);
            appendProperty(document, properties, "targetMapId", portal.targetMapId);
            appendProperty(document, properties, "targetPortalId", portal.targetPortalId == null ? "" : portal.targetPortalId);
            if (portal.circle) {
                appendProperty(document, properties, "shape", "circle");
            }
            object.appendChild(properties);
            triggers.appendChild(object);
        }
        root.setAttribute("nextobjectid", String.valueOf(nextId));
    }

    private String serializeLinks() {
        LinkedHashMap<String, Link> all = new LinkedHashMap<String, Link>(this.links);
        for (Portal portal : this.portals) {
            String linkId = portal.linkId != null && portal.linkId.trim().length() > 0 ? portal.linkId : portal.id;
            if (!all.containsKey(linkId)) {
                all.put(linkId, new Link(linkId, portal.targetMapId, "oneWay"));
            }
        }
        StringBuilder builder = new StringBuilder();
        for (Link link : all.values()) {
            if (link.targetMapId == null || link.targetMapId.trim().length() == 0) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(link.id).append('|').append(link.targetMapId).append('|').append(link.direction);
        }
        return builder.toString();
    }

    private static LinkedHashMap<String, Link> parseLinks(String text) {
        LinkedHashMap<String, Link> result = new LinkedHashMap<String, Link>();
        if (text == null) {
            return result;
        }
        String[] entries = text.split("[;\\n]");
        for (String entry : entries) {
            String trimmed = entry.trim();
            if (trimmed.length() == 0) {
                continue;
            }
            String[] rawParts = trimmed.split("[|,]");
            java.util.ArrayList<String> parts = new java.util.ArrayList<String>();
            for (String rawPart : rawParts) {
                String part = rawPart.trim();
                if (part.length() > 0) {
                    parts.add(part);
                }
            }
            if (parts.size() == 1) {
                result.put(parts.get(0), new Link(parts.get(0), parts.get(0), "oneWay"));
            } else if (parts.size() >= 2) {
                String direction = parts.size() >= 3 ? parts.get(2) : "oneWay";
                result.put(parts.get(0), new Link(parts.get(0), parts.get(1), direction));
            }
        }
        return result;
    }

    private boolean requestTransfer(GameEngine gameEngine, Portal portal, BaseUnit unit) {
        UnitType unitType = unit.r();
        if (!(unitType instanceof UnitTypeEnum)) {
            GameEngine.log("Map portal skipped custom unit transfer: " + unitType.getUnitTypeDescriptionShort());
            return false;
        }
        int teamId = unit.team != null ? unit.team.teamId : -1;
        if (teamId < 0) {
            return false;
        }
        float healthFraction = unit.maxHealth > 0.0f ? unit.currentHealth / unit.maxHealth : 1.0f;
        CoreUiEventQueue.requestInGameMapPortalTransfer(
                gameEngine.currentMapPath,
                portal.targetMapId,
                portal.targetPortalId,
                ((UnitTypeEnum) unitType).name(),
                teamId,
                healthFraction,
                unit.direction
        );
        GameEngine.log("Map portal transfer requested: " + ((UnitTypeEnum) unitType).name());
        return true;
    }

    private static Element findOrCreateTriggers(Document document, Element root) {
        NodeList groups = root.getElementsByTagName("objectgroup");
        for (int i = 0; i < groups.getLength(); i++) {
            Element group = (Element) groups.item(i);
            if ("triggers".equalsIgnoreCase(group.getAttribute("name")) || "Triggers".equalsIgnoreCase(group.getAttribute("name"))) {
                return group;
            }
        }
        Element group = document.createElement("objectgroup");
        group.setAttribute("name", "Triggers");
        root.appendChild(group);
        return group;
    }

    private static Element findOrCreateMapInfo(Document document, Element triggers) {
        NodeList objects = triggers.getElementsByTagName("object");
        for (int i = 0; i < objects.getLength(); i++) {
            Element object = (Element) objects.item(i);
            if ("map_info".equalsIgnoreCase(object.getAttribute("name"))) {
                return object;
            }
        }
        Element mapInfo = document.createElement("object");
        mapInfo.setAttribute("name", "map_info");
        mapInfo.setAttribute("type", "map_info");
        mapInfo.setAttribute("x", "40");
        mapInfo.setAttribute("y", "40");
        mapInfo.setAttribute("width", "203");
        mapInfo.setAttribute("height", "122");
        triggers.insertBefore(mapInfo, triggers.getFirstChild());
        return mapInfo;
    }

    private static int nextObjectId(Element root) {
        int maxId = 0;
        NodeList objects = root.getElementsByTagName("object");
        for (int i = 0; i < objects.getLength(); i++) {
            String id = ((Element) objects.item(i)).getAttribute("id");
            try {
                maxId = Math.max(maxId, Integer.parseInt(id));
            } catch (Exception ignored) {
            }
        }
        return maxId + 1;
    }

    private static void setProperty(Document document, Element object, String name, String value) {
        Element properties = findOrCreateProperties(document, object);
        NodeList entries = properties.getElementsByTagName("property");
        for (int i = 0; i < entries.getLength(); i++) {
            Element entry = (Element) entries.item(i);
            if (name.equalsIgnoreCase(entry.getAttribute("name"))) {
                entry.setAttribute("value", value);
                return;
            }
        }
        appendProperty(document, properties, name, value);
    }

    private static Element findOrCreateProperties(Document document, Element object) {
        NodeList entries = object.getElementsByTagName("properties");
        if (entries.getLength() > 0) {
            return (Element) entries.item(0);
        }
        Element properties = document.createElement("properties");
        object.appendChild(properties);
        return properties;
    }

    private static void appendProperty(Document document, Element properties, String name, String value) {
        Element property = document.createElement("property");
        property.setAttribute("name", name);
        property.setAttribute("value", value == null ? "" : value);
        properties.appendChild(property);
    }

    private static void setObjectProperty(MapObject object, String name, String value) {
        if (object.properties == null) {
            object.properties = new Properties();
        }
        object.properties.setProperty(name, value == null ? "" : value);
    }

    private static void removeObjectProperty(MapObject object, String name) {
        if (object.properties == null) {
            return;
        }
        java.util.ArrayList<String> keys = new java.util.ArrayList<String>();
        for (Object key : object.properties.keySet()) {
            if (key instanceof String && name.equalsIgnoreCase((String) key)) {
                keys.add((String) key);
            }
        }
        for (String key : keys) {
            object.properties.remove(key);
        }
    }

    private void removeTransferredSourceUnit(GameEngine gameEngine, BaseUnit unit) {
        boolean p2pSession = gameEngine.networkEngine != null && gameEngine.networkEngine.p2pSession;
        if (!gameEngine.isNetworkGameActive() || p2pSession) {
            unit.removeFromGame();
        }
    }

    private static void removeObjectsOfType(MapObjectLayer layer, String type) {
        if (layer == null || layer.mapObjects == null) {
            return;
        }
        for (int i = layer.mapObjects.size() - 1; i >= 0; i--) {
            MapObject object = layer.mapObjects.get(i);
            if (object != null && object.type != null && type.equalsIgnoreCase(object.type)) {
                layer.mapObjects.remove(i);
            }
        }
    }

    public void writeEditorPortalsToPath(String abstractPath) throws IOException {
        String path = FileHelper.convertAbstractPath(abstractPath);
        Document document;
        try {
            InputStream inputStream = FileHelper.openFileByPath(abstractPath);
            if (inputStream == null) {
                inputStream = FileHelper.openFileByPath(path);
            }
            if (inputStream == null) {
                throw new IOException("Could not open exported map: " + path);
            }
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(false);
                javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver((publicId, systemId) -> new InputSource(new ByteArrayInputStream(new byte[0])));
                document = builder.parse(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException("Failed to parse exported map", e);
        }
        writeEditorPortalsToDocument(document);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            OutputStream outputStream = FileHelper.openOutputStreamByPath(path, false);
            try {
                transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            } finally {
                outputStream.close();
            }
        } catch (Exception e) {
            throw new IOException("Failed to write exported map", e);
        }
    }

    private static String readProperty(MapObject object, String key) {
        if (object == null) {
            return null;
        }
        String value = object.getDescription(key);
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.length() == 0 ? null : value;
    }

    private static boolean readBooleanProperty(MapObject object, String key, boolean defaultValue) {
        String value = readProperty(object, key);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }

    private static boolean isCircle(MapObject object) {
        String shape = readProperty(object, "shape");
        if (shape != null && ("circle".equalsIgnoreCase(shape) || "ellipse".equalsIgnoreCase(shape))) {
            return true;
        }
        return readBooleanProperty(object, "circle", false);
    }

    private static String trimFloat(float value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

    public static final class EditorPortalProperties {
        public String id;
        public boolean circle;
        public float x;
        public float y;
        public float width;
        public float height;
        public String targetMapId;
        public String targetPortalId;

        private static EditorPortalProperties fromPortal(Portal portal) {
            EditorPortalProperties properties = new EditorPortalProperties();
            properties.id = portal.id;
            properties.circle = portal.circle;
            properties.x = portal.object.tileRect.a;
            properties.y = portal.object.tileRect.b;
            properties.width = portal.object.tileRect.b();
            properties.height = portal.object.tileRect.c();
            properties.targetMapId = portal.targetMapId;
            properties.targetPortalId = portal.targetPortalId;
            return properties;
        }
    }

    private static final class Link {
        final String id;
        final String targetMapId;
        final String direction;

        Link(String id, String targetMapId, String direction) {
            this.id = id;
            this.targetMapId = targetMapId;
            this.direction = direction == null || direction.trim().length() == 0 ? "oneWay" : direction.trim();
        }
    }

    private static final class Portal {
        final String id;
        final String linkId;
        final String targetMapId;
        final String targetPortalId;
        final boolean circle;
        final MapObject object;

        Portal(String id, String linkId, String targetMapId, String targetPortalId, boolean circle, MapObject object) {
            this.id = id;
            this.linkId = linkId;
            this.targetMapId = targetMapId;
            this.targetPortalId = targetPortalId;
            this.circle = circle;
            this.object = object;
        }

        static Portal fromMapObject(MapObject object, int index, Map<String, Link> links) throws MapLoadException {
            String id = readProperty(object, "id");
            if (id == null) {
                id = object.name;
            }
            if (id == null || id.trim().length() == 0) {
                id = "portal" + (index + 1);
            }
            String linkId = readProperty(object, "linkId");
            String targetMapId = readProperty(object, "targetMapId");
            if ((targetMapId == null || targetMapId.trim().length() == 0) && linkId != null) {
                Link link = links.get(linkId);
                if (link != null) {
                    targetMapId = link.targetMapId;
                }
            }
            if (targetMapId == null || targetMapId.trim().length() == 0) {
                throw new MapLoadException("Map portal '" + id + "' requires targetMapId or a valid linkId");
            }
            return new Portal(id.trim(), linkId, targetMapId.trim(), readProperty(object, "targetPortalId"), isCircle(object), object);
        }

        void writeSnapshot(GameOutputStream stream) throws IOException {
            stream.writeStringUTF(this.id);
            stream.writeFloat(this.object.x);
            stream.writeFloat(this.object.y);
            stream.writeFloat(this.object.width);
            stream.writeFloat(this.object.height);
            stream.writeBoolean(this.circle);
            stream.writeStringUTF(this.targetMapId == null ? "" : this.targetMapId);
            stream.writeStringUTF(this.targetPortalId == null ? "" : this.targetPortalId);
            stream.writeStringUTF(this.linkId == null ? "" : this.linkId);
        }

        static Portal readSnapshot(GameInputStream stream, int index, MapObjectLayer layer, Map<String, Link> links) throws IOException {
            String id = stream.readUTF();
            float x = stream.readFloat();
            float y = stream.readFloat();
            float width = stream.readFloat();
            float height = stream.readFloat();
            boolean circle = stream.readBoolean();
            String targetMapId = stream.readUTF();
            String targetPortalId = stream.readUTF();
            String linkId = stream.readUTF();
            LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
            properties.put("id", id);
            properties.put("targetMapId", targetMapId);
            properties.put("targetPortalId", targetPortalId);
            if (linkId != null && linkId.trim().length() > 0) {
                properties.put("linkId", linkId);
                links.put(linkId, new Link(linkId, targetMapId, "oneWay"));
            }
            if (circle) {
                properties.put("shape", "circle");
            }
            MapObject object = createSnapshotObject(layer, id, PORTAL_OBJECT_TYPE, x, y, width, height, properties);
            try {
                return Portal.fromMapObject(object, index, links);
            } catch (MapLoadException e) {
                throw new IOException("Failed to read map portal snapshot", e);
            }
        }

        boolean containsPoint(float x, float y) {
            float left = this.object.tileRect.a;
            float top = this.object.tileRect.b;
            float right = this.object.tileRect.c;
            float bottom = this.object.tileRect.d;
            if (x < left || x > right || y < top || y > bottom) {
                return false;
            }
            if (!this.circle) {
                return true;
            }
            float centerX = (left + right) * 0.5f;
            float centerY = (top + bottom) * 0.5f;
            float radius = Math.min(right - left, bottom - top) * 0.5f;
            float dx = x - centerX;
            float dy = y - centerY;
            return (dx * dx) + (dy * dy) <= radius * radius;
        }
    }
}
