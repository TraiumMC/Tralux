package me.coderfrish.tralux.configuration;

public abstract class TraluxConfiguration {
    public static final String TRALUX_CONFIG_FOLDER = "tralux_config";

    public abstract void load();

    public abstract void save();

    public abstract void set();
}
