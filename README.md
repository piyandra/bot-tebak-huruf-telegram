# Telegram Bot Menfess - Tebak Huruf

Bot Telegram permainan tebak kata berbasis Spring Boot. Pemain menebak kata 5 huruf, dengan umpan balik berupa warna:
- 🟩 Hijau = huruf dan posisi benar
- 🟨 Kuning = huruf benar tapi posisi salah
- 🟥 Merah = huruf salah

## 🎮 Fitur
- Tebak kata 5 huruf (terdaftar di KBBI)
- Menyimpan poin user di database
- Bisa dimainkan di grup dan personal chat

---

## 🧾 Persyaratan

- Java 21+
- Maven
- MySQL

---

## 🤖 Setup Bot Telegram

1. Buka Telegram, cari **@BotFather**
2. Ketik `/newbot` → beri nama & username
3. Simpan token bot dari BotFather

---

## ⚙️ Konfigurasi Aplikasi

Buat file `src/main/resources/application.properties`:

```properties
bot.token=TOKEN_DARI_BOTFATHER
bot.username=USERNAME_BOT

spring.datasource.url=jdbc:mysql://localhost:3306/telegram_bot_db
spring.datasource.username=root
spring.datasource.password=passwordmysql
spring.jpa.hibernate.ddl-auto=update
