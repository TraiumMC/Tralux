package me.coderfrish.tralux;

import joptsimple.OptionSet;
import me.coderfrish.tralux.configuration.GlobalConfiguration;
import net.minecraft.server.Main;

public class TraluxBootstrap {
    public static void bootstrap(final OptionSet options) {
        GlobalConfiguration.create(options);

        Main.main(options);
    }
}
