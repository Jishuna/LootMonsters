package me.jishuna.lootmonsters.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.jishuna.commonlib.random.WeightedRandom;

public class MonsterRegistry {
	
	private final Map<String, LootMonster> registryMap = new HashMap<>();
	private final WeightedRandom<LootMonster> monsterRandom = new WeightedRandom<>();
	
	public void registerMonster(LootMonster monster) {
		this.registryMap.put(monster.getName(), monster);
		this.monsterRandom.add(monster.getWeight(), monster);
	}
	
	public LootMonster getRandomMonster() {
		return monsterRandom.poll();
	}
	
	public Optional<LootMonster> getMonster(String key) {
		return Optional.ofNullable(this.registryMap.get(key));
	}
}
