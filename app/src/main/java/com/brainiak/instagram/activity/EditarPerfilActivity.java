package com.brainiak.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainiak.instagram.R;
import com.brainiak.instagram.helper.UsuarioFirebase;
import com.brainiak.instagram.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlteraFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;

    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //inicializar componentes
        inicializarComponentes();

        //recuperar dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText( usuarioPerfil.getDisplayName() );
        editEmailPerfil.setText( usuarioPerfil.getEmail() );

        //salvar alterações do nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editNomePerfil.getText().toString();

                //atualizar nome no perfil Firebase
                UsuarioFirebase.atualizarNomeUsuario( nomeAtualizado );

                //atualiza nome no banco de dados
                usuarioLogado.setNome( nomeAtualizado );
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

        //alterar foto do usuario
        textAlteraFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if ( i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);



                }
            }
        });


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if ( resultCode == RESULT_OK ){
                Bitmap imagem = null;
                try{
                    //selecao apenas da galeria
                    switch ( requestCode){
                        case SELECAO_GALERIA:
                            Uri = localImagemSelecionada = data.getData();
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                            break;
                    }

                    //caso tenha sido escolhido uma imagem

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    public void inicializarComponentes(){
        imageEditarPerfil      = findViewById(R.id.imageEditarPerfil);
        textAlteraFoto         = findViewById(R.id.textAlterarFoto);
        editEmailPerfil        = findViewById(R.id.editEmailPerfil);
        editNomePerfil         = findViewById(R.id.editNomeUsuario);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editEmailPerfil.setFocusable(false);

    }

    public boolean onSupportNavigationUp(){
        finish();
        return false;
    }
}
