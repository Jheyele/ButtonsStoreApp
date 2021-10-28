package com.example.jheyeleraquel.buttonsstore.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.example.jheyeleraquel.buttonsstore.Adapter.ProdutosAdapter;
import com.example.jheyeleraquel.buttonsstore.Classes.Produto;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaProdutosActivity extends AppCompatActivity {


    private RecyclerView mRecyclerViewProdutos;

    private List<Produto> produtos;

    private ProdutosAdapter adapter;

    private DatabaseReference referenciaFirebase;

    private Produto todosProdutos;

    private LinearLayoutManager mLayoutTodosProdutos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);

        mRecyclerViewProdutos = (RecyclerView) findViewById(R.id.recyclerViewTodosOsProdutos);

        carregarTodosProdutos();

    }

    private void carregarTodosProdutos(){

        mRecyclerViewProdutos.setHasFixedSize(true);

        mLayoutTodosProdutos = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        mRecyclerViewProdutos.setLayoutManager(mLayoutTodosProdutos);

        produtos = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("produtos").orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    todosProdutos = postSnapshot.getValue(Produto.class);

                    produtos.add(todosProdutos);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapter = new ProdutosAdapter(produtos,this);

        mRecyclerViewProdutos.setAdapter(adapter);
    }
}
