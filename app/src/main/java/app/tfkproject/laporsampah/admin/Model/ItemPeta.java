package app.tfkproject.laporsampah.admin.Model;

/**
 * Created by taufik on 15/05/18.
 */

public class ItemPeta {
    String id_laporan, url_gambar, judul, nama, timestamp, latitude, longitude, jalan, status;

    public ItemPeta(String id_laporan, String url_gambar, String judul, String nama, String timestamp, String jalan, String latitude, String longitude, String status){
        this.id_laporan = id_laporan;
        this.url_gambar = url_gambar;
        this.judul = judul;
        this.nama = nama;
        this.timestamp = timestamp;
        this.jalan = jalan;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getId_laporan() {
        return id_laporan;
    }

    public String getUrl_gambar() {
        return url_gambar;
    }

    public String getJudul() {
        return judul;
    }

    public String getNama() {
        return nama;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getJalan() {
        return jalan;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

}
