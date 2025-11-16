package me.coderfrish.tralux.config.configs;

import me.coderfrish.tralux.config.BaseConfiguration;
import me.coderfrish.tralux.config.annotation.ConfigurationField;
import me.coderfrish.tralux.config.annotation.ConfigurationClass;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.StringJoiner;

public class CMTConfiguration extends BaseConfiguration {
    private ConfigurationClass currentConfigurationClass;

    public CMTConfiguration(Path path) {
        super(path);
    }

    @Override
    public void loadOnlyClass(Class<?> clazz) {
        this.currentConfigurationClass = clazz.getAnnotation(ConfigurationClass.class);

        String[] strings = {
                currentConfigurationClass.type().toString(),
                currentConfigurationClass.name()
        };
        String parentPath = String.join(".", strings);

        if (currentConfigurationClass.deprecated()) {
            return;
        }

        Arrays.stream(clazz.getDeclaredFields()).filter(
                field -> field.isAnnotationPresent(ConfigurationField.class)
        ).forEach(this::loadOnlyField);

        this.addConfigComment(parentPath, currentConfigurationClass.comments());
    }

    private void loadOnlyField(Field field) {
        ConfigurationField annotation0 = field.getAnnotation(ConfigurationField.class);
        String[] strings = {
                currentConfigurationClass.type().toString(),
                currentConfigurationClass.name(),
                annotation0.alisa().isBlank() ? field.getName() : annotation0.alisa()
        };
        String fullPath = String.join(".", strings);

        if (annotation0.deprecated() || field.isAnnotationPresent(Deprecated.class))
            if (!this.config.contains(fullPath))
                return;

        this.addConfigComment(fullPath, currentConfigurationClass.comments());
    }

    private void addConfigComment(String fullPath, String comment) {
        this.config.setComment(fullPath, comment);
    }

    private void addConfigComment(String fullPath, String[] comments) {
        if (comments.length > 0) {
            StringJoiner joiner = new StringJoiner("\n");
            for (String line : comments) {
                joiner.add(" " + line);
            }
            addConfigComment(fullPath, joiner.toString());
        }
    }
}
