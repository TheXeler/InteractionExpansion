package cc.sighs.interactionexpansion;

import cc.sighs.interactionexpansion.framework.InteractionManager;
import net.minecraft.world.level.block.Block;
import org.openjdk.nashorn.api.scripting.JSObject;

public class InteractionExpansionKubeJS {

    public static void addInteraction(String blockId, JSObject handler) {
        InteractionManager.addInteraction(blockId, (context) -> {
            handler.call(null, new InteractionContextWrapper(context));
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
    }
}