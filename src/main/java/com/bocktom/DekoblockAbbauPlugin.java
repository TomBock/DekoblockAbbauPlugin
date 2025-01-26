package com.bocktom;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DekoblockAbbauPlugin extends JavaPlugin {

	public Logger log;
	public FileConfiguration config;


	@Override
	public void onEnable() {
		log = getLogger();
		config = getConfig();

		getServer().getPluginManager().registerEvents(new DekoblockBreakListener(this), this);
	}
}
