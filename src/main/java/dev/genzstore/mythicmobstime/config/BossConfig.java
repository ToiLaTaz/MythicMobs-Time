package dev.genzstore.mythicmobstime.config;

import dev.genzstore.mythicmobstime.utils.Logger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class BossConfig {
    private final String bossId;
    private final List<String> mythicMobs;
    private final String name;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final List<BossConfig.Schedule> schedules;
    private final ZoneId timezone;
    private final String title;
    private final String subtitle;
    private final List<String> messages;
    private final Sound sound;
    private final int priority;
    private final boolean killAll;

   public BossConfig(String bossId, ConfigurationSection config) {
      this.bossId = bossId;
      List<String> tempMythicMobs;
      if (config.isList("mythicmob")) {
         tempMythicMobs = config.getStringList("mythicmob");
      } else {
         tempMythicMobs = new ArrayList();
         tempMythicMobs.add(config.getString("mythicmob", "Unknown_Mob"));
      }
      this.mythicMobs = tempMythicMobs;
      this.name = config.getString("name", this.mythicMobs.get(0));
      this.world = config.getString("world", "world");
      this.x = config.getDouble("x", 0.0D);
      this.y = config.getDouble("y", 64.0D);
      this.z = config.getDouble("z", 0.0D);
      this.timezone = ZoneId.of(config.getString("timezone", "Asia/Ho_Chi_Minh"));
      this.title = config.getString("title", "&cBOSS XUẤT HIỆN");
      this.subtitle = config.getString("subtitle", "&eBoss đã xuất hiện!");
      List<String> tempMessages = config.getStringList("messages");
      if (tempMessages == null || ((List)tempMessages).isEmpty()) {
         tempMessages = new ArrayList();
         ((List)tempMessages).add("&6[Boss] &eBoss &f%boss_name% &eđã xuất hiện tại &b%world%!");
      }

      this.messages = (List)tempMessages;
      this.sound = parseSound(config.getString("sound", "ENTITY_EXPERIENCE_ORB_PICKUP"));
      this.priority = config.getInt("priority", 1);
      this.killAll = config.getBoolean("killAll", false);
      this.schedules = new ArrayList();
      if (config.isList("schedule")) {
         List<String> scheduleList = config.getStringList("schedule");
         Iterator var5 = scheduleList.iterator();

         while(var5.hasNext()) {
            String schedule = (String)var5.next();
            this.schedules.add(new BossConfig.Schedule(schedule));
         }
      } else {
         String schedule = config.getString("schedule", "SUNDAY-00:00");
         this.schedules.add(new BossConfig.Schedule(schedule));
      }

   }

   private static DayOfWeek parseDayOfWeek(String dayStr) {
      if (dayStr == null) {
         return DayOfWeek.SUNDAY;
      } else {
         String key = dayStr.trim().toUpperCase();
         byte var3 = -1;
         switch(key.hashCode()) {
         case 2574321:
            if (key.equals("THU2")) {
               var3 = 0;
            }
            break;
         case 2574322:
            if (key.equals("THU3")) {
               var3 = 1;
            }
            break;
         case 2574323:
            if (key.equals("THU4")) {
               var3 = 2;
            }
            break;
         case 2574324:
            if (key.equals("THU5")) {
               var3 = 3;
            }
            break;
         case 2574325:
            if (key.equals("THU6")) {
               var3 = 4;
            }
            break;
         case 2574326:
            if (key.equals("THU7")) {
               var3 = 5;
            }
            break;
         case 1475397629:
            if (key.equals("CHUNHAT")) {
               var3 = 6;
            }
         }

         switch(var3) {
         case 0:
            return DayOfWeek.MONDAY;
         case 1:
            return DayOfWeek.TUESDAY;
         case 2:
            return DayOfWeek.WEDNESDAY;
         case 3:
            return DayOfWeek.THURSDAY;
         case 4:
            return DayOfWeek.FRIDAY;
         case 5:
            return DayOfWeek.SATURDAY;
         case 6:
            return DayOfWeek.SUNDAY;
         default:
            try {
               return DayOfWeek.valueOf(key);
            } catch (IllegalArgumentException var5) {
               return DayOfWeek.SUNDAY;
            }
         }
      }
   }

   private static LocalTime parseTime(String timeStr) {
      try {
         return LocalTime.parse(timeStr);
      } catch (Exception var2) {
         return LocalTime.of(20, 0);
      }
   }

   private static Sound parseSound(String soundStr) {
      if (soundStr != null && !soundStr.trim().isEmpty()) {
         try {
            return Sound.valueOf(soundStr.toUpperCase().trim());
         } catch (IllegalArgumentException var2) {
            Logger.warning("Sound '" + soundStr + "' không hợp lệ, sử dụng sound mặc định");
            return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
         }
      } else {
         return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
      }
   }

   public String getBossId() {
      return this.bossId;
   }

   public List<String> getMythicMobs() {
      return this.mythicMobs;
   }

   public String getMythicMob() {
      return this.mythicMobs.get(0);
   }

   public String getName() {
      return this.name;
   }

   public String getWorld() {
      return this.world;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public ZoneId getTimezone() {
      return this.timezone;
   }

   public String getTitle() {
      return this.title;
   }

   public String getSubtitle() {
      return this.subtitle;
   }

   public List<String> getMessages() {
      return this.messages;
   }

   public Sound getSound() {
      return this.sound;
   }

   public int getPriority() {
      return this.priority;
   }

   public boolean isKillAll() {
      return this.killAll;
   }

   public List<BossConfig.Schedule> getSchedules() {
      return this.schedules;
   }

   public boolean isScheduledForToday() {
      DayOfWeek today = ZonedDateTime.now(this.timezone).getDayOfWeek();
      Iterator var2 = this.schedules.iterator();

      BossConfig.Schedule schedule;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         schedule = (BossConfig.Schedule)var2.next();
      } while(!schedule.getDay().equals(today));

      return true;
   }

   public String getFormattedSchedules() {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < this.schedules.size(); ++i) {
         if (i > 0) {
            sb.append(", ");
         }

         sb.append(((BossConfig.Schedule)this.schedules.get(i)).toString());
      }

      return sb.toString();
   }

   public static Map<String, BossConfig> loadAll(ConfigurationSection configSection) {
      Map<String, BossConfig> configs = new HashMap();
      if (configSection != null) {
         Iterator var2 = configSection.getKeys(false).iterator();

         while(var2.hasNext()) {
            String bossId = (String)var2.next();
            ConfigurationSection bossCfg = configSection.getConfigurationSection(bossId);
            if (bossCfg != null) {
               configs.put(bossId, new BossConfig(bossId, bossCfg));
            }
         }
      }

      return configs;
   }

   public static class Schedule {
      private final DayOfWeek day;
      private final LocalTime time;

      public Schedule(String schedule) {
         String[] scheduleParts = schedule.split("-");
         this.day = BossConfig.parseDayOfWeek(scheduleParts[0]);
         this.time = BossConfig.parseTime(scheduleParts[1]);
      }

      public DayOfWeek getDay() {
         return this.day;
      }

      public LocalTime getTime() {
         return this.time;
      }

      public String toString() {
         return this.day.toString() + "-" + this.time.toString();
      }
   }
}
