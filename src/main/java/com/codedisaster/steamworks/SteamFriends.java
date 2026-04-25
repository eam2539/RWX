package com.codedisaster.steamworks;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.util.Collection;
import java.util.Iterator;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends.class */
public class SteamFriends extends SteamInterface {

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends$OverlayToStoreFlag.class */
    public enum OverlayToStoreFlag {
        None,
        AddToCart,
        AddToCartAndShow
    }

    private static native long createCallback(SteamFriendsCallbackAdapter steamFriendsCallbackAdapter);

    private static native String getPersonaName(long j);

    private static native long setPersonaName(long j, long j2, String str);

    private static native int getPersonaState(long j);

    private static native int getFriendCount(long j, int i);

    private static native long getFriendByIndex(long j, int i, int i2);

    private static native int getFriendRelationship(long j, long j2);

    private static native int getFriendPersonaState(long j, long j2);

    private static native String getFriendPersonaName(long j, long j2);

    private static native int getSmallFriendAvatar(long j, long j2);

    private static native int getMediumFriendAvatar(long j, long j2);

    private static native int getLargeFriendAvatar(long j, long j2);

    private static native boolean requestUserInformation(long j, long j2, boolean z);

    private static native void activateGameOverlay(long j, String str);

    private static native void activateGameOverlayToUser(long j, String str, long j2);

    private static native void activateGameOverlayToWebPage(long j, String str);

    private static native void activateGameOverlayToStore(long j, int i, int i2);

    private static native void activateGameOverlayInviteDialog(long j, long j2);

    private static native boolean setRichPresence(long j, String str, String str2);

    private static native void clearRichPresence(long j);

    private static native String getFriendRichPresence(long j, long j2, String str);

    private static native int getFriendRichPresenceKeyCount(long j, long j2);

    private static native String getFriendRichPresenceKeyByIndex(long j, long j2, int i);

    private static native void requestFriendRichPresence(long j, long j2);

    private static native boolean inviteUserToGame(long j, long j2, String str);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends$FriendRelationship.class */
    public enum FriendRelationship {
        None,
        Blocked,
        Recipient,
        Friend,
        RequestInitiator,
        Ignored,
        IgnoredFriend,
        Suggested_DEPRECATED,
        Max;

        private static final FriendRelationship[] values = values();

