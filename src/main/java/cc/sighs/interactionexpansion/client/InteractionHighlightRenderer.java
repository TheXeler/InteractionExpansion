package cc.sighs.interactionexpansion.client;

import cc.sighs.interactionexpansion.InteractionExpansion;
import cc.sighs.interactionexpansion.framework.InteractionManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = InteractionExpansion.MOD_ID, value = Dist.CLIENT)
public class InteractionHighlightRenderer {
    private static final float HIGHLIGHT_ALPHA = 0.5f;
    private static final int HIGHLIGHT_COLOR = 0x00FF00;
    private static final int LOOKING_AT_COLOR = 0xFFFF00;
    private static final int MAX_HIGHLIGHT_BLOCKS = 5;
    private static final int SEARCH_RADIUS = 32;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        HitResult hitResult = mc.hitResult;
        BlockPos lookingAtPos = null;

        if (hitResult instanceof BlockHitResult blockHitResult) {
            Block block = mc.level.getBlockState(blockHitResult.getBlockPos()).getBlock();
            List<InteractionManager.NamedInteraction> interactions = InteractionManager.getNamedInteractions(block);
            if (!interactions.isEmpty()) {
                lookingAtPos = blockHitResult.getBlockPos();
            }
        }

        Vec3 playerVec = mc.player.getPosition(1.0f);
        BlockPos playerPos = mc.player.blockPosition();

        List<BlockPosWithDistance> candidateBlocks = new ArrayList<>();

        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);

                    double distSqr = playerVec.distanceToSqr(Vec3.atCenterOf(pos));
                    if (distSqr > SEARCH_RADIUS * SEARCH_RADIUS) continue;

                    Block block = mc.level.getBlockState(pos).getBlock();
                    List<InteractionManager.NamedInteraction> interactions = InteractionManager.getNamedInteractions(block);

                    if (!interactions.isEmpty()) {
                        candidateBlocks.add(new BlockPosWithDistance(pos, distSqr));
                    }
                }
            }
        }

        if (candidateBlocks.isEmpty()) return;

        candidateBlocks.sort(Comparator.comparingDouble(BlockPosWithDistance::distance));

        List<BlockPos> highlightBlocks = candidateBlocks.stream()
            .limit(MAX_HIGHLIGHT_BLOCKS)
            .map(BlockPosWithDistance::pos)
            .collect(Collectors.toList());

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix = poseStack.last().pose();

        for (BlockPos pos : highlightBlocks) {
            VoxelShape shape = mc.level.getBlockState(pos).getShape(mc.level, pos);
            if (shape.isEmpty()) continue;

            int color = (lookingAtPos != null && pos.equals(lookingAtPos)) ? LOOKING_AT_COLOR : HIGHLIGHT_COLOR;
            float alpha = HIGHLIGHT_ALPHA;

            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
                double x1 = minX + pos.getX();
                double y1 = minY + pos.getY();
                double z1 = minZ + pos.getZ();
                double x2 = maxX + pos.getX();
                double y2 = maxY + pos.getY();
                double z2 = maxZ + pos.getZ();

                bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z1)
                    .color(r, g, b, alpha).endVertex();
                bufferBuilder.vertex(matrix, (float) x2, (float) y2, (float) z2)
                    .color(r, g, b, alpha).endVertex();
            });
        }

        tesselator.end();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private record BlockPosWithDistance(BlockPos pos, double distance) {}
}
