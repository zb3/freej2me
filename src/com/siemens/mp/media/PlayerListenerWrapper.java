package com.siemens.mp.media;

import java.util.HashMap;
import java.util.Map;

public class PlayerListenerWrapper implements javax.microedition.media.PlayerListener {

    private final PlayerListener delegate;
    private final Player player;

    private static final Map<PlayerListener, PlayerListenerWrapper> wrapperMap = new HashMap<>();

    private PlayerListenerWrapper(Player player, PlayerListener delegate) {
        this.player = player;
        this.delegate = delegate;
        wrapperMap.put(delegate, this);
    }

    public static PlayerListenerWrapper createWrapper(Player player, PlayerListener delegate) {
        if (wrapperMap.containsKey(delegate)) {
            return wrapperMap.get(delegate);
        }

        return new PlayerListenerWrapper(player, delegate);
    }

    public static PlayerListenerWrapper getWrapperForListener(PlayerListener listener) {
        return wrapperMap.get(listener);
    }

    public void removeWrapperAssociation() {
        wrapperMap.remove(delegate);
    }

    @Override
    public void playerUpdate(javax.microedition.media.Player player, String event, Object eventData) {
        delegate.playerUpdate(this.player, event, eventData);
    }
}