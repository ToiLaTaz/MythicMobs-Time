package dev.genzstore.mythicmobstime.utils;

import dev.genzstore.mythicmobstime.MythicMobsTime;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class Logger {
   public static void info(String message) {
      Bukkit.getLogger().log(Level.INFO, "[MythicMobs-Time] " + message);
   }

   public static void warning(String message) {
      Bukkit.getLogger().log(Level.WARNING, "[MythicMobs-Time] " + message);
   }

   public static void severe(String message) {
      Bukkit.getLogger().log(Level.SEVERE, "[MythicMobs-Time] " + message);
   }

   public static void debug(String message) {
      MythicMobsTime plugin = MythicMobsTime.getInstance();
      if (plugin != null && plugin.getPluginConfig().getBoolean("debug", false)) {
         Bukkit.getLogger().log(Level.INFO, "[MythicMobs-Time-DEBUG] " + message);
      }

   }
}
