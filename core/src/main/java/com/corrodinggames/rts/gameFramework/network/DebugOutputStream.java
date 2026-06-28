package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import io.github.rwx.geometry.PointF;

import java.io.*;
import java.util.LinkedList;
import java.util.ListIterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.aw */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/aw.class */
public class DebugOutputStream extends GameOutputStream {

    /* JADX INFO: renamed from: a */
    ByteArrayOutputStream buffer;

    /* JADX INFO: renamed from: e */
    PrintStream mainPrintStream;

    /* JADX INFO: renamed from: f */
    private PrintStream activePrintStream;

    /* JADX INFO: renamed from: g */
    private LinkedList blockStack;

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void flushAllBuffers() throws IOException {
        ListIterator listIterator = this.blockStack.listIterator(this.blockStack.size());
        while (listIterator.hasPrevious()) {
            ((BlockOutputStream) listIterator.previous()).a();
        }
        this.mainPrintStream.flush();
        if (this.buffer != null) {
            this.buffer.flush();
        }
    }

    @Override
        // com.corrodinggames.rts.gameFramework.network.GameOutputStream
        /* JADX INFO: renamed from: b */
    void useMainStream() {
        this.activePrintStream = this.mainPrintStream;
    }

    public DebugOutputStream() {
        this.blockStack = new LinkedList();
        this.buffer = new ByteArrayOutputStream();
        this.mainPrintStream = new PrintStream(this.buffer);
        useMainStream();
    }

