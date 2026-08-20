package com.corrodinggames.rts.game.units.custom.logic.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.TransportUnitInterface;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.UnitSpawner;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentSlotDefinition;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.geometry.PointF;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/e.class */
public class AttachmentAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    public UnitSpawner addNewUnits;

    /* JADX INFO: renamed from: b */
    public ArrayList onlyOnSlots;

    /* JADX INFO: renamed from: c */
    public int deleteNumUnits;

    /* JADX INFO: renamed from: d */
    public boolean disconnect;

    /* JADX INFO: renamed from: e */
    public boolean unload;

    /* JADX INFO: renamed from: f */
    public boolean disconnectFromParent;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        UnitSpawner unitSpawnerA = UnitSpawner.a(customUnitConfig, iniFile, str, str2 + "attachments_addNewUnits");
        int iIntValue = iniFile.getInt(str, str2 + "attachments_deleteNumUnits", (Integer) 0).intValue();
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "attachments_disconnect", (Boolean) false).booleanValue();
        boolean zBooleanValue2 = iniFile.getBoolean(str, str2 + "attachments_unload", (Boolean) false).booleanValue();
        boolean zBooleanValue3 = iniFile.getBoolean(str, str2 + "disconnectFromParent", (Boolean) false).booleanValue();
        if (!unitSpawnerA.b() || iIntValue != 0 || zBooleanValue3 || zBooleanValue || zBooleanValue2) {
            AttachmentAction attachmentAction = new AttachmentAction();
            attachmentAction.addNewUnits = unitSpawnerA;
            String string = iniFile.getString(str, "attachments_onlyOnSlots", (String) null);
            if (string != null) {
                for (String str4 : string.split(",")) {
                    String strTrim = str4.trim();
                    if (!strTrim.equals(VariableScope.nullOrMissingString)) {
                        AttachmentSlotDefinition attachmentSlotDefinitionFindEnergyTransferRuleByName = customUnitConfig.findEnergyTransferRuleByName(strTrim);
                        if (attachmentAction.onlyOnSlots == null) {
                            attachmentAction.onlyOnSlots = new ArrayList();
                        }
                        if (attachmentSlotDefinitionFindEnergyTransferRuleByName == null) {
                            throw new ConfigParseException("[" + str + "]attachments_onlyOnSlots: Could not find attachment slot with name: " + strTrim);
                        }
                        attachmentAction.onlyOnSlots.add(attachmentSlotDefinitionFindEnergyTransferRuleByName);
                    }
                }
            }
            attachmentAction.deleteNumUnits = iIntValue;
            attachmentAction.disconnectFromParent = zBooleanValue3;
            attachmentAction.disconnect = zBooleanValue;
            attachmentAction.unload = zBooleanValue2;
            customActionDef.logicActions.add(attachmentAction);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        if ((this.disconnect || this.unload) && customUnit.C != null && customUnit.C.size() > 0) {
            int size = customUnit.C.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                BaseUnit baseUnit2 = (BaseUnit) customUnit.C.get(size);
                if (baseUnit2 != null) {
                    if (this.onlyOnSlots != null) {
                        boolean z = false;
                        Iterator it = this.onlyOnSlots.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            if (((AttachmentSlotDefinition) it.next()).a() == size) {
                                z = true;
                                break;
                            }
                        }
                        if (!z) {
                            continue;
                        }
                    }
                    if (!(baseUnit2 instanceof OrderableUnit)) {
                        GameEngine.log("Failed to deattach unit:" + baseUnit2.r().getUnitTypeDescriptionShort() + " is not an OrderableUnit");
                    } else {
                        OrderableUnit orderableUnit = (OrderableUnit) baseUnit2;
                        if (this.unload) {
                            customUnit.canAttackTargetUnit((BaseUnit) orderableUnit, true, customUnit.transportedUnits.size() % 2 == 0);
                        } else {
                            orderableUnit.bx();
                        }
                    }
                }
                size--;
            }
        }
        if (this.deleteNumUnits != 0) {
            for (int i2 = 0; i2 < this.deleteNumUnits; i2++) {
                if (customUnit.C != null && customUnit.C.size() > 0) {
                    for (int size2 = customUnit.C.size() - 1; size2 >= 0; size2--) {
                        BaseUnit baseUnit3 = (BaseUnit) customUnit.C.get(size2);
                        if (baseUnit3 != null) {
                            if (this.onlyOnSlots != null) {
                                boolean z2 = false;
                                Iterator it2 = this.onlyOnSlots.iterator();
                                while (true) {
                                    if (!it2.hasNext()) {
                                        break;
                                    }
                                    if (((AttachmentSlotDefinition) it2.next()).a() == size2) {
                                        z2 = true;
                                        break;
                                    }
                                }
                                if (!z2) {
                                }
                            }
                            baseUnit3.removeFromGame();
                            break;
                        }
                    }
                }
            }
        }
        if (this.addNewUnits != null) {
            FastArrayList<BaseUnit> fastArrayList = new FastArrayList();
            this.addNewUnits.a(fastArrayList, customUnit.team, (BaseUnit) customUnit, true);
            for (BaseUnit baseUnit4 : fastArrayList) {
                boolean z3 = false;
                if (!(baseUnit4 instanceof OrderableUnit)) {
                    GameEngine.log("Failed to attach unit:" + baseUnit4.r().getUnitTypeDescriptionShort() + " is not an OrderableUnit");
                } else {
                    OrderableUnit orderableUnit2 = (OrderableUnit) baseUnit4;
                    if (this.onlyOnSlots != null) {
                        Iterator it3 = this.onlyOnSlots.iterator();
                        while (true) {
                            if (!it3.hasNext()) {
                                break;
                            }
                            AttachmentSlotDefinition attachmentSlotDefinition = (AttachmentSlotDefinition) it3.next();
                            if (customUnit.a(attachmentSlotDefinition) == null && customUnit.a(orderableUnit2, attachmentSlotDefinition)) {
                                orderableUnit2.attachmentStartTimeMillis = -9999;
                                z3 = true;
                                break;
                            }
                        }
                    } else {
                        Iterator it4 = customUnit.unitConfig.attachmentSlotDefinitions.iterator();
                        while (true) {
                            if (!it4.hasNext()) {
                                break;
                            }
                            AttachmentSlotDefinition attachmentSlotDefinition2 = (AttachmentSlotDefinition) it4.next();
                            if (customUnit.a(attachmentSlotDefinition2) == null && customUnit.a(orderableUnit2, attachmentSlotDefinition2)) {
                                orderableUnit2.attachmentStartTimeMillis = -9999;
                                z3 = true;
                                break;
                            }
                        }
                    }
                    if (!z3) {
                        orderableUnit2.remove();
                    }
                }
            }
        }
        if (this.disconnectFromParent) {
            if (customUnit.parentEntity != null) {
                customUnit.bx();
            }
            if (customUnit.transportContainer != null) {
                if (customUnit.transportContainer instanceof TransportUnitInterface) {
                    ((TransportUnitInterface) customUnit.transportContainer).e(customUnit);
                    return true;
                }
                GameEngine.logWarningAndStack("transportedBy is not a TransportInterface");
                customUnit.transportContainer = null;
                return true;
            }
            return true;
        }
        return true;
    }
}
