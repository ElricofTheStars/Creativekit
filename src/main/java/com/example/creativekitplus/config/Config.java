package com.example.creativekitplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Central configuration. Serialized to  config/creativekitplus.json.
 * Add fields here and they persist automatically.
 */
public final class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("creativekitplus.json");

    private static Config INSTANCE = new Config();

    // ---- Module toggles (persisted default states) ----
    public boolean flightEnabled = false;
    public boolean xrayEnabled = false;
    public boolean highlightEnabled = false;
    public boolean autoToolEnabled = false;
    public boolean noClipEnabled = false;
    public boolean speedEnabled = false;
    public boolean fullBrightEnabled = false;

    // ---- Tunables ----
    public double flightSpeed = 1.0;          // blocks per tick baseline multiplier
    public double speedMultiplier = 1.5;      // horizontal movement multiplier
    public double xrayRange = 32.0;           // used only for chunk re-render radius hint

    /** Ore / interesting blocks kept visible in X-ray. Everything else is hidden. */
    public Set<String> xrayVisibleBlocks = defaultXrayBlocks();

    public static Config get() {
        return INSTANCE;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                String json = Files.readString(FILE);
                Config loaded = GSON.fromJson(json, Config.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                    if (INSTANCE.xrayVisibleBlocks == null || INSTANCE.xrayVisibleBlocks.isEmpty()) {
                        INSTANCE.xrayVisibleBlocks = defaultXrayBlocks();
                    }
                }
            } else {
                save();
            }
        } catch (Exception e) {
            System.err.println("[CreativeKitPlus] Failed to load config, using defaults: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            System.err.println("[CreativeKitPlus] Failed to save config: " + e.getMessage());
        }
    }

    private static Set<String> defaultXrayBlocks() {
        Set<String> s = new HashSet<>();
        s.add("minecraft:coal_ore");
        s.add("minecraft:deepslate_coal_ore");
        s.add("minecraft:iron_ore");
        s.add("minecraft:deepslate_iron_ore");
        s.add("minecraft:copper_ore");
        s.add("minecraft:deepslate_copper_ore");
        s.add("minecraft:gold_ore");
        s.add("minecraft:deepslate_gold_ore");
        s.add("minecraft:redstone_ore");
        s.add("minecraft:deepslate_redstone_ore");
        s.add("minecraft:lapis_ore");
        s.add("minecraft:deepslate_lapis_ore");
        s.add("minecraft:diamond_ore");
        s.add("minecraft:deepslate_diamond_ore");
        s.add("minecraft:emerald_ore");
        s.add("minecraft:deepslate_emerald_ore");
        s.add("minecraft:nether_gold_ore");
        s.add("minecraft:nether_quartz_ore");
        s.add("minecraft:ancient_debris");
        return s;
    }
}
