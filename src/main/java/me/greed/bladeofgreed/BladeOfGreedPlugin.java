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

        GreedCommand greedCommand = new GreedCommand(this);

        if (getCommand("greed") != null) {
            getCommand("greed").setExecutor(greedCommand);
            getCommand("greed").setTabCompleter(greedCommand);
        }

        getServer().getPluginManager()
                .registerEvents(new GreedListener(this), this);

        getLogger().info("BladeOfGreed has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BladeOfGreed has been disabled.");
    }

    public static BladeOfGreedPlugin getInstance() {
        return instance;
    }
}