package net.runelite.client.game.chatbox;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseWheelListener;

@Slf4j
public class ChatBoxInputManager {
    private final Client client;
    private final ClientThread clientThread;
    private final EventBus eventBus;
    private final KeyManager keyManager;
    private final MouseManager mouseManager;
    private final ChatboxPanelManager chatboxPanelManager;

    public ChatBoxInputManager(Client client, ClientThread clientThread, EventBus eventBus,
                               KeyManager keyManager, MouseManager mouseManager,
                               ChatboxPanelManager chatboxPanelManager) {
        this.client = client;
        this.clientThread = clientThread;
        this.eventBus = eventBus;
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;
        this.chatboxPanelManager = chatboxPanelManager;
    }

    public void close() {
        clientThread.invokeLater(this::unsafeCloseInput);
    }

    private void unsafeCloseInput() {
        client.runScript(ScriptID.MESSAGE_LAYER_CLOSE, 0, 1, 0);
        if (chatboxPanelManager.getCurrentInput() != null) {
            killCurrentPanel();
        }
    }

    public void openInput(ChatboxInput input) {
        clientThread.invokeLater(() -> unsafeOpenInput(input));
    }

    private void unsafeOpenInput(ChatboxInput input) {
        client.runScript(ScriptID.MESSAGE_LAYER_OPEN, 0);

        eventBus.register(input);
        if (input instanceof KeyListener) {
            keyManager.registerKeyListener((KeyListener) input);
        }
        if (input instanceof MouseListener) {
            mouseManager.registerMouseListener((MouseListener) input);
        }
        if (input instanceof MouseWheelListener) {
            mouseManager.registerMouseWheelListener((MouseWheelListener) input);
        }

        if (chatboxPanelManager.getCurrentInput() != null) {
            killCurrentPanel();
        }

        chatboxPanelManager.setCurrentInput(input);
        client.setVarcIntValue(VarClientInt.INPUT_TYPE, InputType.RUNELITE_CHATBOX_PANEL.getType());
        client.getWidget(ComponentID.CHATBOX_TITLE).setHidden(true);
        client.getWidget(ComponentID.CHATBOX_FULL_INPUT).setHidden(true);

        Widget c = getContainerWidget();
        c.deleteAllChildren();
        c.setOnDialogAbortListener((JavaScriptCallback) ev -> this.unsafeCloseInput());
        input.open();
    }

    public void onScriptPreFired(ScriptPreFired ev) {
        if (chatboxPanelManager.getCurrentInput() != null && ev.getScriptId() == ScriptID.MESSAGE_LAYER_CLOSE) {
            killCurrentPanel();
        }
    }

    public void onGameStateChanged(GameStateChanged ev) {
        if (chatboxPanelManager.getCurrentInput() != null && ev.getGameState() == GameState.LOGIN_SCREEN) {
            killCurrentPanel();
        }
    }

    private void killCurrentPanel() {
        try {
            chatboxPanelManager.getCurrentInput().close();
        } catch (Exception e) {
            log.warn("Exception closing {}", chatboxPanelManager.getCurrentInput().getClass(), e);
        }

        eventBus.unregister(chatboxPanelManager.getCurrentInput());
        if (chatboxPanelManager.getCurrentInput() instanceof KeyListener) {
            keyManager.unregisterKeyListener((KeyListener) chatboxPanelManager.getCurrentInput());
        }
        if (chatboxPanelManager.getCurrentInput() instanceof MouseListener) {
            mouseManager.unregisterMouseListener((MouseListener) chatboxPanelManager.getCurrentInput());
        }
        if (chatboxPanelManager.getCurrentInput() instanceof MouseWheelListener) {
            mouseManager.unregisterMouseWheelListener((MouseWheelListener) chatboxPanelManager.getCurrentInput());
        }
        chatboxPanelManager.setCurrentInput(null);
    }

    private Widget getContainerWidget() {
        return client.getWidget(ComponentID.CHATBOX_CONTAINER);
    }

    public boolean shouldTakeInput() {
        Widget worldMapSearch = client.getWidget(ComponentID.WORLD_MAP_SEARCH);
        return worldMapSearch == null || client.getVarcIntValue(VarClientInt.WORLD_MAP_SEARCH_FOCUSED) != 1;
    }
}
