package cc.sighs.interactionexpansion.framework;

import cc.sighs.interactionexpansion.InteractionExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class InteractionManager {
    private static final Map<Block, List<InteractionProcesser>> INTERACTIONS = new HashMap<>();

    public static void addInteraction(Block block, InteractionProcesser processer) {
        INTERACTIONS.computeIfAbsent(block, k -> new ArrayList<>()).add(processer);
        InteractionExpansion.LOGGER.debug("Added interaction to block: {}", ForgeRegistries.BLOCKS.getKey(block));
    }

    public static void addInteraction(String blockId, InteractionProcesser processer) {
        ResourceLocation location = new ResourceLocation(blockId);
        Block block = ForgeRegistries.BLOCKS.getValue(location);
        if (block != null) {
            addInteraction(block, processer);
        } else {
            InteractionExpansion.LOGGER.warn("Block not found: {}", blockId);
        }
    }

    public static List<InteractionProcesser> getInteractions(Block block) {
        return INTERACTIONS.getOrDefault(block, Collections.emptyList());
    }

    public static void clearInteractions(Block block) {
        INTERACTIONS.remove(block);
        InteractionExpansion.LOGGER.debug("Cleared interactions for block: {}", ForgeRegistries.BLOCKS.getKey(block));
    }

    public static void clearAll() {
        INTERACTIONS.clear();
        InteractionExpansion.LOGGER.debug("Cleared all interactions");
    }

    @FunctionalInterface
    public interface InteractionProcesser {
        void handle(InteractionContext context);
    }

    public static class InteractionContext {
        private final Block block;
        private final Map<String, Object> data;

        public InteractionContext(Block block) {
            this.block = block;
            this.data = new HashMap<>();
        }

        public Block getBlock() {
            return block;
        }

        public void setData(String key, Object value) {
            data.put(key, value);
        }

        public Object getData(String key) {
            return data.get(key);
        }

        public <T> T getData(String key, Class<T> type) {
            Object value = data.get(key);
            return type.isInstance(value) ? type.cast(value) : null;
        }
    }
}