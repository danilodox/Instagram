package com.brainiak.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.brainiak.instagram.R;
import com.brainiak.instagram.adapter.AdapterMiniaturas;
import com.brainiak.instagram.helper.RecyclerItemClickListener;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap image, imageFiltro;
    private List<ThumbnailItem> listaFiltros;
    private RecyclerView recyclerFilters;
    private AdapterMiniaturas adapterMiniaturas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //Configuracoes iniciais
        listaFiltros = new ArrayList<>();

        //inicializando componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFilters = findViewById(R.id.recyclerViewPesquisa);

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //recupera imagem escolhida pelo usuario
        Bundle bundle = getIntent().getExtras();
        if( bundle != null){
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            image = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap( image);


            //Configura recyclerView de filtros
            adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFilters.setLayoutManager( layoutManager );
            recyclerFilters.setAdapter( adapterMiniaturas );

            //Adiciona evento de clique no recyclerView
            recyclerFilters.addOnItemTouchListener( new RecyclerItemClickListener(getApplicationContext(), recyclerFilters, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    ThumbnailItem item = listaFiltros.get(position);

                    imageFiltro = image.copy(image.getConfig(), true);
                    Filter filter = item.filter;
                    imageFotoEscolhida.setImageBitmap( filter.processFilter( imageFiltro));
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            }));

            //Recupera filtros
            recuperaFiltros();


        }
    }

    private void recuperaFiltros (){

        //Limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();


        //Configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = image;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb( item );

        //Lista todos os filtros
        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

        for(Filter filter : filters){
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = image;
            itemFiltro.filter = filter;
            itemFiltro.filterName = filter.getName();

            ThumbnailsManager.addThumb( itemFiltro );
        }
        listaFiltros.addAll( ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterMiniaturas.notifyDataSetChanged(); //para dizer q os dados mudaram
    }

    //configura menu da toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_salvar_postagem:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;

    }
}
