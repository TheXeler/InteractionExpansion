package cc.sighs.interactionexpansion.client;

import cc.sighs.interactionexpansion.InteractionExpansion;
import cc.sighs.interactionexpansion.framework.InteractionManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = InteractionExpansion.MOD_ID, value = Dist.CLIENT)
public class InteractionTooltipHandler {
    private static final int MAX_VISIBLE_ITEMS = 5;
    private static final int ITEM_HEIGHT = 12;
    private static final int PADDING = 4;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return;

        BlockPos pos = blockHitResult.getBlockPos();
        Block block = mc.level.getBlockState(pos).getBlock();

        List<InteractionManager.InteractionHandler> interactions = InteractionManager.getInteractions(block);
        if (interactions.isEmpty()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font font = mc.font;

        int interactionCount = interactions.size();
        int visibleCount = Math.min(interactionCount, MAX_VISIBLE_ITEMS);
        int boxWidth = 150;
        int boxHeight = visibleCount * ITEM_HEIGHT + PADDING * 2;

        int mouseX = event.getWindow().getGuiScaledWidth() / 2 + 8;
        int mouseY = event.getWindow().getGuiScaledHeight() / 2 - 8;

        RenderSystem.enableBlend();
        guiGraphics.fill(mouseX, mouseY, mouseX + boxWidth, mouseY + boxHeight, 0x90000000);
        guiGraphics.renderOutline(mouseX, mouseY, boxWidth, boxHeight, 0xFFFFFFFF);

        Component title = Component.literal("§6可用交互 (" + interactionCount + ")");
        guiGraphics.drawString(font, title, mouseX + PADDING, mouseY + PADDING, 0xFFFFFF, true);

        for (int i = 0; i < visibleCount; i++) {
            String text = "• 交互 #" + (i + 1);
            int yPos = mouseY + PADDING + ITEM_HEIGHT + (i * ITEM_HEIGHT);
            guiGraphics.drawString(font, text, mouseX + PADDING + 4, yPos, 0xAAAAAA, false);
        }

        if (interactionCount > MAX_VISIBLE_ITEMS) {
            String moreText = "§7...还有 " + (interactionCount - MAX_VISIBLE_ITEMS) + " 个";
            int yPos = mouseY + PADDING + ITEM_HEIGHT + (visibleCount * ITEM_HEIGHT);
            guiGraphics.drawString(font, moreText, mouseX + PADDING + 4, yPos, 0x888888, false);
        }

        RenderSystem.disableBlend();
    }
}