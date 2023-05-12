package com.example.zuberek;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class Fragment2 extends Fragment implements View.OnClickListener{

    Button button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2, container , false);
    }

    @Override
    public void onActivityCreated(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button = getActivity().findViewById(R.id.catalog_f2);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){

        switch(view.getId()){
            case R.id.catalog_f2:
                Intent intent = new Intent(getActivity(), CatalogActivity.class);
                startActivity(intent);
                break;
        }
    }
}
