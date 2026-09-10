package me.greed.bladeofgreed.command;

import me.greed.bladeofgreed.BladeOfGreedPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class GreedCommand implements CommandExecutor, TabCompleter {

    private final BladeOfGreedPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public GreedCommand(BladeOfGreedPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("bladeofgreed.use")) {
            sender.sendMessage(
                    miniMessage.deserialize(
                            plugin.getConfig().getString(
                                    "messages.no-permission",
                                    "<red>No permission.</red>"
                            )
                    )
            );
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(
                    miniMessage.deserialize(
                            "<gold>BladeOfGreed</gold> <gray>v"
                                    + plugin.getPluginMeta().getVersion()
                                    + "</gray>"
                    )
            );

            sender.sendMessage(
                    miniMessage.deserialize(
                            "<yellow>/greed reload</yellow> <gray>- reload configuration</gray>"
                    )
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();

            sender.sendMessage(
                    miniMessage.deserialize(
                            "<green>BladeOfGreed configuration reloaded.</green>"
                    )
            );

            return true;
        }

        sender.sendMessage(
                miniMessage.deserialize(
                        "<red>Unknown subcommand.</red>"
                )
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();

            suggestions.add("reload");

            return suggestions;
        }

        return List.of();
    }
}