package me.jishuna.lootmonsters.api;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.lootmonsters.LootMonsters;

public enum PluginKeys {
	MONSTER_TYPE("type");

	private final NamespacedKey key;

	private PluginKeys(String name) {
		this.key = new NamespacedKey(JavaPlugin.getPlugin(LootMonsters.class), name);
	}

	public NamespacedKey getKey() {
		return this.key;
	}

}
