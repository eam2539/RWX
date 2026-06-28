package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.local.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.as */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/as.class */
class UnitDamagedLogEntry extends WarLogEntry {
    private boolean a;

    public UnitDamagedLogEntry(float f, float f2, boolean z) {
        super(f, f2);
        this.a = z;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.WarLogEntry
    public boolean a(WarLogEntry warLogEntry) {
        return super.a(warLogEntry) && (warLogEntry instanceof UnitDamagedLogEntry) && ((UnitDamagedLogEntry) warLogEntry).a == this.a;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.WarLogEntry
    public void b(WarLogEntry warLogEntry) {
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.WarLogEntry
    protected long b() {
        return 20000L;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.WarLogEntry
    public String a() {
        if (this.g == null) {
            if (this.a) {
                this.g = Locale.get("gui.log.baseDamaged", new Object[0]);
            } else {
                this.g = Locale.get("gui.log.unitDamaged", new Object[0]);
            }
        }
        return this.g;
    }
}
