package cc.sighs.interactionexpansion.handler;

import cc.sighs.interactionexpansion.InteractionExpansion;
import cc.sighs.interactionexpansion.client.InteractionTooltipHandler;
import cc.sighs.interactionexpansion.framework.InteractionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = InteractionExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionHandler {
    private static final long INTERACTION_COOLDOWN = 250;
    private static final Map<UUID, Long> lastInteractionTime = new HashMap<>();

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        Player player = event.getEntity();
        UUID playerId = player.getUUID();
        long currentTime = System.currentTimeMillis();

        Long lastTime = lastInteractionTime.get(playerId);
        if (lastTime != null && (currentTime - lastTime) < INTERACTION_COOLDOWN) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();
        BlockHitResult hitResult = event.getHitVec();

        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();

        int interactionCount = InteractionManager.getInteractionCount(block);

        if (interactionCount > 0) {
            int selectedIndex = InteractionTooltipHandler.getCurrentSelectedIndex();

            if (selectedIndex >= 0 && selectedIndex < interactionCount) {
                InteractionManager.NamedInteraction namedInteraction =
                    InteractionManager.getInteraction(block, selectedIndex);

                if (namedInteraction != null) {
                    InteractionManager.InteractionContext context =
                        new InteractionManager.InteractionContext(block);

                    context.setData("player", player);
                    context.setData("level", level);
                    context.setData("pos", pos);
                    context.setData("hand", hand);
                    context.setData("hitResult", hitResult);
                    context.setData("selectedIndex", selectedIndex);
                    context.setData("interactionName", namedInteraction.name());

                    namedInteraction.processer().handle(context);

                    lastInteractionTime.put(playerId, currentTime);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
