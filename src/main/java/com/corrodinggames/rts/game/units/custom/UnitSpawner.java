package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;
import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bp */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bp.class */
public class UnitSpawner {

    /* JADX INFO: renamed from: a */
    FastArrayList<SpawnConfig> spawnQueue;

    public static UnitSpawner a(String str, String str2, String str3) throws ConfigParseException {
        return b(null, str, str2, str3, false);
    }

    public static UnitSpawner a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) throws ConfigParseException {
        return a(customUnitConfig, iniFile.getString(str, str2, (String) null), str, str2, false);
    }

    public static UnitSpawner b(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) throws ConfigParseException {
        return a(customUnitConfig, iniFile.getString(str, str2, (String) null), str, str2, true);
    }

    public static UnitSpawner a(CustomUnitConfig customUnitConfig, String str, String str2, String str3, boolean z) throws ConfigParseException {
        if (customUnitConfig == null) {
            throw new RuntimeException("meta==null");
        }
        return b(customUnitConfig, str, str2, str3, z);
    }

    public static UnitSpawner b(CustomUnitConfig customUnitConfig, String str, String str2, String str3, boolean z) throws ConfigParseException {
        int iA;
        UnitSpawner unitSpawner = new UnitSpawner();
        if (str == null || VariableScope.nullOrMissingString.equals(str) || "NONE".equalsIgnoreCase(str)) {
            return unitSpawner;
        }
        Iterator it = StringUtils.a(str, ",", false).iterator();
        while (it.hasNext()) {
            String strTrim = ((String) it.next()).trim();
            if (!VariableScope.nullOrMissingString.equals(strTrim)) {
                String strTrim2 = null;
                if (strTrim.contains("(") && strTrim.contains(")")) {
                    String[] strArrB = StringUtils.b(strTrim, "(");
                    if (strArrB == null) {
                        throw new ConfigParseException("[" + str2 + "]" + str3 + " UnitList: Unexpected format for '" + strTrim + "' of " + str);
                    }
                    strTrim = strArrB[0];
                    strTrim2 = strArrB[1].trim();
                }
                String[] strArrSplit = strTrim.split("\\*");
                String str4 = strArrSplit[0];
                int i = 1;
                if (strArrSplit.length >= 2) {
                    i = Integer.parseInt(strArrSplit[1]);
                }
                UnitTypeReference unitTypeReference = new UnitTypeReference();
                unitTypeReference.configKey = str3;
                unitTypeReference.sectionName = str2;
                unitTypeReference.unitTypeName = str4;
                if (customUnitConfig != null) {
                    customUnitConfig.unitTypeReferences.add(unitTypeReference);
                } else {
                    unitTypeReference.a();
                }
                SpawnConfig spawnConfig = new SpawnConfig(unitTypeReference);
                if (unitSpawner.spawnQueue == null) {
                    unitSpawner.spawnQueue = new FastArrayList();
                }
                spawnConfig.spawnCount = i;
                if (strTrim2 != null) {
                    if (!strTrim2.endsWith(")")) {
                        throw new ConfigParseException("[" + str2 + "]" + str3 + " UnitList: Expected ')' in '" + strTrim + "' of " + str);
                    }
                    for (String str5 : StringUtils.a(strTrim2.substring(0, strTrim2.length() - 1), ",", false, false)) {
                        if (!str5.trim().equals(VariableScope.nullOrMissingString)) {
                            String[] strArrB2 = StringUtils.b(str5, "=");
                            if (strArrB2 == null) {
                                throw new RuntimeException("[" + str2 + "]" + str3 + " UnitList: Unexpected key format for '" + strTrim + "' of " + str);
                            }
                            String strTrim3 = strArrB2[0].trim();
                            String strTrim4 = strArrB2[1].trim();
                            if (strTrim3.equalsIgnoreCase("neutralTeam")) {
                                spawnConfig.neutralTeam = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("setToTeamOfLastAttacker")) {
                                spawnConfig.setToTeamOfLastAttacker = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("aggressiveTeam")) {
                                spawnConfig.aggressiveTeam = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("spawnChance")) {
                                spawnConfig.spawnChance = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("maxSpawnLimit")) {
                                spawnConfig.maxSpawnLimit = IniFile.parseInt(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("techLevel")) {
                                spawnConfig.techLevel = IniFile.parseInt(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("gridAlign")) {
                                spawnConfig.gridAlign = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("skipIfOverlapping")) {
                                spawnConfig.skipIfOverlapping = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("falling")) {
                                spawnConfig.falling = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("transportedUnitsToTransfer")) {
                                spawnConfig.transportedUnitsToTransfer = (short) IniFile.parseInt(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("alwaysStartDirAtZero")) {
                                spawnConfig.alwaysStartDirAtZero = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("alwayStartDirAtZero")) {
                                spawnConfig.alwaysStartDirAtZero = IniFile.parseBoolean(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetX")) {
                                spawnConfig.offsetX = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetY")) {
                                spawnConfig.offsetY = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomXY")) {
                                float f = IniFile.parseFloat(str2, str3, strTrim4);
                                spawnConfig.offsetRandomX = f;
                                spawnConfig.offsetRandomY = f;
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomX")) {
                                spawnConfig.offsetRandomX = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomY")) {
                                spawnConfig.offsetRandomY = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetHeight")) {
                                spawnConfig.offsetHeight = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetRandomDir")) {
                                spawnConfig.offsetRandomDir = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("offsetDir")) {
                                spawnConfig.offsetDir = IniFile.parseFloat(str2, str3, strTrim4);
                            } else if (strTrim3.equalsIgnoreCase("addResources")) {
                                if (customUnitConfig == null) {
                                    throw new ConfigParseException("[" + str2 + "]" + str3 + " addResources not supported from here");
                                }
                                try {
                                    spawnConfig.addResources = UnitPrice.b(customUnitConfig, strTrim4);
                                } catch (ConfigParseException e) {
                                    e.printStackTrace();
                                    throw new ConfigParseException("[" + str2 + "]" + str3 + " addResources:" + e.getMessage());
                                }
                            } else if (strTrim3.equalsIgnoreCase("spawnSource")) {
                                spawnConfig.spawnSource = IniFile.getLogicBooleanBlock(strTrim4, customUnitConfig, str2, str3, (LogicBoolean) null);
                            } else if (strTrim3.equalsIgnoreCase("copyWaypointsFrom")) {
                                spawnConfig.copyWaypointsFrom = IniFile.getLogicBooleanBlock(strTrim4, customUnitConfig, str2, str3, (LogicBoolean) null);
                            } else {
                                throw new ConfigParseException("[" + str2 + "]" + str3 + " UnitList: Unknown parameter '" + strTrim3 + "' for '" + strTrim + "' of " + str);
                            }
                        }
                    }
                    if (spawnConfig.setToTeamOfLastAttacker && spawnConfig.neutralTeam) {
                        throw new ConfigParseException("[" + str2 + "]" + str3 + " Cannot set setToTeamOfLastAttacker and neutralTeam at same time in " + str);
                    }
                    if (spawnConfig.aggressiveTeam && spawnConfig.neutralTeam) {
                        throw new ConfigParseException("[" + str2 + "]" + str3 + " Cannot set aggressiveTeam and neutralTeam at same time in " + str);
                    }
                    if (spawnConfig.aggressiveTeam && spawnConfig.setToTeamOfLastAttacker) {
                        throw new ConfigParseException("[" + str2 + "]" + str3 + " Cannot set aggressiveTeam and setToTeamOfLastAttacker at same time in " + str);
                    }
                }
                unitSpawner.spawnQueue.add(spawnConfig);
            }
        }
        if (z && (iA = unitSpawner.a()) > 1) {
            throw new ConfigParseException("[" + str2 + "]" + str3 + " Too many units: " + iA + ", only single unit is allowed here");
        }
        return unitSpawner;
    }

    public int a() {
        if (this.spawnQueue == null || this.spawnQueue.size() == 0) {
            return 0;
        }
        int i = 0;
        Iterator it = this.spawnQueue.iterator();
        while (it.hasNext()) {
            i += ((SpawnConfig) it.next()).spawnCount;
        }
        return i;
    }

    public boolean b() {
        if (this.spawnQueue == null || this.spawnQueue.size() == 0) {
            return true;
        }
        return false;
    }

    public void a(FastArrayList fastArrayList, PlayerTeam playerTeam, BaseUnit baseUnit, boolean z) {
        a(0.0f, 0.0f, 0.0f, 0.0f, playerTeam, false, baseUnit, fastArrayList, z);
    }

    public void a(float f, float f2, float f3, float f4, PlayerTeam playerTeam, boolean z, BaseUnit baseUnit) {
        a(f, f2, f3, f4, playerTeam, z, baseUnit, null, false);
    }


    public void a(
            float float1,
            float float2,
            float float3,
            float float4,
            PlayerTeam n,
            boolean boolean6,
            BaseUnit am,
            FastArrayList m,
            boolean boolean9
    ) {
        if (this.spawnQueue != null && this.spawnQueue.size() != 0) {
            boolean var10 = false;
            GameEngine var11 = GameEngine.getInstance();
            int var12 = 0;
            int var13 = 0;

            for (SpawnConfig var15 : this.spawnQueue) {
                PlayerTeam var16 = n;
                BaseUnit var17 = am;
                float var18 = float1;
                float var19 = float2;
                float var20 = float3;
                float var21 = float4;
                if (var15.spawnSource != null) {
                    if (!(am instanceof OrderableUnit)) {
                        GameEngine.updatePaintTextSizeIfNeeded("spawnUnitsAt: sourceUnit!=OrderableUnit is:" + BaseUnit.serialize(am));
                        continue;
                    }

                    BaseUnit var22 = var15.spawnSource.readUnit((OrderableUnit)am);
                    if (var22 == null) {
                        GameEngine.updatePaintTextSizeIfNeeded("spawnUnitsAt: spawnSource==null");
                        continue;
                    }

                    var16 = var22.team;
                    var17 = var22;
                    var18 = var22.posX;
                    var19 = var22.posY;
                    var20 = var22.posZ;
                    var21 = var22.rotationSpeed;
                    if (var16 == null) {
                        GameEngine.updatePaintTextSizeIfNeeded("spawnUnitsAt: newSpawnSource.team==null");
                        continue;
                    }
                }

                if (!boolean9) {
                    if (var16.getTeamUnitCountInt() > var16.getTeamBuildingCountInt() + 300) {
                        var10 = true;
                    }
                } else if (var16.addUnitToTeam(true, false) > var16.getTeamBuildingCountInt() + 20000) {
                    var10 = true;
                }

                if (var10) {
                    String var34 = "";
                    if (var17 != null) {
                        var34 = var34 + "source:" + var17.getVelocityX();
                    }

                    GameEngine.updatePaintTextSizeIfNeeded("spawnUnitsAt: Skipping, too many units already on team:" + var16.teamId + " count:" + var16.getTeamUnitCountInt() + " " + var34);
                    if (GameEngine.getInstance().isNetworkGameActive) {
                        var16.updateTeamTextures();
                    }
                } else if (var16.getTeamPing() > var16.getTeamBuildingCountInt() + 25000) {
                    String var33 = "";
                    if (var17 != null) {
                        var33 = var33 + "source:" + var17.getVelocityX();
                    }

                    GameEngine.updatePaintTextSizeIfNeeded(
                            "spawnUnitsAt: Failsafe, too many units already on team (including ignored):" + var16.teamId + " total count:" + var16.getTeamPing() + " " + var33
                    );
                    if (GameEngine.getInstance().isNetworkGameActive) {
                        var16.updateTeamTextures();
                    }
                } else {
                    UnitType var32 = var15.unitType.c();
                    if (var32 != null) {
                        for (int var23 = 0; var23 < var15.spawnCount; var23++) {
                            var13++;
                            PlayerTeam var24 = var16;
                            if (var15.spawnChance < 1.0F) {
                                float var25 = Utility.copyStream(var17, 0.0F, 1.0F, var13);
                                if (var25 > var15.spawnChance) {
                                    continue;
                                }
                            }

                            if (var15.setToTeamOfLastAttacker) {
                                if (var17 == null || var17.unitTarget1 == null) {
                                    continue;
                                }

                                var24 = var17.unitTarget1.team;
                                if (var24 == null) {
                                    throw new RuntimeException("setToTeamOfLastAttacker targetTeam==null");
                                }
                            }

                            if (var12 < var15.maxSpawnLimit) {
                                BaseUnit var35 = var32.a();
                                if (var15.neutralTeam) {
                                    var24 = PlayerTeam.TEAM_ALL;
                                }

                                if (var15.aggressiveTeam) {
                                    var24 = PlayerTeam.TEAM_UNKNOWN;
                                }

                                if (var24 == null) {
                                    throw new RuntimeException("Team==null");
                                }

                                var35.f(var24);
                                var35.getUnitType(var17);
                                var35.posX = var18;
                                var35.posY = var19;
                                var35.posZ = var20;
                                if (!var35.bI() && !var15.alwaysStartDirAtZero) {
                                    var35.rotationSpeed = var21;
                                }

                                var35.posZ = var35.posZ + var15.offsetHeight;
                                if (var15.techLevel != -1 && var35 instanceof OrderableUnit) {
                                    ((OrderableUnit)var35).a(var15.techLevel);
                                }

                                float var26 = var15.offsetDir;
                                if (var15.offsetRandomDir != 0.0F) {
                                    var26 += Utility.copyStream(var17, -var15.offsetRandomDir, var15.offsetRandomDir, var13 * 4 + 3);
                                }

                                if (var26 != 0.0F) {
                                    if (var35 instanceof OrderableUnit) {
                                        ((OrderableUnit)var35).addRotation(var26);
                                    } else {
                                        var35.rotationSpeed += var26;
                                    }
                                }

                                var35.posX += var23;
                                if (var15.offsetRandomX != 0.0F) {
                                    var35.posX = var35.posX + Utility.copyStream(var17, -var15.offsetRandomX, var15.offsetRandomX, var13 * 2 + 1);
                                }

                                if (var15.offsetRandomY != 0.0F) {
                                    var35.posY = var35.posY + Utility.copyStream(var17, -var15.offsetRandomY, var15.offsetRandomY, var13 * 3 + 2);
                                }

                                if (var15.gridAlign) {
                                    var11.tileMap.exportTmxToFile(var35.posX, var35.posY);
                                    var35.posX = var11.tileMap.cursorTileX;
                                    var35.posY = var11.tileMap.cursorTileY;
                                    var35.posX = var35.posX + var35.getUnitAIState();
                                    var35.posY = var35.posY + var35.getUnitAIPathfindStatus();
                                }

                                var35.posX = var35.posX + var15.offsetX;
                                var35.posY = var35.posY + var15.offsetY;
                                var12++;
                                if (var15.skipIfOverlapping && var35 instanceof OrderableUnit && !((OrderableUnit)var35).canPlaceAtCurrentPosition(null)) {
                                    var35.getUnitAICondition();
                                } else {
                                    if (var15.falling && var35 instanceof OrderableUnit) {
                                        var35.getUnitAICombatState();
                                    }

                                    if (var15.addResources != null) {
                                        var15.addResources.h(var35);
                                    }

                                    if (var15.transportedUnitsToTransfer > 0 && var17 != null && var17 instanceof CustomUnit) {
                                        CustomUnit var27 = (CustomUnit)var17;
                                        int var28 = var15.transportedUnitsToTransfer;
                                        if (var27.transportedUnits != null) {
                                            for (; var28 > 0; var28--) {
                                                int var29 = -1;

                                                for (int var30 = var27.transportedUnits.size() - 1; var30 >= 0; var30--) {
                                                    BaseUnit var31 = (BaseUnit)var27.transportedUnits.get(var30);
                                                    if (var35.isUnitArmorEffective(var31, true)) {
                                                        var29 = var30;
                                                        break;
                                                    }
                                                }

                                                if (var29 == -1) {
                                                    break;
                                                }

                                                BaseUnit var37 = (BaseUnit)var27.transportedUnits.remove(var29);
                                                GameViewUtils.a(var37, var27);
                                                var27.unloadTransportedUnit(var37);
                                                var37.posX = var35.posX;
                                                var37.posY = var35.posY;
                                                var37.rotationSpeed = var35.rotationSpeed;
                                                if (var37 instanceof OrderableUnit) {
                                                    OrderableUnit var38 = (OrderableUnit)var37;
                                                    var38.clearAllWaypoints();
                                                }

                                                if (!var35.e(var37, true)) {
                                                    GameEngine.updatePaintTextSizeIfNeeded("transportedUnitsToTransfer failed for: " + var37.getVelocityX() + " to: " + var35.getVelocityX());
                                                    var37.getUnitAICondition();
                                                }
                                            }
                                        }
                                    }

                                    PlayerTeam.c(var35);
                                    if (var35.bI() && var35 instanceof OrderableUnit) {
                                        var11.pathfindingEngine.a((OrderableUnit)var35);
                                    }

                                    if (boolean6 && !var35.u()) {
                                        GameEngine.getInstance().gameUI.addToSelection(var35);
                                    }

                                    if (var15.copyWaypointsFrom != null) {
                                        if (!(var35 instanceof OrderableUnit)) {
                                            GameEngine.updatePaintTextSizeIfNeeded("copyWaypointsFrom: spawnedUnit!=OrderableUnit is:" + BaseUnit.serialize(var17));
                                        } else {
                                            BaseUnit var36 = var15.copyWaypointsFrom.readUnit((OrderableUnit)am);
                                            if (var36 != null) {
                                                if (!(var36 instanceof OrderableUnit)) {
                                                    GameEngine.updatePaintTextSizeIfNeeded("copyWaypointsFrom: copyWaypointsFrom!=OrderableUnit is:" + BaseUnit.serialize(var17));
                                                } else {
                                                    OrderableUnit.copyWaypoints((OrderableUnit)var36, (OrderableUnit)var35);
                                                }
                                            }
                                        }
                                    }

                                    if (m != null) {
                                        m.add(var35);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @Deprecated
    public static UnitSpawner a(GameInputStream gameInputStream, boolean z) throws IOException {
        int i = gameInputStream.readInt();
        if (z && i == 0) {
            return null;
        }
        UnitSpawner unitSpawner = new UnitSpawner();
        for (int i2 = 0; i2 < i; i2++) {
            SpawnConfig spawnConfig = new SpawnConfig(null);
            UnitType unitTypeQ = gameInputStream.q();
            if (unitTypeQ != null) {
                if (unitSpawner.spawnQueue == null) {
                    unitSpawner.spawnQueue = new FastArrayList();
                }
                spawnConfig.unitType = CustomUnitConfig.a(unitTypeQ);
            }
            if (gameInputStream.getProtocolVersion() >= 75 && gameInputStream.readBoolean()) {
                spawnConfig.spawnCount = gameInputStream.readInt();
                spawnConfig.neutralTeam = gameInputStream.readBoolean();
                spawnConfig.setToTeamOfLastAttacker = gameInputStream.readBoolean();
                if (gameInputStream.getProtocolVersion() >= 76) {
                    spawnConfig.spawnChance = gameInputStream.readFloat();
                }
            }
            if (unitTypeQ != null) {
                unitSpawner.spawnQueue.add(spawnConfig);
            }
        }
        return unitSpawner;
    }
}
