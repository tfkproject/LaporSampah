package app.tfkproject.laporsampah.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import app.tfkproject.laporsampah.Config;
import app.tfkproject.laporsampah.R;
import app.tfkproject.laporsampah.user.Util.Request;

public class DataDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView txtJudul, txtJalan;
    Button btnTandaiD, btnTandaiW;

    private ProgressDialog pDialog;
    private static String url = Config.HOST+"laporan_update.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail_admin);

        final String id_laporan = getIntent().getStringExtra("key_id_laporan");
        String link_img = getIntent().getStringExtra("key_link_img");
        String judul = getIntent().getStringExtra("key_judul");
        String lokasi = getIntent().getStringExtra("key_lokasi");

        getSupportActionBar().setTitle("Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.gambar);
        Glide.with(DataDetailActivity.this).load(link_img).into(imageView);

        txtJudul = (TextView) findViewById(R.id.txt_judul);
        txtJudul.setText(judul);

        txtJalan = (TextView) findViewById(R.id.txt_jalan);
        txtJalan.setText(lokasi);

        btnTandaiD = (Button) findViewById(R.id.btn_tandai_d);
        btnTandaiD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new updateStatus(id_laporan, "D").execute();
            }
        });

        btnTandaiW = (Button) findViewById(R.id.btn_tandai_w);
        btnTandaiW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new updateStatus(id_laporan, "W").execute();
            }
        });
    }

    private class updateStatus extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String psn;
        private String id_laporan, status;

        public updateStatus(String id_laporan, String status){
            this.id_laporan = id_laporan;
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DataDetailActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {

            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("id_laporan", id_laporan);
                detail.put("status", status);

                try {
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to php file
                    String response = Request.post(url, dataToSend);

                    //dapatkan respon
                    Log.e("Respon", response);

                    JSONObject ob = new JSONObject(response);
                    scs = ob.getInt("success");

                    if (scs == 1) {
                        psn = ob.getString("message");

                    } else {
                        // no data found
                        psn = ob.getString("message");
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
            if(scs == 1){
                finish();
            }
            else{
                Toast.makeText(DataDetailActivity.this, psn, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
