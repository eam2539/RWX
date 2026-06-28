package com.corrodinggames.rts.gameFramework.mission;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.MapObject;
import com.corrodinggames.rts.game.map.MapObjectLayer;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolPaint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AreaControlMode {
    public static final String MODE_ID = "areaControl";
    public static final String MAP_INFO_PROPERTY = "rwxMode";
    public static final String MAP_INFO_PROPERTY_ALIAS = "rwx_mode";
    public static final String CONTROL_ZONE_TYPE = "control_zone";

    private int scoreLimit;
    private ControlZone[] zones;
    private final HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
    private final HashMap<Integer, Integer> announcedScoreMilestones = new HashMap<Integer, Integer>();
    private boolean ended;
    private boolean announcedStart;
    private int winningAllyGroup = -1;
    private final RectF hudRect = new RectF();
    private KoolPaint hudBackgroundPaint;
    private KoolPaint hudBorderPaint;
    private KoolPaint hudTitlePaint;
    private KoolPaint hudTextPaint;
    private KoolPaint hudMutedTextPaint;
    private KoolPaint hudAccentPaint;
    private KoolPaint hudCellPaint;
    private final RectF worldZoneRect = new RectF();
    private KoolPaint worldZoneFillPaint;
    private KoolPaint worldZoneBorderPaint;
    private KoolPaint worldZoneLabelPaint;
    private KoolPaint worldZoneProgressPaint;
    private KoolPaint worldZoneProgressRingPaint;

    private AreaControlMode(int scoreLimit, ControlZone[] zones) {
        this.scoreLimit = scoreLimit;
        this.zones = zones;
        for (ControlZone zone : zones) {
            if (zone.ownerAllyGroup != -1) {
                ensureScoreEntry(zone.ownerAllyGroup);
            }
        }
    }

    public static AreaControlMode createForEditor() {
        return new AreaControlMode(180, new ControlZone[0]);
    }

    public static String readMode(MapObject mapInfo) {
        String mode = readProperty(mapInfo, MAP_INFO_PROPERTY);
        if (mode == null) {
            mode = readProperty(mapInfo, MAP_INFO_PROPERTY_ALIAS);
        }
        return mode;
    }

    public static boolean isAreaControlMode(String mode) {
        return mode != null && MODE_ID.equalsIgnoreCase(mode.trim());
    }

    public static boolean isControlZoneObject(MapObject object) {
        return object != null && object.type != null && CONTROL_ZONE_TYPE.equalsIgnoreCase(object.type);
    }

    public static AreaControlMode fromMap(MapObject mapInfo, MapObjectLayer objectLayer) throws MapLoadException {
        int scoreLimit = readIntProperty(mapInfo, "areaControlScoreLimit", readIntProperty(mapInfo, "scoreLimit", 500));
        if (scoreLimit <= 0) {
            throw new MapLoadException("areaControl scoreLimit must FastArrayList greater than 0");
        }
        float defaultScoreIntervalFrames = readSecondsProperty(mapInfo, "scoreInterval", 5.0f);
        if (readProperty(mapInfo, "areaControlScoreInterval") != null) {
            defaultScoreIntervalFrames = readSecondsProperty(mapInfo, "areaControlScoreInterval", defaultScoreIntervalFrames / 60.0f);
        }
        java.util.ArrayList<ControlZone> zones = new java.util.ArrayList<ControlZone>();
        if (objectLayer != null && objectLayer.mapObjects != null) {
            for (MapObject object : objectLayer.mapObjects) {
                if (isControlZoneObject(object)) {
                    zones.add(ControlZone.fromMapObject(object, zones.size(), defaultScoreIntervalFrames));
                }
            }
        }
        if (zones.isEmpty()) {
            throw new MapLoadException("areaControl mode requires at least one control_zone object");
        }
        return new AreaControlMode(scoreLimit, zones.toArray(new ControlZone[0]));
    }

    public int getZoneCount() {
        return this.zones.length;
    }

    public int getScoreLimit() {
        return this.scoreLimit;
    }

    public void setEditorScoreLimit(int scoreLimit) throws MapLoadException {
        if (scoreLimit <= 0) {
            throw new MapLoadException("RWX area control victory score must FastArrayList greater than 0");
        }
        this.scoreLimit = scoreLimit;
    }

    public EditorZoneProperties getEditorZonePropertiesAt(float x, float y) {
        int index = findEditorZoneIndexAt(x, y);
        if (index < 0) {
            return null;
        }
        return EditorZoneProperties.fromZone(this.zones[index]);
    }

    public int addEditorZone(MapObject object) throws MapLoadException {
        ControlZone zone = ControlZone.fromMapObject(object, this.zones.length, 180.0f);
        ControlZone[] updated = new ControlZone[this.zones.length + 1];
        System.arraycopy(this.zones, 0, updated, 0, this.zones.length);
        updated[this.zones.length] = zone;
        this.zones = updated;
        return this.zones.length;
    }

    public String updateEditorZoneAt(float x, float y, EditorZoneProperties properties) throws MapLoadException {
        int index = findEditorZoneIndexAt(x, y);
        if (index < 0) {
            return null;
        }
        if (properties == null) {
            throw new MapLoadException("RWX area zone properties are missing");
        }
        String id = properties.id == null ? "" : properties.id.trim();
        if (id.length() == 0) {
            throw new MapLoadException("RWX area zone id must not FastArrayList empty");
        }
        float width = properties.width;
        float height = properties.height;
        if (properties.circle) {
            float diameter = Math.min(width, height);
            width = diameter;
            height = diameter;
        }
        if (width <= 0.0f || height <= 0.0f) {
            throw new MapLoadException("RWX area zone size must FastArrayList greater than 0");
        }

        ControlZone existing = this.zones[index];
        MapObject object = existing.object;
        object.name = id;
        object.normalizedName = id.trim().toLowerCase(java.util.Locale.ENGLISH);
        object.type = CONTROL_ZONE_TYPE;
        object.x = properties.x;
        object.y = properties.y;
        object.width = width;
        object.height = height;
        object.tileRect.a(properties.x, properties.y, properties.x + width, properties.y + height);
        setObjectProperty(object, "id", id);
        if (properties.circle) {
            setObjectProperty(object, "shape", "circle");
        } else {
            removeObjectProperty(object, "shape");
            removeObjectProperty(object, "circle");
        }
        setObjectProperty(object, "captureTime", trimFloat(properties.captureTimeSeconds));
        setObjectProperty(object, "neutralizeTime", trimFloat(properties.neutralizeTimeSeconds));
        setObjectProperty(object, "scoreRate", String.valueOf(properties.scoreRate));
        setObjectProperty(object, "scoreInterval", trimFloat(properties.scoreIntervalSeconds));
        setObjectProperty(object, "groundOnly", String.valueOf(properties.groundOnly));
        setObjectProperty(object, "maxCaptureWeight", trimFloat(properties.maxCaptureWeight));
        if (properties.startingOwner == -1) {
            removeObjectProperty(object, "startingOwner");
        } else {
            setObjectProperty(object, "startingOwner", String.valueOf(properties.startingOwner));
        }
        this.zones[index] = ControlZone.fromMapObject(object, index, 180.0f);
        return id;
    }

    public MapObject removeEditorZoneAt(float x, float y) {
        int i = findEditorZoneIndexAt(x, y);
        if (i >= 0) {
            ControlZone[] updated = new ControlZone[this.zones.length - 1];
            if (i > 0) {
                System.arraycopy(this.zones, 0, updated, 0, i);
            }
            if (i < this.zones.length - 1) {
                System.arraycopy(this.zones, i + 1, updated, i, this.zones.length - i - 1);
            }
            MapObject object = this.zones[i].object;
            this.zones = updated;
            return object;
        }
        return null;
    }

    private int findEditorZoneIndexAt(float x, float y) {
        for (int i = this.zones.length - 1; i >= 0; i--) {
            ControlZone zone = this.zones[i];
            if (zone.containsPoint(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public void writeEditorZonesToDocument(Document document) {
        Element root = document.getDocumentElement();
        Element triggers = findOrCreateTriggers(document, root);
        Element mapInfo = findOrCreateMapInfo(document, triggers, root);
        setProperty(document, mapInfo, "type", "skirmish");
        setProperty(document, mapInfo, MAP_INFO_PROPERTY, MODE_ID);
        setProperty(document, mapInfo, "areaControlScoreLimit", String.valueOf(this.scoreLimit));
        setProperty(document, mapInfo, "areaControlScoreInterval", "3");

        NodeList objects = triggers.getElementsByTagName("object");
        for (int i = objects.getLength() - 1; i >= 0; i--) {
            Element object = (Element) objects.item(i);
            if (CONTROL_ZONE_TYPE.equalsIgnoreCase(object.getAttribute("type"))) {
                object.getParentNode().removeChild(object);
            }
        }

        int nextId = nextObjectId(root);
        for (ControlZone zone : this.zones) {
            Element object = document.createElement("object");
            object.setAttribute("id", String.valueOf(nextId++));
            object.setAttribute("name", zone.id);
            object.setAttribute("type", CONTROL_ZONE_TYPE);
            object.setAttribute("x", trimFloat(zone.object.tileRect.a));
            object.setAttribute("y", trimFloat(zone.object.tileRect.b));
            object.setAttribute("width", trimFloat(zone.object.tileRect.b()));
            object.setAttribute("height", trimFloat(zone.object.tileRect.c()));

            Element properties = document.createElement("properties");
            appendProperty(document, properties, "id", zone.id);
            if (zone.circle) {
                appendProperty(document, properties, "shape", "circle");
            }
            appendProperty(document, properties, "captureTime", trimFloat(zone.captureFrames / 60.0f));
            appendProperty(document, properties, "neutralizeTime", trimFloat(zone.neutralizeFrames / 60.0f));
            appendProperty(document, properties, "scoreRate", String.valueOf(zone.scoreRate));
            appendProperty(document, properties, "scoreInterval", trimFloat(zone.scoreIntervalFrames / 60.0f));
            appendProperty(document, properties, "groundOnly", String.valueOf(zone.groundOnly));
            object.appendChild(properties);
            triggers.appendChild(object);
        }
        root.setAttribute("nextobjectid", String.valueOf(nextId));
    }

    public void writeEditorZonesToPath(String abstractPath) throws IOException {
        updateEditorZonesAtPath(abstractPath, this);
    }

    public static void clearEditorZonesFromPath(String abstractPath) throws IOException {
        updateEditorZonesAtPath(abstractPath, null);
    }

    private static void updateEditorZonesAtPath(String abstractPath, AreaControlMode mode) throws IOException {
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
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(new EntityResolver() {
                    @Override
                    public InputSource resolveEntity(String publicId, String systemId) {
                        return new InputSource(new ByteArrayInputStream(new byte[0]));
                    }
                });
                document = builder.parse(inputStream);
            } finally {
                inputStream.close();
            }
            if (mode == null) {
                clearEditorZonesFromDocument(document);
            } else {
                mode.writeEditorZonesToDocument(document);
            }
            OutputStream outputStream = FileHelper.openOutputStreamByPath(path, false);
            if (outputStream == null) {
                throw new IOException("Could not write exported map: " + path);
            }
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("indent", "yes");
                transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            } finally {
                outputStream.close();
            }
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    public static void clearEditorZonesFromDocument(Document document) {
        Element root = document.getDocumentElement();
        NodeList groups = root.getElementsByTagName("objectgroup");
        for (int groupIndex = 0; groupIndex < groups.getLength(); groupIndex++) {
            Element group = (Element) groups.item(groupIndex);
            if (!"Triggers".equalsIgnoreCase(group.getAttribute("name"))) {
                continue;
            }
            NodeList objects = group.getElementsByTagName("object");
            for (int i = objects.getLength() - 1; i >= 0; i--) {
                Element object = (Element) objects.item(i);
                if (CONTROL_ZONE_TYPE.equalsIgnoreCase(object.getAttribute("type"))) {
                    object.getParentNode().removeChild(object);
                    continue;
                }
                if ("map_info".equalsIgnoreCase(object.getAttribute("name"))) {
                    removeProperty(object, MAP_INFO_PROPERTY);
                    removeProperty(object, MAP_INFO_PROPERTY_ALIAS);
                    removeProperty(object, "areaControlScoreLimit");
                    removeProperty(object, "areaControlScoreInterval");
                }
            }
        }
    }

    public void update(float delta) {
        if (this.ended) {
            return;
        }
        announceStartIfNeeded();
        for (ControlZone zone : this.zones) {
            zone.update(delta, this);
            if (this.ended) {
                return;
            }
        }
    }

    public void draw(KoolPaint titlePaint, KoolPaint detailPaint) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null || gameEngine.renderGraphicsEngine == null || this.zones.length == 0) {
            return;
        }
        ensureHudPaints(gameEngine, titlePaint, detailPaint);

        drawWorldZones(gameEngine);

        float screenWidth = gameEngine.currentScreenWidthPixels;
        float availableWidth = Math.max(1.0f, screenWidth - Math.max(0.0f, gameEngine.sidebarWidth));
        float margin = gameEngine.toScreenPixels(8.0f);
        float horizontalPadding = gameEngine.toScreenPixels(10.0f);
        float verticalPadding = gameEngine.toScreenPixels(5.0f);
        float rowGap = gameEngine.toScreenPixels(2.0f);
        float zoneStripHeight = Math.max(gameEngine.toScreenPixels(14.0f), this.hudMutedTextPaint.k() + gameEngine.toScreenPixels(4.0f));
        float titleHeight = gameEngine.renderGraphicsEngine.a("Area Control", this.hudTitlePaint);
        float scoreHeight = gameEngine.renderGraphicsEngine.a("Score", this.hudTextPaint);
        float scoreWidth = measureScoreSummaryWidth(gameEngine);
        float desiredWidth = Math.max(
                gameEngine.toScreenPixels(220.0f),
                Math.max(scoreWidth + horizontalPadding * 2.0f, (float) this.zones.length * gameEngine.toScreenPixels(62.0f) + horizontalPadding * 2.0f)
        );
        float panelWidth = Math.min(desiredWidth, Math.max(gameEngine.toScreenPixels(180.0f), availableWidth - margin * 2.0f));
        float panelLeft = Math.max(margin, (availableWidth - panelWidth) * 0.5f);
        float panelTop = margin;
        float panelHeight = verticalPadding * 2.0f + titleHeight + scoreHeight + zoneStripHeight + rowGap * 2.0f;

        drawRect(gameEngine, panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, this.hudBackgroundPaint);
        drawRect(gameEngine, panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, this.hudBorderPaint);
        float centerX = panelLeft + panelWidth * 0.5f;
        float titleBaseline = panelTop + verticalPadding + titleHeight * 0.82f;
        float scoreBaseline = panelTop + verticalPadding + titleHeight + rowGap + scoreHeight * 0.82f;
        gameEngine.renderGraphicsEngine.a("Area Control", centerX, titleBaseline, this.hudTitlePaint);
        drawScoreSummary(gameEngine, centerX, scoreBaseline);

        float zoneTop = panelTop + verticalPadding + titleHeight + scoreHeight + rowGap * 2.0f;
        drawZoneStrip(gameEngine, panelLeft + horizontalPadding, zoneTop, panelWidth - horizontalPadding * 2.0f, zoneStripHeight);
    }

    private void drawWorldZones(GameEngine gameEngine) {
        float zoom = gameEngine.zoom;
        float screenWidth = gameEngine.currentScreenWidthPixels;
        float screenHeight = gameEngine.currentScreenHeightPixels;
        for (ControlZone zone : this.zones) {
            float left = (zone.object.tileRect.a - gameEngine.viewpointXSnapped) * zoom;
            float top = (zone.object.tileRect.b - gameEngine.viewpointYSnapped) * zoom;
            float right = (zone.object.tileRect.c - gameEngine.viewpointXSnapped) * zoom;
            float bottom = (zone.object.tileRect.d - gameEngine.viewpointYSnapped) * zoom;
            if (right < 0.0f || bottom < 0.0f || left > screenWidth || top > screenHeight) {
                continue;
            }

            int displayGroup = zone.displayGroup();
            int fillColor = zone.contested ? argb(42, 240, 184, 64) : colorForAllyGroup(displayGroup, zone.ownerAllyGroup == -1 ? 34 : 46);
            int borderColor = zone.contested ? argb(230, 255, 205, 72) : colorForAllyGroup(displayGroup, 220);
            this.worldZoneFillPaint.b(fillColor);
            this.worldZoneBorderPaint.b(borderColor);
            this.worldZoneRect.a(left, top, right, bottom);
            if (zone.circle) {
                float centerX = (left + right) * 0.5f;
                float centerY = (top + bottom) * 0.5f;
                float radius = Math.min(right - left, bottom - top) * 0.5f;
                gameEngine.renderGraphicsEngine.a(centerX, centerY, radius, this.worldZoneFillPaint);
                gameEngine.renderGraphicsEngine.a(centerX, centerY, radius, this.worldZoneBorderPaint);
                if (zone.targetAllyGroup != -1 && zone.captureFrames > 0.0f) {
                    float progress = Math.max(0.0f, Math.min(1.0f, zone.captureProgress / zone.captureFrames));
                    this.worldZoneProgressRingPaint.b(colorForAllyGroup(zone.targetAllyGroup, 235));
                    drawCircleProgress(gameEngine, centerX, centerY, radius, progress, this.worldZoneProgressRingPaint);
                }
            } else {
                gameEngine.renderGraphicsEngine.a(this.worldZoneRect, this.worldZoneFillPaint);
                gameEngine.renderGraphicsEngine.a(this.worldZoneRect, this.worldZoneBorderPaint);
                if (zone.targetAllyGroup != -1 && zone.captureFrames > 0.0f) {
                    float progress = Math.max(0.0f, Math.min(1.0f, zone.captureProgress / zone.captureFrames));
                    float barHeight = Math.max(4.0f, Math.min(8.0f, (bottom - top) * 0.04f));
                    this.worldZoneProgressPaint.b(colorForAllyGroup(zone.targetAllyGroup, 205));
                    this.worldZoneRect.a(left, bottom - barHeight, left + ((right - left) * progress), bottom);
                    gameEngine.renderGraphicsEngine.a(this.worldZoneRect, this.worldZoneProgressPaint);
                }
            }

            String label = zone.contested ? zone.id + " contested" : zone.shortHudLabel();
            gameEngine.renderGraphicsEngine.a(label, (left + right) * 0.5f, (top + bottom) * 0.5f, this.worldZoneLabelPaint);
        }
    }

    private void drawCircleProgress(GameEngine gameEngine, float centerX, float centerY, float radius, float progress, KoolPaint paint) {
        if (progress <= 0.0f || radius <= 0.0f) {
            return;
        }
        int segments = Math.max(12, Math.min(72, (int) (radius / 4.0f)));
        float sweep = progress * 360.0f;
        float previousX = centerX;
        float previousY = centerY - radius;
        int steps = Math.max(1, (int) Math.ceil((double) segments * (double) progress));
        for (int i = 1; i <= steps; i++) {
            float angle = -90.0f + Math.min(sweep, (sweep * (float) i) / (float) steps);
            double radians = Math.toRadians((double) angle);
            float x = centerX + ((float) Math.cos(radians) * radius);
            float y = centerY + ((float) Math.sin(radians) * radius);
            gameEngine.renderGraphicsEngine.a(previousX, previousY, x, y, paint);
            previousX = x;
            previousY = y;
        }
    }

    private void drawZoneStrip(GameEngine gameEngine, float left, float top, float width, float height) {
        if (this.zones.length == 0) {
            return;
        }
        float gap = gameEngine.toScreenPixels(2.0f);
        float cellWidth = (width - gap * (float) (this.zones.length - 1)) / (float) this.zones.length;
        boolean showCellText = cellWidth >= 44.0f;
        for (int i = 0; i < this.zones.length; i++) {
            ControlZone zone = this.zones[i];
            float cellLeft = left + (cellWidth + gap) * (float) i;
            float cellRight = cellLeft + cellWidth;
            int ownerColor = zone.contested ? argb(150, 240, 184, 64) : colorForAllyGroup(zone.ownerAllyGroup, 150);
            this.hudCellPaint.b(ownerColor);
            drawRect(gameEngine, cellLeft, top, cellRight, top + height, this.hudCellPaint);
            this.hudAccentPaint.b(zone.contested ? argb(230, 255, 205, 72) : colorForAllyGroup(zone.ownerAllyGroup, 230));
            drawRect(gameEngine, cellLeft, top, cellRight, top + height, this.hudAccentPaint);

            if (zone.ownerAllyGroup == -1 && zone.targetAllyGroup != -1 && zone.captureFrames > 0.0f) {
                float progress = Math.max(0.0f, Math.min(1.0f, zone.captureProgress / zone.captureFrames));
                this.hudCellPaint.b(colorForAllyGroup(zone.targetAllyGroup, 210));
                float progressHeight = Math.max(2.0f, gameEngine.toScreenPixels(2.0f));
                drawRect(gameEngine, cellLeft, top + height - progressHeight, cellLeft + cellWidth * progress, top + height, this.hudCellPaint);
            }

            if (showCellText) {
                gameEngine.renderGraphicsEngine.a(zone.shortHudLabel(), (cellLeft + cellRight) * 0.5f, top + (height + this.hudMutedTextPaint.k()) * 0.5f - gameEngine.toScreenPixels(1.0f), this.hudMutedTextPaint);
            }
        }
    }

    private void ensureHudPaints(GameEngine gameEngine, KoolPaint titlePaint, KoolPaint detailPaint) {
        if (this.hudBackgroundPaint == null) {
            this.hudBackgroundPaint = new KoolPaint();
            this.hudBorderPaint = new KoolPaint();
            this.hudTitlePaint = new KoolPaint();
            this.hudTextPaint = new KoolPaint();
            this.hudMutedTextPaint = new KoolPaint();
            this.hudAccentPaint = new KoolPaint();
            this.hudCellPaint = new KoolPaint();
            this.worldZoneFillPaint = new KoolPaint();
            this.worldZoneBorderPaint = new KoolPaint();
            this.worldZoneLabelPaint = new KoolPaint();
            this.worldZoneProgressPaint = new KoolPaint();
            this.worldZoneProgressRingPaint = new KoolPaint();
        }
        this.hudBackgroundPaint.a(180, 18, 18, 18);
        this.hudBackgroundPaint.a(KoolPaint.Style.FILL);
        this.hudBorderPaint.a(210, 151, 188, 98);
        this.hudBorderPaint.a(KoolPaint.Style.STROKE);
        this.hudBorderPaint.a(1.0f);
        this.hudTitlePaint.a(titlePaint);
        this.hudTitlePaint.a(KoolPaint.Align.CENTER);
        this.hudTitlePaint.a(255, 232, 242, 220);
        gameEngine.setScaledTextSize(this.hudTitlePaint, 13.0f);
        this.hudTextPaint.a(detailPaint);
        this.hudTextPaint.a(KoolPaint.Align.LEFT);
        this.hudTextPaint.a(255, 255, 255, 255);
        gameEngine.setScaledTextSize(this.hudTextPaint, 10.0f);
        this.hudMutedTextPaint.a(detailPaint);
        this.hudMutedTextPaint.a(KoolPaint.Align.CENTER);
        this.hudMutedTextPaint.a(255, 230, 230, 230);
        gameEngine.setScaledTextSize(this.hudMutedTextPaint, 8.0f);
        this.hudAccentPaint.a(KoolPaint.Style.STROKE);
        this.hudAccentPaint.a(1.0f);
        this.hudCellPaint.a(KoolPaint.Style.FILL);
        this.worldZoneFillPaint.a(KoolPaint.Style.FILL);
        this.worldZoneBorderPaint.a(KoolPaint.Style.STROKE);
        this.worldZoneBorderPaint.a(2.0f);
        this.worldZoneLabelPaint.a(detailPaint);
        this.worldZoneLabelPaint.a(KoolPaint.Align.CENTER);
        this.worldZoneLabelPaint.a(255, 245, 246, 241);
        gameEngine.setScaledTextSize(this.worldZoneLabelPaint, 11.0f);
        this.worldZoneProgressPaint.a(KoolPaint.Style.FILL);
        this.worldZoneProgressRingPaint.a(KoolPaint.Style.STROKE);
        this.worldZoneProgressRingPaint.a(4.0f);
    }

    private float measureScoreSummaryWidth(GameEngine gameEngine) {
        TreeMap<Integer, Integer> sortedScores = scoresForDisplay();
        float width = 0.0f;
        float gap = gameEngine.toScreenPixels(8.0f);
        for (Map.Entry<Integer, Integer> entry : sortedScores.entrySet()) {
            if (width > 0.0f) {
                width += gap;
            }
            width += gameEngine.renderGraphicsEngine.b(scoreLabel(entry), this.hudTextPaint);
        }
        if (width > 0.0f) {
            width += gap;
        }
        return width + gameEngine.renderGraphicsEngine.b("/ " + this.scoreLimit, this.hudTextPaint);
    }

    private void drawScoreSummary(GameEngine gameEngine, float centerX, float baseline) {
        TreeMap<Integer, Integer> sortedScores = scoresForDisplay();
        float totalWidth = measureScoreSummaryWidth(gameEngine);
        float gap = gameEngine.toScreenPixels(8.0f);
        float x = centerX - totalWidth * 0.5f;
        for (Map.Entry<Integer, Integer> entry : sortedScores.entrySet()) {
            String label = scoreLabel(entry);
            this.hudTextPaint.b(colorForAllyGroup(entry.getKey().intValue(), 255));
            gameEngine.renderGraphicsEngine.a(label, x, baseline, this.hudTextPaint);
            x += gameEngine.renderGraphicsEngine.b(label, this.hudTextPaint) + gap;
        }
        this.hudTextPaint.a(255, 232, 242, 220);
        gameEngine.renderGraphicsEngine.a("/ " + this.scoreLimit, x, baseline, this.hudTextPaint);
    }

    private TreeMap<Integer, Integer> scoresForDisplay() {
        TreeMap<Integer, Integer> sortedScores = new TreeMap<Integer, Integer>(this.scores);
        for (ControlZone zone : this.zones) {
            if (zone.ownerAllyGroup != -1) {
                sortedScores.put(Integer.valueOf(zone.ownerAllyGroup), Integer.valueOf(getScore(zone.ownerAllyGroup)));
            }
            if (zone.targetAllyGroup != -1) {
                sortedScores.put(Integer.valueOf(zone.targetAllyGroup), Integer.valueOf(getScore(zone.targetAllyGroup)));
            }
        }
        PlayerTeam localTeam = GameEngine.getInstance().playerTeam;
        if (sortedScores.isEmpty() && localTeam != null && !localTeam.isSpectatorTeamColor()) {
            sortedScores.put(Integer.valueOf(localTeam.teamId), Integer.valueOf(0));
        }
        return sortedScores;
    }

    private String scoreLabel(Map.Entry<Integer, Integer> entry) {
        return teamLabel(entry.getKey().intValue()) + ": " + entry.getValue().intValue();
    }

    private void drawRect(GameEngine gameEngine, float left, float top, float right, float bottom, KoolPaint paint) {
        this.hudRect.a(left, top, right, bottom);
        gameEngine.renderGraphicsEngine.a(this.hudRect, paint);
    }

    public void writeState(GameOutputStream stream) throws IOException {
        stream.writeBoolean(this.ended);
        stream.writeInt(this.winningAllyGroup);
        stream.writeInt(this.scores.size());
        TreeMap<Integer, Integer> sortedScores = new TreeMap<Integer, Integer>(this.scores);
        for (Map.Entry<Integer, Integer> entry : sortedScores.entrySet()) {
            stream.writeInt(entry.getKey().intValue());
            stream.writeInt(entry.getValue().intValue());
        }
        stream.writeInt(this.zones.length);
        for (ControlZone zone : this.zones) {
            zone.writeState(stream);
        }
    }

    public void writeSnapshot(GameOutputStream stream) throws IOException {
        stream.writeBoolean(this.ended);
        stream.writeInt(this.winningAllyGroup);
        stream.writeInt(this.scores.size());
        TreeMap<Integer, Integer> sortedScores = new TreeMap<Integer, Integer>(this.scores);
        for (Map.Entry<Integer, Integer> entry : sortedScores.entrySet()) {
            stream.writeInt(entry.getKey().intValue());
            stream.writeInt(entry.getValue().intValue());
        }
        stream.writeInt(this.scoreLimit);
        stream.writeInt(this.zones.length);
        for (ControlZone zone : this.zones) {
            zone.writeSnapshot(stream);
        }
    }

    public static AreaControlMode readSnapshot(GameInputStream stream, MapObjectLayer layer) throws IOException {
        boolean ended = stream.readBoolean();
        int winningAllyGroup = stream.readInt();
        HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
        int scoreCount = stream.readInt();
        for (int i = 0; i < scoreCount; i++) {
            scores.put(Integer.valueOf(stream.readInt()), Integer.valueOf(stream.readInt()));
        }
        int scoreLimit = stream.readInt();
        int zoneCount = stream.readInt();
        MapObjectLayer targetLayer = ensureSnapshotObjectLayer(layer);
        removeObjectsOfType(targetLayer, CONTROL_ZONE_TYPE);
        ControlZone[] zones = new ControlZone[zoneCount];
        for (int i = 0; i < zoneCount; i++) {
            zones[i] = ControlZone.readSnapshot(stream, i, targetLayer);
        }
        AreaControlMode mode = new AreaControlMode(scoreLimit, zones);
        mode.ended = ended;
        mode.winningAllyGroup = winningAllyGroup;
        mode.scores.clear();
        mode.scores.putAll(scores);
        return mode;
    }

    public void readState(GameInputStream stream) throws IOException {
        this.ended = stream.readBoolean();
        this.winningAllyGroup = stream.readInt();
        this.scores.clear();
        int scoreCount = stream.readInt();
        for (int i = 0; i < scoreCount; i++) {
            this.scores.put(Integer.valueOf(stream.readInt()), Integer.valueOf(stream.readInt()));
        }
        int zoneCount = stream.readInt();
        for (int i = 0; i < zoneCount; i++) {
            if (i < this.zones.length) {
                this.zones[i].readState(stream);
            } else {
                ControlZone.skipState(stream);
            }
        }
    }

    private static MapObjectLayer ensureSnapshotObjectLayer(MapObjectLayer layer) throws IOException {
        if (layer != null) {
            return layer;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null || gameEngine.tileMap == null) {
            throw new IOException("No map loaded for RWX area snapshot");
        }
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element group = document.createElement("objectgroup");
            group.setAttribute("name", "Triggers");
            gameEngine.tileMap.objectsLayer = new MapObjectLayer(group, gameEngine.tileMap);
            return gameEngine.tileMap.objectsLayer;
        } catch (Exception e) {
            throw new IOException("Failed to create RWX area snapshot object layer", e);
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

    private static MapObject createSnapshotObject(MapObjectLayer layer, String id, String type, float x, float y, float width, float height, Map<String, String> properties) throws IOException {
        try {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine == null || gameEngine.tileMap == null) {
                throw new IOException("No map loaded for RWX snapshot object");
            }
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element object = document.createElement("object");
            object.setAttribute("id", String.valueOf(12000 + (layer.mapObjects == null ? 0 : layer.mapObjects.size())));
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
            throw new IOException("Failed to create RWX area snapshot object", e);
        }
    }

    public static void skipState(GameInputStream stream) throws IOException {
        stream.readBoolean();
        stream.readInt();
        int scoreCount = stream.readInt();
        for (int i = 0; i < scoreCount; i++) {
            stream.readInt();
            stream.readInt();
        }
        int zoneCount = stream.readInt();
        for (int i = 0; i < zoneCount; i++) {
            ControlZone.skipState(stream);
        }
    }

    private void addScore(int allyGroup, int amount) {
        if (allyGroup < 0 || amount <= 0 || this.ended) {
            return;
        }
        int newScore = getScore(allyGroup) + amount;
        this.scores.put(Integer.valueOf(allyGroup), Integer.valueOf(newScore));
        announceScoreProgress(allyGroup, newScore);
        if (newScore >= this.scoreLimit) {
            this.ended = true;
            this.winningAllyGroup = allyGroup;
            postAreaMessage(teamLabel(allyGroup) + " wins by area control", true);
            endGameForWinner(allyGroup);
        }
    }

    private int getScore(int allyGroup) {
        Integer value = this.scores.get(Integer.valueOf(allyGroup));
        return value == null ? 0 : value.intValue();
    }

    private void ensureScoreEntry(int allyGroup) {
        if (allyGroup >= 0 && !this.scores.containsKey(Integer.valueOf(allyGroup))) {
            this.scores.put(Integer.valueOf(allyGroup), Integer.valueOf(0));
        }
    }

    private void announceStartIfNeeded() {
        if (this.announcedStart) {
            return;
        }
        this.announcedStart = true;
        postAreaMessage("Area Control active: hold zones to reach " + this.scoreLimit, true);
    }

    private void announceScoreProgress(int allyGroup, int score) {
        int milestone = 0;
        if (score * 10 >= this.scoreLimit * 9) {
            milestone = 90;
        } else if (score * 4 >= this.scoreLimit * 3) {
            milestone = 75;
        } else if (score * 2 >= this.scoreLimit) {
            milestone = 50;
        }
        if (milestone == 0) {
            return;
        }
        Integer announced = this.announcedScoreMilestones.get(Integer.valueOf(allyGroup));
        if (announced != null && announced.intValue() >= milestone) {
            return;
        }
        this.announcedScoreMilestones.put(Integer.valueOf(allyGroup), Integer.valueOf(milestone));
        String message = milestone >= 75
                ? teamLabel(allyGroup) + " is close to victory (" + score + "/" + this.scoreLimit + ")"
                : teamLabel(allyGroup) + " is leading (" + score + "/" + this.scoreLimit + ")";
        postAreaMessage(message, milestone >= 75);
    }

    private void onZoneContested(ControlZone zone) {
        postAreaMessage("Zone " + zone.id + " contested", true);
    }

    private void onZoneCaptureStarted(ControlZone zone, int allyGroup) {
        postAreaMessage(teamLabel(allyGroup) + " is capturing zone " + zone.id, false);
    }

    private void onZoneNeutralizeStarted(ControlZone zone, int allyGroup) {
        postAreaMessage(teamLabel(allyGroup) + " is attacking zone " + zone.id, false);
    }

    private void onZoneNeutralized(ControlZone zone, int allyGroup) {
        postAreaMessage("Zone " + zone.id + " neutralized by " + teamLabel(allyGroup), true);
    }

    private void onZoneCaptured(ControlZone zone, int allyGroup) {
        postAreaMessage(teamLabel(allyGroup) + " captured zone " + zone.id, true);
    }

    private void postAreaMessage(String message, boolean priority) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null || gameEngine.gameUI == null) {
            return;
        }
        if (priority) {
            gameEngine.gameUI.showMediumPriorityMessage(message);
        }
        if (gameEngine.gameUI.warLogDisplay != null) {
            gameEngine.gameUI.warLogDisplay.a(message, priority ? 4500 : 3000);
        }
    }

    private void endGameForWinner(int allyGroup) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null || gameEngine.gameUI == null) {
            return;
        }
        PlayerTeam localTeam = gameEngine.playerTeam;
        if (localTeam != null && !localTeam.isSpectatorTeamColor() && localTeam.teamId == allyGroup) {
            gameEngine.gameUI.startGameEndSequence();
        } else {
            gameEngine.gameUI.endGameSequence();
        }
    }

    private String getScoreSummary() {
        TreeMap<Integer, Integer> sortedScores = scoresForDisplay();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : sortedScores.entrySet()) {
            if (builder.length() > 0) {
                builder.append("  ");
            }
            builder.append(scoreLabel(entry));
        }
        builder.append(" / ");
        builder.append(this.scoreLimit);
        return builder.toString();
    }

    private String getZoneSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("Zones: ");
        for (int i = 0; i < this.zones.length; i++) {
            if (i > 0) {
                builder.append("  ");
            }
            ControlZone zone = this.zones[i];
            builder.append(zone.id);
            builder.append("=");
            builder.append(ownerLabel(zone.ownerAllyGroup));
            if (zone.ownerAllyGroup == -1 && zone.targetAllyGroup != -1 && zone.captureFrames > 0.0f) {
                int percent = (int) ((zone.captureProgress / zone.captureFrames) * 100.0f);
                builder.append("(");
                builder.append(ownerLabel(zone.targetAllyGroup));
                builder.append(" ");
                builder.append(percent);
                builder.append("%)");
            }
        }
        return builder.toString();
    }

    private static String ownerLabel(int allyGroup) {
        return allyGroup == -1 ? "N" : String.valueOf(allyGroup);
    }

    private static String teamLabel(int allyGroup) {
        if (allyGroup == -1) {
            return "Neutral";
        }
        PlayerTeam team = playerTeamForGroup(allyGroup);
        if (team != null) {
            if (team.teamName != null && team.teamName.trim().length() > 0) {
                return team.teamName.trim();
            }
            String colorName = team.getTeamColorDisplayName();
            if (colorName != null && colorName.trim().length() > 0) {
                return colorName.trim();
            }
        }
        return "Player " + (allyGroup + 1);
    }

    private static int colorForAllyGroup(int allyGroup, int alpha) {
        PlayerTeam team = playerTeamForGroup(allyGroup);
        if (team != null) {
            int color = team.getTeamSlotColorArgb();
            return ((alpha & 255) << 24) | (color & 16777215);
        }
        switch (allyGroup) {
            case 0:
                return argb(alpha, 76, 175, 80);
            case 1:
                return argb(alpha, 230, 69, 69);
            case 2:
                return argb(alpha, 92, 138, 237);
            case 3:
                return argb(alpha, 226, 197, 65);
            case 4:
                return argb(alpha, 70, 197, 197);
            case 5:
                return argb(alpha, 238, 241, 244);
            case 6:
                return argb(alpha, 138, 143, 150);
            case 7:
                return argb(alpha, 230, 103, 176);
            case 8:
                return argb(alpha, 232, 146, 60);
            case 9:
                return argb(alpha, 155, 107, 208);
            default:
                return argb(alpha, 88, 88, 88);
        }
    }

    private static PlayerTeam playerTeamForGroup(int allyGroup) {
        if (allyGroup < 0 || allyGroup >= PlayerTeam.TEAM_ENEMIES) {
            return null;
        }
        return PlayerTeam.k(allyGroup);
    }

    private static int argb(int alpha, int red, int green, int blue) {
        return ((alpha & 255) << 24) | ((red & 255) << 16) | ((green & 255) << 8) | (blue & 255);
    }

    private static Element findOrCreateTriggers(Document document, Element root) {
        NodeList groups = root.getElementsByTagName("objectgroup");
        for (int i = 0; i < groups.getLength(); i++) {
            Element group = (Element) groups.item(i);
            if ("Triggers".equalsIgnoreCase(group.getAttribute("name"))) {
                return group;
            }
        }
        Element group = document.createElement("objectgroup");
        group.setAttribute("name", "Triggers");
        root.appendChild(group);
        return group;
    }

    private static Element findOrCreateMapInfo(Document document, Element triggers, Element root) {
        NodeList objects = triggers.getElementsByTagName("object");
        for (int i = 0; i < objects.getLength(); i++) {
            Element object = (Element) objects.item(i);
            if ("map_info".equalsIgnoreCase(object.getAttribute("name"))) {
                return object;
            }
        }
        Element mapInfo = document.createElement("object");
        int id = nextObjectId(root);
        mapInfo.setAttribute("id", String.valueOf(id));
        root.setAttribute("nextobjectid", String.valueOf(id + 1));
        mapInfo.setAttribute("name", "map_info");
        mapInfo.setAttribute("x", "40");
        mapInfo.setAttribute("y", "40");
        mapInfo.setAttribute("width", "203");
        mapInfo.setAttribute("height", "122");
        triggers.insertBefore(mapInfo, triggers.getFirstChild());
        return mapInfo;
    }

    private static int nextObjectId(Element root) {
        String value = root.getAttribute("nextobjectid");
        if (value != null && value.length() > 0) {
            try {
                return Math.max(1, Integer.parseInt(value));
            } catch (NumberFormatException ignored) {
            }
        }
        int maxId = 0;
        NodeList objects = root.getElementsByTagName("object");
        for (int i = 0; i < objects.getLength(); i++) {
            Element object = (Element) objects.item(i);
            String id = object.getAttribute("id");
            if (id != null && id.length() > 0) {
                try {
                    maxId = Math.max(maxId, Integer.parseInt(id));
                } catch (NumberFormatException ignored) {
                }
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

    private static void removeProperty(Element object, String name) {
        NodeList propertyGroups = object.getElementsByTagName("properties");
        for (int groupIndex = propertyGroups.getLength() - 1; groupIndex >= 0; groupIndex--) {
            Element properties = (Element) propertyGroups.item(groupIndex);
            NodeList entries = properties.getElementsByTagName("property");
            for (int i = entries.getLength() - 1; i >= 0; i--) {
                Element entry = (Element) entries.item(i);
                if (name.equalsIgnoreCase(entry.getAttribute("name"))) {
                    entry.getParentNode().removeChild(entry);
                }
            }
            if (!properties.hasChildNodes()) {
                properties.getParentNode().removeChild(properties);
            }
        }
    }

    private static void appendProperty(Document document, Element properties, String name, String value) {
        Element property = document.createElement("property");
        property.setAttribute("name", name);
        property.setAttribute("value", value);
        properties.appendChild(property);
    }

    private static void setObjectProperty(MapObject object, String name, String value) {
        if (object.properties == null) {
            object.properties = new java.util.Properties();
        }
        object.properties.setProperty(name, value);
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

    private static String trimFloat(float value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
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

    private static int readIntProperty(MapObject object, String key, int defaultValue) throws MapLoadException {
        String value = readProperty(object, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new MapLoadException(object.getTriggerTag() + " property '" + key + "' expected integer, got: " + value, e);
        }
    }

    private static float readFloatProperty(MapObject object, String key, float defaultValue) throws MapLoadException {
        String value = readProperty(object, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new MapLoadException(object.getTriggerTag() + " property '" + key + "' expected number, got: " + value, e);
        }
    }

    private static float readSecondsProperty(MapObject object, String key, float defaultSeconds) throws MapLoadException {
        String value = readProperty(object, key);
        if (value == null) {
            return defaultSeconds * 60.0f;
        }
        if (value.endsWith("s") || value.endsWith("S")) {
            value = value.substring(0, value.length() - 1).trim();
        }
        try {
            float seconds = Float.parseFloat(value);
            if (seconds <= 0.0f) {
                throw new MapLoadException(object.getTriggerTag() + " property '" + key + "' must FastArrayList greater than 0");
            }
            return seconds * 60.0f;
        } catch (NumberFormatException e) {
            throw new MapLoadException(object.getTriggerTag() + " property '" + key + "' expected seconds, got: " + value, e);
        }
    }

    private static boolean readBooleanProperty(MapObject object, String key, boolean defaultValue) {
        String value = readProperty(object, key);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }

    private static boolean isCircleZone(MapObject object) {
        String shape = readProperty(object, "shape");
        if (shape != null && ("circle".equalsIgnoreCase(shape) || "ellipse".equalsIgnoreCase(shape))) {
            return true;
        }
        return readBooleanProperty(object, "circle", false);
    }

    public static final class EditorZoneProperties {
        public String id;
        public boolean circle;
        public float x;
        public float y;
        public float width;
        public float height;
        public float captureTimeSeconds;
        public float neutralizeTimeSeconds;
        public int scoreRate;
        public float scoreIntervalSeconds;
        public boolean groundOnly;
        public float maxCaptureWeight;
        public int startingOwner;

        private static EditorZoneProperties fromZone(ControlZone zone) {
            EditorZoneProperties properties = new EditorZoneProperties();
            properties.id = zone.id;
            properties.circle = zone.circle;
            properties.x = zone.object.tileRect.a;
            properties.y = zone.object.tileRect.b;
            properties.width = zone.object.tileRect.b();
            properties.height = zone.object.tileRect.c();
            properties.captureTimeSeconds = zone.captureFrames / 60.0f;
            properties.neutralizeTimeSeconds = zone.neutralizeFrames / 60.0f;
            properties.scoreRate = zone.scoreRate;
            properties.scoreIntervalSeconds = zone.scoreIntervalFrames / 60.0f;
            properties.groundOnly = zone.groundOnly;
            properties.maxCaptureWeight = zone.maxCaptureWeight;
            properties.startingOwner = readStartingOwner(zone);
            return properties;
        }

        private static int readStartingOwner(ControlZone zone) {
            try {
                return readOwnerProperty(zone.object, "startingOwner", -1);
            } catch (MapLoadException ignored) {
                return zone.ownerAllyGroup;
            }
        }
    }

    private static int readOwnerProperty(MapObject object, String key, int defaultValue) throws MapLoadException {
        String value = readProperty(object, key);
        if (value == null) {
            return defaultValue;
        }
        if ("neutral".equalsIgnoreCase(value) || "none".equalsIgnoreCase(value)) {
            return -1;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new MapLoadException(object.getTriggerTag() + " property '" + key + "' expected team id, got: " + value, e);
        }
    }

    private static final class CapturePresence {
        int allyGroup = -1;
        float weight = 0.0f;
        boolean contested;
    }

    private static final class ControlZone {
        final String id;
        final MapObject object;
        final float captureFrames;
        final float neutralizeFrames;
        final int scoreRate;
        final float scoreIntervalFrames;
        final boolean groundOnly;
        final float maxCaptureWeight;
        final boolean circle;

        int ownerAllyGroup;
        int targetAllyGroup = -1;
        float captureProgress;
        float scoreTimer;
        boolean contested;

        ControlZone(String id, MapObject object, float captureFrames, float neutralizeFrames, int scoreRate, float scoreIntervalFrames, boolean groundOnly, float maxCaptureWeight, boolean circle, int startingOwner) {
            this.id = id;
            this.object = object;
            this.captureFrames = captureFrames;
            this.neutralizeFrames = neutralizeFrames;
            this.scoreRate = scoreRate;
            this.scoreIntervalFrames = scoreIntervalFrames;
            this.groundOnly = groundOnly;
            this.maxCaptureWeight = maxCaptureWeight;
            this.circle = circle;
            this.ownerAllyGroup = startingOwner;
            this.captureProgress = startingOwner == -1 ? 0.0f : captureFrames;
            this.scoreTimer = scoreIntervalFrames;
        }

        static ControlZone fromMapObject(MapObject object, int index, float defaultScoreIntervalFrames) throws MapLoadException {
            String id = readProperty(object, "id");
            if (id == null) {
                id = object.name;
            }
            if (id == null || id.trim().length() == 0) {
                id = "zone" + (index + 1);
            }
            float captureFrames = readSecondsProperty(object, "captureTime", 20.0f);
            float neutralizeFrames = readSecondsProperty(object, "neutralizeTime", 10.0f);
            int scoreRate = readIntProperty(object, "scoreRate", 1);
            float scoreIntervalFrames = readSecondsProperty(object, "scoreInterval", defaultScoreIntervalFrames / 60.0f);
            boolean groundOnly = readBooleanProperty(object, "groundOnly", true);
            float maxCaptureWeight = readFloatProperty(object, "maxCaptureWeight", 5.0f);
            boolean circle = isCircleZone(object);
            int startingOwner = readOwnerProperty(object, "startingOwner", -1);
            if (scoreRate < 0) {
                throw new MapLoadException(object.getTriggerTag() + " property 'scoreRate' must FastArrayList 0 or greater");
            }
            if (maxCaptureWeight <= 0.0f) {
                throw new MapLoadException(object.getTriggerTag() + " property 'maxCaptureWeight' must FastArrayList greater than 0");
            }
            return new ControlZone(id, object, captureFrames, neutralizeFrames, scoreRate, scoreIntervalFrames, groundOnly, maxCaptureWeight, circle, startingOwner);
        }

        void update(float delta, AreaControlMode mode) {
            CapturePresence presence = getCapturePresence();
            boolean wasContested = this.contested;
            this.contested = presence.contested;
            if (presence.contested) {
                if (!wasContested) {
                    mode.onZoneContested(this);
                }
                return;
            }
            if (presence.allyGroup != -1) {
                updateCapture(delta, presence, mode);
            } else if (this.ownerAllyGroup != -1) {
                this.targetAllyGroup = -1;
                this.captureProgress = this.captureFrames;
            }
            updateScoring(delta, mode);
        }

        private void updateCapture(float delta, CapturePresence presence, AreaControlMode mode) {
            int capturingGroup = presence.allyGroup;
            float weight = presence.weight;
            if (this.ownerAllyGroup == capturingGroup) {
                this.targetAllyGroup = -1;
                this.captureProgress = this.captureFrames;
                return;
            }
            if (this.ownerAllyGroup != -1) {
                if (this.targetAllyGroup != capturingGroup) {
                    mode.onZoneNeutralizeStarted(this, capturingGroup);
                }
                this.targetAllyGroup = capturingGroup;
                float neutralizeScale = this.captureFrames / this.neutralizeFrames;
                this.captureProgress -= delta * weight * neutralizeScale;
                if (this.captureProgress <= 0.0f) {
                    this.ownerAllyGroup = -1;
                    this.captureProgress = 0.0f;
                    this.scoreTimer = this.scoreIntervalFrames;
                    mode.ensureScoreEntry(capturingGroup);
                    mode.onZoneNeutralized(this, capturingGroup);
                }
                return;
            }
            if (this.targetAllyGroup != capturingGroup) {
                this.targetAllyGroup = capturingGroup;
                this.captureProgress = 0.0f;
                mode.onZoneCaptureStarted(this, capturingGroup);
            }
            this.captureProgress += delta * weight;
            if (this.captureProgress >= this.captureFrames) {
                this.ownerAllyGroup = capturingGroup;
                this.targetAllyGroup = -1;
                this.captureProgress = this.captureFrames;
                this.scoreTimer = this.scoreIntervalFrames;
                mode.ensureScoreEntry(capturingGroup);
                mode.onZoneCaptured(this, capturingGroup);
            }
        }

        private void updateScoring(float delta, AreaControlMode mode) {
            if (this.ownerAllyGroup == -1 || this.scoreRate <= 0) {
                return;
            }
            this.scoreTimer -= delta;
            while (this.scoreTimer <= 0.0f) {
                this.scoreTimer += this.scoreIntervalFrames;
                mode.addScore(this.ownerAllyGroup, this.scoreRate);
                if (mode.ended) {
                    return;
                }
            }
        }

        private CapturePresence getCapturePresence() {
            HashMap<Integer, Float> weightsByGroup = new HashMap<Integer, Float>();
            BaseUnit[] units = BaseUnit.bE.a();
            int unitCount = BaseUnit.bE.size();
            for (int i = 0; i < unitCount; i++) {
                BaseUnit unit = units[i];
                if (!isEligibleCaptureUnit(unit)) {
                    continue;
                }
                if (!containsUnit(unit)) {
                    continue;
                }
                Integer key = Integer.valueOf(unit.team.teamId);
                Float current = weightsByGroup.get(key);
                float newWeight = (current == null ? 0.0f : current.floatValue()) + 1.0f;
                if (newWeight > this.maxCaptureWeight) {
                    newWeight = this.maxCaptureWeight;
                }
                weightsByGroup.put(key, Float.valueOf(newWeight));
            }
            CapturePresence presence = new CapturePresence();
            for (Map.Entry<Integer, Float> entry : weightsByGroup.entrySet()) {
                if (entry.getValue().floatValue() <= 0.0f) {
                    continue;
                }
                if (presence.allyGroup != -1 && presence.allyGroup != entry.getKey().intValue()) {
                    presence.contested = true;
                    return presence;
                }
                presence.allyGroup = entry.getKey().intValue();
                presence.weight = entry.getValue().floatValue();
            }
            return presence;
        }

        private boolean containsUnit(BaseUnit unit) {
            if (!this.circle) {
                return this.object.containsUnitPosition(unit);
            }
            return containsPoint(unit.posX, unit.posY);
        }

        private boolean containsPoint(float x, float y) {
            if (!this.circle) {
                return this.object.tileRect.b(x, y);
            }
            float centerX = (this.object.tileRect.a + this.object.tileRect.c) * 0.5f;
            float centerY = (this.object.tileRect.b + this.object.tileRect.d) * 0.5f;
            float radius = Math.min(this.object.tileRect.b(), this.object.tileRect.c()) * 0.5f;
            float dx = x - centerX;
            float dy = y - centerY;
            return (dx * dx) + (dy * dy) <= radius * radius;
        }

        private boolean isEligibleCaptureUnit(BaseUnit unit) {
            if (!(unit instanceof OrderableUnit) || unit.isDead || !unit.isAlive() || unit.isExcludedFromDefeatCheck()) {
                return false;
            }
            if (unit.unitTransportTarget != null || unit.parentEntity != null || unit.team == null) {
                return false;
            }
            return isEligibleCaptureTeam(unit.team) && (!this.groundOnly || unit.h() != UnitMovementType.AIR);
        }

        private boolean isEligibleCaptureTeam(PlayerTeam team) {
            // Avoid isTeamSpectator here: that decompiled field is also set for AI teams in this codebase.
            return team != PlayerTeam.TEAM_ALL && team != PlayerTeam.TEAM_UNKNOWN && team.teamId >= 0 && !team.isSpectatorTeamColor();
        }

        int displayGroup() {
            if (this.ownerAllyGroup != -1) {
                return this.ownerAllyGroup;
            }
            if (this.targetAllyGroup != -1) {
                return this.targetAllyGroup;
            }
            return -1;
        }

        String shortHudLabel() {
            String label = this.id;
            if (label.length() > 6) {
                label = label.substring(0, 6);
            }
            if (this.contested) {
                return label + " !";
            }
            String owner = ownerLabel(this.ownerAllyGroup);
            if (this.ownerAllyGroup == -1 && this.targetAllyGroup != -1 && this.captureFrames > 0.0f) {
                int percent = (int) ((this.captureProgress / this.captureFrames) * 100.0f);
                return label + " " + ownerLabel(this.targetAllyGroup) + " " + percent + "%";
            }
            return label + " " + owner;
        }

        void writeState(GameOutputStream stream) throws IOException {
            stream.writeInt(this.ownerAllyGroup);
            stream.writeInt(this.targetAllyGroup);
            stream.writeFloat(this.captureProgress);
            stream.writeFloat(this.scoreTimer);
        }

        void writeSnapshot(GameOutputStream stream) throws IOException {
            stream.writeStringUTF(this.id);
            stream.writeFloat(this.object.x);
            stream.writeFloat(this.object.y);
            stream.writeFloat(this.object.width);
            stream.writeFloat(this.object.height);
            stream.writeBoolean(this.circle);
            stream.writeFloat(this.captureFrames / 60.0f);
            stream.writeFloat(this.neutralizeFrames / 60.0f);
            stream.writeInt(this.scoreRate);
            stream.writeFloat(this.scoreIntervalFrames / 60.0f);
            stream.writeBoolean(this.groundOnly);
            stream.writeFloat(this.maxCaptureWeight);
            int startingOwner = -1;
            try {
                startingOwner = readOwnerProperty(this.object, "startingOwner", -1);
            } catch (MapLoadException ignored) {
            }
            stream.writeInt(startingOwner);
            writeState(stream);
        }

        static ControlZone readSnapshot(GameInputStream stream, int index, MapObjectLayer layer) throws IOException {
            String id = stream.readUTF();
            float x = stream.readFloat();
            float y = stream.readFloat();
            float width = stream.readFloat();
            float height = stream.readFloat();
            boolean circle = stream.readBoolean();
            float captureTime = stream.readFloat();
            float neutralizeTime = stream.readFloat();
            int scoreRate = stream.readInt();
            float scoreInterval = stream.readFloat();
            boolean groundOnly = stream.readBoolean();
            float maxCaptureWeight = stream.readFloat();
            int startingOwner = stream.readInt();
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("id", id);
            if (circle) {
                properties.put("shape", "circle");
            }
            properties.put("captureTime", trimFloat(captureTime));
            properties.put("neutralizeTime", trimFloat(neutralizeTime));
            properties.put("scoreRate", String.valueOf(scoreRate));
            properties.put("scoreInterval", trimFloat(scoreInterval));
            properties.put("groundOnly", String.valueOf(groundOnly));
            properties.put("maxCaptureWeight", trimFloat(maxCaptureWeight));
            if (startingOwner != -1) {
                properties.put("startingOwner", String.valueOf(startingOwner));
            }
            MapObject object = createSnapshotObject(layer, id, CONTROL_ZONE_TYPE, x, y, width, height, properties);
            try {
                ControlZone zone = ControlZone.fromMapObject(object, index, 180.0f);
                zone.readState(stream);
                return zone;
            } catch (MapLoadException e) {
                throw new IOException("Failed to read RWX area zone snapshot", e);
            }
        }

        void readState(GameInputStream stream) throws IOException {
            this.ownerAllyGroup = stream.readInt();
            this.targetAllyGroup = stream.readInt();
            this.captureProgress = stream.readFloat();
            this.scoreTimer = stream.readFloat();
        }

        static void skipState(GameInputStream stream) throws IOException {
            stream.readInt();
            stream.readInt();
            stream.readFloat();
            stream.readFloat();
        }
    }
}
