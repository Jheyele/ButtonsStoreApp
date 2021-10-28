package com.example.jheyeleraquel.buttonsstore.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jheyeleraquel.buttonsstore.Adapter.PedidosAdapter;
import com.example.jheyeleraquel.buttonsstore.Classes.Pedido;
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

public class ListaMeusPedidosActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewPedidos;

    private List<Pedido> pedidos;

    private PedidosAdapter adapter;

    private DatabaseReference referenciaFirebase;

    private Pedido todosPedidos;

    private LinearLayoutManager mLayoutTodosPedidos;

    private String emailUsuarioLogado;

    private FirebaseAuth autenticacao;

    private String idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_meus_pedidos);

        mRecyclerViewPedidos = (RecyclerView) findViewById(R.id.recyclerViewMeusPedidos);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("clientes").orderByChild("email").equalTo(emailUsuarioLogado.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    idCliente = postSnapshot.child("key").getValue().toString();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
                    if(postSnapshot.child("keyCliente").getValue().equals(idCliente)) {
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

        adapter = new PedidosAdapter(pedidos,this);

        mRecyclerViewPedidos.setAdapter(adapter);
    }
}