    public DebugOutputStream(PrintStream printStream) {
        this.blockStack = new LinkedList();
        this.mainPrintStream = printStream;
        useMainStream();
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: b */
    public PacketData buildPacketData(int i) {
        return buildPacketData(i, -1);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public PacketData buildPacketData(int i, int i2) {
        try {
            flushAllBuffers();
            PacketData packetData = new PacketData(i);
            packetData.bytes = this.buffer.toByteArray();
            packetData.delayMillis = i2;
            return packetData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: c */
    public String getBufferAsString() {
        try {
            flushAllBuffers();
            return this.buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: d */
    public byte[] toByteArray() {
        try {
            flushAllBuffers();
            return this.buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: c */
    public void writeByte(int i) {
        this.activePrintStream.println(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeBoolean(boolean z) {
        this.activePrintStream.println(z);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeInt(int i) {
        this.activePrintStream.println("#int:");
        this.activePrintStream.println(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeFloat(float f) {
        this.activePrintStream.println("#writeFloat");
        this.activePrintStream.println(f);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeLong(long j) {
        this.activePrintStream.println("#writeLong");
        this.activePrintStream.println(j);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: b */
    public void writeStringNullable(String str) {
        writeBoolean(str != null);
        if (str != null) {
            writeStringUTF(str);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: c */
    public void writeStringUTF(String str) {
        this.activePrintStream.println(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeObjectId(GameObject gameObject) {
        this.activePrintStream.println("#writeGameObject:");
        if (gameObject == null) {
            this.activePrintStream.println(-1);
        } else {
            this.activePrintStream.println(gameObject.objectId);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: b */
    public void writeExistingObjectId(GameObject gameObject) {
        this.activePrintStream.println("#writeExistingGameObject:");
        if (gameObject != null && !gameObject.isDestroyed) {
            this.activePrintStream.println(gameObject.objectId);
        } else {
            this.activePrintStream.println(-1);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: b */
    public void writeUnitIdIfAlive(BaseUnit baseUnit) {
        if (baseUnit != null && !baseUnit.isDestroyed && !baseUnit.isDead) {
            writeObjectId((GameObject) baseUnit);
        } else {
            writeObjectId((GameObject) null);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeUnitIdOrNullBaseUnit(BaseUnit baseUnit) {
        if (baseUnit != null && !baseUnit.isDestroyed) {
            writeObjectId((GameObject) baseUnit);
        } else {
            writeObjectId((GameObject) null);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeOrderableUnit(OrderableUnit orderableUnit) {
        if (orderableUnit != null && !orderableUnit.isDestroyed) {
            writeObjectId((GameObject) orderableUnit);
        } else {
            writeObjectId((GameObject) null);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writePointFNullable(PointF pointF) {
        this.activePrintStream.println("#PointF:");
        writeBoolean(pointF != null);
        if (pointF != null) {
            writeFloat(pointF.x);
            writeFloat(pointF.y);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeEnumOrdinal(Enum r5) {
        if (r5 == null) {
            this.activePrintStream.println("#Enum: null");
            this.activePrintStream.println(-1);
        } else {
            this.activePrintStream.println("#Enum:" + r5.getClass().getSimpleName() + " : " + r5.toString());
            this.activePrintStream.println(r5.ordinal());
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeUnitTypeId(UnitType unitType) {
        this.activePrintStream.println("#unitType:");
        if (unitType == null) {
            this.activePrintStream.println(-1);
        } else if (unitType instanceof CustomUnitConfig) {
            this.activePrintStream.println(-2);
            writeStringUTF(((CustomUnitConfig) unitType).name);
        } else {
            this.activePrintStream.println(((UnitTypeEnum) unitType).ordinal());
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeConnectionIdInt(NetworkConnection networkConnection) {
        if (networkConnection == null) {
            this.activePrintStream.println(0);
        } else {
            this.activePrintStream.println(networkConnection.connectionId);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeTeamIdByte(PlayerTeam playerTeam) {
        this.activePrintStream.println("#team:");
        this.activePrintStream.println(playerTeam.teamId);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeStreamWithLength(File file) throws IOException {
        AssetInputStream assetInputStreamOpenFile = FileHelper.openFile(file);
        try {
            writeInputStreamWithLength(assetInputStreamOpenFile, (int) file.length());
        } finally {
            assetInputStreamOpenFile.close();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
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
                        this.activePrintStream.write(bArr, 0, i4);
                        return;
                    }
                }
                this.activePrintStream.write(bArr, 0, i3);
                i2 += i3;
            } else {
                return;
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeByteArrayWithLength(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        writeInt(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(this.activePrintStream);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeBytesWithLength(byte[] bArr) throws IOException {
        writeInt(bArr.length);
        this.activePrintStream.write(bArr);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeShort(short s) {
        this.activePrintStream.println("#writeShort");
        this.activePrintStream.println((int) s);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: e */
    public void writeMagicShort() {
        this.activePrintStream.println("#writeMark:");
        writeShort((short) 12345);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: d */
    public void writeDebugMessage(String str) {
        this.activePrintStream.println("#writeIfDebugOnly: " + str);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: f */
    public boolean isDebugStream() {
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: e */
    public void startBlock(String str) {
        beginBlockInternal(str, false);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void beginBlockInternal(String str, boolean z) {
        BlockOutputStream blockOutputStream = new BlockOutputStream(z);
        blockOutputStream.blockName = str;
        this.blockStack.add(blockOutputStream);
        this.activePrintStream = ((BlockOutputStream) this.blockStack.getLast()).printStream;
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void endBlock(String str) throws IOException {
        BlockOutputStream blockOutputStream = (BlockOutputStream) this.blockStack.removeLast();
        if (!blockOutputStream.blockName.equals(str)) {
            GameEngine.log("OutputNetStream:endBlock", "Name does not match: expected" + str + " , got:" + blockOutputStream.blockName);
        }
        blockOutputStream.a();
        if (this.blockStack.isEmpty()) {
            this.activePrintStream = this.mainPrintStream;
        } else {
            this.activePrintStream = ((BlockOutputStream) this.blockStack.getLast()).printStream;
        }
        String str2 = VariableScope.nullOrMissingString;
        String str3 = VariableScope.nullOrMissingString;
        for (int i = 0; i < this.blockStack.size(); i++) {
            str2 = str2 + ">";
            str3 = str3 + "<";
        }
        this.activePrintStream.println(str2 + ">>>> Start of block: " + blockOutputStream.blockName);
        writeByteArrayWithLength(blockOutputStream.buffer);
        this.activePrintStream.println(str3 + "<<<< End of block: " + blockOutputStream.blockName);
        blockOutputStream.b();
    }
}
