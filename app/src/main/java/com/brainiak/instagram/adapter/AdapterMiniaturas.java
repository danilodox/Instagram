package com.brainiak.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainiak.instagram.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMiniaturas extends RecyclerView.Adapter<AdapterMiniaturas.MyViewHolder> {

    private List<ThumbnailItem> listFilter;
    private Context context;

    public AdapterMiniaturas(List<ThumbnailItem> listFilter, Context context) {
        this.listFilter = listFilter;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaper_filters, parent, false);
        return new AdapterMiniaturas.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ThumbnailItem item = listFilter.get( position );
        holder.foto.setImageBitmap( item.image );
        holder.nomeFilter.setText( item.filterName );
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView foto;
        TextView nomeFilter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoFiltro);
            nomeFilter = itemView.findViewById(R.id.textNomeFiltro);



        }
    }
}
