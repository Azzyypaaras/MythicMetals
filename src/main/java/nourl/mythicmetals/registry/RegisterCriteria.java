package nourl.mythicmetals.registry;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.misc.SimpleCriteria;
import java.lang.reflect.Field;

public class RegisterCriteria implements SimpleFieldProcessingSubject<Criterion<?>> {
    public static final SimpleCriteria USED_BLAST_MINING = new SimpleCriteria();
    public static final SimpleCriteria USE_ENCHANTED_MIDAS_IN_CARMOT_STAFF = new SimpleCriteria();
    public static final SimpleCriteria RECEIVED_COMBUSTION_FROM_CREEPER = new SimpleCriteria();

    @Override
    public void processField(Criterion<?> value, String name, Field field) {
        Criteria.register(MythicMetals.MOD_ID + ":" + name, value);
    }

    @Override
    public Class<Criterion<?>> getTargetFieldType() {
        return AutoRegistryContainer.conform(Criterion.class);
    }
}
