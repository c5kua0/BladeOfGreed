package me.greed.bladeofgreed.listener;

import me.greed.bladeofgreed.BladeOfGreedPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class GreedListener implements Listener {

    private final BladeOfGreedPlugin plugin;

    public GreedListener(BladeOfGreedPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (!plugin.getConfig().getBoolean("settings.enabled", true)) {
            return;
        }

        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info(
                    event.getPlayer().getName() + " joined the server."
            );
        }
    }
}