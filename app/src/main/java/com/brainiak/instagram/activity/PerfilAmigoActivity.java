package com.brainiak.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.brainiak.instagram.R;
import com.brainiak.instagram.helper.ConfiguracaoFirebase;
import com.brainiak.instagram.helper.UsuarioFirebase;
import com.brainiak.instagram.model.Postagem;
import com.brainiak.instagram.model.Usuario;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference fireBaseRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private DatabaseReference usuarioLogadoRef;
    private String idUsuarioLogado;
    private TextView textPublicacao, textSeguidores, textSeguindo;
    private DatabaseReference postagensUsuarioRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Configurações iniciais
        fireBaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = fireBaseRef.child("usuarios");
        seguidoresRef = fireBaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Inicializar os componentes
        inicializarComponentes();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Configura referencia postagens usuario
            postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("postagens")
                    .child( usuarioSelecionado.getId() );

            //Configura o nome do usuario na toolbar
            getSupportActionBar().setTitle( usuarioSelecionado.getNome());

            //Recupera foto do usuario
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();

            if(caminhoFoto != null) {
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this)
                        .load( url )
                        .into( imagePerfil );

            }
        }

    //Carrega as fotos das postagens de um usuario
        carregarFotoPostagem();


    }

    public void carregarFotoPostagem(){

        //Recupera as fotos postadas pelo usuario
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> urlFotos = new ArrayList<>();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Postagem postagem = ds.getValue( Postagem.class );
                    urlFotos.add( postagem.getCaminhoFoto() );

                }
                textPublicacao.setText( String.valueOf( urlFotos.size() ) );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Recupera dados do usuario logado
                        usuarioLogado = dataSnapshot.getValue( Usuario.class);

                        //verifica se o usuario já está seguindo amigo selecionado
                        verificaSegueUsuarioAmigo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void verificaSegueUsuarioAmigo(){
        DatabaseReference seguidorRef = seguidoresRef
                .child( idUsuarioLogado )
                .child( usuarioSelecionado.getId() );

        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if( dataSnapshot.exists() ){
                           //ja esta seguindo
                           // Log.i("dadosUsuario", "seguindo ");
                            habilitarBotaoSeguir(true);
                        }else{
                            //nao esta seguindo
                           // Log.i("dadosUsuario", "Seguir ");
                            habilitarBotaoSeguir(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void inicializarComponentes(){
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        textPublicacao = findViewById(R.id.textPublicacao);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguidos);
        imagePerfil = findViewById(R.id.imageEditarPerfil);
        buttonAcaoPerfil.setText("Carregando");
    }

    private void habilitarBotaoSeguir(boolean segueUsuario){
        if(segueUsuario){

            buttonAcaoPerfil.setText("Seguindo");

        }else{

            buttonAcaoPerfil.setText("Seguir");
            //adiciona evento para seguir usuario
            buttonAcaoPerfil.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //salvar Seguidor
                            salvarSeguidor(usuarioLogado, usuarioSelecionado);
                        }
                    }
            );

        }
    }

    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){

        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nome", uAmigo.getNome());
        dadosAmigo.put("caminhoFoto", uAmigo.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child( uLogado.getId() )
                .child( uAmigo.getId() );
        seguidorRef.setValue( dadosAmigo );

        //alterar botao para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener( null );

        //incrementar seguindo do usuario logado
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("Seguindo", seguindo);
        DatabaseReference usuarioSeguindo = usuariosRef
                .child( uLogado.getId() );
        usuarioSeguindo.updateChildren( dadosSeguindo );

        //incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguindo.put("Seguidores", seguindo);
        DatabaseReference usuarioSeguidores = usuariosRef
                .child( uAmigo.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );


    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();

        //recuperar dados usuario logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener( valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuariosRef.child( usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue( Usuario.class );

                        //String postagens = String.valueOf( usuario.getPostagens());
                        String seguindo = String.valueOf( usuario.getSeguindo());
                        String seguidores = String.valueOf( usuario.getSeguidores());

                        //Configura valores recuperados
                       // textPublicacao.setText( postagens );
                        textSeguidores.setText( seguidores );
                        textSeguindo.setText( seguindo );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
