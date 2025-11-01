package dev.smartshub.shpets.api.config;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper for list values to be accessible as ConfigurationSection
 * This allows MessageAbility and CommandAbility to read lists from parent
 */
public class WrapperConfigurationSection implements ConfigurationSection {
    private final ConfigurationSection parent;
    private final String key;

    public WrapperConfigurationSection(ConfigurationSection parent, String key) {
        this.parent = parent;
        this.key = key;
    }

    @Override
    @NotNull
    public String getName() {
        return key;
    }

    @Override
    @Nullable
    public ConfigurationSection getParent() {
        return parent;
    }

    @Override
    @NotNull
    public Set<String> getKeys(boolean deep) {
        return Set.of();
    }

    @Override
    @NotNull
    public Map<String, Object> getValues(boolean deep) {
        return Map.of();
    }

    @Override
    public boolean contains(@NotNull String path) {
        return parent.contains(key + (path.isEmpty() ? "" : "." + path));
    }

    @Override
    public boolean contains(@NotNull String path, boolean ignoreDefault) {
        return parent.contains(key + (path.isEmpty() ? "" : "." + path), ignoreDefault);
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return parent.isSet(key + (path.isEmpty() ? "" : "." + path));
    }

    @Override
    @Nullable
    public String getCurrentPath() {
        return parent.getCurrentPath() + "." + key;
    }

    @Override
    @Nullable
    public ConfigurationSection getDefaultSection() {
        return null;
    }

    @Override
    public void addDefault(@NotNull String path, @Nullable Object value) {
        parent.addDefault(key + "." + path, value);
    }

    @Override
    @Nullable
    public ConfigurationSection getConfigurationSection(@NotNull String path) {
        return parent.getConfigurationSection(key + "." + path);
    }

    @Override
    public boolean isConfigurationSection(@NotNull String path) {
        return parent.isConfigurationSection(key + "." + path);
    }

    @Override
    @Nullable
    public ConfigurationSection createSection(@NotNull String path) {
        return parent.createSection(key + "." + path);
    }

