package me.jishuna.lootmonsters.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.lootmonsters.LootMonsters;
import net.md_5.bungee.api.ChatColor;

public class InfoCommand extends SimpleCommandHandler {

	private final LootMonsters plugin;

	public InfoCommand(LootMonsters plugin) {
		super("lootmonsters.command.info");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}
		PluginDescriptionFile descriptionFile = this.plugin.getDescription();
		sender.sendMessage(ChatColor.GOLD + "=".repeat(40));
		sender.sendMessage(ChatColor.GOLD + descriptionFile.getFullName() + ChatColor.GREEN + " by " + ChatColor.GOLD
				+ String.join(", ", descriptionFile.getAuthors()));
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.GOLD + "Registered Monsters: " + ChatColor.GREEN
				+ this.plugin.getMonsterRegistry().getTotalRegistered());
		sender.sendMessage(ChatColor.GOLD + "=".repeat(40));

		return true;
	}

}
