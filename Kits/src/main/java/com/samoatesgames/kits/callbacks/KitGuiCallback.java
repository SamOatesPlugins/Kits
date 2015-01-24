package com.samoatesgames.kits.callbacks;

import com.samoatesgames.kits.Kit;
import com.samoatesgames.kits.Kits;
import com.samoatesgames.samoatesplugincore.gui.GuiCallback;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Sam
 */
public class KitGuiCallback implements GuiCallback {

    final private Kits m_plugin;
    final private Player m_player;
    final private Kit m_kit;
    final private boolean m_canClaim;

    /**
     *
     * @param plugin
     * @param player
     * @param kit
     * @param canClaim
     */
    public KitGuiCallback(Kits plugin, Player player, Kit kit, boolean canClaim) {
        m_plugin = plugin;
        m_player = player;
        m_kit = kit;
        m_canClaim = canClaim;
    }

    /**
     *
     * @param inventory
     * @param clickEvent
     */
    @Override
    public void onClick(GuiInventory inventory, InventoryClickEvent clickEvent) {

        if (clickEvent.isLeftClick() && m_canClaim) {
            m_plugin.giveKit(m_player, m_kit);
            inventory.close(m_player);
            m_player.closeInventory();
        } else if (clickEvent.isRightClick()) {

            // Preview Kit
            GuiInventory previewInvent = new GuiInventory(inventory.getPlugin());
            previewInvent.createInventory(m_kit.getName() + " Kit Preview", 3);

            Map<Integer, ItemStack> kitItems = m_kit.getItems();
            for (Entry<Integer, ItemStack> item : kitItems.entrySet()) {

                int slot = item.getKey();
                ItemStack previewItem = item.getValue().clone();
                ItemMeta meta = previewItem.getItemMeta();

                String name = previewItem.getType().name();
                if (meta.hasDisplayName()) {
                    name = meta.getDisplayName();
                }

                String[] details = new String[]{};
                if (meta.hasLore()) {
                    details = (String[]) meta.getLore().toArray();
                }

                previewInvent.addMenuItem(formatName(name), previewItem, details, slot, previewItem.getAmount(), new KitPreviewGuiCallback(inventory, m_player));

            }

            inventory.close(m_player);
            m_player.closeInventory();
            previewInvent.open(m_player);

        }

    }

    /**
     *
     * @param raw
     * @return
     */
    private String formatName(String raw) {

        String lower = raw.toLowerCase();
        String[] parts = lower.split("_");
        String formatted = "";
        for (String part : parts) {
            formatted += part.substring(0, 1).toUpperCase() + part.substring(1) + " ";
        }
        return formatted;

    }

}
