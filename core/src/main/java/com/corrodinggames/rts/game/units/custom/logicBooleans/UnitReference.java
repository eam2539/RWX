package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.TeamRelation;
import com.corrodinggames.rts.game.units.*;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentSlotDefinition;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.ConfigException;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.geometry.PointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference.class */
public abstract class UnitReference extends LogicBoolean implements Cloneable {
    public static final SelfUnitReference selfUnitReference = new SelfUnitReference();
    static HashMap referenceTypes = new HashMap();
    static final LogicBooleanLoader.LogicBooleanContext unitContextChangingContext;
    static final LogicBooleanLoader.LogicBooleanContext placeholderUnitContext;

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$UnitContextChangingContext.class */
    public static class UnitContextChangingContext extends LogicBooleanLoader.LogicBooleanContextWithDefault {
    }

    public abstract BaseUnit getSingleRaw(OrderableUnit orderableUnit);

    static {
        addUnitReferenceType(new AttachmentUnitReference(), "attachment");
        addUnitReferenceType(new ParentUnitReference(), "parent");
        addUnitReferenceType(new TransportingUnitReference(), "transporting");
        addUnitReferenceType(new ActiveWaypointTargetReference(), "activeWaypointTarget");
        addUnitReferenceType(new AttackingReference(), "attacking");
        addUnitReferenceType(new Memory1UnitReference(), "customTarget1");
        addUnitReferenceType(new Memory2UnitReference(), "customTarget2");
        addUnitReferenceType(new LastDamagedByUnitReference(), "lastDamagedBy");
        addUnitReferenceType(new NearestUnitReference(), "nearestUnit");
        addUnitReferenceType(new FirstUnitReference(), "globalSearchForFirstUnit");
        addUnitReferenceType(new NullUnitReference(), "nullUnit");
        addUnitReferenceType(new NullUnitReference(), "null");
        addUnitReferenceType(new GetOffsetAbsolute(), "getOffsetAbsolute");
        addUnitReferenceType(new GetOffsetRelative(), "getOffsetRelative");
        addUnitReferenceType(new GetAsMarker(), "getAsMarker");
        addUnitReferenceType(new ThisActionTargetReference(), "thisActionTarget");
        addUnitReferenceType(new EventSourceReference(), "eventSource");
        unitContextChangingContext = new UnitContextChangingContext();
        placeholderUnitContext = new UnitContextChangingContext();
    }

    public final BaseUnit get(BaseUnit baseUnit) {
        if (!(baseUnit instanceof OrderableUnit)) {
            return null;
        }
        return getSingleRaw((OrderableUnit) baseUnit);
    }

    public final BaseUnit get(OrderableUnit orderableUnit) {
        return getSingleRaw(orderableUnit);
    }

