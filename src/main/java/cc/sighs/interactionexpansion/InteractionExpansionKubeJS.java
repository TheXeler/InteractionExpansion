package cc.sighs.interactionexpansion;

import cc.sighs.interactionexpansion.framework.InteractionManager;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class InteractionExpansionKubeJS {

    public static void addInteraction(String blockId, Consumer<InteractionContextWrapper> handler) {
        addInteraction(blockId, "交互 #" + (getNextIndex(blockId)), handler);
    }

    public static void addInteraction(String blockId, String name, Consumer<InteractionContextWrapper> handler) {
        InteractionManager.addInteraction(blockId, name, (context) -> {
            handler.accept(new InteractionContextWrapper(context));
        });
    }

    public static void clearInteractions(String blockId) {
        net.minecraft.resources.ResourceLocation location = new net.minecraft.resources.ResourceLocation(blockId);
        Block block = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValue(location);
        if (block != null) {
            InteractionManager.clearInteractions(block);
        }
    }

    public static void clearAll() {
        InteractionManager.clearAll();
    }

    private static int getNextIndex(String blockId) {
        try {
            net.minecraft.resources.ResourceLocation location = new net.minecraft.resources.ResourceLocation(blockId);
            Block block = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                return InteractionManager.getInteractionCount(block) + 1;
            }
        } catch (Exception e) {
            // 忽略错误
        }
        return 1;
    }

    public static class InteractionContextWrapper {
        private final InteractionManager.InteractionContext context;

        public InteractionContextWrapper(InteractionManager.InteractionContext context) {
            this.context = context;
        }

        public String getBlockId() {
            return net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(context.getBlock()).toString();
        }

        public void setData(String key, Object value) {
            context.setData(key, value);
        }

        public Object getData(String key) {
            return context.getData(key);
        }

        public int getSelectedIndex() {
            Integer index = context.getData("selectedIndex", Integer.class);
            return index != null ? index : 0;
        }

        public String getInteractionName() {
            return context.getData("interactionName", String.class);
        }

        public Object getPlayer() {
            return context.getData("player");
        }

        public Object getLevel() {
            return context.getData("level");
        }

        public Object getPos() {
            return context.getData("pos");
        }
    }
}
