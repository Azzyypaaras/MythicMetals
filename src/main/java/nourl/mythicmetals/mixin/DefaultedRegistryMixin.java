package nourl.mythicmetals.mixin;

import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.util.Identifier;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.misc.LegacyIds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// This Mixin is a class that works as a datafixer.
// Upon loading a world it will check for missing objects in the recipe and replace them in order to
// prevent air pockets when upgrading from older worlds, as well as returning changed/removed items.
@Mixin(SimpleDefaultedRegistry.class)
public class DefaultedRegistryMixin {

    @ModifyVariable(at = @At("HEAD"), method = "get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;", ordinal = 0, argsOnly = true)
    Identifier fixMissingFromRegistry(@Nullable Identifier id) {
        if (id != null) {
            // Various MOD_ID renames across mod versions, including Mythic Metals Decorations
            if (id.getNamespace().equals("mm_decorations"))
                return Identifier.of("mythicmetals_decorations", id.getPath());
            if (id.getNamespace().equals("mythicaddons") && !id.getPath().contains("aegis"))
                return Identifier.of("mythicmetals_decorations", id.getPath());
            if (id.getNamespace().equals("mythicaddons") && id.getPath().contains("aegis"))
                return Identifier.of(MythicMetals.MOD_ID, id.getPath());
            if (LegacyIds.getLegacyIds().containsKey(id)) return LegacyIds.getLegacyIds().get(id);

        }
        return id;
    }
}
