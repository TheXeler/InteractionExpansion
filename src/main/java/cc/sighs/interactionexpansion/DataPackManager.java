package cc.sighs.interactionexpansion;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = InteractionExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataPackManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

    private static final Map<ResourceLocation, JsonObject> INTERACTION_DATA = new HashMap<>();

    public DataPackManager() {
        super(GSON, "interaction");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        INTERACTION_DATA.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonElement jsonElement = entry.getValue();

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                INTERACTION_DATA.put(location, jsonObject);

                String formattedText = convertJsonToText(jsonObject, location);
                LOGGER.info("Loaded interaction data from {}: {}", location, formattedText);
            } else {
                LOGGER.warn("Invalid JSON format in data pack file: {}", location);
            }
        }

        LOGGER.info("Successfully loaded {} interaction data files", INTERACTION_DATA.size());
    }

    private String convertJsonToText(JsonObject jsonObject, ResourceLocation location) {
        StringBuilder sb = new StringBuilder();
        sb.append("File: ").append(location.toString()).append("\n");

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            sb.append("  ").append(key).append(": ").append(value.toString()).append("\n");
        }

        return sb.toString().trim();
    }

    public static Map<ResourceLocation, JsonObject> getInteractionData() {
        return new HashMap<>(INTERACTION_DATA);
    }

    public static JsonObject getInteractionData(ResourceLocation location) {
        return INTERACTION_DATA.get(location);
    }

    public static boolean hasInteractionData(ResourceLocation location) {
        return INTERACTION_DATA.containsKey(location);
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new DataPackManager());
        LOGGER.info("Registered data pack reload listener for interaction data");
    }
}
