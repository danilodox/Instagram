package com.brainiak.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainiak.instagram.R;
import com.brainiak.instagram.adapter.AdapterMiniaturas;
import com.brainiak.instagram.helper.ConfiguracaoFirebase;
import com.brainiak.instagram.helper.RecyclerItemClickListener;
import com.brainiak.instagram.helper.UsuarioFirebase;
import com.brainiak.instagram.model.Postagem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.brainiak.instagram.helper.UsuarioFirebase.atualizarFotoUsuario;

public class FiltroActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap image, imageFiltro;
    private TextInputEditText textDescricaoFiltro;
    private List<ThumbnailItem> listaFiltros;
    private RecyclerView recyclerFilters;
    private AdapterMiniaturas adapterMiniaturas;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //Configuracoes iniciais
        listaFiltros = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        imageFiltro = image.copy(image.getConfig(), true);

        //inicializando componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFilters = findViewById(R.id.recyclerViewPesquisa);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);

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
            imageFiltro = image.copy(image.getConfig(), true);


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
                publicarPostagem();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;

    }

    private void publicarPostagem(){
        final Postagem postagem = new Postagem();
        postagem.setIdUsuario( idUsuarioLogado );
        postagem.setDescricao( textDescricaoFiltro.getText().toString());

        //Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageFiltro.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //Salvar imagem no firebase store
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        final StorageReference imageRef = storageRef
                .child("imagens")
                .child("postagens")
                .child(postagem.getId() + "jpeg");

        UploadTask uploadTask = imageRef.putBytes( dadosImagem );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FiltroActivity.this, "Erro ao salvar imagem, tente novamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Recuperar local da foto

                //Em versoes anteriores usava:
                //taskSnapshot.getDownloadUrl();

                //Agora usa assim:
                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();
                        postagem.setCaminhoFoto( url.toString() );
                    }
                });

                if( postagem.salvar() ){
                    Toast.makeText(FiltroActivity.this, "Sucesso ao salvar postagem", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }
}
