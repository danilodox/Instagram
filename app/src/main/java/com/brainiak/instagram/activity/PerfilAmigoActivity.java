package com.brainiak.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toolbar;

import com.brainiak.instagram.R;
import com.brainiak.instagram.model.Usuario;

public class PerfilAmigoActivity extends AppCompatActivity {

private Usuario usuarioSelecionado;
    private Button buttonAcaoPerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Inicializar os componentes
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Seguir");

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Configura o nome do usuario na toolbar
            getSupportActionBar().setTitle( usuarioSelecionado.getNome());


        }




    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}