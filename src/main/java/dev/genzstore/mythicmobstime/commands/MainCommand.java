package dev.genzstore.mythicmobstime.commands;

import dev.genzstore.mythicmobstime.MythicMobsTime;
import dev.genzstore.mythicmobstime.config.BossConfig;
import dev.genzstore.mythicmobstime.utils.Utils;
import java.util.Iterator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
   private final MythicMobsTime plugin;

   public MainCommand(MythicMobsTime plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         this.sendHelp(sender);
         return true;
      } else {
         String var5 = args[0].toLowerCase();
         byte var6 = -1;
         switch(var5.hashCode()) {
         case -934641255:
            if (var5.equals("reload")) {
               var6 = 0;
            }
            break;
         case 3322014:
            if (var5.equals("list")) {
               var6 = 1;
            }
            break;
         case 97618667:
            if (var5.equals("force")) {
               var6 = 2;
            }
            break;
         case 108275:
            if (var5.equals("remove")) {
               var6 = 3;
            }
         }

         switch(var6) {
         case 0:
            if (!sender.hasPermission("mythicmobstime.admin")) {
               sender.sendMessage(Utils.color("&cBạn không có quyền sử dụng lệnh này!"));
               return true;
            }

            this.plugin.reloadPluginConfig();
            sender.sendMessage(Utils.color("&aĐã reload config thành công!"));
            return true;
         case 1:
            if (!sender.hasPermission("mythicmobstime.admin")) {
               sender.sendMessage(Utils.color("&cBạn không có quyền sử dụng lệnh này!"));
               return true;
            }

            sender.sendMessage(Utils.color("&6&lDANH SÁCH BOSS &7- &eTổng cộng: " + this.plugin.getBossManager().getBossCount()));
            Iterator var9 = this.plugin.getBossManager().getBossConfigs().keySet().iterator();

            while(var9.hasNext()) {
               String bossId = (String)var9.next();
               sender.sendMessage(Utils.color("&e" + bossId + " &7- &f" + ((BossConfig)this.plugin.getBossManager().getBossConfigs().get(bossId)).getFormattedSchedules()));
            }

            return true;
         case 2:
            if (args.length < 2) {
               sender.sendMessage(Utils.color("&cSử dụng: /mmtime force <boss_id>"));
               return true;
            } else {
               if (!sender.hasPermission("mythicmobstime.admin")) {
                  sender.sendMessage(Utils.color("&cBạn không có quyền sử dụng lệnh này!"));
                  return true;
               }

               String bossId = args[1];
               if (this.plugin.getBossManager().getBossConfigs().containsKey(bossId)) {
                  this.plugin.getBossManager().forceSpawnBoss(bossId);
                  sender.sendMessage(Utils.color("&aĐã force spawn boss: " + bossId));
               } else {
                  sender.sendMessage(Utils.color("&cKhông tìm thấy boss với ID: " + bossId));
               }

               return true;
            }
         case 3:
            if (!sender.hasPermission("mythicmobstime.admin")) {
               sender.sendMessage(Utils.color("&cBạn không có quyền sử dụng lệnh này!"));
               return true;
            }

            if (args.length < 2) {
               sender.sendMessage(Utils.color("&cSử dụng: /mmtime remove <boss_id> hoặc /mmtime remove all"));
               return true;
            }

            String removeArg = args[1];
            if ("all".equalsIgnoreCase(removeArg)) {
               this.plugin.getBossManager().cleanup();
               sender.sendMessage(Utils.color("&aĐã remove tất cả boss đang hoạt động!"));
            } else {
               if (this.plugin.getBossManager().getBossConfigs().containsKey(removeArg)) {
                  this.plugin.getBossManager().removeBoss(removeArg);
                  sender.sendMessage(Utils.color("&aĐã remove boss: " + removeArg));
               } else {
                  sender.sendMessage(Utils.color("&cKhông tìm thấy boss với ID: " + removeArg));
               }
            }

            return true;
         default:
            this.sendHelp(sender);
            return true;
         }
      }
   }

   private void sendHelp(CommandSender sender) {
      sender.sendMessage(Utils.color("&6&lMythicMobs-Time &7- &eHệ thống boss tự động"));
      sender.sendMessage(Utils.color("&e/mmtime reload &7- Reload config"));
      sender.sendMessage(Utils.color("&e/mmtime list &7- Liệt kê boss đã config"));
      sender.sendMessage(Utils.color("&e/mmtime force <boss_id> &7- Force spawn boss"));
      sender.sendMessage(Utils.color("&e/mmtime remove <boss_id> &7- Remove boss cụ thể"));
      sender.sendMessage(Utils.color("&e/mmtime remove all &7- Remove tất cả boss"));
      if (sender instanceof Player) {
         Player player = (Player)sender;
         player.sendMessage(Utils.color("&e/mmtime help &7- Hiện thị trợ giúp"));
      }

   }
}
