package com.samoatesgames.kits.commands;

import com.samoatesgames.kits.Kits;
import com.samoatesgames.samoatesplugincore.commands.BasicCommandHandler;
import com.samoatesgames.samoatesplugincore.commands.PluginCommandManager;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Sam
 */
public class KitSaveCommand extends BasicCommandHandler {

    final private static String PERMISSION_KIT_SAVE = "kits.save";
    final private Kits m_plugin;

    public KitSaveCommand(Kits plugin) {
        super(PERMISSION_KIT_SAVE);
        m_plugin = plugin;
    }

    /**
     *
     * @param manager
     * @param sender
     * @param arguments
     * @return
     */
    @Override
    public boolean execute(PluginCommandManager manager, CommandSender sender, String[] arguments) {
        
        if (!(sender instanceof Player)) {
            manager.sendMessage(sender, "Only players can save kits");
            return true;
        }

        final Player player = (Player) sender;

        if (!manager.hasPermission(player, PERMISSION_KIT_SAVE)) {
            manager.sendMessage(player, "You do not have the required permissions to save kits");
            return true;
        }

        if (arguments.length != 2) {
            manager.sendMessage(player, "Invalid Usage: /kitsave <kitname> <timeout in minutes>");
            return true;
        }

        final PlayerInventory invent = player.getInventory();
        final String kitName = arguments[0];
        final Double kitTimeout = Double.parseDouble(arguments[1]);

        JSONObject newKit = new JSONObject();

        newKit.put("name", kitName);
        newKit.put("timeout-minutes", kitTimeout);

        JSONArray items = new JSONArray();
        for (int itemIndex = 9; itemIndex < invent.getSize(); ++itemIndex) {
            ItemStack item = invent.getItem(itemIndex);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            JSONObject itemObject = convertItemToObject(item, itemIndex - 9);
            items.add(itemObject);
        }
        newKit.put("items", items);

        m_plugin.addNewKit(kitName, newKit);
        m_plugin.reloadKits();
        manager.sendMessage(sender, "New kit created and saved.");

        return true;
    }

    /**
     *
     * @param item
     * @param slotIndex
     * @return
     */
    private JSONObject convertItemToObject(ItemStack item, int slotIndex) {

        JSONObject itemObject = new JSONObject();
        ItemMeta meta = item.getItemMeta();

        // name
        if (meta.hasDisplayName()) {
            itemObject.put("name", meta.getDisplayName());
        }

        // material
        itemObject.put("material", item.getType().name());

        // amount
        itemObject.put("amount", item.getAmount());

        // data
        if (item.getData().getData() != 0) {
            itemObject.put("data", item.getData().getData());
        }

        // inventory slot
        itemObject.put("inventory-slot", slotIndex);

        // lore
        if (meta.hasLore()) {
            JSONArray lores = new JSONArray();
            for (String lore : meta.getLore()) {
                lores.add(lore);
            }
            itemObject.put("lore", lores);
        }

        // enchantments
        if (meta.hasEnchants()) {
            JSONArray enchants = new JSONArray();
            for (Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()) {
                JSONObject objectEnchant = new JSONObject();
                objectEnchant.put("type", enchant.getKey().getName());
                objectEnchant.put("level", enchant.getValue());
                enchants.add(objectEnchant);
            }
            itemObject.put("enchantments", enchants);
        }

        return itemObject;

    }
}
