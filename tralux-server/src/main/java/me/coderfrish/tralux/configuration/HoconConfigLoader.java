package me.coderfrish.tralux.configuration;

import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public class HoconConfigLoader {
    public static HoconConfigurationLoader create(Path path) {
        return HoconConfigurationLoader.builder().path(path).build();
    }
}
