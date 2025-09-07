package dev.lrxh.neptune.configs.impl.handler;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import java.util.*;
import java.util.stream.Collectors;

public interface IDataAccessor {

    Neptune plugin = Neptune.get();

    /**
     * Retrieves a string value from the config at the current accessor's path.
     *
     * @return the string value
     */
    default String getString() {
        return getConfigFile().getConfiguration().getString(getPath());
    }

    /**
     * Retrieves a list of strings from the config at the current accessor's path.
     *
     * @return the list of strings
     */
    default List<String> getStringList() {
        return getConfigFile().getConfiguration().getStringList(getPath());
    }

    /**
     * Retrieves an integer value from the config at the current accessor's path.
     *
     * @return the integer value
     */
    default int getInt() {
        return getConfigFile().getConfiguration().getInt(getPath());
    }

    /**
     * Retrieves a boolean value from the config at the current accessor's path.
     *
     * @return the boolean value
     */
    default boolean getBoolean() {
        return getConfigFile().getConfiguration().getBoolean(getPath());
    }

    /**
     * Determines whether unknown config entries should be removed during load.
     *
     * @return true if unknown entries should be cleaned up
     */
    default boolean resetUnknown() {
        return true;
    }

    String getHeader();

    String getPath();

    String getComment();

    List<String> getDefaultValue();

    DataType getDataType();

    ConfigFile getConfigFile();

    default void applyHeader() {
        String header = getHeader();
        if (!header.isEmpty()) {
            getConfigFile().getConfiguration().options().setHeader(List.of(header));
        }
    }

    /**
     * Sets a configuration value at the specified path according to the provided {@link DataType}.
     *
     * @param path the configuration path
     * @param rawValue the raw value(s) to set
     * @param type the type of the data
     */
    default void setValue(String path, List<String> rawValue, DataType type) {
        if (rawValue == null || rawValue.isEmpty()) return;

        switch (type) {
            case STRING_LIST -> getConfigFile().getConfiguration().set(path, rawValue);
            case STRING -> getConfigFile().getConfiguration().set(path, rawValue.get(0));
            case INT -> getConfigFile().getConfiguration().set(path, Integer.parseInt(rawValue.get(0)));
            case BOOLEAN -> getConfigFile().getConfiguration().set(path, Boolean.parseBoolean(rawValue.get(0)));
        }
    }

    /**
     * Sets the configuration value for this accessor and immediately saves the file.
     *
     * @param value the value to set
     */
    default void set(Object value) {
        getConfigFile().getConfiguration().set(getPath(), value);
        getConfigFile().save();
    }

    /**
     * Adds an inline comment to the given path and saves the configuration.
     *
     * @param path the configuration path
     * @param comment the comment to add
     */
    default void comment(String path, String comment) {
        if (comment != null && !comment.isBlank()) {
            getConfigFile().getConfiguration()
                    .setInlineComments(path, Collections.singletonList(comment));
            getConfigFile().save();
        }
    }

    /**
     * Loads the configuration values, applies defaults, headers, comments,
     * and optionally removes unknown entries.
     */
    default void load() {
        applyHeader();

        ConfigFile configFile = getConfigFile();
        if (configFile == null) return;

        ConfigurationSection root = configFile.getConfiguration();
        var accessors = List.of(this.getClass().getEnumConstants());

        for (var accessor : accessors) {
            if (root.get(accessor.getPath()) == null) {
                setValue(accessor.getPath(), accessor.getDefaultValue(), accessor.getDataType());
                comment(accessor.getPath(), accessor.getComment());
            }
        }

        if (resetUnknown()) {
            Set<String> validPaths = accessors.stream()
                    .map(IDataAccessor::getPath)
                    .collect(Collectors.toSet());
            cleanupSection(root, "", validPaths);
        }

        configFile.save();
    }

    /**
     * Recursively removes invalid or unknown paths from the configuration section.
     *
     * @param section the section to clean
     * @param parentPath the current parent path
     * @param validPaths set of valid paths to preserve
     */
    private void cleanupSection(ConfigurationSection section, String parentPath, Set<String> validPaths) {
        for (String key : section.getKeys(false)) {
            String fullPath = parentPath.isEmpty() ? key : parentPath + "." + key;

            if (section.isConfigurationSection(key)) {
                ConfigurationSection nested = section.getConfigurationSection(key);
                cleanupSection(nested, fullPath, validPaths);

                boolean hasValidChild = validPaths.stream().anyMatch(p -> p.startsWith(fullPath + "."));
                if (nested.getKeys(false).isEmpty() && !hasValidChild) {
                    section.set(key, null);
                }
            } else if (!validPaths.contains(fullPath)) {
                section.set(key, null);
            }
        }
    }
}