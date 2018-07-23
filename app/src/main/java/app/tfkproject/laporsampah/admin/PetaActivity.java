package app.tfkproject.laporsampah.admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tfkproject.laporsampah.Config;
import app.tfkproject.laporsampah.R;
import app.tfkproject.laporsampah.admin.Model.ItemPeta;
import app.tfkproject.laporsampah.user.Model.ItemLapor;
import app.tfkproject.laporsampah.user.Util.Request;

public class PetaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng lokasi_tujuan;
    private ProgressDialog pDialog;
    List<ItemPeta> items;
    private static String url = Config.HOST+"laporan.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peta);

        getSupportActionBar().setTitle("Laporan Sampah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //dapatkan  izin untuk melakukan thread policy (proses Background AsycnTask)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        items = new ArrayList<>();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //new dapatkanData().execute();

        /*marker("0.485638", "101.402023", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Banyak sampah", "Jl, ballaasdf");
        marker("0.519283", "101.444595", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Ada sampah", "Jl, ballaasdf");
        marker("0.414229", "101.420562", "http://203.153.21.11/app/lapor-sampah/images/IMG_1530604447.jpg", "Sampah semua", "Jl, ballaasdf");*/

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0.506566, 101.437790))); //area pekanbaru
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String mId = marker.getId();
                String markId = mId.replaceAll("\\D+","");
                int markerId = Integer.valueOf(markId);

                for(int i = 0 ; i < items.size() ; i++) {
                    if(markerId == i){
                        Intent intent = new Intent(PetaActivity.this, DataDetailActivity.class);
                        intent.putExtra("key_id_laporan", items.get(i).getId_laporan());
                        intent.putExtra("key_link_img", items.get(i).getUrl_gambar());
                        intent.putExtra("key_judul", items.get(i).getJudul());
                        intent.putExtra("key_lokasi", items.get(i).getJalan());
                        startActivity(intent);
                    }

                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class dapatkanData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PetaActivity.this);
            pDialog.setMessage("Memuat data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {
            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(url,dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        JSONArray products = ob.getJSONArray("field");

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString("id_laporan");
                            String usr = c.getString("nama_user");
                            String foto = c.getString("foto");
                            String judul = c.getString("judul");
                            String jalan = c.getString("jalan");
                            String lat = c.getString("lat");
                            String lng = c.getString("lng");
                            String status = c.getString("status");
                            String timestamp = c.getString("timestamp");

                            items.add(new ItemPeta(id, foto, judul, usr, timestamp, jalan, lat, lng, status));
                        }
                    } else {
                        // no data found

                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            for(int i = 0 ; i < items.size() ; i++) {
                createMarker(items.get(i).getLatitude(), items.get(i).getLongitude(), items.get(i).getUrl_gambar(), items.get(i).getJudul(), items.get(i).getJalan());

            }

        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    protected Marker createMarker(final String lok_lat, final String lok_long, final String link_img, final String nama_objek, final String lokasi_objek) {

        double lat = Double.valueOf(lok_lat);
        double lng = Double.valueOf(lok_long);

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(nama_objek)
                .snippet(lokasi_objek)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.maps_info_window, null);

                ImageView img = v.findViewById(R.id.img);
                Glide.with(PetaActivity.this).load(link_img).asBitmap().override(250,250).listener(new RequestListener<String, Bitmap>() {
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
                lat.setText("Lat: "+lok_lat);

                TextView lon = v.findViewById(R.id.txt_long);
                lon.setText("Lon: "+lok_long);
                return  v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }


        });
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();

        return marker;*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        items.clear();
        new dapatkanData().execute();
    }
}
