package com.brainiak.instagram.model;

import android.provider.ContactsContract;

import com.brainiak.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Postagem {
    /*
    Modelo de postagem

    postagens
        <id_usuario>
            <id_postagem_firebase>
                <descricao>
                <caminhoFoto>
                <idUsuario>
     */
    private String id, idUsuario, descricao, caminhoFoto;

    public Postagem(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey(); // .push -> gerar id unico dentro de postagem .getKey -> e pegar a referencia dela
        setId( idPostagem );
    }

    public boolean salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagensRef = firebaseRef
                .child("postagens")
                .child( getIdUsuario());
        postagensRef.setValue(this);
        return true;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