    @Override
    @NotNull
    public ConfigurationSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return parent.createSection(key + "." + path, map);
    }

    // Delegación simple para el resto de métodos
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        parent.set(key + "." + path, value);
    }

    @Override
    @Nullable
    public Object get(@NotNull String path) {
        return parent.get(key + (path.isEmpty() ? "" : "." + path));
    }

    @Override
    @Nullable
    public Object get(@NotNull String path, @Nullable Object def) {
        return parent.get(key + (path.isEmpty() ? "" : "." + path), def);
    }

    @Override
    @Nullable
    public String getString(@NotNull String path) {
        return parent.getString(key + "." + path);
    }

    @Override
    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        return parent.getString(key + "." + path, def);
    }

    @Override
    public boolean isString(@NotNull String path) {
        return parent.isString(key + "." + path);
    }

    @Override
    public int getInt(@NotNull String path) {
        return parent.getInt(key + "." + path);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return parent.getInt(key + "." + path, def);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        return parent.isInt(key + "." + path);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return parent.getBoolean(key + "." + path);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return parent.getBoolean(key + "." + path, def);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        return parent.isBoolean(key + "." + path);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return parent.getDouble(key + "." + path);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return parent.getDouble(key + "." + path, def);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        return parent.isDouble(key + "." + path);
    }

    @Override
    public long getLong(@NotNull String path) {
        return parent.getLong(key + "." + path);
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return parent.getLong(key + "." + path, def);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        return parent.isLong(key + "." + path);
    }

    @Override
    @Nullable
    public List<?> getList(@NotNull String path) {
        return parent.getList(key + "." + path);
    }

    @Override
    @Nullable
    public List<?> getList(@NotNull String path, @Nullable List<?> def) {
        return parent.getList(key + "." + path, def);
    }

    @Override
    public boolean isList(@NotNull String path) {
        return parent.isList(key + "." + path);
    }

    @Override
    @NotNull
    public List<String> getStringList(@NotNull String path) {
        return parent.getStringList(key + (path.isEmpty() ? "" : "." + path));
    }

    @Override
    @NotNull
    public List<Integer> getIntegerList(@NotNull String path) {
        return parent.getIntegerList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Boolean> getBooleanList(@NotNull String path) {
        return parent.getBooleanList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Double> getDoubleList(@NotNull String path) {
        return parent.getDoubleList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Float> getFloatList(@NotNull String path) {
        return parent.getFloatList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Long> getLongList(@NotNull String path) {
        return parent.getLongList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Byte> getByteList(@NotNull String path) {
        return parent.getByteList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Character> getCharacterList(@NotNull String path) {
        return parent.getCharacterList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Short> getShortList(@NotNull String path) {
        return parent.getShortList(key + "." + path);
    }

    @Override
    @NotNull
    public List<Map<?, ?>> getMapList(@NotNull String path) {
        return parent.getMapList(key + "." + path);
    }

    @Override
    @Nullable
    public <T> T getObject(@NotNull String path, @NotNull Class<T> clazz) {
        return parent.getObject(key + "." + path, clazz);
    }

    @Override
    @Nullable
    public <T> T getObject(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return parent.getObject(key + "." + path, clazz, def);
    }

    @Override
    @Nullable
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        return parent.getSerializable(key + "." + path, clazz);
    }

    @Override
    @Nullable
    public <T extends ConfigurationSerializable> T getSerializable(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return parent.getSerializable(key + "." + path, clazz, def);
    }

    @Override
    @Nullable
    public Vector getVector(@NotNull String path) {
        return parent.getVector(key + "." + path);
    }

    @Override
    @Nullable
    public Vector getVector(@NotNull String path, @Nullable Vector def) {
        return parent.getVector(key + "." + path, def);
    }

    @Override
    public boolean isVector(@NotNull String path) {
        return parent.isVector(key + "." + path);
    }

    @Override
    @Nullable
    public OfflinePlayer getOfflinePlayer(@NotNull String path) {
        return parent.getOfflinePlayer(key + "." + path);
    }

    @Override
    @Nullable
    public OfflinePlayer getOfflinePlayer(@NotNull String path, @Nullable OfflinePlayer def) {
        return parent.getOfflinePlayer(key + "." + path, def);
    }

    @Override
    public boolean isOfflinePlayer(@NotNull String path) {
        return parent.isOfflinePlayer(key + "." + path);
    }

    @Override
    @Nullable
    public ItemStack getItemStack(@NotNull String path) {
        return parent.getItemStack(key + "." + path);
    }

    @Override
    @Nullable
    public ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        return parent.getItemStack(key + "." + path, def);
    }

    @Override
    public boolean isItemStack(@NotNull String path) {
        return parent.isItemStack(key + "." + path);
    }

    @Override
    @Nullable
    public Color getColor(@NotNull String path) {
        return parent.getColor(key + "." + path);
    }

    @Override
    @Nullable
    public Color getColor(@NotNull String path, @Nullable Color def) {
        return parent.getColor(key + "." + path, def);
    }

    @Override
    public boolean isColor(@NotNull String path) {
        return parent.isColor(key + "." + path);
    }

    @Override
    @Nullable
    public Location getLocation(@NotNull String path) {
        return parent.getLocation(key + "." + path);
    }

    @Override
    @Nullable
    public Location getLocation(@NotNull String path, @Nullable Location def) {
        return parent.getLocation(key + "." + path, def);
    }

    @Override
    public boolean isLocation(@NotNull String path) {
        return parent.isLocation(key + "." + path);
    }

    @Override
    @Nullable
    public Configuration getRoot() {
        return parent.getRoot();
    }

    @Override
    @NotNull
    public List<String> getComments(@NotNull String path) {
        return parent.getComments(key + "." + path);
    }

    @Override
    @NotNull
    public List<String> getInlineComments(@NotNull String path) {
        return parent.getInlineComments(key + "." + path);
    }

    @Override
    public void setComments(@NotNull String path, @Nullable List<String> comments) {
        parent.setComments(key + "." + path, comments);
    }

    @Override
    public void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        parent.setInlineComments(key + "." + path, comments);
    }
}