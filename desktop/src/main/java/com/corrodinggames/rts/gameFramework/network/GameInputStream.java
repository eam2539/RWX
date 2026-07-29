package com.corrodinggames.rts.gameFramework.network;

import android.graphics.PointF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/k.class */
public class GameInputStream {

    /* JADX INFO: renamed from: a */
    ByteArrayInputStream byteArrayInput;

    /* JADX INFO: renamed from: e */
    private DataInputStream rootDataInput;

    /* JADX INFO: renamed from: f */
    private DataInputStream currentDataInput;

    /* JADX INFO: renamed from: g */
    private LinkedList inputBlockStack = new LinkedList();

    /* JADX INFO: renamed from: b */
    int protocolVersion = 999999;

    /* JADX INFO: renamed from: c */
    int streamVersion = 999999;

    /* JADX INFO: renamed from: d */
    int bytesRead = 0;

    /* JADX INFO: renamed from: a */
    void useMainStream() {
        this.currentDataInput = this.rootDataInput;
    }

    public GameInputStream(PacketData packet) {
        this.byteArrayInput = new ByteArrayInputStream(packet.bytes);
        this.rootDataInput = new DataInputStream(this.byteArrayInput);
        useMainStream();
    }

    public GameInputStream(DataInputStream dataInputStream) {
        this.rootDataInput = dataInputStream;
        useMainStream();
    }

    public GameInputStream(String str) {
        this.byteArrayInput = new ByteArrayInputStream(str.getBytes());
        this.rootDataInput = new DataInputStream(this.byteArrayInput);
        useMainStream();
    }

    public GameInputStream(byte[] bArr) {
        this.byteArrayInput = new ByteArrayInputStream(bArr);
        this.rootDataInput = new DataInputStream(this.byteArrayInput);
        useMainStream();
    }

    /* JADX INFO: renamed from: a */
    public void setProtocolVersion(int i) {
        this.protocolVersion = i;
    }

    /* JADX INFO: renamed from: b */
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    /* JADX INFO: renamed from: b */
    public void setStreamVersion(int i) {
        this.streamVersion = i;
    }

    /* JADX INFO: renamed from: c */
    public int getStreamVersion() {
        return this.streamVersion;
    }

    /* JADX INFO: renamed from: d */
    public byte readByte() throws IOException {
        return this.currentDataInput.readByte();
    }

    /* JADX INFO: renamed from: e */
    public boolean readBoolean() throws IOException {
        return this.currentDataInput.readBoolean();
    }

    /* JADX INFO: renamed from: f */
    public int readInt() throws IOException {
        return this.currentDataInput.readInt();
    }

    /* JADX INFO: renamed from: g */
    public float readFloat() throws IOException {
        return this.currentDataInput.readFloat();
    }

    /* JADX INFO: renamed from: h */
    public double readDouble() throws IOException {
        return this.currentDataInput.readDouble();
    }

    /* JADX INFO: renamed from: i */
    public long readLong() throws IOException {
        return this.currentDataInput.readLong();
    }

    /* JADX INFO: renamed from: j */
    public String readNullableString() throws IOException {
        if (!readBoolean()) {
            return null;
        }
        return readUTF();
    }

    /* JADX INFO: renamed from: k */
    public Integer readNullableInt() throws IOException {
        if (!readBoolean()) {
            return null;
        }
        return readInt();
    }

    /* JADX INFO: renamed from: l */
    public String readUTF() throws IOException {
        return this.currentDataInput.readUTF();
    }

    /* JADX INFO: renamed from: m */
    public AnimationTag readAnimationTag() throws IOException {
        String utf = this.currentDataInput.readUTF();
        if (utf.equals(VariableScope.nullOrMissingString)) {
            return null;
        }
        return AnimationTag.c(utf);
    }

    /* JADX INFO: renamed from: n */
    public long readUnitId() throws IOException {
        return this.currentDataInput.readLong();
    }

    /* JADX INFO: renamed from: a */
    public GameObject readGameObject(Class clazz) throws IOException {
        return GameObject.a(this.currentDataInput.readLong(), clazz, false);
    }

    /* JADX INFO: renamed from: a */
    public void readGameObjectList(FastArrayList fastArrayList, Class cls) throws IOException {
        int i = readInt();
        for (int i2 = 0; i2 < i; i2++) {
            GameObject gameObject = readGameObject(cls);
            if (gameObject != null) {
                fastArrayList.add(gameObject);
            }
        }
    }

    /* JADX INFO: renamed from: o */
    public BaseUnit readBaseUnit() throws IOException {
        return readBaseUnit(ConnectionStatus.WARN);
    }

    /* JADX INFO: renamed from: a */
    public BaseUnit readBaseUnit(ConnectionStatus connectionStatus) throws IOException {
        return GameObject.a(this.currentDataInput.readLong(), connectionStatus == ConnectionStatus.EXPECTED);
    }

    /* JADX INFO: renamed from: p */
    public OrderableUnit readOrderableUnit() throws IOException {
        return GameObject.b(this.currentDataInput.readLong(), false);
    }

    /* JADX INFO: renamed from: b */
    public Enum readEnumOrdinalOrNull(Class cls) throws IOException {
        int i = this.currentDataInput.readInt();
        if (i == -1) {
            return null;
        }
        Object[] enumConstants = cls.getEnumConstants();
        if (i < 0 || i >= enumConstants.length) {
            NetworkEngine.reportDesync("readEnum:" + i + " is out of range for " + cls.toString());
            return null;
        }
        return (Enum) enumConstants[i];
    }

