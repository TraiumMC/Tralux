package me.coderfrish.tralux.configuration;

import joptsimple.OptionSet;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class GlobalConfiguration extends TraluxConfiguration{
    static final String TRALUX_GLOBAL_CONFIG_FILE = "tralux-global.cfg";
    private static GlobalConfiguration instance;
    private static HoconConfigurationLoader loader;
    private ConfigurationNode root;

    private boolean loaded;

    public static GlobalConfiguration get() {
        return instance;
    }

    @Override
    public void load() {
        try {
            this.root = loader.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        this.loaded = true;
    }

    @Override
    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public String brand;

    @Override
    public void set() {
        instance = this;

        if (!loaded) {
            this.root = loader.createNode();
        }

        try {
            if (this.root.hasChild("server", "brand")) {
                this.brand = this.root.node("server", "brand").getString();
            } else {
                this.brand = "Tralux";
                this.root.node("server", "brand").set(this.brand);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static GlobalConfiguration create(OptionSet optionSet) {
        final Path configDirPath = ((File) optionSet.valueOf("tralux-settings-directory")).toPath();
        final Path configFilePath = configDirPath.resolve(TRALUX_GLOBAL_CONFIG_FILE);
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        loader = HoconConfigLoader.create(configFilePath);

        if (Files.exists(configFilePath))
            globalConfiguration.load();

        globalConfiguration.set();

        if (!Files.exists(configFilePath))
            globalConfiguration.save();

        return globalConfiguration;
    }
}
