package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.au */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/au.class */
public class CustomDataWriterFactory extends VariableScope.CachedWriter.WriterFactory {
    CustomUnitConfig a;

    public CustomDataWriterFactory(CustomUnitConfig customUnitConfig) {
        this.a = customUnitConfig;
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.CachedWriter.WriterFactory
    public VariableScope.CachedWriter.WriterElement createWriterElement(String str, String str2, String str3, String str4) throws ConfigParseException {
        if (!str2.equals("=") && !str2.equals("+=") && !str2.equals("-=")) {
            throw new ConfigParseException("Only '=','+=','-='  is supported here, got:" + str2);
        }
        if (str3 == null) {
            throw new ConfigParseException("Expected a value for: " + str + " (likely missing '=')");
        }
        VariableScope.CachedWriter.Operator operator = VariableScope.CachedWriter.Operator.set;
        if (str2.equals("+=")) {
            operator = VariableScope.CachedWriter.Operator.add;
        }
        if (str2.equals("-=")) {
            operator = VariableScope.CachedWriter.Operator.subtract;
        }
        try {
            LogicBoolean booleanBlock = LogicBooleanLoader.parseBooleanBlock(this.a, str3, false);
            if (str4 != null) {
                throw new RuntimeException("Unexpected array [] index operator on " + str);
            }
            CustomUnitDataField customUnitDataField = (CustomUnitDataField) UnitStats.s.get(str);
            if (customUnitDataField == null) {
                customUnitDataField = (CustomUnitDataField) UnitStats.s.get("core." + str);
            }
            if (customUnitDataField == null) {
                throw new ConfigParseException("Unknown key: " + str + VariableScope.nullOrMissingString);
            }
            CustomDataWriterElement customDataWriterElement = new CustomDataWriterElement();
            customDataWriterElement.a = customUnitDataField;
            customDataWriterElement.b = booleanBlock;
            customDataWriterElement.c = operator;
            if (booleanBlock.getReturnType() != customUnitDataField.a()) {
                throw new ConfigParseException("Field: " + str + " expects " + customUnitDataField.a() + " type getting: " + booleanBlock.getReturnType() + " from: " + str3);
            }
            return customDataWriterElement;
        } catch (RuntimeException e) {
            throw new RuntimeException("LogicBoolean - Error: " + e.getMessage() + ", [parsing: '" + str3 + "']", e);
        }
    }
}
