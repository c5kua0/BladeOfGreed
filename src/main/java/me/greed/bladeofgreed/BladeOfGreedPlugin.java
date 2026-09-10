package me.greed.bladeofgreed;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BladeOfGreedPlugin extends JavaPlugin implements Listener {

    private static final String OWNER = "TUKOSHIBU";

    private NamespacedKey greedRelicKey;

    private final Map<UUID, Long> authorityCooldown = new HashMap<>();
    private final Map<UUID, Long> claimCooldown = new HashMap<>();
    private final Map<UUID, Long> ultimateCooldown = new HashMap<>();

    private final Map<UUID, Boolean> greedEnabled = new HashMap<>();

    /*
     * Greed Gold Dust
     * RGB: 255, 190, 0
     * Size: 1.5
     */
    private final Particle.DustOptions goldDust =
            new Particle.DustOptions(Color.fromRGB(255, 190, 0), 1.5f);

    @Override
    public void onEnable() {

        greedRelicKey = new NamespacedKey(this, "greed_relic");

        getServer().getPluginManager().registerEvents(this, this);

        saveDefaultConfig();

        getLogger().info("BladeOfGreed enabled.");
        getLogger().info("Owner: " + OWNER);
    }

    @Override
    public void onDisable() {
        authorityCooldown.clear();
        claimCooldown.clear();
        ultimateCooldown.clear();
        greedEnabled.clear();

        getLogger().info("BladeOfGreed disabled.");
    }

    // =========================================================
    // /bg COMMAND
    // =========================================================

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!command.getName().equalsIgnoreCase("bg")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.getName().equalsIgnoreCase(OWNER)) {
            player.sendMessage(
                    ChatColor.RED + "This relic belongs to " +
                    ChatColor.GOLD + OWNER + ChatColor.RED + "."
            );
            return true;
        }

        ItemStack relic = createGreedRelic();

        player.getInventory().addItem(relic);

        player.sendMessage(
                ChatColor.GOLD + "✦ Greed Relic received!"
        );

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP,
                1.0f,
                1.0f
        );

        return true;
    }

    // =========================================================
    // CREATE GREED RELIC
    // =========================================================

    private ItemStack createGreedRelic() {

        ItemStack item = new ItemStack(Material.NETHER_STAR);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                ChatColor.GOLD + "" + ChatColor.BOLD + "Greed Relic"
        );

        meta.setUnbreakable(true);

        meta.setLore(java.util.Arrays.asList(

                ChatColor.DARK_GRAY + "Sin Archbishop of Greed",

                "",

                ChatColor.YELLOW + "Hotbar Skills:",

                ChatColor.GRAY + "Slot 5 " +
                        ChatColor.WHITE + "→ " +
                        ChatColor.GOLD + "Greed",

                ChatColor.GRAY + "Slot 6 " +
                        ChatColor.WHITE + "→ " +
                        ChatColor.GOLD + "Authority of Greed",

                ChatColor.GRAY + "Slot 7 " +
                        ChatColor.WHITE + "→ " +
                        ChatColor.GOLD + "Greedy Claim",

                ChatColor.GRAY + "Slot 8 " +
                        ChatColor.WHITE + "→ " +
                        ChatColor.GOLD + "Everything Is Mine",

                ChatColor.GRAY + "Slot 9 " +
                        ChatColor.WHITE + "→ " +
                        ChatColor.RED + "Turn Off Skills",

                "",

                ChatColor.DARK_GRAY + "Right-click to activate skills."
        ));

        meta.getPersistentDataContainer().set(
                greedRelicKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        item.setItemMeta(meta);

        return item;
    }

    // =========================================================
    // CHECK GREED RELIC
    // =========================================================

    private boolean isGreedRelic(ItemStack item) {

        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        Byte value = meta.getPersistentDataContainer().get(
                greedRelicKey,
                PersistentDataType.BYTE
        );

        return value != null && value == (byte) 1;
    }

    // =========================================================
    // HOTBAR SELECTION
    // =========================================================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHotbarChange(PlayerItemHeldEvent event) {

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (!isGreedRelic(item)) {
            return;
        }

        int slot = event.getNewSlot();

        /*
         * Minecraft slots:
         *
         * 0 = Slot 1
         * 1 = Slot 2
         * 2 = Slot 3
         * 3 = Slot 4
         * 4 = Slot 5
         * 5 = Slot 6
         * 6 = Slot 7
         * 7 = Slot 8
         * 8 = Slot 9
         */

        switch (slot) {

            case 4 -> {
                player.sendActionBar(
                        ChatColor.GOLD + "Greed"
                );

                playSelectionEffect(player);
            }

            case 5 -> {
                player.sendActionBar(
                        ChatColor.GOLD + "Authority of Greed " +
                        ChatColor.GRAY + "→ Right-click"
                );

                playSelectionEffect(player);
            }

            case 6 -> {
                player.sendActionBar(
                        ChatColor.GOLD + "Greedy Claim " +
                        ChatColor.GRAY + "→ Right-click"
                );

                playSelectionEffect(player);
            }

            case 7 -> {
                player.sendActionBar(
                        ChatColor.GOLD + "Everything Is Mine " +
                        ChatColor.GRAY + "→ Right-click"
                );

                playSelectionEffect(player);
            }

            case 8 -> {

                greedEnabled.put(player.getUniqueId(), false);

                player.sendActionBar(
                        ChatColor.RED + "Greed Skills Disabled"
                );

                playGoldEffect(player);

                player.playSound(
                        player.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_BASS,
                        1.0f,
                        0.7f
                );
            }

            default -> {
                // Slots 1-4 do nothing.
            }
        }
    }

    // =========================================================
    // RIGHT CLICK SKILLS
    // =========================================================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRightClick(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!event.getAction().isRightClick()) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isGreedRelic(item)) {
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();

        switch (slot) {

            // Slot 5
            case 4 -> toggleGreed(player);

            // Slot 6
            case 5 -> authorityOfGreed(player);

            // Slot 7
            case 6 -> greedyClaim(player);

            // Slot 8
            case 7 -> everythingIsMine(player);

            // Slot 9
            case 8 -> {

                greedEnabled.put(player.getUniqueId(), false);

                player.sendMessage(
                        ChatColor.RED +
                        "Greed skills disabled."
                );
            }

            default -> {
            }
        }
    }

    // =========================================================
    // SKILL 1 - GREED
    // =========================================================

    private void toggleGreed(Player player) {

        UUID uuid = player.getUniqueId();

        boolean enabled =
                greedEnabled.getOrDefault(uuid, false);

        enabled = !enabled;

        greedEnabled.put(uuid, enabled);

        if (enabled) {

            player.sendMessage(
                    ChatColor.GOLD + "✦ GREED ACTIVATED"
            );

            player.sendActionBar(
                    ChatColor.GOLD + "GREED: " +
                    ChatColor.GREEN + "ON"
            );

            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_BEACON_ACTIVATE,
                    1.0f,
                    1.2f
            );

            playGoldBurst(player);

        } else {

            player.sendMessage(
                    ChatColor.RED + "✦ GREED DEACTIVATED"
            );

            player.sendActionBar(
                    ChatColor.GOLD + "GREED: " +
                    ChatColor.RED + "OFF"
            );

            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_BEACON_DEACTIVATE,
                    1.0f,
                    0.8f
            );
        }
    }

    // =========================================================
    // SKILL 2 - AUTHORITY OF GREED
    // =========================================================

    private void authorityOfGreed(Player player) {

        UUID uuid = player.getUniqueId();

        int cooldown =
                getConfig().getInt("cooldowns.authority", 25);

        if (isOnCooldown(authorityCooldown, uuid)) {

            sendCooldownMessage(
                    player,
                    authorityCooldown,
                    uuid
            );

            return;
        }

        authorityCooldown.put(
                uuid,
                System.currentTimeMillis() + (cooldown * 1000L)
        );

        int radius =
                getConfig().getInt("skills.authority.radius", 6);

        player.sendMessage(
                ChatColor.GOLD +
                "✦ Authority of Greed activated!"
        );

        player.sendActionBar(
                ChatColor.GOLD +
                "AUTHORITY OF GREED"
        );

        playGoldBurst(player);

        playGoldRing(
                player.getLocation(),
                radius
        );

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_BEACON_POWER_SELECT,
                1.0f,
                1.0f
        );
    }

    // =========================================================
    // SKILL 3 - GREEDY CLAIM
    // =========================================================

    private void greedyClaim(Player player) {

        UUID uuid = player.getUniqueId();

        int cooldown =
                getConfig().getInt("cooldowns.claim", 20);

        if (isOnCooldown(claimCooldown, uuid)) {

            sendCooldownMessage(
                    player,
                    claimCooldown,
                    uuid
            );

            return;
        }

        claimCooldown.put(
                uuid,
                System.currentTimeMillis() + (cooldown * 1000L)
        );

        int radius =
                getConfig().getInt("skills.claim.radius", 8);

        player.sendMessage(
                ChatColor.GOLD +
                "✦ Greedy Claim activated!"
        );

        player.sendActionBar(
                ChatColor.GOLD +
                "GREEDY CLAIM"
        );

        playGoldBurst(player);

        playGoldRing(
                player.getLocation(),
                radius
        );

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP,
                1.0f,
                1.5f
        );
    }

    // =========================================================
    // ULTIMATE - EVERYTHING IS MINE
    // =========================================================

    private void everythingIsMine(Player player) {

        UUID uuid = player.getUniqueId();

        int cooldown =
                getConfig().getInt("cooldowns.ultimate", 60);

        if (isOnCooldown(ultimateCooldown, uuid)) {

            sendCooldownMessage(
                    player,
                    ultimateCooldown,
                    uuid
            );

            return;
        }

        ultimateCooldown.put(
                uuid,
                System.currentTimeMillis() + (cooldown * 1000L)
        );

        int duration =
                getConfig().getInt(
                        "skills.ultimate.duration",
                        8
                );

        /*
         * NO CHARGE REQUIREMENT.
         * The ultimate only uses its cooldown.
         */

        player.sendMessage(
                ChatColor.GOLD + "" +
                ChatColor.BOLD +
                "✦ EVERYTHING IS MINE!"
        );

        player.sendActionBar(
                ChatColor.GOLD + "" +
                ChatColor.BOLD +
                "EVERYTHING IS MINE"
        );

        getServer().broadcastMessage(
                ChatColor.GOLD +
                "[GREED] " +
                ChatColor.YELLOW +
                player.getName() +
                ChatColor.GOLD +
                " has unleashed " +
                ChatColor.YELLOW +
                "EVERYTHING IS MINE!"
        );

        playUltimateEffect(player);

        player.playSound(
                player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                1.0f,
                0.8f
        );

        /*
         * Temporary self effects.
         */
        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.LUCK,
                        duration * 20,
                        2,
                        false,
                        false,
                        true
                )
        );

        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.HASTE,
                        duration * 20,
                        1,
                        false,
                        false,
                        true
                )
        );
    }

    // =========================================================
    // COOLDOWN CHECK
    // =========================================================

    private boolean isOnCooldown(
            Map<UUID, Long> cooldownMap,
            UUID uuid
    ) {

        Long end = cooldownMap.get(uuid);

        if (end == null) {
            return false;
        }

        if (System.currentTimeMillis() >= end) {
            cooldownMap.remove(uuid);
            return false;
        }

        return true;
    }

    // =========================================================
    // COOLDOWN MESSAGE
    // =========================================================

    private void sendCooldownMessage(
            Player player,
            Map<UUID, Long> cooldownMap,
            UUID uuid
    ) {

        Long end = cooldownMap.get(uuid);

        if (end == null) {
            return;
        }

        long remaining =
                Math.max(
                        0,
                        end - System.currentTimeMillis()
                );

        double seconds =
                remaining / 1000.0;

        player.sendActionBar(
                ChatColor.RED +
                String.format(
                        "Cooldown: %.1fs",
                        seconds
                )
        );
    }

    // =========================================================
    // GOLD SELECTION EFFECT
    // =========================================================

    private void playSelectionEffect(Player player) {

        World world = player.getWorld();

        world.spawnParticle(
                Particle.DUST,
                player.getLocation().clone().add(0, 1, 0),
                12,
                0.35,
                0.45,
                0.35,
                0,
                goldDust
        );
    }

    // =========================================================
    // GOLD EFFECT
    // =========================================================

    private void playGoldEffect(Player player) {

        World world = player.getWorld();

        world.spawnParticle(
                Particle.DUST,
                player.getLocation().clone().add(0, 1, 0),
                20,
                0.5,
                0.7,
                0.5,
                0,
                goldDust
        );
    }

    // =========================================================
    // GOLD BURST
    // =========================================================

    private void playGoldBurst(Player player) {

        World world = player.getWorld();

        world.spawnParticle(
                Particle.DUST,
                player.getLocation().clone().add(0, 1, 0),
                45,
                0.8,
                1.0,
                0.8,
                0,
                goldDust
        );

        world.spawnParticle(
                Particle.TOTEM_OF_UNDYING,
                player.getLocation().clone().add(0, 1, 0),
                12,
                0.4,
                0.7,
                0.4,
                0
        );
    }

    // =========================================================
    // GOLD RING
    // =========================================================

    private void playGoldRing(
            org.bukkit.Location center,
            double radius
    ) {

        World world = center.getWorld();

        if (world == null) {
            return;
        }

        for (int i = 0; i < 80; i++) {

            double angle =
                    (Math.PI * 2 * i) / 80.0;

            double x =
                    Math.cos(angle) * radius;

            double z =
                    Math.sin(angle) * radius;

            org.bukkit.Location particleLocation =
                    center.clone().add(
                            x,
                            0.15,
                            z
                    );

            world.spawnParticle(
                    Particle.DUST,
                    particleLocation,
                    1,
                    0,
                    0,
                    0,
                    0,
                    goldDust
            );
        }
    }

    // =========================================================
    // ULTIMATE EFFECT
    // =========================================================

    private void playUltimateEffect(Player player) {

        World world = player.getWorld();

        org.bukkit.Location center =
                player.getLocation().clone().add(0, 1, 0);

        /*
         * Main gold explosion.
         */
        world.spawnParticle(
                Particle.DUST,
                center,
                100,
                1.2,
                1.5,
                1.2,
                0,
                goldDust
        );

        /*
         * Totem-style secondary effect.
         */
        world.spawnParticle(
                Particle.TOTEM_OF_UNDYING,
                center,
                40,
                1.0,
                1.2,
                1.0,
                0
        );

        /*
         * Gold rings.
         */
        for (int ring = 1; ring <= 3; ring++) {

            double radius = ring * 2.0;

            playGoldRing(
                    center,
                    radius
            );
        }

        /*
         * Gold pillar.
         */
        for (double y = 0; y <= 4; y += 0.25) {

            world.spawnParticle(
                    Particle.DUST,
                    player.getLocation().clone().add(
                            0,
                            y,
                            0
                    ),
                    5,
                    0.3,
                    0.05,
                    0.3,
                    0,
                    goldDust
            );
        }
    }
}