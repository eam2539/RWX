package com.codedisaster.steamworks;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUtils.class */
public class SteamUtils extends SteamInterface {
    private SteamUtilsCallbackAdapter callbackAdapter;

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUtils$NotificationPosition.class */
    public enum NotificationPosition {
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight
    }

    private static native long createCallback(SteamUtilsCallbackAdapter steamUtilsCallbackAdapter);

    private static native int getSecondsSinceAppActive(long j);

    private static native int getSecondsSinceComputerActive(long j);

    private static native int getConnectedUniverse(long j);

    private static native int getServerRealTime(long j);

    private static native String getIPCountry(long j);

    private static native int getImageWidth(long j, int i);

    private static native int getImageHeight(long j, int i);

    private static native boolean getImageRGBA(long j, int i, ByteBuffer byteBuffer, int i2);

    private static native int getAppID(long j);

    private static native void setOverlayNotificationPosition(long j, int i);

    private static native boolean isAPICallCompleted(long j, long j2, boolean[] zArr);

    private static native int getAPICallFailureReason(long j, long j2);

    private static native void enableWarningMessageHook(long j, boolean z);

    private static native boolean isOverlayEnabled(long j);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUtils$SteamAPICallFailure.class */
    public enum SteamAPICallFailure {
        None(-1),
        SteamGone(0),
        NetworkFailure(1),
        InvalidHandle(2),
        MismatchedCallback(3);

        private final int code;
        private static final SteamAPICallFailure[] values = values();

        SteamAPICallFailure(int i) {
            this.code = i;
        }

        static SteamAPICallFailure byValue(int i) {
            for (SteamAPICallFailure steamAPICallFailure : values) {
                if (steamAPICallFailure.code == i) {
                    return steamAPICallFailure;
                }
            }
            return None;
        }
    }

    public SteamUtils(SteamUtilsCallback steamUtilsCallback) {
        super(SteamAPI.getSteamUtilsPointer());
        this.callbackAdapter = new SteamUtilsCallbackAdapter(steamUtilsCallback);
        setCallback(createCallback(this.callbackAdapter));
    }

    public int getSecondsSinceAppActive() {
        return getSecondsSinceAppActive(this.pointer);
    }

    public int getSecondsSinceComputerActive() {
        return getSecondsSinceComputerActive(this.pointer);
    }

    public SteamUniverse getConnectedUniverse() {
        return SteamUniverse.byValue(getConnectedUniverse(this.pointer));
    }

    public int getServerRealTime() {
        return getServerRealTime(this.pointer);
    }

    public int getImageWidth(int i) {
        return getImageWidth(this.pointer, i);
    }

    public int getImageHeight(int i) {
        return getImageHeight(this.pointer, i);
    }

    public boolean getImageRGBA(int i, ByteBuffer byteBuffer, int i2) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        return getImageRGBA(this.pointer, i, byteBuffer, i2);
    }

    public int getAppID() {
        return getAppID(this.pointer);
    }

    public void setOverlayNotificationPosition(NotificationPosition notificationPosition) {
        setOverlayNotificationPosition(this.pointer, notificationPosition.ordinal());
    }

    public boolean isAPICallCompleted(SteamAPICall steamAPICall, boolean[] zArr) {
        return isAPICallCompleted(this.pointer, steamAPICall.handle, zArr);
    }

    public SteamAPICallFailure getAPICallFailureReason(SteamAPICall steamAPICall) {
        return SteamAPICallFailure.byValue(getAPICallFailureReason(this.pointer, steamAPICall.handle));
    }

    public void setWarningMessageHook(SteamAPIWarningMessageHook steamAPIWarningMessageHook) {
        this.callbackAdapter.setWarningMessageHook(steamAPIWarningMessageHook);
        enableWarningMessageHook(this.callback, steamAPIWarningMessageHook != null);
    }

    public boolean isOverlayEnabled() {
        return isOverlayEnabled(this.pointer);
    }
}
