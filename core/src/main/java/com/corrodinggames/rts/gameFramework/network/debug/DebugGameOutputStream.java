package com.corrodinggames.rts.gameFramework.network.debug;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/f.class */
public class DebugGameOutputStream extends GameOutputStream {
    public String a = VariableScope.nullOrMissingString;

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeInt(int i) throws IOException {
        this.a += "|" + i;
        super.writeInt(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeFloat(float f) throws IOException {
        this.a += "|" + f;
        super.writeFloat(f);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeShort(short s) throws IOException {
        this.a += "|" + ((int) s);
        super.writeShort(s);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeBoolean(boolean z) throws IOException {
        this.a += "|" + z;
        super.writeBoolean(z);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void beginBlockInternal(String str, boolean z) throws IOException {
        this.a += "<" + str + ">";
        super.beginBlockInternal(str, z);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void endBlock(String str) throws IOException {
        this.a += "</" + str + ">";
        super.endBlock(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.GameOutputStream
    /* JADX INFO: renamed from: a */
    public void writeUnitIdOrNullBaseUnit(BaseUnit baseUnit) throws IOException {
        this.a += "|u:" + baseUnit;
        super.writeUnitIdOrNullBaseUnit(baseUnit);
    }
}
