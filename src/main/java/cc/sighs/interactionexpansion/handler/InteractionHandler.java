package cc.sighs.interactionexpansion.handler;

import cc.sighs.interactionexpansion.InteractionExpansion;
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

@Mod.EventBusSubscriber(modid = InteractionExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        BlockHitResult hitResult = event.getHitVec();

        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();

        if (InteractionManager.getInteractions(block) != null && InteractionManager.getInteractions(block).size() > 1) {
            InteractionManager.InteractionContext context = new InteractionManager.InteractionContext(block);

            context.setData("player", player);
            context.setData("is_server", !level.isClientSide());
            context.setData("level", level);
            context.setData("pos", pos);
            context.setData("hand", hand);
            context.setData("hitResult", hitResult);

            for (InteractionManager.InteractionProcesser processer : InteractionManager.getInteractions(block)) {
                processer.handle(context);
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
