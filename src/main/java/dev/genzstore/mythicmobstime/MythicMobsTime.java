package dev.genzstore.mythicmobstime;

import dev.genzstore.mythicmobstime.commands.MainCommand;
import dev.genzstore.mythicmobstime.managers.BossManager;
import dev.genzstore.mythicmobstime.managers.Scheduler;
import dev.genzstore.mythicmobstime.placeholder.BossPlaceholderExpansion;
import dev.genzstore.mythicmobstime.utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MythicMobsTime extends JavaPlugin {
   private static MythicMobsTime instance;
   private BossManager bossManager;
   private Scheduler scheduler;
   private FileConfiguration config;

   public void onEnable() {
      instance = this;
      this.saveDefaultConfig();
      this.config = this.getConfig();
      this.bossManager = new BossManager(this);
      this.scheduler = new Scheduler(this, this.bossManager);
      this.getCommand("mmtime").setExecutor(new MainCommand(this));
      if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         (new BossPlaceholderExpansion(this)).register();
         Logger.info("Đã đăng ký PlaceholderAPI expansion!");
      }

      this.scheduler.start();
      Logger.info("MythicMobs-Time đã được kích hoạt!");
      Logger.info("Đã load " + this.bossManager.getBossCount() + " boss từ config");
   }

   public void onDisable() {
      if (this.scheduler != null) {
         this.scheduler.stop();
      }

      if (this.bossManager != null) {
         this.bossManager.cleanup();
      }

      Logger.info("MythicMobs-Time đã được tắt!");
   }

   public static MythicMobsTime getInstance() {
      return instance;
   }

   public BossManager getBossManager() {
      return this.bossManager;
   }

   public Scheduler getScheduler() {
      return this.scheduler;
   }

   public FileConfiguration getPluginConfig() {
      return this.config;
   }

   public void reloadPluginConfig() {
      this.reloadConfig();
      this.config = this.getConfig();
      this.bossManager.reloadConfig();
      this.scheduler.reload();
      Logger.info("Config đã được reload!");
   }
}
