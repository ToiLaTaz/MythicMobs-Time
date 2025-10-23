package dev.genzstore.mythicmobstime.utils;

import java.time.DayOfWeek;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Utils {
   public static String color(String text) {
      return ChatColor.translateAlternateColorCodes('&', text);
   }

   public static String formatTime(long seconds) {
      long hours = seconds / 3600L;
      long minutes = seconds % 3600L / 60L;
      long secs = seconds % 60L;
      return hours > 0L ? String.format("%02d:%02d:%02d", hours, minutes, secs) : String.format("%02d:%02d", minutes, secs);
   }

   public static boolean isInteger(String str) {
      try {
         Integer.parseInt(str);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isValidWorld(String worldName) {
      return Bukkit.getWorld(worldName) != null;
   }

   public static boolean isValidLocation(String worldName, double x, double y, double z) {
      return isInteger(worldName) && x >= -3.0E7D && x <= 3.0E7D && y >= -64.0D && y <= 320.0D;
   }

   public static String capitalizeFirst(String text) {
      return text != null && !text.isEmpty() ? text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase() : text;
   }

   public static String getDayName(DayOfWeek day) {
      switch(day) {
      case MONDAY:
         return "Thứ Hai";
      case TUESDAY:
         return "Thứ Ba";
      case WEDNESDAY:
         return "Thứ Tư";
      case THURSDAY:
         return "Thứ Năm";
      case FRIDAY:
         return "Thứ Sáu";
      case SATURDAY:
         return "Thứ Bảy";
      case SUNDAY:
         return "Chủ Nhật";
      default:
         return day.toString();
      }
   }

   public static String getVietnameseDayName(DayOfWeek day) {
      switch(day) {
      case MONDAY:
         return "Thu2";
      case TUESDAY:
         return "Thu3";
      case WEDNESDAY:
         return "Thu4";
      case THURSDAY:
         return "Thu5";
      case FRIDAY:
         return "Thu6";
      case SATURDAY:
         return "Thu7";
      case SUNDAY:
         return "ChuNhat";
      default:
         return day.toString();
      }
   }
}
