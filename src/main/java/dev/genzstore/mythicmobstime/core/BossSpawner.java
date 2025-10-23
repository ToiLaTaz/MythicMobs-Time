package dev.genzstore.mythicmobstime.core;

import dev.genzstore.mythicmobstime.MythicMobsTime;
import dev.genzstore.mythicmobstime.config.BossConfig;
import dev.genzstore.mythicmobstime.utils.Logger;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BossSpawner {
   public static ActiveMob spawnBoss(BossConfig config) {
      try {
         World world = Bukkit.getWorld(config.getWorld());
         if (world == null) {
            Logger.warning("World '" + config.getWorld() + "' không t tồn tại cho boss: " + config.getBossId());
            return null;
         }

         Location spawnLocation = new Location(world, config.getX(), config.getY(), config.getZ());
         String selectedMythicMob = selectRandomMythicMob(config);
         if (selectedMythicMob == null) {
            Logger.warning("Không có MythicMob hợp lệ nào trong danh sách cho boss: " + config.getBossId());
            return null;
         }

         MythicMob mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(selectedMythicMob).orElse(null);
         if (mythicMob == null) {
            Logger.warning("MythicMob '" + selectedMythicMob + "' không t tồn tại!");
            return null;
         }

         if (config.isKillAll()) {
            removeAllBosses();
         } else {
            BossSpawner.removeOldBoss(config);
         }

         ActiveMob activeMob = MythicBukkit.inst().getMobManager().spawnMob(selectedMythicMob, spawnLocation);
         if (activeMob != null) {
            Logger.info("Đã spawn boss: " + config.getBossId() + " (" + selectedMythicMob + ") tại " + spawnLocation);
            broadcastBossSpawn(config, selectedMythicMob);
            return activeMob;
         }
      } catch (Exception var5) {
         Logger.severe("Lỗi khi spawn boss: " + config.getBossId());
         var5.printStackTrace();
      }

      return null;
   }

   public static void broadcastBossSpawn(BossConfig config, String selectedMythicMob) {
      String bossName = getMythicMobDisplayName(selectedMythicMob);
      String worldName = config.getWorld();
      Iterator var3 = Bukkit.getOnlinePlayers().iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();

         String message;
         try {
            String title = config.getTitle().replace('&', '§').replace("%boss_name%", bossName).replace("%world%", worldName);
            message = config.getSubtitle().replace('&', '§').replace("%boss_name%", bossName).replace("%world%", worldName);
            title = processPlaceholders(player, title);
            message = processPlaceholders(player, message);
            int fadeIn = 10;
            int stay = 70;
            int fadeOut = 20;

            try {
               ConfigurationSection broadcastSection = MythicMobsTime.getInstance().getPluginConfig().getConfigurationSection("broadcast.title_duration");
               if (broadcastSection != null) {
                  fadeIn = broadcastSection.getInt("fade_in", 10);
                  stay = broadcastSection.getInt("stay", 70);
                  fadeOut = broadcastSection.getInt("fade_out", 20);
               }
            } catch (Exception var14) {
               Logger.debug("Sử dụng title duration mặc định: " + var14.getMessage());
            }

            player.sendTitle(title, message, fadeIn, stay, fadeOut);
         } catch (Exception var15) {
            Logger.warning("Không thể gửi title cho player " + player.getName() + ": " + var15.getMessage());
         }

         Iterator var16 = config.getMessages().iterator();

         while(var16.hasNext()) {
            message = (String)var16.next();

            try {
               String formattedMessage = message.replace('&', '§').replace("%boss_name%", bossName).replace("%world%", worldName).replace("%x%", String.valueOf(config.getX())).replace("%y%", String.valueOf(config.getY())).replace("%z%", String.valueOf(config.getZ()));
               formattedMessage = processPlaceholders(player, formattedMessage);
               player.sendMessage(formattedMessage);
            } catch (Exception var13) {
               Logger.warning("Không thể gửi message cho player " + player.getName() + ": " + var13.getMessage());
            }
         }

         try {
            player.playSound(player.getLocation(), config.getSound(), 1.0F, 1.0F);
         } catch (Exception var12) {
            Logger.warning("Không thể phát sound cho player " + player.getName() + ": " + var12.getMessage());

            try {
               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            } catch (Exception var11) {
               Logger.warning("Không thể phát sound fallback: " + var11.getMessage());
            }
         }
      }

      Logger.info("Đã thông báo boss spawn: " + config.getBossId());
   }

   private static String processPlaceholders(Player player, String message) {
      String processed = message.replace("%player_name%", player.getName());
      processed = processed.replace("%player_displayname%", player.getDisplayName());
      processed = processed.replace("%player_world%", player.getWorld().getName());
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         try {
            processed = PlaceholderAPI.setPlaceholders(player, processed);
         } catch (Exception var4) {
            Logger.warning("Lỗi khi xử lý PlaceholderAPI cho player " + player.getName() + ": " + var4.getMessage());
         }
      }

      return processed;
   }

   private static String selectRandomMythicMob(BossConfig config) {
      List<String> mythicMobs = config.getMythicMobs();
      if (mythicMobs.isEmpty()) {
         return null;
      }
      if (mythicMobs.size() == 1) {
         return mythicMobs.get(0);
      }
      // Chọn ngẫu nhiên từ danh sách
      int randomIndex = (int)(Math.random() * mythicMobs.size());
      return mythicMobs.get(randomIndex);
   }

   private static String getMythicMobDisplayName(String mythicMobId) {
      try {
         MythicMob mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(mythicMobId).orElse(null);
         if (mythicMob != null) {
            PlaceholderString displayNamePlaceholder = mythicMob.getDisplayName();
            if (displayNamePlaceholder != null) {
               String displayName = displayNamePlaceholder.get();
               if (displayName != null && !displayName.isEmpty()) {
                  return displayName;
               }
            }

            return mythicMob.getInternalName();
         }
      } catch (Exception var4) {
      }

      return mythicMobId;
   }

   public static void removeOldBoss(BossConfig config) {
      try {
         // Remove tất cả mythicmob trong danh sách
         for (String mythicMob : config.getMythicMobs()) {
            String command = "mm mobs kill " + mythicMob;
            boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            if (success) {
               Logger.info("Đã thực thi lệnh remove boss: " + command + " cho " + config.getBossId());
            } else {
               Logger.warning("Không thể thực thi lệnh remove boss: " + command);
            }
         }
         removeOldBossManually(config);
      } catch (Exception var3) {
         Logger.warning("Lỗi khi remove boss bằng lệnh, thử phương pháp thủ công: " + var3.getMessage());
         removeOldBossManually(config);
      }

   }

   public static void removeAllBosses() {
      try {
         String command = "mm mobs killall";
         boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
         if (success) {
            Logger.info("Đã thực thi lệnh remove all bosses: " + command);
         } else {
            Logger.warning("Không thể thực thi lệnh remove all bosses: " + command);
            removeAllBossesManually();
         }
      } catch (Exception var2) {
         Logger.warning("Lỗi khi remove all bosses bằng lệnh, thử phương pháp thủ công: " + var2.getMessage());
         removeAllBossesManually();
      }

   }

   private static void removeOldBossManually(BossConfig config) {
      World world = Bukkit.getWorld(config.getWorld());
      if (world == null) {
         Logger.warning("World '" + config.getWorld() + "' không tồn tại để remove boss");
      } else {
         Collection<Entity> entities = world.getEntities();
         int removedCount = 0;
         Iterator var4 = entities.iterator();

         while(var4.hasNext()) {
            Entity entity = (Entity)var4.next();
            if (entity != null && !entity.isDead()) {
               try {
                  ActiveMob activeMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
                  if (activeMob != null) {
                     String mobType = activeMob.getMobType();
                     if (mobType != null && config.getMythicMobs().contains(mobType)) {
                        entity.remove();
                        ++removedCount;
                        Logger.debug("Đã remove boss entity: " + entity.getUniqueId() + " (" + mobType + ")");
                     }
                  }
               } catch (Exception var8) {
                  Logger.warning("Lỗi khi kiểm tra entity " + entity.getUniqueId() + ": " + var8.getMessage());
               }
            }
         }

         if (removedCount > 0) {
            Logger.info("Đã remove thủ công " + removedCount + " boss cũ: " + config.getBossId() + " (" + config.getMythicMobs() + ")");
         } else {
            Logger.debug("Không tìm thấy boss cũ để remove: " + config.getBossId());
         }

      }
   }

   public static boolean isBossAlive(BossConfig config) {
      World world = Bukkit.getWorld(config.getWorld());
      if (world == null) {
         return false;
      } else {
         Collection<Entity> entities = world.getEntities();
         Iterator var3 = entities.iterator();

         while(var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            ActiveMob activeMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
            if (activeMob != null) {
               String mobType = activeMob.getMobType();
               if (mobType != null && config.getMythicMobs().contains(mobType)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static void removeAllBossesManually() {
      for (World world : Bukkit.getWorlds()) {
         Collection<Entity> entities = world.getEntities();
         int removedCount = 0;
         Iterator var4 = entities.iterator();

         while(var4.hasNext()) {
            Entity entity = (Entity)var4.next();
            if (entity != null && !entity.isDead()) {
               try {
                  ActiveMob activeMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
                  if (activeMob != null) {
                     entity.remove();
                     ++removedCount;
                     Logger.debug("Đã remove boss entity: " + entity.getUniqueId() + " (" + activeMob.getMobType() + ")");
                  }
               } catch (Exception var8) {
                  Logger.warning("Lỗi khi kiểm tra entity " + entity.getUniqueId() + ": " + var8.getMessage());
               }
            }
         }

         if (removedCount > 0) {
            Logger.info("Đã remove thủ công " + removedCount + " boss entities trong world " + world.getName());
         }
      }

   }
}
