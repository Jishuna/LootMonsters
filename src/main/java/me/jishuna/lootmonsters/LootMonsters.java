package me.jishuna.lootmonsters;

import java.io.File;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.commonlib.SimpleSemVersion;
import me.jishuna.commonlib.UpdateChecker;
import me.jishuna.commonlib.language.MessageConfig;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.commonlib.utils.ServerUtils;
import me.jishuna.lootmonsters.api.LootMonster;
import me.jishuna.lootmonsters.api.MonsterRegistry;
import me.jishuna.lootmonsters.commands.LootMonstersCommandHandler;
import me.jishuna.lootmonsters.listeners.DeathListener;
import me.jishuna.lootmonsters.listeners.SpawnListener;
import net.md_5.bungee.api.ChatColor;

public class LootMonsters extends JavaPlugin {
	private static final int BSTATS_ID = 13375;
	private static final int PLUGIN_ID = 97761;
	
	private static final String PATH = "Monsters";
	private static Logger logger;

	private YamlConfiguration configuration;
	private MessageConfig messageConfig;
	private MonsterRegistry registry;

	@Override
	public void onEnable() {
		logger = this.getLogger();
		
		this.loadConfiguration();

		this.registry = new MonsterRegistry();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new SpawnListener(this), this);
		pm.registerEvents(new DeathListener(this), this);

		// Delay 1 tick so any enchantment plugins can load
		Bukkit.getScheduler().runTask(this, this::loadMonsters);
		
		getCommand("lootmonsters").setExecutor(new LootMonstersCommandHandler(this));
		
		this.initializeMetrics();
		this.initializeUpdateChecker();
	}

	public void loadMonsters() {
		File monsterFolder = new File(this.getDataFolder(), PATH);
		if (!monsterFolder.exists()) {
			monsterFolder.mkdirs();
			FileUtils.copyDefaults(this, PATH, name -> FileUtils.loadResourceFile(this, name));
		}

		this.loadRecursively(monsterFolder);
	}

	private void loadRecursively(File folder) {
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
		

		FileUtils.loadResourceFile(this, "messages.yml")
				.ifPresent(file -> this.messageConfig = new MessageConfig(file));

		FileUtils.loadResource(this, "config.yml").ifPresent(config -> this.configuration = config);
	}
	
	public MessageConfig getMessageConfig() {
		return messageConfig;
	}

	public String getMessage(String key) {
		return this.messageConfig.getString(key);
	}

	public YamlConfiguration getConfiguration() {
		return configuration;
	}

	public MonsterRegistry getMonsterRegistry() {
		return registry;
	}

	public static Logger getPluginLogger() {
		return logger;
	}
	
	private void initializeMetrics() {
		Metrics metrics = new Metrics(this, BSTATS_ID);
		metrics.addCustomChart(new SimplePie("online_status", ServerUtils::getOnlineMode));
	}
	
	private void initializeUpdateChecker() {
		UpdateChecker checker = new UpdateChecker(this, PLUGIN_ID);
		SimpleSemVersion current = SimpleSemVersion.fromString(this.getDescription().getVersion());

		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> checker.getVersion(version -> {
			if (SimpleSemVersion.fromString(version).isNewerThan(current)) {
				ConsoleCommandSender sender = Bukkit.getConsoleSender();
				sender.sendMessage(ChatColor.GOLD + "=".repeat(70));
				sender.sendMessage(
						ChatColor.GOLD + "A new version of MineTweaks is available: " + ChatColor.DARK_AQUA + version);
				sender.sendMessage(
						ChatColor.GOLD + "Download it at https://www.spigotmc.org/resources/minetweaks.96757/");
				sender.sendMessage(ChatColor.GOLD + "=".repeat(70));
			}
		}), 0, 20l * 60 * 60);
	}

}
