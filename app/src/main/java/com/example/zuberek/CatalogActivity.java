package com.example.zuberek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class CatalogActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CatalogAdapter catalogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CatalogModel> options =
                new FirebaseRecyclerOptions.Builder<CatalogModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("zubry"), CatalogModel.class)
                        .build();

        catalogAdapter = new CatalogAdapter(options);
        recyclerView.setAdapter(catalogAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        catalogAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        catalogAdapter.stopListening();
    }
}