package net.runelite.client.game.chatbox;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
@Singleton
@Slf4j
public class ChatboxPanelManager {
	private final Client client;
	private final ClientThread clientThread;
	private final EventBus eventBus;
	private final KeyManager keyManager;
	private final MouseManager mouseManager;
	private final Provider<ChatboxTextMenuInput> chatboxTextMenuInputProvider;
	private final Provider<ChatboxTextInput> chatboxTextInputProvider;

	@Getter
	@Setter
	private ChatboxInput currentInput = null;

	private final ChatBoxInputManager chatBoxInputManager;

	@Inject
	public ChatboxPanelManager(EventBus eventBus, Client client, ClientThread clientThread,
							   KeyManager keyManager, MouseManager mouseManager,
							   Provider<ChatboxTextMenuInput> chatboxTextMenuInputProvider, Provider<ChatboxTextInput> chatboxTextInputProvider) {
		this.client = client;
		this.clientThread = clientThread;
		this.eventBus = eventBus;
		this.keyManager = keyManager;
		this.mouseManager = mouseManager;
		this.chatboxTextMenuInputProvider = chatboxTextMenuInputProvider;
		this.chatboxTextInputProvider = chatboxTextInputProvider;
		this.chatBoxInputManager = new ChatBoxInputManager(client, clientThread, eventBus, keyManager, mouseManager, this);

		eventBus.register(this);
	}

	public void close() {
		chatBoxInputManager.close();
	}

	public void openInput(ChatboxInput input) {
		chatBoxInputManager.openInput(input);
	}

	public ChatboxTextMenuInput openTextMenuInput(String title) {
		return chatboxTextMenuInputProvider.get().title(title);
	}

	public ChatboxTextInput openTextInput(String prompt) {
		return chatboxTextInputProvider.get().prompt(prompt);
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired ev) {
		chatBoxInputManager.onScriptPreFired(ev);
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged ev) {
		chatBoxInputManager.onGameStateChanged(ev);
	}

	public boolean shouldTakeInput() {
		return chatBoxInputManager.shouldTakeInput();
	}
	public Widget getContainerWidget() {
		return client.getWidget(ComponentID.CHATBOX_CONTAINER);
	}
}
