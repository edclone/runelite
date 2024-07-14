package net.runelite.client.game.chatbox;

import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;

public class ClientHelper {
    private final Client client;

    public ClientHelper(Client client) {
        this.client = client;
    }

    public boolean shouldTakeInput() {
        Widget worldMapSearch = client.getWidget(ComponentID.WORLD_MAP_SEARCH);
        return worldMapSearch == null || client.getVarcIntValue(VarClientInt.WORLD_MAP_SEARCH_FOCUSED) != 1;
    }

}
