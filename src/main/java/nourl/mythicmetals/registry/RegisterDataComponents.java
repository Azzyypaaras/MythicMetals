package nourl.mythicmetals.registry;

import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import nourl.mythicmetals.misc.RegistryHelper;

public class RegisterDataComponents {
    public static final DataComponentType<Integer> GOLD_FOLDED = RegistryHelper.dataComponentType(
        "max_damage", builder -> builder.codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.VAR_INT)
    );


}
