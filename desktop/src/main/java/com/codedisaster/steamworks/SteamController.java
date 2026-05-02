package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamController.class */
public class SteamController extends SteamInterface {
    public static final int STEAM_CONTROLLER_MAX_COUNT = 16;
    public static final int STEAM_CONTROLLER_MAX_ANALOG_ACTIONS = 16;
    public static final int STEAM_CONTROLLER_MAX_DIGITAL_ACTIONS = 128;
    public static final int STEAM_CONTROLLER_MAX_ORIGINS = 8;
    public static final long STEAM_CONTROLLER_HANDLE_ALL_CONTROLLERS = -1;
    public static final float STEAM_CONTROLLER_MIN_ANALOG_ACTION_DATA = -1.0f;
    public static final float STEAM_CONTROLLER_MAX_ANALOG_ACTION_DATA = 1.0f;
    private long[] controllerHandles;
    private int[] actionOrigins;

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamController$LEDFlag.class */
    public enum LEDFlag {
        SetColor,
        RestoreUserDefault
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamController$Pad.class */
    public enum Pad {
        Left,
        Right
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamController$Source.class */
    public enum Source {
        None,
        LeftTrackpad,
        RightTrackpad,
        Joystick,
        ABXY,
        Switch,
        LeftTrigger,
        RightTrigger,
        Gyro,
        CenterTrackpad,
        RightJoystick,
        DPad
    }

    private static native boolean init(long j);

    private static native boolean shutdown(long j);

    private static native void runFrame(long j);

    private static native int getConnectedControllers(long j, long[] jArr);

    private static native boolean showBindingPanel(long j, long j2);

    private static native long getActionSetHandle(long j, String str);

    private static native void activateActionSet(long j, long j2, long j3);

    private static native long getCurrentActionSet(long j, long j2);

    private static native long getDigitalActionHandle(long j, String str);

    private static native void getDigitalActionData(long j, long j2, long j3, SteamControllerDigitalActionData steamControllerDigitalActionData);

    private static native int getDigitalActionOrigins(long j, long j2, long j3, long j4, int[] iArr);

    private static native long getAnalogActionHandle(long j, String str);

    private static native void getAnalogActionData(long j, long j2, long j3, SteamControllerAnalogActionData steamControllerAnalogActionData);

    private static native int getAnalogActionOrigins(long j, long j2, long j3, long j4, int[] iArr);

    private static native void stopAnalogActionMomentum(long j, long j2, long j3);

    private static native void triggerHapticPulse(long j, long j2, int i, int i2);

    private static native void triggerRepeatedHapticPulse(long j, long j2, int i, int i2, int i3, int i4, int i5);

    private static native void triggerVibration(long j, long j2, short s, short s2);

    private static native void setLEDColor(long j, long j2, byte b, byte b2, byte b3, int i);

    private static native int getGamepadIndexForController(long j, long j2);

    private static native long getControllerForGamepadIndex(long j, int i);

    private static native void getMotionData(long j, long j2, float[] fArr);

    private static native boolean showDigitalActionOrigins(long j, long j2, long j3, float f, float f2, float f3);

    private static native boolean showAnalogActionOrigins(long j, long j2, long j3, float f, float f2, float f3);

    private static native String getStringForActionOrigin(long j, int i);

    private static native String getGlyphForActionOrigin(long j, int i);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamController$SourceMode.class */
    public enum SourceMode {
        None,
        Dpad,
        Buttons,
        FourButtons,
        AbsoluteMouse,
        RelativeMouse,
        JoystickMove,
        JoystickMouse,
        JoystickCamera,
        ScrollWheel,
        Trigger,
        TouchMenu,
        MouseJoystick,
        MouseRegion,
        RadialMenu,
        Switches;

        private static final SourceMode[] values = values();

