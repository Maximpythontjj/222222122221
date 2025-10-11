package com.example.rtp;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class RtpPlugin extends JavaPlugin {

    private static final int MAX_ATTEMPTS = 24;

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private long cooldownMillis;
    private int minRadius;
    private int maxRadius;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadSettings();

        Objects.requireNonNull(getCommand("rtp"), "Command /rtp must be defined in plugin.yml").setExecutor(this);
    }

    @Override
    public void onDisable() {
        cooldowns.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда доступна только игрокам.");
            return true;
        }

        if (!player.hasPermission("rtp.use")) {
            player.sendMessage("§cУ вас нет прав на использование этой команды.");
            return true;
        }

        World world = player.getWorld();
        long now = System.currentTimeMillis();
        long expiresAt = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (expiresAt > now) {
            long remainingSeconds = (long) Math.ceil((expiresAt - now) / 1000.0);
            player.sendMessage("§cПодождите еще " + remainingSeconds + " секунд перед следующим телепортом.");
            return true;
        }

        Location target = findSafeLocation(world, player.getLocation());
        if (target == null) {
            player.sendMessage("§cНе удалось найти безопасное место для телепортации. Попробуйте позже.");
            return true;
        }

        UUID playerId = player.getUniqueId();
        cooldowns.put(playerId, now + cooldownMillis);
        playTeleportEffects(player.getLocation());
        player.teleportAsync(target).thenAccept(success -> {
            getServer().getScheduler().runTask(this, () -> {
                if (success) {
                    playTeleportEffects(target);
                    triggerSuccessEffects(player, target);
                } else {
                    cooldowns.remove(playerId);
                    player.sendMessage("§cТелепортация не удалась. Попробуйте снова.");
                }
            });
        });
        return true;
    }

    private void reloadSettings() {
        reloadConfig();
        this.minRadius = Math.max(0, getConfig().getInt("radius.min", 500));
        this.maxRadius = Math.max(this.minRadius + 1, getConfig().getInt("radius.max", 1500));
        long seconds = Math.max(1L, getConfig().getLong("cooldown-seconds", 30L));
        this.cooldownMillis = Duration.ofSeconds(seconds).toMillis();
    }

    private Location findSafeLocation(World world, Location center) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            double distance = random.nextDouble(minRadius, maxRadius);
            double angle = random.nextDouble(0, Math.PI * 2);
            int x = (int) Math.round(center.getX() + Math.cos(angle) * distance);
            int z = (int) Math.round(center.getZ() + Math.sin(angle) * distance);

            world.getChunkAt(x >> 4, z >> 4); // ensure the chunk is loaded synchronously

            Block highest = world.getHighestBlockAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
            if (!isBlockSafe(highest)) {
                continue;
            }

            Location spawnLocation = highest.getLocation().add(0.5, 1, 0.5);
            if (!isSpaceSafe(spawnLocation)) {
                continue;
            }

            return spawnLocation;
        }
        return null;
    }

    private boolean isBlockSafe(Block block) {
        Material type = block.getType();
        if (type == Material.BEDROCK || type == Material.LAVA || type == Material.WATER) {
            return false;
        }
        if (type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR) {
            return false;
        }
        return type.isSolid();
    }

    private boolean isSpaceSafe(Location location) {
        Block feet = location.getBlock();
        Block head = location.clone().add(0, 1, 0).getBlock();
        return feet.isPassable() && head.isPassable();
    }

    private void playTeleportEffects(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        world.spawnParticle(Particle.PORTAL, location, 120, 0.5, 1.0, 0.5, 0.1);
        world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    private void triggerSuccessEffects(Player player, Location location) {
        player.sendMessage("§aВы успешно телепортированы.");
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0, false, true, true));

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.spawn(location, Firework.class, firework -> {
            firework.setSilent(true);
            firework.setShotAtAngle(false);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(1);
            meta.clearEffects();
            meta.addEffect(FireworkEffect.builder()
                .withColor(Color.LIME, Color.WHITE)
                .withFade(Color.GREEN)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withFlicker()
                .build());
            firework.setFireworkMeta(meta);
        });
    }
}
