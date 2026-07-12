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
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.zip.DataFormatException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.as */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/as.class */
public class GameOutputStream {

    /* JADX INFO: renamed from: b */
    ByteArrayOutputStream rootByteBuffer;

    /* JADX INFO: renamed from: c */
    DataOutputStream rootDataOutput;

    /* JADX INFO: renamed from: a */
    private DataOutputStream currentDataOutput;

    /* JADX INFO: renamed from: e */
    private LinkedList outputBlockStack;

    /* JADX INFO: renamed from: d */
    public int maxBufferSize;

    /* JADX INFO: renamed from: a */
    public void flushAllBuffers() throws IOException {
        ListIterator listIterator = this.outputBlockStack.listIterator(this.outputBlockStack.size());
        while (listIterator.hasPrevious()) {
            ((StreamBlock) listIterator.previous()).flush();
        }
        this.rootDataOutput.flush();
        if (this.rootByteBuffer != null) {
            this.rootByteBuffer.flush();
        }
    }

    /* JADX INFO: renamed from: b */
    void useMainStream() {
        this.currentDataOutput = this.rootDataOutput;
    }

    public GameOutputStream(int i) {
        this();
        this.maxBufferSize = i;
    }

    public GameOutputStream() {
        this.outputBlockStack = new LinkedList();
        this.maxBufferSize = 999999;
        this.rootByteBuffer = new ByteArrayOutputStream();
        this.rootDataOutput = new DataOutputStream(this.rootByteBuffer);
        useMainStream();
    }

    public GameOutputStream(DataOutputStream dataOutputStream) {
        this.outputBlockStack = new LinkedList();
        this.maxBufferSize = 999999;
        this.rootDataOutput = dataOutputStream;
        useMainStream();
    }

    /* JADX INFO: renamed from: b */
    public PacketData buildPacketData(int packetType) {
        return buildPacketData(packetType, -1);
    }

