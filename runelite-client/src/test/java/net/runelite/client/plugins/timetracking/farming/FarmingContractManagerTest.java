package net.runelite.client.plugins.timetracking.farming;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
/*
Farming Contract manage was too big, so i made it an abstract class and extended it for contract setting tests
and patch state tests
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class FarmingContractManagerTest
{
	protected Map<Integer, FarmingPatch> farmingGuildPatches = new HashMap<>();

	@Inject
	protected FarmingContractManager farmingContractManager;

	@Inject
	protected FarmingWorld farmingWorld;

	@Mock
	@Bind
	protected TimeTrackingConfig config;

	@Mock
	@Bind
	protected Client client;

	@Mock
	@Bind
	protected FarmingTracker farmingTracker;

	@Mock
	@Bind
	protected ConfigManager configManager;

	@Mock
	@Bind
	protected Notifier notifier;

	@Mock
	@Bind
	protected ItemManager itemManager;

	@Mock
	@Bind
	protected InfoBoxManager infoBoxManager;

	@Mock
	@Bind
	protected ScheduledExecutorService executor;

	@Mock
	@Bind
	protected ClientToolbar clientToolbar;

	@Before
	public void before()
	{
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

		for (FarmingPatch p : farmingWorld.getFarmingGuildRegion().getPatches())
		{
			farmingGuildPatches.put(p.getVarbit(), p);
		}

		// Consider all patches to be empty by default
		when(farmingTracker.predictPatch(any(FarmingPatch.class)))
				.thenReturn(new PatchPrediction(null, null, 0, 0, 0));
	}
}
