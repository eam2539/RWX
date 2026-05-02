package com.codedisaster.steamworks;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking.class */
public class SteamMatchmaking extends SteamInterface {

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking$LobbyDistanceFilter.class */
    public enum LobbyDistanceFilter {
        Close,
        Default,
        Far,
        Worldwide
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking$LobbyType.class */
    public enum LobbyType {
        Private,
        FriendsOnly,
        Public,
        Invisible
    }

    private static native long createCallback(SteamMatchmakingCallbackAdapter steamMatchmakingCallbackAdapter);

    private static native long requestLobbyList(long j, long j2);

    private static native void addRequestLobbyListStringFilter(long j, String str, String str2, int i);

    private static native void addRequestLobbyListNumericalFilter(long j, String str, int i, int i2);

    private static native void addRequestLobbyListNearValueFilter(long j, String str, int i);

    private static native void addRequestLobbyListFilterSlotsAvailable(long j, int i);

    private static native void addRequestLobbyListDistanceFilter(long j, int i);

    private static native void addRequestLobbyListResultCountFilter(long j, int i);

    private static native void addRequestLobbyListCompatibleMembersFilter(long j, long j2);

    private static native long getLobbyByIndex(long j, int i);

    private static native long createLobby(long j, long j2, int i, int i2);

    private static native long joinLobby(long j, long j2, long j3);

    private static native void leaveLobby(long j, long j2);

    private static native boolean inviteUserToLobby(long j, long j2, long j3);

    private static native int getNumLobbyMembers(long j, long j2);

    private static native long getLobbyMemberByIndex(long j, long j2, int i);

    private static native String getLobbyData(long j, long j2, String str);

    private static native boolean setLobbyData(long j, long j2, String str, String str2);

    private static native int getLobbyDataCount(long j, long j2);

    private static native boolean getLobbyDataByIndex(long j, long j2, int i, SteamMatchMakingKeyValuePair steamMatchMakingKeyValuePair);

    private static native boolean deleteLobbyData(long j, long j2, String str);

    private static native boolean sendLobbyChatMsg(long j, long j2, ByteBuffer byteBuffer, int i, int i2);

    private static native boolean sendLobbyChatMsg(long j, long j2, String str);

    private static native int getLobbyChatEntry(long j, long j2, int i, ChatEntry chatEntry, ByteBuffer byteBuffer, int i2, int i3);

    private static native boolean requestLobbyData(long j, long j2);

    private static native boolean setLobbyMemberLimit(long j, long j2, int i);

    private static native boolean getLobbyMemberLimit(long j, long j2);

    private static native boolean setLobbyType(long j, long j2, int i);

    private static native boolean setLobbyJoinable(long j, long j2, boolean z);

    private static native long getLobbyOwner(long j, long j2);

    private static native boolean setLobbyOwner(long j, long j2, long j3);

    private static native boolean setLinkedLobby(long j, long j2, long j3);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking$LobbyComparison.class */
    public enum LobbyComparison {
        EqualToOrLessThan(-2),
        LessThan(-1),
        Equal(0),
        GreaterThan(1),
        EqualToOrGreaterThan(2),
        NotEqual(3);

        private final int value;

