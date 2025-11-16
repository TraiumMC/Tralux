package me.coderfrish.tralux.config.configs;

import me.coderfrish.tralux.config.BaseConfiguration;
import me.coderfrish.tralux.config.annotation.ConfigurationField;
import me.coderfrish.tralux.config.annotation.ConfigurationClass;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ValueConfiguration extends BaseConfiguration {
    private ConfigurationClass currentConfigurationClass;

    public ValueConfiguration(BaseConfiguration config) {
        super(config);
    }

    @Override
    public void loadOnlyClass(Class<?> clazz) {
        this.currentConfigurationClass = clazz.getAnnotation(ConfigurationClass.class);

        if (!this.currentConfigurationClass.deprecated()) {
            this.loadOnlyNormalClass(clazz);
            return;
        }

        this.loadOnlyDeprecatedClass(clazz);
    }

    private void loadOnlyNormalClass(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredFields()).filter(
                field -> field.isAnnotationPresent(ConfigurationField.class)
        ).forEach(this::loadOnlyNormalField);
    }

    private void loadOnlyNormalField(Field field) {
        try {
            ConfigurationField annotation = field.getAnnotation(ConfigurationField.class);
            String[] strings = {
                    this.currentConfigurationClass.type().name(),
                    this.currentConfigurationClass.name(),
                    annotation.alisa().isBlank() ? field.getName() : annotation.alisa()
            };
            String fullPath = String.join(".", strings);

            if (annotation.deprecated() || field.isAnnotationPresent(Deprecated.class)) {
                if (!this.config.contains(fullPath))
                    return;

                field.set(null, this.config.get(fullPath));
            }

            if (this.config.contains(fullPath)) {
                field.set(null, this.config.get(fullPath));
                return;
            }

            this.config.add(fullPath, field.get(null));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadOnlyDeprecatedClass(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredFields()).filter(
                field -> field.isAnnotationPresent(ConfigurationField.class)
        ).forEach(this::loadOnlyDeprecatedClass);
    }

    private void loadOnlyDeprecatedClass(Field field) {
        try {
            ConfigurationField annotation = field.getAnnotation(ConfigurationField.class);
            String[] strings = {
                    this.currentConfigurationClass.type().name(),
                    this.currentConfigurationClass.name(),
                    annotation.alisa().isBlank() ? field.getName() : annotation.alisa()
            };
            String fullPath = String.join(".", strings);

            if (!this.config.contains(fullPath)) {
                return;
            }

            field.set(null, this.config.get(fullPath));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
