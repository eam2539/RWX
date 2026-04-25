package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.hooks.LegInstance;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e.class */
public final class CustomUnitAnimationController {

    /* JADX INFO: renamed from: a */
    public AnimationConfig currentAnimation;

    /* JADX INFO: renamed from: b */
    float currentTime;

    /* JADX INFO: renamed from: c */
    float previousTime;

    /* JADX INFO: renamed from: g */
    boolean forceVisible;

    /* JADX INFO: renamed from: h */
    boolean forceInvisible;

    /* JADX INFO: renamed from: i */
    boolean animationActive;

    /* JADX INFO: renamed from: j */
    int animationId;

    /* JADX INFO: renamed from: m */
    CustomUnit unitInstance;

    /* JADX INFO: renamed from: n */
    float[] baseBlendValues;

    /* JADX INFO: renamed from: d */
    float speedMultiplier = 1.0f;

    /* JADX INFO: renamed from: e */
    boolean animationPlaying = false;

    /* JADX INFO: renamed from: f */
    boolean forceLoop = false;

    /* JADX INFO: renamed from: k */
    float fadeMultiplier = 0.0f;

    /* JADX INFO: renamed from: l */
    float fadeSpeed = 0.05f;

    public CustomUnitAnimationController(CustomUnit customUnit) {
        this.unitInstance = customUnit;
    }

    public void a(AnimationConfig animationConfig, int i) {
        a(animationConfig, i, false);
    }

    public void a(AnimationConfig animationConfig, int i, boolean z) {
        if (animationConfig == null || !animationConfig.a()) {
            return;
        }
        if ((this.animationActive || (this.forceLoop && this.animationPlaying)) && i <= this.animationId && (!z || animationConfig != this.currentAnimation)) {
            return;
        }
        this.animationActive = true;
        if (animationConfig != this.currentAnimation || z || this.forceVisible) {
            float f = 0.0f;
            if (this.currentAnimation != null && this.animationPlaying) {
                f = this.currentAnimation.i;
            }
            this.currentAnimation = animationConfig;
            this.animationId = i;
            c();
            this.forceLoop = z;
            if (z) {
                this.forceInvisible = false;
            } else {
                this.forceInvisible = true;
            }
            this.currentTime = -1.0f;
            this.previousTime = -1.0f;
            this.speedMultiplier = 1.0f;
            this.forceVisible = false;
            float f2 = animationConfig.h;
            if (f > f2) {
                f2 = f;
            }
            if (f2 > 0.0f) {
                this.fadeMultiplier = 1.0f;
                this.fadeSpeed = f2;
            } else {
                this.fadeMultiplier = 0.0f;
            }
        }
        this.animationPlaying = true;
    }

    public void a() {
        if (this.currentAnimation != null) {
            b(true);
        }
        this.animationPlaying = false;
        this.currentAnimation = null;
        this.animationId = -1;
    }

    public void b() {
        if (this.currentAnimation != null) {
            if (!this.forceVisible) {
                float f = this.currentAnimation.i;
                if (f > 0.0f) {
                    this.forceVisible = true;
                    c();
                    this.forceInvisible = false;
                    this.animationId = -1;
                    this.fadeMultiplier = 1.0f;
                    this.fadeSpeed = f;
                    return;
                }
            }
            b(true);
        }
        this.animationPlaying = false;
        this.currentAnimation = null;
        this.animationId = -1;
    }

