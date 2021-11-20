package me.jishuna.lootmonsters.api;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.commonlib.items.ItemBuilder;
import me.jishuna.lootmonsters.LootMonsters;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class Utils {
	private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&')
			.hexCharacter('#').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

	private static final EnumSet<Material> LEATHER_ITEMS = EnumSet.of(Material.LEATHER_HELMET,
			Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
			Material.LEATHER_HORSE_ARMOR);

	public static String colorString(String string) {
		return ChatColor.translateAlternateColorCodes('&',
				SERIALIZER.serialize(MiniMessage.miniMessage().parse(string)));
	}

	public static final ItemStack parseItem(LootMonster monster, ConfigurationSection section) {
		if (section == null)
			return null;

		String material = section.getString("type");

		if (material == null || material.isBlank())
			return null;

		String[] data = material.split(",");
		Material mat = Material.matchMaterial(data[0]);

		if (mat == null) {
			LootMonsters.getPluginLogger()
					.severe(() -> String.format("Error parsing monster %s, the provided item type %s is invalid.",
							monster.getName(), data[0]));
			return null;
		}

		int amount = section.getInt("count", 1);

		ItemBuilder builder = new ItemBuilder(mat, amount);

		parseExtra(builder, mat, data);
		parseEnchantments(builder, section);

		return builder.build();
	}

	public static PotionEffect parsePotionEffect(LootMonster monster, String effect) {
		String[] data = effect.split(",");

		if (data.length <= 1)
			return null;

		PotionEffectType type = PotionEffectType.getByName(data[0]);

		if (type == null) {
			LootMonsters.getPluginLogger()
					.severe(() -> String.format("Error parsing monster %s, the provided potion type %s is invalid.",
							monster.getName(), data[0]));
			return null;
		}

		int level = 0;

		if (StringUtils.isNumeric(data[1])) {
			level = Integer.parseInt(data[1]) - 1;
		}

		return new PotionEffect(type, Integer.MAX_VALUE, level, true, false);
	}

	private static void parseEnchantments(ItemBuilder builder, ConfigurationSection section) {
		for (String enchant : section.getStringList("enchantments")) {
			String[] data = enchant.split(",");

			if (data.length <= 1)
				continue;

			Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(data[0]));

			if (enchantment == null)
				continue;

			int level = 1;

			if (StringUtils.isNumeric(data[1])) {
				level = Integer.parseInt(data[1]);
			}

			builder.withEnchantment(enchantment, level);
		}
	}

	private static void parseExtra(ItemBuilder builder, Material type, String[] data) {
		if (type == Material.PLAYER_HEAD && data.length > 1) {
			builder.withSkullTextureUrl(data[1]);
		}

		if (type == Material.POTION && data.length >= 4) {
			int red = Integer.parseInt(data[1]);
			int green = Integer.parseInt(data[2]);
			int blue = Integer.parseInt(data[3]);

			builder.withPotionColor(red, green, blue);
		}

		if (LEATHER_ITEMS.contains(type) && data.length >= 4) {
			int red = Integer.parseInt(data[1]);
			int green = Integer.parseInt(data[2]);
			int blue = Integer.parseInt(data[3]);

			builder.withDyeColor(Color.fromRGB(red, green, blue));
		}
	}

}
