package me.coderfrish.tralux.core;

import joptsimple.OptionSet;
import net.minecraft.server.Main;

public class TraluxBootstrap {
    public static void bootstrap(final OptionSet options) throws Exception {
        /* tralux start - tralux configuration system */
        me.coderfrish.tralux.config.TraluxConfiguration.initTraluxConfig(options);
        me.coderfrish.tralux.config.TraluxConfiguration.loadAllConfigs();
        /* tralux end - tralux configuration system */

        Main.main(options);
    }

    public static void setup() throws Exception {
        me.coderfrish.tralux.config.TraluxConfiguration.setupAllConfigs();
    }
}