    public void a(float f) {
        if (!this.animationPlaying) {
            return;
        }
        this.previousTime = this.currentTime;
        if (this.currentTime < 0.0f) {
            this.currentTime = 0.0f;
        }
        float number = this.speedMultiplier;
        if (this.currentAnimation != null && this.currentAnimation.j != null) {
            number *= this.currentAnimation.j.readNumber(this.unitInstance);
        }
        this.currentTime += number * f;
        if (this.forceInvisible && !this.animationActive) {
            b();
        }
        this.animationActive = false;
        if (this.animationPlaying) {
            if (this.fadeMultiplier > 0.0f) {
                this.fadeMultiplier -= this.fadeSpeed * f;
            } else if (this.forceVisible) {
                b();
                return;
            }
            if (!this.forceVisible && this.currentAnimation != null) {
                if (!this.currentAnimation.g) {
                    if (this.currentTime > this.currentAnimation.n) {
                        if (this.forceLoop) {
                            a(false);
                            b();
                            if (!this.forceVisible) {
                                return;
                            }
                        } else {
                            a(false);
                            this.currentTime = 0.0f;
                            this.speedMultiplier = 1.0f;
                        }
                    }
                    if (this.currentTime < 0.0f && !this.forceLoop && number < 0.0f) {
                        this.currentTime = this.currentAnimation.n;
                    }
                } else if (this.currentTime > this.currentAnimation.n) {
                    a(false);
                    this.currentTime = this.currentAnimation.n;
                    this.speedMultiplier = -1.0f;
                } else if (this.currentTime < 0.0f) {
                    this.currentTime = 0.0f;
                    this.speedMultiplier = 1.0f;
                    if (this.forceLoop) {
                        b();
                        if (!this.forceVisible) {
                            return;
                        }
                    }
                }
            }
            boolean z = false;
            if (this.forceVisible) {
                z = true;
            }
            b(z);
        }
    }

