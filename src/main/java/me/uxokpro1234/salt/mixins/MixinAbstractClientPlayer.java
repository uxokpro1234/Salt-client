package me.uxokpro1234.salt.mixins;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.modules.render.Capes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
PlayerShader module = Salt.INSTANCE.moduleManager.getModuleByClass(PlayerShader.class);
if (module == null || !module.isEnabled()) return;
    public enum CapeMode {
        M2011,
        M2012,
        M2013,
        M2015,
        M2016,
        M_C_C,
        M_C,
        M_S_C
    }
 */
@Mixin(AbstractClientPlayer.class)
class MixinAbstractClientPlayer {
    Capes module = Salt.INSTANCE.moduleManager.getModuleByClass(Capes.class);
    private static final ResourceLocation MINECON2011_CAPE = new ResourceLocation("salt", "textures/capes/minecon-2011-cape.png");
    private static final ResourceLocation MINECON2012_CAPE = new ResourceLocation("salt", "textures/capes/minecon-2012-cape.png");
    private static final ResourceLocation MINECON2013_CAPE = new ResourceLocation("salt", "textures/capes/minecon-2013-cape.png");
    private static final ResourceLocation MINECON2015_CAPE = new ResourceLocation("salt", "textures/capes/minecon-2015-cape.png");
    private static final ResourceLocation MINECON2016_CAPE = new ResourceLocation("salt", "textures/capes/minecon-2016-cape.png");
    private static final ResourceLocation MOJANG_CAPE = new ResourceLocation("salt", "textures/capes/mojang-cape.png");
    private static final ResourceLocation MOJANG_CLASSIC_CAPE = new ResourceLocation("salt", "textures/capes/mojang-classic-cape.png");
    private static final ResourceLocation MOJANG_STUDIOS_CAPE = new ResourceLocation("salt", "textures/capes/mojang-studios-cape.png");

    @Inject(method = "getLocationCape", at = @At("RETURN"), cancellable = true)
    private void salt$replaceCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if (module == null || !module.isEnabled()) return;
        switch (module.capeMode.getValue()) {
            case M2011:
                cir.setReturnValue(MINECON2011_CAPE);
                break;
            case M2012:
                cir.setReturnValue(MINECON2012_CAPE);
                break;
            case M2013:
                cir.setReturnValue(MINECON2013_CAPE);
                break;
            case M2015:
                cir.setReturnValue(MINECON2015_CAPE);
                break;
            case M2016:
                cir.setReturnValue(MINECON2016_CAPE);
                break;
            case M_C:
                cir.setReturnValue(MOJANG_CAPE);
                break;
            case M_C_C:
                cir.setReturnValue(MOJANG_CLASSIC_CAPE);
                break;
            case M_S_C:
                cir.setReturnValue(MOJANG_STUDIOS_CAPE);
                break;
        }
    }
}
