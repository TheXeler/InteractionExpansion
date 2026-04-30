package cc.sighs.interactionexpansion;

import cc.sighs.interactionexpansion.framework.InteractionManager;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;

public class InteractionExpansionJSPlugins extends KubeJSPlugin {
    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("InteractionExpansion", InteractionExpansionKubeJS.class);
    }

    @Override
    public void registerClasses(ScriptType type, ClassFilter filter) {
        filter.allow("cc.sighs.interactionexpansion");
    }
}
