package com.example.jheyeleraquel.buttonsstore.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jheyeleraquel.buttonsstore.Adapter.AdmPedidosAdapter;
import com.example.jheyeleraquel.buttonsstore.Adapter.PedidosAdapter;
import com.example.jheyeleraquel.buttonsstore.Adapter.ProdutosAdapter;
import com.example.jheyeleraquel.buttonsstore.Classes.Pedido;
import com.example.jheyeleraquel.buttonsstore.Classes.Produto;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaPedidosActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewPedidos;

    private List<Pedido> pedidos;

    private AdmPedidosAdapter adapter;

    private DatabaseReference referenciaFirebase;

    private Pedido todosPedidos;

    private LinearLayoutManager mLayoutTodosPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pedidos);

        mRecyclerViewPedidos = (RecyclerView) findViewById(R.id.recyclerViewTodosOsPedidos);

        carregarTodosProdutos();

    }
    private void carregarTodosProdutos(){

        mRecyclerViewPedidos.setHasFixedSize(true);

        mLayoutTodosPedidos = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        mRecyclerViewPedidos.setLayoutManager(mLayoutTodosPedidos);

        pedidos = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("pedidos").orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(postSnapshot.child("status").getValue().equals("Espera")) {
                        todosPedidos = postSnapshot.getValue(Pedido.class);

                        pedidos.add(todosPedidos);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapter = new AdmPedidosAdapter(pedidos,this);

        mRecyclerViewPedidos.setAdapter(adapter);
    }
}
