package com.samoatesgames.kits.commands;

import com.samoatesgames.kits.Kit;
import com.samoatesgames.kits.KitClaim;
import com.samoatesgames.kits.Kits;
import com.samoatesgames.kits.callbacks.KitGuiCallback;
import com.samoatesgames.samoatesplugincore.commands.BasicCommandHandler;
import com.samoatesgames.samoatesplugincore.commands.PluginCommandManager;
import com.samoatesgames.samoatesplugincore.gui.GuiInventory;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitCommand extends BasicCommandHandler {

    private static final String PERMISSION_KIT = "kits";
    private static final String PERMISSION_KIT_GUI = "kits.gui";

    private final Kits m_plugin;

    /**
     *
     * @param plugin
     */
    public KitCommand(Kits plugin) {
        super(PERMISSION_KIT);
        m_plugin = plugin;
    }

    @Override
    public boolean execute(PluginCommandManager manager, CommandSender sender, String[] arguments) {
        
        if (!(sender instanceof Player)) {
            manager.sendMessage(sender, "Only players can use kit commands");
            return true;
        }

        final Player player = (Player) sender;
        
        if (arguments.length == 0) {
            handleKitGUI(manager, player);
            return true;
        }

        final String kitname = arguments[0].toLowerCase();
        final Map<String, Kit> kits = m_plugin.getKits();
        
        if (!kits.containsKey(kitname)) {
            manager.sendMessage(player, "There is no kit with the name '" + kitname + "'");
            return true;
        }

        Kit kit = kits.get(kitname);
        KitClaim claim = m_plugin.canPlayerClaimKit(player, kit);

        if (!claim.canClaim) {
            manager.sendMessage(player, claim.timeLeft);
            return true;
        }

        m_plugin.giveKit(player, kit);
        return true;
    }
    
    /**
     *
     * @param player
     */
    private void handleKitGUI(final PluginCommandManager manager, final Player player) {

        if (!manager.hasPermission(player, PERMISSION_KIT_GUI)) {
            manager.sendMessage(player, "You do not have permission to use the kits GUI");
            return;
        }

        final Map<String, Kit> kits = m_plugin.getKits();
        final int noofKits = kits.size();
        final int rowCount = (int) Math.ceil(noofKits / 9.0f);

        GuiInventory inventory = new GuiInventory(m_plugin);
        inventory.createInventory("Kit Selection", rowCount);

        for (Kit kit : kits.values()) {

            if (!manager.hasPermission(player, PERMISSION_KIT + "." + (kit.getName().toLowerCase()))) {
                continue;
            }

            ItemStack item = new ItemStack(kit.getIcon());
            String[] details = new String[3];

            KitClaim claim = m_plugin.canPlayerClaimKit(player, kit);

            if (claim.canClaim) {
                details[0] = ChatColor.GREEN + "Available";
                details[1] = ChatColor.GOLD + "Left click to claim kit";
            } else {
                details[0] = ChatColor.RED + claim.timeLeft;
                details[1] = "";
            }

            details[2] = ChatColor.GOLD + "Right click to preview kit";

            inventory.addMenuItem(kit.getName(), item, details, new KitGuiCallback(m_plugin, player, kit, claim.canClaim));
        }

        inventory.open(player);
    }
}
