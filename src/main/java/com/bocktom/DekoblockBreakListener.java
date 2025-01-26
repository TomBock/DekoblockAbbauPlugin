package com.bocktom;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;

import java.util.HashSet;


public class DekoblockBreakListener implements Listener {

	private final HashSet<Material> materialQuickLookup = new HashSet<>();
	private final DekoblockAbbauPlugin plugin;

	public DekoblockBreakListener(DekoblockAbbauPlugin plugin) {
		this.plugin = plugin;

		// Cache the materials to avoid storing the whole list in cache or reading the config every time
		loadMaterials();
	}

	private void loadMaterials() {
		var serializedItems = plugin.config.getList("blocks");
		if(serializedItems != null) {
			for(var serializedItem : serializedItems) {
				if(serializedItem instanceof ItemStack item)
					materialQuickLookup.add(item.getType());
			}
		} else {
			plugin.log.warning("Blocks could not be loaded. This plugin will not work.");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		ItemStack tool = player.getInventory().getItemInMainHand();

		// 1. Check; Silk-touch
		if(tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
			player.sendMessage("You broke a " + event.getBlock() + " with Silk Touch!");

			// 2. Check; Material
			if(materialQuickLookup.contains(event.getBlock().getType())) {

				// 3. Check; Check against config
				var item = findDekoblockViaConfig(event.getBlock().getBlockData());
				if(item != null) {
					event.setDropItems(false);
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), item);

					player.sendMessage("You got a " + item);
				}
			}
		}
	}

	/**
	 * Resource-expensive lookup to find the matching block based on its <@param>brokenBlockData</@param>
	 */
	private ItemStack findDekoblockViaConfig(BlockData brokenBlockData) {
		var serializedList = plugin.config.getList("blocks");

		if(serializedList == null)
			return null; // A warning is logged at startup if this is the case, ignore all calls to avoid errors

		for(var serializedItem : serializedList) {
			if(serializedItem instanceof ItemStack item)
			{
				if(!(item.getItemMeta() instanceof BlockDataMeta blockMeta)) {
					plugin.log.warning("Item in config is no block: " + item.getItemMeta().getAsString());
					continue;
				}

				var blockData = blockMeta.getBlockData(item.getType());

				if(brokenBlockData.matches(blockData))
					return item;
			}
		}
		return null;
	}

}
