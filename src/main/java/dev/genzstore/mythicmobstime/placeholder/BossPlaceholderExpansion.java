package dev.genzstore.mythicmobstime.placeholder;

import dev.genzstore.mythicmobstime.MythicMobsTime;
import dev.genzstore.mythicmobstime.config.BossConfig;
import dev.genzstore.mythicmobstime.managers.BossManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class BossPlaceholderExpansion extends PlaceholderExpansion {
   private final MythicMobsTime plugin;

   public BossPlaceholderExpansion(MythicMobsTime plugin) {
      this.plugin = plugin;
   }

   public String getIdentifier() {
      return "mythicmobstime";
   }

   public String getAuthor() {
      return "GenzStore";
   }

   public String getVersion() {
      return "1.0.0";
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player player, String identifier) {
      if (player == null) {
         return "";
      } else {
         BossManager bossManager = this.plugin.getBossManager();
         Map<String, BossConfig> bossConfigs = bossManager.getBossConfigs();
         String bossId;
         BossConfig config;
         if (identifier.startsWith("boss_next_")) {
            bossId = identifier.substring("boss_next_".length());
            config = (BossConfig)bossConfigs.get(bossId);
            return config != null ? this.calculateNextSpawnTime(config) : "Boss không tồn tại";
         } else if (identifier.startsWith("boss_next_name_")) {
            bossId = identifier.substring("boss_next_name_".length());
            config = (BossConfig)bossConfigs.get(bossId);
            return config != null ? this.getMythicMobDisplayName(config.getMythicMob()) : "Boss không tồn tại";
         } else if (identifier.startsWith("boss_status_")) {
            bossId = identifier.substring("boss_status_".length());
            config = (BossConfig)bossConfigs.get(bossId);
            if (config != null) {
               return bossManager.isBossAlive(bossId) ? "Alive" : "Dead";
            } else {
               return "Unknown";
            }
         } else {
            return identifier.equals("boss_count") ? String.valueOf(bossConfigs.size()) : null;
         }
      }
   }

   private String calculateNextSpawnTime(BossConfig config) {
      ZoneId timezone = config.getTimezone();
      ZonedDateTime now = ZonedDateTime.now(timezone);
      ZonedDateTime nextSpawn = null;
      Iterator var5 = config.getSchedules().iterator();

      while(true) {
         ZonedDateTime candidate;
         do {
            if (!var5.hasNext()) {
               if (nextSpawn != null) {
                  long hours = ChronoUnit.HOURS.between(now, nextSpawn);
                  long minutes = ChronoUnit.MINUTES.between(now, nextSpawn) % 60L;
                  if (hours > 24L) {
                     long days = hours / 24L;
                     hours %= 24L;
                     return String.format("%d ngày %d giờ %d phút", days, hours, minutes);
                  }

                  if (hours > 0L) {
                     return String.format("%d giờ %d phút", hours, minutes);
                  }

                  return String.format("%d phút", minutes);
               }

               return "Không xác định";
            }

            BossConfig.Schedule schedule = (BossConfig.Schedule)var5.next();
            DayOfWeek targetDay = schedule.getDay();
            candidate = now.with(TemporalAdjusters.nextOrSame(targetDay)).withHour(schedule.getTime().getHour()).withMinute(schedule.getTime().getMinute()).withSecond(0).withNano(0);
            if (candidate.isBefore(now) || candidate.equals(now)) {
               candidate = candidate.plusWeeks(1L);
            }
         } while(nextSpawn != null && !candidate.isBefore(nextSpawn));

         nextSpawn = candidate;
      }
   }

   private String getMythicMobDisplayName(String mythicMobId) {
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
      } catch (Exception var5) {
      }

      return mythicMobId;
   }
}
