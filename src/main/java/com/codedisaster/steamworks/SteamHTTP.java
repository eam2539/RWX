package com.codedisaster.steamworks;

import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamHTTP.class */
public class SteamHTTP extends SteamInterface {

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamHTTP$API.class */
    public enum API {
        Client,
        Server
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamHTTP$HTTPMethod.class */
    public enum HTTPMethod {
        Invalid,
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        OPTIONS
    }

    private static native long createCallback(SteamHTTPCallbackAdapter steamHTTPCallbackAdapter, boolean z);

    private static native long createHTTPRequest(long j, int i, String str);

    private static native boolean setHTTPRequestContextValue(long j, long j2, long j3);

    private static native boolean setHTTPRequestNetworkActivityTimeout(long j, long j2, int i);

    private static native boolean setHTTPRequestHeaderValue(long j, long j2, String str, String str2);

    private static native boolean setHTTPRequestGetOrPostParameter(long j, long j2, String str, String str2);

    private static native long sendHTTPRequest(long j, long j2, long j3);

    private static native long sendHTTPRequestAndStreamResponse(long j, long j2);

    private static native int getHTTPResponseHeaderSize(long j, long j2, String str);

    private static native boolean getHTTPResponseHeaderValue(long j, long j2, String str, ByteBuffer byteBuffer, int i, int i2);

    private static native int getHTTPResponseBodySize(long j, long j2);

    private static native boolean getHTTPResponseBodyData(long j, long j2, ByteBuffer byteBuffer, int i, int i2);

    private static native boolean getHTTPStreamingResponseBodyData(long j, long j2, int i, ByteBuffer byteBuffer, int i2, int i3);

    private static native boolean releaseHTTPRequest(long j, long j2);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamHTTP$HTTPStatusCode.class */
    public enum HTTPStatusCode {
        Invalid(0),
        Continue(100),
        SwitchingProtocols(101),
        OK(200),
        Created(201),
        Accepted(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_BUTTON_15),
        NonAuthoritative(203),
        NoContent(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_LANGUAGE_SWITCH),
        ResetContent(205),
        PartialContent(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_3D_MODE),
        MultipleChoices(300),
        MovedPermanently(301),
        Found(302),
        SeeOther(303),
        NotModified(304),
        UseProxy(305),
        TemporaryRedirect(307),
        BadRequest(400),
        Unauthorized(401),
        PaymentRequired(402),
        Forbidden(403),
        NotFound(404),
        MethodNotAllowed(405),
        NotAcceptable(406),
        ProxyAuthRequired(407),
        RequestTimeout(408),
        Conflict(409),
        Gone(410),
        LengthRequired(411),
        PreconditionFailed(412),
        RequestEntityTooLarge(413),
        RequestURITooLong(414),
        UnsupportedMediaType(415),
        RequestedRangeNotSatisfiable(416),
        ExpectationFailed(417),
        Unknown4xx(418),
        TooManyRequests(429),
        InternalServerError(500),
        NotImplemented(501),
        BadGateway(502),
        ServiceUnavailable(503),
        GatewayTimeout(504),
        HTTPVersionNotSupported(505),
        Unknown5xx(599);

        private final int code;
        private static final HTTPStatusCode[] values = values();

        HTTPStatusCode(int i) {
            this.code = i;
        }

        static HTTPStatusCode byValue(int i) {
            for (HTTPStatusCode hTTPStatusCode : values) {
                if (hTTPStatusCode.code == i) {
                    return hTTPStatusCode;
                }
            }
            return Invalid;
        }
    }

    public SteamHTTP(SteamHTTPCallback steamHTTPCallback, API api) {
        super(api == API.Client ? SteamAPI.getSteamHTTPPointer() : SteamGameServerAPI.getSteamGameServerHTTPPointer(), createCallback(new SteamHTTPCallbackAdapter(steamHTTPCallback), api == API.Client));
    }

    public SteamHTTPRequestHandle createHTTPRequest(HTTPMethod hTTPMethod, String str) {
        return new SteamHTTPRequestHandle(createHTTPRequest(this.pointer, hTTPMethod.ordinal(), str));
    }

    public boolean setHTTPRequestContextValue(SteamHTTPRequestHandle steamHTTPRequestHandle, long j) {
        return setHTTPRequestContextValue(this.pointer, steamHTTPRequestHandle.handle, j);
    }

    public boolean setHTTPRequestNetworkActivityTimeout(SteamHTTPRequestHandle steamHTTPRequestHandle, int i) {
        return setHTTPRequestNetworkActivityTimeout(this.pointer, steamHTTPRequestHandle.handle, i);
    }

    public boolean setHTTPRequestHeaderValue(SteamHTTPRequestHandle steamHTTPRequestHandle, String str, String str2) {
        return setHTTPRequestHeaderValue(this.pointer, steamHTTPRequestHandle.handle, str, str2);
    }

    public boolean setHTTPRequestGetOrPostParameter(SteamHTTPRequestHandle steamHTTPRequestHandle, String str, String str2) {
        return setHTTPRequestGetOrPostParameter(this.pointer, steamHTTPRequestHandle.handle, str, str2);
    }

    public SteamAPICall sendHTTPRequest(SteamHTTPRequestHandle steamHTTPRequestHandle) {
        return new SteamAPICall(sendHTTPRequest(this.pointer, this.callback, steamHTTPRequestHandle.handle));
    }

    public SteamAPICall sendHTTPRequestAndStreamResponse(SteamHTTPRequestHandle steamHTTPRequestHandle) {
        return new SteamAPICall(sendHTTPRequestAndStreamResponse(this.pointer, steamHTTPRequestHandle.handle));
    }

    public int getHTTPResponseHeaderSize(SteamHTTPRequestHandle steamHTTPRequestHandle, String str) {
        return getHTTPResponseHeaderSize(this.pointer, steamHTTPRequestHandle.handle, str);
    }

    public boolean getHTTPResponseHeaderValue(SteamHTTPRequestHandle steamHTTPRequestHandle, String str, ByteBuffer byteBuffer) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        int iPosition = byteBuffer.position();
        return getHTTPResponseHeaderValue(this.pointer, steamHTTPRequestHandle.handle, str, byteBuffer, iPosition, byteBuffer.limit() - iPosition);
    }

    public int getHTTPResponseBodySize(SteamHTTPRequestHandle steamHTTPRequestHandle) {
        return getHTTPResponseBodySize(this.pointer, steamHTTPRequestHandle.handle);
    }

    public boolean getHTTPResponseBodyData(SteamHTTPRequestHandle steamHTTPRequestHandle, ByteBuffer byteBuffer) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        int iPosition = byteBuffer.position();
        return getHTTPResponseBodyData(this.pointer, steamHTTPRequestHandle.handle, byteBuffer, iPosition, byteBuffer.limit() - iPosition);
    }

    public boolean getHTTPStreamingResponseBodyData(SteamHTTPRequestHandle steamHTTPRequestHandle, int i, ByteBuffer byteBuffer) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        int iPosition = byteBuffer.position();
        return getHTTPStreamingResponseBodyData(this.pointer, steamHTTPRequestHandle.handle, i, byteBuffer, iPosition, byteBuffer.limit() - iPosition);
    }

    public boolean releaseHTTPRequest(SteamHTTPRequestHandle steamHTTPRequestHandle) {
        return releaseHTTPRequest(this.pointer, steamHTTPRequestHandle.handle);
    }
}
