package com.example.zuberek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class CatalogActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CatalogAdapter catalogAdapter;
    FloatingActionButton floatingActionButton;

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

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AddBisonActivity.class));
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void txtSearch(String str){
        FirebaseRecyclerOptions<CatalogModel> options =
                new FirebaseRecyclerOptions.Builder<CatalogModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("zubry")
                                .orderByChild("name").startAt(str).endAt(str+"~"), CatalogModel.class).build();

        catalogAdapter = new CatalogAdapter(options);
        catalogAdapter.startListening();
        recyclerView.setAdapter(catalogAdapter);
    }
}