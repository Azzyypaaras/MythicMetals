package nourl.mythicmetals.item.tools;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.*;
import java.util.function.Consumer;

public class BanglumToolSet extends ToolSet {

    @Override
    protected PickaxeItem makePickaxe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new BanglumPick(material, settings.attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed)));
    }

    @Override
    protected ShovelItem makeShovel(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new BanglumShovel(material, settings.attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed)));
    }

    public BanglumToolSet(ToolMaterial material, int[] damage, float[] speed, Consumer<OwoItemSettings> settingsProcessor) {
        super(material, damage, speed, settingsProcessor);
    }

}
