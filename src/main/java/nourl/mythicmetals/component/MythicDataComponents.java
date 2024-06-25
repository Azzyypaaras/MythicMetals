package nourl.mythicmetals.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import nourl.mythicmetals.misc.RegistryHelper;

public class MythicDataComponents {
    public static final DataComponentType<GoldFoldedComponent> GOLD_FOLDED = RegistryHelper.dataComponentType(
        "gold_folded", builder -> builder.codec(GoldFoldedComponent.ENDEC.codec()).packetCodec(GoldFoldedComponent.ENDEC.packetCodec())
    );
    public static final DataComponentType<Boolean> LOCKED = RegistryHelper.dataComponentType(
        "locked", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL)
    );
    public static final DataComponentType<Boolean> ENCORE = RegistryHelper.dataComponentType(
        "encore", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL)
    );
    public static final DataComponentType<Boolean> IS_USED = RegistryHelper.dataComponentType(
        "encore", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL)
    );
    public static final DataComponentType<CarmotStaffComponent> CARMOT_STAFF_BLOCK = RegistryHelper.dataComponentType("carmot_staff_block",
        builder -> builder.codec(CarmotStaffComponent.ENDEC.codec()).packetCodec(CarmotStaffComponent.ENDEC.packetCodec())
    );
    public static final DataComponentType<TidesingerPatternComponent> TIDESINGER = RegistryHelper.dataComponentType("tidesinger",
      builder -> builder.codec(TidesingerPatternComponent.ENDEC.codec()).packetCodec(TidesingerPatternComponent.ENDEC.packetCodec())
    );
    public static final DataComponentType<DrillComponent> DRILL = RegistryHelper.dataComponentType("drill", builder ->
        builder.codec(DrillComponent.ENDEC.codec()).packetCodec(DrillComponent.ENDEC.packetCodec())
    );
    public static final DataComponentType<UpgradeComponent> UPGRADES = RegistryHelper.dataComponentType("upgrades", builder ->
        builder.codec(UpgradeComponent.ENDEC.codec()).packetCodec(UpgradeComponent.ENDEC.packetCodec())
    );
    public static final DataComponentType<PrometheumComponent> PROMETHEUM = RegistryHelper.dataComponentType("prometheum", builder ->
        builder.codec(PrometheumComponent.ENDEC.codec()).packetCodec(PrometheumComponent.ENDEC.packetCodec())
    );
}
