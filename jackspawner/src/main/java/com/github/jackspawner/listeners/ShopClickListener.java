package com.github.jackspawner.listeners;

import com.github.jackspawner.commands.JackShopCommand;
import com.github.jackspawner.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopClickListener implements Listener {

    private static final int PRICE_GOLD = 15;     // diamonds
    private static final int PRICE_DIAMOND = 45;  // diamonds

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title() == null) return;
        Component title = event.getView().title();
        String plainTitle = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(title);
        if (!JackShopCommand.SHOP_TITLE.equals(plainTitle)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        int slot = event.getSlot();
        if (slot == JackShopCommand.SLOT_GOLD) {
            handlePurchase(player, PRICE_GOLD, Material.GOLDEN_PICKAXE, 20);
        } else if (slot == JackShopCommand.SLOT_DIAMOND) {
            handlePurchase(player, PRICE_DIAMOND, Material.DIAMOND_PICKAXE, 50);
        }
    }

    private void handlePurchase(Player player, int price, Material material, int chance) {
        int diamondsInInv = countItem(player, Material.DIAMOND);
        if (diamondsInInv < price) {
            player.sendMessage(Component.text("Недостаточно алмазов! Нужно " + price)
                .color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            return;
        }

        removeItems(player, Material.DIAMOND, price);
        ItemStack pickaxe = ItemUtils.createJackPickaxeWith(material, chance);
        player.getInventory().addItem(pickaxe);
        player.sendMessage(Component.text("Покупка успешна!" )
            .color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
    }

    private int countItem(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private void removeItems(Player player, Material material, int amount) {
        int toRemove = amount;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() != material) continue;
            int remove = Math.min(item.getAmount(), toRemove);
            item.setAmount(item.getAmount() - remove);
            toRemove -= remove;
            if (item.getAmount() <= 0) {
                player.getInventory().setItem(i, null);
            }
            if (toRemove <= 0) break;
        }
    }
}

