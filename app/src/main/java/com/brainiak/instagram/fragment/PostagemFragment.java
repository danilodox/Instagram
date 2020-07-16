package com.brainiak.instagram.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brainiak.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment {
    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA = 100, SELECAO_GALERIA = 200;

    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        //inicializar componentes
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);

        //Adiciona evento de clique o botao camera
        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getActivity().getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getActivity().getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        return view;
    }

}
