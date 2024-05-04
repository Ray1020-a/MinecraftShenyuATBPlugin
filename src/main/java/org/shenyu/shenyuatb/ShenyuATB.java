package org.shenyu.shenyuatb;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public final class ShenyuATB extends JavaPlugin implements Listener{

    private static Economy econ = null;
    private static Permission perms = null;
    private FileConfiguration config;
    private File configFile;

    private String datamoney;
    private String currency;
    private int cmoney;

    private final List<String> specialAchievements = Arrays.asList(
            "story/root",
            "story/mine_stone",
            "story/upgrade_tools",
            "story/smelt_iron",
            "story/obtain_armor",
            "story/lava_bucket",
            "story/iron_tools",
            "story/deflect_arrow",
            "story/form_obsidian",
            "story/mine_diamond",
            "story/enter_the_nether",
            "story/shiny_gear",
            "story/enchant_item",
            "story/cure_zombie_villager",
            "story/follow_ender_eye",
            "story/enter_the_end",
            "nether/root",
            "nether/return_to_sender",
            "nether/find_bastion",
            "nether/obtain_ancient_debris",
            "nether/find_fortress",
            "nether/fast_travel",
            "nether/obtain_crying_obsidian",
            "nether/distract_piglin",
            "nether/use_lodestone",
            "nether/uneasy_alliance",
            "nether/ride_strider",
            "nether/loot_bastion",
            "nether/netherite_armor",
            "nether/get_wither_skull",
            "nether/obtain_blaze_rod",
            "nether/charge_respawn_anchor",
            "nether/ride_strider_in_overworld_lava",
            "nether/explore_nether",
            "nether/summon_wither",
            "nether/brew_potion",
            "nether/create_beacon",
            "nether/all_potions",
            "nether/create_full_beacon",
            "nether/all_effects",
            "end/root",
            "end/kill_dragon",
            "end/dragon_egg",
            "end/enter_end_gateway",
            "end/respawn_dragon",
            "end/dragon_breath",
            "end/find_end_city",
            "end/elytra",
            "end/levitate",
            "adventure/root",
            "adventure/voluntary_exile",
            "adventure/spyglass_at_parrot",
            "adventure/kill_a_mob",
            "adventure/read_power_from_chiseled_bookshelf",
            "adventure/trade",
            "adventure/trim_with_any_armor_pattern",
            "adventure/honey_block_slide",
            "adventure/ol_betsy",
            "adventure/lightning_rod_with_villager_no_fire",
            "adventure/fall_from_world_height",
            "adventure/salvage_sherd",
            "adventure/avoid_vibration",
            "adventure/sleep_in_bed",
            "adventure/hero_of_the_village",
            "adventure/spyglass_at_ghast",
            "adventure/throw_trident",
            "adventure/kill_mob_near_sculk_catalyst",
            "adventure/shoot_arrow",
            "adventure/kill_all_mobs",
            "adventure/totem_of_undying",
            "adventure/summon_iron_golem",
            "adventure/trade_at_world_height",
            "adventure/trim_with_all_exclusive_armor_patterns",
            "adventure/two_birds_one_arrow",
            "adventure/whos_the_pillager_now",
            "adventure/arbalistic",
            "adventure/craft_decorated_pot_using_only_sherds",
            "adventure/adventuring_time",
            "adventure/play_jukebox_in_meadows",
            "adventure/walk_on_powder_snow_with_leather_boots",
            "adventure/spyglass_at_dragon",
            "adventure/very_very_frightening",
            "adventure/sniper_duel",
            "adventure/bullseye",
            "husbandry/root",
            "husbandry/safely_harvest_honey",
            "husbandry/breed_an_animal",
            "husbandry/allay_deliver_item_to_player",
            "husbandry/ride_a_boat_with_a_goat",
            "husbandry/tame_an_animal",
            "husbandry/make_a_sign_glow",
            "husbandry/fishy_business",
            "husbandry/silk_touch_nest",
            "husbandry/tadpole_in_a_bucket",
            "husbandry/obtain_sniffer_egg",
            "husbandry/plant_seed",
            "husbandry/wax_on",
            "husbandry/bred_all_animals",
            "husbandry/allay_deliver_cake_to_note_block",
            "husbandry/complete_catalogue",
            "husbandry/tactical_fishing",
            "husbandry/leash_all_frog_variants",
            "husbandry/feed_snifflet",
            "husbandry/balanced_diet",
            "husbandry/obtain_netherite_hoe",
            "husbandry/wax_off",
            "husbandry/axolotl_in_a_bucket",
            "husbandry/froglights",
            "husbandry/plant_any_sniffer_seed",
            "husbandry/kill_axolotl_target"
    );

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().info("找不到可配合的經濟插件!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        setupPermissions();
        initConfigFile();
        datamoney = config.getString("money");
        currency = config.getString("currency");
        cmoney = Integer.parseInt(datamoney);
        getLogger().info("成就獎金插件已啟用!");
    }

    private void initConfigFile() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getDataFolder().mkdirs();
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public void onDisable() {
        if (config != null) {
            try {
                config.save(configFile);
            } catch (IOException e) {
                getLogger().warning("Could not save config.yml!");
            }
        } else {
            getLogger().warning("Config is null! Unable to save config.yml.");
        }
        getLogger().info(String.format("成就獎金插件經濟系統關閉 %s", getDescription().getVersion()));
        getLogger().info("成就獎金插件已停用!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public void addMoney(Player player, double amount) {
        econ.depositPlayer(player, amount);
    }


    @EventHandler
    public void onPlayerAchievement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        if (advancement != null) {
            String advancementId = advancement.getKey().getKey();
            Player ecoplayer = event.getPlayer();
            if (specialAchievements.contains(advancementId)) {
                String playerName = event.getPlayer().getName();
                addMoney(ecoplayer, cmoney);
                String message = "§7[§6ShenyuATB§7]§e" + playerName + " §f您解鎖了一個成就，您已獲得獎勵金§9" + cmoney +"§f元" + currency;
                event.getPlayer().sendMessage(message);
            }
        }
    }
}
