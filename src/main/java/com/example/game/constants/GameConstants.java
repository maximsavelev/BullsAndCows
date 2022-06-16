package com.example.game.constants;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public final class GameConstants {
    private final static String GAME_PROPERTIES = "game-settings.properties";

    private static final Properties props;

    static {
        try {
            props = PropertiesLoaderUtils.loadAllProperties(GAME_PROPERTIES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static final String SYMBOLS = props.getProperty("symbols");

    public static final int COUNT_OF_SYMBOLS = Integer.parseInt(props.getProperty("count_of_symbols"));
}
