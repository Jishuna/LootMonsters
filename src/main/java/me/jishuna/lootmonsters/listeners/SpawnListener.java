package me.jishuna.lootmonsters.listeners;

import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import me.jishuna.commonlib.utils.NumberUtils;
import me.jishuna.lootmonsters.LootMonsters;
import me.jishuna.lootmonsters.api.LootMonster;

public class SpawnListener implements Listener {

	private final LootMonsters plugin;
	private boolean ignore;

	public SpawnListener(LootMonsters plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSpawn(CreatureSpawnEvent event) {
		if (ignore || !(event.getEntity() instanceof Monster))
			return;

		if (event.getSpawnReason() == SpawnReason.NATURAL) {
			processEvent(event, this.plugin.getConfiguration().getDouble("spawn-chance-natural", 0.5));
		} else if (event.getSpawnReason() == SpawnReason.SPAWNER) {
			processEvent(event, this.plugin.getConfiguration().getDouble("spawn-chance-spawner", 0));
		}
	}

	private void processEvent(CreatureSpawnEvent event, double threshold) {
		double chance = NumberUtils.getRandomDouble(0, 100);

		if (threshold <= 0 || chance >= threshold)
			return;

		ignore = true;
		LootMonster monster = this.plugin.getMonsterRegistry().getRandomMonster(event.getEntityType());
		if (monster != null) {
			monster.spawn(event.getLocation());
			event.setCancelled(true);
		}
		ignore = false;
	}

}
