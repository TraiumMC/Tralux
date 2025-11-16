package me.coderfrish.tralux.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Frish2021
 * Mint Configuration Parent Class.
 */
public abstract class BaseConfiguration {
    protected final CommentedFileConfig config;

    public BaseConfiguration(BaseConfiguration config) {
        this.config = config.config;
    }

    public BaseConfiguration(Path path) {
        this(CommentedFileConfig.builder(path).build());
        if (Files.exists(path)) this.config.load();
    }

    protected BaseConfiguration(CommentedFileConfig config) {
        this.config = config;
    }

    public abstract void loadOnlyClass(Class<?> clazz) throws Exception;
}
