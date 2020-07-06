package com.brainiak.instagram.helper;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;

public class UsuarioFirebase {
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void atualizarNomeUsuario( String nome ){
        try{

            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar  objeto para alteração de dados
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(nome)
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful()){
                        Log.d("PERFIL", "erro ao atualizar nome de perfil.");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}