    void c() {
        FastArrayList fastArrayList = this.currentAnimation.l;
        if (this.baseBlendValues == null || this.baseBlendValues.length < fastArrayList.size()) {
            this.baseBlendValues = new float[fastArrayList.size()];
        }
        for (int i = 0; i < fastArrayList.size(); i++) {
            CustomUnitAnimationSet customUnitAnimationSet = (CustomUnitAnimationSet) fastArrayList.get(i);
            CustomUnitAnimationType customUnitAnimationType = customUnitAnimationSet.animationType;
            if (customUnitAnimationType == CustomUnitAnimationType.scale) {
                this.baseBlendValues[i] = this.unitInstance.currentFrameTime;
            } else if (customUnitAnimationType == CustomUnitAnimationType.frame) {
                this.baseBlendValues[i] = -99.0f;
            } else if (customUnitAnimationType == CustomUnitAnimationType.legX) {
                if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                    this.baseBlendValues[i] = this.unitInstance.legInstances[customUnitAnimationSet.animationId].p;
                } else {
                    this.baseBlendValues[i] = 0.0f;
                    GameEngine.updatePaintTextSizeIfNeeded("setBaseBlendValues: Target leg out of range for: " + this.unitInstance.r().getUnitTypeDescriptionShort());
                }
            } else if (customUnitAnimationType == CustomUnitAnimationType.legY) {
                if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                    this.baseBlendValues[i] = this.unitInstance.legInstances[customUnitAnimationSet.animationId].q;
                }
            } else if (customUnitAnimationType == CustomUnitAnimationType.legDir) {
                if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                    this.unitInstance.legInstances[customUnitAnimationSet.animationId].r = Utility.countOccurrences(this.unitInstance.legInstances[customUnitAnimationSet.animationId].r, false);
                    this.baseBlendValues[i] = this.unitInstance.legInstances[customUnitAnimationSet.animationId].r;
                }
            } else if (customUnitAnimationType == CustomUnitAnimationType.legHeight) {
                if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                    this.baseBlendValues[i] = this.unitInstance.legInstances[customUnitAnimationSet.animationId].d;
                }
            } else if (customUnitAnimationType == CustomUnitAnimationType.legAlpha) {
                if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                    this.baseBlendValues[i] = this.unitInstance.legInstances[customUnitAnimationSet.animationId].s;
                }
            } else if (customUnitAnimationType != CustomUnitAnimationType.event) {
                this.baseBlendValues[i] = 0.0f;
                GameEngine.updatePaintTextSizeIfNeeded("Unsupported blend type:" + customUnitAnimationType);
            }
        }
    }

    void a(boolean z) {
        FastArrayList fastArrayList = this.currentAnimation.l;
        for (int i = 0; i < fastArrayList.size(); i++) {
            CustomUnitAnimationSet customUnitAnimationSet = (CustomUnitAnimationSet) fastArrayList.get(i);
            if (customUnitAnimationSet.animationType == CustomUnitAnimationType.event) {
                customUnitAnimationSet.a(this.unitInstance, this.previousTime, this.currentTime, z);
            }
        }
    }

    void b(boolean z) {
        float fB;
        FastArrayList fastArrayList = this.currentAnimation.l;
        for (int i = 0; i < fastArrayList.size(); i++) {
            CustomUnitAnimationSet customUnitAnimationSet = (CustomUnitAnimationSet) fastArrayList.get(i);
            CustomUnitAnimationType customUnitAnimationType = customUnitAnimationSet.animationType;
            if (customUnitAnimationType != CustomUnitAnimationType.frame || this.unitInstance.flag3 || z) {
                if (z) {
                    fB = 0.0f;
                    if (customUnitAnimationType == CustomUnitAnimationType.scale) {
                        fB = 1.0f;
                    } else if (customUnitAnimationType == CustomUnitAnimationType.frame) {
                        fB = this.unitInstance.unitConfig.lockTurretWithBody;
                    } else if (customUnitAnimationType == CustomUnitAnimationType.legAlpha) {
                        fB = 1.0f;
                        LegConfig[] legConfigArr = this.unitInstance.unitConfig.energyDisplayName;
                        if (legConfigArr != null && customUnitAnimationSet.animationId < legConfigArr.length) {
                            fB = legConfigArr[customUnitAnimationSet.animationId].r;
                        }
                    }
                } else {
                    fB = customUnitAnimationSet.b(this.currentTime);
                }
                if (this.fadeMultiplier > 0.0f && customUnitAnimationType != CustomUnitAnimationType.frame) {
                    fB = (fB * (1.0f - this.fadeMultiplier)) + (this.baseBlendValues[i] * this.fadeMultiplier);
                }
                if (customUnitAnimationType == CustomUnitAnimationType.frame) {
                    this.unitInstance.animationFrameIndex = (int) fB;
                } else if (customUnitAnimationType == CustomUnitAnimationType.scale) {
                    this.unitInstance.currentFrameTime = fB;
                } else if (customUnitAnimationType == CustomUnitAnimationType.legX) {
                    if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                        LegInstance legInstance = this.unitInstance.legInstances[customUnitAnimationSet.animationId];
                        legInstance.p = fB;
                        legInstance.k = true;
                        legInstance.o = true;
                    }
                } else if (customUnitAnimationType == CustomUnitAnimationType.legY) {
                    if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                        LegInstance legInstance2 = this.unitInstance.legInstances[customUnitAnimationSet.animationId];
                        legInstance2.q = fB;
                        legInstance2.k = true;
                        legInstance2.o = true;
                    }
                } else if (customUnitAnimationType == CustomUnitAnimationType.legDir) {
                    if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                        this.unitInstance.legInstances[customUnitAnimationSet.animationId].r = fB;
                    }
                } else if (customUnitAnimationType == CustomUnitAnimationType.legHeight) {
                    if (this.unitInstance.legInstances != null && customUnitAnimationSet.animationId < this.unitInstance.legInstances.length) {
                        this.unitInstance.legInstances[customUnitAnimationSet.animationId].d = fB;
                    }
                } else if (customUnitAnimationType == CustomUnitAnimationType.legAlpha) {
                    LegInstance[] legInstanceArr = this.unitInstance.legInstances;
                    if (legInstanceArr != null && customUnitAnimationSet.animationId < legInstanceArr.length) {
                        legInstanceArr[customUnitAnimationSet.animationId].s = fB;
                    }
                } else if (customUnitAnimationType != CustomUnitAnimationType.turretX && customUnitAnimationType == CustomUnitAnimationType.event) {
                    customUnitAnimationSet.a(this.unitInstance, this.previousTime, this.currentTime, z);
                }
            }
        }
    }

    public boolean a(AnimationConfig animationConfig) {
        return this.animationPlaying && this.currentAnimation == animationConfig;
    }
}
