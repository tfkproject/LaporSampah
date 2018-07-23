package app.tfkproject.laporsampah.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import app.tfkproject.laporsampah.Config;
import app.tfkproject.laporsampah.R;
import app.tfkproject.laporsampah.user.Util.Request;
import app.tfkproject.laporsampah.user.Util.SessionManager;

public class RegistrasiActivity extends AppCompatActivity {

    EditText edtNama, edtEmail, edtPass, edtNohp;
    Button btnReg;

    private ProgressDialog pDialog;

    SessionManager session;
    private static String url = Config.HOST+"registrasi.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        session = new SessionManager(getApplicationContext());

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNama = (EditText) findViewById(R.id.edt_nama);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPass = (EditText) findViewById(R.id.edt_pass);
        edtNohp = (EditText) findViewById(R.id.edt_nohp);

        btnReg = (Button) findViewById(R.id.btn_reg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = edtNama.getText().toString();
                String email = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                String nohp = edtNohp.getText().toString();

                if(nama.equals("") && email.equals("") && pass.equals("") && nohp.equals("")){
                    Toast.makeText(RegistrasiActivity.this, "Maaf, field tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                }
                else{
                    new prosesRegister(nama, email, pass, nohp).execute();
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

    private class prosesRegister extends AsyncTask<Void,Void,String> {

        //variabel untuk tangkap data
        private int scs = 0;
        private String psn;
        private String nama, email, password, nohp;

        public prosesRegister(String nama, String email, String password, String nohp){
            this.nama = nama;
            this.email = email;
            this.password = password;
            this.nohp = nohp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegistrasiActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(Void... params) {

            try{
                //susun parameter
                HashMap<String,String> detail = new HashMap<>();
                detail.put("nama", nama);
                detail.put("email", email);
                detail.put("password", password);
                detail.put("nohp", nohp);

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
                        // Storing each json item in variable
                        String id_user = ob.getString("id_user");
                        psn = ob.getString("message");

                        //buat sesi login
                        session.createLoginSession(id_user);
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
                Toast.makeText(RegistrasiActivity.this, psn, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistrasiActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(RegistrasiActivity.this, psn, Toast.LENGTH_SHORT).show();
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
}