    /* JADX INFO: renamed from: a */
    public PacketData buildPacketData(int packetType, int delayMills) {
        try {
            flushAllBuffers();
            PacketData packetData = new PacketData(packetType);
            packetData.bytes = this.rootByteBuffer.toByteArray();
            packetData.delayMillis = delayMills;
            return packetData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: c */
    public String getBufferAsString() {
        try {
            flushAllBuffers();
            return this.rootByteBuffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: d */
    public byte[] toByteArray() {
        try {
            flushAllBuffers();
            return this.rootByteBuffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: c */
    public void writeByte(int i) throws IOException {
        this.currentDataOutput.writeByte(i);
    }

    /* JADX INFO: renamed from: a */
    public void writeBoolean(boolean z) throws IOException {
        this.currentDataOutput.writeBoolean(z);
    }

    /* JADX INFO: renamed from: a */
    public void writeInt(int i) throws IOException {
        this.currentDataOutput.writeInt(i);
    }

    /* JADX INFO: renamed from: a */
    public void writeFloat(float f) throws IOException {
        this.currentDataOutput.writeFloat(f);
    }

    /* JADX INFO: renamed from: a */
    public void writeDouble(double d) throws IOException {
        this.currentDataOutput.writeDouble(d);
    }

    /* JADX INFO: renamed from: a */
    public void writeLong(long j) throws IOException {
        this.currentDataOutput.writeLong(j);
    }

    /* JADX INFO: renamed from: b */
    public void writeStringNullable(String str) throws IOException {
        writeBoolean(str != null);
        if (str != null) {
            writeStringUTF(str);
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeIntNullable(Integer num) throws IOException {
        writeBoolean(num != null);
        if (num != null) {
            writeInt(num.intValue());
        }
    }

    /* JADX INFO: renamed from: c */
    public void writeStringUTF(String str) throws IOException {
        this.currentDataOutput.writeUTF(str);
    }

    /* JADX INFO: renamed from: a */
    public void writeAnimationTag(AnimationTag animationTag) throws IOException {
        if (animationTag == null) {
            this.currentDataOutput.writeUTF(VariableScope.nullOrMissingString);
        }
        this.currentDataOutput.writeUTF(animationTag.toString());
    }

    /* JADX INFO: renamed from: a */
    public void writeObjectId(GameObject gameObject) throws IOException {
        if (gameObject == null) {
            this.currentDataOutput.writeLong(-1L);
        } else {
            this.currentDataOutput.writeLong(gameObject.objectId);
        }
    }

    /* JADX INFO: renamed from: b */
    public void writeExistingObjectId(GameObject gameObject) throws IOException {
        if (gameObject != null && !gameObject.isDestroyed) {
            this.currentDataOutput.writeLong(gameObject.objectId);
        } else {
            this.currentDataOutput.writeLong(-1L);
        }
    }

    /* JADX INFO: renamed from: a */
    public void startBlockInternal(FastArrayList fastArrayList) throws IOException {
        if (fastArrayList == null) {
            writeInt(0);
            return;
        }
        writeInt(fastArrayList.size());
        Iterator it = fastArrayList.iterator();
        while (it.hasNext()) {
            writeExistingObjectId((GameObject) it.next());
        }
    }

    /* JADX INFO: renamed from: b */
    public void writeUnitIdIfAlive(BaseUnit baseUnit) throws IOException {
        if (baseUnit != null && !baseUnit.isDestroyed && !baseUnit.isDestroyed) {
            writeObjectId((GameObject) baseUnit);
        } else {
            writeObjectId((GameObject) null);
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeUnitIdOrNullBaseUnit(BaseUnit baseUnit) throws IOException {
        if (baseUnit != null && !baseUnit.isDestroyed) {
            writeObjectId((GameObject) baseUnit);
        } else {
            writeObjectId((GameObject) null);
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeUnitIdOrNullUnitEntity(OrderableUnit orderableUnit) throws IOException {
        if (orderableUnit != null && !orderableUnit.isDestroyed) {
            writeObjectId((GameObject) orderableUnit);
        } else {
            writeObjectId((GameObject) null);
        }
    }

    /* JADX INFO: renamed from: a */
    public void writePointFNullable(PointF pointF) throws IOException {
        writeBoolean(pointF != null);
        if (pointF != null) {
            writeFloat(pointF.x);
            writeFloat(pointF.y);
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeEnumOrdinal(Enum r4) throws IOException {
        if (r4 == null) {
            this.currentDataOutput.writeInt(-1);
        } else {
            this.currentDataOutput.writeInt(r4.ordinal());
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeUnitTypeId(UnitType unitType) throws IOException {
        if (unitType == null) {
            this.currentDataOutput.writeInt(-1);
        } else if (unitType instanceof CustomUnitConfig) {
            this.currentDataOutput.writeInt(-2);
            writeStringUTF(((CustomUnitConfig) unitType).name);
        } else {
            this.currentDataOutput.writeInt(((UnitTypeEnum) unitType).ordinal());
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeConnectionIdInt(NetworkConnection networkConnection) throws IOException {
        if (networkConnection == null) {
            this.currentDataOutput.writeInt(0);
        } else {
            this.currentDataOutput.writeInt(networkConnection.connectionId);
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeTeamIdByte(PlayerTeam playerTeam) throws IOException {
        this.currentDataOutput.writeByte(playerTeam.teamId);
    }

    /* JADX INFO: renamed from: a */
    public void writeStreamWithLength(File file) throws IOException {
        AssetInputStream assetInputStreamOpenFile = FileHelper.openFile(file);
        if (assetInputStreamOpenFile == null) {
            throw new IOException("Failed to read save file data");
        }
        try {
            writeInputStreamWithLength(assetInputStreamOpenFile, (int) file.length());
            if (assetInputStreamOpenFile != null) {
                assetInputStreamOpenFile.close();
            }
        } catch (Throwable th) {
            if (assetInputStreamOpenFile != null) {
                assetInputStreamOpenFile.close();
            }
            throw th;
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeGameInputStreamWithLength(GameInputStream gameInputStream) throws IOException {
        InputStream activeInputStream = gameInputStream.getActiveInputStream();
        try {
            activeInputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeInputStreamWithLength(activeInputStream, activeInputStream.available());
    }

    /* JADX INFO: renamed from: a */
    public void writeInputStreamWithLength(InputStream inputStream, int i) throws IOException {
        int i2 = 0;
        writeInt(i);
        byte[] bArr = new byte[16384];
        while (true) {
            int i3 = inputStream.read(bArr, 0, bArr.length);
            if (i3 != -1) {
                if (i2 + i3 > i) {
                    int i4 = i - i2;
                    if (i4 < 0) {
                        NetworkEngine.reportDesync("writeStream: bytesTillFull is " + i4);
                        return;
                    } else {
                        this.currentDataOutput.write(bArr, 0, i4);
                        return;
                    }
                }
                this.currentDataOutput.write(bArr, 0, i3);
                i2 += i3;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeByteArrayWithLength(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        writeInt(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(this.currentDataOutput);
    }

    /* JADX INFO: renamed from: a */
    public void writeBytesWithLength(byte[] bArr) throws IOException {
        writeInt(bArr.length);
        this.currentDataOutput.write(bArr);
    }

    /* JADX INFO: renamed from: b */
    public void writeBytesRaw(byte[] bArr) throws IOException {
        this.currentDataOutput.write(bArr);
    }

    /* JADX INFO: renamed from: a */
    public void writeShort(short s) throws IOException {
        this.currentDataOutput.writeShort(s);
    }

    /* JADX INFO: renamed from: e */
    public void writeMagicShort() throws IOException {
        writeShort((short) 12345);
    }

    /* JADX INFO: renamed from: d */
    public void debugPlaceholder(String str) {
    }

    /* JADX INFO: renamed from: f */
    public boolean isCompressionEnabled() {
        return false;
    }

    /* JADX INFO: renamed from: e */
    public void startBlock(String str) throws IOException {
        beginBlockInternal(str, false);
    }

    /* JADX INFO: renamed from: a */
    public void beginBlockInternal(String str, boolean z) throws IOException {
        StreamBlock streamBlock = new StreamBlock(z);
        streamBlock.blockName = str;
        this.outputBlockStack.add(streamBlock);
        this.currentDataOutput = ((StreamBlock) this.outputBlockStack.getLast()).dataOutput;
    }

    /* JADX INFO: renamed from: a */
    public void endBlock(String name) throws IOException {
        StreamBlock streamBlock = (StreamBlock) this.outputBlockStack.removeLast();
        if (!streamBlock.blockName.equals(name)) {
            GameEngine.log("OutputNetStream:endBlock", "Name does not match: expected" + name + " , got:" + streamBlock.blockName);
        }
        streamBlock.flush();
        if (this.outputBlockStack.isEmpty()) {
            this.currentDataOutput = this.rootDataOutput;
        } else {
            this.currentDataOutput = ((StreamBlock) this.outputBlockStack.getLast()).dataOutput;
        }
        this.currentDataOutput.writeUTF(streamBlock.blockName);
        writeByteArrayWithLength(streamBlock.byteBuffer);
        try {
            streamBlock.close();
        } catch (Exception e) {
            if (e instanceof DataFormatException) {
                if (!GameEngine.isIOSVersion) {
                    GameEngine.logColored("DataFormatException error calling streamBlock.close() (this is expected on android 4.4)");
                }
            } else {
                GameEngine.logColored("Error calling streamBlock.close() to clean up memory");
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: renamed from: g */
    public int getMaxSize() {
        return this.maxBufferSize;
    }

    /* JADX INFO: renamed from: h */
    public void close() {
        this.rootDataOutput = null;
        this.currentDataOutput = null;
        try {
            if (this.rootByteBuffer != null) {
                this.rootByteBuffer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.rootByteBuffer = null;
    }
}
