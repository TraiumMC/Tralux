package me.coderfrish.tralux.config.configs;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.coderfrish.tralux.config.BaseConfiguration;

import java.lang.reflect.Method;

public class LoadConfiguration extends BaseConfiguration {
    public LoadConfiguration(BaseConfiguration config) {
        super(config);
    }

    @Override
    public void loadOnlyClass(Class<?> clazz) throws Exception {
        try {
            Method loaded = clazz.getDeclaredMethod("loaded", CommentedFileConfig.class);
            loaded.invoke(null, this.config);
        } catch (NoSuchMethodException ignore) {
        }
    }
}
