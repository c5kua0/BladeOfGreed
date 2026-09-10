package me.greed.bladeofgreed.listener;

import me.greed.bladeofgreed.BladeOfGreedPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GreedListener implements Listener {

    private final BladeOfGreedPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final Map<UUID, Long> skillCooldowns = new HashMap<>();
    private final Map<UUID, Long> awakenCooldowns = new HashMap<>();

    public GreedListener(BladeOfGreedPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        if (!plugin.getConfig().getBoolean("settings.enabled", true)) {
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();

        // Minecraft hotbar:
        // 0 = slot 1
        // 4 = slot 5
        // 5 = slot 6

        if (slot == 4) {
            activateGoldenClaim(player);
        }

        if (slot == 5) {
            activateAwaken(player);
        }
    }

    private void activateGoldenClaim(Player player) {

        UUID uuid = player.getUniqueId();

        long cooldown = plugin.getConfig()
                .getLong("skill.cooldown-seconds", 15) * 1000L;

        long now = System.currentTimeMillis();

        if (skillCooldowns.containsKey(uuid)
                && now - skillCooldowns.get(uuid) < cooldown) {

            player.sendMessage(
                    miniMessage.deserialize(
                            plugin.getConfig().getString(
                                    "messages.cooldown",
                                    "<red>Ability is on cooldown.</red>"
                            )
                    )
            );

            return;
        }

        skillCooldowns.put(uuid, now);

        int duration = plugin.getConfig()
                .getInt("skill.duration-seconds", 5);

        int radius = plugin.getConfig()
                .getInt("skill.radius", 6);

        player.sendMessage(
                miniMessage.deserialize(
                        "<gold><bold>Golden Claim activated!</bold></gold>"
                )
        );

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                1.0f,
                1.2f
        );

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                Location location = player.getLocation().clone();
                location.add(0, 1, 0);

                for (int i = 0; i < 12; i++) {

                    double angle =
                            (Math.PI * 2 * i) / 12;

                    double x =
                            Math.cos(angle) * radius;

                    double z =
                            Math.sin(angle) * radius;

                    Location particleLocation =
                            location.clone().add(x, 0, z);

                    player.getWorld().spawnParticle(
                            Particle.GLOW,
                            particleLocation,
                            1,
                            0,
                            0,
                            0,
                            0
                    );
                }

                player.getWorld().spawnParticle(
                        Particle.END_ROD,
                        location,
                        3,
                        0.25,
                        0.35,
                        0.25,
                        0.01
                );

                ticks += 5;

                if (ticks >= duration * 20) {
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 0L, 5L);
    }

    private void activateAwaken(Player player) {

        UUID uuid = player.getUniqueId();

        long cooldown = plugin.getConfig()
                .getLong("awaken.cooldown-seconds", 60) * 1000L;

        long now = System.currentTimeMillis();

        if (awakenCooldowns.containsKey(uuid)
                && now - awakenCooldowns.get(uuid) < cooldown) {

            player.sendMessage(
                    miniMessage.deserialize(
                            plugin.getConfig().getString(
                                    "messages.cooldown",
                                    "<red>Ability is on cooldown.</red>"
                            )
                    )
            );

            return;
        }

        awakenCooldowns.put(uuid, now);

        int duration = plugin.getConfig()
                .getInt("awaken.duration-seconds", 30);

        String message = plugin.getConfig()
                .getString(
                        "messages.global-awaken.message",
                        "<gold><bold>✦ KING OF GREED ✦</bold></gold> <yellow>%player% has awakened!</yellow>"
                );

        message = message.replace(
                "%player%",
                player.getName()
        );

        if (plugin.getConfig()
                .getBoolean("messages.global-awaken.enabled", true)) {

            plugin.getServer()
                    .broadcast(
                            miniMessage.deserialize(message)
                    );
        }

        player.playSound(
                player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                1.0f,
                0.8f
        );

        player.sendMessage(
                miniMessage.deserialize(
                        plugin.getConfig().getString(
                                "messages.awakened",
                                "<gold><bold>King of Greed has awakened!</bold></gold>"
                        )
                )
        );

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                Location location =
                        player.getLocation().clone();

                location.add(0, 1, 0);

                double radius = 2.0;

                for (int i = 0; i < 20; i++) {

                    double angle =
                            (Math.PI * 2 * i) / 20;

                    double x =
                            Math.cos(angle) * radius;

                    double z =
                            Math.sin(angle) * radius;

                    Location particleLocation =
                            location.clone().add(x, 0, z);

                    player.getWorld().spawnParticle(
                            Particle.GLOW,
                            particleLocation,
                            1,
                            0,
                            0,
                            0,
                            0
                    );
                }

                player.getWorld().spawnParticle(
                        Particle.END_ROD,
                        location,
                        5,
                        0.4,
                        0.7,
                        0.4,
                        0.02
                );

                ticks += 5;

                if (ticks >= duration * 20) {
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 0L, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();

        skillCooldowns.remove(uuid);
        awakenCooldowns.remove(uuid);
    }
}