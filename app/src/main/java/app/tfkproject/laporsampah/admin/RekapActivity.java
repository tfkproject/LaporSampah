package app.tfkproject.laporsampah.admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tfkproject.laporsampah.Config;
import app.tfkproject.laporsampah.R;
import app.tfkproject.laporsampah.admin.Adapter.AdapterRekap;
import app.tfkproject.laporsampah.user.Adapter.AdapterLapor;
import app.tfkproject.laporsampah.user.Model.ItemLapor;
import app.tfkproject.laporsampah.user.TambahActivity;
import app.tfkproject.laporsampah.user.Util.Request;

public class RekapActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdapterRekap adapter;
    List<ItemLapor> items;

    // Calender
    private Calendar cal;
    private int day, month, year;

    private ProgressDialog pDialog;
    private static String url = Config.HOST+"laporan_rekap.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        getSupportActionBar().setTitle("Rekapitulasi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //        C A L E N D E R
        cal     = Calendar.getInstance();
        day     = cal.get(Calendar.DAY_OF_MONTH);
        month   = cal.get(Calendar.MONTH);
        year    = cal.get(Calendar.YEAR);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        items = new ArrayList<>();

        /////

        adapter = new AdapterRekap(RekapActivity.this, items);
        recyclerView.setAdapter(adapter);
    }

    private class dapatkanData extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String param;

        public dapatkanData(String param){
            this.param = param;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RekapActivity.this);
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
                    String response = Request.post(url+"?"+param,dataToSend);

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


                            items.add(new ItemLapor(id, foto, judul, usr, timestamp, jalan, lat, lng, status));

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
            adapter.notifyDataSetChanged();
            pDialog.dismiss();

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

    @Override
    protected void onResume() {
        super.onResume();
        items.clear();
        new dapatkanData(null).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rekap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }

        if (id == R.id.action_refresh){
            items.clear();
            new dapatkanData(null).execute();
        }

        if (id == R.id.action_filter){
            //tampilkan dialog tanggal
            showDialog(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            NumberFormat f = new DecimalFormat("00");
            String tanggal = selectedYear + "-" + (f.format(selectedMonth + 1)) + "-" + f.format(selectedDay);
            items.clear();
            new dapatkanData("tgl="+tanggal).execute();
        }
    };

}
