package net.runelite.client.plugins.timetracking.farming;

import java.time.Instant;

import net.runelite.api.Varbits;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManagerTest;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class PatchStateTests extends FarmingContractManagerTest {
    @Test
    public void cabbageContractOnionHarvestableAndEmptyPatch() {
        final long unixNow = Instant.now().getEpochSecond();

        final FarmingPatch patch = farmingGuildPatches.get(Varbits.FARMING_4773);

        assertNotNull(patch);

        when(farmingTracker.predictPatch(patch))
                .thenReturn(new PatchPrediction(Produce.ONION, CropState.HARVESTABLE, unixNow, 3, 3));

        farmingContractManager.setContract(Produce.CABBAGE);

        assertEquals(SummaryState.EMPTY, farmingContractManager.getSummary());
    }

    @Test
    public void cabbageContractOnionHarvestableAndCabbageGrowing() {
        final long unixNow = Instant.now().getEpochSecond();
        final long expectedTime = unixNow + 60;

        final FarmingPatch patch1 = farmingGuildPatches.get(Varbits.FARMING_4773);
        final FarmingPatch patch2 = farmingGuildPatches.get(Varbits.FARMING_4774);

        assertNotNull(patch1);
        assertNotNull(patch2);

        when(farmingTracker.predictPatch(patch1))
                .thenReturn(new PatchPrediction(Produce.ONION, CropState.HARVESTABLE, unixNow, 3, 3));
        when(farmingTracker.predictPatch(patch2))
                .thenReturn(new PatchPrediction(Produce.CABBAGE, CropState.GROWING, expectedTime, 2, 3));

        farmingContractManager.setContract(Produce.CABBAGE);

        assertEquals(SummaryState.IN_PROGRESS, farmingContractManager.getSummary());
        assertEquals(CropState.GROWING, farmingContractManager.getContractCropState());
        assertEquals(expectedTime, farmingContractManager.getCompletionTime());
    }
}
