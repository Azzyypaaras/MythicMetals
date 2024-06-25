package nourl.mythicmetals.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import nourl.mythicmetals.MythicMetals;
import nourl.mythicmetals.registry.RegisterEntityAttributes;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class CarmotShield implements Component, AutoSyncedComponent {
    private final PlayerEntity player;
    public float shieldHealth;
    public int renderTime;
    public int cooldown;

    public static final int SHIELD_HEALTH_PER_PIECE = 5;
    public static final int MAX_COOLDOWN = 160;

    public CarmotShield(PlayerEntity player) {
        this.player = player;
        shieldHealth = 0;
        renderTime = 0;
        cooldown = 0;
    }

    public boolean shouldRenderShield() {
        return renderTime > 0;
    }

    public void damageShield(float damage) {
        shieldHealth = MathHelper.clamp(shieldHealth - damage, 0f, getMaxHealth());

        // Put the shield on cooldown when you take damage
        if (shieldHealth > 0) {
            renderTime = 20;
            cooldown = 50;
        }

        // Handle if the shield should break
        if (shieldHealth == 0) {
            // Set the shield to render the break animation once
            if (cooldown == 0) {
                renderTime = 30;
            }
            cooldown = MAX_COOLDOWN;
        }
    }

    public void tickShield() {
        if (player.getWorld() == null) return;

        // Prevent overshields
        if (shieldHealth > getMaxHealth()) {
            shieldHealth = getMaxHealth();
        }

        // Regenerate shield if not on cooldown
        if (shieldHealth < getMaxHealth()) {
            if (cooldown == 0) {
                shieldHealth = MathHelper.clamp(shieldHealth += 0.1f, 0f, this.getMaxHealth());
                renderTime = 40;
            } else {
                cooldown--;
            }
        }

        if (shouldRenderShield()) {
            renderTime--;
            MythicMetals.CARMOT_SHIELD.sync(player);
        }

        // No shield, stop rendering
        if (getMaxHealth() == 0) {
            renderTime = 0;
            shieldHealth = 0;
        }
    }

    // TODO - Will the rounding here cause issues?
    public float getMaxHealth() {
        int result = 0;
        if (this.player.getAttributes().hasAttribute(RegisterEntityAttributes.CARMOT_SHIELD)) {
            return (float) this.player.getAttributes().getValue(RegisterEntityAttributes.CARMOT_SHIELD);
        }
        return result;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        shieldHealth = tag.getFloat("health");
        renderTime = tag.getInt("rendertime");
        cooldown = tag.getInt("cooldown");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putFloat("health", shieldHealth);
        tag.putInt("rendertime", renderTime);
        tag.putInt("cooldown", cooldown);

    }
}
