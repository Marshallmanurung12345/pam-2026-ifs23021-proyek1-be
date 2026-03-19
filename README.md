# pam-2026-p5-laundry-be

Backend API untuk Aplikasi Manajemen Laundry, dibangun menggunakan [Ktor](https://ktor.io/).

## Fitur

- **Autentikasi** — Register, Login, Refresh Token, Logout (JWT)
- **Manajemen Akun** — Lihat profil, ubah profil, ubah password, ubah foto
- **Layanan Laundry** — CRUD jenis layanan (cuci kiloan, dry clean, dll.) + upload gambar + filter aktif/nonaktif + pencarian
- **Pesanan Laundry** — CRUD pesanan + update status + pagination (infinite scroll) + filter status + pencarian nama pelanggan

## Struktur Endpoint

| Method | Endpoint | Keterangan |
|--------|----------|------------|
| POST | `/auth/register` | Daftar akun baru |
| POST | `/auth/login` | Login |
| POST | `/auth/refresh-token` | Perbarui token |
| POST | `/auth/logout` | Logout |
| GET | `/users/me` | Info akun saya |
| PUT | `/users/me` | Ubah profil |
| PUT | `/users/me/password` | Ubah kata sandi |
| PUT | `/users/me/photo` | Ubah foto profil |
| GET | `/images/users/{id}` | Ambil foto user |
| GET | `/laundry-services` | Daftar layanan (`?search=&isActive=`) |
| POST | `/laundry-services` | Tambah layanan |
| GET | `/laundry-services/{id}` | Detail layanan |
| PUT | `/laundry-services/{id}` | Ubah layanan |
| PUT | `/laundry-services/{id}/image` | Ubah gambar layanan |
| DELETE | `/laundry-services/{id}` | Hapus layanan |
| GET | `/images/laundry-services/{id}` | Ambil gambar layanan |
| GET | `/laundry-orders` | Daftar pesanan (`?search=&status=&page=&limit=`) |
| POST | `/laundry-orders` | Tambah pesanan |
| GET | `/laundry-orders/{id}` | Detail pesanan |
| PUT | `/laundry-orders/{id}` | Ubah pesanan |
| PUT | `/laundry-orders/{id}/status` | Ubah status pesanan |
| DELETE | `/laundry-orders/{id}` | Hapus pesanan |

## Status Pesanan

| Status | Keterangan |
|--------|------------|
| `pending` | Menunggu konfirmasi |
| `processing` | Sedang diproses |
| `done` | Selesai dicuci |
| `delivered` | Sudah diambil/dikirim |
| `cancelled` | Dibatalkan |

## Setup

### 1. Buat database PostgreSQL

```sql
CREATE DATABASE db_laundry;
```

Lalu jalankan `data.sql` untuk membuat tabel:

```bash
psql -U postgres -d db_laundry -f data.sql
```

### 2. Buat file `.env`

Salin dari `.env.example`:

```bash
cp .env.example .env
```

Isi sesuai konfigurasi lokal Anda.

### 3. Jalankan server

```bash
./gradlew run
```

Server berjalan di `http://localhost:8000`

## Build & Run

| Task | Keterangan |
|------|------------|
| `./gradlew run` | Jalankan server |
| `./gradlew build` | Build project |
| `./gradlew buildFatJar` | Build JAR dengan semua dependensi |
| `./gradlew test` | Jalankan tests |
