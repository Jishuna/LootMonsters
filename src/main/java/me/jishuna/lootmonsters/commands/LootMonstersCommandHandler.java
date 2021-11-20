package me.jishuna.lootmonsters.commands;

import me.jishuna.commonlib.commands.ArgumentCommandHandler;
import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.lootmonsters.LootMonsters;

public class LootMonstersCommandHandler extends ArgumentCommandHandler {
	public LootMonstersCommandHandler(LootMonsters plugin) {
		super(plugin.getMessageConfig(), "lootmonsters.command");

		SimpleCommandHandler info = new InfoCommand(plugin);

		setDefault(info);
		addArgumentExecutor("info", info);
		addArgumentExecutor("reload", new ReloadCommand(plugin));
	}
}