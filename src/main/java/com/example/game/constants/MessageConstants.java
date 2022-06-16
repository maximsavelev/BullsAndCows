package com.example.game.constants;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public final class MessageConstants {
    private final static String MESSAGE_PROPERTIES = "message.properties";
    private static final Properties props;

    static {
        try {
            props = PropertiesLoaderUtils.loadAllProperties(MESSAGE_PROPERTIES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String WIN_MESSAGE = props.getProperty("win_message");

    public static final String NO_TIME_MESSAGE = props.getProperty("no_time_message");

    public static final String NO_ATTEMPTS_MESSAGE = props.getProperty("no_attempts_message");

    public static final String TIME_NOT_CALCULATED_MESSAGE = props.getProperty("time_not_calculated_message");
    public static final String TIME_IN_SECONDS_MESSAGE = props.getProperty("time_in_seconds_message");
    public static final String TIME_IN_MINUTES_AND_SECONDS_MESSAGE = props.getProperty("time_in_minutes_and_seconds_message");

    public static final String GAME_STATUS_MESSAGE = props.getProperty("game_answer_message");

}
