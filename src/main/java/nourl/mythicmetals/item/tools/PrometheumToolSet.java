package nourl.mythicmetals.item.tools;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.*;
import nourl.mythicmetals.component.MythicDataComponents;
import nourl.mythicmetals.component.PrometheumComponent;

public class PrometheumToolSet extends ToolSet {

    public PrometheumToolSet(ToolMaterial material, int[] damage, float[] speed) {
        super(material, damage, speed);
    }

    @Override
    protected PickaxeItem makePickaxe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumPick(material, (OwoItemSettings) settings
            .attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected SwordItem makeSword(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumSword(material, (OwoItemSettings) settings
            .attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected AxeItem makeAxe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumAxe(material, (OwoItemSettings) settings
            .attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected HoeItem makeHoe(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumHoe(material, (OwoItemSettings) settings
            .attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    @Override
    protected ShovelItem makeShovel(ToolMaterial material, int damage, float speed, OwoItemSettings settings) {
        return new PrometheumShovel(material, (OwoItemSettings) settings
            .attributeModifiers(ToolSet.createAttributeModifiers(material, damage, speed))
            .component(MythicDataComponents.PROMETHEUM, PrometheumComponent.DEFAULT)
        );
    }

    public static class PrometheumAxe extends AxeItem implements AutoRepairable {
        public PrometheumAxe(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

    }

    public static class PrometheumHoe extends HoeItem implements AutoRepairable {
        public PrometheumHoe(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

    }

    public static class PrometheumPick extends PickaxeItem implements AutoRepairable {
        public PrometheumPick(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

    }

    public static class PrometheumShovel extends ShovelItem implements AutoRepairable {
        public PrometheumShovel(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

    }

    public static class PrometheumSword extends SwordItem implements AutoRepairable {
        public PrometheumSword(ToolMaterial material, OwoItemSettings settings) {
            super(material, settings);
        }

    }

}
