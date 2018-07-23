package app.tfkproject.laporsampah.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;


import app.tfkproject.laporsampah.R;

public class MainActivity extends AppCompatActivity {

    CardView btnPeta, btnRekap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        btnPeta = (CardView) findViewById(R.id.btn_peta);
        btnRekap = (CardView) findViewById(R.id.btn_rekap);

        btnPeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PetaActivity.class);
                startActivity(intent);
            }
        });

        btnRekap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RekapActivity.class);
                startActivity(intent);
            }
        });
    }

}
