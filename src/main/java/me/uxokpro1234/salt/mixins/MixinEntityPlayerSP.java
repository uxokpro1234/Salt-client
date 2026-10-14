package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.modules.movement.NoSlow;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("all")
@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {
    NoSlow module = Salt.INSTANCE.moduleManager.getModuleByClass(NoSlow.class);

    // Modify a float constant inside EntityPlayerSP.onLivingUpdate().
    // In Minecraft 1.8.9, 0.2F is commonly used as the
    // movement slowdown multiplier when using an item.
    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(floatValue = 0.2F))
    private float salt$cancelEatingSlowdown(float constant) {
        // If NoSlow doesn't exist or is disabled,
        // keep Minecraft's original constant unchanged.
        if (module == null || !module.isEnabled()) {
            return constant;
        }
        // Replace the slowdown multiplier with 1.0F.
        // Multiplying movement by 1.0 means it stays unchanged,
        // instead of being reduced to 20% by a 0.2F multiplier.
        return 1.0F;
    }
}