package app.tfkproject.laporsampah.user.Model;

/**
 * Created by taufik on 15/05/18.
 */

public class ItemLapor {
    String id, url_gambar, judul, nama, timestamp, jalan, latitude, longitude, status;

    public ItemLapor(String id, String url_gambar, String judul, String nama, String timestamp, String jalan, String latitude, String longitude, String status){
        this.id = id;
        this.url_gambar = url_gambar;
        this.judul = judul;
        this.nama = nama;
        this.timestamp = timestamp;
        this.jalan = jalan;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getId() {
        return id;
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

    public String getStatus() {
        return status;
    }
}
