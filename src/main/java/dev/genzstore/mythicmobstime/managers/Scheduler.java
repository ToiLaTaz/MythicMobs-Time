package dev.genzstore.mythicmobstime.managers;

import dev.genzstore.mythicmobstime.MythicMobsTime;
import dev.genzstore.mythicmobstime.config.BossConfig;
import dev.genzstore.mythicmobstime.utils.Logger;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Scheduler {
   private final MythicMobsTime plugin;
   private final BossManager bossManager;
   private final Map<String, BukkitTask> taskIds;

   public Scheduler(MythicMobsTime plugin, BossManager bossManager) {
      this.plugin = plugin;
      this.bossManager = bossManager;
      this.taskIds = new HashMap();
   }

   public void start() {
      Map<String, BossConfig> bossConfigs = this.bossManager.getBossConfigs();
      Iterator var2 = bossConfigs.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, BossConfig> entry = (Entry)var2.next();
         this.scheduleBoss((String)entry.getKey(), (BossConfig)entry.getValue());
      }

      Logger.info("Đã khởi động scheduler với " + bossConfigs.size() + " boss");
   }

   public void stop() {
      Iterator var1 = this.taskIds.values().iterator();

      while(var1.hasNext()) {
         BukkitTask task = (BukkitTask)var1.next();
         task.cancel();
      }

      this.taskIds.clear();
   }

   public void reload() {
      this.stop();
      this.start();
   }

   private void scheduleBoss(String bossId, BossConfig config) {
      BossConfig.Schedule schedule;
      for(Iterator var3 = config.getSchedules().iterator(); var3.hasNext(); this.scheduleBossForSchedule(bossId, config, schedule)) {
         schedule = (BossConfig.Schedule)var3.next();
         long delay = this.calculateNextSpawnDelay(config, schedule);
         if (delay <= 0L) {
            this.bossManager.spawnBoss(bossId);
         }
      }

   }

   private void scheduleBossForSchedule(final String bossId, final BossConfig config, final BossConfig.Schedule schedule) {
      long delay = this.calculateNextSpawnDelay(config, schedule);
      BukkitTask task = (new BukkitRunnable() {
         public void run() {
            Scheduler.this.bossManager.spawnBoss(bossId);
            Scheduler.this.scheduleBossForSchedule(bossId, config, schedule);
         }
      }).runTaskLater(this.plugin, delay);
      this.taskIds.put(bossId + "_" + schedule.toString(), task);
      Logger.info("Đã schedule boss: " + bossId + " với lịch trình: " + schedule.toString() + " - delay: " + delay / 20L + " giây");
   }

   private long calculateNextSpawnDelay(BossConfig config, BossConfig.Schedule schedule) {
      ZoneId timezone = config.getTimezone();
      DayOfWeek targetDay = schedule.getDay();
      ZonedDateTime now = ZonedDateTime.now(timezone);
      ZonedDateTime nextSpawn = now.with(TemporalAdjusters.nextOrSame(targetDay)).withHour(schedule.getTime().getHour()).withMinute(schedule.getTime().getMinute()).withSecond(0).withNano(0);
      if (nextSpawn.isBefore(now)) {
         nextSpawn = nextSpawn.plusWeeks(1L);
      }

      return Duration.between(now, nextSpawn).getSeconds() * 20L;
   }

   public Map<String, BukkitTask> getTaskIds() {
      return new HashMap(this.taskIds);
   }
}