    public UnitType q() throws IOException {
        int i = this.currentDataInput.readInt();
        if (i == -1) {
            return null;
        }
        if (i == -2) {
            String utf = readUTF();
            CustomUnitConfig customUnitConfigFindConfigByName = CustomUnitConfig.findConfigByName(utf);
            if (customUnitConfigFindConfigByName == null) {
                NetworkEngine.reportDesync("readUnitType: Could not find customUnitMetadata:" + utf);
            }
            UnitType unitTypeC = CustomUnitConfig.c(customUnitConfigFindConfigByName);
            if (unitTypeC != null) {
                if (unitTypeC instanceof CustomUnitConfig) {
                    customUnitConfigFindConfigByName = (CustomUnitConfig) unitTypeC;
                } else {
                    GameEngine.logColored("replacement not a custom unit:" + unitTypeC.getUnitTypeDescriptionShort());
                }
            }
            return customUnitConfigFindConfigByName;
        }
        Object[] enumConstants = UnitTypeEnum.class.getEnumConstants();
        if (i < 0 || i >= enumConstants.length) {
            NetworkEngine.reportDesync("readUnitType:" + i + " is out of range for UnitType");
            return null;
        }
        return (UnitTypeEnum) enumConstants[i];
    }

    /* JADX INFO: renamed from: r */
    public PlayerTeam readRequiredPlayerTeam() throws IOException {
        byte b = this.currentDataInput.readByte();
        PlayerTeam playerTeamK = PlayerTeam.k(b);
        if (playerTeamK == null) {
            throw new IOException("Error loading save data, could not find referenced team:" + ((int) b) + VariableScope.nullOrMissingString);
        }
        return playerTeamK;
    }

    /* JADX INFO: renamed from: s */
    public PlayerTeam readOptionalPlayerTeam() throws IOException {
        return PlayerTeam.k(this.currentDataInput.readByte());
    }

    /* JADX INFO: renamed from: t */
    public byte[] readBytesWithLength() throws IOException {
        int i;
        int i2 = 0;
        int i3 = readInt();
        byte[] bArr = new byte[i3];
        while (i2 < i3 && (i = this.currentDataInput.read(bArr, i2, i3 - i2)) != -1) {
            i2 += i;
        }
        return bArr;
    }

    /* JADX INFO: renamed from: u */
    public GameInputStream readNestedStream() throws IOException {
        return new GameInputStream(readBytesWithLength());
    }

    /* JADX INFO: renamed from: v */
    public short readShortValue() throws IOException {
        return this.currentDataInput.readShort();
    }

    public void a(String str) throws IOException {
        if (readShortValue() != 12345) {
            NetworkEngine.reportDesync("Mark wasn't read for:" + str);
            if (GameEngine.getInstance().isMissionActive()) {
                throw new RuntimeException("Mark wasn't read for:" + str);
            }
        }
    }

    /* JADX INFO: renamed from: w */
    public InputStream getActiveInputStream() {
        return this.currentDataInput;
    }

    /* JADX INFO: renamed from: b */
    public void startBlockNamed(String str) throws IOException {
        a(str, false);
    }

    /* JADX INFO: renamed from: x */
    public String startBlockAndGetName() throws IOException {
        return a(false, false);
    }

    public void a(String str, boolean z) throws IOException {
        a(str, z, false);
    }

    public void a(String str, boolean z, boolean z2) throws IOException {
        if (this.protocolVersion < 11) {
            GameEngine.log("Skipping start block:" + str);
            return;
        }
        String strA = a(z, z2);
        if (!strA.equals(str)) {
            GameEngine.log("InputNetStream:endBlock", "Name does not match: expected:" + str + " , got:" + strA);
        }
    }

    public byte[] c(String str) throws IOException {
        String utf = this.currentDataInput.readUTF();
        if (!utf.equals(str)) {
            GameEngine.log("getBlockRaw", "Name does not match: expected:" + str + " , got:" + utf);
        }
        return readBytesWithLength();
    }

    public String a(boolean z, boolean z2) throws IOException {
        if (this.protocolVersion < 11) {
            GameEngine.log("Skipping start block: startBlockAndGetName()");
            return "<skipped>";
        }
        String utf = this.currentDataInput.readUTF();
        BlockInput blockInput = new BlockInput(readBytesWithLength(), z, z2);
        blockInput.blockName = utf;
        this.inputBlockStack.add(blockInput);
        this.currentDataInput = ((BlockInput) this.inputBlockStack.getLast()).dataInput;
        return utf;
    }

    public void d(String str) {
        if (this.protocolVersion < 11) {
            GameEngine.log("Skipping end block:" + str);
            return;
        }
        BlockInput blockInput = (BlockInput) this.inputBlockStack.removeLast();
        if (!blockInput.blockName.equals(str)) {
            GameEngine.log("InputNetStream:endBlock", "Name does not match: expected" + str + " ," + blockInput.blockName);
        }
        if (this.inputBlockStack.isEmpty()) {
            this.currentDataInput = this.rootDataInput;
        } else {
            this.currentDataInput = ((BlockInput) this.inputBlockStack.getLast()).dataInput;
        }
    }

    public PointF y() throws IOException {
        if (!readBoolean()) {
            return null;
        }
        PointF pointF = new PointF();
        pointF.x = readFloat();
        pointF.y = readFloat();
        return pointF;
    }
}