        LobbyComparison(int i) {
            this.value = i;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking$ChatRoomEnterResponse.class */
    public enum ChatRoomEnterResponse {
        Success(1),
        DoesntExist(2),
        NotAllowed(3),
        Full(4),
        Error(5),
        Banned(6),
        Limited(7),
        ClanDisabled(8),
        CommunityBan(9),
        MemberBlockedYou(10),
        YouBlockedMember(11);

        private final int code;
        private static final ChatRoomEnterResponse[] values = values();

        ChatRoomEnterResponse(int i) {
            this.code = i;
        }

        static ChatRoomEnterResponse byCode(int i) {
            for (ChatRoomEnterResponse chatRoomEnterResponse : values) {
                if (chatRoomEnterResponse.code == i) {
                    return chatRoomEnterResponse;
                }
            }
            return Error;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking$ChatMemberStateChange.class */
    public enum ChatMemberStateChange {
        Entered(1),
        Left(2),
        Disconnected(4),
        Kicked(8),
        Banned(16);

        private final int bits;

        ChatMemberStateChange(int i) {
            this.bits = i;
        }

        static boolean isSet(ChatMemberStateChange chatMemberStateChange, int i) {
            return (chatMemberStateChange.bits & i) == chatMemberStateChange.bits;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking$ChatEntryType.class */
    public enum ChatEntryType {
        Invalid(0),
        ChatMsg(1),
        Typing(2),
        InviteGame(3),
        Emote(4),
        LeftConversation(6),
        Entered(7),
        WasKicked(8),
        WasBanned(9),
        Disconnected(10),
        HistoricalChat(11),
        Reserved1(12),
        Reserved2(13),
        LinkBlocked(14);

        private final int code;
        private static final ChatEntryType[] values = values();

        ChatEntryType(int i) {
            this.code = i;
        }

        static ChatEntryType byCode(int i) {
            for (ChatEntryType chatEntryType : values) {
                if (chatEntryType.code == i) {
                    return chatEntryType;
                }
            }
            return Invalid;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmaking$ChatEntry.class */
    public class ChatEntry {
        private long steamIDUser;
        private int chatEntryType;

        public SteamID getSteamIDUser() {
            return new SteamID(this.steamIDUser);
        }

        public ChatEntryType getChatEntryType() {
            return ChatEntryType.byCode(this.chatEntryType);
        }
    }

    public SteamMatchmaking(SteamMatchmakingCallback steamMatchmakingCallback) {
        super(SteamAPI.getSteamMatchmakingPointer(), createCallback(new SteamMatchmakingCallbackAdapter(steamMatchmakingCallback)));
    }

    public SteamAPICall requestLobbyList() {
        return new SteamAPICall(requestLobbyList(this.pointer, this.callback));
    }

    public void addRequestLobbyListStringFilter(String str, String str2, LobbyComparison lobbyComparison) {
        addRequestLobbyListStringFilter(this.pointer, str, str2, lobbyComparison.value);
    }

    public void addRequestLobbyListNumericalFilter(String str, int i, LobbyComparison lobbyComparison) {
        addRequestLobbyListNumericalFilter(this.pointer, str, i, lobbyComparison.value);
    }

    public void addRequestLobbyListNearValueFilter(String str, int i) {
        addRequestLobbyListNearValueFilter(this.pointer, str, i);
    }

    public void addRequestLobbyListFilterSlotsAvailable(int i) {
        addRequestLobbyListFilterSlotsAvailable(this.pointer, i);
    }

    public void addRequestLobbyListDistanceFilter(LobbyDistanceFilter lobbyDistanceFilter) {
        addRequestLobbyListDistanceFilter(this.pointer, lobbyDistanceFilter.ordinal());
    }

    public void addRequestLobbyListResultCountFilter(int i) {
        addRequestLobbyListResultCountFilter(this.pointer, i);
    }

    public void addRequestLobbyListCompatibleMembersFilter(SteamID steamID) {
        addRequestLobbyListCompatibleMembersFilter(this.pointer, steamID.handle);
    }

    public SteamID getLobbyByIndex(int i) {
        return new SteamID(getLobbyByIndex(this.pointer, i));
    }

    public SteamAPICall createLobby(LobbyType lobbyType, int i) {
        return new SteamAPICall(createLobby(this.pointer, this.callback, lobbyType.ordinal(), i));
    }

    public SteamAPICall joinLobby(SteamID steamID) {
        return new SteamAPICall(joinLobby(this.pointer, this.callback, steamID.handle));
    }

    public void leaveLobby(SteamID steamID) {
        leaveLobby(this.pointer, steamID.handle);
    }

    public boolean inviteUserToLobby(SteamID steamID, SteamID steamID2) {
        return inviteUserToLobby(this.pointer, steamID.handle, steamID2.handle);
    }

    public int getNumLobbyMembers(SteamID steamID) {
        return getNumLobbyMembers(this.pointer, steamID.handle);
    }

    public SteamID getLobbyMemberByIndex(SteamID steamID, int i) {
        return new SteamID(getLobbyMemberByIndex(this.pointer, steamID.handle, i));
    }

    public String getLobbyData(SteamID steamID, String str) {
        return getLobbyData(this.pointer, steamID.handle, str);
    }

    public boolean setLobbyData(SteamID steamID, String str, String str2) {
        return setLobbyData(this.pointer, steamID.handle, str, str2);
    }

    public boolean setLobbyData(SteamID steamID, SteamMatchMakingKeyValuePair steamMatchMakingKeyValuePair) {
        return setLobbyData(this.pointer, steamID.handle, steamMatchMakingKeyValuePair.getKey(), steamMatchMakingKeyValuePair.getValue());
    }

    public int getLobbyDataCount(SteamID steamID) {
        return getLobbyDataCount(this.pointer, steamID.handle);
    }

    public boolean getLobbyDataByIndex(SteamID steamID, int i, SteamMatchMakingKeyValuePair steamMatchMakingKeyValuePair) {
        return getLobbyDataByIndex(this.pointer, steamID.handle, i, steamMatchMakingKeyValuePair);
    }

    public boolean deleteLobbyData(SteamID steamID, String str) {
        return deleteLobbyData(this.pointer, steamID.handle, str);
    }

    public boolean sendLobbyChatMsg(SteamID steamID, ByteBuffer byteBuffer) throws SteamException {
        int iPosition = byteBuffer.position();
        return sendLobbyChatMsg(steamID, byteBuffer, iPosition, byteBuffer.limit() - iPosition);
    }

    public boolean sendLobbyChatMsg(SteamID steamID, ByteBuffer byteBuffer, int i, int i2) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        if (byteBuffer.capacity() < i + i2) {
            throw new SteamException("Buffer capacity exceeded!");
        }
        return sendLobbyChatMsg(this.pointer, steamID.handle, byteBuffer, i, i2);
    }

    public boolean sendLobbyChatMsg(SteamID steamID, String str) {
        return sendLobbyChatMsg(this.pointer, steamID.handle, str);
    }

    public int getLobbyChatEntry(SteamID steamID, int i, ChatEntry chatEntry, ByteBuffer byteBuffer) throws SteamException {
        int iPosition = byteBuffer.position();
        return getLobbyChatEntry(steamID, i, chatEntry, byteBuffer, iPosition, byteBuffer.limit() - iPosition);
    }

    public int getLobbyChatEntry(SteamID steamID, int i, ChatEntry chatEntry, ByteBuffer byteBuffer, int i2, int i3) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        if (byteBuffer.capacity() < i2 + i3) {
            throw new SteamException("Buffer capacity exceeded!");
        }
        int lobbyChatEntry = getLobbyChatEntry(this.pointer, steamID.handle, i, chatEntry, byteBuffer, i2, i3);
        if (lobbyChatEntry >= 0) {
            byteBuffer.position(i2);
            byteBuffer.limit(i2 + lobbyChatEntry);
        }
        return lobbyChatEntry;
    }

    public boolean requestLobbyData(SteamID steamID) {
        return requestLobbyData(this.pointer, steamID.handle);
    }

    public boolean setLobbyMemberLimit(SteamID steamID, int i) {
        return setLobbyMemberLimit(this.pointer, steamID.handle, i);
    }

    public boolean getLobbyMemberLimit(SteamID steamID) {
        return getLobbyMemberLimit(this.pointer, steamID.handle);
    }

    public boolean setLobbyType(SteamID steamID, LobbyType lobbyType) {
        return setLobbyType(this.pointer, steamID.handle, lobbyType.ordinal());
    }

    public boolean setLobbyJoinable(SteamID steamID, boolean z) {
        return setLobbyJoinable(this.pointer, steamID.handle, z);
    }

    public SteamID getLobbyOwner(SteamID steamID) {
        return new SteamID(getLobbyOwner(this.pointer, steamID.handle));
    }

    public boolean setLobbyOwner(SteamID steamID, SteamID steamID2) {
        return setLobbyOwner(this.pointer, steamID.handle, steamID2.handle);
    }

    public boolean setLinkedLobby(SteamID steamID, SteamID steamID2) {
        return setLinkedLobby(this.pointer, steamID.handle, steamID2.handle);
    }
}