        static SourceMode byOrdinal(int i) {
            return values[i];
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamController$ActionOrigin.class */
    public enum ActionOrigin {
        None,
        A,
        B,
        X,
        Y,
        LeftBumper,
        RightBumper,
        LeftGrip,
        RightGrip,
        Start,
        Back,
        LeftPad_Touch,
        LeftPad_Swipe,
        LeftPad_Click,
        LeftPad_DPadNorth,
        LeftPad_DPadSouth,
        LeftPad_DPadWest,
        LeftPad_DPadEast,
        RightPad_Touch,
        RightPad_Swipe,
        RightPad_Click,
        RightPad_DPadNorth,
        RightPad_DPadSouth,
        RightPad_DPadWest,
        RightPad_DPadEast,
        LeftTrigger_Pull,
        LeftTrigger_Click,
        RightTrigger_Pull,
        RightTrigger_Click,
        LeftStick_Move,
        LeftStick_Click,
        LeftStick_DPadNorth,
        LeftStick_DPadSouth,
        LeftStick_DPadWest,
        LeftStick_DPadEast,
        Gyro_Move,
        Gyro_Pitch,
        Gyro_Yaw,
        Gyro_Roll,
        PS4_X,
        PS4_Circle,
        PS4_Triangle,
        PS4_Square,
        PS4_LeftBumper,
        PS4_RightBumper,
        PS4_Options,
        PS4_Share,
        PS4_LeftPad_Touch,
        PS4_LeftPad_Swipe,
        PS4_LeftPad_Click,
        PS4_LeftPad_DPadNorth,
        PS4_LeftPad_DPadSouth,
        PS4_LeftPad_DPadWest,
        PS4_LeftPad_DPadEast,
        PS4_RightPad_Touch,
        PS4_RightPad_Swipe,
        PS4_RightPad_Click,
        PS4_RightPad_DPadNorth,
        PS4_RightPad_DPadSouth,
        PS4_RightPad_DPadWest,
        PS4_RightPad_DPadEast,
        PS4_CenterPad_Touch,
        PS4_CenterPad_Swipe,
        PS4_CenterPad_Click,
        PS4_CenterPad_DPadNorth,
        PS4_CenterPad_DPadSouth,
        PS4_CenterPad_DPadWest,
        PS4_CenterPad_DPadEast,
        PS4_LeftTrigger_Pull,
        PS4_LeftTrigger_Click,
        PS4_RightTrigger_Pull,
        PS4_RightTrigger_Click,
        PS4_LeftStick_Move,
        PS4_LeftStick_Click,
        PS4_LeftStick_DPadNorth,
        PS4_LeftStick_DPadSouth,
        PS4_LeftStick_DPadWest,
        PS4_LeftStick_DPadEast,
        PS4_RightStick_Move,
        PS4_RightStick_Click,
        PS4_RightStick_DPadNorth,
        PS4_RightStick_DPadSouth,
        PS4_RightStick_DPadWest,
        PS4_RightStick_DPadEast,
        PS4_DPad_North,
        PS4_DPad_South,
        PS4_DPad_West,
        PS4_DPad_East,
        PS4_Gyro_Move,
        PS4_Gyro_Pitch,
        PS4_Gyro_Yaw,
        PS4_Gyro_Roll,
        XBoxOne_A,
        XBoxOne_B,
        XBoxOne_X,
        XBoxOne_Y,
        XBoxOne_LeftBumper,
        XBoxOne_RightBumper,
        XBoxOne_Menu,
        XBoxOne_View,
        XBoxOne_LeftTrigger_Pull,
        XBoxOne_LeftTrigger_Click,
        XBoxOne_RightTrigger_Pull,
        XBoxOne_RightTrigger_Click,
        XBoxOne_LeftStick_Move,
        XBoxOne_LeftStick_Click,
        XBoxOne_LeftStick_DPadNorth,
        XBoxOne_LeftStick_DPadSouth,
        XBoxOne_LeftStick_DPadWest,
        XBoxOne_LeftStick_DPadEast,
        XBoxOne_RightStick_Move,
        XBoxOne_RightStick_Click,
        XBoxOne_RightStick_DPadNorth,
        XBoxOne_RightStick_DPadSouth,
        XBoxOne_RightStick_DPadWest,
        XBoxOne_RightStick_DPadEast,
        XBoxOne_DPad_North,
        XBoxOne_DPad_South,
        XBoxOne_DPad_West,
        XBoxOne_DPad_East,
        XBox360_A,
        XBox360_B,
        XBox360_X,
        XBox360_Y,
        XBox360_LeftBumper,
        XBox360_RightBumper,
        XBox360_Start,
        XBox360_Back,
        XBox360_LeftTrigger_Pull,
        XBox360_LeftTrigger_Click,
        XBox360_RightTrigger_Pull,
        XBox360_RightTrigger_Click,
        XBox360_LeftStick_Move,
        XBox360_LeftStick_Click,
        XBox360_LeftStick_DPadNorth,
        XBox360_LeftStick_DPadSouth,
        XBox360_LeftStick_DPadWest,
        XBox360_LeftStick_DPadEast,
        XBox360_RightStick_Move,
        XBox360_RightStick_Click,
        XBox360_RightStick_DPadNorth,
        XBox360_RightStick_DPadSouth,
        XBox360_RightStick_DPadWest,
        XBox360_RightStick_DPadEast,
        XBox360_DPad_North,
        XBox360_DPad_South,
        XBox360_DPad_West,
        XBox360_DPad_East,
        SteamV2_A,
        SteamV2_B,
        SteamV2_X,
        SteamV2_Y,
        SteamV2_LeftBumper,
        SteamV2_RightBumper,
        SteamV2_LeftGrip,
        SteamV2_RightGrip,
        SteamV2_Start,
        SteamV2_Back,
        SteamV2_LeftPad_Touch,
        SteamV2_LeftPad_Swipe,
        SteamV2_LeftPad_Click,
        SteamV2_LeftPad_DPadNorth,
        SteamV2_LeftPad_DPadSouth,
        SteamV2_LeftPad_DPadWest,
        SteamV2_LeftPad_DPadEast,
        SteamV2_RightPad_Touch,
        SteamV2_RightPad_Swipe,
        SteamV2_RightPad_Click,
        SteamV2_RightPad_DPadNorth,
        SteamV2_RightPad_DPadSouth,
        SteamV2_RightPad_DPadWest,
        SteamV2_RightPad_DPadEast,
        SteamV2_LeftTrigger_Pull,
        SteamV2_LeftTrigger_Click,
        SteamV2_RightTrigger_Pull,
        SteamV2_RightTrigger_Click,
        SteamV2_LeftStick_Move,
        SteamV2_LeftStick_Click,
        SteamV2_LeftStick_DPadNorth,
        SteamV2_LeftStick_DPadSouth,
        SteamV2_LeftStick_DPadWest,
        SteamV2_LeftStick_DPadEast,
        SteamV2_Gyro_Move,
        SteamV2_Gyro_Pitch,
        SteamV2_Gyro_Yaw,
        SteamV2_Gyro_Roll;

        private static final ActionOrigin[] values = values();

        static ActionOrigin byOrdinal(int i) {
            return values[i];
        }
    }

    public SteamController() {
        super(SteamAPI.getSteamControllerPointer());
        this.controllerHandles = new long[16];
        this.actionOrigins = new int[8];
    }

    public boolean init() {
        return init(this.pointer);
    }

    public boolean shutdown() {
        return shutdown(this.pointer);
    }

    public void runFrame() {
        runFrame(this.pointer);
    }

    public int getConnectedControllers(SteamControllerHandle[] steamControllerHandleArr) {
        if (steamControllerHandleArr.length < 16) {
            throw new IllegalArgumentException("Array size must be at least STEAM_CONTROLLER_MAX_COUNT");
        }
        int connectedControllers = getConnectedControllers(this.pointer, this.controllerHandles);
        for (int i = 0; i < connectedControllers; i++) {
            steamControllerHandleArr[i] = new SteamControllerHandle(this.controllerHandles[i]);
        }
        return connectedControllers;
    }

    public boolean showBindingPanel(SteamControllerHandle steamControllerHandle) {
        return showBindingPanel(this.pointer, steamControllerHandle.handle);
    }

    public SteamControllerActionSetHandle getActionSetHandle(String str) {
        return new SteamControllerActionSetHandle(getActionSetHandle(this.pointer, str));
    }

    public void activateActionSet(SteamControllerHandle steamControllerHandle, SteamControllerActionSetHandle steamControllerActionSetHandle) {
        activateActionSet(this.pointer, steamControllerHandle.handle, steamControllerActionSetHandle.handle);
    }

    public SteamControllerActionSetHandle getCurrentActionSet(SteamControllerHandle steamControllerHandle) {
        return new SteamControllerActionSetHandle(getCurrentActionSet(this.pointer, steamControllerHandle.handle));
    }

    public SteamControllerDigitalActionHandle getDigitalActionHandle(String str) {
        return new SteamControllerDigitalActionHandle(getDigitalActionHandle(this.pointer, str));
    }

    public void getDigitalActionData(SteamControllerHandle steamControllerHandle, SteamControllerDigitalActionHandle steamControllerDigitalActionHandle, SteamControllerDigitalActionData steamControllerDigitalActionData) {
        getDigitalActionData(this.pointer, steamControllerHandle.handle, steamControllerDigitalActionHandle.handle, steamControllerDigitalActionData);
    }

    public int getDigitalActionOrigins(SteamControllerHandle steamControllerHandle, SteamControllerActionSetHandle steamControllerActionSetHandle, SteamControllerDigitalActionHandle steamControllerDigitalActionHandle, ActionOrigin[] actionOriginArr) {
        if (actionOriginArr.length < 8) {
            throw new IllegalArgumentException("Array size must be at least STEAM_CONTROLLER_MAX_ORIGINS");
        }
        int digitalActionOrigins = getDigitalActionOrigins(this.pointer, steamControllerHandle.handle, steamControllerActionSetHandle.handle, steamControllerDigitalActionHandle.handle, this.actionOrigins);
        for (int i = 0; i < digitalActionOrigins; i++) {
            actionOriginArr[i] = ActionOrigin.byOrdinal(this.actionOrigins[i]);
        }
        return digitalActionOrigins;
    }

    public SteamControllerAnalogActionHandle getAnalogActionHandle(String str) {
        return new SteamControllerAnalogActionHandle(getAnalogActionHandle(this.pointer, str));
    }

    public void getAnalogActionData(SteamControllerHandle steamControllerHandle, SteamControllerAnalogActionHandle steamControllerAnalogActionHandle, SteamControllerAnalogActionData steamControllerAnalogActionData) {
        getAnalogActionData(this.pointer, steamControllerHandle.handle, steamControllerAnalogActionHandle.handle, steamControllerAnalogActionData);
    }

    public int getAnalogActionOrigins(SteamControllerHandle steamControllerHandle, SteamControllerActionSetHandle steamControllerActionSetHandle, SteamControllerAnalogActionHandle steamControllerAnalogActionHandle, ActionOrigin[] actionOriginArr) {
        if (actionOriginArr.length < 8) {
            throw new IllegalArgumentException("Array size must be at least STEAM_CONTROLLER_MAX_ORIGINS");
        }
        int analogActionOrigins = getAnalogActionOrigins(this.pointer, steamControllerHandle.handle, steamControllerActionSetHandle.handle, steamControllerAnalogActionHandle.handle, this.actionOrigins);
        for (int i = 0; i < analogActionOrigins; i++) {
            actionOriginArr[i] = ActionOrigin.byOrdinal(this.actionOrigins[i]);
        }
        return analogActionOrigins;
    }

    public void stopAnalogActionMomentum(SteamControllerHandle steamControllerHandle, SteamControllerAnalogActionHandle steamControllerAnalogActionHandle) {
        stopAnalogActionMomentum(this.pointer, steamControllerHandle.handle, steamControllerAnalogActionHandle.handle);
    }

    public void triggerHapticPulse(SteamControllerHandle steamControllerHandle, Pad pad, int i) {
        triggerHapticPulse(this.pointer, steamControllerHandle.handle, pad.ordinal(), i);
    }

    public void triggerRepeatedHapticPulse(SteamControllerHandle steamControllerHandle, Pad pad, int i, int i2, int i3, int i4) {
        triggerRepeatedHapticPulse(this.pointer, steamControllerHandle.handle, pad.ordinal(), i, i2, i3, i4);
    }

    public void triggerVibration(SteamControllerHandle steamControllerHandle, short s, short s2) {
        triggerVibration(this.pointer, steamControllerHandle.handle, s, s2);
    }

    public void setLEDColor(SteamControllerHandle steamControllerHandle, int i, int i2, int i3, LEDFlag lEDFlag) {
        setLEDColor(this.pointer, steamControllerHandle.handle, (byte) (i & 255), (byte) (i2 & 255), (byte) (i3 & 255), lEDFlag.ordinal());
    }

    public int getGamepadIndexForController(SteamControllerHandle steamControllerHandle) {
        return getGamepadIndexForController(this.pointer, steamControllerHandle.handle);
    }

    public SteamControllerHandle getControllerForGamepadIndex(int i) {
        return new SteamControllerHandle(getControllerForGamepadIndex(this.pointer, i));
    }

    public void getMotionData(SteamControllerHandle steamControllerHandle, SteamControllerMotionData steamControllerMotionData) {
        getMotionData(this.pointer, steamControllerHandle.handle, steamControllerMotionData.data);
    }

    public boolean showDigitalActionOrigins(SteamControllerHandle steamControllerHandle, SteamControllerDigitalActionHandle steamControllerDigitalActionHandle, float f, float f2, float f3) {
        return showDigitalActionOrigins(this.pointer, steamControllerHandle.handle, steamControllerDigitalActionHandle.handle, f, f2, f3);
    }

    public boolean showAnalogActionOrigins(SteamControllerHandle steamControllerHandle, SteamControllerAnalogActionHandle steamControllerAnalogActionHandle, float f, float f2, float f3) {
        return showAnalogActionOrigins(this.pointer, steamControllerHandle.handle, steamControllerAnalogActionHandle.handle, f, f2, f3);
    }

    public String getStringForActionOrigin(ActionOrigin actionOrigin) {
        return getStringForActionOrigin(this.pointer, actionOrigin.ordinal());
    }

    public String getGlyphForActionOrigin(ActionOrigin actionOrigin) {
        return getGlyphForActionOrigin(this.pointer, actionOrigin.ordinal());
    }
}
