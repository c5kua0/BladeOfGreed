package me.greed.bladeofgreed.command;

import me.greed.bladeofgreed.BladeOfGreedPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
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
                            "<red>You don't have permission to use /bg.</red>"
                    )
            );
            return true;
        }

        if (args.length == 0) {

            sender.sendMessage(
                    miniMessage.deserialize(
                            "<gold><bold>BladeOfGreed</bold></gold>"
                    )
            );

            sender.sendMessage(
                    miniMessage.deserialize(
                            "<yellow>/bg reload</yellow> <gray>- Reload configuration</gray>"
                    )
            );

            sender.sendMessage(
                    miniMessage.deserialize(
                            "<yellow>Slot 5</yellow> <gray>- Golden Claim</gray>"
                    )
            );

            sender.sendMessage(
                    miniMessage.deserialize(
                            "<yellow>Slot 6</yellow> <gray>- King of Greed</gray>"
                    )
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            plugin.reloadConfig();

            sender.sendMessage(
                    miniMessage.deserialize(
                            plugin.getConfig().getString(
                                    "messages.reloaded",
                                    "<green>Configuration reloaded.</green>"
                            )
                    )
            );

            return true;
        }

        sender.sendMessage(
                miniMessage.deserialize(
                        "<red>Unknown command.</red> <gray>Use /bg</gray>"
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
            return List.of("reload");
        }

        return Collections.emptyList();
    }
}