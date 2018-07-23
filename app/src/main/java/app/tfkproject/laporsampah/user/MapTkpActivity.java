package app.tfkproject.laporsampah.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import app.tfkproject.laporsampah.R;

public class MapTkpActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "Lokasi";

    Location lastLocation;
    Marker currLocationMarker;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    LatLng lokasi_asal;
    LatLng lokasi_tujuan;

    //TextView ShowDistanceDuration;
    Polyline line;
    String tipe_rute;
    private int PROXIMITY_RADIUS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tkp);

        getSupportActionBar().setTitle("Lokasi Sampah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //cek permission di android M
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final String link_img = getIntent().getStringExtra("key_link_img");
        final String nama_objek = getIntent().getStringExtra("key_nama_tujuan");
        final String lokasi_objek = getIntent().getStringExtra("key_lokasi_tujuan");
        final String lokasi_lat = getIntent().getStringExtra("key_lat_tujuan");
        final String lokasi_long = getIntent().getStringExtra("key_long_tujuan");

        double lok_lat = Double.valueOf(lokasi_lat);
        double lok_long= Double.valueOf(lokasi_long);

        // tambahkan marker dan aktifkan pindah kamera
        lokasi_tujuan = new LatLng(lok_lat, lok_long);
        //lokasi_tujuan = new LatLng(1.660664, 101.437487);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(lokasi_tujuan);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        /*mMap.addMarker(new MarkerOptions()
                .position(lokasi_tujuan)
                .title(nama_objek)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow();*/

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.maps_info_window, null);

                ImageView img = v.findViewById(R.id.img);
                Glide.with(MapTkpActivity.this).load(link_img).asBitmap().override(250,250).listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if(!isFromMemoryCache) marker.showInfoWindow();
                        return false;
                    }
                }).into(img);

                TextView nama = v.findViewById(R.id.txt_nama);
                nama.setText(nama_objek);

                TextView jalan = v.findViewById(R.id.txt_jalan);
                jalan.setText("Lokasi: "+lokasi_objek);

                TextView lat = v.findViewById(R.id.txt_lat);
                lat.setText("Lat: "+lokasi_lat);

                TextView lon = v.findViewById(R.id.txt_long);
                lon.setText("Lon: "+lokasi_long);
                return  v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }


        });
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasi_tujuan));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
