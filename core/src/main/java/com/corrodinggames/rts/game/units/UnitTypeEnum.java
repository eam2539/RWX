package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.SelectUnitTypeAction;
import com.corrodinggames.rts.game.units.air.*;
import com.corrodinggames.rts.game.units.bug.Ladybug;
import com.corrodinggames.rts.game.units.buildings.*;
import com.corrodinggames.rts.game.units.buildings.turrets.AntiAirTurret;
import com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.PlacementRules;
import com.corrodinggames.rts.game.units.custom.condition.StoredResources;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.land.*;
import com.corrodinggames.rts.game.units.sea.*;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.RenderTargetMode;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.local.Locale;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ar */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ar.class */
public enum UnitTypeEnum implements UnitType {
    extractor { // from class: com.corrodinggames.rts.game.units.ar.1

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new ResourceExtractor(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            ResourceExtractor.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 700;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: c */
        public int getUpgradeCost(int i) {
            if (i == 2) {
                return 1200;
            }
            if (i == 3) {
                return 2500;
            }
            return 0;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean p() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int a(BaseUnit baseUnit) {
            if (baseUnit.isOverWater()) {
                return 110;
            }
            return 0;
        }
    },
    landFactory { // from class: com.corrodinggames.rts.game.units.ar.12

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new LandFactory(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            LandFactory.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 700;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: c */
        public int getUpgradeCost(int i) {
            if (i == 2) {
                return 2000;
            }
            return 0;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
            LandFactory.addAvailableActions(arrayList, i);
        }
    },
    airFactory { // from class: com.corrodinggames.rts.game.units.ar.23

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new AirFactory(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            AirFactory.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: c */
        public int getUpgradeCost(int i) {
            if (i == 2) {
                return 1500;
            }
            return 0;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
            AirFactory.addAvailableActions(arrayList, i);
        }
    },
    seaFactory { // from class: com.corrodinggames.rts.game.units.ar.34

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new SeaFactory(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            SeaFactory.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: c */
        public int getUpgradeCost(int i) {
            if (i == 2) {
                return 2000;
            }
            return 0;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 7.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
            SeaFactory.addAvailableActions(arrayList, i);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int a(BaseUnit baseUnit) {
            return 110;
        }
    },
    commandCenter { // from class: com.corrodinggames.rts.game.units.ar.45

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new CommandCenter(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            CommandCenter.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 3000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 5.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
            CommandCenter.addAvailableActions(arrayList, i);
        }
    },
    turret { // from class: com.corrodinggames.rts.game.units.ar.50

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new TurretFactory(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            TurretFactory.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 500;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    antiAirTurret { // from class: com.corrodinggames.rts.game.units.ar.51

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new AntiAirTurret(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            AntiAirTurret.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 600;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 8.0E-4f;
        }
    },
    builder { // from class: com.corrodinggames.rts.game.units.ar.52

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new BuilderUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            BuilderUnit.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 500;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.002f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean l() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean m() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean n() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
            BuilderUnit.addAvailableActions(arrayList, i);
            EditorOrBuilder.a((ArrayList) null, i);
        }
    },
    tank { // from class: com.corrodinggames.rts.game.units.ar.53

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new TankUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            TankUnit.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 350;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.002f;
        }
    },
    hoverTank { // from class: com.corrodinggames.rts.game.units.ar.2

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new HoverTankUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            HoverTankUnit.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 450;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.002f;
        }
    },
    artillery { // from class: com.corrodinggames.rts.game.units.ar.3

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new Artillery(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            Artillery.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 900;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.0014f;
        }
    },
    helicopter { // from class: com.corrodinggames.rts.game.units.ar.4

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new Helicopter(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            Helicopter.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 650;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.0012f;
        }
    },
    airShip { // from class: com.corrodinggames.rts.game.units.ar.5

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new AirShip(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            AirShip.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 600;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.002f;
        }
    },
    gunShip { // from class: com.corrodinggames.rts.game.units.ar.6

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new Gunship(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            Gunship.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 800;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 2;
        }
    },
    missileShip { // from class: com.corrodinggames.rts.game.units.ar.7

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new MissileShip(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            MissileShip.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 900;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }
    },
    gunBoat { // from class: com.corrodinggames.rts.game.units.ar.8

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new GunBoat(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            GunBoat.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 300;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.005f;
        }
    },
    megaTank { // from class: com.corrodinggames.rts.game.units.ar.9

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new MegaTankUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            MegaTankUnit.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 800;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.0015f;
        }
    },
    laserTank { // from class: com.corrodinggames.rts.game.units.ar.10

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new LaserTankUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            LaserTankUnit.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1300;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.0013f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 2;
        }
    },
    hovercraft { // from class: com.corrodinggames.rts.game.units.ar.11

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new HovercraftUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            HovercraftUnit.L();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 600;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.003f;
        }
    },
    ladybug { // from class: com.corrodinggames.rts.game.units.ar.13

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new Ladybug(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            Ladybug.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 400;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.004f;
        }
    },
    battleShip { // from class: com.corrodinggames.rts.game.units.ar.14

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new BattleShip(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            BattleShip.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1500;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }
    },
    tankDestroyer { // from class: com.corrodinggames.rts.game.units.ar.15

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new TankDestroyer(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            TankDestroyer.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 800;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.003f;
        }
    },
    heavyTank { // from class: com.corrodinggames.rts.game.units.ar.16

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new HeavyTankUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            HeavyTankUnit.loadHeavyTankTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 800;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.0011f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 2;
        }
    },
    heavyHoverTank { // from class: com.corrodinggames.rts.game.units.ar.17

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new HeavyHoverTank(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            HeavyHoverTank.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 2;
        }
    },
    laserDefence { // from class: com.corrodinggames.rts.game.units.ar.18

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new LaserDefense(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            LaserDefense.initializeTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1200;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: c */
        public int getUpgradeCost(int i) {
            if (i == 2) {
                return 2000;
            }
            return 0;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }
    },
    dropship { // from class: com.corrodinggames.rts.game.units.ar.19

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new Dropship(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            Dropship.L();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 800;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 2;
        }
    },
    tree { // from class: com.corrodinggames.rts.game.units.ar.20

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new Tree(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            Tree.b();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 0;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.0025f;
        }
    },
    repairbay { // from class: com.corrodinggames.rts.game.units.ar.21

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new RepairBay(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            RepairBay.initializeTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1500;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }
    },
    NukeLaucher { // from class: com.corrodinggames.rts.game.units.ar.22

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new NukeLauncher(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            NukeLauncher.b();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 45000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 1.0E-4f;
        }
    },
    AntiNukeLaucher { // from class: com.corrodinggames.rts.game.units.ar.24

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new AntiNukeLauncher(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            AntiNukeLauncher.initializeTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 15000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 7.0E-4f;
        }
    },
    mammothTank { // from class: com.corrodinggames.rts.game.units.ar.25

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new MammothTank(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            MammothTank.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 3900;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 9.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 3;
        }
    },
    experimentalTank { // from class: com.corrodinggames.rts.game.units.ar.26

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new ExperimentalTank(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            ExperimentalTank.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 14000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 2.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 3;
        }
    },
    experimentalLandFactory { // from class: com.corrodinggames.rts.game.units.ar.27

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new ExperimentalLandFactory(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            ExperimentalLandFactory.b();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 11000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 3.5E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
            ExperimentalLandFactory.a(arrayList, i);
        }
    },
    crystalResource { // from class: com.corrodinggames.rts.game.units.ar.28

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new CrystalResource(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            CrystalResource.a_();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 5000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }
    },
    wall_v { // from class: com.corrodinggames.rts.game.units.ar.29

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new WallVertical(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            WallVertical.b();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 100;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.003f;
        }
    },
    fabricator { // from class: com.corrodinggames.rts.game.units.ar.30

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new PowerFabricator(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            PowerFabricator.loadTextures();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1500;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: c */
        public int getUpgradeCost(int i) {
            if (i == 2) {
                return 3000;
            }
            if (i == 3) {
                return 5000;
            }
            return 0;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    attackSubmarine { // from class: com.corrodinggames.rts.game.units.ar.31

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new AttackSubmarine(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            AttackSubmarine.b();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 800;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }
    },
    builderShip { // from class: com.corrodinggames.rts.game.units.ar.32

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new BuilderShip(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            BuilderShip.t_();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 500;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean l() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean m() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
            BuilderShip.a(arrayList, i);
        }
    },
    amphibiousJet { // from class: com.corrodinggames.rts.game.units.ar.33

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new AmphibiousJet(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            AmphibiousJet.L();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 2000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 2;
        }
    },
    supplyDepot { // from class: com.corrodinggames.rts.game.units.ar.35

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new SupplyDepot(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            SupplyDepot.K();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.001f;
        }
    },
    experimentalHoverTank { // from class: com.corrodinggames.rts.game.units.ar.36

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new ExperimentalHoverTank(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            ExperimentalHoverTank.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 21000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 2.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int g() {
            return 3;
        }
    },
    turret_artillery { // from class: com.corrodinggames.rts.game.units.ar.37

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            TurretFactory turretFactory = new TurretFactory(z);
            turretFactory.a_("artillery");
            return turretFactory;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return UnitTypeEnum.turret.c() + TurretFactory.upgradeToArtilleryAction.getCostAmount();
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    turret_flamethrower { // from class: com.corrodinggames.rts.game.units.ar.38

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            TurretFactory turretFactory = new TurretFactory(z);
            turretFactory.a_("flamethrower");
            return turretFactory;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return UnitTypeEnum.turret.c() + TurretFactory.upgradeToFlamethrowerAction.getCostAmount();
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    fogRevealer { // from class: com.corrodinggames.rts.game.units.ar.39

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new FogRevealer(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            FogRevealer.f();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    spreadingFire { // from class: com.corrodinggames.rts.game.units.ar.40

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new FireUnit(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            FireUnit.b();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    antiAirTurretT2 { // from class: com.corrodinggames.rts.game.units.ar.41

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            AntiAirTurret antiAirTurret = new AntiAirTurret(z);
            antiAirTurret.a(2);
            return antiAirTurret;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return UnitTypeEnum.turret.c() + AntiAirTurret.upgradeToLevel2Action.getCostAmount();
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    turretT2 { // from class: com.corrodinggames.rts.game.units.ar.42

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            TurretFactory turretFactory = new TurretFactory(z);
            turretFactory.a_("gunT2");
            return turretFactory;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return UnitTypeEnum.turret.c() + TurretFactory.upgradeToT2Action.getCostAmount();
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    turretT3 { // from class: com.corrodinggames.rts.game.units.ar.43

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean isBuildingUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            TurretFactory turretFactory = new TurretFactory(z);
            turretFactory.a_("gunT3");
            return turretFactory;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return UnitTypeEnum.turret.c() + TurretFactory.upgradeToT2Action.getCostAmount() + TurretFactory.upgradeToT3Action.getCostAmount();
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 3.0E-4f;
        }
    },
    damagingBorder { // from class: com.corrodinggames.rts.game.units.ar.44

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: A */
        public boolean createUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new DamageZone(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            DamageZone.d_();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    zoneMarker { // from class: com.corrodinggames.rts.game.units.ar.46

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: A */
        public boolean createUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            DamageZone damageZone = new DamageZone(z);
            damageZone.isZoneMarker = true;
            return damageZone;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            DamageZone.d_();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 6.0E-4f;
        }
    },
    editorOrBuilder { // from class: com.corrodinggames.rts.game.units.ar.47

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: A */
        public boolean createUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new EditorOrBuilder(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            EditorOrBuilder.K();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 500;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 0.002f;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean l() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean m() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public boolean n() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public void addUnitsToList(ArrayList arrayList, int i) {
        }
    },
    dummyNonUnitWithTeam { // from class: com.corrodinggames.rts.game.units.ar.48

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        /* JADX INFO: renamed from: e */
        public String getUnitName() {
            return getUnitTypeDescriptionShort();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        /* JADX INFO: renamed from: i */
        public String getUnitTypeDescriptionShort() {
            return "marker";
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: A */
        public boolean createUnit() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public boolean isAvailableInDemo() {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: a */
        public BaseUnit createUnitInstanceWithBoolean(boolean z) {
            return new DummyNonUnitWithTeam(z);
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum
        /* JADX INFO: renamed from: b */
        public void abstractMethodB() {
            DummyNonUnitWithTeam.b();
        }

        @Override // com.corrodinggames.rts.game.units.UnitTypeEnum, com.corrodinggames.rts.game.units.UnitType
        public int c() {
            return 9999;
        }

        @Override // com.corrodinggames.rts.game.units.UnitType
        public float D() {
            return 1.0f;
        }
    };


    /* JADX INFO: renamed from: aa */
    SelectUnitTypeAction buildAction;

    /* JADX INFO: renamed from: ab */
    int buildPriority;
    String ac;
    String ad;
    public static ArrayList<UnitType> ae;
    UnitContainer[] af;
    public static boolean ag;
    UnitPrice ah;

    /* JADX INFO: renamed from: a */
    public abstract BaseUnit createUnitInstanceWithBoolean(boolean z);

    /* JADX INFO: renamed from: b */
    public abstract void abstractMethodB();

    @Override // com.corrodinggames.rts.game.units.UnitType
    public abstract int c();

    UnitTypeEnum() {
        this.buildAction = new SelectUnitTypeAction(this);
        this.buildPriority = -1;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public BaseUnit a() {
        return createUnitInstanceWithBoolean(false);
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public SelectUnitTypeAction d() {
        return this.buildAction;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    /* JADX INFO: renamed from: e */
    public String getUnitName() {
        if (this.buildPriority != Locale.reloadCount || this.ac == null) {
            this.buildPriority = Locale.reloadCount;
            String str = "units." + name() + ".name";
            this.ac = Locale.getFormattedString(str, null, new Object[0]);
            if (this.ac == null) {
                if (GameEngine.getInstance().usesCoreUnitTypes() && !createUnit()) {
                    throw new RuntimeException("Can't find translation text for: " + str);
                }
                this.ac = name();
            }
        }
        return this.ac;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public String f() {
        if (this.buildPriority != Locale.reloadCount || this.ad == null) {
            this.buildPriority = Locale.reloadCount;
            String str = "units." + name() + ".description";
            this.ad = Locale.getFormattedString(str, null, new Object[0]);
            if (this.ad == null) {
                if (GameEngine.getInstance().usesCoreUnitTypes() && !createUnit()) {
                    throw new RuntimeException("Can't find translation text for: " + str);
                }
                this.ad = VariableScope.nullOrMissingString;
            }
        }
        return this.ad;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public int g() {
        return 1;
    }

    /* JADX INFO: renamed from: a */
    public void addUnitsToList(ArrayList arrayList, int i) {
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public void h() {
        UnitContainer[] unitContainerArr = new UnitContainer[3];
        for (int i = 1; i <= 3; i++) {
            UnitContainer unitContainer = new UnitContainer();
            addUnitsToList(unitContainer.a, i);
            unitContainerArr[i - 1] = unitContainer;
        }
        this.af = unitContainerArr;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public ArrayList a(int i) {
        if (i > 3) {
            throw new RuntimeException("Tech level:" + i + " greater than maxTechLevel");
        }
        return this.af[i - 1].a;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    /* JADX INFO: renamed from: i */
    public String getUnitTypeDescriptionShort() {
        return name();
    }

    /* JADX INFO: renamed from: a */
    public static void drawUnitWithBoolean(UnitType unitType, float f, float f2, float f3, float f4, PlayerTeam playerTeam, float f5, float f6, boolean z, boolean z2, int i, boolean z3, BaseUnit baseUnit) {
        GameEngine gameEngine = GameEngine.getInstance();
        BaseUnit baseUnitCanAttack = BaseUnit.getPrototypeForUnitType(unitType);
        boolean zBI = baseUnitCanAttack.bI();
        baseUnitCanAttack.setUnitTeam(playerTeam);
        if (baseUnitCanAttack instanceof OrderableUnit) {
            ((OrderableUnit) baseUnitCanAttack).a(i);
        }
        baseUnitCanAttack.posZ = f4;
        if (baseUnitCanAttack.getMovementType() == UnitMovementType.HOVER || baseUnitCanAttack.getMovementType() == UnitMovementType.OVER_CLIFF || baseUnitCanAttack.getMovementType() == UnitMovementType.OVER_CLIFF_WATER) {
            baseUnitCanAttack.posZ += 4.0f;
        }
        if (baseUnitCanAttack.getMovementType() == UnitMovementType.AIR) {
            baseUnitCanAttack.posZ += 10.0f;
        }
        if (!zBI) {
            baseUnitCanAttack.rotationSpeed = f3;
            if (baseUnitCanAttack instanceof OrderableUnit) {
                ((OrderableUnit) baseUnitCanAttack).j(f3);
            }
        } else {
            baseUnitCanAttack.rotationSpeed = -90.0f;
        }
        boolean z4 = true;
        boolean z5 = baseUnitCanAttack.isUnitParalyzed;
        baseUnitCanAttack.isUnitParalyzed = true;
        baseUnitCanAttack.isUnitDisabled = false;
        baseUnitCanAttack.isUnitCapturable = false;
        if (!z3) {
            baseUnitCanAttack.isUnitCapturable = true;
        }
        baseUnitCanAttack.isUnitStunned = false;
        baseUnitCanAttack.isUnitInvulnerable = false;
        baseUnitCanAttack.isUnitUntargetable = false;
        if (z || z2) {
            baseUnitCanAttack.isUnitInvulnerable = z2;
            baseUnitCanAttack.isUnitUntargetable = z;
            z4 = false;
        } else {
            baseUnitCanAttack.isUnitStunned = true;
        }
        if (!z4) {
            baseUnitCanAttack.posX = f;
            baseUnitCanAttack.posY = f2;
        } else {
            baseUnitCanAttack.posX = gameEngine.viewpointXSnapped + f;
            baseUnitCanAttack.posY = gameEngine.viewpointYSnapped + f2;
        }
        float f7 = baseUnitCanAttack.radius * 2.0f * 0.8f;
        if (baseUnitCanAttack instanceof OrderableUnit) {
            OrderableUnit orderableUnit = (OrderableUnit) baseUnitCanAttack;
            if (orderableUnit.baseTexture != null) {
                float fCD = orderableUnit.et * orderableUnit.getRenderScale();
                if (fCD > f7) {
                    f7 = fCD;
                }
            }
        }
        float f8 = 1.0f;
        if (f7 < f5) {
            f8 = f5 / f7;
        }
        if (f7 > f6) {
            f8 = f6 / f7;
        }
        gameEngine.renderGraphicsEngine.k();
        if (z4) {
        }
        if (f8 != 1.0f) {
            gameEngine.renderGraphicsEngine.a(f8, f8, f, f2);
        }
        if (f8 < 1.0f) {
            ag = true;
        } else {
            ag = false;
        }
        if (baseUnit != null) {
            StoredResources storedResources = baseUnitCanAttack.unitCustomEffects;
            baseUnitCanAttack.unitCustomEffects = baseUnit.unitCustomEffects;
            int i2 = baseUnitCanAttack.ammo;
            baseUnitCanAttack.ammo = baseUnit.ammo;
            float f9 = baseUnitCanAttack.currentHealth;
            baseUnitCanAttack.currentHealth = baseUnit.currentHealth;
            float f10 = baseUnitCanAttack.currentEnergy;
            baseUnitCanAttack.currentEnergy = baseUnit.currentEnergy;
            VariableScope variableScope = baseUnitCanAttack.unitVariables;
            baseUnitCanAttack.unitVariables = baseUnit.unitVariables;
            baseUnitCanAttack.d(0.0f);
            baseUnitCanAttack.c(0.0f);
            baseUnitCanAttack.a(0.0f, false);
            baseUnitCanAttack.unitCustomEffects = storedResources;
            baseUnitCanAttack.ammo = i2;
            baseUnitCanAttack.currentHealth = f9;
            baseUnitCanAttack.currentEnergy = f10;
            baseUnitCanAttack.unitVariables = variableScope;
        } else {
            baseUnitCanAttack.d(0.0f);
            baseUnitCanAttack.c(0.0f);
            baseUnitCanAttack.a(0.0f, false);
        }
        gameEngine.renderGraphicsEngine.l();
        baseUnitCanAttack.posZ = 0.0f;
        if (!zBI) {
            baseUnitCanAttack.rotationSpeed = 0.0f;
        } else {
            baseUnitCanAttack.rotationSpeed = -90.0f;
        }
        if (baseUnitCanAttack instanceof OrderableUnit) {
            OrderableUnit orderableUnit2 = (OrderableUnit) baseUnitCanAttack;
            orderableUnit2.j(0.0f);
            orderableUnit2.a(1);
        }
        baseUnitCanAttack.isUnitInvulnerable = false;
        baseUnitCanAttack.isUnitUntargetable = false;
        baseUnitCanAttack.isUnitParalyzed = z5;
        baseUnitCanAttack.isUnitStunned = false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean isBuildingUnit() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean l() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean m() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean n() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean k() {
        return isBuildingUnit();
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean p() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public PlacementRules q() {
        return null;
    }

    /* JADX INFO: renamed from: a */
    public static UnitType getUnitTypeByName(String str) {
        return getUnitTypeByNameWithBoolean(str, true);
    }

    /* JADX INFO: renamed from: a */
    public static UnitType getUnitTypeByNameWithBoolean(String str, boolean z) {
        UnitType unitTypeFindUnitTypeByShortName;
        if (z && (unitTypeFindUnitTypeByShortName = CustomUnitConfig.findUnitTypeByShortName(str)) != null) {
            return unitTypeFindUnitTypeByShortName;
        }
        for (UnitTypeEnum unitTypeEnum : values()) {
            if (unitTypeEnum.name().equalsIgnoreCase(str)) {
                return unitTypeEnum;
            }
        }
        CustomUnitConfig customUnitConfigFindConfigByName = CustomUnitConfig.findConfigByName(str);
        if (customUnitConfigFindConfigByName != null) {
            return customUnitConfigFindConfigByName;
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    private static String formatStringWithFloat(String str, float f) {
        return formatStringWithFloatAndString(str, f, VariableScope.nullOrMissingString);
    }

    /* JADX INFO: renamed from: a */
    private static String formatStringWithFloatAndString(String str, float f, String str2) {
        String str3 = VariableScope.nullOrMissingString + f;
        if (f % 1.0f == 0.0f) {
            str3 = VariableScope.nullOrMissingString + ((int) f);
        }
        return formatStringWithThreeStrings(str, str3, str2);
    }

    /* JADX INFO: renamed from: a */
    private static String formatStringWithThreeStrings(String str, String str2, String str3) {
        return str + ": " + str2 + str3 + "\n";
    }

    /* JADX INFO: renamed from: a */
    private static int getUnitCountByUnit(OrderableUnit orderableUnit) {
        AbstractUnitAction abstractUnitActionA;
        ActionId actionIdCm = orderableUnit.cm();
        if (actionIdCm != null && (abstractUnitActionA = orderableUnit.validateActionId(actionIdCm)) != null) {
            return abstractUnitActionA.getCostAmount();
        }
        return 0;
    }

    /* JADX INFO: renamed from: r */
    public static void loadAllUnitTypes() {
        new File("output_all_unit_images/").mkdirs();
        for (int i = 0; i < 50; i++) {
            GameEngine.logErrorColored("running outputUnitImages()");
        }
        String[] strArr = {"carrier", "experimentalGunship", "experimentalGunshipLanded", "mech_gun", "ladybug", "spiderBot", "wall_v", "crystalResource", "test_tank", "missing", "fogRevealer", "supplyDepot", "tankDestroyer", "megaTank", "crystal_mid", "mechFlyingLanded"};
        for (UnitType unitType : ae) {
            BaseUnit baseUnitFindTurretPosition = BaseUnit.findTurretPosition(unitType);
            if ((baseUnitFindTurretPosition instanceof OrderableUnit) && !unitType.getUnitTypeDescriptionShort().startsWith("bug") && CustomUnitConfig.c(unitType) == null && (!(unitType instanceof CustomUnitConfig) || ((CustomUnitConfig) unitType).showInEditor)) {
                boolean z = false;
                for (String str : strArr) {
                    if (str.equals(unitType.getUnitTypeDescriptionShort())) {
                        z = true;
                    }
                }
                if (!z) {
                    String str2 = "output_all_unit_images/" + unitType.getUnitTypeDescriptionShort().replace("/", "_").replace("\\", "_") + ".png";
                    GameEngine gameEngine = GameEngine.getInstance();
                    GraphicsEngine graphicsEngine = gameEngine.renderGraphicsEngine;
                    Texture textureB = graphicsEngine.b(100, 100, true);
                    GraphicsEngine graphicsEngineB = graphicsEngine.b(textureB, RenderTargetMode.IMMEDIATE);
                    try {
                        gameEngine.renderGraphicsEngine = graphicsEngineB;
                        try {
                            drawUnitWithBoolean(unitType, textureB.r, textureB.s, 0.0f, 0.0f, PlayerTeam.k(0), 20.0f, 100, false, false, 1, true, null);
                        } finally {
                            gameEngine.renderGraphicsEngine = graphicsEngine;
                        }
                        graphicsEngineB.p();
                        graphicsEngine.a(textureB, new File(str2));
                    } finally {
                        gameEngine.renderGraphicsEngine = graphicsEngine;
                        graphicsEngineB.q();
                        textureB.o();
                    }
                }
            }
        }
    }

    /* JADX INFO: renamed from: s */
    public static void loadUnitTypeImages() {
        int unitCountByUnit;
        for (int i = 0; i < 50; i++) {
            GameEngine.logErrorColored("running printForHelp()");
        }
        String[] strArr = {"carrier", "experimentalGunship", "experimentalGunshipLanded", "mech_gun", "ladybug", "spiderBot", "wall_v", "crystalResource", "test_tank", "missing", "fogRevealer", "supplyDepot", "tankDestroyer", "megaTank", "crystal_mid", "mechFlyingLanded"};
        String str = VariableScope.nullOrMissingString;
        ArrayList<UnitType> arrayList = new ArrayList();
        arrayList.addAll(ae);
        // from class: com.corrodinggames.rts.game.units.ar.49
// java.util.Comparator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        arrayList.sort(Comparator.comparing(UnitType::u));
        for (UnitType unitType : arrayList) {
            BaseUnit baseUnitFindTurretPosition = BaseUnit.findTurretPosition(unitType);
            if ((baseUnitFindTurretPosition instanceof OrderableUnit) && !unitType.getUnitTypeDescriptionShort().startsWith("bug") && CustomUnitConfig.c(unitType) == null && (!(unitType instanceof CustomUnitConfig) || ((CustomUnitConfig) unitType).showInEditor)) {
                if (unitType != editorOrBuilder) {
                    OrderableUnit orderableUnit = (OrderableUnit) baseUnitFindTurretPosition;
                    boolean z = false;
                    for (String str2 : strArr) {
                        if (str2.equals(unitType.getUnitTypeDescriptionShort())) {
                            z = true;
                        }
                    }
                    if (!z) {
                        String str3 = ((((((str + "\n") + "<div class=\"unit\">\n") + "<img src=\"unit:" + unitType.getUnitTypeDescriptionShort() + "\" />\n") + "<h4>" + unitType.getUnitName() + "</h4>\n") + "<p>" + unitType.f().replace("\n", "<br/>") + "</p>\n") + "<pre>") + formatStringWithThreeStrings("Price", "$" + unitType.c(), VariableScope.nullOrMissingString);
                        int unitCountByUnit2 = getUnitCountByUnit(orderableUnit);
                        if (unitCountByUnit2 > 0) {
                            str3 = str3 + formatStringWithThreeStrings("T2 Upgrade Price", "$" + unitCountByUnit2, VariableScope.nullOrMissingString);
                            OrderableUnit orderableUnit2 = (OrderableUnit) unitType.a();
                            orderableUnit2.a(2);
                            if (orderableUnit2.getUpgradeLevel() == 2 && (unitCountByUnit = getUnitCountByUnit(orderableUnit2)) > 0) {
                                str3 = str3 + formatStringWithThreeStrings("T3 Upgrade Price", "$" + unitCountByUnit, VariableScope.nullOrMissingString);
                            }
                        }
                        String str4 = (((str3 + formatStringWithFloat("Hp", orderableUnit.maxHealth)) + formatStringWithFloat("Speed", orderableUnit.getMoveSpeed())) + formatStringWithFloat("Turn speed", orderableUnit.getMaxTurnSpeed())) + formatStringWithFloat("Mass", orderableUnit.getPushMass());
                        if (orderableUnit.canAttack()) {
                            str4 = (str4 + formatStringWithFloat("Shoot Delay", orderableUnit.b(0))) + formatStringWithFloat("Attack Range", orderableUnit.m());
                            float f = 0.0f;
                            float f2 = 0.0f;
                            float f3 = 0.0f;
                            float f4 = 0.0f;
                            int techLevel = orderableUnit.getTechLevel();
                            for (int i2 = 0; i2 < techLevel; i2++) {
                                int i3 = Projectile.a.size;
                                orderableUnit.a((BaseUnit) orderableUnit, i2);
                                if (i3 != Projectile.a.size) {
                                    Projectile projectile = (Projectile) Projectile.a.get(Projectile.a.size - 1);
                                    if (projectile.damage > f) {
                                        f = projectile.damage;
                                    }
                                    if (projectile.splashDamage > f2) {
                                        f2 = projectile.splashDamage;
                                    }
                                    f3 += projectile.damage;
                                    f4 += projectile.splashDamage;
                                }
                            }
                            if (f3 != 0.0f) {
                                String str5 = VariableScope.nullOrMissingString;
                                if (f3 != f) {
                                    str5 = " (total:" + f3 + ")";
                                }
                                str4 = str4 + formatStringWithFloatAndString("Direct Damage", f, str5);
                            }
                            if (f4 != 0.0f) {
                                String str6 = VariableScope.nullOrMissingString;
                                if (f4 != f2) {
                                    str6 = " (total:" + f4 + ")";
                                }
                                str4 = str4 + formatStringWithFloatAndString("Area Damage", f2, str6);
                            }
                        }
                        str = (str4 + "</pre>") + "</div>\n";
                    }
                }
            }
        }
        GameEngine.log(str);
    }

    /* JADX INFO: renamed from: t */
    public static void loadUnitTypeSounds() {
        for (UnitTypeEnum unitTypeEnum : values()) {
            unitTypeEnum.name();
            unitTypeEnum.getUnitName();
            unitTypeEnum.f();
        }
    }

    /* JADX INFO: renamed from: a */
    public static boolean canPlaceUnit(UnitType unitType, float f, float f2, float f3, float f4, PlayerTeam playerTeam) {
        GameEngine.getInstance();
        BaseUnit baseUnitFindTurretPosition = BaseUnit.findTurretPosition(unitType);
        if (baseUnitFindTurretPosition == null) {
            GameEngine.log("isValidHere: Failed to get unit from type:" + unitType);
            return false;
        }
        baseUnitFindTurretPosition.setUnitTeam(playerTeam);
        baseUnitFindTurretPosition.posZ = f4;
        baseUnitFindTurretPosition.posX = f;
        baseUnitFindTurretPosition.posY = f2;
        if (!baseUnitFindTurretPosition.bI()) {
            baseUnitFindTurretPosition.rotationSpeed = f3;
            if (baseUnitFindTurretPosition instanceof OrderableUnit) {
                ((OrderableUnit) baseUnitFindTurretPosition).j(f3);
            }
        }
        boolean zCanPlaceAtCurrentPosition = true;
        if (baseUnitFindTurretPosition instanceof OrderableUnit) {
            zCanPlaceAtCurrentPosition = ((OrderableUnit) baseUnitFindTurretPosition).canPlaceAtCurrentPosition(playerTeam);
        }
        baseUnitFindTurretPosition.posZ = 0.0f;
        baseUnitFindTurretPosition.rotationSpeed = 0.0f;
        return zCanPlaceAtCurrentPosition;
    }

    /* JADX INFO: renamed from: a */
    public static void drawUnit(UnitType unitType, float f, float f2, float f3, float f4, PlayerTeam playerTeam, float f5, float f6, boolean z, boolean z2, int i, BaseUnit baseUnit) {
        drawUnitWithBoolean(unitType, f, f2, f3, f4, playerTeam, f5, f6, z, z2, i, true, baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitMovementType o() {
        BaseUnit baseUnitFindTurretPosition = BaseUnit.findTurretPosition(this);
        if (baseUnitFindTurretPosition == null) {
            throw new RuntimeException("Shared unit is null for:" + name());
        }
        return baseUnitFindTurretPosition.getMovementType();
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public int b(int i) {
        int iC = c();
        if (i >= 2) {
            iC += getUpgradeCost(2);
        }
        if (i >= 3) {
            iC += getUpgradeCost(2);
        }
        return iC;
    }

    /* JADX INFO: renamed from: c */
    public int getUpgradeCost(int i) {
        return 0;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitPrice u() {
        int iC = c();
        if (iC == 0) {
            return UnitPrice.a;
        }
        if (this.ah == null || this.ah.a() != iC) {
            this.ah = UnitPrice.a(iC);
        }
        return this.ah;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitPrice d(int i) {
        return UnitPrice.a(b(i));
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public String v() {
        return name();
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean w() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public AnimationSet x() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public boolean y() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public Texture z() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public int a(BaseUnit baseUnit) {
        return 0;
    }

    /* JADX INFO: renamed from: A */
    public boolean createUnit() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.UnitType
    public UnitPrice B() {
        return null;
    }
}
