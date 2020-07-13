package com.brainiak.instagram.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.brainiak.instagram.R;
import com.brainiak.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

//widget
private SearchView searchViewPesquisa;
private RecyclerView recyclerViewPesquisa;
private ArrayList<Usuario> listaUsuarios;
private DatabaseReference usuariosRef;

    public PesquisaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        //usa-se view. pra acessar o findViewById pq estamos usando fragments
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerViewPesquisa);


        //Configuracoes iniciais
       listaUsuarios = new ArrayList<Usuario>();
       usuariosRef = ConfiguracaoFirebase.getFirebase()
                        .child( "usuarios" );


        //Configura searchView
        searchViewPesquisa.setQueryHint("Buscar usuários");

        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisaUsuario( textoDigitado );

                return true;
            }
        });



        return view;
    }

    private void pesquisaUsuario(String str){
        //Limpa lista
        listaUsuarios.clear();

        //é necessario criar um outro nó firebase para nomes
        // de usuarios com as letras todas maiusculas ou
        // minusculas. Pra quando for filtrar com a queue não der problema

        //Pesquisa usuarios caso tenha texto na pesquisa
        if( str.length() > 0){

            Query query = usuariosRef.orderByChild("nome")
                    .startAt(str)
                    .endAt(str + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for( DataSnapshot ds : dataSnapshot.getChildren() ){
                        listaUsuarios.add(ds.getValue(Usuario.class));
                    }

                    int total = listaUsuarios.size();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}
