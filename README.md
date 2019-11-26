# Web Service Bank

Web service Bank diimplementasikan di atas java servlet menggunakan JAX-WS dengan protokol SOAP. Web service ini digunakan untuk Aplikasi Bank Pro dan Engima.

Web service Bank memiliki basis data terpisah dari basis data Engima yang terdiri dari data nasabah dan transaksi rekening setiap bank (lihat daftar bank di Aplikasi Bank) Bank Pro. Informasi rekening yang disimpan adalah nama pemilik, nomor rekening, dan nomor akun virtual (jika ada). Nomor akun virtual untuk satu rekening bisa lebih dari satu. Informasi transaksi yang disimpan adalah id nasabah, jenis transaksi (debit/kredit), jumlah transaksi, nomor yang terkait (rekening atau akun virtual, jika ada), dan waktu transaksi.

Layanan yang disediakan oleh web service ini adalah:
1. Validasi nomor rekening. Jika nomor rekening terdaftar di basis data, maka nomor tersebut valid.
2. Memberikan data rekening seorang nasabah. Data pengguna meliputi nama pengguna, nomor kartu nomor rekening, saldo terakhir, dan riwayat transaksi (debit dan kredit).
3. Melakukan transaksi transfer dengan input nomor rekening pengirim, nomor rekening/akun virtual penerima, dan jumlah uang yang ditransfer. Layanan mengembalikan status transfer (berhasil/gagal). Transfer berhasil jika:
4. Nomor rekening atau akun virtual tujuan valid
5. Saldo rekening mencukupi untuk transaksi
6. Jika transfer berhasil, akan dicatat transaksi debit pada rekening pengirim dan transaksi kredit pada rekening penerima.
7. Membuat akun virtual untuk suatu nomor rekening. Layanan mengembalikan nomor unik akun virtual tersebut.
8. Mengecek ada atau tidak sebuah transaksi kredit dalam suatu rentang waktu. Input yang diterima adalah nomor rekening atau akun virtual tujuan, jumlah nominal yang diharapkan

## Basis Data yang Digunakan: MySQL

</br>

## Pembagian Tugas DPPL
1. CI/CD: 13517089
2. Eksplorasi dan setup mesin deployment: 13517059, 13517069, 13517134

</br>

## URL Deployment
Halaman Engima: 100.26.106.0/engima

Halaman Bank Pro: 100.26.106.0:5000

URL WS Bank: 
100.26.106.0:8080/ws-bank-1.0-SNAPSHOT/services/AccountService
100.26.106.0:8080/ws-bank-1.0-SNAPSHOT/services/TransactionService