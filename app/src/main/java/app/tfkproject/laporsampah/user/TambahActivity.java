package app.tfkproject.laporsampah.user;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import app.tfkproject.laporsampah.Config;
import app.tfkproject.laporsampah.R;
import app.tfkproject.laporsampah.user.Util.Request;
import app.tfkproject.laporsampah.user.Util.SessionManager;

public class TambahActivity extends AppCompatActivity {

    ImageView imgView;
    EditText edtJudul, edtJalan;
    Button btnLapor;
    private static final int RESULT_SELECT_IMAGE = 1;
    public String timestamp;
    SessionManager session;
    private ProgressDialog pDialog;
    private static final String TAG = TambahActivity.class.getSimpleName();
    private static String url = Config.HOST+"laporan_tambah.php";
    private String pictureImagePath = "";
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSIONS_REQUEST = 100;
    private static final int MY_READ_STORAGE_PERMISSIONS_REQUEST = 99;
    private static final int MY_WRITE_STORAGE_PERMISSIONS_REQUEST = 98;
    Bitmap img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);

        session = new SessionManager(getApplicationContext());
        //ambil data user
        HashMap<String, String> user = session.getUserDetails();
        final String id_user = user.get(SessionManager.KEY_ID_USER);

        getSupportActionBar().setTitle("Laporkan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //cek permission di android M
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        final String lokasi_lat = getIntent().getStringExtra("key_lat");
        final String lokasi_lng = getIntent().getStringExtra("key_lng");

        edtJudul = (EditText) findViewById(R.id.edt_judul);
        edtJalan = (EditText) findViewById(R.id.edt_jalan);

        imgView = (ImageView) findViewById(R.id.gambar);

        btnLapor = (Button) findViewById(R.id.btn_lapor);
        btnLapor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(img == null){
                    //get image in bitmap format
                    Bitmap image = ((GlideBitmapDrawable) imgView.getDrawable()).getBitmap();
                    //execute the async task and upload the image to server

                    if(imgView.getDrawable() != null)
                    {
                        if(edtJudul.getText().toString().length() > 0 && edtJalan.getText().toString().length() > 0){
                            String judul = edtJudul.getText().toString();
                            String jalan = edtJalan.getText().toString();

                            new Upload(image,"IMG_"+timestamp, id_user, judul, jalan, lokasi_lat, lokasi_lng, "W").execute(); //W artinya status waiting

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Foto perlu ditambahkan",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //pakai Bitmap dari camera result yang di pass ke variabel img
                    String judul = edtJudul.getText().toString();
                    String jalan = edtJalan.getText().toString();

                    new Upload(img,"IMG_"+timestamp, id_user, judul, jalan, lokasi_lat, lokasi_lng, "W").execute(); //W artinya status waiting
                }

            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the current timeStamp and store that in the time Variable
                Long tsLong = System.currentTimeMillis() / 1000;
                timestamp = tsLong.toString();

                final CharSequence[] options = { "Ambil foto", "Pilih dari galeri","Batal" };
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(TambahActivity.this);
                builder.setItems(options,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if(options[which].equals("Ambil foto")) {
                            bukaKamera();
                        }
                        else if(options[which].equals("Pilih dari galeri")) {
                            bukaGaleri();
                        }
                        else if(options[which].equals("Batal")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void bukaKamera(){
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        String imageFileName = "IMG_"+timestamp+ ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        Log.e("Lokasi image", pictureImagePath);
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void bukaGaleri() {
        //open album untuk pilih image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null){
            //set the selected image to image variable
            Uri image = data.getData();
            Glide.with(getApplicationContext()).load(image).into(imgView);
            //imgView.setImageURI(image);

            Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();

        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            File imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                //Matrix matrix = new Matrix();
                //matrix.postRotate(-90);
                img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                //Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imgView.setImageBitmap(img);
            }

            Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSIONS_REQUEST);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSIONS_REQUEST);
            }
            return false;
        }
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_READ_STORAGE_PERMISSIONS_REQUEST);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_READ_STORAGE_PERMISSIONS_REQUEST);
            }
            return false;
        }
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_WRITE_STORAGE_PERMISSIONS_REQUEST);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_WRITE_STORAGE_PERMISSIONS_REQUEST);
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
            case MY_READ_STORAGE_PERMISSIONS_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(TambahActivity.this, "read storage permission granted", Toast.LENGTH_LONG).show();
                    //bukaGaleri();
                } else {
                    Toast.makeText(this, "read storage permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_CAMERA_PERMISSIONS_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(TambahActivity.this, "camera permission granted", Toast.LENGTH_LONG).show();
                    //bukaKamera();
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_WRITE_STORAGE_PERMISSIONS_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(TambahActivity.this, "write storage permission granted", Toast.LENGTH_LONG).show();
                    //bukaKamera();
                } else {
                    Toast.makeText(this, "write storage permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.

        }
    }

    private class Upload extends AsyncTask<Void,Void,String> {
        private Bitmap image;
        private String file_name;
        private String id_user;
        private String judul;
        private String jalan;
        private String lat;
        private String lng;
        private String status;

        private String psn = "";

        public Upload(Bitmap image, String file_name, String id_user, String judul, String jalan, String lat, String lng, String status){
            this.image = image;
            this.file_name = file_name;
            this.id_user = id_user;
            this.judul = judul;
            this.jalan = jalan;
            this.lat = lat;
            this.lng = lng;
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TambahActivity.this);
            pDialog.setMessage("Sedang upload..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //kompress image ke format jpg
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image ke base64 agar bisa di ambil/dibaca nanti pada file php
            * */
            final String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            try {
                //menganbil data-data yang akan dikirim

                //generate hashMap to store encodedImage and the name
                HashMap<String,String> detail = new HashMap<>();
                detail.put("image", encodeImage);
                detail.put("file_name", file_name);
                detail.put("id_user", id_user);
                detail.put("judul", judul);
                detail.put("jalan", jalan);
                detail.put("lat", lat);
                detail.put("lng", lng);
                detail.put("status", status);

                try{
                    //convert this HashMap to encodedUrl to send to php file
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to saveImage.php file
                    String response = Request.post(url,dataToSend);

                    //dapatkan respon
                    Log.e("Hasil upload", response);

                    JSONObject ob = new JSONObject(response);
                    psn = ob.getString("message");

                    // return response;

                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e(TAG, "ERROR  " + e);
                    Toast.makeText(getApplicationContext(),"Maaf, Gagal mengupload",Toast.LENGTH_SHORT).show();
                    //return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), psn,Toast.LENGTH_SHORT).show();
            //terus tutup activity ini
            finish();

            //show image uploaded
            pDialog.dismiss();

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
