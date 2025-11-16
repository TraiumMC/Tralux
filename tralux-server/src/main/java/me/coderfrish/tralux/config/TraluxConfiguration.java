package me.coderfrish.tralux.config;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import joptsimple.OptionSet;
import me.coderfrish.tralux.config.annotation.ConfigurationClass;
import me.coderfrish.tralux.config.configs.CMTConfiguration;
import me.coderfrish.tralux.config.configs.LoadConfiguration;
import me.coderfrish.tralux.config.configs.ValueConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TraluxConfiguration {
    private static final Queue<Class<?>> loadQueue = new ConcurrentLinkedQueue<>();
    public static final Path traluxConfigFolder = Path.of("tralux_config");
    private static final Path traluxGlobalFile = traluxConfigFolder.resolve("tralux_global.toml");

    private static final BaseConfiguration cmt = new CMTConfiguration(traluxGlobalFile);
    private static final BaseConfiguration loader = new LoadConfiguration(cmt);
    private static final BaseConfiguration value = new ValueConfiguration(cmt);

    public static void initTraluxConfig(OptionSet options) {
        final Path configDirPath = ((File) options.valueOf("tralux-settings-directory")).toPath();

        if (!Files.exists(configDirPath)) {
            try {
                Files.createDirectories(configDirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loadAllConfigs() {
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            scanResult.getClassesWithAnnotation(ConfigurationClass.class).forEach(clazz -> {
                Class<?> configClass = clazz.loadClass();

                try {
                    value.loadOnlyClass(configClass);
                    cmt.loadOnlyClass(configClass);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                loadQueue.add(configClass);
            });
        }

        cmt.config.save(); /* save config file */
    }

    public static void setupAllConfigs() throws Exception {
        while (!loadQueue.isEmpty()) {
            Class<?> clazz = loadQueue.poll();
            loader.loadOnlyClass(clazz);
        }
    }
}
