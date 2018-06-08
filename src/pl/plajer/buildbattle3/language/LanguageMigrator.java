/*
 * BuildBattle 3 - Ultimate building competition minigame
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.buildbattle3.language;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.buildbattle3.Main;
import pl.plajer.buildbattle3.handlers.ConfigurationManager;
import pl.plajer.buildbattle3.utils.MessageUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 31.05.2018
 */
public class LanguageMigrator {

    public static final int LANGUAGE_FILE_VERSION = 1;
    public static final int CONFIG_FILE_VERSION = 2;

    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    private static List<String> migratable = Arrays.asList("bungee", "config", "language", "MySQL");

    public static void migrateToNewFormat() {
        MessageUtils.gonnaMigrate();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Build Battle 3 is migrating all files to the new file format...");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Don't worry! Old files will be renamed not overridden!");
        for(String file : migratable) {
            if(ConfigurationManager.getFile(file).exists()) {
                ConfigurationManager.getFile(file).renameTo(new File(plugin.getDataFolder(), "BB2_" + file + ".yml"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Renamed file " + file + ".yml");
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Done! Enabling BB2...");
    }

    public static void configUpdate(){
        if(plugin.getConfig().getInt("Version") == CONFIG_FILE_VERSION) return;
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[BuildBattle] System notify >> Your config file is outdated! Updating...");
        File file = new File(plugin.getDataFolder() + "/config.yml");

        LanguageMigrator.removeLineFromFile(file, "# Don't modify.");
        LanguageMigrator.removeLineFromFile(file, "Version: " + plugin.getConfig().getInt("Version"));
        LanguageMigrator.removeLineFromFile(file, "# No way! You've reached the end! But... where's the dragon!?");
        switch(plugin.getConfig().getInt("Version")) {
            case 0:
                LanguageMigrator.addNewLines(file, "# Should blocks behind game signs change their color based on game state?\r\n# They will change color to:\r\n" +
                        "# - white (waiting for players) stained glass\r\n# - yellow (starting) stained glass\r\n# - orange (in game) stained glass\r\n# - gray (ending) stained glass\r\n" +
                        "# - black (restarting) stained glass\r\nSigns-Block-States-Enabled: true\r\n\r\n" +
                        "# Don't modify\r\nVersion: 1\r\n\r\n# No way! You've reached the end! But... where's the dragon!?");
                break;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] System notify >> Config updated, no comments were removed :)");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] System notify >> You're using latest config file version! Nice!");
    }

    public static void languageFileUpdate() {
        if(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit").equals(String.valueOf(LANGUAGE_FILE_VERSION))) return;
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[BuildBattle] System notify >> Your language file is outdated! Updating...");

        int version = Integer.valueOf(LanguageManager.getDefaultLanguageMessage("File-Version-Do-Not-Edit"));
        LanguageMigrator.updateLanguageVersionControl(version);

        File file = new File(plugin.getDataFolder() + "/language.yml");

        switch(version) {
            case 0:
                LanguageMigrator.insertAfterLine(file, "Arena-Started", "  Wait-For-Start: \"&cYou must wait for arena start!\"");
                LanguageMigrator.insertAfterLine(file, "No-Arena-Like-That", "  No-Playing: \"&cYou're not playing!\"");
                LanguageMigrator.insertAfterLine(file, "Admin-Messages:", "      Changed-Theme: \"&bAdmin has changed theme to %THEME%\"");
                LanguageMigrator.insertAfterLine(file, "Particles-Placed:", "  Main-Command:\r\n    Header: \"&6----------------{BuildBattle commands}----------\"\r\n" +
                        "    Description: \"&aGame commands:\\n\r\n    &b/bb stats: &7Shows your stats!\\n\r\n    &b/bb leave: &7Quits current arena!\\n\r\n" +
                        "    &b/bb join <arena>: &7Joins specified arena!\"\r\n    Admin-Bonus-Description: \"\\n&b/bba help: &7Shows all the admin commands\"\r\n" +
                        "    Footer: \"&6-------------------------------------------------\"");
                break;
            case 1:
                LanguageMigrator.insertAfterLine(file, "Particles-Placed:", "  Main-Command:\r\n    Header: \"&6----------------{BuildBattle commands}----------\"\r\n" +
                        "    Description: \"&aGame commands:\\n\r\n    &b/bb stats: &7Shows your stats!\\n\r\n    &b/bb leave: &7Quits current arena!\\n\r\n" +
                        "    &b/bb join <arena>: &7Joins specified arena!\"\r\n    Admin-Bonus-Description: \"\\n&b/bba help: &7Shows all the admin commands\"\r\n" +
                        "    Footer: \"&6-------------------------------------------------\"");
                break;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] System notify >> Language file updated! Nice!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[BuildBattle] System notify >> You're using latest language file version! Nice!");
    }

    private static void removeLineFromFile(File file, String lineToRemove) {
        try {
            List<String> lines = FileUtils.readLines(file);
            List<String> updatedLines = lines.stream().filter(s -> !s.contains(lineToRemove)).collect(Collectors.toList());
            FileUtils.writeLines(file, updatedLines, false);
        } catch(IOException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Something went horribly wrong with migration! Please contact author!");
        }
    }

    private static void insertAfterLine(File file, String search, String text) {
        try {
            int i = 1;
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for(String line : lines) {
                if(line.contains(search)) {
                    lines.add(i, text);
                    Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
                    break;
                }
                i++;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateLanguageVersionControl(int oldVersion) {
        File file = new File(plugin.getDataFolder() + "/language.yml");
        LanguageMigrator.removeLineFromFile(file, "# Do not modify!");
        LanguageMigrator.removeLineFromFile(file, "File-Version-Do-Not-Edit: " + oldVersion);
        LanguageMigrator.addNewLines(file, "# Do not modify!\nFile-Version-Do-Not-Edit: " + LANGUAGE_FILE_VERSION);
    }

    private static void addNewLines(File file, String newLines) {
        try {
            FileWriter fw = new FileWriter(file.getPath(), true);
            fw.write(newLines);
            fw.close();
        } catch(IOException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Something went horribly wrong with migration! Please contact author!");
        }
    }

}
