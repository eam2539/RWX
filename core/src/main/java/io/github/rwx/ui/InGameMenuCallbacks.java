package io.github.rwx.ui;

public interface InGameMenuCallbacks {
    InGameMenuCallbacks CORE_UI_EVENT_QUEUE = new InGameMenuCallbacks() {
        @Override
        public void requestSettings() {
            CoreUiEventQueue.requestInGameSettings();
        }

        @Override
        public void requestSave() {
            CoreUiEventQueue.requestInGameSave();
        }

        @Override
        public void requestExportMap() {
            CoreUiEventQueue.requestInGameExportMap();
        }

        @Override
        public void requestMapList() {
            CoreUiEventQueue.requestInGameMapList();
        }

        @Override
        public void requestChat(boolean teamOnly) {
            CoreUiEventQueue.requestInGameChat(teamOnly);
        }

        @Override
        public void requestPlayerList() {
            CoreUiEventQueue.requestInGamePlayerList();
        }

        @Override
        public void requestSurrender() {
            CoreUiEventQueue.requestInGameSurrender();
        }

        @Override
        public void requestExit() {
            CoreUiEventQueue.requestInGameExit();
        }

        @Override
        public void requestBattleRoomRefresh() {
            CoreUiEventQueue.requestBattleRoomRefresh();
        }

        @Override
        public void requestReturnToBattleRoom() {
            CoreUiEventQueue.requestInGameReturnToBattleRoom();
        }
    };

    void requestSettings();

    void requestSave();

    void requestExportMap();

    void requestMapList();

    default boolean shouldShowMapList() {
        return false;
    }

    void requestChat(boolean teamOnly);

    void requestPlayerList();

    void requestSurrender();

    void requestExit();

    void requestBattleRoomRefresh();

    void requestReturnToBattleRoom();
}
