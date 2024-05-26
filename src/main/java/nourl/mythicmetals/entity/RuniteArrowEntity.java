package nourl.mythicmetals.entity;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.data.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import nourl.mythicmetals.item.tools.MythicTools;

// [VanillaCopy]
public class RuniteArrowEntity extends PersistentProjectileEntity {
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(RuniteArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final ItemStack RUNITE_ARROW_STACK = new ItemStack(MythicTools.RUNITE_ARROW);

    public RuniteArrowEntity(EntityType<RuniteArrowEntity> type, World world) {
        super(MythicEntities.RUNITE_ARROW_ENTITY_TYPE, world, RUNITE_ARROW_STACK);
    }

    public RuniteArrowEntity(LivingEntity shooter, World world) {
        super(MythicEntities.RUNITE_ARROW_ENTITY_TYPE, shooter, world, RUNITE_ARROW_STACK);
    }

    private PotionContentsComponent getPotionContents() {
        return this.getItemStack().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
    }

    protected void initColor() {
        PotionContentsComponent potionContentsComponent = this.getPotionContents();
        this.dataTracker.set(COLOR, potionContentsComponent.equals(PotionContentsComponent.DEFAULT) ? -1 : potionContentsComponent.getColor());
    }

    @Override
    protected void setStack(ItemStack stack) {
        super.setStack(stack);
        this.initColor();
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return RUNITE_ARROW_STACK;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        Entity entity = this.getEffectCause();
        PotionContentsComponent potionContentsComponent = this.getPotionContents();
        if (potionContentsComponent.potion().isPresent()) {
            for (StatusEffectInstance statusEffectInstance : potionContentsComponent.getEffects()) {
                target.addStatusEffect(
                    new StatusEffectInstance(
                        statusEffectInstance.getEffectType(),
                        Math.max(statusEffectInstance.mapDuration(i -> i / 8), 1),
                        statusEffectInstance.getAmplifier(),
                        statusEffectInstance.isAmbient(),
                        statusEffectInstance.shouldShowParticles()
                    ),
                    entity
                );
            }
        }

        for (StatusEffectInstance statusEffectInstance : potionContentsComponent.customEffects()) {
            target.addStatusEffect(statusEffectInstance, entity);
        }

    }

    public int getColor() {
        return this.dataTracker.get(COLOR);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContentsComponent.DEFAULT) && this.inGroundTime >= 600) {
            this.getWorld().sendEntityStatus(this, (byte) 0);
            this.setStack(RUNITE_ARROW_STACK);
        }
    }

    private void spawnParticles(int amount) {
        int i = this.getColor();
        if (i != -1 && amount > 0) {
            for (int j = 0; j < amount; ++j) {
                this.getWorld()
                    .addParticle(
                        EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, i), this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0
                    );
            }
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (float) (i >> 16 & 0xFF) / 255.0F;
                float g = (float) (i >> 8 & 0xFF) / 255.0F;
                float h = (float) (i & 0xFF) / 255.0F;

                for (int j = 0; j < 20; ++j) {
                    this.getWorld()
                        .addParticle(
                            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, f, g, h),
                            this.getParticleX(0.5),
                            this.getRandomBodyY(),
                            this.getParticleZ(0.5),
                            0.0,
                            0.0,
                            0.0
                        );
                }
            }
        } else {
            super.handleStatus(status);
        }
    }
}
