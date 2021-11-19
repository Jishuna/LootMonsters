package me.jishuna.lootmonsters;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.lootmonsters.api.LootMonster;
import me.jishuna.lootmonsters.api.MonsterRegistry;
import me.jishuna.lootmonsters.listeners.DeathListener;
import me.jishuna.lootmonsters.listeners.SpawnListener;

public class LootMonsters extends JavaPlugin {
	private static final String PATH = "Monsters";

	private YamlConfiguration configuration;
	private MonsterRegistry registry;

	@Override
	public void onEnable() {
		this.loadConfiguration();

		this.registry = new MonsterRegistry();
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new SpawnListener(this), this);
		pm.registerEvents(new DeathListener(this), this);
		
		this.loadMonsters();
	}

	@Override
	public void onDisable() {
	}
	
	private void loadMonsters() {
		File monsterFolder = new File(this.getDataFolder(), PATH);
		if (!monsterFolder.exists()) {
			monsterFolder.mkdirs();
			FileUtils.copyDefaults(this, PATH, name -> FileUtils.loadResourceFile(this, name));
		}

		this.loadRecursively(monsterFolder);
	}
	
	public void loadRecursively(File folder) {
		for (File file : folder.listFiles()) {
			String name = file.getName();

			if (file.isDirectory())
				loadRecursively(file);

			if (!name.endsWith(".yml"))
				continue;

			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			LootMonster monster = new LootMonster(config);
			this.registry.registerMonster(monster);
		}
	}

	public void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		FileUtils.loadResource(this, "config.yml").ifPresent(config -> this.configuration = config);
	}

	public YamlConfiguration getConfiguration() {
		return configuration;
	}

	public MonsterRegistry getMonsterRegistry() {
		return registry;
	}

}
