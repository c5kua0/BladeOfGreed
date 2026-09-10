package me.greed.bladeofgreed;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
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

    private NamespacedKey relicKey;

    private final Map<UUID, Long> authorityCooldown = new HashMap<>();
    private final Map<UUID, Long> claimCooldown = new HashMap<>();
    private final Map<UUID, Long> ultimateCooldown = new HashMap<>();

    private final Map<UUID, Boolean> greedEnabled = new HashMap<>();

    @Override
    public void onEnable() {

        saveDefaultConfig();

        relicKey = new NamespacedKey(this, "greed_relic");

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Greed Relic enabled!");
    }

    /*
     * /bg
     */
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
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.getName().equalsIgnoreCase(OWNER)) {
            player.sendMessage(
                    ChatColor.RED +
                    "This relic belongs to " +
                    ChatColor.YELLOW +
                    OWNER +
                    ChatColor.RED +
                    "."
            );
            return true;
        }

        player.getInventory().addItem(createRelic());

        player.sendMessage(
                ChatColor.GOLD +
                "✦ You received the Greed Relic!"
        );

        return true;
    }

    /*
     * Create relic
     */
    private ItemStack createRelic() {

        ItemStack item = new ItemStack(Material.NETHER_STAR);

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(
                    ChatColor.GOLD +
                    "" +
                    ChatColor.BOLD +
                    "Greed Relic"
            );

            meta.setLore(java.util.List.of(
                    ChatColor.GRAY + "Relic of the Sin Archbishop of Greed",
                    "",
                    ChatColor.YELLOW + "Slot 5: Greed",
                    ChatColor.YELLOW + "Slot 6: Authority",
                    ChatColor.YELLOW + "Slot 7: Claim",
                    ChatColor.YELLOW + "Slot 8: Everything Is Mine",
                    ChatColor.GRAY + "Slot 9: Disable Skills"
            ));

            meta.setUnbreakable(true);

            meta.getPersistentDataContainer().set(
                    relicKey,
                    PersistentDataType.BYTE,
                    (byte) 1
            );

            item.setItemMeta(meta);
        }

        return item;
    }

    /*
     * Check relic
     */
    private boolean isRelic(ItemStack item) {

        if (item == null) {
            return false;
        }

        if (item.getType() != Material.NETHER_STAR) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        Byte value = meta.getPersistentDataContainer().get(
                relicKey,
                PersistentDataType.BYTE
        );

        return value != null && value == (byte) 1;
    }

    /*
     * Hotbar selection
     */
    @EventHandler
    public void onHotbar(PlayerItemHeldEvent event) {

        Player player = event.getPlayer();

        if (!player.getName().equalsIgnoreCase(OWNER)) {
            return;
        }

        int newSlot = event.getNewSlot();

        // Slot 9 = disable
        if (newSlot == 8) {

            greedEnabled.put(player.getUniqueId(), false);

            player.sendMessage(
                    ChatColor.GRAY +
                    "Greed skills disabled."
            );

            return;
        }

        // Slot 5
        if (newSlot == 4) {

            player.sendMessage(
                    ChatColor.GOLD +
                    "Greed Relic selected."
            );

            return;
        }

        // Slot 6
        if (newSlot == 5) {

            player.sendMessage(
                    ChatColor.YELLOW +
                    "Authority selected. Right-click to activate."
            );

            return;
        }

        // Slot 7
        if (newSlot == 6) {

            player.sendMessage(
                    ChatColor.YELLOW +
                    "Greedy Claim selected. Right-click to activate."
            );

            return;
        }

        // Slot 8
        if (newSlot == 7) {

            player.sendMessage(
                    ChatColor.GOLD +
                    "" +
                    ChatColor.BOLD +
                    "Everything Is Mine selected. Right-click to activate."
            );
        }
    }

    /*
     * Right click abilities
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (!player.getName().equalsIgnoreCase(OWNER)) {
            return;
        }

        if (!event.getAction().isRightClick()) {
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();

        /*
         * Slot 5
         */
        if (slot == 4) {

            toggleGreed(player);
            return;
        }

        /*
         * Slot 6
         */
        if (slot == 5) {

            authority(player);
            return;
        }

        /*
         * Slot 7
         */
        if (slot == 6) {

            greedyClaim(player);
            return;
        }

        /*
         * Slot 8
         */
        if (slot == 7) {

            everythingIsMine(player);
        }
    }

    /*
     * Skill 1
     */
    private void toggleGreed(Player player) {

        boolean enabled = greedEnabled.getOrDefault(
                player.getUniqueId(),
                false
        );

        enabled = !enabled;

        greedEnabled.put(
                player.getUniqueId(),
                enabled
        );

        if (enabled) {

            player.sendMessage(
                    ChatColor.GOLD +
                    "" +
                    ChatColor.BOLD +
                    "GREED ACTIVATED!"
            );

            player.getWorld().spawnParticle(
                    Particle.GOLD_NUGGET,
                    player.getLocation().add(0, 1, 0),
                    30,
                    0.5,
                    1,
                    0.5,
                    0.05
            );

            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                    1.0f,
                    0.7f
            );

        } else {

            player.sendMessage(
                    ChatColor.GRAY +
                    "Greed deactivated."
            );
        }
    }

    /*
     * Skill 2
     */
    private void authority(Player player) {

        if (isCooldown(
                player,
                authorityCooldown,
                getConfig().getLong("cooldowns.authority")
        )) {
            return;
        }

        setCooldown(
                player,
                authorityCooldown,
                getConfig().getLong("cooldowns.authority")
        );

        int radius = getConfig().getInt(
                "skills.authority.radius"
        );

        player.sendMessage(
                ChatColor.YELLOW +
                "" +
                ChatColor.BOLD +
                "AUTHORITY OF GREED!"
        );

        player.getWorld().spawnParticle(
                Particle.GOLD_NUGGET,
                player.getLocation().add(0, 1, 0),
                100,
                radius / 2.0,
                1,
                radius / 2.0,
                0.05
        );

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_BEACON_ACTIVATE,
                1.0f,
                0.8f
        );
    }

    /*
     * Skill 3
     */
    private void greedyClaim(Player player) {

        if (isCooldown(
                player,
                claimCooldown,
                getConfig().getLong("cooldowns.claim")
        )) {
            return;
        }

        setCooldown(
                player,
                claimCooldown,
                getConfig().getLong("cooldowns.claim")
        );

        player.sendMessage(
                ChatColor.GOLD +
                "" +
                ChatColor.BOLD +
                "GREEDY CLAIM!"
        );

        player.getWorld().spawnParticle(
                Particle.GOLD_NUGGET,
                player.getLocation().add(0, 1, 0),
                60,
                1,
                1,
                1,
                0.08
        );

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,
                1.0f,
                1.2f
        );
    }

    /*
     * Ultimate
     */
    private void everythingIsMine(Player player) {

        if (isCooldown(
                player,
                ultimateCooldown,
                getConfig().getLong("cooldowns.ultimate")
        )) {
            return;
        }

        setCooldown(
                player,
                ultimateCooldown,
                getConfig().getLong("cooldowns.ultimate")
        );

        player.sendMessage(
                ChatColor.GOLD +
                "" +
                ChatColor.BOLD +
                "EVERYTHING IS MINE!"
        );

        Bukkit.broadcastMessage(
                ChatColor.GOLD +
                "" +
                ChatColor.BOLD +
                "[GREED] " +
                player.getName() +
                " has unleashed EVERYTHING IS MINE!"
        );

        player.getWorld().spawnParticle(
                Particle.GOLD_NUGGET,
                player.getLocation().add(0, 1, 0),
                250,
                4,
                2,
                4,
                0.15
        );

        player.getWorld().spawnParticle(
                Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0),
                100,
                2,
                2,
                2,
                0.1
        );

        player.playSound(
                player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                1.0f,
                0.7f
        );

        int duration = getConfig().getInt(
                "skills.ultimate.duration"
        );

        /*
         * Self-buff only.
         * This keeps the relic version non-combat-focused.
         */
        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.LUCK,
                        duration * 20,
                        2,
                        false,
                        true,
                        true
                )
        );

        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.HASTE,
                        duration * 20,
                        1,
                        false,
                        true,
                        true
                )
        );
    }

    /*
     * Cooldown helpers
     */
    private boolean isCooldown(
            Player player,
            Map<UUID, Long> cooldowns,
            long seconds
    ) {

        long now = System.currentTimeMillis();

        Long end = cooldowns.get(
                player.getUniqueId()
        );

        if (end == null || now >= end) {
            return false;
        }

        long remaining =
                (end - now + 999) / 1000;

        player.sendMessage(
                ChatColor.RED +
                "Ability cooldown: " +
                ChatColor.YELLOW +
                remaining +
                "s"
        );

        return true;
    }

    private void setCooldown(
            Player player,
            Map<UUID, Long> cooldowns,
            long seconds
    ) {

        cooldowns.put(
                player.getUniqueId(),
                System.currentTimeMillis() +
                        (seconds * 1000)
        );
    }
}