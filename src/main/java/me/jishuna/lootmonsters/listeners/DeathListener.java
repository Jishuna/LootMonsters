package me.jishuna.lootmonsters.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.lootmonsters.LootMonsters;
import me.jishuna.lootmonsters.api.PluginKeys;

public class DeathListener implements Listener {

	private final LootMonsters plugin;

	public DeathListener(LootMonsters plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() == null
				&& this.plugin.getConfiguration().getBoolean("player-kill-only", true))
			return;

		PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
		String key = container.getOrDefault(PluginKeys.MONSTER_TYPE.getKey(), PersistentDataType.STRING, "");

		this.plugin.getMonsterRegistry().getMonster(key).ifPresent(monster -> monster.handleDeath(event));
	}
}
