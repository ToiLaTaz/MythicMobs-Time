# MythicMobs-Time

MythicMobs-Time là một plugin Minecraft tự động spawn boss theo lịch trình sử dụng MythicMobs.

## Tính năng

- ✅ Spawn boss tự động theo lịch trình
- ✅ Hỗ trợ nhiều mythicmob cho mỗi boss (chọn ngẫu nhiên)
- ✅ Tùy chọn xóa tất cả boss hoặc chỉ boss cùng loại khi spawn mới
- ✅ Hỗ trợ PlaceholderAPI
- ✅ Broadcast tin nhắn khi boss spawn
- ✅ Lệnh admin để quản lý boss

## Cài đặt

1. Cài đặt MythicMobs plugin
2. Cài đặt PlaceholderAPI plugin (tùy chọn)
3. Đặt file jar vào thư mục plugins
4. Khởi động server để tạo file config.yml
5. Cấu hình boss trong config.yml
6. Reload plugin hoặc restart server

## Cấu hình

### Cấu hình cơ bản

```yaml
boss_schedule:
  boss_id:
    mythicmob: "Boss_Name"  # hoặc ["Boss1", "Boss2", "Boss3"] cho nhiều boss
    world: "world"
    x: 100
    y: 64
    z: 200
    schedule: "Thu7-20:00"  # hoặc ["Thu7-20:00", "ChuNhat-21:00"]
    timezone: "Asia/Ho_Chi_Minh"
    title: "&4⚠ &cBOSS XUẤT HIỆN &4⚠"
    subtitle: "&6%Boss_Name% &eđã xuất hiện!"
    messages:
      - "&6[Boss] &eBoss &f%Boss_Name% &eđã xuất hiện tại &b%world%!"
    sound: "ENTITY_EXPERIENCE_ORB_PICKUP"
    priority: 1
    killAll: false  # true để xóa tất cả boss, false để chỉ xóa boss cùng loại
```

### Tùy chọn killAll

- `killAll: false` (mặc định): Chỉ xóa boss cùng loại khi spawn boss mới
- `killAll: true`: Xóa tất cả boss đang hoạt động khi spawn boss mới

### Lịch trình

- `Thu2, Thu3, Thu4, Thu5, Thu6, Thu7, ChuNhat` (Thứ 2 đến Chủ Nhật)
- Định dạng: `Ngay-Gio:Phut` (VD: `Thu7-20:00`, `ChuNhat-21:30`)

## Lệnh

- `/mmtime reload` - Reload config
- `/mmtime list` - Liệt kê boss đã config
- `/mmtime force <boss_id>` - Force spawn boss
- `/mmtime remove <boss_id>` - Xóa boss cụ thể
- `/mmtime remove all` - Xóa tất cả boss

## PlaceholderAPI

- `%mythicmobstime_boss_next_<boss_id>%` - Thời gian đến lần spawn tiếp theo
- `%mythicmobstime_boss_next_name_<boss_id>%` - Tên boss từ MythicMob
- `%mythicmobstime_boss_status_<boss_id>%` - Trạng thái boss (Alive/Dead)
- `%mythicmobstime_boss_count%` - Tổng số boss đã config

## Phiên bản

Phiên bản hiện tại: 1.1.0

## Tác giả

GenzStore
