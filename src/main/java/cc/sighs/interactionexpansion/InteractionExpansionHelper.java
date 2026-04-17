package cc.sighs.interactionexpansion;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class InteractionExpansionHelper {
    public static boolean isValidItem(String itemId) {
        ResourceLocation location = new ResourceLocation(itemId);
        return ForgeRegistries.ITEMS.containsKey(location);
    }

    public static boolean isValidBlock(String blockId) {
        ResourceLocation location = new ResourceLocation(blockId);
        return ForgeRegistries.BLOCKS.containsKey(location);
    }

    public static String getModId() {
        return InteractionExpansion.MOD_ID;
    }

    public static void log(String message) {
        InteractionExpansion.LOGGER.info("[KubeJS] {}", message);
    }
}