package dev.genzstore.mythicmobstime.managers;

import dev.genzstore.mythicmobstime.MythicMobsTime;
import dev.genzstore.mythicmobstime.config.BossConfig;
import dev.genzstore.mythicmobstime.core.BossSpawner;
import dev.genzstore.mythicmobstime.utils.Logger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class BossManager {
   private final MythicMobsTime plugin;
   private Map<String, BossConfig> bossConfigs;
   private final Map<String, UUID> currentBosses;
   private final Map<String, Long> lastSpawnTime;
   private static final long SPAWN_COOLDOWN_MS = 5000L;

   public BossManager(MythicMobsTime plugin) {
      this.plugin = plugin;
      this.bossConfigs = new HashMap();
      this.currentBosses = new HashMap();
      this.lastSpawnTime = new ConcurrentHashMap();
      this.loadConfig();
      this.startCleanupTask();
   }

   public void loadConfig() {
      ConfigurationSection bossSection = this.plugin.getPluginConfig().getConfigurationSection("boss_schedule");
      this.bossConfigs = BossConfig.loadAll(bossSection);
   }

   public void reloadConfig() {
      this.bossConfigs.clear();
      this.currentBosses.clear();
      this.lastSpawnTime.clear();
      this.loadConfig();
   }

   public void spawnBoss(String bossId) {
      BossConfig config = (BossConfig)this.bossConfigs.get(bossId);
      if (config == null) {
         Logger.warning("Boss config không t t tồn tại: " + bossId);
      } else {
         long currentTime = System.currentTimeMillis();
         Long lastSpawn = (Long)this.lastSpawnTime.get(bossId);
         if (lastSpawn != null && currentTime - lastSpawn < 5000L) {
            Logger.debug("Boss " + bossId + " đang trong cooldown spawn, bỏ qua");
         } else if (this.isBossAlive(bossId)) {
            Logger.info("Boss " + bossId + " vẫn còn sống, bỏ qua spawn");
         } else {
            BossSpawner.removeOldBoss(config);
            UUID oldBossUUID = (UUID)this.currentBosses.remove(bossId);
            if (oldBossUUID != null) {
               Entity oldEntity = this.plugin.getServer().getEntity(oldBossUUID);
               if (oldEntity != null && !oldEntity.isDead()) {
                  oldEntity.remove();
                  Logger.info("Đã force remove boss cũ từ tracking: " + bossId);
               }
            }

            ActiveMob activeMob = BossSpawner.spawnBoss(config);
            if (activeMob != null) {
               this.currentBosses.put(bossId, activeMob.getEntity().getUniqueId());
               this.lastSpawnTime.put(bossId, currentTime);
               Logger.info("Boss " + bossId + " đã được spawn thành công");
            } else {
               Logger.warning("Không thể spawn boss: " + bossId);
            }

         }
      }
   }

   public void forceSpawnBoss(String bossId) {
      BossConfig config = (BossConfig)this.bossConfigs.get(bossId);
      if (config == null) {
         Logger.warning("Boss config không t t tồn tại: " + bossId);
      } else {
         BossSpawner.removeOldBoss(config);
         UUID oldBossUUID = (UUID)this.currentBosses.remove(bossId);
         if (oldBossUUID != null) {
            Entity oldEntity = this.plugin.getServer().getEntity(oldBossUUID);
            if (oldEntity != null && !oldEntity.isDead()) {
               oldEntity.remove();
               Logger.info("Đã force remove boss cũ từ tracking: " + bossId);
            }
         }

         ActiveMob activeMob = BossSpawner.spawnBoss(config);
         if (activeMob != null) {
            this.currentBosses.put(bossId, activeMob.getEntity().getUniqueId());
            this.lastSpawnTime.put(bossId, System.currentTimeMillis());
            Logger.info("Boss " + bossId + " đã được force spawn thành công");
         } else {
            Logger.warning("Không thể force spawn boss: " + bossId);
         }

      }
   }

   public void removeBoss(String bossId) {
      UUID bossUUID = (UUID)this.currentBosses.remove(bossId);
      if (bossUUID != null) {
         Entity entity = this.plugin.getServer().getEntity(bossUUID);
         if (entity != null) {
            entity.remove();
            Logger.info("Đã force remove boss: " + bossId);
         }
      }

   }

   public boolean isBossAlive(String bossId) {
      return BossSpawner.isBossAlive((BossConfig)this.bossConfigs.get(bossId));
   }

   public Map<String, BossConfig> getBossConfigs() {
      return new HashMap(this.bossConfigs);
   }

   public int getBossCount() {
      return this.bossConfigs.size();
   }

   public Map<String, UUID> getCurrentBosses() {
      return new HashMap(this.currentBosses);
   }

   public void cleanup() {
      Iterator var1 = this.currentBosses.keySet().iterator();

      while(var1.hasNext()) {
         String bossId = (String)var1.next();
         this.removeBoss(bossId);
      }

      this.currentBosses.clear();
   }

   private void startCleanupTask() {
      if (this.plugin.getPluginConfig().getBoolean("cleanup.enabled", true)) {
         int interval = this.plugin.getPluginConfig().getInt("cleanup.check_interval", 300);
         (new BukkitRunnable() {
            public void run() {
               Iterator var1 = BossManager.this.bossConfigs.keySet().iterator();

               while(var1.hasNext()) {
                  String bossId = (String)var1.next();
                  if (!BossManager.this.isBossAlive(bossId)) {
                     BossManager.this.currentBosses.remove(bossId);
                  }
               }

            }
         }).runTaskTimer(this.plugin, (long)interval * 20L, (long)interval * 20L);
         Logger.info("Đã khởi động cleanup task với interval: " + interval + " giây");
      }
   }
}
