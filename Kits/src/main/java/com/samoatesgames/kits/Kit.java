package com.samoatesgames.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Sam
 */
public class Kit {

    public void loadKit(JSONObject kitJson) {
        m_json = kitJson;

        // name
        m_name = (String) loadJsonString(kitJson, "name", m_name);

        // icon
        m_icon = (Material) loadJsonString(kitJson, "icon", m_icon);

        // timeout (minutes)
        Object rawTimeout = loadJsonString(kitJson, "timeout-minutes", m_timeout);
        if (rawTimeout instanceof Double) {
            m_timeout = (Double) rawTimeout;
        } else if (rawTimeout instanceof Integer) {
            m_timeout = ((Integer) rawTimeout).doubleValue();
        }
        

        // items
        loadItems(kitJson, "items");
    }

    /**
     *
     * @param kitJson
     * @param items
     */
    private void loadItems(JSONObject kitJson, String key) {

        JSONArray jsonItems = (JSONArray) kitJson.get(key);
        for (Object itemRaw : jsonItems) {
            try {
                JSONObject itemJson = (JSONObject) itemRaw;
                ItemStack item = loadItem(itemJson);
                int slot = -1;
                if (itemJson.containsKey("inventory-slot")) {
                    Object rawInvSlot = itemJson.get("inventory-slot");
                    if (rawInvSlot instanceof Long) {
                        slot = ((Long) rawInvSlot).intValue();
                    } else if (rawInvSlot instanceof Integer) {
                        slot = ((Integer) rawInvSlot);
                    }                    
                }
                m_items.put(slot, item);
            } catch (Exception ex) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to load an item from kit '" + m_name + "'", ex);
            }
        }

    }

    /**
     *
     * @param itemJson
     * @return
     */
    private ItemStack loadItem(JSONObject itemJson) {

        // Material
        Material type = Material.getMaterial((String) itemJson.get("material"));

        // Amount
        Integer amount;
        Object rawAmount = itemJson.get("amount");
        if (rawAmount instanceof Long) {
            amount = ((Long)rawAmount).intValue();
        } else {
            amount = (Integer)rawAmount;
        }
        
        // data
        byte data = 0;
        if (itemJson.containsKey("data")) {
            Object rawData = itemJson.get("data");
            if (rawData instanceof Long) {
                data = ((Long) itemJson.get("data")).byteValue();
            } else if (rawData instanceof Byte) {
                data = (Byte) itemJson.get("data");
            }            
        }

        // Make the base item from material and amount
        ItemStack item = new ItemStack(type, amount, (short) 0, data);
        ItemMeta meta = item.getItemMeta();

        // Name
        if (itemJson.containsKey("name")) {
            meta.setDisplayName((String) itemJson.get("name"));
        }

        // lore
        if (itemJson.containsKey("lore")) {
            JSONArray loreJson = (JSONArray) itemJson.get("lore");
            List<String> itemLore = new ArrayList<String>();
            for (Object lore : loreJson) {
                itemLore.add((String) lore);
            }
            meta.setLore(itemLore);
        }

        // enchantments
        if (itemJson.containsKey("enchantments")) {
            JSONArray enchantmentsArray = (JSONArray) itemJson.get("enchantments");
            for (Object enchantmentRaw : enchantmentsArray) {
                JSONObject enchantJson = (JSONObject) enchantmentRaw;
                String stringType = (String) enchantJson.get("type");
                Long level = (Long) enchantJson.get("level");
                item.addUnsafeEnchantment(Enchantment.getByName(stringType), level.intValue());
            }
        }

        item.setItemMeta(meta);
        return item;

    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private Object loadJsonString(JSONObject json, String key, Object defaultValue) {

        if (!json.containsKey(key)) {
            return defaultValue;
        }
        return json.get(key);
    }

    private JSONObject m_json = null;

    public JSONObject getJson() {
        return m_json;
    }

    private Material m_icon = Material.CHEST;

    public Material getIcon() {
        return m_icon;
    }

    private String m_name = "Kit";

    public String getName() {
        return m_name;
    }

    private Double m_timeout = 60.0;

    public Double getTimeout() {
        return m_timeout;
    }

    private Map<Integer, ItemStack> m_items = new HashMap<Integer, ItemStack>();

    public Map<Integer, ItemStack> getItems() {
        return m_items;
    }

}
