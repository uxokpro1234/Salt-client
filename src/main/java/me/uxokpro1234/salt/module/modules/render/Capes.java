package me.uxokpro1234.salt.module.modules.render;

import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("all")
public class Capes extends Module {

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

    public Setting<CapeMode> capeMode = register(new Setting<> ("Cape", CapeMode.M2011));
    private static final Capes INSTANCE = new Capes();
    public static Capes getInstance() {
        return INSTANCE;
    }

    public Capes() {
        super("Capes", Category.RENDER, Keyboard.KEY_NONE);
    }
}