    public final BaseUnit getRealUnitOnly(OrderableUnit orderableUnit) {
        BaseUnit singleRaw = getSingleRaw(orderableUnit);
        if (singleRaw instanceof DummyNonUnitWithTeam) {
            return null;
        }
        return singleRaw;
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public void forMeta(CustomUnitConfig customUnitConfig) {
    }

    static void addUnitReferenceType(UnitReference unitReference, String... strArr) {
        for (String str : strArr) {
            String lowerCase = str.toLowerCase(Locale.ROOT);
            referenceTypes.put(lowerCase, unitReference);
            if (!lowerCase.replace("self.", "subject.").equals(lowerCase)) {
            }
        }
    }

    public static UnitReferenceOrUnitType parseUnitTypeOrReferenceFromConf(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, UnitReferenceOrUnitType unitReferenceOrUnitType) throws ConfigParseException {
        return parseUnitTypeOrReference(customUnitConfig, iniFile.getString(str, str2, (String) null), str, str2, unitReferenceOrUnitType);
    }

    public static UnitReferenceOrUnitType parseUnitTypeOrReference(CustomUnitConfig customUnitConfig, String str, String str2, String str3, UnitReferenceOrUnitType unitReferenceOrUnitType) throws ConfigParseException {
        if (str == null) {
            return unitReferenceOrUnitType;
        }
        String strTrim = str.trim();
        if (VariableScope.nullOrMissingString.equals(strTrim) || "NONE".equalsIgnoreCase(strTrim)) {
            return unitReferenceOrUnitType;
        }
        if (strTrim.toLowerCase(Locale.ROOT).startsWith("unitref ")) {
            return new UnitReferenceOrUnitType(parseUnitReference(customUnitConfig, strTrim, str2, str3, null, true));
        }
        UnitTypeReference unitTypeReferenceReloadAllCustomUnits = customUnitConfig.reloadAllCustomUnits(strTrim, str3, str2);
        if (unitTypeReferenceReloadAllCustomUnits != null) {
            unitTypeReferenceReloadAllCustomUnits.f = true;
        }
        return new UnitReferenceOrUnitType(unitTypeReferenceReloadAllCustomUnits);
    }

    public static UnitReference parseUnitReferenceFromConf(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, UnitReference unitReference) throws ConfigParseException {
        return parseUnitReference(customUnitConfig, iniFile.getString(str, str2, (String) null), str, str2, unitReference, false);
    }

    public static UnitReference parseUnitReference(CustomUnitConfig customUnitConfig, String str, String str2, String str3, UnitReference unitReference, boolean z) throws ConfigParseException {
        if (str == null) {
            return unitReference;
        }
        String strTrim = str.trim();
        if (VariableScope.nullOrMissingString.equals(strTrim) || "NONE".equalsIgnoreCase(strTrim)) {
            return unitReference;
        }
        String lowerCase = strTrim.toLowerCase(Locale.ROOT);
        if (lowerCase.startsWith("unitref ")) {
            lowerCase = lowerCase.substring("unitref ".length()).trim();
        }
        if (lowerCase.equals("self")) {
            return selfUnitReference;
        }
        if (!lowerCase.contains("(")) {
            lowerCase.length();
        } else if (lowerCase.indexOf(")") != lowerCase.length() - 1) {
            throw new ConfigParseException("[" + str2 + "]" + str3 + " UnitReference: Unexpected format for: '" + lowerCase + "'");
        }
        try {
            UnitReference singleUnitReferenceBlock = parseSingleUnitReferenceBlock(customUnitConfig, lowerCase);
            if (singleUnitReferenceBlock == null) {
                throw new RuntimeException("Unknown function:'" + lowerCase + "'");
            }
            return singleUnitReferenceBlock;
        } catch (RuntimeException e) {
            throw new RuntimeException("[" + str2 + "]" + str3 + " UnitReference error: " + e.getMessage() + ", [parsing: '" + lowerCase + "']", e);
        }
    }

    public static UnitReference parseSingleUnitReferenceElement(CustomUnitConfig customUnitConfig, String str) {
        String lowerCase = str.split("\\(")[0].trim().toLowerCase(Locale.ROOT);
        UnitReference unitReference = (UnitReference) referenceTypes.get(lowerCase);
        if (unitReference == null) {
            return null;
        }
        String strTrim = str.substring(lowerCase.length()).trim();
        if (strTrim.equals(VariableScope.nullOrMissingString)) {
            strTrim = "()";
        }
        if (!strTrim.startsWith("(") || !strTrim.endsWith(")")) {
            throw new RuntimeException("Failed to parse unit reference arguments for:'" + str + "'");
        }
        UnitReference unitReferenceWith = (UnitReference) unitReference.with(customUnitConfig, strTrim.substring(1, strTrim.length() - 1).trim(), lowerCase);
        if (unitReferenceWith instanceof NullUnitReference) {
        }
        return unitReferenceWith;
    }

    public static UnitReference parseSingleUnitReferenceBlock(CustomUnitConfig customUnitConfig, String str) {
        int iB = StringUtils.b(str);
        if (iB != 0) {
            if (iB > 0) {
                throw new RuntimeException("Brackets unbalanced for: '" + str + "'. A '(' was not closed.");
            }
            if (iB < 0) {
                throw new RuntimeException("Brackets unbalanced for: '" + str + "'. Too many ')'.");
            }
        }
        String strBreakOuterLayerBrackets = LogicBooleanLoader.breakOuterLayerBrackets(str.trim());
        String[] strArrB = StringUtils.b(strBreakOuterLayerBrackets, ".", false);
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        for (String str2 : strArrB) {
            if (str2.equalsIgnoreCase("self")) {
                z = true;
            } else {
                UnitReference singleUnitReferenceElement = parseSingleUnitReferenceElement(customUnitConfig, str2);
                if (singleUnitReferenceElement == null) {
                    throw new RuntimeException("Unknown unit reference:'" + str2 + "'");
                }
                arrayList.add(singleUnitReferenceElement);
            }
        }
        if (arrayList.size() == 0) {
            if (z) {
                return selfUnitReference;
            }
            throw new RuntimeException("Unexpected unit reference:'" + strBreakOuterLayerBrackets + "'");
        }
        if (arrayList.size() == 1) {
            return (UnitReference) arrayList.get(0);
        }
        return new ChainedUnitReference((UnitReference[]) arrayList.toArray(new UnitReference[0]));
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public LogicBoolean with(String str) {
        return with(null, str, null);
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
        try {
            UnitReference unitReference = (UnitReference) clone();
            unitReference.forMeta(customUnitConfig);
            unitReference.setArgumentsRaw(str, customUnitConfig, str2);
            return unitReference;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public boolean read(OrderableUnit orderableUnit) {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public BaseUnit readUnit(OrderableUnit orderableUnit) {
        return getSingleRaw(orderableUnit);
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public LogicBoolean.ReturnType getReturnType() {
        return LogicBoolean.ReturnType.unit;
    }

    public String getClassDebugName() {
        return "<unit reference>";
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
        return getClassDebugName() + "(" + BaseUnit.serialize(getSingleRaw(orderableUnit)) + ")";
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$ChainedUnitReference.class */
    public static class ChainedUnitReference extends UnitReference {
        UnitReference[] chain;

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        ChainedUnitReference(UnitReference[] unitReferenceArr) {
            this.chain = unitReferenceArr;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            UnitReference[] unitReferenceArr = this.chain;
            BaseUnit baseUnit = orderableUnit;
            LogicBoolean.externalUnitContext = orderableUnit;
            for (UnitReference unitReference : unitReferenceArr) {
                baseUnit = unitReference.get(baseUnit);
                if (baseUnit == null) {
                    return null;
                }
            }
            LogicBoolean.externalUnitContext = null;
            return baseUnit;
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            UnitReference[] unitReferenceArr = this.chain;
            BaseUnit baseUnit = orderableUnit;
            if (baseUnit instanceof OrderableUnit) {
                LogicBoolean.externalUnitContext = (OrderableUnit) baseUnit;
            }
            String str = VariableScope.nullOrMissingString + "[";
            int i = 0;
            while (true) {
                if (i >= unitReferenceArr.length) {
                    break;
                }
                str = str + unitReferenceArr[i].getMatchFailReasonForPlayer(orderableUnit);
                if (i != unitReferenceArr.length - 1) {
                    str = str + ",";
                }
                baseUnit = unitReferenceArr[i].get(baseUnit);
                if (baseUnit != null) {
                    i++;
                } else {
                    str = str + "<null>";
                    break;
                }
            }
            LogicBoolean.externalUnitContext = null;
            return str + "]";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$AttachmentUnitReference.class */
    public static class AttachmentUnitReference extends UnitReference {
        CustomUnitConfig meta;
        AnimationTag _withTag;
        short attachmentId = -1;

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (logicBooleanContext != null && logicBooleanContext != LogicBooleanLoader.defaultContextReader && this.attachmentId != -1) {
                throw new BooleanParseException("Function:" + str + " only supports use with 'self.' when using 'slot'");
            }
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new ConfigException("AttachmentUnitReference requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @LogicBoolean.Parameter
        public void slot(String str) {
            AttachmentSlotDefinition attachmentSlotDefinitionFindEnergyTransferRuleByName = this.meta.findEnergyTransferRuleByName(str);
            if (attachmentSlotDefinitionFindEnergyTransferRuleByName == null) {
                throw new ConfigException("No attachment slot with name: " + str + " found");
            }
            this.attachmentId = attachmentSlotDefinitionFindEnergyTransferRuleByName.a();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            if (!(orderableUnit instanceof CustomUnit)) {
                return null;
            }
            CustomUnit customUnit = (CustomUnit) orderableUnit;
            if (customUnit.C == null) {
                return null;
            }
            Object[] objArrA = customUnit.C.a();
            for (int i = customUnit.C.size - 1; i >= 0; i--) {
                OrderableUnit orderableUnit2 = (OrderableUnit) objArrA[i];
                if (orderableUnit2 != null && (this.attachmentId == -1 || i == this.attachmentId)) {
                    if (this._withTag != null) {
                        if (!AnimationTag.a(this._withTag, orderableUnit2.getTags())) {
                        }
                    }
                    return orderableUnit2;
                }
            }
            return null;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "attachment";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$TransportingUnitReference.class */
    public static class TransportingUnitReference extends UnitReference {
        CustomUnitConfig meta;

        @LogicBoolean.Parameter
        public int slot = -1;

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new ConfigException("TransportingUnitReference requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            BaseUnit baseUnit = null;
            FastArrayList transportedUnitList = orderableUnit.getTransportedUnitList();
            if (transportedUnitList != null) {
                if (this.slot == -1) {
                    if (transportedUnitList.size() > 0) {
                        baseUnit = (BaseUnit) transportedUnitList.get(0);
                    }
                } else if (this.slot >= 0 && this.slot < transportedUnitList.size()) {
                    baseUnit = (BaseUnit) transportedUnitList.get(this.slot);
                }
            }
            return baseUnit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "transporting";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$ParentUnitReference.class */
    public static class ParentUnitReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            return orderableUnit.dr();
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "parent";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$Memory1UnitReference.class */
    public static class Memory1UnitReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            BaseUnit baseUnit = orderableUnit.unitTarget2;
            if (baseUnit == null || baseUnit.isDead) {
                return null;
            }
            return baseUnit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "customTarget1";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$Memory2UnitReference.class */
    public static class Memory2UnitReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            BaseUnit baseUnit = orderableUnit.unitTarget3;
            if (baseUnit == null || baseUnit.isDead) {
                return null;
            }
            return baseUnit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "customTarget2";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$LastDamagedByUnitReference.class */
    public static class LastDamagedByUnitReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            BaseUnit baseUnit = orderableUnit.unitTarget1;
            if (baseUnit == null || baseUnit.isDead) {
                return null;
            }
            return baseUnit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "lastDamagedBy";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$NearestUnitReference.class */
    public static class NearestUnitReference extends UnitReference {
        public AnimationTag _withTag;
        public AnimationTag _withoutTag;

        @LogicBoolean.Parameter
        public boolean incompleteBuildings;
        public static final HandleCallbackNearest handleCallbackNearest = new HandleCallbackNearest();
        public float withinRange = 500.0f;
        public float withinRangeSq = this.withinRange * this.withinRange;
        public TeamRelation relation = TeamRelation.any;

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "NearestUnit";
        }

        @LogicBoolean.Parameter
        public void withinRange(float f) {
            if (f > 1500.0f) {
                throw new ConfigException("NearestUnit distance cannot be over 1500 is: " + f);
            }
            this.withinRange = f;
            this.withinRangeSq = f * f;
        }

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @LogicBoolean.Parameter
        public void withoutTag(String str) {
            this._withoutTag = AnimationTag.c(str);
        }

        @LogicBoolean.Parameter
        public void relation(String str) {
            try {
                this.relation = (TeamRelation) IniFile.parseEnum(str, TeamRelation.any, TeamRelation.class);
            } catch (ConfigParseException e) {
                throw new ConfigException(e.getMessage(), e);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            handleCallbackNearest.nearest = null;
            handleCallbackNearest.withinRangeSq = this.withinRangeSq;
            handleCallbackNearest.tag = this._withTag;
            handleCallbackNearest.withoutTag = this._withoutTag;
            handleCallbackNearest.incompleteBuildings = this.incompleteBuildings;
            handleCallbackNearest.relation = this.relation;
            GameEngine.getInstance().unitSpatialIndex.a(orderableUnit.posX, orderableUnit.posY, this.withinRange, orderableUnit, 0.0f, handleCallbackNearest);
            return handleCallbackNearest.nearest;
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$NearestUnitReference$HandleCallbackNearest.class */
        public static class HandleCallbackNearest extends FilteredUnitCallback {
            public AnimationTag tag;
            public AnimationTag withoutTag;
            public float withinRangeSq;
            public boolean incompleteBuildings;
            public TeamRelation relation = TeamRelation.any;
            public BaseUnit nearest;

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public void setup(OrderableUnit orderableUnit, float f) {
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public int excludeTeam(OrderableUnit orderableUnit) {
                return -3;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
                return null;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyTeam(OrderableUnit orderableUnit) {
                return null;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
            public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
                if ((this.relation != TeamRelation.any && !orderableUnit.team.a(this.relation, baseUnit.team)) || orderableUnit == baseUnit) {
                    return;
                }
                AnimationSet unitCombatAnimation = baseUnit.getTags();
                if (this.tag == null || (unitCombatAnimation != null && AnimationTag.a(this.tag, unitCombatAnimation))) {
                    float fDistanceSq = Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY);
                    if (fDistanceSq < this.withinRangeSq) {
                        if (baseUnit.buildProgress < 1.0f && !this.incompleteBuildings) {
                            return;
                        }
                        if (this.withoutTag != null && unitCombatAnimation != null && AnimationTag.a(this.withoutTag, unitCombatAnimation)) {
                            return;
                        }
                        this.withinRangeSq = fDistanceSq;
                        this.nearest = baseUnit;
                    }
                }
            }
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$FirstUnitReference.class */
    public static class FirstUnitReference extends UnitReference {
        public AnimationTag _withTag;
        public TeamRelation relation = TeamRelation.any;

        @LogicBoolean.Parameter
        public boolean incompleteBuildings;

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "globalSearchForFirstUnit";
        }

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @LogicBoolean.Parameter
        public void relation(String str) {
            try {
                this.relation = (TeamRelation) IniFile.parseEnum(str, (Enum) null, TeamRelation.class);
            } catch (ConfigParseException e) {
                throw new ConfigException(e.getMessage(), e);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
            int size = BaseUnit.bE.size();
            for (int i = 0; i < size; i++) {
                BaseUnit baseUnit = baseUnitArrA[i];
                if ((this.relation == TeamRelation.any || orderableUnit.team.a(this.relation, baseUnit.team)) && orderableUnit != baseUnit) {
                    AnimationSet unitCombatAnimation = baseUnit.getTags();
                    if ((this._withTag == null || (unitCombatAnimation != null && AnimationTag.a(this._withTag, unitCombatAnimation))) && (baseUnit.buildProgress >= 1.0f || this.incompleteBuildings)) {
                        return baseUnit;
                    }
                }
            }
            return null;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$PlaceholderUnitReference.class */
    public abstract static class PlaceholderUnitReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBooleanLoader.LogicBooleanContext createContext() {
            return UnitReference.placeholderUnitContext;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$GetOffsetAbsolute.class */
    public static class GetOffsetAbsolute extends PlaceholderUnitReference {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0)
        public LogicBoolean x = LogicBoolean.StaticValueBoolean.static_0;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1)
        public LogicBoolean y = LogicBoolean.StaticValueBoolean.static_0;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number)
        public LogicBoolean height = LogicBoolean.StaticValueBoolean.static_0;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "getOffsetAbsolute";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            OrderableUnit orderableUnit2 = orderableUnit.team.teamPrimaryUnit;
            OrderableUnit parameterContext = getParameterContext(orderableUnit);
            orderableUnit2.rotationSpeed = orderableUnit.rotationSpeed;
            orderableUnit2.posX = orderableUnit.posX + this.x.readNumber(parameterContext);
            orderableUnit2.posY = orderableUnit.posY + this.y.readNumber(parameterContext);
            orderableUnit2.posZ = orderableUnit.posZ + this.height.readNumber(parameterContext);
            return orderableUnit2;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$GetOffsetRelative.class */
    public static class GetOffsetRelative extends PlaceholderUnitReference {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0)
        public LogicBoolean x = LogicBoolean.StaticValueBoolean.static_0;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1)
        public LogicBoolean y = LogicBoolean.StaticValueBoolean.static_0;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number)
        public LogicBoolean height = LogicBoolean.StaticValueBoolean.static_0;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number)
        public LogicBoolean dirOffset = LogicBoolean.StaticValueBoolean.static_0;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "getOffsetRelative";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            OrderableUnit orderableUnit2 = orderableUnit.team.teamPrimaryUnit;
            OrderableUnit parameterContext = getParameterContext(orderableUnit);
            float number = orderableUnit.rotationSpeed + this.dirOffset.readNumber(parameterContext);
            float fFastCos = Utility.fastCos(number);
            float fFastSin = Utility.fastSin(number);
            float number2 = this.x.readNumber(parameterContext);
            float number3 = this.y.readNumber(parameterContext);
            float f = (fFastCos * number3) - (fFastSin * number2);
            orderableUnit2.rotationSpeed = number;
            orderableUnit2.posX = orderableUnit.posX + f;
            orderableUnit2.posY = orderableUnit.posY + (fFastSin * number3) + (fFastCos * number2);
            orderableUnit2.posZ = orderableUnit.posZ + this.height.readNumber(parameterContext);
            return orderableUnit2;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$GetOffsetRelativeStatic.class */
    public class GetOffsetRelativeStatic extends PlaceholderUnitReference {

        @LogicBoolean.Parameter(positional = 0)
        public float x;

        @LogicBoolean.Parameter(positional = 1)
        public float y;

        @LogicBoolean.Parameter
        public float height;

        @LogicBoolean.Parameter
        public float dirOffset;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "getOffsetRelativeStatic";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            OrderableUnit orderableUnit2 = orderableUnit.team.teamPrimaryUnit;
            float f = orderableUnit.rotationSpeed + this.dirOffset;
            float fFastCos = Utility.fastCos(f);
            float fFastSin = Utility.fastSin(f);
            float f2 = this.x;
            float f3 = this.y;
            float f4 = (fFastCos * f3) - (fFastSin * f2);
            orderableUnit2.rotationSpeed = f;
            orderableUnit2.posX = orderableUnit.posX + f4;
            orderableUnit2.posY = orderableUnit.posY + (fFastSin * f3) + (fFastCos * f2);
            orderableUnit2.posZ = orderableUnit.posZ + this.height;
            return orderableUnit2;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$GetAsMarker.class */
    public static class GetAsMarker extends PlaceholderUnitReference {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "getAsMarker";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            OrderableUnit orderableUnit2 = orderableUnit.team.teamPrimaryUnit;
            orderableUnit2.rotationSpeed = orderableUnit.rotationSpeed;
            orderableUnit2.posX = orderableUnit.posX;
            orderableUnit2.posY = orderableUnit.posY;
            orderableUnit2.posZ = orderableUnit.posZ;
            return orderableUnit2;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$ThisActionTargetReference.class */
    public static class ThisActionTargetReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            BaseUnit baseUnit = CustomUnit.dN;
            if (baseUnit != null) {
                return baseUnit;
            }
            PointF pointF = CustomUnit.dM;
            if (pointF != null) {
                OrderableUnit orderableUnit2 = PlayerTeam.TEAM_ALL.teamPrimaryUnit;
                orderableUnit2.rotationSpeed = 0.0f;
                orderableUnit2.posX = pointF.x;
                orderableUnit2.posY = pointF.y;
                orderableUnit2.posZ = 0.0f;
                return orderableUnit2;
            }
            return null;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "ThisActionTarget";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$EventSourceReference.class */
    public static class EventSourceReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            CustomUnitEventData customUnitEventData = LogicBoolean.currentEventContext;
            if (customUnitEventData == null) {
                return null;
            }
            return customUnitEventData.unit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "EventSource";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$ActiveWaypointTargetReference.class */
    public static class ActiveWaypointTargetReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            UnitCommand currentWaypoint = orderableUnit.getCurrentWaypoint();
            if (currentWaypoint == null) {
                return null;
            }
            return currentWaypoint.getResolvedTargetEntity();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "ActiveWaypointTarget";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$AttackingReference.class */
    public static class AttackingReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            return orderableUnit.attackTarget;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "Attacking";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$NullUnitReference.class */
    public static class NullUnitReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            return null;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "NULL";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return null;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$SelfUnitReference.class */
    public static class SelfUnitReference extends UnitReference {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            return orderableUnit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "self";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$LockedUnitReference.class */
    public class LockedUnitReference extends UnitReference {
        BaseUnit target;

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return super.with(customUnitConfig, str, str2);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean with(String str) {
            return super.with(str);
        }

        public LockedUnitReference(BaseUnit baseUnit) {
            this.target = baseUnit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            return this.target;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "unit";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$UnitReferenceOrUnitType.class */
    public static class UnitReferenceOrUnitType {
        UnitTypeReference unitType;
        UnitReference unitReference;

        UnitReferenceOrUnitType(UnitTypeReference unitTypeReference) {
            this.unitType = unitTypeReference;
        }

        UnitReferenceOrUnitType(UnitReference unitReference) {
            this.unitReference = unitReference;
        }

        public BaseUnit getUnitOrSharedUnit(BaseUnit baseUnit) {
            BaseUnit baseUnit2;
            if (this.unitType != null) {
                return BaseUnit.canAttack(this.unitType.c());
            }
            if (this.unitReference != null && (baseUnit2 = this.unitReference.get(baseUnit)) != null) {
                return baseUnit2;
            }
            return null;
        }

        public BaseUnit getUnitReferenceOrNull(BaseUnit baseUnit) {
            BaseUnit baseUnit2;
            if (this.unitReference != null && (baseUnit2 = this.unitReference.get(baseUnit)) != null) {
                return baseUnit2;
            }
            return null;
        }

        public UnitType getTypeOrNull(BaseUnit baseUnit) {
            BaseUnit baseUnit2;
            if (this.unitType != null) {
                return this.unitType.c();
            }
            if (this.unitReference != null && (baseUnit2 = this.unitReference.get(baseUnit)) != null) {
                return baseUnit2.r();
            }
            return null;
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public LogicBooleanLoader.LogicBooleanContext createContext() {
        return unitContextChangingContext;
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public LogicBoolean setChild(LogicBoolean logicBoolean) {
        return UnitContextChangingBooleanByLogic.create(this, logicBoolean);
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/UnitReference$UnitContextChangingBooleanByLogic.class */
    public static class UnitContextChangingBooleanByLogic extends LogicBoolean {
        LogicBoolean targetBoolean;
        LogicBoolean dynamicContext;
        LogicBoolean[] dynamicContextChain;

        public static UnitContextChangingBooleanByLogic create(LogicBoolean logicBoolean, LogicBoolean logicBoolean2) {
            ArrayList arrayList = null;
            if (logicBoolean2 instanceof UnitContextChangingBooleanByLogic) {
                UnitContextChangingBooleanByLogic unitContextChangingBooleanByLogic = (UnitContextChangingBooleanByLogic) logicBoolean2;
                arrayList = new ArrayList();
                arrayList.add(logicBoolean);
                if (unitContextChangingBooleanByLogic.dynamicContextChain != null) {
                    for (LogicBoolean logicBoolean3 : unitContextChangingBooleanByLogic.dynamicContextChain) {
                        arrayList.add(logicBoolean3);
                    }
                } else {
                    arrayList.add(unitContextChangingBooleanByLogic.dynamicContext);
                }
                logicBoolean2 = unitContextChangingBooleanByLogic.targetBoolean;
            }
            UnitContextChangingBooleanByLogic unitContextChangingBooleanByLogic2 = new UnitContextChangingBooleanByLogic();
            if (arrayList != null) {
                unitContextChangingBooleanByLogic2.dynamicContextChain = (LogicBoolean[]) arrayList.toArray(new LogicBoolean[0]);
            } else {
                unitContextChangingBooleanByLogic2.dynamicContext = logicBoolean;
            }
            unitContextChangingBooleanByLogic2.targetBoolean = logicBoolean2;
            if (logicBoolean == null) {
                throw new RuntimeException("dynamicContext==null");
            }
            if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.unit) {
                throw new RuntimeException("dynamicContext type!=unit. Got:" + logicBoolean.getReturnType());
            }
            return unitContextChangingBooleanByLogic2;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean setChild(LogicBoolean logicBoolean) {
            return super.setChild(logicBoolean);
        }

        public OrderableUnit getUnitContext(OrderableUnit orderableUnit) {
            if (this.dynamicContextChain != null) {
                OrderableUnit orderableUnit2 = orderableUnit;
                for (LogicBoolean logicBoolean : this.dynamicContextChain) {
                    BaseUnit unit = logicBoolean.readUnit(orderableUnit2);
                    if (!(unit instanceof OrderableUnit)) {
                        return null;
                    }
                    orderableUnit2 = (OrderableUnit) unit;
                }
                return orderableUnit2;
            }
            BaseUnit unit2 = this.dynamicContext.readUnit(orderableUnit);
            if (!(unit2 instanceof OrderableUnit)) {
                return null;
            }
            return (OrderableUnit) unit2;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean.setOuterUnitParameterContext(orderableUnit);
            try {
                OrderableUnit unitContext = getUnitContext(orderableUnit);
                if (unitContext != null) {
                    boolean z = this.targetBoolean.read(unitContext);
                    LogicBoolean.clearOuterUnitParameterContext();
                    return z;
                }
                LogicBoolean.clearOuterUnitParameterContext();
                return false;
            } catch (Throwable th) {
                LogicBoolean.clearOuterUnitParameterContext();
                throw th;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            LogicBoolean.setOuterUnitParameterContext(orderableUnit);
            try {
                OrderableUnit unitContext = getUnitContext(orderableUnit);
                if (unitContext != null) {
                    float number = this.targetBoolean.readNumber(unitContext);
                    LogicBoolean.clearOuterUnitParameterContext();
                    return number;
                }
                LogicBoolean.clearOuterUnitParameterContext();
                return 0.0f;
            } catch (Throwable th) {
                LogicBoolean.clearOuterUnitParameterContext();
                throw th;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            LogicBoolean.setOuterUnitParameterContext(orderableUnit);
            try {
                OrderableUnit unitContext = getUnitContext(orderableUnit);
                if (unitContext != null) {
                    String string = this.targetBoolean.readString(unitContext);
                    LogicBoolean.clearOuterUnitParameterContext();
                    return string;
                }
                LogicBoolean.clearOuterUnitParameterContext();
                return "<unit not found>";
            } catch (Throwable th) {
                LogicBoolean.clearOuterUnitParameterContext();
                throw th;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public BaseUnit readUnit(OrderableUnit orderableUnit) {
            LogicBoolean.setOuterUnitParameterContext(orderableUnit);
            try {
                OrderableUnit unitContext = getUnitContext(orderableUnit);
                if (unitContext != null) {
                    BaseUnit unit = this.targetBoolean.readUnit(unitContext);
                    LogicBoolean.clearOuterUnitParameterContext();
                    return unit;
                }
                LogicBoolean.clearOuterUnitParameterContext();
                return null;
            } catch (Throwable th) {
                LogicBoolean.clearOuterUnitParameterContext();
                throw th;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public UnitContextChangingBooleanByLogic with(CustomUnitConfig customUnitConfig, String str, String str2) {
            return this;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return this.targetBoolean.getReturnType();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str;
            if (this.dynamicContextChain != null) {
                String str2 = VariableScope.nullOrMissingString;
                OrderableUnit orderableUnit2 = orderableUnit;
                LogicBoolean.setOuterUnitParameterContext(orderableUnit);
                try {
                    for (LogicBoolean logicBoolean : this.dynamicContextChain) {
                        String str3 = str2 + logicBoolean.getMatchFailReasonForPlayer(orderableUnit2);
                        BaseUnit unit = logicBoolean.readUnit(orderableUnit2);
                        if (unit instanceof OrderableUnit) {
                            str2 = str3 + ".";
                            orderableUnit2 = (OrderableUnit) unit;
                        } else {
                            return str3 + "<unit not found>";
                        }
                    }
                    return str2 + this.targetBoolean.getMatchFailReasonForPlayer(orderableUnit2);
                } finally {
                }
            }
            LogicBoolean.setOuterUnitParameterContext(orderableUnit);
            try {
                BaseUnit unit2 = this.dynamicContext.readUnit(orderableUnit);
                if (!(unit2 instanceof OrderableUnit)) {
                    str = "=<unit not found> (type:" + this.dynamicContext.getReturnType() + ")";
                } else {
                    str = "." + this.targetBoolean.getMatchFailReasonForPlayer((OrderableUnit) unit2);
                }
                return this.dynamicContext.getMatchFailReasonForPlayer(orderableUnit) + str;
            } finally {
            }
        }
    }
}
