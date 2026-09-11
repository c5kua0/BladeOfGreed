package me.greed.bladeofgreed;

import me.greed.bladeofgreed.command.GreedCommand;
import me.greed.bladeofgreed.listener.GreedListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BladeOfGreedPlugin extends JavaPlugin {

    private static BladeOfGreedPlugin instance;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        GreedCommand command = new GreedCommand(this);

        if (getCommand("bg") != null) {
            getCommand("bg").setExecutor(command);
            getCommand("bg").setTabCompleter(command);
        }

        getServer().getPluginManager().registerEvents(
                new GreedListener(this),
                this
        );

        getLogger().info("=================================");
        getLogger().info("        BladeOfGreed");
        getLogger().info("        Version 1.0.0");
        getLogger().info("=================================");
        getLogger().info("Plugin enabled successfully.");
        getLogger().info(
                "Greed Owner: " +
                getConfig().getString("owner.name", "TUKOSHIBU")
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("BladeOfGreed disabled.");
    }

    public static BladeOfGreedPlugin getInstance() {
        return instance;
    }
}