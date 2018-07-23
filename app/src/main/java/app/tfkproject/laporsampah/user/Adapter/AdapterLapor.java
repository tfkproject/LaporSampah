package app.tfkproject.laporsampah.user.Adapter;

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

import app.tfkproject.laporsampah.user.Model.ItemLapor;
import app.tfkproject.laporsampah.user.MapTkpActivity;
import app.tfkproject.laporsampah.user.Model.ItemLapor;
import app.tfkproject.laporsampah.R;

/**
 * Created by taufik on 15/05/18.
 */

public class AdapterLapor extends RecyclerView.Adapter<AdapterLapor.ViewHolder> {

    private Context context;
    private List<ItemLapor> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardItem;
        public TextView txtJudul, txtJalan, txtNama, txtTimestamp;
        public ImageView gambar;

        public ViewHolder(View view) {
            super(view);
            cardItem = (CardView) view.findViewById(R.id.card_item);
            gambar = (ImageView) view.findViewById(R.id.gambar);
            txtJudul = (TextView) view.findViewById(R.id.txt_judul);
            txtJalan = (TextView) view.findViewById(R.id.txt_jalan);
            txtNama = (TextView) view.findViewById(R.id.txt_nama);
            txtTimestamp = (TextView) view.findViewById(R.id.txt_timestamp);
        }
    }

    public AdapterLapor(Context context, List<ItemLapor> itemList){
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laporan, parent, false);

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
                        .setIcon(android.R.drawable.ic_dialog_map)
                        .show();
            }
        });

        Glide.with(context).load(itemList.get(position).getUrl_gambar()).into(holder.gambar);

        holder.txtJudul.setText(itemList.get(position).getJudul());
        holder.txtJalan.setText(" "+itemList.get(position).getJalan());
        holder.txtNama.setText("Dari: "+itemList.get(position).getNama());
        holder.txtTimestamp.setText(", "+itemList.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