        static FriendRelationship byOrdinal(int i) {
            return values[i];
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends$PersonaState.class */
    public enum PersonaState {
        Offline,
        Online,
        Busy,
        Away,
        Snooze,
        LookingToTrade,
        LookingToPlay;

        private static final PersonaState[] values = values();

        static PersonaState byOrdinal(int i) {
            return values[i];
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends$FriendFlags.class */
    public enum FriendFlags {
        None(0),
        Blocked(1),
        FriendshipRequested(2),
        Immediate(4),
        ClanMember(8),
        OnGameServer(16),
        RequestingFriendship(128),
        RequestingInfo(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_CONTENTS_MENU),
        Ignored(512),
        IgnoredFriend(1024),
        ChatMember(4096),
        All(65535);

        private final int bits;

        FriendFlags(int i) {
            this.bits = i;
        }

        static int asBits(Collection collection) {
            int i = 0;
            Iterator it = collection.iterator();
            while (it.hasNext()) {
                i |= ((FriendFlags) it.next()).bits;
            }
            return i;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends$PersonaChange.class */
    public enum PersonaChange {
        Name(1),
        Status(2),
        ComeOnline(4),
        GoneOffline(8),
        GamePlayed(16),
        GameServer(32),
        Avatar(64),
        JoinedSource(128),
        LeftSource(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_CONTENTS_MENU),
        RelationshipChanged(512),
        NameFirstSet(1024),
        FacebookInfo(2048),
        Nickname(4096),
        SteamLevel(8192);

        private final int bits;

        PersonaChange(int i) {
            this.bits = i;
        }

        static boolean isSet(PersonaChange personaChange, int i) {
            return (personaChange.bits & i) == personaChange.bits;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends$OverlayDialog.class */
    public enum OverlayDialog {
        Friends("Friends"),
        Community("Community"),
        Players("Players"),
        Settings("Settings"),
        OfficialGameGroup("OfficialGameGroup"),
        Stats("Stats"),
        Achievements("Achievements");

        private final String id;

        OverlayDialog(String str) {
            this.id = str;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriends$OverlayToUserDialog.class */
    public enum OverlayToUserDialog {
        SteamID("steamid"),
        Chat("chat"),
        JoinTrade("jointrade"),
        Stats("stats"),
        Achievements("achievements"),
        FriendAdd("friendadd"),
        FriendRemove("friendremove"),
        FriendRequestAccept("friendrequestaccept"),
        FriendRequestIgnore("friendrequestignore");

        private final String id;

        OverlayToUserDialog(String str) {
            this.id = str;
        }
    }

    public SteamFriends(SteamFriendsCallback steamFriendsCallback) {
        super(SteamAPI.getSteamFriendsPointer(), createCallback(new SteamFriendsCallbackAdapter(steamFriendsCallback)));
    }

    public String getPersonaName() {
        return getPersonaName(this.pointer);
    }

    public SteamAPICall setPersonaName(String str) {
        return new SteamAPICall(setPersonaName(this.pointer, this.callback, str));
    }

    public PersonaState getPersonaState() {
        return PersonaState.byOrdinal(getPersonaState(this.pointer));
    }

    public int getFriendCount(FriendFlags friendFlags) {
        return getFriendCount(this.pointer, friendFlags.bits);
    }

    public int getFriendCount(Collection collection) {
        return getFriendCount(this.pointer, FriendFlags.asBits(collection));
    }

    public SteamID getFriendByIndex(int i, FriendFlags friendFlags) {
        return new SteamID(getFriendByIndex(this.pointer, i, friendFlags.bits));
    }

    public SteamID getFriendByIndex(int i, Collection collection) {
        return new SteamID(getFriendByIndex(this.pointer, i, FriendFlags.asBits(collection)));
    }

    public FriendRelationship getFriendRelationship(SteamID steamID) {
        return FriendRelationship.byOrdinal(getFriendRelationship(this.pointer, steamID.handle));
    }

    public PersonaState getFriendPersonaState(SteamID steamID) {
        return PersonaState.byOrdinal(getFriendPersonaState(this.pointer, steamID.handle));
    }

    public String getFriendPersonaName(SteamID steamID) {
        return getFriendPersonaName(this.pointer, steamID.handle);
    }

    public int getSmallFriendAvatar(SteamID steamID) {
        return getSmallFriendAvatar(this.pointer, steamID.handle);
    }

    public int getMediumFriendAvatar(SteamID steamID) {
        return getMediumFriendAvatar(this.pointer, steamID.handle);
    }

    public int getLargeFriendAvatar(SteamID steamID) {
        return getLargeFriendAvatar(this.pointer, steamID.handle);
    }

    public boolean requestUserInformation(SteamID steamID, boolean z) {
        return requestUserInformation(this.pointer, steamID.handle, z);
    }

    public void activateGameOverlay(OverlayDialog overlayDialog) {
        activateGameOverlay(this.pointer, overlayDialog.id);
    }

    public void activateGameOverlayToUser(OverlayToUserDialog overlayToUserDialog, SteamID steamID) {
        activateGameOverlayToUser(this.pointer, overlayToUserDialog.id, steamID.handle);
    }

    public void activateGameOverlayToWebPage(String str) {
        activateGameOverlayToWebPage(this.pointer, str);
    }

    public void activateGameOverlayToStore(int i, OverlayToStoreFlag overlayToStoreFlag) {
        activateGameOverlayToStore(this.pointer, i, overlayToStoreFlag.ordinal());
    }

    public void activateGameOverlayInviteDialog(SteamID steamID) {
        activateGameOverlayInviteDialog(this.pointer, steamID.handle);
    }

    public boolean setRichPresence(String str, String str2) {
        return setRichPresence(this.pointer, str, str2 != null ? str2 : VariableScope.nullOrMissingString);
    }

    public void clearRichPresence() {
        clearRichPresence(this.pointer);
    }

    public String getFriendRichPresence(SteamID steamID, String str) {
        return getFriendRichPresence(this.pointer, steamID.handle, str);
    }

    public int getFriendRichPresenceKeyCount(SteamID steamID) {
        return getFriendRichPresenceKeyCount(this.pointer, steamID.handle);
    }

    public String getFriendRichPresenceKeyByIndex(SteamID steamID, int i) {
        return getFriendRichPresenceKeyByIndex(this.pointer, steamID.handle, i);
    }

    public void requestFriendRichPresence(SteamID steamID) {
        requestFriendRichPresence(this.pointer, steamID.handle);
    }

    public boolean inviteUserToGame(SteamID steamID, String str) {
        return inviteUserToGame(this.pointer, steamID.handle, str);
    }
}
