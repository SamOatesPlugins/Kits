package com.samoatesgames.kits.callbacks;

import com.samoatesgames.samoatesplugincore.gui.GuiCallback;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author Sam
 */
public class KitPreviewGuiCallback implements GuiCallback {

    final private GuiInventory m_kitMenu;
    final private Player m_player;

    public KitPreviewGuiCallback(GuiInventory kitMenu, Player player) {
        m_kitMenu = kitMenu;
        m_player = player;
    }

    @Override
    public void onClick(GuiInventory inventory, InventoryClickEvent clickEvent) {

        inventory.close(m_player);
        m_kitMenu.open(m_player);

    }

}
