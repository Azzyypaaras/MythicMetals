package nourl.mythicmetals.registry;

import net.minecraft.util.Identifier;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.misc.RegistryHelper;

/**
 * Simple resource conditions that check if the corresponding {@link MythicMetals#CONFIG} booleans are enabled.
 * <br>
 * This is used to check if anvils or nuggets are disabled, so that errors regarding them can be surpressed.
 */
public class RegisterResourceConditions {

    private static final Identifier ANVILS_LOADED = RegistryHelper.id("anvils_enabled");
    private static final Identifier NUGGETS_LOADED = RegistryHelper.id("nuggets_enabled");

    public static void init() {
        // FIXME - No resource conditions for determining whether config options are present
        //ResourceConditions.register(ANVILS_LOADED, object -> MythicMetals.CONFIG.enableAnvils());
        //ResourceConditions.register(NUGGETS_LOADED, object -> MythicMetals.CONFIG.enableNuggets());
    }
}
