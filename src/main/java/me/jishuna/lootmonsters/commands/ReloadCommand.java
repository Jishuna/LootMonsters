package me.jishuna.lootmonsters.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.lootmonsters.LootMonsters;

public class ReloadCommand extends SimpleCommandHandler {

	private final LootMonsters plugin;

	public ReloadCommand(LootMonsters plugin) {
		super("lootmonsters.command.reload");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		sender.sendMessage(this.plugin.getMessage("reload-messages"));
		this.plugin.loadConfiguration();

		sender.sendMessage(this.plugin.getMessage("reload-monsters"));
		this.plugin.getMonsterRegistry().reload();
		this.plugin.loadMonsters();

		sender.sendMessage(this.plugin.getMessage("reload-complete"));
		return true;
	}

}
