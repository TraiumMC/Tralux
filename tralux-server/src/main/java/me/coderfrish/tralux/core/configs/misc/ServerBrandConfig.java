package me.coderfrish.tralux.core.configs.misc;

import me.coderfrish.tralux.config.ConfigurationType;
import me.coderfrish.tralux.config.annotation.ConfigurationClass;
import me.coderfrish.tralux.config.annotation.ConfigurationField;

@ConfigurationClass(type = ConfigurationType.misc, name = "server_brand")
public class ServerBrandConfig {
    @ConfigurationField
    public static String value = "Tralux";
}
