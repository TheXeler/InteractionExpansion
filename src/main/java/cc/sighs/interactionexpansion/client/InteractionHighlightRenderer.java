package cc.sighs.interactionexpansion.client;

import cc.sighs.interactionexpansion.InteractionExpansion;
import cc.sighs.interactionexpansion.framework.InteractionManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = InteractionExpansion.MOD_ID, value = Dist.CLIENT)
public class InteractionHighlightRenderer {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        //TODO : 拼尽全力未能肘赢
        /*
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            return;
        }

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        double renderDistance = 16.0;

        BlockPos playerPos = mc.player.blockPosition();
        int range = (int) Math.ceil(renderDistance);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Matrix4f matrix = poseStack.last().pose();

        for (int x = playerPos.getX() - range; x <= playerPos.getX() + range; x++) {
            for (int y = playerPos.getY() - range; y <= playerPos.getY() + range; y++) {
                for (int z = playerPos.getZ() - range; z <= playerPos.getZ() + range; z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (!mc.level.isLoaded(pos)) {
                        continue;
                    }

                    BlockState blockState = mc.level.getBlockState(pos);
                    Block block = blockState.getBlock();

                    if (InteractionManager.getInteractionCount(block) > 0) {
                        double centerX = pos.getX() + 0.5;
                        double centerY = pos.getY() + 0.5;
                        double centerZ = pos.getZ() + 0.5;

                        float size = 0.15F;

                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY - size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY - size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY + size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY + size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();

                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY - size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY - size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY + size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY + size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();

                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY - size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY - size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY + size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY + size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();

                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY - size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY - size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY + size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY + size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();

                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY - size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY - size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY - size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY - size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();

                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY + size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX - size), (float)(centerY + size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY + size), (float)(centerZ + size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                        bufferBuilder.vertex(matrix, (float)(centerX + size), (float)(centerY + size), (float)(centerZ - size))
                                .color(255, 255, 255, 255)
                                .endVertex();
                    }
                }
            }
        }

        tesselator.end();
        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        */
    }
}
