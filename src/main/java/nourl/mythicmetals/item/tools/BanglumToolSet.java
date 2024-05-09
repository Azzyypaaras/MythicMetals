package nourl.mythicmetals.item.tools;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.*;
import java.util.function.Consumer;

public class BanglumToolSet extends ToolSet {

    @Override
    protected PickaxeItem makePickaxe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new BanglumPick(material, settings);
    }

    @Override
    protected ShovelItem makeShovel(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new BanglumShovel(material, settings);
    }

    public BanglumToolSet(ToolMaterial material, int[] damage, float[] speed, Consumer<OwoItemSettings> settingsProcessor) {
        super(material, damage, speed, settingsProcessor);
    }

}
