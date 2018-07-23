package app.tfkproject.laporsampah.admin.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.tfkproject.laporsampah.R;
import app.tfkproject.laporsampah.admin.DataDetailActivity;
import app.tfkproject.laporsampah.user.MapTkpActivity;
import app.tfkproject.laporsampah.user.Model.ItemLapor;

/**
 * Created by taufik on 15/05/18.
 */

public class AdapterRekap extends RecyclerView.Adapter<AdapterRekap.ViewHolder> {

    private Context context;
    private List<ItemLapor> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardItem;
        public TextView txtJudul, txtLokasi, txtNama, txtTimestamp, txtStatus;
        public ImageView gambar;

        public ViewHolder(View view) {
            super(view);
            cardItem = (CardView) view.findViewById(R.id.cardView);
            gambar = (ImageView) view.findViewById(R.id.gambar);
            txtJudul = (TextView) view.findViewById(R.id.txt_judul);
            txtLokasi = (TextView) view.findViewById(R.id.txt_lokasi);
            txtTimestamp = (TextView) view.findViewById(R.id.txt_timestamp);
            txtNama = (TextView) view.findViewById(R.id.txt_nama);
            txtStatus = (TextView) view.findViewById(R.id.txt_sts);
        }
    }

    public AdapterRekap(Context context, List<ItemLapor> itemList){
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rekap, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Buka Peta")
                        .setMessage("Lihat posisi dalam peta?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, MapTkpActivity.class);
                                intent.putExtra("key_link_img", itemList.get(position).getUrl_gambar());
                                intent.putExtra("key_nama_tujuan", itemList.get(position).getJudul());
                                intent.putExtra("key_lokasi_tujuan", itemList.get(position).getJalan());
                                intent.putExtra("key_lat_tujuan", itemList.get(position).getLatitude());
                                intent.putExtra("key_long_tujuan", itemList.get(position).getLongitude());
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("Ubah status", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, DataDetailActivity.class);
                                intent.putExtra("key_id_laporan", itemList.get(position).getId());
                                intent.putExtra("key_link_img", itemList.get(position).getUrl_gambar());
                                intent.putExtra("key_judul", itemList.get(position).getJudul());
                                intent.putExtra("key_lokasi", itemList.get(position).getJalan());
                                context.startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_map)
                        .show();
            }
        });

        Glide.with(context).load(itemList.get(position).getUrl_gambar()).into(holder.gambar);

        holder.txtJudul.setText(itemList.get(position).getJudul());
        holder.txtLokasi.setText(" "+itemList.get(position).getJalan());
        holder.txtNama.setText(itemList.get(position).getNama());
        holder.txtTimestamp.setText(", "+itemList.get(position).getTimestamp());
        holder.txtStatus.setText(itemList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
